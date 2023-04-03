package br.com.adlerlopes.bypiramid.hungergames.game.handler.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;

/**
 * Copyright (C) LittleMC, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public enum CacheItems {
	SPEC(new ItemBuilder[] { new ItemBuilder(Material.COMPASS).setName("§6Lista de Players §7(Clique para utilizar)"),
			new ItemBuilder(Material.DIAMOND_SWORD).setName("§aJogadores em PvP §7(Clique para utilizar)"), },
			new Integer[] { 2, 6 }),
	JOIN_ONEKIT(
			new ItemBuilder[] { new ItemBuilder(Material.CHEST).setName("§eSeletor de Kits §7(Clique para utilizar)"),
					new ItemBuilder(Material.ENDER_CHEST).setName("§eKit da partida §7(Clique para utilizar)"),
					new ItemBuilder(Material.STORAGE_MINECART).setName("§eLoja de Kits §7(Clique para utilizar)")},
			new Integer[] { 0, 1,4 }),
	JOIN_SECKIT(
			new ItemBuilder[] { new ItemBuilder(Material.CHEST).setName("§eSeletor de Kits §7(Clique para utilizar)"),
					new ItemBuilder(Material.CHEST).setName("§eSeletor de Kits 2 §7(Clique para utilizar)"),
					new ItemBuilder(Material.EMERALD).setName("§eLoja de Kits §7(Clique para utilizar)"),
					new ItemBuilder(Material.STORAGE_MINECART).setName("§eKit da partida §7(Clique para utilizar)")},

			new Integer[] { 0, 1, 2, 4 });

	private ItemBuilder[] items;
	private Integer[] slots;

	CacheItems(ItemBuilder[] items, Integer[] slots) {
		this.items = items;
		this.slots = slots;
	}

	public ItemBuilder getItem(int id) {
		return id <= items.length - 1 ? items[id] : items[0];
	}

	public Integer[] getSlots() {
		return slots;
	}

	public ItemBuilder[] getItems() {
		return items;
	}

	public void build(Inventory inventory) {
		if (slots != null) {
			int id = 0;
			for (Integer slot : slots) {
				getItem(id).build(inventory, slot);
				id++;
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				getItem(i).build(inventory);
			}
		}
	}

	public void build(Player player) {
		if (slots != null) {
			int id = 0;
			for (Integer slot : slots) {
				if (getItem(id).getStack().getType() == Material.SKULL_ITEM)
					getItem(id).setSkull(player.getName());
				getItem(id).build(player.getInventory(), slot);
				id++;
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				if (getItem(i).getStack().getType() == Material.SKULL_ITEM)
					getItem(i).setSkull(player.getName());
				getItem(i).build(player.getInventory());
			}
		}
	}
}
