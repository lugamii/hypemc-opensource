package br.com.weavenmc.skywars.hability.events;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.player.PlayerController;

public class VanessaEvent implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().getKiller() instanceof Player) {
				Player killer = e.getEntity().getKiller();
				if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(killer, Hability.VANESSA)) {
					if (killer.getInventory().getItemInHand().getType().name().contains("SWORD")) {
						if (PlayerController.kills.get(killer.getUniqueId()) >= 2 && killer.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) >= 2) {
							killer.sendMessage("§cSua espada chegou ao nível máximo do fire_aspect");
							return;
						}
						if (killer.getInventory().getItemInHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
							killer.getInventory().getItemInHand().removeEnchantment(Enchantment.FIRE_ASPECT);
						}
						killer.getInventory().getItemInHand().addEnchantment(Enchantment.FIRE_ASPECT, PlayerController.kills.get(killer.getUniqueId()));
						killer.updateInventory();
					} else {
						killer.sendMessage("§cO item em sua mão precisa ser um espada.");
					}
				}
			}
		}
	}

}
