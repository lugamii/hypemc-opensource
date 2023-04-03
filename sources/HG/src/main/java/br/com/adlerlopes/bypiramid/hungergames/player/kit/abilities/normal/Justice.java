package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class Justice extends Kit {

	public Justice(Manager manager) {
		super(manager);
		
		setPrice(60000);
		setCooldownTime(90D);
		setIcon(new ItemStack(Material.BEDROCK));
		setFree(false);
		setDescription("Lute contra times facilmente, revidando e ganhando força!");
		setRecent(true);
	}

	@EventHandler
	public void onTimeEvt(ServerTimeEvent event) {
		for (Player player : getManager().getGamerManager().getAlivePlayers()) {
			if (!hasKit(player))
				continue;

			int size = 0;

			for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
				if (!(entity instanceof Player))
					continue;

				if (!getManager().getGamerManager().getGamer((Player) entity).isAlive())
					continue;

				size++;
				((Player) entity).playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
			}

			if (size >= 4) {

				Random random = new Random();
				int number = random.nextInt(4);

				if (number == 1) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90, 2));
				} else if (number == 2) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 3));
				} else if (number == 3){
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 30, 2));
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1));
				}
				
				player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1F, 1F);
			}
		}
	}
}
