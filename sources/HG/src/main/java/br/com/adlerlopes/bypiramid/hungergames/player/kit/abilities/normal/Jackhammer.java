package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Jackhammer extends Kit {

	private static HashMap<Player, Integer> jackhammerUses = new HashMap<>();

	public Jackhammer(Manager manager) {
		super(manager);
		
		setPrice(41000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STONE_AXE));
		setFree(true);
		setDescription("Quebre um bloco com seu machado mágico e crie um grande buraco.");
		setRecent(false);
		setItems(new ItemStack(Material.STONE_AXE));
	}

	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (hasKit(player) && player.getItemInHand() != null && player.getItemInHand().getType() == Material.STONE_AXE) {
			if (getManager().getGameManager().isInvencibility()) {
				player.sendMessage("§4§lJACKHAMMER §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
				return;
			}

			int x = event.getBlock().getX();
			int z = event.getBlock().getZ();
			if (x >= 489 || x <= -489 || z >= 489 || z <= -489) {
				event.setCancelled(true);
				player.sendMessage("§4§lJACKHAMMER §fVocê não pode usar seu kit §4§lAQUI!");
				return;
			}

			if (inCooldown(player)) {
				sendCooldown(player);
				return;
			}

			if (jackhammerUses.containsKey(player)) {
				jackhammerUses.put(player, jackhammerUses.get(player) + 1);
			} else {
				jackhammerUses.put(player, 1);
			}

			if (jackhammerUses.get(player) == 6) {
				if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					breakBlock(event.getBlock(), BlockFace.UP);
				} else {
					breakBlock(event.getBlock(), BlockFace.DOWN);
				}
				jackhammerUses.remove(player);
				addCooldown(player);
			} else {
				if (event.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
					breakBlock(event.getBlock(), BlockFace.UP);
				} else {
					breakBlock(event.getBlock(), BlockFace.DOWN);
				}
			}
		}
	}

	private void breakBlock(final Block b, final BlockFace face) {
		new BukkitRunnable() {
			Block block = b;

			@SuppressWarnings("deprecation")
			public void run() {
				if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME && block.getY() <= 128 && !block.hasMetadata("inquebravel")) {
					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
					block.setType(Material.AIR);
					block = block.getRelative(face);
				} else {
					cancel();
				}
			}
		}.runTaskTimer(getManager().getPlugin(), 2L, 2L);
	}
}
