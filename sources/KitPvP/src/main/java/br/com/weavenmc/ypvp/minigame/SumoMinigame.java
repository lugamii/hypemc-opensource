package br.com.weavenmc.ypvp.minigame;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeEnterEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerHideEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerShowEvent;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.event.SpectatorBattleEndEvent;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class SumoMinigame extends Minigame {

	private final HashMap<UUID, UUID> battle = new HashMap<>();
	private final HashMap<UUID, List<UUID>> normalChallenge = new HashMap<>();
	private UUID nextBattle = null;

	public SumoMinigame() {
		setName("Sumo");
		setOtherNames(new String[] { "OnevsOneSumo", "BattleSumo" });
		setTopKillStreakMinigame(false);
	}

	@Override
	public void join(Player p) {
		// BossBarAPI.removeBar(p);
		
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lSUMO");
		
		VanishAPI.getInstance().updateVanishToPlayer(p);
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		
		if (gamer.getWarp() != null)
			gamer.getWarp().quit(p);
		
		joinPlayer(p.getUniqueId());
		yPvP.getPlugin().getCooldownManager().removeCooldown(p);//
		yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));//
		gamer.setWarp(this);
		gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		p.getInventory().clear();
		teleport(p);
		// yPvP.getPlugin().getTournament().quitPlayer(p);
		
		p.getInventory().setArmorContents(null);
		
		ItemBuilder builder = new ItemBuilder().type(Material.BLAZE_ROD).name("§6§lConvidar");
		p.getInventory().setItem(3, builder.build());
		
		builder = new ItemBuilder().type(Material.INK_SACK).durability(8).name("§e§l1v1 Rápido");
		p.getInventory().setItem(5, builder.build());
		
		p.updateInventory();
		
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	public void callBattleEnd(Player player1, Player player2) {
		Bukkit.getPluginManager().callEvent(new SpectatorBattleEndEvent(player1, player2));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShow(PlayerShowEvent event) {
		Player show = event.getToShow();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(show.getUniqueId());
		if (gamer.hasSpectator()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShow(PlayerHideEvent event) {
		Player hide = event.getToHide();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(hide.getUniqueId());
		if (gamer.hasSpectator()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpectatorBattleEnd(SpectatorBattleEndEvent event) {
		for (Gamer gamer : yPvP.getPlugin().getGamerManager().getGamers()) {
			if (!gamer.hasSpectator())
				continue;
			if (gamer.getSpectator().equals(event.getPlayer1().getUniqueId())
					|| gamer.getSpectator().equals(event.getPlayer2().getUniqueId())) {
				Player player = Bukkit.getPlayer(gamer.getUniqueId());
				if (player == null)
					continue;
				gamer.setSpectator(null);
				teleport(player);
				player.sendMessage("§b§lESPECTAR§f O player acabou a luta!");
				VanishAPI.getInstance().updateVanishToPlayer(player);
			}
		}
	}

	public boolean isBattling(Player player) {
		return battle.containsKey(player.getUniqueId());
	}

	public Player getCurrentBattlePlayer(Player player) {
		return Bukkit.getPlayer(battle.get(player.getUniqueId()));
	}

	@EventHandler
	public void onAdminEnter(PlayerAdminModeEnterEvent event) {
		if (battle.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§4§lADMIN§f Você não pode entrar no Modo Admin durante uma batalha");
		}
	}

	@EventHandler
	public void onPlayerHideListener(PlayerHideEvent event) {
		Player hide = event.getToHide();
		if (battle.containsKey(hide.getUniqueId())) {
			if (battle.get(hide.getUniqueId()) == event.getPlayer().getUniqueId()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerShowListener(PlayerShowEvent event) {
		Player show = event.getToShow();
		if (battle.containsKey(show.getUniqueId())) {
			if (battle.get(show.getUniqueId()) != event.getPlayer().getUniqueId()) {
				event.setCancelled(true);
			}
		}
	}

	public String getBattlePlayer(Player p) {
		if (battle.containsKey(p.getUniqueId())) {
			Player battlePlayer = Bukkit.getPlayer(battle.get(p.getUniqueId()));
			if (battlePlayer != null) {
				return battlePlayer.getName();
			}
		}
		return "Ninguém";
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			ItemStack itemInHand = p.getItemInHand();
			if (itemInHand.getType() == Material.INK_SACK) {
				event.setCancelled(true);
				callFastBattle(p);
			}
		}
	}

	public synchronized void callFastBattle(Player p) {
		ItemStack itemInHand = p.getItemInHand();
		if (itemInHand.getType() == Material.INK_SACK) {
			if (itemInHand.hasItemMeta()) {
				if (itemInHand.getItemMeta().getDisplayName().equals("§e§l1v1 Rápido")) {
					if (nextBattle == null) {
						nextBattle = p.getUniqueId();
						p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
								.name("§e§lProcurando partidas").build());
						p.updateInventory();
						p.sendMessage("§eO 1v1 Rápido está procurando alguém para você batalhar!");
					} else {
						Player finded = Bukkit.getPlayer(nextBattle);
						if (finded != null) {
							if (finded.getUniqueId() != p.getUniqueId()) {
								nextBattle = null;
								clearRequests(finded, p);
								startNormalBattle(p, finded);
								finded.sendMessage(
										"§9O 1v1 Rápido encontrou alguém para você lutar! O player escolhido foi §e"
												+ p.getName());
								p.sendMessage(
										"§9O 1v1 Rápido encontrou alguém para você lutar! O player escolhido foi §e"
												+ finded.getName());
							} else {
								nextBattle = p.getUniqueId();
								p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
										.name("§e§lProcurando partidas").build());
								p.updateInventory();
								p.sendMessage("§eO 1v1 Rápido está procurando alguém para você batalhar!");
							}
						} else {
							nextBattle = p.getUniqueId();
							p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
									.name("§e§lProcurando partidas").build());
							p.updateInventory();
							p.sendMessage("§eO 1v1 Rápido está procurando alguém para você batalhar!");
						}
					}
				} else {
					if (nextBattle == p.getUniqueId())
						nextBattle = null;
					p.setItemInHand(
							new ItemBuilder().type(Material.INK_SACK).durability(8).name("§e§l1v1 Rápido").build());
					p.updateInventory();
					p.sendMessage("§eO 1v1 Rápido parou de procurar alguém para você batalhar!");
				}
			}
		}
	}

	HashMap<Player, Long> cooldown = new HashMap<Player, Long>();

	@EventHandler
	public void onChallenge(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player p = event.getPlayer();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			ItemStack itemInHand = p.getItemInHand();
			if (gamer.getWarp() == this) {
				if (!battle.containsKey(p.getUniqueId())) {
					if (!hasCooldown(p)) {
						Player challenged = (Player) event.getRightClicked();
						Gamer g = yPvP.getPlugin().getGamerManager().getGamer(challenged.getUniqueId());
						if (g.getWarp() == this) {
							if (!battle.containsKey(challenged.getUniqueId())) {
								if (itemInHand.getType() == Material.BLAZE_ROD) {
									event.setCancelled(true);
									if (normalChallenge.containsKey(p.getUniqueId()) && normalChallenge
											.get(p.getUniqueId()).contains(challenged.getUniqueId())) {
										clearRequests(p, challenged);
										startNormalBattle(challenged, p);
										challenged.sendMessage("§b" + p.getName() + "§2 aceitou seu desafio");
										p.sendMessage("§2Você aceitou o desafio de §b" + challenged.getName());
									} else {
										addCooldown(p, 5);
										p.sendMessage("§7Você enviou um desafio de 1v1 normal para §b"
												+ challenged.getName());
										challenged.sendMessage(
												"§eVocê recebeu desafio de 1v1 normal de §7" + p.getName());
										List<UUID> challengers = normalChallenge.containsKey(challenged.getUniqueId())
												? normalChallenge.get(challenged.getUniqueId())
												: new ArrayList<>();
										challengers.add(p.getUniqueId());
										normalChallenge.put(challenged.getUniqueId(), challengers);
										new BukkitRunnable() {
											@Override
											public void run() {
												if (normalChallenge.containsKey(challenged.getUniqueId())) {
													normalChallenge.get(challenged.getUniqueId())
															.remove(p.getUniqueId());
												}
											}
										}.runTaskLater(yPvP.getPlugin(), 5 * 20);
									}
								}
							}
						}
					} else {
						p.sendMessage("§cAguarde para desafiar novamente!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() == this) {
				if (battle.containsKey(p.getUniqueId())) {
					if (event.getCause() == DamageCause.VOID) {
						event.setCancelled(true);
						p.damage(22.0, getCurrentBattlePlayer(p));
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamageHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player e = (Player) event.getEntity();
			Player d = (Player) event.getDamager();
			Gamer entity = yPvP.getPlugin().getGamerManager().getGamer(e.getUniqueId());
			Gamer damager = yPvP.getPlugin().getGamerManager().getGamer(d.getUniqueId());
			if (entity.getWarp() == this || damager.getWarp() == this) {
				if (!battle.containsKey(e.getUniqueId())) {
					event.setCancelled(true);
				} else if (!battle.containsKey(d.getUniqueId())) {
					event.setCancelled(true);
				} else if (battle.containsKey(e.getUniqueId()) && battle.containsKey(d.getUniqueId())) {
					if (battle.get(d.getUniqueId()) != e.getUniqueId()
							&& battle.get(e.getUniqueId()) != d.getUniqueId()) {
						event.setCancelled(true);
					} else {
						event.setDamage(0.0);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBattleLogout(PlayerQuitEvent event) {
		Player logout = event.getPlayer();
		if (battle.containsKey(logout.getUniqueId())) {
			BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(logout.getUniqueId());
			int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
			bPLoser.getData(DataType.PVP_DEATHS).setValue(deaths += 1);
			Player winner = Bukkit.getPlayer(battle.get(logout.getUniqueId()));
			
			callBattleEnd(logout, winner);
			
			winner.sendMessage("§c" + logout.getName() + " deslogou.");
			battle.remove(logout.getUniqueId());
			battle.remove(winner.getUniqueId());
			
			BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(winner.getUniqueId());

			bPWinner.addXp(5);
			bPWinner.addMoney(10);
			winner.sendMessage("§cVocê venceu a batalha SUMO contra " + logout.getName());
			winner.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
			winner.sendMessage(
					"§9§lXP§f Você recebeu §9§l" + 5 + " XPs" + (bPWinner.isDoubleXPActived() ? " §7(doublexp)" : ""));
			int kills = bPWinner.getData(DataType.PVP_KILLS).asInt();
			bPWinner.getData(DataType.PVP_KILLS).setValue(kills += 1);
			
			bPLoser.save(DataCategory.KITPVP);
			bPWinner.save(DataCategory.BALANCE, DataCategory.KITPVP);
			join(winner);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLose(PlayerDeathEvent event) {
		Player loser = event.getEntity();
		EntityPlayer l = ((CraftPlayer) loser).getHandle();
		
		if (battle.containsKey(loser.getUniqueId())) {
			event.getDrops().clear();
			
			BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(loser.getUniqueId());
			int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
			bPLoser.getData(DataType.PVP_DEATHS).setValue(deaths += 1);
			
			Player winner = Bukkit.getPlayer(battle.get(loser.getUniqueId()));
			
			loser.sendMessage("§cVocê perdeu a batalha SUMO para o " + loser.getName());
			
			callBattleEnd(loser, winner);
			l.killer = ((CraftPlayer) winner).getHandle();
			
			battle.remove(loser.getUniqueId());
			battle.remove(winner.getUniqueId());
			
			BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(winner.getUniqueId());

			bPWinner.addXp(5);
			bPWinner.addMoney(10);
			winner.sendMessage("§cVocê venceu a batalha SUMO contra " + loser.getName());
			winner.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
			winner.sendMessage(
					"§9§lXP§f Você recebeu §9§l" + 5 + " XPs" + (bPWinner.isDoubleXPActived() ? " §7(doublexp)" : ""));
			int kills = bPWinner.getData(DataType.PVP_KILLS).asInt();
			bPWinner.getData(DataType.PVP_KILLS).setValue(kills += 1);
			
			bPLoser.save(DataCategory.KITPVP);
			bPWinner.save(DataCategory.BALANCE, DataCategory.KITPVP);
			join(winner);
		}
	}

	@EventHandler
	public void onJoinListener(PlayerJoinEvent event) {
		Player joined = event.getPlayer();
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (!battle.containsKey(o.getUniqueId()))
				continue;
			o.hidePlayer(joined);
		}
		joined = null;
	}

	public void addCooldown(Player p, int segs) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, segs);
		cooldown.put(p, calendar.getTimeInMillis());
	}

	public boolean hasCooldown(Player p) {
		if (cooldown.containsKey(p))
			if (new Date().after(new Date(cooldown.get(p)))) {
				cooldown.remove(p);
				return false;
			} else {
				return true;
			}
		return false;
	}

	@EventHandler
	public void onQuitListener(PlayerQuitEvent event) {
		Player quited = event.getPlayer();
		clearRequests(quited);
		quited = null;
	}

	public void startNormalBattle(Player p1, Player p2) {
		battle.put(p1.getUniqueId(), p2.getUniqueId());
		battle.put(p2.getUniqueId(), p1.getUniqueId());
		
		Location pos1 = yPvP.getPlugin().getLocationManager().getLocation("sumoloc1");
		Location pos2 = yPvP.getPlugin().getLocationManager().getLocation("sumoloc2");
		
		prepareNormalBattle(p1);
		prepareNormalBattle(p2);
		
		if (pos1 != null && pos2 != null) {
			p1.teleport(pos1);
			p2.teleport(pos2);
		}
		
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() == p1.getUniqueId() || o.getUniqueId() == p2.getUniqueId())
				continue;
			p1.hidePlayer(o);
			p2.hidePlayer(o);
		}
		
		p1.showPlayer(p2);
		p2.showPlayer(p1);
	}

	private void prepareNormalBattle(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		p.setNoDamageTicks(60);
		p.setFallDistance(-5);
		
		p.updateInventory();
	}

	public void clearRequests(Player... players) {
		for (Player p : players) {
			if (nextBattle == p.getUniqueId())
				nextBattle = null;
			
			if (normalChallenge.containsKey(p.getUniqueId()))
				normalChallenge.remove(p.getUniqueId());
			//
		}
	}

	@Override
	public void quit(Player p) {
		clearRequests(p);
		quitPlayer(p.getUniqueId());
	}
}