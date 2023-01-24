package br.com.mcweaven.gladiator.fight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.mcweaven.gladiator.gamer.Gamer;
import br.com.mcweaven.gladiator.jnbt.DataException;
import br.com.mcweaven.gladiator.jnbt.Schematic;
import br.com.mcweaven.gladiator.jnbt.Schematic.GladiatorBlock;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Fight {

	private UUID duelPlayer1, duelPlayer2;

	private List<Item> drops;
	private Map<GladiatorBlock, Block> gladiatorBlocks;
	@Setter
	private int fightTime;

	public Fight(UUID duelPlayer1, UUID duelPlayer2) {
		this.duelPlayer1 = duelPlayer1;
		this.duelPlayer2 = duelPlayer2;
		drops = new ArrayList<>();
		gladiatorBlocks = new HashMap<>();
		fightTime = 0;
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (Bukkit.getPlayer(duelPlayer1) == null || Bukkit.getPlayer(duelPlayer2) == null) {
					cancel();
					return;
				}
				Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(duelPlayer1);
				if (gamer.getFight() == null) {
					cancel();
					return;
				}

				fightTime++;

				if (fightTime % 300 == 0) {

					Bukkit.getPlayer(duelPlayer1).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
					Bukkit.getPlayer(duelPlayer2).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
				}

				if (fightTime % 180 == 0) {
					clearDrops();
					for (Block blocks : gladiatorBlocks.values()) {
						if (blocks.getType() != Material.GLASS && blocks.getType() != Material.BEDROCK
								&& blocks.getType() != Material.AIR) {
							blocks.setType(Material.AIR);
						}
					}
				}

			}
		}.runTaskTimer(Gladiator.getInstance(), 0, 20l);
	}

	public void clearPlacedBlocks() {
		for (Block blocks : gladiatorBlocks.values()) {
			if (blocks.getType() != Material.GLASS || blocks.getType() != Material.BEDROCK)
				blocks.setType(Material.AIR);
		}
	}

	public void clearDrops() {
		if (drops.isEmpty())
			return;
		for (Item itens : drops) {

			itens.remove();

		}
		drops.clear();
	}

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	public void drop(PlayerDropItemEvent event) {

		drops.add(event.getItemDrop());

	}

	public Location GenerateLocation(Location refLocation) {

		Location location = null;
		location = refLocation.add(new Random().nextInt(400) + 800.0D + new Random().nextInt(500), 60.0D,
				new Random().nextInt(100) + (new Random().nextBoolean() ? -800.0D : 800.0D)
						+ new Random().nextInt(500));
		if (location.getBlock().getChunk().isLoaded()) {
			return location;
		} else {
			return GenerateLocation(refLocation);
		}
	}

	public void startBattle() {

		Schematic gladiatorArena = null;
		//
		Player player = Bukkit.getPlayer(duelPlayer1);
		Player duel = Bukkit.getPlayer(duelPlayer2);

		try {
			gladiatorArena = Schematic.getInstance()
					.loadSchematic(new File(Gladiator.getInstance().getDataFolder(), "gladiator.schematic"));
		} catch (IOException | DataException ex) {
			ex.printStackTrace();
			player.sendMessage("§3§lGLADIATOR§f Não foi possivel criar a §9§lARENA§f!");
			Gladiator.getInstance().getGladiatorManager().respawnPlayer(player);
			Gladiator.getInstance().getGladiatorManager().respawnPlayer(duel);
			return;
		}

		Location loc = GenerateLocation(player.getLocation());

		new GladiatorBattle(player, duel, gladiatorArena, loc);
	}

	public class GladiatorBattle {

		private boolean started = false;
		private Player gladiator;
		private Player target;
		private BukkitTask whiterTask;
		private BukkitTask endTask;
		private Listener gladiatorListener;

		public GladiatorBattle(Player gladiator, Player target, Schematic schematic, Location loc) {
			gladiatorBlocks = new HashMap<>();

			this.gladiator = gladiator;
			this.target = target;

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

//				@EventHandler
//				public void onScape(PlayerCommandPreprocessEvent event) {
//					if (inBattle(event.getPlayer())) {
//						event.setCancelled(true);
//						event.getPlayer()
//								.sendMessage("§3§lGLADIATOR§f Você não pode usar comandos durante uma §9§lBATALHA!");
//					}
//				}

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

					GladiatorBattle.this.gladiator.removePotionEffect(PotionEffectType.WITHER);
					GladiatorBattle.this.target.removePotionEffect(PotionEffectType.WITHER);

					destroy();
				}

				@EventHandler
				public void onQuit(PlayerQuitEvent event) {
					if (!inBattle(event.getPlayer())) {
						return;
					}

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

						event.getPlayer().teleport(event.getPlayer().getLocation());

					}
				}

			}, Gladiator.getInstance());

			callBattle(schematic, loc);
		}

		@SuppressWarnings("deprecation")
		public Object callBattle(Schematic schematic, Location loc) {
			World world = loc.getWorld();
			short[] blocks = schematic.getBlocks();
			byte[] blockData = schematic.getData();

			short length = schematic.getLenght();
			short width = schematic.getWidth();
			short height = schematic.getHeight();

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int z = 0; z < length; z++) {
						int index = y * width * length + z * width + x;
						Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
						gladiatorBlocks.put(
								new GladiatorBlock(block.getLocation().getBlockX(), block.getLocation().getBlockY(),
										block.getLocation().getBlockZ(), blocks[index], blockData[index]),
								block);

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
			gladiator.teleport(new Location(loc.getWorld(), loc.getBlockX() + 4, loc.getBlockY() + 2,
					loc.getBlockZ() + 4, -40, 0));

			target.setFallDistance(0);
			target.teleport(new Location(loc.getWorld(), loc.getBlockX() + 15, loc.getBlockY() + 2,
					loc.getBlockZ() + 16, 120, 7));

			gladiator.sendMessage("§3§lGLADIATOR§f Você está batalhando com §9§l" + target.getName() + "§f!");

			target.sendMessage("§3§lGLADIATOR§f Você está batalhando com §9§l" + gladiator.getName() + "§f!");

			return started = true;
		}

		public boolean inBattle(Player player) {
			return gladiator == player || target == player;
		}

		public void destroy() {
			HandlerList.unregisterAll(gladiatorListener);
			// gladiatorListener = null;

			for (Block b : gladiatorBlocks.values()) {
				b.setType(Material.AIR);
			}

			clearDrops();
			drops.clear();
			gladiatorBlocks.clear();
			// gladiatorBlocks = null;

			if (whiterTask != null) {
				whiterTask.cancel();
				// whiterTask = null;
			}

			if (endTask != null) {
				endTask.cancel();
				// endTask = null;
			}

			// gladiator = null;
			// target = null;

			// gladiatorLocation = null;
			// targetLocation = null;
		}
	}
}
