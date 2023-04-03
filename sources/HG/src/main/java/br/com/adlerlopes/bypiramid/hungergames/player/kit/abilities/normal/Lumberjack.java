package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Lumberjack extends Kit {

	public Lumberjack(Manager manager) {
		super(manager);
		
		setPrice(41000);
		setCooldownTime(30D);
		setIcon(new ItemStack(Material.WOOD_AXE));
		setFree(true);
		setDescription("Use suas habilidades de lenhador e derrube uma arvore por inteiro.");
		setRecent(false);
		setItems(new ItemStack(Material.WOOD_AXE));
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (hasKit(event.getPlayer())) {
			if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
				World world = (World) Bukkit.getServer().getWorlds().get(0);
				Double doub = Double.valueOf(block.getLocation().getY() + 1.0D);
				Location location = new Location(world, block.getLocation().getX(), doub.doubleValue(),
						block.getLocation().getZ());
				while ((location.getBlock().getType() == Material.LOG)
						|| (location.getBlock().getType() == Material.LOG_2)) {
					location.getBlock().breakNaturally();
					doub = Double.valueOf(doub.doubleValue() + 1.0D);
					location.setY(doub.doubleValue());
				}
			}
		}
	}

}
