package br.com.adlerlopes.bypiramid.hungergames.player.kitaward.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class AwardListener extends HungerListener {

	@EventHandler()
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory() != null) {
			if (event.getInventory().getName().contains("surpresa")) {
				event.setCancelled(true);
			}
		}
	}
}
