package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Cannibal extends Kit {

	public Cannibal(Manager manager) {
		super(manager);
		
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.RAW_FISH));
		setFree(true);
		setDescription("Cause fome em seus inimigos");
		setRecent(false);
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent event) {

		if (HungerGames.getManager().getGameManager().getGameStage() != GameStage.GAME)
			return;
		if (event.isCancelled()) {
			return;
		}
		if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player && hasKit((Player) event.getDamager()) && new Random().nextInt(100) <= 20) {

			((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 80, 1), true);
			Player player = (Player) event.getDamager();
			int hungry = player.getFoodLevel();
			hungry++;
			if (hungry <= 20) {
				player.setFoodLevel(hungry);
			}
		}
	}

}
