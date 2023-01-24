package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class ViperAbility extends Ability {

	public ViperAbility() {
		setName("Viper");
		setHasItem(false);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.SPIDER_EYE);
		setDescription(
				new String[] { "§7A cada hit tenha 30% de chance de", "§7seu inimigo pegar efeito de veneno." });
		setPrice(50000);
		setTempPrice(2000);
	}

	@Override
	public void eject(Player p) {
		p.getActivePotionEffects().clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (hasKit(p)) {
				if (!event.isCancelled()) {
					Player target = (Player) event.getEntity();
					if (!target.hasPotionEffect(PotionEffectType.POISON)) {
						int random = new Random().nextInt(100);
						if (random > 0 && random <= 30) {
							target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 1));
						}
					}
				}
			}
		}
	}
}
