package br.com.adlerlopes.bypiramid.hungergames.game.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;

/**
 * Copyright (C) LittleMC, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class ArenaManager extends Management {

	public final List<Player> gamersAlready = new ArrayList<>();
	private Location lastLocation = null;

	public ArenaManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public List<Player> getGamersAlready() {
		return gamersAlready;
	}

}
