package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Demoman extends Kit {

	public Demoman(Manager manager) {
		super(manager);
		
		this.setActive(false);
		setPrice(42000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.GRAVEL));
		setFree(false);
		setDescription("Tenha a habilidade de montar uma mina e com ela exploda seus inimigos.");
		setRecent(false);
		setItems(new ItemStack(Material.STONE_PLATE, 6), new ItemStack(Material.GRAVEL, 6));
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!hasKit(player))
			return;
		if (getManager().getGamerManager().getGamer(player).isSpectating())
			return;

		if (isInvencibility()) {
			player.sendMessage("§3§lDEMOMAN §fVocê não pode usar seu kit na §b§lINVENCIBILIDADE");
			return;
		}

		if (block.getType() == Material.GRAVEL || block.getType() == Material.STONE_PLATE) {
			if (block.hasMetadata("Demoman")) {
				block.removeMetadata("Demoman", getManager().getPlugin());
				block.setMetadata("Demoman", new FixedMetadataValue(getManager().getPlugin(), player.getName()));
			} else {
				block.setMetadata("Demoman", new FixedMetadataValue(getManager().getPlugin(), player.getName()));
			}
		}
	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		if (!block.hasMetadata("Demoman")) {
			return;
		}
		if (block.getType() != Material.STONE_PLATE) {
			return;
		}
		if (block.getRelative(BlockFace.DOWN).getType() != Material.GRAVEL) {
			return;
		}
		if (event.getAction() == Action.PHYSICAL) {
			if (isInvencibility())
				return;
			if (getGamer(event.getPlayer()).isSpectating())
				return;
			block.removeMetadata("Demoman", getManager().getPlugin());
			block.setType(Material.AIR);
			block.getWorld().createExplosion(block.getLocation().clone().add(0.5D, 0.5D, 0.5D), 4.0F);
		}
	}

}
