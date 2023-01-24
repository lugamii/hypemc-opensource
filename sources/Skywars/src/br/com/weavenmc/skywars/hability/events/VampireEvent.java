package br.com.weavenmc.skywars.hability.events;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class VampireEvent implements Listener {
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;
		Player player = (Player) e.getDamager();
		Player target = (Player) e.getEntity();
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME) {
			return;
		}
		if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.VAMPIRE)) {
			if (new Random().nextInt(100) < 25) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1), true);
				e.setDamage(e.getDamage() + 4D);
				target.getLocation().getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, 55);
			}
		}
	}

}
