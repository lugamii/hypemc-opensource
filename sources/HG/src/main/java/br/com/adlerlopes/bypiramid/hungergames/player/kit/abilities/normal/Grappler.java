package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.Hook;

/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Grappler extends Kit {

	private HashMap<UUID, Hook> hooks = new HashMap<>();
	private static HashMap<UUID, Long> hitNerf = new HashMap<>();

	public Grappler(Manager manager) {
		super(manager);
		
		setPrice(52000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.LEASH));
		setFree(false);
		setDescription("Tenha suas hablidades de um escalador profissional e com sua corda.");
		setRecent(false);
		setItems(createItemStack("§aGrappler", Material.LEASH));
	}

	@EventHandler
	private void grapplerHabilidade(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		if (p.getItemInHand().getType().equals(Material.LEASH)) {
			if (hasKit(p)) {
				event.setCancelled(true);

				if (hitNerf.containsKey(p.getUniqueId())
						&& hitNerf.get(p.getUniqueId()) > System.currentTimeMillis()) {
					p.setVelocity(new Vector(0, -1.0, 0));
					p.sendMessage("§6§lGRAPPLER §fVocê está em §4§lCOMBATE§f, aguarde para usar sua habilidade.");
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
		if (item.getType() == Material.LEASH)
			return;
		event.setCancelled(true);
		
		if (hitNerf.containsKey(p.getUniqueId())
				&& hitNerf.get(p.getUniqueId()) > System.currentTimeMillis()) {
			p.setVelocity(new Vector(0, -1.0, 0));
			p.sendMessage("§6§lGRAPPLER §fVocê está em §4§lCOMBATE§f, aguarde para usar sua habilidade.");
			return;
		}
		
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
	private void onRemoveLeash(PlayerItemHeldEvent event) {
		if (hooks.containsKey(event.getPlayer().getUniqueId())) {
			hooks.get(event.getPlayer().getUniqueId()).remove();
			hooks.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!hasKit((Player) event.getEntity()))
			return;
		if (event.isCancelled())
			return;
		if (!(event.getDamager() instanceof LivingEntity))
			return;
		if (getManager().getGameManager().isInvencibility())
			return;
		if (event.getDamager() instanceof Player && getGamer((Player) event.getDamager()).isSpectating())
			return;

		hitNerf.put(event.getEntity().getUniqueId(), System.currentTimeMillis() + 5000L);
		
		if (hooks.containsKey(((Player) event.getEntity()).getUniqueId())) {
			hooks.get(((Player) event.getEntity()).getUniqueId()).remove();
			hooks.remove(((Player) event.getEntity()).getUniqueId());
		}
	}

	@EventHandler
	public void KitDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		ItemStack itemStack = event.getItemDrop().getItemStack();
		if (hasKit(player) && (itemStack.getType() == Material.LEASH)) {
			event.setCancelled(true);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
				public void run() {
					player.updateInventory();
				}
			}, 1L);
		}
	}

	private boolean isNear(Location loc, Location playerLoc) {
		return loc.distance(playerLoc) < 1.5;
	}
}
