package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Launcher extends Kit {

	private static HashMap<Block, SpongeDirection> spongesDirections = new HashMap<>();
	private static List<Player> noFall = new ArrayList<>();

	public Launcher(Manager manager) {
		super(manager);
		
		setPrice(46000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SPONGE));
		setFree(false);
		setDescription("Use suas esponjas para ser lançado em uma direção.");
		setRecent(false);
		setItems(new ItemStack(Material.SPONGE, 20));
	}

	private enum SpongeDirection {
		SKY;
	}

	@EventHandler
	public void launcher(BlockPlaceEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			return;
		}
		if (event.getBlock().getType() == Material.SPONGE) {
			Player player = event.getPlayer();
			if (getDirection(player) == null) {
				event.setCancelled(true);
				player.sendMessage("§6§lLAUNCHER §fOcorreu um problema com a §e§lDIRECAO§f da sua §6§lSPONGE");
				return;
			}
			spongesDirections.put(event.getBlock(), getDirection(player));
		}
	}

	private SpongeDirection getDirection(Player p) {
		return SpongeDirection.SKY;
	}

	@EventHandler
	public void launcher(EntityExplodeEvent event) {
		for (Block blayer : event.blockList()) {
			if (spongesDirections.containsKey(blayer)) {
				spongesDirections.remove(blayer);
			}
		}
	}

	@EventHandler
	public void launcher(BlockBreakEvent event) {
		if (spongesDirections.containsKey(event.getBlock())) {
			spongesDirections.remove(event.getBlock());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void launcher(final PlayerMoveEvent event) {
		if (spongesDirections.containsKey(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN))
				&& event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				&& event.getPlayer().isOnGround()) {
			SpongeDirection direction = spongesDirections
					.get(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN));
			Vector vector = event.getPlayer().getVelocity();
			boolean cancel = false;
			float v_x = 0.0F;
			float v_y = 0.333F;
			float v_z = 0.0F;
			if (event.getPlayer().getLocation().getBlock().getType() == Material.AIR) {
				cancel = true;
			}
			Block sponge = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
			for (int x = 1; x <= 5; x++) {
				Block b = sponge;
				if (b.getRelative(BlockFace.DOWN).getType() == Material.SPONGE) {
					v_y = v_y + 0.33F;
					if (spongesDirections.containsKey(b.getRelative(BlockFace.DOWN))) {
						SpongeDirection Dir = spongesDirections.get(b.getRelative(BlockFace.DOWN));
						if (Dir == SpongeDirection.SKY) {
							v_y = v_y + 0.25F;
						}
					}
				}
				b = b.getRelative(BlockFace.DOWN);
			}

			if (direction == SpongeDirection.SKY) {
				v_y = v_y + 2.3F;
			}

			vector.setX(v_x);
			vector.setY(v_y);
			vector.setZ(v_z);
			event.getPlayer().setVelocity(vector);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
				public void run() {
					event.getPlayer().setVelocity(vector.setX(vector.getX() / 2).setZ(vector.getZ() / 2));
				}
			}, 5L);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
				public void run() {
					event.getPlayer().setVelocity(vector.setX(vector.getX() / 2).setZ(vector.getZ() / 2));
				}
			}, 10L);
			if (cancel == true && !noFall.contains(event.getPlayer())) {
				noFall.add(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void launcher(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player
				&& noFall.contains(event.getEntity())) {
			if (getManager().getGamerManager().getGamer((Player) event.getEntity()).getKit().getName()
					.equalsIgnoreCase("Stomper")
					|| getManager().getGamerManager().getGamer((Player) event.getEntity()).getKit().getName()
							.equalsIgnoreCase("Tower")) {
				return;
			}
			noFall.remove(event.getEntity());
			event.setCancelled(true);
		}
	}

	public static List<Player> getNoFallList() {
		return noFall;
	}
}
