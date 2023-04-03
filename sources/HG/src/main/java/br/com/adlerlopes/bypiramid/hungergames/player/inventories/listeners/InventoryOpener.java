package br.com.adlerlopes.bypiramid.hungergames.player.inventories.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.game.handler.item.CacheItems;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;

public class InventoryOpener extends HungerListener {
	private static final ItemBuilder itemBuilder;
//	public static ArrayList<Player> heatingPvP;

	static {
		itemBuilder = new ItemBuilder(Material.AIR);
//		heatingPvP = new ArrayList<>();
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}
		if (this.getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
			if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			event.setCancelled(true);
			final Player player = event.getPlayer();
			if (InventoryOpener.itemBuilder.checkItem(event.getItem(),
					CacheItems.JOIN_ONEKIT.getItem(0).getStack().getItemMeta().getDisplayName())) {
				openKitsMenu(player, 1, 1);
			}
			if (InventoryOpener.itemBuilder.checkItem(event.getItem(),
					CacheItems.JOIN_SECKIT.getItem(1).getStack().getItemMeta().getDisplayName())) {
				openKitsMenu(player, 1, 2);
			}
			if (InventoryOpener.itemBuilder.checkItem(event.getItem(),
					CacheItems.JOIN_SECKIT.getItem(3).getStack().getItemMeta().getDisplayName())) {
				final Gamer gamer = this.getManager().getGamerManager().getGamer(player);
				if (player.hasPermission("hgkit2.*")) {
					gamer.sendMessage("§6§lKIT DA PARTIDA §fVocê já usou o kit dessa partida!");
					return;
				}
				if (this.getManager().getSurpriseKitManager().suject.contains(player)) {
					gamer.sendMessage("§6§lKIT DA PARTIDA §fVocê já usou o kit dessa partida!");
					return;
				}
				if (gamer.getSurpriseKit() == null) {
					this.getManager().getSurpriseKitManager().generate(player);
				} else {
					gamer.sendMessage("§6§lKIT DA PARTIDA §fVocê já usou o kit dessa partida!");
				}
			}
			// loja
			if (InventoryOpener.itemBuilder.checkItem(event.getItem(),
					CacheItems.JOIN_SECKIT.getItem(2).getStack().getItemMeta().getDisplayName())) {
				openStoreMenu(player, 1);
			}
			// aquece
//			if (InventoryOpener.itemBuilder.checkItem(event.getItem(),
//					CacheItems.JOIN_SECKIT.getItem(4).getStack().getItemMeta().getDisplayName())) {
//
//				if (HungerGames.getManager().getGameManager().getTimer().getTime() <= 30) {
//					player.sendMessage("§c§lAQUECIMENTO§f Você não pode entrar no aquecimento agora!");
//					return;
//				}
//
////				heatingPvP.add(player);
//				final Gamer gamer = this.getManager().getGamerManager().getGamer(player);
//				gamer.setPvpPregame(true);
//				player.getInventory().clear();
//				for (int i = 0, lenght = player.getInventory().getSize(); i < lenght; i++) {
//					player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
//				} // 31.32.33
//
//				player.getInventory().setItem(13, new ItemStack(Material.BOWL, 32));
//				player.getInventory().setItem(14, new ItemStack(Material.BROWN_MUSHROOM, 32));
//				player.getInventory().setItem(15, new ItemStack(Material.RED_MUSHROOM, 32));
//				player.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
//				Location l = HungerGames.getSpawn().clone();
//				l.setY(l.getY() - 8);
//				player.teleport(l);
//			}
		}
	}

	public void openKitsMenu(Player p, int page, int index) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§7Kits " + index);
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		br.com.weavenmc.commons.bukkit.api.item.ItemBuilder ItemBuilder = new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder();
		Kit ability;
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (int initial = (page == 1 ? 1 : (page == 2 ? 22 : 22 * page)), max = 22 * page; initial < max; initial++) {
			ability = HungerGames.getManager().getKitManager().getKits().get(initial);
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (ability == null) 
				break;
			String description = ability.getDescription();
			if (description.length() > 18) {
				int dif = description.length() - 18;
				lore.add("§7" + description.substring(0, dif));
				lore.add("§7" + description.substring(dif));
			} else {
				lore.add("§7" + description);
			}
			lore.add("§7");

			if (bukkitPlayer.hasGroupPermission(Group.BLADE) || hasKit(ability, p)) {
				lore.add("§eClique para selecionar");
				itemStack = ItemBuilder.type(ability.getIcon().getType()).name("§aKit " + ability.getName()).lore(lore)
						.build();
			} else {
				lore.add("§cVocê não possuí esse kit!");
				itemStack = ItemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§cKit " + ability.getName()).lore(lore).build();

			}
			lore.clear();
			inventory.setItem(slot, itemStack);
			if (slot >= 34) {
				if (page == 1) {
					if (HungerGames.getManager().getKitManager().getKits().get(max) != null) {
						inventory.setItem(53, ItemBuilder.name("§aPágina " + (page + 1)).type(Material.ARROW).build());
					}
				}
				break;
			}

		}
		if (page != 1) {
			inventory.setItem(45, new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder()
					.name("§aPágina " + (page - 1)).type(Material.ARROW).build());
		}
		Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(p.getUniqueId());
		Kit kit = index == 1 ? gamer.getKit() : gamer.getKit2();
		inventory.setItem(49,
				new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder().name("§aSeu kit: §e" + kit.getName())
						.type(kit.getIcon().getType() == Material.GLASS ? Material.ITEM_FRAME : kit.getIcon().getType()).build());
		inventory.setItem(50, new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder().name("§aExibindo de: §eA-Z")
				.type(Material.SLIME_BALL).build());
		p.openInventory(inventory);
	}

	public void openStoreMenu(Player p, int page) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§7Loja de Kits");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		br.com.weavenmc.commons.bukkit.api.item.ItemBuilder ItemBuilder = new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder();
		Kit ability;
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (int initial = (page == 1 ? 1 : (page == 2 ? 22 : 22 * page)), max = 22 * page; initial < max; initial++) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			ability = HungerGames.getManager().getKitManager().getKits().get(initial);
			if (ability == null) 
				break;

			String description = ability.getDescription();
			if (description.length() > 18) {
				int dif = description.length() - 18;
				lore.add("§7" + description.substring(0, dif));
				lore.add("§7" + description.substring(dif));
			} else {
				lore.add("§7" + description);
			}
			lore.add("§7");

			if (bukkitPlayer.hasGroupPermission(Group.BLADE) || hasKit(ability, p)) {
				lore.add("§eClique para selecionar");
				itemStack = ItemBuilder.type(ability.getIcon().getType()).name("§aKit " + ability.getName()).lore(lore)
						.build();
			} else {
				lore.add("§cVocê não possuí esse kit!");
				itemStack = ItemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§cKit " + ability.getName()).lore(lore).build();

			}
			lore.clear();
			inventory.setItem(slot, itemStack);
			if (slot >= 34) {
				if (page == 1) {
					if (HungerGames.getManager().getKitManager().getKits().get(max) != null) {
						inventory.setItem(53, ItemBuilder.name("§aPágina " + (page + 1)).type(Material.ARROW).build());
					}
				}
				break;
			}

		}
		if (page != 1) {
			inventory.setItem(45, new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder()
					.name("§aPágina " + (page - 1)).type(Material.ARROW).build());
		}
		inventory.setItem(49, new br.com.weavenmc.commons.bukkit.api.item.ItemBuilder()
				.name("§7Suas coins: §6" + bukkitPlayer.getMoney()).type(Material.EMERALD).build());

		p.openInventory(inventory);
	}

	public boolean hasKit(final Kit kit, final Player player) {
		if (kit.isFree()) {
			return true;
		}
		if (player.hasPermission("hgkit." + kit.getName().toLowerCase())
				|| player.hasPermission("hgkit2." + kit.getName().toLowerCase())) {
			return true;
		}
		final BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		return bP.getGroup().getId() >= Group.VIP.getId()
				|| this.getManager().getGamerManager().getGamer(player).isWinner()
				|| bP.getPermissions().contains("hgkit." + kit.getName().toLowerCase());
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}
		if (event.getCurrentItem().getItemMeta() == null) {
			return;
		}
		ItemStack current = event.getCurrentItem();
		Player p = (Player) event.getWhoClicked();
		if (p == null)
			return;
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());

		if (event.getInventory().getName().startsWith("§7Kits 2")) {
			event.setCancelled(true);
			if (current.getType() == Material.ARROW) {
				p.closeInventory();
				openKitsMenu(p, Integer.valueOf(current.getItemMeta().getDisplayName().replace("§aPágina ", "")), 2);
				return;
			}

			Kit kit = this.getManager().getKitManager()
					.getKit(event.getCurrentItem().getItemMeta().getDisplayName().replace("§aKit ", "").replace("§cKit ", ""));

			p.closeInventory();
			if (kit != null)
				p.chat("/kit2 " + kit.getName());
		}

		if (event.getInventory().getName().startsWith("§7Kits 1")) {
			event.setCancelled(true);
			if (current.getType() == Material.ARROW) {
				p.closeInventory();
				openKitsMenu(p, Integer.valueOf(current.getItemMeta().getDisplayName().replace("§aPágina ", "")), 1);
				return;
			}

			Kit kit = this.getManager().getKitManager()
					.getKit(event.getCurrentItem().getItemMeta().getDisplayName().replace("§aKit ", "").replace("§cKit ", ""));
			if (kit == null) {
				return;
			}
			p.closeInventory();
			p.chat("/kit " + kit.getName());
		}

		if (event.getInventory().getName().startsWith("§7Loja de Kits")) {
			event.setCancelled(true);
			if (current.getType() == Material.ARROW) {
				p.closeInventory();
				openStoreMenu(p, Integer.valueOf(current.getItemMeta().getDisplayName().replace("§aPágina ", "")));
				return;
			}

			for (Kit ability : HungerGames.getManager().getKitManager().getKits()) {
				if (current.getType().equals(ability.getIcon().getType())) {
					if (bP.getMoney() >= ability.getPrice()) {
						p.closeInventory();
						bP.removeMoney(ability.getPrice());
						bP.addPermission("hgkit." + ability.getName().toLowerCase(), -1);
						bP.addPermission("hgkit2." + ability.getName().toLowerCase(), -1);
						bP.save(DataCategory.ACCOUNT, DataCategory.BALANCE);
						p.sendMessage("§b§lSHOP§f Parabéns! Você §3§lCOMPROU§f a habilidade §b§l"
								+ ability.getName().toUpperCase() + "§f com duração §3§lETERNA!");
					} else {
						p.closeInventory();
						p.sendMessage("§b§lSHOP§f Você precisa de mais §b§l" + (ability.getPrice() - bP.getMoney())
								+ " MOEDAS§f para §3§lCOMPRAR§f a habilidade §b§l" + ability.getName().toUpperCase());
					}

					return;
				}
			}
		}
	}

}
