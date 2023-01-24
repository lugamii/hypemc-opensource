package br.com.mcweaven.gladiator.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.mcweaven.gladiator.gamer.Gamer;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		event.setJoinMessage(null);
		Player player = event.getPlayer();
		Gladiator.getInstance().getGamerManager().registerGamer(player.getUniqueId());

		player.setFoodLevel(20);
		player.setHealth(20);

		player.teleport(player.getWorld().getSpawnLocation());

		player.getInventory().setArmorContents(null);
		player.getInventory().clear();

		Gladiator.getInstance().getGladiatorManager().giveJoinItens(player);

		Gladiator.getInstance().getScoreboardManager().create(player);

	}

	@EventHandler
	public void onFoodChangeLevel(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getFight() == null) {
			event.getItem().remove();
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player)
			if (AdminMode.getInstance().isAdmin(event.getPlayer()))
				event.getPlayer().chat("/inv " + event.getRightClicked().getName());

	}

	@EventHandler
	public void onInventoryInteractEvent(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getFight() == null) {

			if (event.getInventory().getName().contains("Duelo") || player.getGameMode() == GameMode.CREATIVE)
				return;

			event.setCancelled(true);

		} else {
			if (player.getLocation().getBlock().getX() > 830 && player.getLocation().getBlock().getX() < 950) {
				if (player.getLocation().getBlock().getZ() > 160 && player.getLocation().getBlock().getZ() < 273) {
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectile(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.DISPENSE_EGG && event.getSpawnReason() != SpawnReason.CUSTOM
				&& event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void explode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		Player player = event.getEntity();
		Gamer playerGamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

		event.getDrops().clear();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (player != null) {
					player.spigot().respawn();
					Gladiator.getInstance().getGladiatorManager().respawnPlayer(player);
					player.setFireTicks(0);
				}
			}

		}.runTaskLater(Gladiator.getInstance(), 8l);

		if (playerGamer.getGladiatorEnemy() == null) {
			System.out.println("inimigo nulo wtf");
			Gladiator.getInstance().getScoreboardManager().create(player);
			return;
		}

		Player killer = Bukkit.getPlayer(playerGamer.getGladiatorEnemy());

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
		BukkitPlayer bK = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(killer.getUniqueId());
		Gamer killerGamer = Gladiator.getInstance().getGamerManager().getGamer(killer.getUniqueId());

		int xp = killerGamer.calculateXp(bK, bP);
		bK.addXp(xp);
		bK.addMoney(120);

		killer.setFireTicks(0);

		killerGamer.getFight().clearDrops();
		killerGamer.getFight().clearPlacedBlocks();

		Gladiator.getInstance().getGladiatorManager().respawnPlayer(killer);

		if (playerGamer.getFight() != null) {
			if (Gladiator.getInstance().getFightManager().fightExists(playerGamer.getFight()))
				Gladiator.getInstance().getFightManager().endFight(playerGamer.getFight());
			playerGamer.getFight().clearDrops();
			playerGamer.getFight().clearPlacedBlocks();
			playerGamer.setFight(null);
		}

		if (killerGamer.getFight() != null) {
			if (Gladiator.getInstance().getFightManager().fightExists(killerGamer.getFight()))
				Gladiator.getInstance().getFightManager().endFight(killerGamer.getFight());
			killerGamer.setFight(null);
		}

		killerGamer.setGladiatorEnemy(null);
		playerGamer.setGladiatorEnemy(null);

		playerGamer.addLose(1);

		killerGamer.addWin(1);
		killerGamer.addWinStreak(1);

		Gladiator.getInstance().getScoreboardManager().create(player);
		Gladiator.getInstance().getScoreboardManager().create(killer);

		killer.sendMessage("§4§lMORTE §fVocê matou o jogador §3§l" + player.getName() + "§f!");
		killer.sendMessage("§4§lMORTE §fForam adicionados §b§l" + xp + " XPS§f em sua conta!");
		killer.sendMessage("§4§lMORTE §fForam adicionados §b§l" + 120 + " MOEDAS§f em sua conta!");

		player.sendMessage("§4§lMORTE §fVocê morreu para §3§l" + killer.getName() + "§f!");

		if (killerGamer.getWinStreak() % 5 == 0 && killerGamer.getWinStreak() > 5)
			Bukkit.broadcastMessage("§3§lWINSTREAK§f O jogador §6§l" + killer.getName()
					+ " §festá com uma winstreak de §6§l" + killerGamer.getWinStreak() + "§f!");

		if (playerGamer.getWinStreak() > 10)
			Bukkit.broadcastMessage("§3§lWINSTREAK§f O jogador §6§l" + player.getName()
					+ " §fperdeu sua winstreak para §6§l" + killer.getName() + "§f!");
		playerGamer.removeWinStreak();

		Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(player, player.getLocation(), false));

	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() == null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
			if (gamer.getFight() == null)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() != null) {
			Gladiator.getInstance().getFightManager().endFight(gamer.getFight());
			gamer.setFight(null);
		}
		player.setFireTicks(0);
		Gladiator.getInstance().getGladiatorManager().respawnPlayer(player);
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (player.isOnline())
					Gladiator.getInstance().getGladiatorManager().respawnPlayer(player);
			}
		}.runTaskLater(Gladiator.getInstance(), 6l);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId());
		if (gamer.getGladiatorEnemy() != null) {

			gamer.removeWinStreak();
			gamer.addLose(1);

			Player killer = Bukkit.getPlayer(gamer.getGladiatorEnemy());
			Gamer killerGamer = Gladiator.getInstance().getGamerManager().getGamer(gamer.getGladiatorEnemy());

			killerGamer.setGladiatorEnemy(null);

			if (killerGamer.getWinStreak() % 5 == 0 && killerGamer.getWinStreak() > 5)
				Bukkit.broadcastMessage("§3§lWINSTREAK§f O jogador §6§l" + killer.getName()
						+ " §festá com uma winstreak de §6§l" + killerGamer.getWinStreak() + "§f!");

			if (gamer.getWinStreak() > 10)
				Bukkit.broadcastMessage("§3§lWINSTREAK§f O jogador §6§l" + event.getPlayer().getName()
						+ " §fperdeu sua winstreak para §6§l" + killer.getName() + "§f!");
			gamer.removeWinStreak();

			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
			BukkitPlayer bK = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(killer.getUniqueId());

			int xp = killerGamer.calculateXp(bK, bP);

			if (killerGamer.getFight() != null) {
				if (Gladiator.getInstance().getFightManager().fightExists(killerGamer.getFight()))
					Gladiator.getInstance().getFightManager().endFight(killerGamer.getFight());
				killerGamer.setFight(null);
			}
			Gladiator.getInstance().getGladiatorManager().respawnPlayer(killer);
			Gladiator.getInstance().getScoreboardManager().create(killer);

			killer.sendMessage("§4§lMORTE §fVocê matou o jogador §3§l" + player.getName() + "§f!");
			killer.sendMessage("§4§lMORTE §fForam adicionados §b§l" + (xp + 8) + " XPS§f em sua conta!");
			killer.sendMessage("§4§lMORTE §fForam adicionados §b§l" + 120 + " MOEDAS§f em sua conta!");

			bK.addXp(xp + 8);
			bK.addMoney(120);

			killerGamer.addWin(1);
			killerGamer.addWinStreak(1);

		}
		Gladiator.getInstance().getGamerManager().unregisterGamer(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSoup(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Material material = p.getItemInHand().getType();

		if (material == null || material != Material.MUSHROOM_SOUP) {
			return;
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (p.getHealth() < (p).getMaxHealth()) {
				int restores = 7;
				event.setCancelled(true);
				if (p.getHealth() + restores <= p.getMaxHealth())
					p.setHealth(p.getHealth() + restores);
				else
					p.setHealth(p.getMaxHealth());
				p.setItemInHand(new ItemBuilder().type(Material.BOWL).build());
			}
		}

		material = null;
		p = null;
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() == null)
			event.getItemDrop().remove();

	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() != null) {

			if (event.getBlock().getX() > 830 && event.getBlock().getX() < 950) {
				if (event.getBlock().getZ() > 160 && event.getBlock().getZ() < 273) {
					event.setCancelled(true);
					event.getPlayer().kickPlayer("§cOcorreu um erro, §erelogue§f!");
				}
			}

			if (event.getBlock().getType().toString().contains("GLASS")) {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() == null)
			event.setCancelled(true);

	}

}
