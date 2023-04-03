package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Worm extends Kit {

	public Worm(Manager manager) {
		super(manager);

		setPrice(41000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.DIRT));
		setFree(false);
		setDescription("Transforme-se em uma minhoca e regenere sua fome comendo terra.");
		setRecent(false);
	}

	@EventHandler
	public void onDamage(BlockDamageEvent event) {
		if (hasKit(event.getPlayer()) && event.getBlock().getType() == Material.DIRT) {
			event.setInstaBreak(true);
			if (((Damageable) event.getPlayer()).getHealth() < ((Damageable) event.getPlayer()).getMaxHealth()) {
				double life = ((Damageable) event.getPlayer()).getHealth() + 2.0D;
				if (life > ((Damageable) event.getPlayer()).getMaxHealth()) {
					life = ((Damageable) event.getPlayer()).getMaxHealth();
				}
				event.getPlayer().setHealth(life);
			} else if (event.getPlayer().getFoodLevel() < 20) {
				event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 1);
			}
		}
	}
}
