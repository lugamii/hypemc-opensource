package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Poseidon extends Kit {

	public Poseidon(Manager manager) {
		super(manager);
		
		setPrice(47000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.WATER_BUCKET));
		setFree(false);
		setDescription("Ganhe habilidades de um deus grego e adquira diversos poderes como força.");
		setRecent(false);
	}

	@EventHandler
	public void onPoseidon(ServerTimeEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (hasKit(player)) {
				Block block = player.getLocation().getBlock();
				if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 80, 0));
				}
			}
		}
	}
}
