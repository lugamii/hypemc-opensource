package br.com.weavenmc.ypvp.ability.list;

public class PickpocketAbility { //extends Ability {
//
//	private final List<UUID> open = new ArrayList<>();
//	private final List<Material> blocked_items;
//
//	public PickpocketAbility() {
//		blocked_items = new ArrayList<>();
//		setName("Pickpocket");
//		setHasItem(true);
//		setGroupToUse(Group.LIGHT);
//		setIcon(Material.BLAZE_ROD);
//		setDescription(new String[] { "§7Abra o inventário de seus inimigos", "§7e roube items dele." });
//		setPrice(80000);
//		setTempPrice(8700);
//		blocked_items.add(Material.DIAMOND_SWORD);
//		blocked_items.add(Material.IRON_SWORD);
//		blocked_items.add(Material.STONE_SWORD);
//		blocked_items.add(Material.GOLD_SWORD);
//		blocked_items.add(Material.WOOD_SWORD);
//	}
//
//	@Override
//	public void eject(Player p) {
//		if (open.contains(p.getUniqueId())) {
//			open.remove(p.getUniqueId());
//		}
//	}
//
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onInteract(PlayerInteractEntityEvent event) {
//		Player p = event.getPlayer();
//		if (hasKit(p)) {
//			if (event.getRightClicked() instanceof Player) {
//				if (isItem(p.getItemInHand())) {
//					if (!inCooldown(p)) {
//						addCooldown(p, 40);
//						Player t = (Player) event.getRightClicked();
//						p.openInventory(t.getInventory());
//						open.add(p.getUniqueId());
//						t.sendMessage("§5§lPICKPOCKET§f O jogador §9§l" + p.getName() + "§f abriu o seu inventário!");
//						p.sendMessage("§5§lPICKPOCKET§f Você tem §9§l1.5 SEGUNDOS§f para roubar os itens...");
//						new BukkitRunnable() {
//							@Override
//							public void run() {
//								if (open.contains(p.getUniqueId())) {
//									p.closeInventory();
//								}
//							}
//						}.runTaskLater(yPvP.getPlugin(), 30L);
//					} else {
//						sendCooldown(p);
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onClick(InventoryClickEvent event) {
//		if (event.getWhoClicked() instanceof Player) {
//			Player p = (Player) event.getWhoClicked();
//			if (event.getCurrentItem() != null) {
//				if (open.contains(p.getUniqueId())) {
//					if (blocked_items.contains(event.getCurrentItem().getType())) {
//						event.setCancelled(true);
//					} else {
//						for (Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
//							if (ability.getIcon() == null)
//								continue;
//							if (event.getCurrentItem().getType().equals(Material.BOWL))
//								continue;
//							if (!event.getCurrentItem().getType().equals(ability.getIcon()))
//								continue;
//							event.setCancelled(true);
//							return;
//						}
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onClose(InventoryCloseEvent event) {
//		if (event.getPlayer() instanceof Player) {
//			Player p = (Player) event.getPlayer();
//			if (open.contains(p.getUniqueId())) {
//				open.remove(p.getUniqueId());
//			}
//			p = null;
//		}
//	}
}
