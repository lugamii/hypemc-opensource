 package br.com.weavenmc.skywars.events;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Dispenser;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.actionbar.BarAPI;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.chat.ChatEvent;
import br.com.weavenmc.commons.bukkit.chat.ChatHandler;
import br.com.weavenmc.commons.bukkit.chat.ChatHandlerAPI;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.combatlog.Combat;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.inventorys.SpectatorInventory;
import br.com.weavenmc.skywars.nametag.TagAPI;
import br.com.weavenmc.skywars.player.PlayerController;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;

public class GameEvents implements Listener {

	public ArrayList<UUID> delay = new ArrayList<>();
	
	public GameEvents() {
		ChatHandlerAPI.addHandler(new ChatHandler() {

			@Override
			public void onChat(ChatEvent e) {
				Player player = e.getPlayer();
				if (GameController.spectador.contains(player.getUniqueId()) && !AdminMode.getInstance().isAdmin(player)) {
					e.setCancelled(true);
					player.sendMessage("§cVocê não pode falar após a morte.");
					return;
				} else {
					String tag = BukkitMain.getInstance().getTagManager().getTag(player).getPrefix();
					BukkitPlayer a = BukkitPlayer.getPlayer(player.getUniqueId());
					String league = " §7[" + a.getLeague().getColor() + a.getLeague().getSymbol() + "§7]";
					String m = e.getMessage();
					if (a.hasGroupPermission(Group.VIP))
						m = m.replace("&", "§");
					e.setFormat(tag + player.getName() + league + "§f: " + m.replace("%", "%%"));// |3 I 3
				}
			}	
		});
	}
	
