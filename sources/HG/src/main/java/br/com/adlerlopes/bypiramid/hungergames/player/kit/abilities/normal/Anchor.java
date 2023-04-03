package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Anchor extends Kit {

	public Anchor(Manager manager) {
		super(manager);

		setPrice(41000);
		setCooldownTime(30D);
		setIcon(new ItemStack(Material.ANVIL));
		setFree(false);
		setDescription(
				"Não dê nem receba knockback.");
		setRecent(false);
	}

	@EventHandler
	public void onAnchor(EntityDamageByEntityEvent event) {
		if (HungerGames.getManager().getGameManager().getGameStage() != GameStage.GAME)
			return;
		if ((!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
			return;
		}
		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		if ((hasKit(player)) || (hasKitSecondary(player)) || (hasKit(damager)) || (hasKitSecondary(damager))) {
			anchor(player);
			anchor(damager);
		} 
	}

	private void anchor(final Player player) {
		player.setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			public void run() {
				player.setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(getManager().getPlugin(), 1L);
	}

}
