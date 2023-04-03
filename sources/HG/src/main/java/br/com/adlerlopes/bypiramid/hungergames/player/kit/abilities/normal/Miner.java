package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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

public class Miner extends Kit {

	public Miner(Manager manager) {
		super(manager);
		
		setPrice(40000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.STONE_PICKAXE));
		setFree(true);
		setDescription("Com sua picareta, minere rapidamente com sua super habilidade de minerador.");
		setRecent(false);

		ItemStack item = createItemStack("§cMiner", Material.STONE_PICKAXE);
		item.addEnchantment(Enchantment.DURABILITY, 1);
		item.addEnchantment(Enchantment.DIG_SPEED, 1);

		setItems(item);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMiner2(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (!hasKit(p)) {
			return;
		}
		if (event.getBlock().getType() == Material.COAL_ORE) {
			Block u = event.getBlock().getRelative(BlockFace.UP);
			while (u.getType() == Material.COAL_ORE) {
				u.breakNaturally();

				u = u.getRelative(BlockFace.UP);
			}
			Block d = event.getBlock().getRelative(BlockFace.DOWN);
			while (d.getType() == Material.COAL_ORE) {
				d.breakNaturally();
				d = d.getRelative(BlockFace.DOWN);

			}
			Block e = event.getBlock().getRelative(BlockFace.EAST);
			while (e.getType() == Material.COAL_ORE) {
				e.breakNaturally();
				e = e.getRelative(BlockFace.EAST);

			}
			Block ene = event.getBlock().getRelative(BlockFace.EAST_NORTH_EAST);
			while (ene.getType() == Material.COAL_ORE) {
				ene.breakNaturally();
				ene = ene.getRelative(BlockFace.EAST_NORTH_EAST);

			}
			Block ese = event.getBlock().getRelative(BlockFace.EAST_SOUTH_EAST);
			while (ese.getType() == Material.COAL_ORE) {
				ese.breakNaturally();
				ese = ese.getRelative(BlockFace.EAST_SOUTH_EAST);

			}
			Block north = event.getBlock().getRelative(BlockFace.NORTH);
			while (north.getType() == Material.COAL_ORE) {
				north.breakNaturally();
				north = north.getRelative(BlockFace.NORTH);

			}
			Block ne = event.getBlock().getRelative(BlockFace.NORTH_EAST);
			while (ne.getType() == Material.COAL_ORE) {
				ne.breakNaturally();
				ne = ne.getRelative(BlockFace.NORTH_EAST);

			}
			Block nne = event.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST);
			while (nne.getType() == Material.COAL_ORE) {
				nne.breakNaturally();
				nne = nne.getRelative(BlockFace.NORTH_NORTH_EAST);

			}
			Block nnw = event.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST);
			while (nnw.getType() == Material.COAL_ORE) {
				nnw.breakNaturally();
				nnw = nnw.getRelative(BlockFace.NORTH_NORTH_WEST);

			}
			Block nw = event.getBlock().getRelative(BlockFace.NORTH_WEST);
			while (nw.getType() == Material.COAL_ORE) {
				nw.breakNaturally();
				nw = nw.getRelative(BlockFace.NORTH_WEST);

			}
			Block s = event.getBlock().getRelative(BlockFace.SELF);
			while (s.getType() == Material.COAL_ORE) {
				s.breakNaturally();
				s = s.getRelative(BlockFace.SELF);

			}
			Block south = event.getBlock().getRelative(BlockFace.SOUTH);
			while (south.getType() == Material.COAL_ORE) {
				south.breakNaturally();
				south = south.getRelative(BlockFace.SOUTH);

			}
			Block se = event.getBlock().getRelative(BlockFace.SOUTH_EAST);
			while (se.getType() == Material.COAL_ORE) {
				se.breakNaturally();
				se = se.getRelative(BlockFace.SOUTH_EAST);

			}
			Block sse = event.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST);
			while (sse.getType() == Material.COAL_ORE) {
				sse.breakNaturally();
				sse = sse.getRelative(BlockFace.SOUTH_SOUTH_EAST);

			}
			Block ssw = event.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST);
			while (ssw.getType() == Material.COAL_ORE) {
				ssw.breakNaturally();
				ssw = ssw.getRelative(BlockFace.SOUTH_SOUTH_WEST);

			}
			Block sw = event.getBlock().getRelative(BlockFace.SOUTH_WEST);
			while (sw.getType() == Material.COAL_ORE) {
				sw.breakNaturally();
				sw = sw.getRelative(BlockFace.SOUTH_WEST);

			}
			Block w = event.getBlock().getRelative(BlockFace.WEST);
			while (w.getType() == Material.COAL_ORE) {
				w.breakNaturally();
				w = w.getRelative(BlockFace.WEST);

			}
			Block wnw = event.getBlock().getRelative(BlockFace.WEST_NORTH_WEST);
			while (wnw.getType() == Material.COAL_ORE) {
				wnw.breakNaturally();
				wnw = wnw.getRelative(BlockFace.WEST_NORTH_WEST);

			}
			Block wsw = event.getBlock().getRelative(BlockFace.WEST_SOUTH_WEST);
			while (wsw.getType() == Material.COAL_ORE) {
				wsw.breakNaturally();
				wsw = wsw.getRelative(BlockFace.WEST_SOUTH_WEST);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMiner(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (!hasKit(p)) {
			return;
		}
		if (event.getBlock().getType() == Material.IRON_ORE) {
			Block u = event.getBlock().getRelative(BlockFace.UP);
			while (u.getType() == Material.IRON_ORE) {
				u.breakNaturally();
				u = u.getRelative(BlockFace.UP);
			}
			Block d = event.getBlock().getRelative(BlockFace.DOWN);
			while (d.getType() == Material.IRON_ORE) {
				d.breakNaturally();
				d = d.getRelative(BlockFace.DOWN);
			}
			Block e = event.getBlock().getRelative(BlockFace.EAST);
			while (e.getType() == Material.IRON_ORE) {
				e.breakNaturally();
				e = e.getRelative(BlockFace.EAST);
			}
			Block ene = event.getBlock().getRelative(BlockFace.EAST_NORTH_EAST);
			while (ene.getType() == Material.IRON_ORE) {
				ene.breakNaturally();
				ene = ene.getRelative(BlockFace.EAST_NORTH_EAST);
			}
			Block ese = event.getBlock().getRelative(BlockFace.EAST_SOUTH_EAST);
			while (ese.getType() == Material.IRON_ORE) {
				ese.breakNaturally();
				ese = ese.getRelative(BlockFace.EAST_SOUTH_EAST);
			}
			Block north = event.getBlock().getRelative(BlockFace.NORTH);
			while (north.getType() == Material.IRON_ORE) {
				north.breakNaturally();
				north = north.getRelative(BlockFace.NORTH);
			}
			Block ne = event.getBlock().getRelative(BlockFace.NORTH_EAST);
			while (ne.getType() == Material.IRON_ORE) {
				ne.breakNaturally();
				ne = ne.getRelative(BlockFace.NORTH_EAST);
			}
			Block nne = event.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST);
			while (nne.getType() == Material.IRON_ORE) {
				nne.breakNaturally();
				nne = nne.getRelative(BlockFace.NORTH_NORTH_EAST);
			}
			Block nnw = event.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST);
			while (nnw.getType() == Material.IRON_ORE) {
				nnw.breakNaturally();
				nnw = nnw.getRelative(BlockFace.NORTH_NORTH_WEST);
			}
			Block nw = event.getBlock().getRelative(BlockFace.NORTH_WEST);
			while (nw.getType() == Material.IRON_ORE) {
				nw.breakNaturally();
				nw = nw.getRelative(BlockFace.NORTH_WEST);
			}
			Block s = event.getBlock().getRelative(BlockFace.SELF);
			while (s.getType() == Material.IRON_ORE) {
				s.breakNaturally();
				s = s.getRelative(BlockFace.SELF);
			}
			Block south = event.getBlock().getRelative(BlockFace.SOUTH);
			while (south.getType() == Material.IRON_ORE) {
				south.breakNaturally();
				south = south.getRelative(BlockFace.SOUTH);
			}
			Block se = event.getBlock().getRelative(BlockFace.SOUTH_EAST);
			while (se.getType() == Material.IRON_ORE) {
				se.breakNaturally();
				se = se.getRelative(BlockFace.SOUTH_EAST);
			}
			Block sse = event.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST);
			while (sse.getType() == Material.IRON_ORE) {
				sse.breakNaturally();
				sse = sse.getRelative(BlockFace.SOUTH_SOUTH_EAST);
			}
			Block ssw = event.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST);
			while (ssw.getType() == Material.IRON_ORE) {
				ssw.breakNaturally();
				ssw = ssw.getRelative(BlockFace.SOUTH_SOUTH_WEST);
			}
			Block sw = event.getBlock().getRelative(BlockFace.SOUTH_WEST);
			while (sw.getType() == Material.IRON_ORE) {
				sw.breakNaturally();
				sw = sw.getRelative(BlockFace.SOUTH_WEST);
			}
			Block w = event.getBlock().getRelative(BlockFace.WEST);
			while (w.getType() == Material.IRON_ORE) {
				w.breakNaturally();
				w = w.getRelative(BlockFace.WEST);
			}
			Block wnw = event.getBlock().getRelative(BlockFace.WEST_NORTH_WEST);
			while (wnw.getType() == Material.IRON_ORE) {
				wnw.breakNaturally();
				wnw = wnw.getRelative(BlockFace.WEST_NORTH_WEST);
			}
			Block wsw = event.getBlock().getRelative(BlockFace.WEST_SOUTH_WEST);
			while (wsw.getType() == Material.IRON_ORE) {
				wsw.breakNaturally();
				wsw = wsw.getRelative(BlockFace.WEST_SOUTH_WEST);
			}
		}
	}
}
