package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class SwitcherAbility extends Ability {

	public SwitcherAbility() {
		setName("Switcher");
		setHasItem(true);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.EGG);
		setDescription(new String[] { "§7Lançe sua bolinha em seu oponente", "§7e troque de lugar com ele." });
		setPrice(45000);
		setTempPrice(2500);
	}

	@Override
	public void eject(Player p) {
		
	}

	@EventHandler
	public void onSwticher(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand()) && event.getAction().name().contains("RIGHT")) {
				event.setCancelled(true);
				p.updateInventory();
				
				if (!inCooldown(p)) {
					addCooldown(p, 12);
					
					Egg egg = p.launchProjectile(Egg.class);
					egg.setMetadata("Switcher", new FixedMetadataValue(yPvP.getPlugin(), p.getUniqueId()));
					egg.setShooter(p);
					egg.setVelocity(egg.getVelocity().multiply(2));
				} else {
					sendCooldown(p);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getDamager().hasMetadata("Switcher")) {
			if (event.getDamager() instanceof Egg) {
				Egg egg = (Egg) event.getDamager();
				if (egg.getShooter() != null && egg.getShooter() instanceof Player) {
					Player shooter = (Player) egg.getShooter();
					if (event.getEntity() instanceof Player) {
						Player entity = (Player) event.getEntity();
						Gamer gamer = gamer(entity);
						if (!gamer.getWarp().isProtected(entity)) {
							final Location comeHereBaby = shooter.getLocation();
							final Location iBeThere = entity.getLocation();
							
							shooter.teleport(iBeThere);
							entity.teleport(comeHereBaby);
						}
					}
				}
			}
		}
	}
}
