package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.gladiator.GladiatorFight;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Gladiator extends Kit {

	public static List<UUID> playersIn1v1 = new ArrayList<>();
	public static List<Block> gladiatorBlocks = new ArrayList<>();

	public Gladiator(Manager manager) {
		super(manager);
		
		setPrice(52000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.IRON_FENCE));
		setFree(false);
		setDescription("Ganhe a habilidade de criar uma pequena arena para duelar com seus oponentes");
		setRecent(false);
		setItems(createItemStack("§eGladiator", Material.IRON_FENCE));
	}

	@EventHandler
	public void onEntityClick(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		ItemStack item = player.getItemInHand();

		if (!(entity instanceof Player))
			return;
		if (!hasKit(player))
			return;
		if (item.getType() != Material.IRON_FENCE) 
			return;
		if (!isKitItem(item, "§eGladiator"))
			return;
		if (isInvencibility())
			return;
		if (playersIn1v1.contains(player.getUniqueId()))
			return;
		if (playersIn1v1.contains(((Player) entity).getUniqueId()))
			return;
		if (!getGamer((Player) entity).isAlive()){
			return;
		}
		if (isOnWarning(player)) {
			player.sendMessage("§b§lGLADIATOR §fVocê não §3§lPODE§f usar seu §3§lGLADIATOR§f aqui!");
			return;
		}

		new GladiatorFight(player, (Player) entity, getManager());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack i = p.getItemInHand();
		if ((event.getAction() != Action.PHYSICAL) && (hasKit(p)) && (i.getType() != null) && (i.getType() == Material.IRON_FENCE)) {
			p.updateInventory();
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlock(BlockDamageEvent event) {
		if (gladiatorBlocks.contains(event.getBlock())) {
			event.getPlayer().sendBlockChange(event.getBlock().getLocation(), Material.BEDROCK, (byte) 0);
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Iterator<Block> blockIt = event.blockList().iterator();
		while (blockIt.hasNext()) {
			Block block = (Block) blockIt.next();
			if (gladiatorBlocks.contains(block)) {
				blockIt.remove();
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (gladiatorBlocks.contains(event.getBlock())) {
			event.setCancelled(true);
		}
	}

	public static boolean inGladiator(Player player) {
		return playersIn1v1.contains(player.getUniqueId());
	}

	private boolean isNotInBoard(Player player) {
		return (player.getLocation().getBlockX() > 500) || (player.getLocation().getBlockX() < -500) || (player.getLocation().getBlockZ() > 500) || (player.getLocation().getBlockZ() < -500);
	}

	private boolean isOnWarning(Player player) {
		return (!isNotInBoard(player)) && ((player.getLocation().getBlockX() > 480) || (player.getLocation().getBlockX() < -480) || (player.getLocation().getBlockZ() > 480) || (player.getLocation().getBlockZ() < -480));
	}
}
