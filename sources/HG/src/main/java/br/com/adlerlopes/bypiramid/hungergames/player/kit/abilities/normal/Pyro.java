package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Pyro extends Kit {

	public Pyro(Manager manager) {
		super(manager);
		
		setPrice(42000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.FIREBALL));
		setFree(false);
		setDescription("Tenha a habilidade de criar bolas de fogo para lançar em seus inimigos.");
		setRecent(false);
		setItems(createItemStack("§cPyro", Material.FIREBALL, 5), new ItemStack(Material.FLINT_AND_STEEL));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.FIREBALL && hasKit(player)) {
			item.setAmount(item.getAmount() - 1);

			if (item.getAmount() == 0)
				player.setItemInHand(new ItemStack(Material.AIR));

			Fireball fireball = player.launchProjectile(Fireball.class);
			fireball.setIsIncendiary(true);
			fireball.setYield(fireball.getYield() * 1.5F);
		}
	}
}
