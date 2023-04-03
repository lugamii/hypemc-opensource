package br.com.adlerlopes.bypiramid.hungergames.game.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import br.com.adlerlopes.bypiramid.hungergames.player.events.CustomEvent;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerHitEntityEvent extends CustomEvent {

	private Player player;
	private LivingEntity entity;
	private double damage;
	private boolean cancel;

	public GamerHitEntityEvent(Player player, LivingEntity entity, double damage) {
		this.player = player;
		this.entity = entity;
		this.damage = damage;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getDamager() {
		return player;
	}

	public LivingEntity getEntity() {
		return this.entity;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isCancelled() {
		return this.cancel;
	}
}
