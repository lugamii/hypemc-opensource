package br.com.adlerlopes.bypiramid.hungergames.player.inventories.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.inventories.CustomInventory;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.Utils;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;

public class KitShopHandler extends CustomInventory {
	public static final HashMap<UUID, Integer> inventoryIndex;
	private HashMap<UUID, Integer> last;

	static {
		inventoryIndex = new HashMap<UUID, Integer>();
	}

	public KitShopHandler(final Manager manager) {
		super(manager);
		this.last = new HashMap<UUID, Integer>();
	}

	public void generate(final Player player, final KitType mode, final int index) {
		this.setTitle("Loja de Kits");

		final Inventory inventory = Bukkit.createInventory((InventoryHolder) player, 54, this.getTitle());
		this.update(player, mode, inventory, index);
		player.openInventory(inventory);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(final Player player, final KitType mode, final Inventory menu, final int index) {
		KitShopHandler.inventoryIndex.put(player.getUniqueId(), index);
		menu.clear();
		ItemBuilder builder = null;
		if (index == 1) {
			this.last.put(player.getUniqueId(), 0);
		}
		if (index == 1) {
			builder = new ItemBuilder().type(Material.ARROW).name("§7Pagina anterior");
			menu.setItem(45, builder.build());
			builder = new ItemBuilder().type(Material.ARROW).name("§7Pr\u00f3xima p\u00e1gina");
			menu.setItem(53, builder.build());
		} else {
			builder = new ItemBuilder().type(Material.ARROW).name("§7Pagina anterior");
			menu.setItem(45, builder.build());
			builder = new ItemBuilder().type(Material.ARROW).name("§7Pr\u00f3xima p\u00e1gina");
			menu.setItem(53, builder.build());
		}
		if (index == 1) {
			int i = 10;
			int size = 0;
			while (this.getManager().getKitManager().getKitBySize(size) != null) {
				final Kit kit = this.getManager().getKitManager().getKitBySize(size);
				if (i == 17) {
					i = 19;
				}
				if (i == 26) {
					i = 28;
				}
				if (i == 26) {
					i = 28;
				}
				if (i == 35) {
					i = 37;
				}
				if (kit.getName().equals("Nenhum")) {
					++size;
					this.last.put(player.getUniqueId(), size);
				} else if (!kit.isActive()) {
					++size;
					this.last.put(player.getUniqueId(), size);
				} else {
					if (i > 43) {
						break;
					}
					final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
					final List<String> list = new ArrayList<String>();
					if (has) {
						list.add("§aVoc\u00ea possui este kit.");
					} else {
						list.add("§cVoc\u00ea n\u00e3o possui este kit.");
					}
					list.add("");
					for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
						list.add("§7" + lore);
					}
					list.add("§fPreço §b§l" + kit.getPrice() + "§f!");
					builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
							.lore((List) list);
					menu.setItem(i, builder.build());
					++size;
					this.last.put(player.getUniqueId(), size);
					++i;
				}
			}
		} else {
			Integer size2 = this.last.get(player.getUniqueId());
			int j = 10;
			if (size2 != null) {
				while (this.getManager().getKitManager().getKitBySize(size2) != null) {
					Kit kit = this.getManager().getKitManager().getKitBySize(size2);

					if (j == 17) {
						j = 19;
					}
					if (j == 26) {
						j = 28;
					}
					if (j == 26) {
						j = 28;
					}
					if (j == 35) {
						j = 37;
					}
					if (kit.getName().equals("Nenhum")) {
						++size2;
					} else if (!kit.isActive()) {
						++size2;
					} else {
						if (j > 43) {
							break;
						}
						final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
						final List<String> list = new ArrayList<String>();
						if (has) {
							list.add("§aVoc\u00ea possui este kit.");
						} else {
							list.add("§cVoc\u00ea n\u00e3o possui este kit.");
						}
						list.add("");
						for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
							list.add("§7" + lore);
						}
						list.add("§fPreço §b§l" + kit.getPrice() + "§f!");
						builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
								.lore((List) list);
						++size2;
						menu.setItem(j, builder.build());
						++j;
					}

				}
			} else {
				size2 = 29;
				while (this.getManager().getKitManager().getKitBySize(size2) != null) {
					Kit kit = this.getManager().getKitManager().getKitBySize(size2);
					if (j == 17) {
						j = 19;
					}
					if (j == 26) {
						j = 28;
					}
					if (j == 26) {
						j = 28;
					}
					if (j == 35) {
						j = 37;
					}
					if (size2 >= 46) {
						break;
					}
					if (kit.getName().equals("Nenhum")) {
						++size2;
					} else if (!kit.isActive()) {
						++size2;
					} else {
						if (j > 43) {
							break;
						}
						final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
						final List<String> list = new ArrayList<String>();
						if (has) {
							list.add("§aVoc\u00ea possui este kit.");
						} else {
							list.add("§cVoc\u00ea n\u00e3o possui este kit.");
						}
						list.add("");
						for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
							list.add("§7" + lore);
						}
						list.add("§fPreço §b§l" + kit.getPrice() + "§f!");
						builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
								.lore((List) list);
						++size2;
						menu.setItem(j, builder.build());
						++j;
					}
				}
			}
		}
		player.playSound(player.getLocation(), Sound.CLICK, 3.0f, 3.0f);
		player.updateInventory();
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
	public void onClose(final InventoryCloseEvent event) {
		if (KitShopHandler.inventoryIndex.containsKey(event.getPlayer().getUniqueId())) {
			KitShopHandler.inventoryIndex.remove(event.getPlayer().getUniqueId());
		}
	}

	public int[] range(final int start, final int stop) {
		final int[] result = new int[stop - start];
		for (int i = 0; i < stop - start; ++i) {
			result[i] = start + i;
		}
		return result;
	}

	public enum KitType {
		YOUR_KITS("YOUR_KITS", 0), SECONDARY_KITS("SECONDARY_KITS", 1);

		private KitType(final String s, final int n) {
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		// TODO Auto-generated method stub
		
	}
}
