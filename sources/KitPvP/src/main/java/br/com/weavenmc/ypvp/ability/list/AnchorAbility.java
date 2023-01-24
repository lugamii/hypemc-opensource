package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class AnchorAbility extends Ability {

	public AnchorAbility() {
		setName("Anchor");
		setHasItem(false);
		setGroupToUse(Group.VIP);
		setIcon(Material.ANVIL);
		setDescription(new String[] { "§7Não dê e nem receba knockback." });
		setPrice(55000);
		setTempPrice(5000);
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player e = (Player) event.getEntity();
			Player d = (Player) event.getDamager();
			if (hasKit(e) || hasKit(d)) {
				if (!event.isCancelled()) {
					anchor(e, d);
				}
			}
		}
	}
	
	public void anchor(Player a, Player b) {
		a.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
		b.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
		Bukkit.getScheduler().runTaskLater(yPvP.getPlugin(), () -> {
			a.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
			b.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
		}, 1L);
	}
}
