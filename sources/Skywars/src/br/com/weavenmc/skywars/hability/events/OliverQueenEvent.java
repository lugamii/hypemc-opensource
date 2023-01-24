package br.com.weavenmc.skywars.hability.events;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class OliverQueenEvent implements Listener {
	
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
		Player shooter = (Player) proj.getShooter();
		if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(shooter, Hability.ARROW)) {
			Player shot = (Player) e.getEntity();
			int r = new Random().nextInt(100);
			if (r <= 25) {
				shot.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 1), true);
				shot.sendMessage(shooter.getName() + " §ete acertou com uma flecha envenenada.");
			} else if (r > 25 && r <= 75) {
				shot.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10*20, 0), true);
				shot.sendMessage(shooter.getName() + " §ete acertou com uma flecha wither.");
			} else if (r > 75) {
				shot.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0), true);
				shot.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10*20, 0), true);
				shot.sendMessage(shooter.getName() + " §ete acertou com uma flecha cegueira e lentidão.");
			}
		}
	}

}
