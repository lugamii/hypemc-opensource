package br.com.weavenmc.skywars.hability.events;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class CounterEvent implements Listener {
	
	private HashMap<UUID, Double> damage = new HashMap<>();
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;
		Player target = (Player) e.getDamager();
		Player player = (Player) e.getEntity();
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME) {
			return;
		}
		if (WeavenSkywars.getGameManager().getHabilityAPI().getHabilidade(player) == Hability.COUNTER) {
			if (!damage.containsKey(player.getUniqueId())) {
				damage.put(player.getUniqueId(), e.getDamage());
			} else {
				damage.put(player.getUniqueId(), damage.get(player.getUniqueId()) + e.getDamage());
			}
			if (new Random().nextInt(100) < 15) {
				target.damage(damage.get(player.getUniqueId()));
				damage.remove(player.getUniqueId());
			} else {
				damage.remove(player.getUniqueId());
			}
		}
	}

}
