package br.com.adlerlopes.bypiramid.hungergames.game.event;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.player.events.CustomEvent;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerDeathEvent extends CustomEvent {

	private Player killer, entity;
	private Location drops;
	private List<ItemStack> items;

	public GamerDeathEvent(Player killer, Player entity, Location dropsLocation, List<ItemStack> drops) {
		this.killer = killer;
		this.entity = entity;
		this.drops = dropsLocation;
		this.items = drops;
	}

	public Player getPlayer() {
		return entity;
	}

	public Player getKiller() {
		return killer;
	}

	public List<ItemStack> getDrops() {
		return items;
	}

	public Location getDropsLocation() {
		return drops;
	}

	public void setDropsLocation(Location location) {
		drops = location;
	}

	public Player getEntity() {
		return entity;
	}

}
