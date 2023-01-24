package br.com.weavenmc.ypvp.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class SpawnMinigame extends Minigame {

	public SpawnMinigame() {
		setName("Spawn");
		setOtherNames(new String[] {});
		setTopKillStreakMinigame(true);
	}

	@Override
	public void join(Player p) {
		BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lSpawn");
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
		protect(p);
		yPvP.getPlugin().getTournament().quitPlayer(p);
		p.getInventory().setArmorContents(null);

		ItemBuilder builder = new ItemBuilder().type(Material.CHEST).name("§aKits §7(Clique para Abrir)");
		p.getInventory().setItem(0, builder.build());

		builder = new ItemBuilder().type(Material.COMPASS).name("§aWarps §7(Clique para Abrir)");
		p.getInventory().setItem(3, builder.build());

		builder = new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(p.getName())
				.name("§a" + gamer.getName() + " §7(Clique para Ver)");
		p.getInventory().setItem(4, builder.build());
		builder = new ItemBuilder().type(Material.ENCHANTED_BOOK).glow().name("§aEvento §7(Clique para Ir)");
		p.getInventory().setItem(5, builder.build());

		builder = new ItemBuilder().type(Material.STORAGE_MINECART).name("§aLoja §7(Clique para Abrir)");
		p.getInventory().setItem(8, builder.build());

		p.getInventory().setHeldItemSlot(1);

		p.updateInventory();
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			if (gamer.getAbility().getName().equalsIgnoreCase("Nenhum")) {
				ItemStack item = event.getItem();
				if (item != null) {
					if (item.getType() == Material.CHEST) {
						event.setCancelled(true);
						openKitsMenu(p, 1);
					} else if (item.getType() == Material.ENDER_CHEST) {
						event.setCancelled(true);
						openKitsMenu(p, 2);
					}
					if (item.getType() == Material.COMPASS) {
						event.setCancelled(true);
						openWarpsMenu(p);
					} else if (item.getType() == Material.SKULL_ITEM) {
						event.setCancelled(true);
						p.performCommand("account");
					} else if (item.getType() == Material.STORAGE_MINECART) {
						event.setCancelled(true);
						openStoreMenu(p, 1);

					} else if (item.getType() == Material.ENCHANTED_BOOK) {
						event.setCancelled(true);
						p.sendMessage("§cModo EVENTO em manutenção.");
					}
				}
			}
		}
	}

	public void openWarpsMenu(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§bWarps");
		
		Minigame minigame = yPvP.getPlugin().getWarpManager().getWarp(FramesMinigame.class);
		ItemBuilder builder = new ItemBuilder().type(Material.GLASS).name("§aFps")
				.lore("§7Treine seu PvP com mais FPSs").amount(minigame.getPlaying());
		menu.setItem(0, builder.build());
		minigame = yPvP.getPlugin().getWarpManager().getWarp(BattleMinigame.class);
		builder = new ItemBuilder().type(Material.BLAZE_ROD).name("§a1v1").lore("§7Tire 1v1 justo com alguém")
				.amount(minigame.getPlaying());
		menu.setItem(1, builder.build());
		minigame = yPvP.getPlugin().getWarpManager().getWarp(LavaChallengeMinigame.class);
		builder = new ItemBuilder().type(Material.LAVA_BUCKET).name("§aLava Challenge")
				.lore("§7Treine seus refils e recrafts", "§7completando os niveis do challenge.")
				.amount(minigame.getPlaying());
		menu.setItem(2, builder.build());
		
//		minigame = yPvP.getPlugin().getWarpManager().getWarp(VoidChallengeMinigame.class);
//		builder = new ItemBuilder()
//				.type(Material.BEDROCK).name("§aVoid Challenge").lore("§7Veja quanto tempo você tanka",
//						"§7com o dano do void e receba", "§7moedas de acordo com o tempo.")
//				.amount(minigame.getPlaying());
//		menu.setItem(3, builder.build());
		
		minigame = yPvP.getPlugin().getWarpManager().getWarp(FishermanMinigame.class);
		builder = new ItemBuilder().type(Material.FISHING_ROD).name("§b§lFisherman")
				.lore("§7Fisgue seus adversários", "§7até o void").amount(minigame.getPlaying());
		menu.setItem(3, builder.build());
		
		minigame = yPvP.getPlugin().getWarpManager().getWarp(SumoMinigame.class);
		builder = new ItemBuilder().type(Material.APPLE).name("§b§lSumo")
				.lore("§7Tire 1v1 no modo sumo").amount(minigame.getPlaying());
		menu.setItem(4, builder.build());
		
		builder = null;
		minigame = null;
		p.openInventory(menu);
	}

	public static void openKitsMenu(Player p, int page) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§7Kits");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		ItemBuilder itemBuilder = new ItemBuilder();
		Ability ability;
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (int initial = (page == 1 ? 0 : (page == 2 ? 21 : 21 * page)), max = 21 * page; initial < max; initial++) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;

			if (initial >= 29) {
				ability = null;
				break;
			} else
				ability = null;
				try {
					ability = yPvP.getPlugin().getAbilityManager().getAbilities().get(initial);
				} catch (Exception e) {
					break;
				}
			if (ability == null) {
				break;
			}

			for (String desc : ability.getDescription())
				lore.add("§7" + desc);
			lore.add("§7");

			if (bukkitPlayer.hasGroupPermission(ability.getGroupToUse()) || hasKit(bukkitPlayer, ability)
					|| bukkitPlayer.hasGroupPermission(Group.BLADE)) {
				lore.add("§eClique para selecionar");
				itemStack = itemBuilder.type(ability.getIcon()).name("§aKit " + ability.getName()).lore(lore).build();
			} else {
				lore.add("§cVocê não possuí esse kit!");
				itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§cKit " + ability.getName()).lore(lore).build();

			}
			lore.clear();
			inventory.setItem(slot, itemStack);
			if (slot >= 34) {
				if (page == 1) {
					if (yPvP.getPlugin().getAbilityManager().getAbilities().get(max) != null) {
						inventory.setItem(53,
								new ItemBuilder().name("§aPágina " + (page + 1)).type(Material.ARROW).build());
					}
				}
				break;
			}

		}
		if (page != 1) {
			inventory.setItem(45, new ItemBuilder().name("§aPágina " + (page - 1)).type(Material.ARROW).build());
		}
		inventory.setItem(49,
				new ItemBuilder()
						.name("§aSeu kit: §e"
								+ yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId()).getAbility().getName())
						.type(Material.ITEM_FRAME).build());
		inventory.setItem(50, new ItemBuilder().name("§aExibindo de: §eA-Z").type(Material.SLIME_BALL).build());
		p.openInventory(inventory);

	}

	public static void openStoreMenu(Player p, int page) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§7Loja de Kits");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		ItemBuilder itemBuilder = new ItemBuilder();
		Ability ability;
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();
		if (bukkitPlayer.hasGroupPermission(Group.ULTRA)) {
			itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§cNada aqui!")
					.lore("§7Você já possui todos", "§7os kits!").build();
			inventory.setItem(22, itemStack);
		} else
			for (int initial = (page == 1 ? 0 : (page == 2 ? 21 : 21 * page)), max = 21
					* page; initial < max; initial++) {
				slot++;
				if (slot % 9 == 0 || slot % 9 == 8)
					slot++;
				if (slot % 9 == 0 || slot % 9 == 8)
					slot++;

				if (initial >= 29) {
					ability = null;
					break;
				} else
					try {
						ability = yPvP.getPlugin().getAbilityManager().getAbilities().get(initial);
						
					} catch (Exception e) {
						ability = null;
					}
				if (ability == null) {
					break;
				}

				for (String desc : ability.getDescription())
					lore.add("§7" + desc);
				lore.add("");

				if (!bukkitPlayer.hasGroupPermission(ability.getGroupToUse())) {
					lore.add("§fClique com o botão §aesquerdo§f para comprar o kit §e" + ability.getName());
					lore.add("§fdurante §e3 dias§f por §e" + ability.getTempPrice() + " coins§f!");
					lore.add("");
					lore.add("§fClique com o botão §adireto§f para comprar o kit §e" + ability.getName());
					lore.add("§eeternamente §fpor §e" + ability.getPrice() + " coins§f!");
					itemStack = itemBuilder.type(ability.getIcon()).name("§aKit " + ability.getName()).lore(lore)
							.build();
				} else {
					lore.add("§cVocê já possuí esse kit!");
					itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
							.name("§cKit " + ability.getName()).lore(lore).build();

				}
				lore.clear();
				inventory.setItem(slot, itemStack);
				if (slot >= 34) {
					if (page == 1) {
						if (yPvP.getPlugin().getAbilityManager().getAbilities().get(max) != null) {
							inventory.setItem(53,
									new ItemBuilder().name("§aPágina " + (page + 1)).type(Material.ARROW).build());
						}
					}
					break;
				}

			}
		if (page != 1) {
			inventory.setItem(45, new ItemBuilder().name("§aPágina " + (page - 1)).type(Material.ARROW).build());
		}
		inventory.setItem(49,
				new ItemBuilder().name("§7Suas coins: §6" + bukkitPlayer.getMoney()).type(Material.EMERALD).build());
		p.openInventory(inventory);

	}

	@EventHandler
	public void onDamageEntity(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player p = (Player) event.getEntity();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			if (isProtected(p)) {
				event.setCancelled(true);
			}
		}
	}

	public static void teleportToWar(Player p) {
		int a = new Random().nextInt(9);
		p.teleport(yPvP.getPlugin().getLocationManager().getLocation("arena" + a) == null
				? yPvP.getPlugin().getLocationManager().getLocation("arena" + 1)
				: yPvP.getPlugin().getLocationManager().getLocation("arena" + a));
	}

	public static boolean hasKit(BukkitPlayer bP, Ability ability) {
		return bP.hasPermission("pvpkit." + ability.getName().toLowerCase());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			ItemStack current = event.getCurrentItem();
			if (current == null) {
				return;
			}
			if (current != null) {
				if (event.getInventory().getName().equals("§7Kits")) {
					event.setCancelled(true);
					if (current.getType() == Material.ARROW) {
						p.closeInventory();
						openKitsMenu(p,
								Integer.valueOf(current.getItemMeta().getDisplayName().replace("§aPágina ", "")));
						return;
					}
					if (current.getType() != Material.DIAMOND) {
						for (Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
							if (current.getType().equals(ability.getIcon())) {
								p.closeInventory();
								p.performCommand("kit " + ability.getName());
								return;
							}
						}
					}
				}
			}
			if (event.getInventory().getName().equals("§7Loja de Kits")) {
				event.setCancelled(true);
				if (current.getType() == Material.ARROW) {

					p.closeInventory();
					openStoreMenu(p, Integer.valueOf(current.getItemMeta().getDisplayName().replace("§aPágina ", "")));

					return;
				}
				for (Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
					if (current.getType().equals(ability.getIcon()) && current.getItemMeta().getDisplayName()
							.toLowerCase().contains(ability.getName().toLowerCase())) {
						if (event.getClick() == ClickType.LEFT) {
							if (bP.getMoney() >= ability.getTempPrice()) {
								p.closeInventory();
								bP.removeMoney(ability.getTempPrice());
								bP.addPermission("pvpkit." + ability.getName().toLowerCase(), getTime("3d"));
								bP.save(DataCategory.ACCOUNT, DataCategory.BALANCE);
								p.sendMessage("§b§lSHOP§f Parabéns! Você §3§lALUGOU§f a habilidade §b§l"
										+ ability.getName().toUpperCase() + "§f durante §3§l3 DIAS!");
							} else {
								p.closeInventory();
								p.sendMessage("§b§lSHOP§f Você precisa de mais §b§l"
										+ (ability.getTempPrice() - bP.getMoney())
										+ " MOEDAS§f para §3§lALUGAR§f a habilidade §b§l"
										+ ability.getName().toUpperCase());
							}
						} else if (event.getClick() == ClickType.RIGHT) {
							if (bP.getMoney() >= ability.getPrice()) {
								p.closeInventory();
								bP.removeMoney(ability.getPrice());
								bP.addPermission("pvpkit." + ability.getName().toLowerCase(), -1);
								bP.save(DataCategory.ACCOUNT, DataCategory.BALANCE);
								p.sendMessage("§b§lSHOP§f Parabéns! Você §3§lCOMPROU§f a habilidade §b§l"
										+ ability.getName().toUpperCase() + "§f com duração §3§lETERNA!");
							} else {
								p.closeInventory();
								p.sendMessage(
										"§b§lSHOP§f Você precisa de mais §b§l" + (ability.getPrice() - bP.getMoney())
												+ " MOEDAS§f para §3§lCOMPRAR§f a habilidade §b§l"
												+ ability.getName().toUpperCase());
							}
						}
						return;
					}
				}
			} else if (event.getInventory().getName().equals("§bWarps")) {
				event.setCancelled(true);
				if (current.getType() == Material.GLASS) {
					p.closeInventory();
					p.performCommand("fps");
				} else if (current.getType() == Material.BLAZE_ROD) {
					p.closeInventory();
					p.performCommand("1v1");
				} else if (current.getType() == Material.LAVA_BUCKET) {
					p.closeInventory();
					p.performCommand("warp lava");
				} else if (current.getType() == Material.BEDROCK) {
					p.closeInventory();
				}  else if (current.getType() == Material.FISHING_ROD) {
					p.closeInventory();
					p.performCommand("warp fisherman");
				} else if (current.getType() == Material.APPLE) {
					p.closeInventory();
					p.performCommand("warp sumo");
				}
			}

			current = null;
		}
	}

	public long getTime(String time) {
		try {
			return StringTimeUtils.parseDateDiff(time, true);
		} catch (Exception ex) {
			return -1;
		}
	}

