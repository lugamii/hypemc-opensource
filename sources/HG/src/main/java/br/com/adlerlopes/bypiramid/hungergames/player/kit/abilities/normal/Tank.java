package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Tank extends Kit {

	public Tank(Manager manager) {
		super(manager);
		
		this.setActive(false);
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.TNT));
		setFree(false);
		setDescription("Transforme-se em um tanque de guerra e resista a ataques de explosão.");
		setRecent(false);
	}

	@EventHandler
	public void tankDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		if (!hasKit(player))
			return;

		if (event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void tankEvent(PlayerDeathEvent event) {
		if (!(event.getEntity().getKiller() instanceof Player))
			return;
		if (event.getEntity().getKiller() == null)
			return;
		if (!hasKit(event.getEntity().getKiller()))
			return;

		if (hasKit(event.getEntity().getKiller()) && event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player) {
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 2.0F);
		}
	}

}
