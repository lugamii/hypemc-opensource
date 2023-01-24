package br.com.weavenmc.skywars.hability.events;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class JohnWickEvent implements Listener {
	
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
		if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(shooter, Hability.JOHN_WICK)) {
			Player shot = (Player) e.getEntity();
			
			double y = proj.getLocation().getY();
			double shotY = shot.getLocation().getY();
			boolean headshot = y - shotY > 1.35d;
			
			if (headshot) {
				e.setDamage(e.getDamage() * 2);
				shot.getLocation().getWorld().playEffect(shot.getLocation(), Effect.STEP_SOUND, 55);
			}
		}
	}

}
