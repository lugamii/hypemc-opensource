package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Thor extends Kit {

	private static ItemBuilder thorItem = new ItemBuilder(Material.WOOD_AXE).setName("§6Thor");

	public Thor(Manager manager) {
		super(manager);
		
		setPrice(47500);
		setCooldownTime(5D);
		setIcon(new ItemStack(Material.WOOD_AXE));
		setFree(false);
		setDescription("Tenha as habilidades do deus nórdico e use o seu Mjölnir para criar raios e trovões.");
		setRecent(false);
		setItems(thorItem.getStack());
	}

	@EventHandler
	public void thor(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (hasKit(player)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (player.getItemInHand().getType() == Material.WOOD_AXE) {

					if (isInvencibility()) {
						player.sendMessage("§6§lTHOR §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
						return;
					}

					if (inCooldown(player)) {
						sendCooldown(player);
						return;
					}

					LightningStrike strike = player.getWorld()
							.strikeLightning(player.getWorld().getHighestBlockAt(event.getClickedBlock().getLocation())
									.getLocation().clone().add(1.5D, 1.0D, 1.5D));
					addCooldown(player);
					for (Entity nearby : strike.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
						if ((nearby instanceof Player)) {
							nearby.setFireTicks(100);
						}
						player.setFireTicks(0);
					}
				}
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.LIGHTNING) {
				if (hasKit(player)) {
					player.setFireTicks(0);
					event.setCancelled(true);
				}
			}
		}
	}
}