//	public static void setInventory(Player p) {
//		p.getInventory().clear();
//		p.getInventory().setArmorContents(null);
//		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
//		Ability ability = gamer.getAbility();
//		for (int i = 0; i < 36; i++)
//			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
//		if (yPvP.getPlugin().getPvpType() == PvPType.FULLIRON) {
//			ItemBuilder builder;
////			p.getInventory().setHelmet(builder.build());
////			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
////			p.getInventory().setChestplate(builder.build());
////			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
////			p.getInventory().setLeggings(builder.build());
////			builder = new ItemBuilder().type(Material.IRON_BOOTS);
////			p.getInventory().setBoots(builder.build());
//			builder = new ItemBuilder().type(Material.STONE_SWORD);
//			if (ability.getName().equals("PvP"))
//				builder.enchantment(Enchantment.DAMAGE_ALL, 1);
//			p.getInventory().setItem(0, builder.build());
//
//			if (ability.isHasItem()) {
//
//				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
//				p.getInventory().setItem(1, builder.build());
//
//				builder = new ItemBuilder().type(Material.COMPASS).name("§3§lBussola");
//				p.getInventory().setItem(8, builder.build());
//				return;
//			}
//
//		} else {
//			ItemBuilder builder = new ItemBuilder().type(Material.STONE_SWORD);
//			if (ability.getName().equals("PvP"))
//				builder.enchantment(Enchantment.DAMAGE_ALL, 1);
//			p.getInventory().setItem(0, builder.build());
//			if (ability.isHasItem()) {
//				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
//				p.getInventory().setItem(1, builder.build());
//			}
//
//			builder = null;
//		}
//		ItemBuilder builder = new ItemBuilder().type(Material.BOWL).amount(32);
//		p.getInventory().setItem(13, builder.build());
//		builder = new ItemBuilder().type(Material.RED_MUSHROOM).amount(32);
//		p.getInventory().setItem(14, builder.build());
//		builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).amount(32);
//		p.getInventory().setItem(15, builder.build());
//		builder = new ItemBuilder().type(Material.COMPASS).name("§3§lBussola");
//		p.getInventory().setItem(8, builder.build());
//		p.updateInventory();
//		builder = null;
//	}

	public static ArrayList<Player> noDamageByFallCause = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOW)
	public void onSpawnMove(PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			if (isProtected(p)) {
				if (p.getLocation().distance(yPvP.getPlugin().getLocationManager().getLocation("spawn")) > 150) {
					unprotect(p);
					noDamageByFallCause.add(p);
//					setInventory(p);
					p.sendMessage("§8§lPROTEÇÃO§f Você §7§lPERDEU§f sua proteção de spawn");
					if (gamer.getAbility().getName().equals("Nenhum")) {
						p.performCommand("kit pvp");
					}
					new BukkitRunnable() {

						@Override
						public void run() {
							if (noDamageByFallCause.contains(p))
								noDamageByFallCause.remove(p);
						}
					}.runTaskLaterAsynchronously(yPvP.getPlugin(), 15l);
				}
			}
		}
		gamer = null;
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL) {
			Player player = (Player) event.getEntity();
			if (noDamageByFallCause.contains(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (noDamageByFallCause.contains(event.getPlayer()))
			noDamageByFallCause.remove(event.getPlayer());
	}

	@Override
	public void quit(Player p) {
		quitPlayer(p.getUniqueId());
		unprotect(p);
	}
}
