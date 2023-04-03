package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Forger extends Kit {

	public Forger(Manager manager) {
		super(manager);
		
		setPrice(43000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.COAL));
		setFree(false);
		setDescription("Seja um ferreiro e forge seus minérios rapidamente.");
		setRecent(false);
		setItems(new ItemStack(Material.COAL, 5));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack currentItem = event.getCurrentItem();
		if (currentItem != null && currentItem.getType() != Material.AIR) {
			if (hasKit((Player) event.getWhoClicked())) {
				int coalAmount = 0;
				Inventory inv = event.getView().getBottomInventory();
				for (ItemStack item : inv.getContents()) {
					if (item != null && item.getType() == Material.COAL)
						coalAmount += item.getAmount();
				}
				if (coalAmount == 0)
					return;
				int hadCoal = coalAmount;
				if (currentItem.getType() == Material.COAL) {
					for (int slot = 0; slot < inv.getSize(); slot++) {
						ItemStack item = inv.getItem(slot);
						if (item != null && item.getType().name().contains("ORE")) {
							while (item.getAmount() > 0 && coalAmount > 0 && (item.getType() == Material.IRON_ORE || item.getType() == Material.GOLD_ORE)) {
								item.setAmount(item.getAmount() - 1);
								coalAmount--;
								if (item.getType() == Material.IRON_ORE)
									event.getWhoClicked().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
								else if (item.getType() == Material.GOLD_ORE)
									event.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
							}
							if (item.getAmount() == 0)
								inv.setItem(slot, new ItemStack(0));
						}
					}
				} else if (currentItem.getType().name().contains("ORE")) {
					while (currentItem.getAmount() > 0 && coalAmount > 0 && (currentItem.getType() == Material.IRON_ORE || currentItem.getType() == Material.GOLD_ORE)) {
						currentItem.setAmount(currentItem.getAmount() - 1);
						coalAmount--;
						if (currentItem.getType() == Material.IRON_ORE)
							event.getWhoClicked().getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
						else if (currentItem.getType() == Material.GOLD_ORE)
							event.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
					}
					if (currentItem.getAmount() == 0)
						event.setCurrentItem(new ItemStack(0));
				}
				if (coalAmount != hadCoal)
					for (int slot = 0; slot < inv.getSize(); slot++) {
						ItemStack item = inv.getItem(slot);
						if (item != null && item.getType() == Material.COAL) {
							while (coalAmount < hadCoal && item.getAmount() > 0) {
								item.setAmount(item.getAmount() - 1);
								coalAmount++;
							}
							if (item.getAmount() == 0)
								inv.setItem(slot, new ItemStack(0));
						}
					}
			}
		}
	}
}
