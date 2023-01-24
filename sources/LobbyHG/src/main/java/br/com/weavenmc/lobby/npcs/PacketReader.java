package br.com.weavenmc.lobby.npcs;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.managers.NpcManager;
import br.com.weavenmc.lobby.managers.MenuManager.MenuType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;

public class PacketReader {

	Player player;
	Channel channel;
	Map<Player, Long> buff;

	public PacketReader(Player player) {
		this.buff = new HashMap<>();
		this.player = player;
	}

	public void inject() {
		CraftPlayer cPlayer = (CraftPlayer) this.player;
		this.channel = (cPlayer.getHandle()).playerConnection.networkManager.channel;
		this.channel.pipeline().addAfter("decoder", "PacketInjector",
				(ChannelHandler) new MessageToMessageDecoder<Packet<?>>() {
					protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2)
							throws Exception {
						arg2.add(packet);
						PacketReader.this.readPacket(packet);
					}
				});
	}

	public void uninject() {
		if (this.channel.pipeline().get("PacketInjector") != null)
			this.channel.pipeline().remove("PacketInjector");
	}

	public void readPacket(Packet<?> packet) {

		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			if (buff.containsKey(player)) {
				if (!new Date().after(new Date(buff.get(player)))) {
					return;
				}
			}
			
			NpcManager npcManager = Lobby.getPlugin().getNpcManager();

			int id = ((Integer) getValue(packet, "a")).intValue();
			if (id == npcManager.getHgNpc().getEntityID()) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
				player.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.DOUBLEKITHG));

			} else 	if (id == npcManager.getEventoNpc().getEntityID()) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
				npcManager.findServer(player, ServerType.TOURNAMENT);

			} else if (id == npcManager.getCopaNPC().getEntityID()) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
				
				player.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.COPA_MENU));
			}


		}
	}

	public void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception exception) {
		}
	}

	public Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception exception) {
			return null;
		}
	}
}