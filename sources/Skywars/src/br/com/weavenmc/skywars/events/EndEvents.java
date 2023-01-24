package br.com.weavenmc.skywars.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;

public class EndEvents implements Listener {
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				e.getPlayer().kickPlayer("§c§lERRO §fVocê não pode entrar no estágio final.");
			}
		}
	}
	
	@EventHandler
	public void onDamageEvent(EntityDamageEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setQuitMessage(null);
			if (GameController.player.contains(e.getPlayer().getUniqueId())) {
				WeavenSkywars.getGameManager().checkWinner();
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.BED)) {
				WeavenSkywars.getGameManager().sendLobby(e.getPlayer());
				return;
			}
			if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.PAPER)) {
				WeavenSkywars.getGameManager().findServer(e.getPlayer(), ServerType.SKYWARS);
				return;
			}
		}
	}
	
	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlock(BlockPlaceEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlock(BlockBreakEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}

}
