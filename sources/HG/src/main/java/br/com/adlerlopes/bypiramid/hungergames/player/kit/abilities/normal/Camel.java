package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Camel extends Kit {

	private ShapelessRecipe shapelessRecipe;

	public Camel(Manager manager) {
		super(manager);

		setPrice(37500);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SAND));
		setFree(true);
		setDescription("Tenha habilidades no deserto, ganhe velocidade e use cactos para fazer sopa.");
		setRecent(false);

		ItemStack itemStack = new ItemStack(Material.MUSHROOM_SOUP);
		shapelessRecipe = new ShapelessRecipe(itemStack);
		shapelessRecipe.addIngredient(Material.BOWL);
		shapelessRecipe.addIngredient(Material.CACTUS);
		Bukkit.getServer().addRecipe(shapelessRecipe);
	}

	@EventHandler
	public void move(PlayerMoveEvent event) {
		if (event.getPlayer().getLocation().getBlock().getBiome() == Biome.DESERT && hasKit(event.getPlayer())) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
		}
	}

	@EventHandler
	public void blockHit(BlockDamageEvent event) {
		if (hasKit(event.getPlayer()) && event.getBlock().getType() == Material.CACTUS) {
			event.getBlock().breakNaturally();
		}
	}

}
