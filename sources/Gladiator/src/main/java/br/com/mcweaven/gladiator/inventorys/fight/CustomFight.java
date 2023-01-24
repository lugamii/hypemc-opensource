package br.com.mcweaven.gladiator.inventorys.fight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;

public class CustomFight {

	public CustomFight(Player player, Player challenged) {

		Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§eDesafio Customizado");

		ItemBuilder builder;
		
		builder = new ItemBuilder().type(Material.DIAMOND_SWORD).name("§eEspada: §6Diamante").lore("", "§6Diamante",
				"§7Ferro", "§7Madeira", "");
		
		inventory.setItem(11, builder.build());
		
		builder = new ItemBuilder().type(Material.IRON_HELMET).name("§eArmadura: §6Full-Iron").lore("", "§7Diamante",
				"§aFull-Iron", "§7Couro", "");
		
		inventory.setItem(13, builder.build());
		
		builder = new ItemBuilder().type(Material.COCOA).name("§eRecraft: §6Cocoa").lore("", "§6Cocoa",
				"§7Cogumelo", "");
		
		inventory.setItem(15, builder.build());
		
		builder = new ItemBuilder().type(Material.ARROW).name("§aDesafiar").lore("", " §fDesafie §6§l" + challenged.getName() + "§f!", "");
		
		inventory.setItem(26, builder.build());
		
		builder = null;
		
	}

}
