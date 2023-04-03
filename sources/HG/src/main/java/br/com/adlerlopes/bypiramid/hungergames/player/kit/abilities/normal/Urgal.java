package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Urgal extends Kit {

	private static final ItemBuilder ITEM_BUILDER = new ItemBuilder(Material.POTION).setAmount(2).setDurability(8201).setName("§r§5Urgal Potion");

	public Urgal(Manager manager) {
		super(manager);
		
		this.setActive(false);
		setPrice(48000);
		setCooldownTime(0D);
		setIcon(new ItemBuilder(Material.POTION).setAmount(2).setDurability(8201).getStack());
		setFree(false);
		setDescription("Use suas poções para ganhar força e derrotar seus inimigos mais facilmente.");
		setRecent(false);
		setItems(ITEM_BUILDER.getStack());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().name().contains("RIGHT") && isKitItem(event.getItem(), "§r§5Urgal Potion")) {
			event.setCancelled(true);
			if (event.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				event.getPlayer().sendMessage("§c§lURGAL §fVocê está está sob §4§lEFEITO§f do §c§lURGAL");
				return;
			}
			event.getItem().setAmount(event.getItem().getAmount() - 1);
			if (event.getItem().getAmount() == 0) {
				event.getPlayer().setItemInHand(null);
			}
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60, 0), true);
		}
	}
}
