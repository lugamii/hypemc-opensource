package br.com.adlerlopes.bypiramid.hungergames.player.inventories.inventory;

public class KitSelectorHandler {//extends CustomInventory {
//	public static final HashMap<UUID, Integer> inventoryIndex;
//	private HashMap<UUID, Integer> last;
//
//	static {
//		inventoryIndex = new HashMap<UUID, Integer>();
//	}
//
//	public KitSelectorHandler(final Manager manager) {
//		super(manager);
//		this.last = new HashMap<UUID, Integer>();
//	}
//
//	public void generate(final Player player, final KitType mode, final int index) {
//		if (mode.equals(KitType.YOUR_KITS)) {
//			this.setTitle("Selecione seu kit");
//		} else if (mode.equals(KitType.SECONDARY_KITS)) {
//			this.setTitle("	 ");
//		}
//		final Inventory inventory = Bukkit.createInventory((InventoryHolder) player, 54, this.getTitle());
//		this.update(player, mode, inventory, index);
//		player.openInventory(inventory);
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void update(final Player player, final KitType mode, final Inventory menu, final int index) {
//		KitSelectorHandler.inventoryIndex.put(player.getUniqueId(), index);
//		menu.clear();
//		ItemBuilder builder = null;
//		if (index == 1) {
//			this.last.put(player.getUniqueId(), 0);
//		}
//		if (index == 1) {
//			builder = new ItemBuilder().type(Material.ARROW).name("§7Pagina anterior");
//			menu.setItem(45, builder.build());
//			builder = new ItemBuilder().type(Material.ARROW).name("§7Pr\u00f3xima p\u00e1gina");
//			menu.setItem(53, builder.build());
//		} else {
//			builder = new ItemBuilder().type(Material.ARROW).name("§7Pagina anterior");
//			menu.setItem(45, builder.build());
//			builder = new ItemBuilder().type(Material.ARROW).name("§7Pr\u00f3xima p\u00e1gina");
//			menu.setItem(53, builder.build());
//		}
//		if (index == 1) {
//			int i = 10;
//			int size = 0;
//			while (this.getManager().getKitManager().getKitBySize(size) != null) {
//				final Kit kit = this.getManager().getKitManager().getKitBySize(size);
//				if (i == 17) {
//					i = 19;
//				}
//				if (i == 26) {
//					i = 28;
//				}
//				if (i == 26) {
//					i = 28;
//				}
//				if (i == 35) {
//					i = 37;
//				}
//				if (kit.getName().equals("Nenhum")) {
//					++size;
//					this.last.put(player.getUniqueId(), size);
//				} else if (!kit.isActive()) {
//					++size;
//					this.last.put(player.getUniqueId(), size);
//				} else {
//					if (i > 43) {
//						break;
//					}
//					final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
//					final List<String> list = new ArrayList<String>();
//					if (has) {
//						list.add("§aVoc\u00ea possui este kit.");
//					} else {
//						list.add("§cVoc\u00ea n\u00e3o possui este kit.");
//					}
//					list.add("");
//					for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
//						list.add("§7" + lore);
//					}
//					builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
//							.lore((List) list);
//					menu.setItem(i, builder.build());
//					++size;
//					this.last.put(player.getUniqueId(), size);
//					++i;
//				}
//			}
//		} else {
//			Integer size2 = this.last.get(player.getUniqueId());
//			int j = 10;
//			if (size2 != null) {
//				while (this.getManager().getKitManager().getKitBySize(size2) != null) {
//					Kit kit = this.getManager().getKitManager().getKitBySize(size2);
//
//					if (j == 17) {
//						j = 19;
//					}
//					if (j == 26) {
//						j = 28;
//					}
//					if (j == 26) {
//						j = 28;
//					}
//					if (j == 35) {
//						break;
//					}
//					if (kit.getName().equals("Nenhum")) {
//						++size2;
//					} else if (!kit.isActive()) {
//						++size2;
//					} else {
//						if (j > 43) {
//							break;
//						}
//						final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
//						final List<String> list = new ArrayList<String>();
//						if (has) {
//							list.add("§aVoc\u00ea possui este kit.");
//						} else {
//							list.add("§cVoc\u00ea n\u00e3o possui este kit.");
//						}
//						list.add("");
//						for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
//							list.add("§7" + lore);
//						}
//						builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
//								.lore((List) list);
//						++size2;
//						menu.setItem(j, builder.build());
//						++j;
//					}
//					
//				}
//			} else {
//				size2 = 29;
//				while (this.getManager().getKitManager().getKitBySize(size2) != null) {
//					Kit kit = this.getManager().getKitManager().getKitBySize(size2);
//					if (j == 17) {
//						j = 19;
//					}
//					if (j == 26) {
//						j = 28;
//					}
//					if (j == 26) {
//						j = 28;
//					}
//					if (j == 35) {
//						j = 37;
//					}
//					if (size2 >= 46) {
//						break;
//					}
//					if (kit.getName().equals("Nenhum")) {
//						++size2;
//					} else if (!kit.isActive()) {
//						++size2;
//					} else {
//						if (j > 43) {
//							break;
//						}
//						final boolean has = mode == KitType.YOUR_KITS || this.hasKit(kit, player);
//						final List<String> list = new ArrayList<String>();
//						if (has) {
//							list.add("§aVoc\u00ea possui este kit.");
//						} else {
//							list.add("§cVoc\u00ea n\u00e3o possui este kit.");
//						}
//						list.add("");
//						for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
//							list.add("§7" + lore);
//						}
//						builder = new ItemBuilder().type(kit.getIcon().getType()).name("§e§l" + kit.getName())
//								.lore((List) list);
//						++size2;
//						menu.setItem(j, builder.build());
//						++j;
//					}
//				}
//			}
//		}
//		player.playSound(player.getLocation(), Sound.CLICK, 5.0f, 5.0f);
//		player.updateInventory();
//	}
//
//	public boolean hasKit(final Kit kit, final Player player) {
//		if (kit.isFree()) {
//			return true;
//		}
//		if (player.hasPermission("hgkit." + kit.getName().toLowerCase())
//				|| player.hasPermission("hgkit2." + kit.getName().toLowerCase())) {
//			return true;
//		}
//		final BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
//		return bP.getGroup().getId() >= Group.LIGHT.getId()
//				|| this.getManager().getGamerManager().getGamer(player).isWinner()
//				|| bP.getPermissions().contains("hgkit." + kit.getName().toLowerCase());
//	}
//
//	@EventHandler
//	@Override
//	public void onClick(final InventoryClickEvent event) {
//		final Player player = (Player) event.getWhoClicked();
//		if (event.getClickedInventory() != null) {
//			if (event.getClickedInventory().getTitle().equalsIgnoreCase("Selecione seu kit")) {
//				event.setCancelled(true);
//				if (event.getCurrentItem() != null) {
//					if (!KitSelectorHandler.inventoryIndex.containsKey(player.getUniqueId())) {
//						KitSelectorHandler.inventoryIndex.put(player.getUniqueId(), 1);
//					}
//					final Material type = event.getCurrentItem().getType();
//					if (type == Material.AIR) {
//						return;
//					}
//					if (!type.equals((Object) Material.INK_SACK) && !type.equals((Object) Material.CHEST)
//							&& !type.equals((Object) Material.DIAMOND) && !type.equals((Object) Material.ENDER_CHEST)
//							&& !type.equals((Object) Material.STAINED_GLASS_PANE)) {
//						final Kit kit = this.getManager().getKitManager().getKit(event.getCurrentItem().getItemMeta()
//								.getDisplayName().replace("§e§l", "").replace("§c", ""));
//						player.chat("/kit " + kit.getName());
//						player.closeInventory();
//					} else if (type.equals((Object) Material.INK_SACK)) {
//						final short data = event.getCurrentItem().getDurability();
//						if (data == 10) {
//							if (event.getCurrentItem().getItemMeta().getDisplayName()
//									.equalsIgnoreCase("§7Pagina anterior")) {
//								this.generate(player, KitType.YOUR_KITS, 1);
//							} else if (event.getCurrentItem().getItemMeta().getDisplayName()
//									.equalsIgnoreCase("§7Pr\u00f3xima p\u00e1gina")) {
//								this.generate(player, KitType.YOUR_KITS, 2);
//							}
//						}
//					}
//				}
//			} else if (event.getClickedInventory().getTitle().equalsIgnoreCase("Selecione seu kit 2")) {
//				event.setCancelled(true);
//				if (event.getCurrentItem() != null) {
//					if (!KitSelectorHandler.inventoryIndex.containsKey(player.getUniqueId())) {
//						KitSelectorHandler.inventoryIndex.put(player.getUniqueId(), 1);
//					}
//					final Material type = event.getCurrentItem().getType();
//					if (type == Material.AIR) {
//						return;
//					}
//					if (!type.equals((Object) Material.INK_SACK) && !type.equals((Object) Material.CHEST)
//							&& !type.equals((Object) Material.DIAMOND) && !type.equals((Object) Material.ENDER_CHEST)
//							&& !type.equals((Object) Material.STAINED_GLASS_PANE)) {
//						final Kit kit = this.getManager().getKitManager().getKit(event.getCurrentItem().getItemMeta()
//								.getDisplayName().replace("§e§l", "").replace("§c", ""));
//						player.chat("/kit2 " + kit.getName());
//						player.closeInventory();
//					} else if (type.equals((Object) Material.INK_SACK)) {
//						final short data = event.getCurrentItem().getDurability();
//						if (data == 10) {
//							if (event.getCurrentItem().getItemMeta().getDisplayName()
//									.equalsIgnoreCase("§7Pagina anterior")) {
//								this.generate(player, KitType.SECONDARY_KITS, 1);
//							} else if (event.getCurrentItem().getItemMeta().getDisplayName()
//									.equalsIgnoreCase("§7Pr\u00f3xima p\u00e1gina")) {
//								this.generate(player, KitType.SECONDARY_KITS, 2);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	public void onClose(final InventoryCloseEvent event) {
//		if (KitSelectorHandler.inventoryIndex.containsKey(event.getPlayer().getUniqueId())) {
//			KitSelectorHandler.inventoryIndex.remove(event.getPlayer().getUniqueId());
//		}
//	}
//
//	public int[] range(final int start, final int stop) {
//		final int[] result = new int[stop - start];
//		for (int i = 0; i < stop - start; ++i) {
//			result[i] = start + i;
//		}
//		return result;
//	}
//
//	public enum KitType {
//		YOUR_KITS("YOUR_KITS", 0), SECONDARY_KITS("SECONDARY_KITS", 1);
//
//		private KitType(final String s, final int n) {
//		}
//	}
}
