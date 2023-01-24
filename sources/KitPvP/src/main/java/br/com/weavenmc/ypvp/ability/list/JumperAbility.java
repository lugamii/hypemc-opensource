package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class JumperAbility extends Ability {

	public JumperAbility() {
		setName("Jumper");
		setHasItem(true);
		setGroupToUse(Group.VIP);
		setIcon(Material.EYE_OF_ENDER);
		setDescription(new String[] { "§7Monte em sua ender pearl", "§7e voe junto com ela." });
		setPrice(80000);
		setTempPrice(8500);
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperListener(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand())) {
				event.setCancelled(true);
				p.updateInventory();
				if (!inCooldown(p)) {
					addCooldown(p, 15);
					p.setFallDistance(0);
					EnderPearl ender = p.launchProjectile(EnderPearl.class);
					ender.setPassenger(p);
					ender.setMetadata("Jumper", new FixedMetadataValue(yPvP.getPlugin(), p.getUniqueId()));
					ender.setShooter(null);
				} else {
					sendCooldown(p);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperHit(ProjectileHitEvent event) {
		if (!event.getEntity().hasMetadata("Jumper"))
			return;
		if (event.getEntity().getPassenger() != null) {
			event.getEntity().eject();
		}
	}
}