	@EventHandler
	public void HP(EntityDamageByEntityEvent e) {
		if (e.getCause() != DamageCause.PROJECTILE)
			return;

		Projectile proj = (Projectile) e.getDamager();
		if (!(proj.getShooter() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		Player shooter = (Player) proj.getShooter();
		Player shot = (Player) e.getEntity();

		String hp = String.format(Locale.US, "%.1f", shot.getHealth());
		shooter.sendMessage("§7" + shot.getName() + " §eestá com §c" + hp + " HP§e.");
	}

	@EventHandler
	public void onBlock(BlockBreakEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
			if (GameController.spectador.contains(e.getPlayer().getUniqueId())
					&& !AdminMode.getInstance().isAdmin(e.getPlayer())) {
				e.setCancelled(true);
			} else {
				e.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onCreatureSpawnListener(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
			if (!GameController.player.contains(e.getPlayer().getUniqueId())) {
				e.setCancelled(true);
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
			} else {
				if ((e.hasItem()) && (e.getItem().getType() == Material.COMPASS)) {
					e.setCancelled(true);
					if (delay.contains(e.getPlayer().getUniqueId())) {
						return;
					}
					Player alvo = getRandomPlayer(e.getPlayer());
					BarAPI.send(e.getPlayer(), "§eApontando para o §c" + GameManager.getGroup(alvo));
					e.getPlayer().sendMessage("§eApontando para o §c" + GameManager.getGroup(alvo));
					delay.add(e.getPlayer().getUniqueId());
					new BukkitRunnable() {
						public void run() {
							delay.remove(e.getPlayer().getUniqueId());
						}
					}.runTaskLater(WeavenSkywars.getInstance(), 20L);
					return;
				}
				e.setCancelled(false);
			}
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		if (GameController.spectador.contains(((Player) e.getEntity()).getUniqueId())) {
			e.setCancelled(true);
			e.setFoodLevel(20);
			return;
		}
		e.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		e.setJoinMessage(null);
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY)
			return;
		for (int i = 0; i < 100; i++) {
			player.sendMessage(" ");
		}
		GameController.player.remove(player.getUniqueId());
		GameController.spectador.add(player.getUniqueId());
		PlayerController.kills.put(player.getUniqueId(), 0);
		WeavenSkywars.getGameManager().updatePlayerCheck(player);
		Scoreboarding.setScoreboard(player);
		Bukkit.getOnlinePlayers().forEach(players -> {
			if (GameController.player.contains(players.getUniqueId())) {
				players.hidePlayer(player);
			}
		});
		Bukkit.getScheduler().scheduleSyncDelayedTask(WeavenSkywars.getInstance(), () -> {
			WeavenSkywars.getGameManager().setSpectador(player);
		}, 2L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(WeavenSkywars.getInstance(), () -> {
			TagAPI.update(player);
		}, 5L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		e.setQuitMessage(null);
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		if (GameController.player.contains(player.getUniqueId())) {
			Bukkit.broadcastMessage("§e" + player.getName() + " desistiu da partida.");
		}
		GameController.player.remove(player.getUniqueId());
		GameController.spectador.remove(player.getUniqueId());
		GameController.check.remove(player.getUniqueId());
		AdminMode.getInstance().removeAdmin(player);
		WeavenSkywars.getGameManager().checkWinner();
	}

	@EventHandler
	public void onDamageEvent(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onDamageVoid(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player target = (Player) e.getEntity();
			if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
				if (e.getCause() == DamageCause.VOID) {
					if (GameController.player.contains(target.getUniqueId())) {
						if (WeavenSkywars.getGameManager().getHabilityAPI().getHabilidade(target) != Hability.FENIX) {
							e.setCancelled(true);
							if (Combat.inCombat(target)) {
								GameController.player.remove(target.getUniqueId());
								Player player = Bukkit
										.getPlayer(Combat.combatLogs.get(target.getUniqueId()));
								Bukkit.getOnlinePlayers().forEach(players -> {
									BarAPI.send(players,
											"§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
								});
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 2f);
								BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
								bPlayer.getData(DataType.SKYWARS_SOLO_KILLS)
										.setValue(bPlayer.getData(DataType.SKYWARS_SOLO_KILLS).asInt() + 1);
								bPlayer.getDataHandler().save(DataCategory.SKYWARS);
								PlayerController.kills.put(player.getUniqueId(),
										PlayerController.kills.get(player.getUniqueId()) + 1);
								Bukkit.broadcastMessage(
										"§7" + GameManager.getGroup(target) + "§e foi derrubado(a) pelo(a) §7"
												+ GameManager.getGroup(player) + "§e no void.");
								BukkitPlayer tPlayer = BukkitPlayer.getPlayer(target.getUniqueId());
								if (!tPlayer.hasGroupPermission(Group.TRIAL)) {
									WeavenSkywars.getGameManager().setSpectador(target);
								} else {
									AdminMode.getInstance().setAdmin(target);
								}
							} else {
								GameController.player.remove(target.getUniqueId());
								Bukkit.getOnlinePlayers().forEach(players -> {
									BarAPI.send(players,
											"§eHá §c" + GameController.player.size() + "§e jogadores restantes.");
								});
								Bukkit.broadcastMessage("§7" + GameManager.getGroup(target) + "§e caiu no void.");
								BukkitPlayer tPlayer = BukkitPlayer.getPlayer(target.getUniqueId());
								if (!tPlayer.hasGroupPermission(Group.TRIAL)) {
									WeavenSkywars.getGameManager().setSpectador(target);
								} else {
									AdminMode.getInstance().setAdmin(target);
								}
							}
						}
					} else {
						e.setCancelled(true);
					}
					WeavenSkywars.getGameManager().checkWinner();
				}
			}
		}
	}

	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME)
			return;
		if ((e.getDamager() instanceof Player)) {
			Player player = (Player) e.getDamager();
			if (GameController.spectador.contains(player.getUniqueId())) {
				e.setCancelled(true);
				return;
			}
			if (GameController.player.contains(player.getUniqueId())) {
				if ((e.getEntity() instanceof Player)) {
					Player target = (Player) e.getEntity();
					if (target == player)
						return;
					makeCombatLog(player, target);
					return;
				}
			}
		} else if ((e.getDamager() instanceof Projectile)) {
			Projectile projectile = (Projectile) e.getDamager();
			if (projectile.getShooter() instanceof Player) {
				Player player = (Player) projectile.getShooter();
				if (e.getEntity() instanceof Player) {
					Player target = (Player) e.getEntity();
					if (target == player)
						return;
					makeCombatLog(player, target);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (!GameController.player.contains(e.getPlayer().getUniqueId())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					BukkitPlayer pPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
					if (!pPlayer.hasGroupPermission(Group.TRIAL)) {
						WeavenSkywars.getGameManager().setSpectador(player);
					} else {
						AdminMode.getInstance().setAdmin(player);
					}
					WeavenSkywars.getGameManager().checkWinner();
				}
			}.runTaskLater(WeavenSkywars.getInstance(), 1L);
		} else {
			WeavenSkywars.getGameManager().getHabilityAPI().getHabilidade().remove(player.getUniqueId());
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 9999));
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			WeavenSkywars.getGameManager().getKitAPI().setItens(player);
			player.sendMessage("§eVocê ganhou uma segunda chance na vida graças à habilidade fênix.");
			Bukkit.getScheduler().scheduleSyncDelayedTask(WeavenSkywars.getInstance(), () -> {
				player.teleport(WeavenSkywars.getGameManager().getFenixLocation().get(player.getUniqueId()));
				WeavenSkywars.getGameManager().checkWinner();
			}, 2L);
		}
	}

	@EventHandler
	public void asd(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& GameController.spectador.contains(event.getPlayer().getUniqueId())) {
			Block b = event.getClickedBlock();
			if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper
					|| b.getState() instanceof Dispenser || b.getState() instanceof Furnace
					|| b.getState() instanceof Beacon) {
				if (!AdminMode.getInstance().isAdmin(event.getPlayer()))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void asd(PlayerCommandPreprocessEvent e) {
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME) {
			if (e.getMessage().equalsIgnoreCase("/tag")) {
				Player p = e.getPlayer();
				p.sendMessage("§cComando bloqueado nesse servidor.");
				e.setCancelled(true);
			}
		} else {
			if (e.getMessage().equalsIgnoreCase("/fake") || e.getMessage().equalsIgnoreCase("/tag")) {
				Player p = e.getPlayer();
				p.sendMessage("§cComando bloqueado no estágio jogo nesse modo.");
				e.setCancelled(true);
			}
		}
		return;
	}

	public void makeCombatLog(Player bateu, Player hitado) {
		if (Combat.inCombat(hitado)) {
			Combat.setCombat(bateu, hitado);
			System.out.println(
					"[Debug-CombatLog] " + bateu.getName() + " entrou novamente em combate contra " + hitado.getName());
		} else {
			Combat.setCombat(bateu, hitado);
			System.out
					.println("[Debug-CombatLog] " + bateu.getName() + " entrou em combate contra " + hitado.getName());
		}
	}

	public Player getRandomPlayer(Player p) {
		Player target = null;
		for (UUID uuid : GameController.player) {
			Player playerTarget = Bukkit.getPlayer(uuid);
			if (playerTarget != null) {
				if (!playerTarget.equals(p)) {
					if (playerTarget.getLocation().distance(p.getLocation()) >= 15.0D) {
						if (target == null) {
							target = playerTarget;
						} else {
							double distanciaAtual = target.getLocation().distance(p.getLocation());
							double novaDistancia = playerTarget.getLocation().distance(p.getLocation());
							if (distanciaAtual > novaDistancia)
								target = playerTarget;
						}
					}
				}
			}
		}
		return target;
	}

}
