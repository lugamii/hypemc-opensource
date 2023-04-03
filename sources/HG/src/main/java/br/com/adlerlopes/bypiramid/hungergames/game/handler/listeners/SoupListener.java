package br.com.adlerlopes.bypiramid.hungergames.game.handler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SoupListener extends HungerListener {

	@EventHandler
	public void onSoup(PlayerInteractEvent event) {
		if (event.hasItem() && event.getItem().getType() == Material.MUSHROOM_SOUP
				&& event.getAction().name().contains("RIGHT")) {
			event.setCancelled(true);
			
			Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(event.getPlayer().getUniqueId());
			boolean bool = false;

			if (((Damageable) event.getPlayer()).getHealth() < ((Damageable) event.getPlayer()).getMaxHealth()) {
				double hp = ((Damageable) event.getPlayer()).getHealth() + 7;
				if (hp > ((Damageable) event.getPlayer()).getMaxHealth()) {
					hp = ((Damageable) event.getPlayer()).getMaxHealth();
				}
				event.getPlayer().setHealth(hp);
				bool = true;
			} else if (event.getPlayer().getFoodLevel() < 20) {
				event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 7);
				bool = true;
			}
			if (bool) {
				event.getPlayer().setItemInHand(new ItemStack(gamer.hasKit("Quickdrop") ? Material.AIR : Material.BOWL));
				event.getPlayer().updateInventory();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void createSoups() {
		ItemBuilder soup = new ItemBuilder().setMaterial(Material.MUSHROOM_SOUP);
		ShapelessRecipe cocoa = new ShapelessRecipe(soup.getStack());
		ShapelessRecipe cactus = new ShapelessRecipe(soup.getStack());
		ShapelessRecipe pumpkin = new ShapelessRecipe(soup.getStack());
		ShapelessRecipe melon = new ShapelessRecipe(soup.getStack());
		ShapelessRecipe flower = new ShapelessRecipe(soup.getStack());
		ShapelessRecipe nether = new ShapelessRecipe(soup.getStack());

		cocoa.addIngredient(Material.BOWL);
		cocoa.addIngredient(Material.INK_SACK, 3);

		cactus.addIngredient(Material.BOWL);
		cactus.addIngredient(2, Material.CACTUS);

		pumpkin.addIngredient(Material.BOWL);
		pumpkin.addIngredient(2, Material.PUMPKIN_SEEDS);

		melon.addIngredient(Material.BOWL);
		melon.addIngredient(2, Material.MELON_SEEDS);

		nether.addIngredient(Material.BOWL);
		nether.addIngredient(Material.getMaterial(372));
		
		flower.addIngredient(Material.BOWL);
		flower.addIngredient(Material.RED_ROSE);
		flower.addIngredient(Material.YELLOW_FLOWER);

		Bukkit.addRecipe(cocoa);
		Bukkit.addRecipe(cactus);
		Bukkit.addRecipe(pumpkin);
		Bukkit.addRecipe(melon);
		Bukkit.addRecipe(nether);
		Bukkit.addRecipe(flower);
	}

}
