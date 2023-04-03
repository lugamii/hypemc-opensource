package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Phantom extends Kit {

	public static HashMap<UUID, ItemStack[]> armorPlayers = new HashMap<>();

	public Phantom(Manager manager) {
		super(manager);
		
		setPrice(49000);
		setCooldownTime(50D);
		setIcon(new ItemStack(Material.FEATHER));
		setFree(false);
		setDescription("Use suas asas e voe por todo o mapa, crie estratégias e fuja da batalha.");
		setRecent(false);
		setItems(createItemStack("§aPhantom", Material.FEATHER));
	}

	private ItemStack getPhantom(Material mat, Color color) {
		ItemStack i = new ItemStack(mat);
		LeatherArmorMeta im = (LeatherArmorMeta) i.getItemMeta();
		im.setColor(color);
		i.setItemMeta(im);
		return i;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (armorPlayers.containsKey(event.getWhoClicked().getUniqueId())) {
			if (!(event.getWhoClicked() instanceof Player)) {
				return;
			}
			if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
				Material type = event.getCurrentItem().getType();
				if (type == getPhantom(Material.LEATHER_BOOTS, Color.fromRGB(40, 240, 33)).getType()
						|| type == getPhantom(Material.LEATHER_LEGGINGS, Color.fromRGB(40, 240, 33)).getType()
						|| type == getPhantom(Material.LEATHER_CHESTPLATE, Color.fromRGB(40, 240, 33)).getType()
						|| type == getPhantom(Material.LEATHER_HELMET, Color.fromRGB(40, 240, 33)).getType()) {
					event.setCancelled(true);
				}
			}
		}
	}

	public void activatePlayer(Player player) {
		new BukkitRunnable() {
			int time = 8;

			public void run() {
				time--;
				if (time == 5) {
					armorPlayers.put(player.getUniqueId(), player.getInventory().getArmorContents());

					ItemStack lhelmet = new ItemBuilder(Material.AIR).setColor(Material.LEATHER_HELMET,
							Color.fromRGB(40, 240, 33));
					ItemStack lchestplate = new ItemBuilder(Material.AIR).setColor(Material.LEATHER_CHESTPLATE,
							Color.fromRGB(40, 240, 33));
					ItemStack lleggings = new ItemBuilder(Material.AIR).setColor(Material.LEATHER_LEGGINGS,
							Color.fromRGB(40, 240, 33));
					ItemStack lboots = new ItemBuilder(Material.AIR).setColor(Material.LEATHER_BOOTS,
							Color.fromRGB(40, 240, 33));

					for (int i = 0; i <= 50; i++)
						player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);

					player.getInventory().setHelmet(lhelmet);
					player.getInventory().setChestplate(lchestplate);
					player.getInventory().setLeggings(lleggings);
					player.getInventory().setBoots(lboots);

					player.setAllowFlight(true);
					player.playSound(player.getLocation(), Sound.WITHER_DEATH, 10.0F, 1.0F);

				} else if (time <= 0) {
					player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 10.0F, 1.0F);
					player.setAllowFlight(false);
					player.setFlying(false);
					player.getInventory().setArmorContents((ItemStack[]) armorPlayers.get(player.getUniqueId()));
					this.cancel();
				} else if (time <= 3) {
					player.sendMessage(
							"§6§lPHANTOM §fVocê tem mais §f" + time + "§f segundo" + (time == 1 ? "" : "s") + " de voo.");
					player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10.0F, 1.0F);
				}

			}
		}.runTaskTimer(getManager().getPlugin(), 0, 20);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().getType() == Material.FEATHER && hasKit(player)) {
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
					|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

				if (inCooldown(player)) {
					sendCooldown(player);
					return;
				}

				addCooldown(player);
				activatePlayer(player);
			}
		}
	}

	@EventHandler
	public void death(ItemSpawnEvent event) {
		if (event.getEntity() instanceof Item) {
			if (event.getEntity().getItemStack() != null) {
				if (event.getEntity().getItemStack().getItemMeta() != null) {
					if (event.getEntity().getItemStack().getItemMeta() instanceof LeatherArmorMeta) {
						if (((LeatherArmorMeta) event.getEntity().getItemStack().getItemMeta()).getColor()
								.equals(Color.fromRGB(40, 240, 33))) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (hasKit(player)) {
			if (event.getCause() == DamageCause.ENTITY_ATTACK) {
				if (getGamer((Player) event.getDamager()).isSpectating()) {
					return;
				}
				if (!inCooldown(player)) {
					addCooldown(player, 10D);
				}
			}
		}
	}
}
