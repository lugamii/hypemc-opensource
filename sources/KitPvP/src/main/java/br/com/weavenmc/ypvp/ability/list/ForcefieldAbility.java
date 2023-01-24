package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class ForcefieldAbility {// extends Ability {
//
//	private final ArrayList<UUID> ff = new ArrayList<>();
//
//	public ForcefieldAbility() {
//		setName("Forcefield");
//		setHasItem(true);
//		setGroupToUse(Group.BETA);
//		setIcon(Material.GOLDEN_APPLE);
//		setDescription(new String[] { "§7Ative seu campo de força e hite", "§7todos á 6 blocos de distancia." });
//		setPrice(60000);
//		setTempPrice(6500);
//	}
//
//	@Override
//	public void eject(Player p) {
//		ff.remove(p.getUniqueId());
//	}
//
//	@EventHandler(priority = EventPriority.LOWEST)
//	public void onForcefield(PlayerInteractEvent event) {
//		Player p = event.getPlayer();
//		ItemStack itemInHand = p.getItemInHand();
//		if (hasKit(p)) {
//			if (!isItem(itemInHand)) {
//				if (ff.contains(p.getUniqueId())) {
//					double damage = getDamage(itemInHand.getType());
//					for (Entity nearby : p.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
//						((Damageable) nearby).damage(damage, p);
//						if (nearby instanceof Player) {
//							((Player) nearby).sendMessage("§5§lFORCEFIELD§f Você está no campo de §9§l" + p.getName());
//						}
//					}
//				}
//			} else if (!inCooldown(p)) {
//				if (!ff.contains(p.getUniqueId())) {
//					addCooldown(p, 50);
//					p.updateInventory();
//					ff.add(p.getUniqueId());
//					p.sendMessage("§5§lFORCEFIELD§f Você §9§lATIVOU§f o seu §9§lCAMPO DE FORÇA");
//					Bukkit.getScheduler().runTaskLater(yPvP.getPlugin(), () -> {
//						p.updateInventory();
//						ff.remove(p.getUniqueId());
//					}, 16 * 20);
//				} else {
//					p.sendMessage("§5§lFORCEFIELD§f O seu §9§lCAMPO DE FORÇA§f já está §9§lATIVADO");
//				}
//			} else {
//				sendCooldown(p);
//			}
//		}
//	}
//
//	private double getDamage(Material type) {
//		double damage = 1.0;
//		if (type.toString().contains("DIAMOND_")) {
//			damage = 8.0;
//		} else if (type.toString().contains("IRON_")) {
//			damage = 7.0;
//		} else if (type.toString().contains("STONE_")) {
//			damage = 6.0;
//		} else if (type.toString().contains("WOOD_")) {
//			damage = 5.0;
//		} else if (type.toString().contains("GOLD_")) {
//			damage = 5.0;
//		}
//		if (!type.toString().contains("_SWORD")) {
//			damage--;
//			if (!type.toString().contains("_AXE")) {
//				damage--;
//				if (!type.toString().contains("_PICKAXE")) {
//					damage--;
//					if (!type.toString().contains("_SPADE")) {
//						damage = 1.0;
//					}
//				}
//			}
//		}
//		return damage;
//	}
}
