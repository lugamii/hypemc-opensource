package br.com.weavenmc.skywars.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.kit.KitAPI.Kits;

public class InventoryEvent implements Listener {

	@EventHandler
	public void inventoryclick(InventoryClickEvent event) {
		ItemStack currentItem = event.getCurrentItem();
		Entity whoClicked = event.getWhoClicked();
		Inventory inventory = event.getInventory();
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) whoClicked;
			if (currentItem == null) {
				return;
			}
			if (!currentItem.hasItemMeta()) {
				return;
			}
			if (inventory.getTitle().equals("§8Jogadores:")) {
				event.setCancelled(true);
				String name = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
				Player p2 = Bukkit.getPlayerExact(name);
				if (p2 == null) {
					player.sendMessage("§c§lERRO §7O jogador §c" + name + "§7 está offline.");
					return;
				}
				player.teleport(p2);
				player.sendMessage("§3§lTELEPORT §fVocê se teleportou para §e" + p2.getName());
			}
			if (inventory.getTitle().equals("§8Suas habilidades")) {
				event.setCancelled(true);
				if (currentItem.getType().equals(Material.CHEST)) {
					player.closeInventory();
					KitSelector.openKitsMenu(player, 1);
					return;
				}
				if (currentItem.getType().equals(Material.STAINED_GLASS_PANE)) {
					player.sendMessage(
							"§c§lHABILIDADE §fVocê não tem a habilidade " + currentItem.getItemMeta().getDisplayName());
					return;
				}
				if (!currentItem.getType().equals(Material.STAINED_GLASS_PANE)) {

					if (!currentItem.getType().equals(Material.STAINED_GLASS_PANE)) {
						for (Hability kit : Hability.values()) {
							if (kit.getDisplay() == currentItem.getType()) {

								player.sendMessage(
										"§aVocê selecionou a habilidade §e" + kit.getName() + " §acom sucesso!");
								player.closeInventory();
								WeavenSkywars.getGameManager().getHabilityAPI().setHabilidade(player, kit);

								break;

							}
						}
					}
				}
			}
			if (inventory.getTitle().equals("§8Seus kit's")) {
				event.setCancelled(true);

				if (currentItem.getType().equals(Material.STAINED_GLASS_PANE)) {
					player.sendMessage("§c§lKIT §fVocê não tem o kit " + currentItem.getItemMeta().getDisplayName());
					return;
				}
				if (!currentItem.getType().equals(Material.STAINED_GLASS_PANE)) {
					for (Kits kit : Kits.values()) {
						if (kit.getDisplay() == currentItem.getType()) {

							player.sendMessage("§aVocê selecionou o kit §e" + kit.getName() + " §acom sucesso!");
							player.closeInventory();
							WeavenSkywars.getGameManager().getKitAPI().setKit(player, kit);

							break;

						}
					}
				}
			}
		}
	}

}
