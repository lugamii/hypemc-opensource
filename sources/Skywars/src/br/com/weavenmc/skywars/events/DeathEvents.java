package br.com.weavenmc.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.actionbar.BarAPI;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.combatlog.Combat;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.player.PlayerController;

public class DeathEvents implements Listener {

	@EventHandler
	public void inVoid(PlayerMoveEvent event) {
		if (event.getTo().getY() < 0) {
			event.getPlayer().closeInventory();
		}

		Player player = event.getPlayer();
		if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("mapa")
				&& !event.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
			if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY) {
				GameController.player.remove(event.getPlayer().getUniqueId());
				WeavenSkywars.getGameManager().setSpectador(event.getPlayer());
				org.bukkit.Location location = WeavenSkywars.getSpawnsManager().getSpawn().get(1);
				event.getPlayer().teleport(location);
			}
		}

		if (GameController.spectador.contains(player.getUniqueId())) {
			double maxDist = 3;
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (other != player)
					if (other.getLocation().distance(player.getLocation()) <= maxDist) {
						player.setVelocity(new Vector().setY(3));
					}
			}

		}

	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (GameController.spectador.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {

	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		if (e.getEntity() instanceof Player) {
			Player target = e.getEntity();
			new BukkitRunnable() {
				@Override
				public void run() {
					target.spigot().respawn();
				}
			}.runTaskLater(WeavenSkywars.getInstance(), 1L);
			if (WeavenSkywars.getGameManager().getHabilityAPI().getHabilidade(target) == Hability.FENIX) {
				Bukkit.broadcastMessage("§7" + target.getName() + " §arenasceu como uma fênix!");
				return;
			}
			if (e.getEntity().getKiller() instanceof Player) {
				Player player = e.getEntity().getKiller();
				GameController.player.remove(target.getUniqueId());
				Bukkit.getOnlinePlayers().forEach(players -> {
					BarAPI.send(players, "§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
				});
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 2f);
				BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
				bPlayer.getData(DataType.SKYWARS_SOLO_KILLS)
						.setValue(bPlayer.getData(DataType.SKYWARS_SOLO_KILLS).asInt() + 1);
				bPlayer.getDataHandler().save(DataCategory.SKYWARS);
				PlayerController.kills.put(player.getUniqueId(), PlayerController.kills.get(player.getUniqueId()) + 1);

				if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.FLASH)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 0), true);
				}

				Bukkit.broadcastMessage("§7" + GameManager.getGroup(target) + "§e foi morto(a) pelo(a) §7"
						+ GameManager.getGroup(player));
			} else if (target.getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
				Projectile projectile = (Projectile) e.getEntity().getKiller();
				if (Combat.inCombat(target)) {
					GameController.player.remove(target.getUniqueId());
					Player player = Bukkit.getPlayer(Combat.combatLogs.get(target.getUniqueId()));
					Bukkit.getOnlinePlayers().forEach(players -> {
						BarAPI.send(players, "§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
					});
					target.spigot().respawn();
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 2f);
					BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
					bPlayer.getData(DataType.SKYWARS_SOLO_KILLS)
							.setValue(bPlayer.getData(DataType.SKYWARS_SOLO_KILLS).asInt() + 1);
					bPlayer.getDataHandler().save(DataCategory.SKYWARS);
					PlayerController.kills.put(player.getUniqueId(),
							PlayerController.kills.get(player.getUniqueId()) + 1);
					Bukkit.broadcastMessage("§7" + GameManager.getGroup(target) + "§e foi morto(a) por uma "
							+ projectile.getName().replace("_", " ").toLowerCase() + "§e jogada pelo(a) §7"
							+ GameManager.getGroup(player) + ".");
				}
			} else {
				if (Combat.inCombat(target)) {
					Player player = Bukkit.getPlayer(Combat.combatLogs.get(target.getUniqueId()));
					Bukkit.getOnlinePlayers().forEach(players -> {
						BarAPI.send(players, "§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
					});
					target.spigot().respawn();
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 2f);
					BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
					bPlayer.getData(DataType.SKYWARS_SOLO_KILLS)
							.setValue(bPlayer.getData(DataType.SKYWARS_SOLO_KILLS).asInt() + 1);
					bPlayer.getDataHandler().save(DataCategory.SKYWARS);
					PlayerController.kills.put(player.getUniqueId(),
							PlayerController.kills.get(player.getUniqueId()) + 1);
					Bukkit.broadcastMessage("§7" + GameManager.getGroup(target) + "§e foi morto(a) pelo(a) §7"
							+ GameManager.getGroup(player) + "§e.");
				} else {
					GameController.player.remove(target.getUniqueId());
					Bukkit.getOnlinePlayers().forEach(players -> {
						BarAPI.send(players, "§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
					});
					target.spigot().respawn();
					Bukkit.broadcastMessage("§7" + GameManager.getGroup(target) + "§e morreu.");

				}
			}
			WeavenSkywars.getGameManager().updatePlayerCheck(target);
			WeavenSkywars.getGameManager().checkWinner();
		}
	}

}
