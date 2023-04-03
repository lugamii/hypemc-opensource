package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.Iterator;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.Line;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Nocturne extends Kit {

	public Nocturne(Manager manager) {
		super(manager);
		
		setPrice(50000);
		setCooldownTime(30D);
		setIcon(new ItemStack(Material.DAYLIGHT_DETECTOR));
		setItems(getItemBuilder().setType(Material.ENDER_PEARL).setName("§eNocturne").getStack());
		setFree(false);
		setDescription("Teleporte-se rapidamente para o seu inimigo, não deixando ele se quer te enxergar.");
		setRecent(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (HungerGames.getManager().getGameManager().getGameStage() != GameStage.GAME)
			return;
		if (event.getItem() == null) {
			return;
		}

		final Player player = event.getPlayer();
		if (!hasKit(player)) {
			return;
		}

		if (getItemBuilder().checkItem(event.getItem(), "§eNocturne")) {
			event.setCancelled(true);
			player.updateInventory();

			if (inCooldown(player)) {
				sendCooldown(player);
				return;
			}

			final Player target = getTarget(player);
			final Iterator<Block> iterator = new Line(player.getLocation(), target.getLocation()).iterator();
			new BukkitRunnable() {
				public void run() {
					if (iterator.hasNext()) {
						Block block = iterator.next();

						Location toTeleport = player.getWorld().getHighestBlockAt(block.getLocation()).getLocation();
						toTeleport.setPitch(player.getLocation().getPitch());
						toTeleport.setYaw(player.getLocation().getYaw());

						player.teleport(toTeleport);

						Location location = player.getLocation();

						location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.OBSIDIAN);
						location.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, Material.OBSIDIAN);
					} else {
						cancel();
					}
				}
			}.runTaskTimer(getManager().getPlugin(), 0L, 1L);

			addCooldown(player);
		}
	}

	private Player getTarget(Player player) {
		Player target = null;

		for (Gamer gamers : getManager().getGamerManager().getAliveGamers()) {
			if (gamers.getPlayer().isOnline()) {

				Player playerTarget = gamers.getPlayer();
				if (playerTarget.equals(player)) {
					continue;
				}
				if (playerTarget.getLocation().distance(player.getLocation()) < 15.0D) {
					continue;
				}
				if (target == null) {
					target = playerTarget;
				} else {
					if (target.getLocation().distance(player.getLocation()) > playerTarget.getLocation().distance(player.getLocation())) {
						target = playerTarget;
					}
				}
			}
		}

		return target;
	}

}
