package br.com.weavenmc.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeEnterEvent;
import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeQuitEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerShowEvent;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;

public class AdminEvents implements Listener {

	@EventHandler
	public void onAdmin(PlayerShowEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME
				|| WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlaceBlocks(BlockPlaceEvent event) {
		ChestEvent.placedByPlayer.add(event.getBlock());
	}

	@EventHandler
	public void onAdmin(PlayerAdminModeEnterEvent e) {
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(false);
			WeavenSkywars.getGameManager().setSpectador(player);
		} else {
			if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY) {
				GameController.player.remove(player.getUniqueId());
				GameController.spectador.add(player.getUniqueId());
				e.setCancelled(false);
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.updateInventory();
				if (!StartingEvents.noMessage.contains(player.getUniqueId())) {
					Bukkit.broadcastMessage("§7" + player.getName() + " §esaiu da partida. (§b"
							+ GameController.player.size() + "§e/§b12§e)");
				} else {
					StartingEvents.noMessage.remove(player.getUniqueId());
				}
			} else {
				GameController.player.remove(player.getUniqueId());
				e.setCancelled(false);
				WeavenSkywars.getGameManager().setSpectador(player);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (GameController.spectador.contains(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onAdmin(PlayerAdminModeQuitEvent e) {
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME
				|| WeavenSkywars.getGameManager().getState() == GameState.END) {
			e.setCancelled(true);
			player.sendMessage("§4§lADMIN§f Você não pode sair do modo §c§lADMIN §fnos estágios jogo e final.");
		} else {
			if (GameController.player.size() >= 12) {
				player.sendMessage("§4§lADMIN §fVocê não pode sair do modo §c§lADMIN§f com o servidor cheio.");
				e.setCancelled(true);
				return;
			}
			GameController.player.add(player.getUniqueId());
			GameController.spectador.remove(player.getUniqueId());
			e.setCancelled(false);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.getInventory().setItem(0, new ItemBuilder().type(Material.CHEST).name("§6Kits §7(Clique)").build());
			player.getInventory().setItem(1,
					new ItemBuilder().type(Material.ENDER_CHEST).name("§aHabilidades §7(Clique)").build());
			player.getInventory().setItem(8,
					new ItemBuilder().type(Material.BED).name("§cVoltar para o lobby §7(Clique)").build());
			player.updateInventory();
			Bukkit.broadcastMessage("§7" + player.getName() + " §eentrou na partida. (§b" + GameController.player.size()
					+ "§e/§b12§e)");
		}
	}

}
