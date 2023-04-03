package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Fireman extends Kit {

	public Fireman(Manager manager) {
		super(manager);
		
		setPrice(49000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.WATER_BUCKET));
		setFree(false);
		setDescription("Ganhe imunidade a altas temperaturas com sua habilidade de bombeiro.");
		setRecent(false);
		setItems(new ItemStack(Material.WATER_BUCKET));
	}

	@EventHandler
	public void onFireman(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player fireman = (Player) event.getEntity();
		if (!hasKit(fireman))
			return;

		DamageCause fire = event.getCause();
		if (fire == DamageCause.FIRE || fire == DamageCause.LAVA || fire == DamageCause.FIRE_TICK || fire == DamageCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}

}
