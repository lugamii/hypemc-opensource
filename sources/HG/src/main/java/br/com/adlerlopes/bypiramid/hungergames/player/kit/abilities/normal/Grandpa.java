package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Grandpa extends Kit {

	public Grandpa(Manager manager) {
		super(manager);
		
		setPrice(40000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STICK));
		setFree(false);
		setRecent(false);
		setDescription("Use seu graveto para arremessar seus oponentes para trás.");
		setItems(createItemStack("§aGrandpa", Material.STICK));
	}

	@EventHandler(ignoreCancelled = true)
	public void grandpaKit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (hasKit(player) && isKitItem(player.getItemInHand(), "§aGrandpa")) {
				Vector vector = ((LivingEntity) event.getEntity()).getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

				double knockBack = 2.0D;
				
				((LivingEntity) event.getEntity()).setVelocity(vector.multiply(knockBack));
			}
		}
	}
}
