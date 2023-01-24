package br.com.weavenmc.commons.bukkit.injector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import br.com.weavenmc.commons.bukkit.BukkitMain;

public class PacketLimiterInjector {

	private final Map<Player, PPS> ppsMap = new HashMap<>();
	private Cache<String, Integer> ignore = CacheBuilder.newBuilder().concurrencyLevel(2).initialCapacity(20)
			.expireAfterWrite(550L, TimeUnit.MILLISECONDS).build();

	public void injectTwo(BukkitMain Main) {
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(Main,
				ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
			public void onPacketReceiving(PacketEvent event) {
				if (event.getPlayer() == null) {
					event.setCancelled(true);
					return;
				}
				Integer attemps = (Integer) ignore.getIfPresent(event.getPlayer().getName().toLowerCase());
				if (attemps != null && attemps.intValue() >= 2) {
					if (attemps.intValue() >= 100) {
						this.plugin.getLogger().log(Level.INFO, "{0} Kicked. Crash attempt detected",
								event.getPlayer().getName());
						forceKick(event.getPlayer());
					} else {
						kick(event.getPlayer());
					}
					event.setCancelled(true);
					return;
				}
				ItemStack stack = (ItemStack) event.getPacket().getItemModifier().readSafely(0);
				if (stack == null || stack.getType() != Material.WRITTEN_BOOK)
					return;
				ItemStack inHand = event.getPlayer().getItemInHand();
				if (inHand == null || inHand.getType() != Material.WRITTEN_BOOK) {
					event.setCancelled(true);
					forceKick(event.getPlayer());
					this.plugin.getLogger().log(Level.INFO, "{0} Kicked. Crash attempt detected",
							event.getPlayer().getName());
					return;
				}
				try {
					checkNbtTags(stack);
				} catch (IOException e) {
					event.setCancelled(true);
					forceKick(event.getPlayer());
					this.plugin.getLogger().log(Level.INFO, "{0} Kicked. Crash attempt detected",
							event.getPlayer().getName());
					return;
				}
				ignore.put(event.getPlayer().getName().toLowerCase(),
						Integer.valueOf((attemps == null) ? 0 : (attemps.intValue() + 1)));
			}
		});
	}

	public void inject(BukkitMain main) {
		ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(main,
				ListenerPriority.NORMAL, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				if (player == null)
					return;
				PPS pps = ppsMap.computeIfAbsent(player, v -> new PPS());
				if (pps.last > System.currentTimeMillis()) {
					if (++pps.count > 500) {
						event.setCancelled(true);
						if (!pps.kicked) {
							try {
								pps.kicked = true;
								PacketContainer packet = new PacketContainer(PacketType.Play.Server.KICK_DISCONNECT);
								packet.getChatComponents().write(0, WrappedChatComponent
										.fromText("§4§lSPEED§f Você está se movendo §c§lMUITO RAPIDO!"));
								main.getProcotolManager().sendServerPacket(player, packet);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					pps.last = System.currentTimeMillis() + 1000L;
					pps.count = 0;
				}
			}
		}).start();

		main.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler(priority = EventPriority.MONITOR)
			public void onPlayerQuit(PlayerQuitEvent event) {
				ppsMap.remove(event.getPlayer());
			}

		}, main);
		injectTwo(main);
	}

	private void kick(Player p) {
		this.ignore.put(p.getName().toLowerCase(), Integer.valueOf(100));
		Bukkit.getScheduler().runTask((Plugin) this, () -> p.kickPlayer("Tentativa de crash."));
	}

	private void forceKick(Player p) {
		try {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.KICK_DISCONNECT);
		packet.getChatComponents().write(0,
				WrappedChatComponent.fromText("§4§lCRASH§f Você foi kickado devido à uma tentativa de crash!"));
		
			BukkitMain.getInstance().getProcotolManager().sendServerPacket(p, packet);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void checkNbtTags(ItemStack itemStack) throws IOException {
		NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
		if (root == null)
			throw new IOException("No NBT tag?!");
		if (!root.containsKey("pages"))
			throw new IOException("No 'pages' NBT compound was found");
		NbtList<String> pages = root.getList("pages");
		if (pages.size() > 50)
			throw new IOException("Too much pages");
		for (String page : pages) {
			if (page.length() > 320)
				throw new IOException("A very long page");
		}
	}

	protected class PPS {

		private boolean kicked;
		private long last;
		private int count;

	}
}
