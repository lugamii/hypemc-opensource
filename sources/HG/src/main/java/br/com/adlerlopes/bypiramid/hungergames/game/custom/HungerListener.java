package br.com.adlerlopes.bypiramid.hungergames.game.custom;

import org.bukkit.event.Listener;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class HungerListener implements Listener {

	public Manager getManager() {
		return HungerGames.getManager();
	}

}
