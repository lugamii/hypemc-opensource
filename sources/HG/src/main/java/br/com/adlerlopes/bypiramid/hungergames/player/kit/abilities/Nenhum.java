package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Nenhum extends Kit {

	public Nenhum(Manager manager) {
		super(manager);
		
		setPrice(0);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.GLASS));
		setItems(new ItemStack(Material.AIR));
		setFree(false);
		setDescription("Kit sem habilidade");
	}
}
