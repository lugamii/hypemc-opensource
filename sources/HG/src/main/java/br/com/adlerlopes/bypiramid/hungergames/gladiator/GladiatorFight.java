package br.com.adlerlopes.bypiramid.hungergames.gladiator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal.Gladiator;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GladiatorFight extends HungerListener {

	private final Manager manager;
	private Player gladiator;
	private Player target;
	private Location tpLocGladiator;
	private Location tpLocTarget;
	private BukkitRunnable witherEffect;
	private BukkitRunnable teleportBack;
	private List<Block> blocksToRemove;
	private Listener listener;

	public GladiatorFight(Player gladiator, Player target, Manager m) {
		manager = m;

		this.gladiator = gladiator;
		this.target = target;
		this.blocksToRemove = new ArrayList<>();

		send1v1();
		this.listener = new Listener() {

			public HashMap<String, Long> canCheck = new HashMap<>();

			public void updateCheck(Player p) {
				canCheck.put(p.getName(), System.currentTimeMillis());
			}

			public boolean canCheck(Player p) {
				return System.currentTimeMillis() - canCheck.get(p.getName()) >= 10000L;
			}

			@EventHandler
			public void onEntityDamage(EntityDamageByEntityEvent event) {
				if (((event.getEntity() instanceof Player)) && ((event.getDamager() instanceof Player))) {
					Player recebe = (Player) event.getEntity();
					Player faz = (Player) event.getDamager();
					if ((isIn1v1(faz)) && (isIn1v1(recebe))) {
						return;
					}
					if ((isIn1v1(faz)) && (!isIn1v1(recebe))) {
						event.setCancelled(true);
					} else if ((!isIn1v1(faz)) && (isIn1v1(recebe))) {
						event.setCancelled(true);
					}
				}
			}

			@EventHandler(priority = EventPriority.LOWEST)
			public void onDeath(PlayerDeathEvent event) {
				Player player = event.getEntity();
				if (!isIn1v1(player)) {
					return;
				}
				if (player == gladiator) {
					target.sendMessage(
							"§b§lGLADIATOR §fVocê venceu a batalha contra §3§l" + gladiator.getName() + "§f!");
					gladiator.sendMessage(
							"§b§lGLADIATOR §fVocê perdeu a batalha contra §3§l" + target.getName() + "§f!");
					dropItems(player, event.getDrops(), tpLocGladiator);
					event.getDrops().clear();
					teleportBack(target, gladiator);
					return;
				}

				target.sendMessage("§b§lGLADIATOR §fVocê perdeu a batalha contra §3§l" + gladiator.getName() + "§f!");
				gladiator.sendMessage("§b§lGLADIATOR §fVocê ganhou a batalha contra §3§l" + target.getName() + "§f!");

				dropItems(player, event.getDrops(), tpLocTarget);
				event.getDrops().clear();
				teleportBack(gladiator, target);
				gladiator.removePotionEffect(PotionEffectType.WITHER);
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent event) {
				Player player = event.getPlayer();
				if (!isIn1v1(player))
					return;
				if (event.getPlayer().isDead())
					return;

				if (player == gladiator) {
					target.sendMessage(
							"§b§lGLADIATOR §fVocê ganhou a batalha contra §3§l" + gladiator.getName() + "§f!");
					dropItems(player, tpLocGladiator);
					teleportBack(target, gladiator);
					gladiator.removePotionEffect(PotionEffectType.WITHER);
				} else {
					gladiator.sendMessage(
							"§b§lGLADIATOR §fVocê ganhou a batalha contra §3§l" + target.getName() + "§f!");
					dropItems(player, tpLocTarget);
					teleportBack(gladiator, target);
					gladiator.removePotionEffect(PotionEffectType.WITHER);
				}
			}

			@EventHandler
			public void onKick(PlayerKickEvent event) {
				Player player = event.getPlayer();
				if (!isIn1v1(player))
					return;
				if (event.getPlayer().isDead())
					return;

				if (player == gladiator) {
					target.sendMessage(
							"§b§lGLADIATOR §fVocê ganhou a batalha contra §3§l" + gladiator.getName() + "§f!");
					dropItems(player, tpLocGladiator);
					gladiator.removePotionEffect(PotionEffectType.WITHER);
					teleportBack(target, gladiator);
				} else {
					gladiator.sendMessage(
							"§b§lGLADIATOR §fVocê ganhou a batalha contra §3§l" + target.getName() + "§f!");
					gladiator.removePotionEffect(PotionEffectType.WITHER);
					dropItems(player, tpLocTarget);
					teleportBack(gladiator, target);
				}
			}

			@EventHandler
			public void onQuitGladiator(PlayerMoveEvent event) {
				if (!isIn1v1(event.getPlayer()))
					return;
				if (blocksToRemove.contains(event.getTo().getBlock()))
					return;
				if (!canCheck.containsKey(event.getPlayer().getName()))
					canCheck.put(event.getPlayer().getName(), 0L);

				if (event.getPlayer() == gladiator) {
					new BukkitRunnable() {
						public void run() {
							if (!canCheck(event.getPlayer()))
								return;
							event.getPlayer().setFallDistance(0);
							teleportBack(target, gladiator);
							event.getPlayer().setFallDistance(0);
							updateCheck(event.getPlayer());
							return;

						}
					}.runTaskLater(manager.getPlugin(), 2L);
				}
				new BukkitRunnable() {
					public void run() {
						if (!canCheck(event.getPlayer())) {
							return;
						}
						event.getPlayer().setFallDistance(0);
						teleportBack(gladiator, target);
						event.getPlayer().setFallDistance(0);
						updateCheck(event.getPlayer());
						return;

					}
				}.runTaskLater(manager.getPlugin(), 2L);

			}

		};
		manager.registerListener(listener);
	}

	public boolean isIn1v1(Player player) {
		return (player == this.gladiator) || (player == this.target);
	}

	public void destroy() {
		HandlerList.unregisterAll(this.listener);
	}

	public void send1v1() {
		Location loc = this.gladiator.getLocation();

		boolean hasGladi = true;
		while (hasGladi) {
			hasGladi = false;
			boolean stop = false;
			for (double x = -8.0D; x <= 8.0D; x += 1.0D) {
				for (double z = -9.0D; z <= 9.0D; z += 1.0D) {
					for (double y = 0.0D; y <= 15.0D; y += 1.0D) {
						Location l = new Location(loc.getWorld(), loc.getX() + x, 100.0D + y, loc.getZ() + z);
						if (l.getBlock().getType() != Material.AIR) {
							hasGladi = true;
							loc = new Location(loc.getWorld(), loc.getX() + 20.0D, loc.getY(), loc.getZ());
							stop = true;
						}
						if (stop) {
							break;
						}
					}
					if (stop) {
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		}
		Block mainBlock = loc.getBlock();
		generateArena(mainBlock);

		this.tpLocGladiator = this.gladiator.getLocation().clone();
		this.tpLocTarget = this.target.getLocation().clone();

		String messageGladiator = "§b§lGLADIATOR §fVocê §a§lDESAFIOU§f o player §3§l" + target.getName()
				+ "§f para uma batalha!";
		String messageTarget = "§b§lGLADIATOR §fVocê foi §a§lDESAFIADO§f pelo player §3§l" + gladiator.getName()
				+ "§f para uma batalha!";

		this.gladiator.sendMessage(messageGladiator);

		this.target.sendMessage(messageTarget);

		Location l1, l2;

		l1 = new Location(mainBlock.getWorld(), mainBlock.getX() + 6.5D, 101.0D, mainBlock.getZ() + 6.5D);
		l1.setYaw(135.0F);

		l2 = new Location(mainBlock.getWorld(), mainBlock.getX() - 5.5D, 101.0D, mainBlock.getZ() - 5.5D);
		l2.setYaw(315.0F);

		this.target.teleport(l1);
		this.gladiator.teleport(l2);

		this.witherEffect = new BukkitRunnable() {
			public void run() {
				gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
				target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
			}
		};
		this.witherEffect.runTaskLater(manager.getPlugin(), 2400L);

		this.teleportBack = new BukkitRunnable() {
			public void run() {
				teleportBack(tpLocGladiator, tpLocTarget);
			}
		};
		this.teleportBack.runTaskLater(manager.getPlugin(), 3600L);

		Gladiator.playersIn1v1.add(this.gladiator.getUniqueId());
		Gladiator.playersIn1v1.add(this.target.getUniqueId());
	}

	public void teleportBack(Location loc, Location loc1) {
		Gladiator.playersIn1v1.remove(this.gladiator.getUniqueId());
		Gladiator.playersIn1v1.remove(this.target.getUniqueId());
		this.gladiator.teleport(loc);
		this.target.teleport(loc1);
		removeBlocks();
		this.gladiator.removePotionEffect(PotionEffectType.WITHER);
		this.target.removePotionEffect(PotionEffectType.WITHER);
		stop();
		destroy();
	}

	public void teleportBack(Player winner, Player loser) {
		Gladiator.playersIn1v1.remove(winner.getUniqueId());
		Gladiator.playersIn1v1.remove(loser.getUniqueId());
		winner.teleport(this.tpLocGladiator);
		loser.teleport(this.tpLocTarget);
		removeBlocks();
		winner.removePotionEffect(PotionEffectType.WITHER);
		stop();
		destroy();
	}

	public void generateArena(Block mainBlock) {
		Location l = mainBlock.getLocation();
		l.setY(100.0D);
		for (Block b : manager.getBO2().spawn(l,
				new File(HungerGames.getManager().getPlugin().getDataFolder() + "/gladiator.bo2"))) {
			Gladiator.gladiatorBlocks.add(b);
			this.blocksToRemove.add(b);
		}
		for (double x = -7.0D; x <= 7.0D; x += 1.0D) {
			for (double z = -7.0D; z <= 7.0D; z += 1.0D) {
				for (double y = 1.0D; y <= 20.0D; y += 1.0D) {
					Location loc = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 100.0D + y,
							mainBlock.getZ() + z);
					this.blocksToRemove.add(loc.getBlock());
				}
			}
		}
	}

	public void dropItems(Player p, Location l) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : p.getPlayer().getInventory().getContents()) {
			if ((item != null) && (item.getType() != Material.AIR)) {
				items.add(item.clone());
			}
		}
		for (ItemStack item : p.getPlayer().getInventory().getArmorContents()) {
			if ((item != null) && (item.getType() != Material.AIR)) {
				items.add(item.clone());
			}
		}
		if ((p.getPlayer().getItemOnCursor() != null) && (p.getPlayer().getItemOnCursor().getType() != Material.AIR)) {
			items.add(p.getPlayer().getItemOnCursor().clone());
		}
		dropItems(p, items, l);
	}

	public void dropItems(Player p, List<ItemStack> items, Location l) {
		World world = l.getWorld();
		for (ItemStack item : items) {
			if ((item != null) && (item.getType() != Material.AIR)) {
				if (!manager.getKitManager().isItemKit(item))
					if ((item.getType() != Material.POTION) || (item.getDurability() != 8201)) {
						if (item.getType() != Material.SKULL_ITEM) {
							if (item.hasItemMeta()) {
								world.dropItemNaturally(l, item.clone()).getItemStack().setItemMeta(item.getItemMeta());
							} else {
								world.dropItemNaturally(l, item);
							}
						}
					}
			}
		}
	}

	public void removeBlocks() {
		for (Block b : this.blocksToRemove) {
			if ((b.getType() != null) && (b.getType() != Material.AIR)) {
				manager.getBO2().setBlockFast(b.getLocation(), Material.AIR, (byte) 0);
			}
			Gladiator.gladiatorBlocks.remove(b);
		}
		this.blocksToRemove.clear();
	}

	public void stop() {
		this.witherEffect.cancel();
		this.teleportBack.cancel();
	}
}
