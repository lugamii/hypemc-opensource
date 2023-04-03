package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Boxer extends Kit {

	public Boxer(Manager manager) {
		super(manager);
		
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STONE_SWORD));
		setFree(false);
		setDescription("Dê 0.5 coraçoes a mais de dano e receba 0.5 a menos");
		setRecent(false);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onHitBoxer(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player player = (Player) event.getDamager();
			if (hasKit(player) && player.getItemInHand().getType() == Material.AIR) {
				event.setDamage(event.getDamage() + 2);
				return;
			}
			if (hasKit(player) && player.getItemInHand().getType() != Material.AIR) {
				event.setDamage(event.getDamage() + 0.50);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageBoxer(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (hasKit(player) && event.getDamage() > 1.0D) {
				event.setDamage(event.getDamage() - 0.50D);
			}
		}
	}

}
