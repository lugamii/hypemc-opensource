package br.com.weavenmc.lobby.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
//import org.bukkit.event.player.PlayerToggleFVIPEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerShowEvent;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.gamer.Gamer;
import br.com.weavenmc.lobby.managers.MenuManager.MenuType;
import br.com.weavenmc.lobby.npcs.PacketReader;

public class PlayerListener implements Listener {

	private ArrayList<String> novidades;
	private ArrayList<UUID> vanish;

	public PlayerListener() {
		novidades = new ArrayList<String>();
		vanish = new ArrayList<>();
		novidades.add("§fSeja bem vindo!;§6§lHypeHG");
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
		if (bP != null) {
			Lobby.getPlugin().getGamerManager().loadGamer(bP.getUniqueId(), new Gamer(bP));
			WeavenMC.getAsynchronousExecutor().runAsync(() -> {
				bP.load(DataCategory.CRATES, DataCategory.COPA);
			});
		} else {
			event.disallow(Result.KICK_OTHER, "§4§lERRO§f Ocorreu um erro ao tentar carregar sua conta.");
		}
	}

	public void loadVip(String username) {
		Group group = null;
		long time = -1;

		if (Lobby.getPlugin().getConfig().get("VIP." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("VIP." + username.toLowerCase());
			if (time == -1) {
				group = Group.VIP;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.VIP;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add VIP " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("VIP." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("SAPPHIRE." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("SAPPHIRE." + username.toLowerCase());
			if (time == -1) {
				group = Group.PRO;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.PRO;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add PRO " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("SAPPHIRE." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("ELITE." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("ELITE." + username.toLowerCase());
			if (time == -1) {
				group = Group.PRO;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.PRO;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add PRO " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("ELITE." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("RUBY." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("RUBY." + username.toLowerCase());
			if (time == -1) {
				group = Group.BETA;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.BETA;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add beta " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("RUBY." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("BETA." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("BETA." + username.toLowerCase());
			if (time == -1) {
				group = Group.BETA;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.BETA;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add beta " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("BETA." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("WEAVEN." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("WEAVEN." + username.toLowerCase());
			if (time == -1) {
				group = Group.ULTRA;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.ULTRA;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add ULTRA " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("WEAVEN." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}

		if (Lobby.getPlugin().getConfig().get("BLADE." + username.toLowerCase()) != null) {
			time = Lobby.getPlugin().getConfig().getLong("BLADE." + username.toLowerCase());
			if (time == -1) {
				group = Group.BLADE;
			} else if (time >= System.currentTimeMillis()) {
				group = Group.BLADE;
			}
			if (group != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"group " + username + " add BLADE " + (time == -1 ? "-1" : "c" + time));
				Lobby.getPlugin().getConfig().set("BLADE." + username.toLowerCase(), null);
				Lobby.getPlugin().saveConfig();
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player p = event.getPlayer();
		Lobby.getPlugin().getScoreboardManager().createScoreboard(p);
		BukkitPlayer bk = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());

		for (UUID vanished : vanish) {
			Player v = Bukkit.getPlayer(vanished);
			if (v != null) {
				v.hidePlayer(p);
			}
		}

		p.teleport(getSpawn(p));

		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setFireTicks(0);
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setGameMode(GameMode.ADVENTURE);

		joinItems(p);

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());

//		if (!bP.isStaffer()) {
//			if (!bP.hasGroupPermission(Group.VIP)) {
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add PRO 2d");
//			}
//		}
		Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(p.getUniqueId());

		if (bP.hasGroupPermission(Group.VIP)) {
			p.setAllowFlight(true);
			gamer.setFlying(true);
		}

		if (bP.hasGroupPermission(Group.VIP) && !bP.isStaffer()) {
			p.setAllowFlight(true);
			Bukkit.broadcastMessage(bP.getGroup().getTagToUse().getPrefix() + bP.getName() + " §6entrou no lobby!");
			return;
		}

		loadVip(p.getName());

		Lobby.getPlugin().getNpcManager().spawnNpcs(p);

		new PacketReader(p).inject();

		for (int i = 0; i < novidades.size(); i++) {
			String novidade = novidades.get(i);
			String title = novidade.split(";")[1].replace("%player%", "§6§l" + p.getName());
			String subtitle = novidade.split(";")[0];
			Bukkit.getScheduler().runTaskLater(Lobby.getPlugin(), () -> {
				TitleAPI.setTitle(p, title, subtitle);
			}, i * 40);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Lobby.getPlugin().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onShow(PlayerShowEvent event) {
		if (vanish.contains(event.getToShow().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteractListener(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().toString().contains("RIGHT")) {
			ItemStack item = event.getItem();
			if (item != null) {
				if (item.getType() == Material.COMPASS) {
					event.setCancelled(true);
					p.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.GAMES_MODE));
				} else if (item.getType() == Material.SKULL_ITEM) {
					event.setCancelled(true);
					p.performCommand("account");
				} else if (item.getType() == Material.EYE_OF_ENDER) {
					p.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.COPA_MENU));
				} else if (item.getType() == Material.CHEST) {
					event.setCancelled(true);
					p.openInventory(Lobby.getPlugin().getCosmeticManager().getCosmeticMenu());
				} else if (item.getType() == Material.INK_SACK) {
					ItemMeta itemMeta = item.getItemMeta();
					if (itemMeta != null) {
						if (itemMeta.getDisplayName().equals("§fJogadores: §A§lON")) {
							p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(8)
									.name("§fJogadores: §c§lOFF").build());
							if (!vanish.contains(p.getUniqueId()))
								vanish.add(p.getUniqueId());
							for (Player o : Bukkit.getOnlinePlayers()) {
								if (o.getUniqueId() == p.getUniqueId())
									continue;
								p.hidePlayer(o);
							}
							p.sendMessage("§cVocê agora não está mais vendo os jogadores");
							p.updateInventory();
						} else {
							p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
									.name("§fJogadores: §A§lON").build());
							if (vanish.contains(p.getUniqueId()))
								vanish.remove(p.getUniqueId());
							VanishAPI.getInstance().updateVanishToPlayer(p);
							p.sendMessage("§aVocê agora está os jogadores");
							p.updateInventory();
						}
						itemMeta = null;
					}
				} else if (item.getType() == Material.NETHER_STAR) {
					event.setCancelled(true);
					p.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.LOBBYS));
				}
				item = null;
			}
		}
		p = null;
	}

	public Location getSpawn(Player p) {// 180, 0
		Location spawn = p.getWorld().getSpawnLocation();
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 180, 2);
	}

	private void joinItems(Player p) {
		ItemBuilder builder = null;
		try {
			builder = new ItemBuilder().name("§aSelecionar jogo §7(Botão Direito)").type(Material.COMPASS)
					.enchantment(Enchantment.DURABILITY);
		} catch (Exception e) {
			builder = new ItemBuilder().name("§aSelecionar jogo §7(Botão Direito)").type(Material.COMPASS);
		}
		p.getInventory().setItem(0, builder.build());

		builder = new ItemBuilder().name("§aMeu perfil §7(Botão Direito)").type(Material.SKULL_ITEM).skin(p.getName())
				.durability(3);
		p.getInventory().setItem(1, builder.build());

		builder = new ItemBuilder().name("§aCopaHG §7(Botão Direito)").type(Material.EYE_OF_ENDER);
		p.getInventory().setItem(3, builder.build());

		builder = new ItemBuilder().name("§aCosméticos §7(Botão Direito)").type(Material.CHEST);
		p.getInventory().setItem(5, builder.build());

		builder = new ItemBuilder().name("§fJogadores: §a§lON").type(Material.INK_SACK).durability(10);
		p.getInventory().setItem(7, builder.build());

		builder = new ItemBuilder().name("§aSelecionar Lobby §7(Botão Direito)").type(Material.NETHER_STAR);
		p.getInventory().setItem(8, builder.build());

		p.updateInventory();
		p.getInventory().setHeldItemSlot(0);

		builder = null;
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getTo().getBlockY() < 1) {
			player.teleport(getSpawn(player));
		}
	}

	@EventHandler
	public void onBucket(PlayerBucketEmptyEvent event) {
		Material bucket = event.getBucket();
		if (bucket.toString().contains("LAVA")) {
			event.setCancelled(true);
		} else if (bucket.toString().contains("WATER")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		if (!bP.hasGroupPermission(Group.ADMIN)) {
			event.setCancelled(true);
			return;
		}
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		if (!bP.hasGroupPermission(Group.ADMIN)) {
			event.setCancelled(true);
			return;
		}
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWheater(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	public static final ArrayList<UUID> toggled = new ArrayList<UUID>();
	public static final ArrayList<UUID> cooldown = new ArrayList<UUID>();

	@EventHandler
	public void onToggleFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		if (gamer == null)
			return;
		if (gamer.isFlying()) {
			return;
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!toggled.contains(event.getPlayer().getUniqueId())) {
			Player player = event.getPlayer();

			if (player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.EMERALD_BLOCK) {
				Vector v = player.getLocation().getDirection();
				v.multiply(3).setY(1);
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 2f);
				player.setVelocity(v);
			}

			Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(player.getUniqueId());
			if (gamer == null)
				return;
			if (gamer.isFlying()) {
				return;
			}

			if (!cooldown.contains(event.getPlayer().getUniqueId())) {
				if ((player.getGameMode() != GameMode.CREATIVE)
						&& (player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR)
						&& (!player.isFlying())) {
				}
			}
		}
	}
}
