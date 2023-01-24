package br.com.weavenmc.skywars.inventorys;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.player.PlayerController;

public class SpectatorInventory {

	public static void openInventory(Player player) {
		int slot = 9;
		Inventory inventory = Bukkit.createInventory(player, 54, "§8Jogadores:");
		for (Player players : Bukkit.getOnlinePlayers()) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta im = (SkullMeta) is.getItemMeta();
			if ((player != players) && (GameController.player.contains(players.getUniqueId()))) {
				im.setDisplayName("§a" + players.getName());
				ArrayList<String> lore = new ArrayList<>();
				lore.add("");
				lore.add("§7Kills: §b" + PlayerController.kills.get(players.getUniqueId()));
				lore.add("§7Kit: §b" + WeavenSkywars.getGameManager().getKitAPI().getKit(players));
				lore.add("§7Habilidade: §b" + WeavenSkywars.getGameManager().getHabilityAPI().name(players));
				lore.add("");
				lore.add("§eClique para teleportar!");
				lore.add("");
				im.setLore(lore);
				im.setOwner(players.getName());
				is.setItemMeta(im);

				inventory.setItem(slot, is);

				slot++;
				lore.clear();
			}
		}
		player.openInventory(inventory);
	}

}
