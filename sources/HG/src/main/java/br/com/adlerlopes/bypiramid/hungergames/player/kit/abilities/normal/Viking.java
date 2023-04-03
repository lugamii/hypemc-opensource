package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Viking extends Kit {

	public Viking(Manager manager) {
		super(manager);
		
		setPrice(47500);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STONE_AXE));
		setFree(false);
		setDescription("Use seu machado viking para atingir mais facilmente seus inimigos.");
		setRecent(false);
//		setItems(new ItemStack(Material.STONE_AXE));
	}

	@EventHandler
	public void onViking(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		if (!hasKit(player) || player.getItemInHand() == null || !player.getItemInHand().getType().name().contains("AXE"))
			return;

		event.setDamage(event.getDamage() + 1.0D);
		if (event.getDamager().getFallDistance() > 0.0D) {
			event.setDamage(event.getDamage() + 1.0D);
		}
	}

}
