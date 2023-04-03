package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Specialist extends Kit {

	public Specialist(Manager manager) {
		super(manager);

		this.setActive(false);
		setPrice(49000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.ENCHANTMENT_TABLE));
		setFree(false);
		setDescription("Tenha a habilidade de encantar seus itens.");
		setRecent(false);
		setItems(createItemStack("§aSpecialist", Material.BOOK));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerEnchantItem(EnchantItemEvent event) {
		event.setCancelled(false);
		event.setExpLevelCost(1);
	}

	@EventHandler
	public void interactOnBook(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (hasKit(player) && event.getItem().getType() == Material.BOOK)
			player.openInventory(Bukkit.createInventory(null, InventoryType.ENCHANTING));
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if (player == null) {
			return;
		}
		if (hasKit(player)) {
			player.setLevel(player.getLevel() + 1);
		}
	}

	@EventHandler
	public void expBottleEvent(ExpBottleEvent event) {
		event.setExperience(event.getExperience() * 2 + 5);
	}

}
