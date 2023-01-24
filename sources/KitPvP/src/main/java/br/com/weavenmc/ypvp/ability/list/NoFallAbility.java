package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class NoFallAbility extends Ability {

	public NoFallAbility() {
		setName("NoFall");
		setHasItem(false);
		setGroupToUse(Group.PRO);
		setIcon(Material.IRON_BOOTS);
		setDescription(new String[] { "§7Não receba dano de queda." });
		setPrice(45000);
		setTempPrice(3500);
	}

	@Override
	public void eject(Player p) {
		
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (hasKit((Player) event.getEntity())) {
				if (event.getCause() == DamageCause.FALL) {
					event.setCancelled(true);
				}
			}
		}
	}
}
