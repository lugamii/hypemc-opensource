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

public class ReaperAbility extends Ability {

	public ReaperAbility() {
		setName("Reaper");
		setHasItem(false);
		setGroupToUse(Group.VIP);
		setIcon(Material.WOOD_AXE);
		setDescription(
				new String[] { "§7A cada hit tenha 30% de chance de", "§7seu inimigo pegar efeito de wither." });
		setPrice(50000);
		setTempPrice(5300);
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
					if (!target.hasPotionEffect(PotionEffectType.WITHER)) {
						int random = new Random().nextInt(100);
						if (random > 0 && random <= 30) {
							target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 3));
						}
					}
				}
			}
		}
	}
}
