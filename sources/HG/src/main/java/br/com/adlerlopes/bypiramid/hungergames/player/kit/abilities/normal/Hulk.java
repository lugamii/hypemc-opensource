package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Hulk extends Kit {

	public Hulk(Manager manager) {
		super(manager);
		
		setPrice(46000);
		setCooldownTime(10D);
		setIcon(new ItemStack(Material.SADDLE));
		setFree(false);
		setDescription("Use sua incrivel força e carregue os seus inimigos na sua cabeça.");
		setRecent(false);
	}

	@EventHandler
	public void hulk(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (isPreGame())
			return;
		if (event.getRightClicked() instanceof Player) {
			Player clicked = (Player) event.getRightClicked();
			if (!player.isInsideVehicle() && !clicked.isInsideVehicle() && player.getItemInHand().getType() == Material.AIR && hasKit(player)) {

				if (inCooldown(player)) {
					sendCooldown(player);
					return;
				}

				addCooldown(player);
				player.setPassenger(clicked);
			}
		}
	}

	@EventHandler
	public void noHulkMor(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {

			if (isPreGame())
				return;
			Player player = (Player) event.getEntity();
			Player hulk = (Player) event.getDamager();
			if (hulk.getPassenger() != null && hulk.getPassenger() == player && hasKit(hulk) && hulk.getPassenger() == player) {
				event.setCancelled(true);
				player.setSneaking(true);

				Vector v = player.getEyeLocation().getDirection().multiply(1.5F);
				v.setY(0.6D);
				player.setVelocity(v);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
					public void run() {
						player.setSneaking(false);
					}
				}, 10L);
			}
		}
	}
}
