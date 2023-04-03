package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.event.GamerHitEntityEvent;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import net.minecraft.server.v1_8_R3.DamageSource;

/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Magma extends Kit {

	public Magma(Manager manager) {
		super(manager);
		
		setPrice(47500);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.LAVA_BUCKET));
		setFree(true);
		setDescription("Seja igual o magma terrestre, fique imune a altas temperaturas mas receba dano na agua.");
		setRecent(false);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {

		if (isPreGame())
			return;
		if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity()) && (e.getCause() == DamageCause.LAVA || e.getCause().name().contains("FIRE"))) {
			if (e.getCause() == DamageCause.LIGHTNING && (Math.abs(e.getEntity().getLocation().getBlockX()) > 490 || Math.abs(e.getEntity().getLocation().getBlockZ()) > 490))
				return;
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(GamerHitEntityEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		if (HungerGames.getManager().getGameManager().getGameStage() != GameStage.GAME)
			return;

		if (isPreGame())
			return;
		if (getManager().getGameManager().isPreGame())
			return;
		if (getGamer(e.getDamager()).isSpectating())
			return;
		if (hasKit(e.getDamager())) {
			if (new Random().nextBoolean()) {
				e.getEntity().setFireTicks(90);
			}
		}
	}

	@EventHandler
	public void onUpdate(ServerTimeEvent event) {
		for (Player p : getManager().getGamerManager().getAlivePlayers()) {
			if (hasKit(p)) {
				if (!p.getLocation().getBlock().getType().name().contains("WATER")) {
					continue;
				}
				((CraftPlayer) p).getHandle().damageEntity(DamageSource.DROWN, 2);
			}
		}
	}
}
