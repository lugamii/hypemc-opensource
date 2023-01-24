package br.com.weavenmc.ypvp.ability.list;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.jnbt.DataException;
import br.com.weavenmc.ypvp.jnbt.Schematic;
import br.com.weavenmc.ypvp.jnbt.Schematic.GladiatorBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;

@SuppressWarnings("unused")
public class GladiatorAbility extends Ability {

	private static AsyncWorldEdit asyncWorldEdit = AsyncWorldEdit.getInstance();
	private ArrayList<UUID> callingToBattle = new ArrayList<>();

	public GladiatorAbility() {
		setName("Gladiator");
		setHasItem(true);
		setGroupToUse(Group.VIP);
		setIcon(Material.IRON_FENCE);
		setDescription(new String[] { "§7Puxe seu inimigo para um duelo", "§71v1 em uma arena nos céus." });
		setPrice(80000);
		setTempPrice(8000);
	}

	@Override
	public void eject(Player p) {
		if (callingToBattle.contains(p.getUniqueId())) {
			callingToBattle.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onGladiatorListener(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;
		Player gladiator = event.getPlayer();
		if (hasKit(gladiator)) {
			if (event.getRightClicked() instanceof Player) {
				if (isItem(gladiator.getItemInHand())) {
					event.setCancelled(true);

					Player target = (Player) event.getRightClicked();
					Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(target.getUniqueId());
					
					if (gamer.getWarp().isProtected(target)) {
						gladiator.sendMessage("§5§lGLADIATOR§f O jogador está com proteção de spawn!");
						return;
					}
					if (callingToBattle.contains(target.getUniqueId()) || callingToBattle.contains(gladiator.getUniqueId())) {
						return;
					}

					callingToBattle.add(gladiator.getUniqueId());
					callingToBattle.add(target.getUniqueId());

					Schematic gladiatorArena = null;

					try {
						gladiatorArena = Schematic.getInstance()
								.loadSchematic(new File(yPvP.getPlugin().getDataFolder(), "gladiator.schematic"));
					} catch (IOException | DataException ex) {
						ex.printStackTrace();
						gladiator.sendMessage("§5§lGLADIATOR§f Não foi possivel criar a §9§lARENA!");
						eject(target);
						eject(gladiator);
						return;
					}

					new GladiatorBattle(gladiator, target, gladiatorArena,
							gladiator.getLocation().add(0.0D, 100.0D, 0.0D));

					eject(target);
					eject(gladiator);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (callingToBattle.contains(event.getPlayer().getUniqueId())) {
			callingToBattle.remove(event.getPlayer().getUniqueId());
		}
	}

	public class GladiatorBattle {

		private boolean started = false;
		private Player gladiator;
		private Player target;
		private Location gladiatorLocation;
		private Location targetLocation;
		private BukkitTask whiterTask;
		private BukkitTask endTask;
		private HashMap<GladiatorBlock, Block> gladiatorBlocks;
		private Listener gladiatorListener;

		public GladiatorBattle(Player gladiator, Player target, Schematic schematic, Location loc) {
			gladiatorBlocks = new HashMap<>();
			
			this.gladiator = gladiator;
			this.target = target;

			gladiatorLocation = gladiator.getLocation();
			targetLocation = target.getLocation();

			Bukkit.getPluginManager().registerEvents(gladiatorListener = new Listener() {

				@EventHandler(priority = EventPriority.LOWEST)
				public void onEntityDamage(EntityDamageByEntityEvent event) {
					if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
						Player entity = (Player) event.getEntity();
						Player damager = (Player) event.getDamager();

						if (inBattle(entity) && inBattle(damager)) {
							return;
						}

						if (inBattle(entity) && !inBattle(damager)) {
							event.setCancelled(true);
						} else if (!inBattle(entity) && inBattle(damager)) {
							event.setCancelled(true);
						}
					}
				}

				@EventHandler
				public void onScape(PlayerCommandPreprocessEvent event) {
					if (inBattle(event.getPlayer())) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage("§5§lGLADIATOR§f Você não pode usar comandos durante uma §9§lBATALHA!");
					}
				}

				@EventHandler(priority = EventPriority.LOWEST)
				public void onGladiatorAgain(PlayerInteractEntityEvent event) {
					if (inBattle(event.getPlayer())) {
						event.setCancelled(true);
					}
				}

				@EventHandler
				public void onDeath(PlayerDeathEvent event) {
					if (!inBattle(event.getEntity()))
						return;

					GladiatorBattle.this.gladiator.teleport(gladiatorLocation);
					GladiatorBattle.this.target.teleport(targetLocation);

					GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
					GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);

					destroy();
				}

				@EventHandler
				public void onQuit(PlayerQuitEvent event) {
					if (!inBattle(event.getPlayer())) {
						return;
					}

					GladiatorBattle.this.gladiator.teleport(gladiatorLocation);
					GladiatorBattle.this.target.teleport(targetLocation);

					GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
					GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);

					destroy();
				}

				@EventHandler(priority = EventPriority.LOWEST)
				public void onPlayerMove(PlayerMoveEvent event) {
					if (!started)
						return;

					if (!(inBattle(event.getPlayer()))) {
						return;
					}

					if (!gladiatorBlocks.values().contains(event.getTo().getBlock())) {
						GladiatorBattle.this.gladiator.teleport(gladiatorLocation);
						GladiatorBattle.this.target.teleport(targetLocation);

						destroy();
					}
				}

			}, yPvP.getPlugin());

			callBattle(schematic, loc);
		}

		public void timeOut() {
			GladiatorBattle.this.gladiator.teleport(gladiatorLocation);
			GladiatorBattle.this.target.teleport(targetLocation);

			GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
			GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);

			destroy();
		}

		@SuppressWarnings("deprecation")
		public Object callBattle(Schematic schematic, Location loc) {
			World world = loc.getWorld();

			short[] blocks = schematic.getBlocks();
			byte[] blockData = schematic.getData();

			short length = schematic.getLenght();
			short width = schematic.getWidth();
			short height = schematic.getHeight();

			Queue<GladiatorBlock> queue = new ConcurrentLinkedQueue<>();

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < length; z++) {
						int index = y * width * length + z * width + x;
						Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
						gladiatorBlocks.put(new GladiatorBlock(block.getLocation().getBlockX(), block.getLocation().getBlockY(),
								block.getLocation().getBlockZ(), blocks[index], blockData[index]), block);

						if (block.getType() != Material.AIR) {
							gladiatorBlocks.clear();
							return callBattle(schematic, loc = loc.add(+1.0D, 0.0D, +1.0D));
						}
					}
				}
			}
			
			for (Entry<GladiatorBlock, Block> entrie : gladiatorBlocks.entrySet()) {
				GladiatorBlock a = entrie.getKey();
				Block b = entrie.getValue();
				
				b.setTypeIdAndData(a.getId(), a.getData(), true);
			}

			gladiator.setFallDistance(0);
			gladiator.teleport(
					new Location(loc.getWorld(), loc.getBlockX() + 4, loc.getBlockY() + 2, loc.getBlockZ() + 4));

			target.setFallDistance(0);
			target.teleport(
					new Location(loc.getWorld(), loc.getBlockX() + 16, loc.getBlockY() + 2, loc.getBlockZ() + 16));
			
			//lookAt(gladiator, target);
			//lookAt(target, gladiator);

			gladiator.sendMessage("§5§lGLADIATOR§f Você desafiou §9§l" + target.getName() + "§f para uma batalha!");

			target.sendMessage(
					"§5§lGLADIATOR§f Você foi desafiado por §9§l" + gladiator.getName() + "§f para uma batalha!");

			whiterTask = new BukkitRunnable() {

				@Override
				public void run() {
					gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
					target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
				}
			}.runTaskLater(yPvP.getPlugin(), 2400L);

			endTask = new BukkitRunnable() {

				@Override
				public void run() {
					timeOut();
				}

			}.runTaskLater(yPvP.getPlugin(), 3600L);

			return started = true;

		}

		public boolean inBattle(Player player) {
			return gladiator == player || target == player;
		}

		public void destroy() {
			HandlerList.unregisterAll(gladiatorListener);
			//gladiatorListener = null;

			for (Block b : gladiatorBlocks.values()) {
				b.setType(Material.AIR);
			}
			
			gladiatorBlocks.clear();

			//gladiatorBlocks = null;

			if (whiterTask != null) {
				whiterTask.cancel();
				//whiterTask = null;
			}

			if (endTask != null) {
				endTask.cancel();
				//endTask = null;
			}

			//gladiator = null;
			//target = null;

			//gladiatorLocation = null;
			//targetLocation = null;
		}
	}
	
	public void lookAt(Player p, Player target) {
		((CraftPlayer) p).getHandle().setSpectatorTarget(((CraftPlayer) target).getHandle());
	}
}
