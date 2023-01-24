package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class CriticalAbility extends Ability {

	public CriticalAbility() {
		setName("Critical");
		setHasItem(false);
		setGroupToUse(Group.BETA);
		setIcon(Material.REDSTONE_BLOCK);
		setDescription(new String[] { "§7A cada hit tenha 30% de um", "§7critical aumentado 3x." });
		setPrice(75000);
		setTempPrice(7000);
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player e = (Player) event.getEntity();
			Player d = (Player) event.getDamager();
			if (hasKit(d)) {
				if (!event.isCancelled()) {
					Integer chance = new Random().nextInt(100);
					if (chance > 0 && chance <= 30) {
						event.setDamage(event.getDamage() + 1.50D);
						d.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 10);
						e.sendMessage("§5§lCRITICAL§f Você recebeu um §9§lGOLPE CRITICO§f de §9§l" + d.getName());
						d.sendMessage("§5§lCRITICAL§f Você deu um §9§lGOLPE CRITICO§f no §9§l" + e.getName());
					}
					chance = null;
				}
			}
			e = null;
			d = null;
		}
	}
}
