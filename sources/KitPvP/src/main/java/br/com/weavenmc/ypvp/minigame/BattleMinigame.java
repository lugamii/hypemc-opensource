package br.com.weavenmc.ypvp.minigame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
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
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.yPvP.PvPType;
import br.com.weavenmc.ypvp.event.SpectatorBattleEndEvent;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class BattleMinigame extends Minigame {

	private final HashMap<UUID, UUID> battle = new HashMap<>();
	private final HashMap<UUID, List<UUID>> normalChallenge = new HashMap<>();
	private final HashMap<UUID, UUID> customChallenge = new HashMap<>();
	private UUID nextBattle = null;

	// custom
	private final HashMap<UUID, Material> armourType = new HashMap<>();
	private final HashMap<UUID, Material> swordType = new HashMap<>();
	private final HashMap<UUID, Material> recraftType = new HashMap<>();
	private final HashMap<UUID, Boolean> recraftOption = new HashMap<>();
	private final HashMap<UUID, Boolean> sharpOption = new HashMap<>();
	private final HashMap<UUID, Boolean> fullSoupOption = new HashMap<>();
	private final HashMap<UUID, UUID> customCalling = new HashMap<>();

	public BattleMinigame() {
		setName("1v1");
		setOtherNames(new String[] { "OnevsOne", "Battle" });
		setTopKillStreakMinigame(true);
	}

	@Override
	public void join(Player p) {
		// BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§l1v1");
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
		ItemBuilder builder = new ItemBuilder().type(Material.BLAZE_ROD).name("§6§l1v1 Normal");
		p.getInventory().setItem(3, builder.build());
		builder = new ItemBuilder().type(Material.IRON_FENCE).name("§b§l1v1 Customizado");
		p.getInventory().setItem(4, builder.build());
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
								clearCustom(p, finded);
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
										clearCustom(challenged, p);
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
								} else if (itemInHand.getType() == Material.IRON_FENCE) {
									if (customChallenge.containsKey(p.getUniqueId())
											&& customChallenge.get(p.getUniqueId()).equals(challenged.getUniqueId())) {
										clearRequests(challenged, p);
										startCustomBattle(challenged, p);
										challenged.sendMessage("§b" + p.getName() + "§2 aceitou seu desafio");
										p.sendMessage("§2Você aceitou o desafio de §b" + challenged.getName());
									} else {
										customCalling.put(p.getUniqueId(), challenged.getUniqueId());
										customCalling.put(challenged.getUniqueId(), p.getUniqueId());
										openCustomInventoryFor(p, challenged);
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

	public void defautCustom(Player p) {
		armourType.put(p.getUniqueId(), Material.LEATHER_CHESTPLATE);
		swordType.put(p.getUniqueId(), Material.WOOD_SWORD);
		recraftType.put(p.getUniqueId(), Material.RED_MUSHROOM);
		recraftOption.put(p.getUniqueId(), false);
		sharpOption.put(p.getUniqueId(), true);
		fullSoupOption.put(p.getUniqueId(), false);
	}

	public void openCustomInventoryFor(Player p, Player challenged) {
		defautCustom(p);
		Inventory menu = Bukkit.createInventory(p, 54, "§c1v1 contra " + challenged.getName());
		for (int i = 0; i < 54; i++)
			menu.setItem(i, new ItemBuilder().type(Material.STAINED_GLASS_PANE).name("§b§l-").durability(8).build());
		ItemBuilder builder = new ItemBuilder().type(Material.WOOL).name("§a§lDesafiar Jogador").durability(5);
		menu.setItem(43, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("§a§lDesafiar Jogador").durability(5);
		menu.setItem(44, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("§a§lDesafiar Jogador").durability(5);
		menu.setItem(52, builder.build());
		builder = new ItemBuilder().type(Material.WOOL).name("§a§lDesafiar Jogador").durability(5);
		menu.setItem(53, builder.build());
		if (swordType.containsKey(p.getUniqueId())) {
			Material sword = swordType.get(p.getUniqueId());
			builder = new ItemBuilder().type(sword);
			if (sword == Material.WOOD_SWORD)
				builder.name("§6Espada de Madeira").lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "");
			else if (sword == Material.STONE_SWORD)
				builder.name("§6Espada de Pedra").lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "");
			else if (sword == Material.IRON_SWORD)
				builder.name("§6Espada de Ferro").lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "");
			else if (sword == Material.DIAMOND_SWORD)
				builder.name("§6Espada de Diamante").lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "");
			menu.setItem(20, builder.build());
		}
		if (armourType.containsKey(p.getUniqueId())) {
			Material armour = armourType.get(p.getUniqueId());
			builder = new ItemBuilder().type(armour);
			if (armour == Material.LEATHER_CHESTPLATE)
				builder.name("§eArmadura de Couro").lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "");
			else if (armour == Material.IRON_CHESTPLATE)
				builder.name("§eArmadura de Ferro").lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "");
			else if (armour == Material.DIAMOND_CHESTPLATE)
				builder.name("§eArmadura de Diamente").lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!",
						"");
			else if (armour == Material.GOLD_HELMET)
				builder.name("§eSem armadura").lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "");
			menu.setItem(21, builder.build());
		}
		if (recraftType.containsKey(p.getUniqueId())) {
			Material recraft = recraftType.get(p.getUniqueId());
			builder = new ItemBuilder().type(recraft);
			if (recraft == Material.RED_MUSHROOM)
				builder.name("§bRecrafts de Cogumelo").lore("§3Clique aqui para mudar", "§3o tipo de seu recraft!", "");
			else if (recraft == Material.COCOA)
				builder.name("§bRecrafts de Cocoabean").lore("§3Clique aqui para mudar", "§3o tipo de seu recraft!",
						"");
			menu.setItem(22, builder.build());
		}
		if (recraftOption.containsKey(p.getUniqueId())) {
			if (recraftOption.get(p.getUniqueId()))
				builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).name("§aCom Recraft")
						.lore("§3Clique aqui para", "§3desativar o recraft!", "");
			else
				builder = new ItemBuilder().type(Material.MAGMA_CREAM).name("§cSem Recraft").lore("§3Clique aqui para",
						"§3ativar o recraft!", "");
			menu.setItem(23, builder.build());
		}
		if (sharpOption.containsKey(p.getUniqueId())) {
			if (sharpOption.get(p.getUniqueId()))
				builder = new ItemBuilder().type(Material.ENCHANTED_BOOK).name("§3Com Sharpness")
						.lore("§3Clique aqui para", "§3tirar a afiaçao da espada!", "");
			else
				builder = new ItemBuilder().type(Material.BOOK).name("§3Sem Sharpness").lore("§3Clique aqui para",
						"§3colocar afiaçao na espada!", "");
			menu.setItem(24, builder.build());
		}
		if (fullSoupOption.containsKey(p.getUniqueId())) {
			if (fullSoupOption.get(p.getUniqueId()))
				builder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§2Full Sopa").lore("§3Clique aqui para",
						"§3usar 1 hotbar apenas", "");
			else
				builder = new ItemBuilder().type(Material.BOWL).name("§21 Hotbar").lore("§3Clique aqui para",
						"§3usar full sopa", "");
			menu.setItem(29, builder.build());
		}
		p.openInventory(menu);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() == this) {
				if (battle.containsKey(p.getUniqueId())) {
					if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.LAVA
							|| event.getCause() == DamageCause.FIRE_TICK) {
						event.setCancelled(true);
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
					}
				}
			}
		}
	}

	public int itemsInInventory(Inventory inventory, Material... search) {
		List<Material> wanted = Arrays.asList(search);
		int found = 0;
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = inventory.getContents()).length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			if ((item != null) && (wanted.contains(item.getType()))) {
				found += item.getAmount();
			}
		}
		return found;
	}

	@EventHandler
	public void onCombatCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (battle.containsKey(p.getUniqueId())) {
			event.setCancelled(true);
			p.sendMessage("§b§l1V1§f Você não pode executar comandos durante a batalha!");
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
			checkLostKs(logout, winner, bPLoser.getData(DataType.PVP_KILLSTREAK).asInt());
			bPLoser.getData(DataType.PVP_KILLSTREAK).setValue(0);
			BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(winner.getUniqueId());
			int streak = bPWinner.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
			bPWinner.getData(DataType.PVP_KILLSTREAK).setValue(streak);
			int maxStreak = bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
			if (streak > maxStreak)
				bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).setValue(streak);
			int xp = calculateXp(bPWinner, bPLoser);
			bPWinner.addXp(xp);
			bPWinner.addMoney(80);
			winner.sendMessage("§e§lKILL§f Você matou §e§l" + logout.getName());
			winner.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
			winner.sendMessage(
					"§9§lXP§f Você recebeu §9§l" + xp + " XPs" + (bPWinner.isDoubleXPActived() ? " §7(doublexp)" : ""));
			int kills = bPWinner.getData(DataType.PVP_KILLS).asInt();
			bPWinner.getData(DataType.PVP_KILLS).setValue(kills += 1);
			checkKs(winner, streak);
			bPLoser.save(DataCategory.KITPVP);
			bPWinner.save(DataCategory.BALANCE, DataCategory.KITPVP);
			join(winner);
		}
	}

	public int calculateXp(WeavenPlayer receiver, WeavenPlayer wP) {
		double result = 5;
		// pvp calculating
		int kills = wP.getData(DataType.PVP_KILLS).asInt();
		int deaths = wP.getData(DataType.PVP_DEATHS).asInt();
		if (kills != 0 && deaths != 0)
			result += Double.valueOf(kills / deaths);
		int battleWins = wP.getData(DataType.PVP_1V1_KILLS).asInt();
		int battleLoses = wP.getData(DataType.PVP_1V1_DEATHS).asInt();
		if (battleWins != 0 && battleLoses != 0)
			result += battleWins / battleLoses;
		// league calculating
		result += Double.valueOf(wP.getLeague().ordinal() / 2);
		// hg calculating
		int hgWins = wP.getData(DataType.HG_WINS).asInt();
		int hgDeaths = wP.getData(DataType.HG_DEATHS).asInt();
		if (hgWins != 0 && hgDeaths != 0)
			result += hgWins / hgDeaths;
		int gladWins = wP.getData(DataType.GLADIATOR_WINS).asInt();
		int gladDeaths = wP.getData(DataType.GLADIATOR_LOSES).asInt();
		if (gladWins != 0 && gladDeaths != 0)
			result += gladWins / gladDeaths;
		if ((int) result <= 0)
			result = 5;
		if (receiver.isDoubleXPActived())
			result = result * 2;
		return (int) result;
	}

	public void checkKs(Player p, int ks) {
		if (ks < 10)
			return;
		if (String.valueOf(ks).endsWith("0") || String.valueOf(ks).endsWith("5")) {
			Bukkit.broadcastMessage("§4§lKILLSTREAK §1§l" + p.getName() + " §fconseguiu um §6§lKILLSTREAK DE " + ks);
		}
	}

	public void checkLostKs(Player p, Player k, int ks) {
		if (ks < 10)
			return;
		Bukkit.broadcastMessage("§4§lKILLSTREAK §1§l" + p.getName() + "§f perdeu seu §6§lKILLSTREAK DE " + ks
				+ " PARA §c§l" + k.getName());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLose(PlayerDeathEvent event) {
		Player loser = event.getEntity();
		EntityPlayer l = ((CraftPlayer) loser).getHandle();
		if (battle.containsKey(loser.getUniqueId())) {
			event.getDrops().clear();
			Player winner = Bukkit.getPlayer(battle.get(loser.getUniqueId()));
			callBattleEnd(loser, winner);
			l.killer = ((CraftPlayer) winner).getHandle();
			String restingLife = StringTimeUtils.toMillis(winner.getHealth() / 2);
			int restingSoups = itemsInInventory(winner.getInventory(), Material.MUSHROOM_SOUP);
			winner.sendMessage("§cVocê venceu o 1v1 contra " + loser.getName() + " com " + restingLife + " coraçoes e "
					+ restingSoups + " sopas restantes");
			loser.sendMessage("§c" + winner.getName() + " venceu o 1v1 com " + restingLife + " coraçoes e "
					+ restingSoups + " sopas restantes");
			battle.remove(winner.getUniqueId());
			battle.remove(loser.getUniqueId());
			join(winner);
		}
	}

	@SuppressWarnings("deprecation")
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
		clearCustom(quited);
		quited = null;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();
			if (customCalling.containsKey(p.getUniqueId())) {
				customCalling.remove(p.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onInventoryClickListener(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			if (customCalling.containsKey(p.getUniqueId())) {
				ItemStack currentItem = event.getCurrentItem();
				if (currentItem != null) {
					int currentSlot = event.getSlot();
					Player t = Bukkit.getPlayer(customCalling.get(p.getUniqueId()));
					if (t != null) {
						Inventory menu = event.getInventory();
						if (menu.getName().equalsIgnoreCase("§c1v1 contra " + t.getName())) {
							event.setCancelled(true);
							// armourType.put(p.getUniqueId(), Material.LEATHER_CHESTPLATE);
							// swordType.put(p.getUniqueId(), Material.WOOD_SWORD);
							// recraftType.put(p.getUniqueId(), Material.RED_MUSHROOM);
							// recraftOption.put(p.getUniqueId(), false);
							// sharpOption.put(p.getUniqueId(), true);
							// fullSoupOption.put(p.getUniqueId(), false);
							if (currentItem.getType() == Material.WOOD_SWORD) {
								swordType.put(p.getUniqueId(), Material.STONE_SWORD);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.STONE_SWORD).name("§6Espada de Pedra")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "")
												.build());
							} else if (currentItem.getType() == Material.STONE_SWORD) {
								swordType.put(p.getUniqueId(), Material.IRON_SWORD);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.IRON_SWORD).name("§6Espada de Ferro")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "")
												.build());
							} else if (currentItem.getType() == Material.IRON_SWORD) {
								swordType.put(p.getUniqueId(), Material.DIAMOND_SWORD);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.DIAMOND_SWORD).name("§6Espada de Diamente")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "")
												.build());
							} else if (currentItem.getType() == Material.DIAMOND_SWORD) {
								swordType.put(p.getUniqueId(), Material.WOOD_SWORD);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.WOOD_SWORD).name("§6Espada de Madeira")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua espada!", "")
												.build());
							} else if (currentItem.getType() == Material.LEATHER_CHESTPLATE) {
								armourType.put(p.getUniqueId(), Material.IRON_CHESTPLATE);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.IRON_CHESTPLATE).name("§eArmadura de Ferro")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "")
												.build());
							} else if (currentItem.getType() == Material.IRON_CHESTPLATE) {
								armourType.put(p.getUniqueId(), Material.DIAMOND_CHESTPLATE);
								menu.setItem(currentSlot, new ItemBuilder().type(Material.DIAMOND_CHESTPLATE)
										.name("§eArmadura de Diamante")
										.lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "").build());
							} else if (currentItem.getType() == Material.DIAMOND_CHESTPLATE) {
								armourType.put(p.getUniqueId(), Material.GOLD_HELMET);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.GOLD_HELMET).name("§eSem Armadura")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "")
												.build());
							} else if (currentItem.getType() == Material.GOLD_HELMET) {
								armourType.put(p.getUniqueId(), Material.LEATHER_CHESTPLATE);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.LEATHER_CHESTPLATE).name("§eArmadura de Couro")
												.lore("§3Clique aqui para mudar", "§3o tipo de sua armadura!", "")
												.build());
							} else if (currentItem.getType() == Material.RED_MUSHROOM) {
								recraftType.put(p.getUniqueId(), Material.CACTUS);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.CACTUS).name("§bRecrafts de Cocoabean")
												.lore("§3Clique aqui para mudar", "§3o tipo de seu recraft!", "")
												.build());
							} else if (currentItem.getType() == Material.CACTUS) {
								recraftType.put(p.getUniqueId(), Material.RED_MUSHROOM);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.RED_MUSHROOM).name("§bRecrafts de Cogumelo")
												.lore("§3Clique aqui para mudar", "§3o tipo de seu recraft!", "")
												.build());
							} else if (currentItem.getType() == Material.BROWN_MUSHROOM) {
								recraftOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.MAGMA_CREAM).name("§cSem Recraft")
												.lore("§3Clique aqui para", "§3ativar o recraft!", "").build());
							} else if (currentItem.getType() == Material.MAGMA_CREAM) {
								recraftOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.BROWN_MUSHROOM).name("§aCom Recraft")
												.lore("§3Clique aqui para", "§3desativar o recraft!", "").build());
							} else if (currentItem.getType() == Material.ENCHANTED_BOOK) {
								sharpOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot, new ItemBuilder().type(Material.BOOK).name("§3Sem Sharpness")
										.lore("§3Clique aqui para", "§3colocar afiaçao na espada!", "").build());
							} else if (currentItem.getType() == Material.BOOK) {
								sharpOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.ENCHANTED_BOOK).name("§3Com Sharpness")
												.lore("§3Clique aqui para", "§3tirar a afiaçao da espada!", "")
												.build());
							} else if (currentItem.getType() == Material.MUSHROOM_SOUP) {
								fullSoupOption.put(p.getUniqueId(), false);
								menu.setItem(currentSlot, new ItemBuilder().type(Material.BOWL).name("§21 Hotbar")
										.lore("§3Clique aqui para", "§3usar full sopa", "").build());
							} else if (currentItem.getType() == Material.BOWL) {
								fullSoupOption.put(p.getUniqueId(), true);
								menu.setItem(currentSlot,
										new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§2Full Sopa")
												.lore("§3Clique aqui para", "§3usar 1 hotbar apenas", "").build());
							} else if (currentItem.getType() == Material.WOOL) {
								p.closeInventory();
								addCooldown(p, 5);
								customChallenge.put(t.getUniqueId(), p.getUniqueId());
								p.sendMessage("§7Você enviou um desafio de 1v1 customizado para §b" + t.getName());
								t.sendMessage("§eVocê recebeu desafio de 1v1 customizado de §7" + p.getName());
								new BukkitRunnable() {
									@Override
									public void run() {
										if (customChallenge.containsKey(t.getUniqueId())
												&& customChallenge.get(t.getUniqueId()) == p.getUniqueId()) {
											customChallenge.remove(t.getUniqueId());
										}
									}
								}.runTaskLater(yPvP.getPlugin(), 5 * 20);
							}
						}
					} else {
						p.closeInventory();
						customCalling.remove(p.getUniqueId());
						p.sendMessage("§cO jogador desafiado não foi encontrado.");
					}
				}
			}
		}
	}

	public void startCustomBattle(Player custommer, Player challenged) {
		battle.put(custommer.getUniqueId(), challenged.getUniqueId());
		battle.put(challenged.getUniqueId(), custommer.getUniqueId());
		Location pos1 = yPvP.getPlugin().getLocationManager().getLocation("1v1loc1");
		Location pos2 = yPvP.getPlugin().getLocationManager().getLocation("1v1loc2");
		if (pos1 != null && pos2 != null) {
			custommer.teleport(pos1);
			challenged.teleport(pos2);
		}
		prepareCustomBattle(custommer, challenged);
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() == custommer.getUniqueId() || o.getUniqueId() == challenged.getUniqueId())
				continue;
			custommer.hidePlayer(o);
			challenged.hidePlayer(o);
		}
		custommer.showPlayer(challenged);
		challenged.showPlayer(custommer);
	}

	@SuppressWarnings("deprecation")
	public void startNormalBattle(Player p1, Player p2) {
		battle.put(p1.getUniqueId(), p2.getUniqueId());
		battle.put(p2.getUniqueId(), p1.getUniqueId());
		Location pos1 = yPvP.getPlugin().getLocationManager().getLocation("1v1loc1");
		Location pos2 = yPvP.getPlugin().getLocationManager().getLocation("1v1loc2");
		if (pos1 != null && pos2 != null) {
			p1.teleport(pos1);
			p2.teleport(pos2);
		}
		prepareNormalBattle(p1);
		prepareNormalBattle(p2);
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() == p1.getUniqueId() || o.getUniqueId() == p2.getUniqueId())
				continue;
			p1.hidePlayer(o);
			p2.hidePlayer(o);
		}
		p1.showPlayer(p2);
		p2.showPlayer(p1);
	}

	private void prepareCustomBattle(Player customer, Player challenged) {
		customer.getInventory().clear();
		challenged.getInventory().clear();
		customer.getInventory().setArmorContents(null);
		challenged.getInventory().setArmorContents(null);
		if (fullSoupOption.get(customer.getUniqueId())) {
			for (int i = 0; i < 36; i++)
				customer.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
			for (int i = 0; i < 36; i++)
				challenged.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		} else {
			for (int i = 1; i < 9; i++)
				customer.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
			for (int i = 1; i < 9; i++)
				challenged.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
		}
		ItemBuilder builder = new ItemBuilder().type(swordType.get(customer.getUniqueId()));
		if (sharpOption.get(customer.getUniqueId()))
			builder.enchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack itemStack = builder.build();
		customer.getInventory().setItem(0, itemStack);
		challenged.getInventory().setItem(0, itemStack);
		//
		Material armour = armourType.get(customer.getUniqueId());
		if (armour == Material.LEATHER_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			//
			challenged.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		} else if (armour == Material.IRON_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
			//
			challenged.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		} else if (armour == Material.DIAMOND_CHESTPLATE) {
			customer.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			customer.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			customer.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			customer.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			//
			challenged.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			challenged.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			challenged.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			challenged.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		} else if (armour == Material.GOLD_HELMET) {
			// no armours
		}
		if (recraftOption.get(customer.getUniqueId())) {
			if (recraftType.get(customer.getUniqueId()) == Material.RED_MUSHROOM) {
				customer.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				customer.getInventory().setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
				customer.getInventory().setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));
				//
				challenged.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				challenged.getInventory().setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
				challenged.getInventory().setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));
			} else if (recraftType.get(customer.getUniqueId()) == Material.CACTUS) {
				customer.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				customer.getInventory().setItem(14, new ItemStack(Material.CACTUS, 64));
				customer.getInventory().setItem(15, new ItemStack(Material.CACTUS, 64));
				//
				challenged.getInventory().setItem(13, new ItemStack(Material.BOWL, 64));
				challenged.getInventory().setItem(14, new ItemStack(Material.CACTUS, 64));
				challenged.getInventory().setItem(15, new ItemStack(Material.CACTUS, 64));
			}
		}
		customer.updateInventory();
		challenged.updateInventory();
	}

	private void prepareNormalBattle(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		for (int i = 1; i < 9; i++)
			p.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
		if (yPvP.getPlugin().getPvpType() == PvPType.FULLIRON) {
			ItemBuilder builder = new ItemBuilder().type(Material.IRON_HELMET);
			p.getInventory().setHelmet(builder.build());
			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
			p.getInventory().setChestplate(builder.build());
			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
			p.getInventory().setLeggings(builder.build());
			builder = new ItemBuilder().type(Material.IRON_BOOTS);
			p.getInventory().setBoots(builder.build());
			builder = new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());
			builder = null;
		} else {
			ItemBuilder builder = new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());
			builder = null;
		}
		p.updateInventory();
	}

	public void clearRequests(Player... players) {
		for (Player p : players) {
			if (nextBattle == p.getUniqueId())
				nextBattle = null;
			if (normalChallenge.containsKey(p.getUniqueId()))
				normalChallenge.remove(p.getUniqueId());
			if (customChallenge.containsKey(p.getUniqueId()))
				customChallenge.remove(p.getUniqueId());
			//
		}
	}

	public void clearCustom(Player... players) {
		for (Player p : players) {
			armourType.remove(p.getUniqueId());
			swordType.remove(p.getUniqueId());
			recraftType.remove(p.getUniqueId());
			recraftOption.remove(p.getUniqueId());
			sharpOption.remove(p.getUniqueId());
			fullSoupOption.remove(p.getUniqueId());
		}
	}

	@Override
	public void quit(Player p) {
		clearRequests(p);
		clearCustom(p);
		quitPlayer(p.getUniqueId());
	}
}
