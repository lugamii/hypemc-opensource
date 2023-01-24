package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class MagmaAbility extends Ability {

	public MagmaAbility() {
		setName("Magma");
		setHasItem(false);
		setGroupToUse(Group.PRO);
		setIcon(Material.WATER_BUCKET);
		setDescription(new String[] { "§7Não receba dano para nenhum elemento", "§7relacionado á fogo, a hitar seus",
				"§7oponentes há 30% de chance de eles", "§7pegarem fogo, porém receba dano na água." });
		setPrice(45000);
		setTempPrice(5500);
	}

	@Override
	public void eject(Player p) {
		// do NULL
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (!hasKit(o))
				continue;
			Location playerLoc = o.getLocation();
			if (playerLoc.add(0, -1, 0).getBlock().getType().equals(Material.STATIONARY_WATER)
					|| playerLoc.add(0, -1, 0).getBlock().getType().equals(Material.WATER)) {
				o.damage(6.0D);
			}
			playerLoc = null;
		}
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
						e.setFireTicks(150);
					}
					chance = null;
				}
			}
			e = null;
			d = null;
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (hasKit(p)) {
				DamageCause cause = event.getCause();
				if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA) {
					event.setCancelled(true);
					p.setFireTicks(0);
				}
				cause = null;
			}
			p = null;
		}
	}
}
