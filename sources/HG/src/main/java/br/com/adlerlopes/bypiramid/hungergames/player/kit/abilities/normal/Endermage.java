package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Endermage extends Kit {

	private static HashMap<UUID, Integer> inventaryNumber = new HashMap<>();
	private static ArrayList<Block> endermages = new ArrayList<>();

	public static ArrayList<UUID> invencible = new ArrayList<>();

	private static ItemBuilder endermageItem = new ItemBuilder(Material.ENDER_PORTAL_FRAME)
			.setName("§cEndermage");

	public Endermage(Manager manager) {
		super(manager);
		
		setPrice(48000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.ENDER_PORTAL_FRAME));
		setFree(false);
		setDescription("Transforme-se em um mago e domine as magias de teletransporte.");
		setRecent(false);
		setItems(endermageItem.getStack());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack itemStack = event.getItem();
		Block block = event.getClickedBlock();
		if (!action.toString().contains("BLOCK"))
			return;
		
		if (itemStack == null) {
			return;
		}
		if (!isKitItem(itemStack, "§cEndermage"))
			return;

		if (!hasKit(player))
			return;

		event.setCancelled(true);

		if (block.getType() == Material.CACTUS || block.getType() == Material.TRAP_DOOR
				|| block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN
				|| block.getType() == Material.SIGN_POST) {
			player.sendMessage("§5§lENDERMAGE §fVocê não pode usar seu kit nesse §5§lBLOCO");
			return;
		}

		if (endermages.contains(block))
			return;

		endermages.add(block);

		inventaryNumber.put(player.getUniqueId(), player.getInventory().getHeldItemSlot());

		itemStack.setAmount(0);
		if (itemStack.getAmount() == 0) {
			player.setItemInHand(null);
		}

		Material blockMaterial = block.getType();
		byte dataValue = block.getData();
		Location portalLocation = block.getLocation().clone().add(0.5D, 0.5D, 0.5D);

		portalLocation.getBlock().setType(Material.ENDER_PORTAL_FRAME);
		for (int i = 0; i <= 5; i++) {
			int number = i;

			new BukkitRunnable() {
				public void run() {

					if (portalLocation.getBlock().getType() != Material.ENDER_PORTAL_FRAME) {
						return;
					}

					if (number < 5) {

						for (Player gamer : portalLocation.getWorld().getPlayers()) {
							if (gamer != player) {
								if (isEnderable(portalLocation, gamer.getLocation())) {
									if (!hasKit(gamer) && !getGamer(gamer).isSpectating()) {
										if (gamer.getLocation().distance(portalLocation) > 3.0D) {

											if (getGamer(gamer).getKit().getName().equalsIgnoreCase("AntiTower"))
												continue;

											gamer.teleport(portalLocation.clone().add(0.0D, 0.5D, 0.0D));
											player.teleport(portalLocation.clone().add(0.0D, 0.5D, 0.0D));

											gamer.sendMessage("§5§lENDERMAGE §fVocê foi teletransportado por " + player.getName()
													+ "\n§5§lENDERMAGE §fVocês estão invencíveis por alguns segundos.");
											player.sendMessage("§5§lENDERMAGE §fVocê puxou um player.");

											invencible.add(gamer.getUniqueId());
											invencible.add(player.getUniqueId());

											player.getInventory().setItem(inventaryNumber.get(player.getUniqueId()),
													endermageItem.getStack());

											endermages.remove(block);

											portalLocation.getBlock().setTypeIdAndData(blockMaterial.getId(), dataValue,
													true);

											Bukkit.getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(),
													new Runnable() {
														public void run() {
															Endermage.invencible.remove(gamer.getUniqueId());
															Endermage.invencible.remove(player.getUniqueId());
														}
													}, 100L);
										}
									}
								}
							}
						}

					} else {
						portalLocation.getBlock().setTypeIdAndData(blockMaterial.getId(), dataValue, true);
						player.getInventory().setItem(inventaryNumber.get(player.getUniqueId()),
								endermageItem.getStack());
						endermages.remove(block);
					}
				}
			}.runTaskLater(getManager().getPlugin(), i * 20);
		}
	}

	@EventHandler
	public void entityDamageEvent(EntityDamageEvent event) {
		Entity damaged = event.getEntity();
		if (damaged.isDead())
			return;
		if (!(damaged instanceof Player))
			return;
		if (invencible.contains(damaged.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		if (!(damager instanceof Player))
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		if (invencible.contains(damager.getUniqueId()))
			event.setCancelled(true);
	}

	private boolean isEnderable(Location portal, Location player) {
		return (Math.abs(portal.getX() - player.getX()) < 2.0D) && (Math.abs(portal.getZ() - player.getZ()) < 2.0D);
	}

}
