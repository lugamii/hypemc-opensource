package br.com.weavenmc.skywars.events;

import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.material.Dispenser;

import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.inventorys.SpectatorInventory;

public class EspectadorEvent implements Listener {

	// tenso, voce quer testar os items entao?
	// Olha dc pelo meu pc
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (GameController.spectador.contains(player.getUniqueId())) {
			e.setCancelled(true);
			if (e.getAction().name().contains("RIGHT")) {
				if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.BED)) {
					WeavenSkywars.getGameManager().sendLobby(e.getPlayer());
					return;
				}
				if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.BOOK)) {
					SpectatorInventory.openInventory(e.getPlayer());
					return;
				}
				if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.PAPER)) {
					WeavenSkywars.getGameManager().findServer(e.getPlayer(), ServerType.SKYWARS);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractEventBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && GameController.spectador.contains(event.getPlayer().getUniqueId())) {
			Block b = event.getClickedBlock();
			if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper || b.getState() instanceof Dispenser
			|| b.getState() instanceof Furnace || b.getState() instanceof Beacon) {
			if (!AdminMode.getInstance().isAdmin(event.getPlayer()))
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (GameController.spectador.contains(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamageEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();
		if (AdminMode.getInstance().isAdmin(damager) || AdminMode.getInstance().isAdmin(damaged)) {
			event.setCancelled(false);
			return;
		}
		if (GameController.spectador.contains(damager.getUniqueId()) || GameController.spectador.contains(damaged.getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		
		if (e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			if (GameController.spectador.contains(p.getUniqueId()) || (AdminMode.getInstance().isAdmin(p)))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		Player p = e.getPlayer();
		if (AdminMode.getInstance().isAdmin(p)) {
			e.setCancelled(false);
			return;
		}
		if (GameController.spectador.contains(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		Player p = e.getPlayer();
		if (AdminMode.getInstance().isAdmin(p)) {
			e.setCancelled(false);
			return;
		}
		if (GameController.spectador.contains(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (GameController.spectador.contains(e.getPlayer().getUniqueId()) || (AdminMode.getInstance().isAdmin(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (GameController.spectador.contains(e.getPlayer().getUniqueId()) || (AdminMode.getInstance().isAdmin(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();
		if (GameController.spectador.contains(player.getUniqueId()) || (AdminMode.getInstance().isAdmin(player))) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}

}
