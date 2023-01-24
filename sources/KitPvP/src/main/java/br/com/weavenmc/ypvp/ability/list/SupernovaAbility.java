package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class SupernovaAbility extends Ability {

	private ArrayList<ArrowDirection> directions;
	private HashMap<Arrow, Vector> arrows;

	public SupernovaAbility() {
		setName("Supernova");
		setHasItem(true);
		setGroupToUse(Group.PRO);
		setIcon(Material.ARROW);
		setDescription(new String[] { "§7Invoque flechas ao seu redor e", "§7cause dano em seus inimigos." });
		setPrice(90000);
		setTempPrice(6500);

		directions = new ArrayList<>();
		ArrayList<Double> list = new ArrayList<>();
		list.add(0.0);
		list.add(22.5);
		list.add(45.0);
		list.add(67.5);
		list.add(90.0);
		list.add(112.5);
		list.add(135.0);
		list.add(157.5);
		list.add(180.0);
		list.add(202.5);
		list.add(225.0);
		list.add(247.5);
		list.add(270.0);
		list.add(292.5);
		list.add(315.0);
		list.add(337.5);
		for (double i : list) {
			directions.add(new ArrowDirection(i, 67.5));
			directions.add(new ArrowDirection(i, 45.0));
			directions.add(new ArrowDirection(i, 22.5));
			directions.add(new ArrowDirection(i, 0.0));
			directions.add(new ArrowDirection(i, -22.5));
			directions.add(new ArrowDirection(i, -45));
			directions.add(new ArrowDirection(i, -67.5));
		}
		directions.add(new ArrowDirection(90.0, 0.0));
		directions.add(new ArrowDirection(-90.0, 0.0));
		directions.add(new ArrowDirection(0.0, 90.0));
		directions.add(new ArrowDirection(0.0, -90.0));
		list.clear();
		list = null;
		arrows = new HashMap<>();
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		Iterator<Entry<Arrow, Vector>> entrys = arrows.entrySet().iterator();
		while (entrys.hasNext()) {
			Entry<Arrow, Vector> entry = entrys.next();
			Arrow arrow = entry.getKey();
			Vector vec = entry.getValue();
			if (!arrow.isDead()) {
				arrow.setVelocity(vec.normalize().multiply(vec.lengthSquared() / 4));
				if (arrow.isOnGround() || arrow.getTicksLived() >= 100) {
					arrow.remove();
				}
			} else {
				entrys.remove();
			}
		}
	}

	@EventHandler
	public void onSupernovaListener(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand()) && event.getAction().toString().contains("RIGHT")) {
				event.setCancelled(true);
				if (inCooldown(p)) {
					sendCooldown(p);
					return;
				} 			
				addCooldown(p, 20);
				Location loc = p.getLocation();
				for (ArrowDirection d : directions) {
					synchronized (this) {
						final Arrow arrow = loc.getWorld().spawn(loc.clone().add(0, 1, 0), Arrow.class);
						arrow.setMetadata("Supernova", new FixedMetadataValue(yPvP.getPlugin(), p.getUniqueId()));
						double pitch = ((d.pitch + 90) * Math.PI) / 180;
						double yaw = ((d.yaw + 90) * Math.PI) / 180;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.sin(pitch) * Math.sin(yaw);
						double z = Math.cos(pitch);
						Vector vec = new Vector(x, z, y);
						arrow.setShooter(p);
						arrow.setVelocity(vec.multiply(2));
						arrows.put(arrow, vec);
					}
				}
				p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 0.5F, 1.0F);
			}
		}
	}

	private Set<UUID> damaged = new HashSet<>();

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager().hasMetadata("Supernova")) {
			if (e.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) e.getDamager();
				if (arrow.getShooter() instanceof Player) {
					Player s = (Player) arrow.getShooter();
					if (e.getEntity() instanceof Player) {
						Player p = (Player) e.getEntity();
						if (s.getUniqueId() == p.getUniqueId()) {
							e.setCancelled(true);
							return;
						}
						if (damaged.contains(p.getUniqueId())) {
							e.setCancelled(true);
							return;
						}
					}
					e.setDamage(10.0D);
					if (e.getEntity() instanceof Player) {
						Player p = (Player) e.getEntity();
						damaged.add(p.getUniqueId());
						new BukkitRunnable() {
							@Override
							public void run() {
								damaged.remove(p.getUniqueId());
							}
						}.runTaskLater(yPvP.getPlugin(), 10);
					}
				}
			}
		}
	}

	protected static class ArrowDirection {

		private double pitch;
		private double yaw;

		public ArrowDirection(double pitch, double yaw) {
			this.pitch = pitch;
			this.yaw = yaw;
		}

	}
}
