package br.com.adlerlopes.bypiramid.hungergames.game.structures.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.game.structures.Structure;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FinalArena extends Structure {

	public FinalArena(Manager manager, Location location) {
		super(manager, new Location(Bukkit.getWorld("arena"), 0, 55, 0));
	}

	public void cleanPlayer() {
		int time = 0;

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {

			new BukkitRunnable() {
				public void run() {
					player.teleport(new Location(Bukkit.getWorld("arena"), 0, 55, 0));
				}
			}.runTaskLater(getManager().getPlugin(), time);

			time++;
		}

		for (Gamer gamer : getManager().getGamerManager().getGamers().values()) {
			Player player = gamer.getPlayer();
			if (gamer.isAlive() && player.isOnline()) {
				player.closeInventory();
				for (Item i : player.getWorld().getEntitiesByClass(Item.class)) {
					i.remove();
				}
			}
		}
	}

}
