package br.com.weavenmc.skywars.inventorys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.kit.KitAPI.Kits;

public class KitSelector {

	public static void openKitsMenu(Player p, int page) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§8Seus kit's");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		ItemBuilder itemBuilder = new ItemBuilder();
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (Kits kit : Kits.values()) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;

			for (String desc : kit.getDescription())
				lore.add("§7" + desc);
			lore.add("§7");

			if (bukkitPlayer.hasGroupPermission(kit.getGroup()) || hasKit(bukkitPlayer, kit)
					|| bukkitPlayer.hasGroupPermission(Group.BLADE)) {
				lore.add("§eClique para selecionar");
				itemStack = itemBuilder.type(kit.getDisplay()).name("§aKit " + kit.getName()).lore(lore).build();
			} else {
				lore.add("§cVocê não possuí esse kit!");
				itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§cKit " + kit.getName()).lore(lore).build();

			}
			lore.clear();
			inventory.setItem(slot, itemStack);


		}
		if (page != 1) {
			inventory.setItem(45, new ItemBuilder().name("§aPágina " + (page - 1)).type(Material.ARROW).build());
		}

		p.openInventory(inventory);

	}
	
	public static boolean hasKit(BukkitPlayer bukkitPlayer, Kits kit) {
		return bukkitPlayer.hasPermission("swkit." + kit.getName().replace(" ", "_"));
	}

}
