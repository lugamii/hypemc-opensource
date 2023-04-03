package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Snail extends Kit {

	public Snail(Manager manager) {
		super(manager);

		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SOUL_SAND));
		setFree(false);
		setDescription("Deixe seu inimigo com lentidão.");
		setRecent(false);
	}

	@EventHandler
	public void onSnail(EntityDamageByEntityEvent event) {
		if (HungerGames.getManager().getGameManager().getGameStage() != GameStage.GAME)
			return;
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		Player snail = (Player) event.getDamager();
		Location location = player.getLocation();
		if (!hasKit(snail)) {
			return;
		}
		if (player instanceof Player && getManager().getRandom().nextInt(3) == 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
			location.getWorld().playEffect(location.add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, 13);
		}
	}

}
