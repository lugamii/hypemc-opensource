package br.com.weavenmc.skywars.events;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.inventorys.HabilitySelector;
import br.com.weavenmc.skywars.inventorys.KitSelector;
import br.com.weavenmc.skywars.kit.KitAPI.Kits;
import br.com.weavenmc.skywars.player.PlayerController;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;
import br.com.weavenmc.timer.Iniciando;

public class StartingEvents implements Listener {

	public static ArrayList<UUID> noMessage = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent e) {
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.load(DataCategory.SKYWARS)) {
				e.disallow(Result.KICK_OTHER, "§c§lERRO §fNão foi possível carregar as informações.");
			}
		});
		if (GameController.player.size() >= 12) {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				e.disallow(Result.KICK_FULL, "§c§lERRO §fO servidor está lotado.");
			}
		}
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				e.disallow(Result.KICK_WHITELIST, "§c§lERRO §fVocê não pode entrar no estágio jogo.");
			}
		}
		if (WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				e.disallow(Result.KICK_WHITELIST, "§c§lERRO §fVocê não pode entrar no estágio jogo.");
			}
		}
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY && Iniciando.timer <= 3) {
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(e.getPlayer().getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				e.disallow(Result.KICK_WHITELIST, "§c§lERRO §fPreparos finais para a inicialização.");
			}
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY)
			return;
		e.setFoodLevel(20);
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			e.setCancelled(true);
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onBlock(BlockPlaceEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			e.setCancelled(true);
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onBlock(BlockBreakEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			e.setCancelled(true);
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.CHEST)) {
				KitSelector.openKitsMenu(e.getPlayer(), 1);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1f, 1f);
				return;
			}
			if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.ENDER_CHEST)) {
				HabilitySelector.openKitsMenu(e.getPlayer(), 1);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1f, 1f);
				return;
			}
			if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.BED)) {
				WeavenSkywars.getGameManager().sendLobby(e.getPlayer());
				return;
			}
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY)
			return;
		for (int i = 0; i < 100; i++) {
			player.sendMessage(" ");
		}
		if (GameController.player.size() < 12) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.getInventory().setItem(0, new ItemBuilder().type(Material.CHEST).name("§6Kits §7(Clique)").build());
			player.getInventory().setItem(1,
					new ItemBuilder().type(Material.ENDER_CHEST).name("§aHabilidades §7(Clique)").build());
			player.getInventory().setItem(8,
					new ItemBuilder().type(Material.BED).name("§cVoltar para o lobby §7(Clique)").build());
			e.setJoinMessage(null);
			player.setGameMode(GameMode.ADVENTURE);
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
			if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
				noMessage.add(player.getUniqueId());
			}
			GameController.player.add(player.getUniqueId());
			GameController.spectador.remove(player.getUniqueId());
			PlayerController.kills.put(player.getUniqueId(), 0);
			WeavenSkywars.getGameManager().updatePlayerCheck(player);
			player.teleport(WeavenSkywars.getGameManager().getLobby());
			Scoreboarding.setScoreboard(player);
			WeavenSkywars.getGameManager().getKitAPI().setKit(player, Kits.PADRAO);
			if (noMessage.contains(player.getUniqueId())) {
				Bukkit.broadcastMessage("§7" + player.getName() + " §eentrou na partida. (§b"
						+ GameController.player.size() + "§e/§b12§e)");
			}
		} else {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.getInventory().setItem(0, new ItemBuilder().type(Material.CHEST).name("§6Kits §7(Clique)").build());
			player.getInventory().setItem(1,
					new ItemBuilder().type(Material.ENDER_CHEST).name("§aHabilidades §7(Clique)").build());
			player.getInventory().setItem(8,
					new ItemBuilder().type(Material.BED).name("§cVoltar para o lobby §7(Clique)").build());
			e.setJoinMessage(null);
			player.setGameMode(GameMode.ADVENTURE);
			GameController.player.remove(player.getUniqueId());
			GameController.spectador.add(player.getUniqueId());
			PlayerController.kills.put(player.getUniqueId(), 0);
			WeavenSkywars.getGameManager().updatePlayerCheck(player);
			Bukkit.getScheduler().scheduleSyncDelayedTask(WeavenSkywars.getInstance(), () -> {
				player.teleport(WeavenSkywars.getGameManager().getLobby());
			}, 1L);
			Scoreboarding.setScoreboard(player);
			WeavenSkywars.getGameManager().getKitAPI().setKit(player, Kits.PADRAO);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY)
			return;
		GameController.player.remove(player.getUniqueId());
		GameController.spectador.remove(player.getUniqueId());
		GameController.check.remove(player.getUniqueId());
		if (!AdminMode.getInstance().isAdmin(player)) {
			Bukkit.broadcastMessage(
					"§7" + player.getName() + " §esaiu da partida. (§b" + GameController.player.size() + "§e/§b12§e)");
		}
		AdminMode.getInstance().removeAdmin(player);
	}

	@EventHandler
	public void onDamageEvent(EntityDamageEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}

}
