package br.com.weavenmc.skywars.hability.events;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class YatiEvent implements Listener {

	private ArrayList<UUID> array = new ArrayList<>();

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getCause() != DamageCause.PROJECTILE)
			return;
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		Projectile proj = (Projectile) e.getDamager();
		if (!(proj.getShooter() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		if (!(e.getDamager() instanceof Snowball))
			return;
		Player shooter = (Player) proj.getShooter();
		if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(shooter, Hability.YATI)) {
			System.out.println("[Yati-Debug] Ueee");
			Player shot = (Player) e.getEntity();
			if (!array.contains(shot.getUniqueId())) {
				System.out.println("[Yati-Debug] Array off");
				shot.sendMessage("§7" + shooter.getName() + " §ete congelou por 5 segundos.");
			}
			array.add(shot.getUniqueId());
			shot.playSound(shot.getLocation(), Sound.DIG_SNOW, 2f, 2f);
			new BukkitRunnable() {

				@Override
				public void run() {
					if (array.contains(shot.getUniqueId())) {
						array.remove(shot.getUniqueId());
						System.out.println("[Yati-Debug] Array remove");
						shot.sendMessage("§eO gelo derreteu, agora você está livre.");
					}

				}
			}.runTaskLater(WeavenSkywars.getInstance(), 20 * 5);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (array.contains(player.getUniqueId())) {
			if (player.isOnGround()) {
				Location to = e.getTo();
				Location from = e.getFrom();
				if (to.getX() != from.getX())
					player.teleport(to);
				if (to.getY() != from.getY())
					player.teleport(to);
				if (to.getZ() != from.getZ())
					player.teleport(to);
			}
		}
	}

}
