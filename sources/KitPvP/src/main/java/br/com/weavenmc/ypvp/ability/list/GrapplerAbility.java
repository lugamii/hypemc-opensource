package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.util.Hook;

public class GrapplerAbility extends Ability {

	private HashMap<UUID, Hook> hooks = new HashMap<>();

	public GrapplerAbility() {
		setName("Grappler");
		setHasItem(true);
		setGroupToUse(Group.VIP);
		setIcon(Material.LEASH);
		setDescription(new String[] { "§7Seja capaz de ir á qualquer lugar", "§7com sua corda." });
		setPrice(50000);
		setTempPrice(5000);
	}

	@Override
	public void eject(Player p) {
		if (hooks.containsKey(p.getUniqueId())) {
			hooks.get(p.getUniqueId()).remove();
			hooks.remove(p.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!hasKit(event.getPlayer()))
			return;
		if (event.getItem() == null)
			return;
		Action a = event.getAction();
		Player p = event.getPlayer();
		ItemStack item = p.getItemInHand();
		if (!isItem(item))
			return;
		if (a.name().contains("RIGHT")) {
			event.setCancelled(true);
		}
		p.updateInventory();
		if (inCooldown(p)) {
			p.playSound(p.getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
			sendCooldown(p);
			return;
		}
		if (event.getAction().name().contains("LEFT")) {
			if (hooks.containsKey(p.getUniqueId())) {
				hooks.get(p.getUniqueId()).remove();
				hooks.remove(p.getUniqueId());
			}
			Hook hook = new Hook(p.getWorld(), ((CraftPlayer) p).getHandle());
			Vector direction = p.getLocation().getDirection();
			hook.spawn(p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()));
			hook.move(direction.getX() * 5.0D, direction.getY() * 5.0D, direction.getZ() * 5.0D);
			hooks.put(p.getUniqueId(), hook);
		} else if (event.getAction().name().contains("RIGHT")) {
			if (hooks.containsKey(p.getUniqueId())) {
				if (!hooks.get(p.getUniqueId()).isHooked()) {
					return;
				}
				Hook hook = hooks.get(p.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location pLoc = p.getLocation();
				double d = loc.distance(p.getLocation());
				double t = d;
				double v_x = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, pLoc) ? 0 : loc.getX() - pLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, pLoc) ? 0.1 : loc.getY() - pLoc.getY()) / t);
				double v_z = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, pLoc) ? 0 : loc.getZ() - pLoc.getZ()) / t);
				Vector v = p.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);
				p.setVelocity(v.multiply((((double) 10) / 10)));
				double player = p.getLocation().getY();
				double grappler = hook.getBukkitEntity().getLocation().getY();
				if (player < grappler || player > grappler) {
					p.setFallDistance(0);
				}
				p.getWorld().playSound(p.getLocation(), Sound.STEP_GRAVEL, 1.0F, 1.0F);
			}
		}
	}

	@EventHandler
	public void onPlayerItemHeldListener(PlayerItemHeldEvent e) {
		if (hooks.containsKey(e.getPlayer().getUniqueId())) {
			hooks.get(e.getPlayer().getUniqueId()).remove();
			hooks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuitListener(PlayerQuitEvent e) {
		if (hooks.containsKey(e.getPlayer().getUniqueId())) {
			hooks.remove(e.getPlayer().getUniqueId());
			hooks.get(e.getPlayer().getUniqueId()).remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeash(PlayerLeashEntityEvent event) {
		if (!hasKit(event.getPlayer()))
			return;
		Player p = event.getPlayer();
		if (p.getItemInHand() == null)
			return;
		ItemStack item = p.getItemInHand();
		if (!isItem(item))
			return;
		event.setCancelled(true);
		if (hooks.containsKey(p.getUniqueId())) {
			if (hooks.get(p.getUniqueId()).isHooked()) {
				Hook hook = hooks.get(p.getUniqueId());
				Location loc = hook.getBukkitEntity().getLocation();
				Location playerLoc = p.getLocation();
				double d = loc.distance(playerLoc);
				double t = d;
				double v_x = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, playerLoc) ? 0 : loc.getX() - playerLoc.getX()) / t);
				double v_y = (0.9D + 0.03D * t) * ((isNear(loc, playerLoc) ? 0.1 : loc.getY() - playerLoc.getY()) / t);
				double v_z = (1.0D + 0.04000000000000001D * t)
						* ((isNear(loc, playerLoc) ? 0 : loc.getZ() - playerLoc.getZ()) / t);
				Vector v = p.getVelocity();
				v.setX(v_x);
				v.setY(v_y);
				v.setZ(v_z);
				p.setVelocity(v.multiply((((double) 10) / 10)));
				double player = p.getLocation().getY();
				double grappler = hook.getBukkitEntity().getLocation().getY();
				if (player < grappler || player > grappler) {
					p.setFallDistance(0);
				}
				p.getWorld().playSound(playerLoc, Sound.STEP_GRAVEL, 1.0F, 1.0F);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (hasKit(p)) {
				if (!event.isCancelled()) {
					if (event.getDamager() instanceof Player) {
						if (hooks.containsKey(p.getUniqueId())) {
							hooks.get(p.getUniqueId()).remove();
							hooks.remove(p.getUniqueId());
						}
						addCooldown(p, 7);
					}
				}
			}
			p = null;
		}
	}

	private boolean isNear(Location loc, Location playerLoc) {
		return loc.distance(playerLoc) < 1.5;
	}
}
