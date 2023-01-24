package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class PhantomAbility extends Ability {

	private ArrayList<UUID> flying = new ArrayList<>();
	private HashMap<UUID, ItemStack[]> previous = new HashMap<>();

	public PhantomAbility() {
		setName("Phantom");
		setHasItem(true);
		setGroupToUse(Group.VIP);
		setIcon(Material.FEATHER);
		setDescription(new String[] { "§7Tenha a habilidade de voar." });
		setPrice(60000);
		setTempPrice(3500);
	}

	@Override
	public void eject(Player p) {
		if (flying.contains(p.getUniqueId())) {
			flying.contains(p.getUniqueId());
		}
		if (previous.containsKey(p.getUniqueId())) {
			previous.remove(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		eject(event.getPlayer());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand())) {
				if (event.getAction().name().contains("RIGHT")) {
					event.setCancelled(true);
					if (!flying.contains(p.getUniqueId())) {
						if (!inCooldown(p)) {
							addCooldown(p, 25);
							
							previous.put(p.getUniqueId(), p.getInventory().getArmorContents());
							flying.add(p.getUniqueId());

							p.setAllowFlight(true);
							p.setFlying(true);

							ItemBuilder builder = new ItemBuilder().type(Material.LEATHER_HELMET).color(Color.AQUA);
							p.getInventory().setHelmet(builder.build());
							builder = new ItemBuilder().type(Material.LEATHER_CHESTPLATE).color(Color.AQUA);
							p.getInventory().setChestplate(builder.build());
							builder = new ItemBuilder().type(Material.LEATHER_LEGGINGS).color(Color.AQUA);
							p.getInventory().setLeggings(builder.build());
							builder = new ItemBuilder().type(Material.LEATHER_BOOTS).color(Color.AQUA);
							p.getInventory().setBoots(builder.build());
							builder = null;
							p.updateInventory();

							for (int i = 0; i < 6; i++) {
								final int current = i;
								new BukkitRunnable() {
									@Override
									public void run() {
										if (current == 5) {
											if (flying.contains(p.getUniqueId())) {
												flying.remove(p.getUniqueId());
												p.sendMessage("§5§lPHANTON§f Acabou o tempo de §9§lVÔO");
										
											}
											p.setAllowFlight(false);
											p.setFlying(false);
											if (previous.containsKey(p.getUniqueId())) {
												p.getInventory().setArmorContents(previous.get(p.getUniqueId()));
												p.updateInventory();
												previous.remove(p.getUniqueId());
											}
										} else {
											if (flying.contains(p.getUniqueId())) {
												p.sendMessage("§5§lPHANTOM§f Você não voará mais em §9§l"
														+ convert(current) + " SEGUNDOS...");
											}
										}
									}
								}.runTaskLater(yPvP.getPlugin(), i * 20);
							}
						} else {
							sendCooldown(p);
						}
					} else {
						p.sendMessage("§5§lPHANTOM§f Você já está §9§lVOANDO!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPhantonBug(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			if (flying.contains(p.getUniqueId()) || previous.containsKey(p.getUniqueId())) {
				if (event.getSlotType() == SlotType.ARMOR) {
					event.setCancelled(true);
				}
			}
		}
	}

	public int convert(int a) {
		if (a == 0) {
			return 5;
		} else if (a == 1) {
			return 4;
		} else if (a == 2) {
			return 3;
		} else if (a == 3) {
			return 2;
		} else if (a == 4) {
			return 1;
		} else {
			return a;
		}
	}
}
