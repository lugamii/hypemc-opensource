package br.com.weavenmc.lobby.listeners;

import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.event.inventory.InventoryClickEvent;
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
import org.bukkit.inventory.Inventory;
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
import lombok.AllArgsConstructor;
import lombok.Getter;

public class PlayerListener implements Listener {

	private ArrayList<String> novidades;
	private ArrayList<UUID> vanish;

	public PlayerListener() {
		novidades = new ArrayList<String>();
		vanish = new ArrayList<>();
		novidades.add("§fSeja bem vindo!;§6§lHypeSW");
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
		if (bP != null) {
			Lobby.getPlugin().getGamerManager().loadGamer(bP.getUniqueId(), new Gamer(bP));
			WeavenMC.getAsynchronousExecutor().runAsync(() -> {
				bP.load(DataCategory.CRATES);
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
		bk.load(DataCategory.SKYWARS);
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

//		if (!bk.hasGroupPermission((Group.VIP))) {
//			if (bk.getName().toLowerCase().contains("hype")) {
//				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
//						"group " + event.getPlayer().getName() + " add VIP 14d");
//				event.getPlayer().sendMessage(
//						"§d§lHYPE§f Você recebeu o vip §a§lVIP§f por possuir o §9§l\"Hype\" §fem seu nickname!");
//			}
//		}

//		p.sendMessage("");
//		p.sendMessage(StringMaker.makeCenteredMessage("§6§lWEAVEN§f§lMC"));
//		p.sendMessage("");
//		p.sendMessage(StringMaker.makeCenteredMessage(
//				"§7Seja bem-vindo ao §e§lLobby§7! Aqui você pode se conectar á todos nossos §e§lmodos de jogos§7 e também pode se §e§ldivertir§7 enquanto espera para jogar! Para obter maiores informações acesse nosso §esite§7!"));
//		p.sendMessage("");
//		p.sendMessage(StringMaker.makeCenteredMessage("§6§lwww.mc-weaven.cc"));
//		p.sendMessage("");
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

	public void openMenu(Player p) {
		Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Lojas");
		inventory.setItem(12, new ItemBuilder().type(Material.CHEST).name("§aLoja de Kit").build());
		inventory.setItem(14, new ItemBuilder().type(Material.ENDER_CHEST).name("§aLoja de Habilidade").build());
		p.openInventory(inventory);
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
				} else if (item.getType() == Material.CHEST) {
					event.setCancelled(true);
					p.openInventory(Lobby.getPlugin().getCosmeticManager().getCosmeticMenu());
				} else if (item.getType() == Material.EMERALD) {
					event.setCancelled(true);
					openMenu(p);
				} else if (item.getType() == Material.INK_SACK) {
					ItemMeta itemMeta = item.getItemMeta();
					if (itemMeta != null) {
						if (itemMeta.getDisplayName().equals("§fJogadores: §A§lON")) {
							p.setItemInHand(new ItemBuilder().type(Material.INK_SACK).durability(10)
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
							p.sendMessage("§cVocê agora está vendo os jogadores");
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
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 88, -1);
	}

	@AllArgsConstructor
	@Getter
	public enum Kits {
		PADRAO("Padrão",
				new String[] { "§7Itens: ", "§f- Picareta de ferro", "§f- Machado de ferro", "§f- Pá de ferro" },
				Group.MEMBRO, Material.STONE_PICKAXE, 0),
		ARCHER("Archer", new String[] { "§7Itens:", "§f- Arco", "§f- Flecha §8x32" }, Group.VIP, Material.ARROW, 20000),
		ECOLOGISTA("Ecologista", new String[] { "§7Itens:", "§f- Tronco de carvalho §8x32", "§f- Machado de ferro" },
				Group.VIP, Material.IRON_AXE, 20000),
		ENGENHEIRO("Engenheiro",
				new String[] { "§7Itens:", "§f- Bloco de redstone §8x6", "§f- Pistão §8x3", "§f- Pistão grudento §8x3",
						"§f- TNT §8x4", "§f- Placa de pressão" },
				Group.PRO, Material.REDSTONE_BLOCK, 20000),
		ENCHANTER("Enchanter", new String[] { "§7Itens:", "§f- Mesa de Encatamento §8x1" }, Group.PRO,
				Material.ENCHANTMENT_TABLE, 20000),
		FIGHTER("Fighter",
				new String[] { "§7Armadura:", "§f- Capacete de ouro", "§f- Peitoral de ouro", "§f- Botas de ouro", " ",
						"§f- Capacete de ouro", "§7Itens:", "§f- Espada de madeira com afiação II" },
				Group.PRO, Material.IRON_SWORD, 40000),
		RUSH("Rush", new String[] { "§7Armadura:", "§f- Peitoral de ferro", " ", "§7Itens:", "§f- Terra §8x32" },
				Group.ULTRA, Material.STONE_SWORD, 50000),
		PALADINO("Paladino",
				new String[] { "§7Armadura:", "§f- Peitoral de diamante", " ", "§7Itens:", "§f- Espada de diamante" },
				Group.ULTRA, Material.DIAMOND_CHESTPLATE, 50000),
		GRANDPA("Grandpa", new String[] { "§7Itens:", "§f- Graveto com knockback II" }, Group.ULTRA, Material.STICK,
				60000),
		SCOUT("Scout", new String[] { "§7Itens:", "§f- Poção de velocidade §8x2" }, Group.BETA, Material.POTION, 40000),

		GOLDEN_BOY("Golden Boy", new String[] { "§7Itens:", "§f- Maçã dourada §8x4" }, Group.BETA,
				Material.GOLDEN_APPLE, 50000),

		VIKING("Viking", new String[] { "§7Itens:", "§f- Machado de Diamante com SHARPNESS 2", "§f - Poção de Força" },
				Group.BETA, Material.DIAMOND_AXE, 50000),

		FISHERMAN("Fisherman", new String[] { "§7Itens:", "§f- Vara de Pesca" }, Group.BETA, Material.FISHING_ROD,
				50000),

		MINER("Miner", new String[] { "§7Itens:", "§f- Picareta de Ferro", "§f- Pedra §8x64" }, Group.BETA,
				Material.STONE_PICKAXE, 50000),

		SNOWMAN("Snowman", new String[] { "§7Itens:", "§f- Bola de Neve §816x", "§f- Full Couro", "§f- Pá de Ferro" },
				Group.BETA, Material.SNOW_BALL, 50000);

		String name;
		String[] description;
		Group group;
		Material display;
		int price;
	}

	public static void openKitsMenu(Player p) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§8Loja de Kits");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		ItemBuilder itemBuilder = new ItemBuilder();
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (Kits kit : Kits.values()) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;

			for (String desc : kit.getDescription())
				lore.add("§7" + desc);
			lore.add("§7");
			lore.add("§fCompre esse kit por §a" + kit.getPrice() + " coins§f!");

			if (bukkitPlayer.hasGroupPermission(kit.getGroup()) || hasKit(bukkitPlayer, kit)
					|| bukkitPlayer.hasGroupPermission(Group.BLADE)) {
				lore.add("§cVocê já tem esse kit");
				itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§cKit " + kit.getName())
						.lore(lore).build();
			} else {
				itemStack = itemBuilder.type(kit.getDisplay()).name("§aKit " + kit.getName()).lore(lore).build();
				lore.add("§cVocê ainda não possuí esse kit!");

			}
			lore.clear();
			inventory.setItem(slot, itemStack);

		}

		p.openInventory(inventory);

	}

	public static boolean hasKit(BukkitPlayer bukkitPlayer, Kits kit) {
		return bukkitPlayer.hasPermission("swkit." + kit.getName().replace(" ", "_"));
	}

	@AllArgsConstructor
	@Getter
	public enum Hability {
		COUNTER("Counter",
				new String[] { "§6Habilidade:", "§f Tenha 25% de chance de revidar",
						"§f o dano que o adversário causou!" },
				Group.VIP, Material.IRON_SWORD, 20000),
		FOGUETE("Foguete",
				new String[] { "§6Habilidade:", "§f Suba aos ares com seu foguete", " ", "§6Item:", " §fFirework", " ",
						"§6Cooldown:", "§f 15 segundos" },
				Group.ULTRA, Material.FIREWORK, 70000),
		FENIX("Fenix", new String[] { "§6Habilidade:", "§f Tenha uma segunda chance na vida!" }, Group.BLADE,
				Material.BLAZE_POWDER, 70000),
		KING_HELL("King Hell", new String[] { "§6Habilidade:", "§f Não sofra dano do fogo!" }, Group.ULTRA,
				Material.NETHER_FENCE, 50000),
		SPARTAN("Spartan",
				new String[] { "§6Habilidade:", " §fGanhe velocidade, resistência ao",
						" §ffogo e regeneração por 10 segundos,", " §fmas sofrerá lentidão e cegueira com o tempo",
						" §figual à cinco multiplicado ao número de kills", " §fefetuado com o kit.", " ", "§6Item:",
						" §fLã vermelha", " ", "§6Cooldown:", " §f35 segundos" },
				Group.BLADE, Material.IRON_CHESTPLATE, 50000),
		JOHN_WICK("John Wick",
				new String[] { "§6Habilidade:", "§f O adversário sofrerá", "§f o dobro de dano se for headshot!" },
				Group.PRO, Material.IRON_SWORD, 50000),
		VAMPIRE("Vampire",
				new String[] { "§6Habilidade:", "§f Tenha 25% de chance de roubar", " §fo sangue do adversário!" },
				Group.PRO, Material.REDSTONE, 50000),
		FLASH("Flash", new String[] { "§6Habilidade:", "§f Ganhe velocidade I a", "§f cada kill que faz!" }, Group.VIP,
				Material.REDSTONE_TORCH_ON, 50000),
		YATI("Yati", new String[] { "§6Habilidade:", "§f Congele o advérsario acertando", "§f bolinhas de neve!" },
				Group.VIP, Material.SNOW_BALL, 50000),
		ARROW("Arrow", new String[] { "§6Habilidade:", "§f Dê efeitos negativos nos adversários!" }, Group.ULTRA,
				Material.ARROW, 50000),
		ENDER_LORD("Lord Ender",
				new String[] { "§6Habilidade:", "§f Não sofra dano quando se", "§f teleporta com ender pearl" },
				Group.VIP, Material.ENDER_PEARL, 50000),
		VANESSA("Vanessa",
				new String[] { "§6Habilidade:", "§f Ganhe fire aspect na espada", "§f a cada kill que faz!" },
				Group.ULTRA, Material.DIAMOND_SWORD, 50000);

		String name;
		String[] description;
		Group group;
		Material display;
		int price;
	}

	public static void openHabilityMenu(Player p) {

		Inventory inventory = Bukkit.createInventory(null, 6 * 9, "§8Loja de Habilidade");
		BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int slot = 9;
		ItemBuilder itemBuilder = new ItemBuilder();
		ItemStack itemStack;
		List<String> lore = new ArrayList<>();

		for (Hability kit : Hability.values()) {
			slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;
			if (slot % 9 == 0 || slot % 9 == 8)
				slot++;

			for (String desc : kit.getDescription())
				lore.add("§7" + desc);
			lore.add("§7");
			lore.add("§fCompre essa habilidade por §a" + kit.getPrice() + " coins§f!");

			if (bukkitPlayer.hasGroupPermission(kit.getGroup()) || hasHability(bukkitPlayer, kit)
					|| bukkitPlayer.hasGroupPermission(Group.BLADE)) {
				lore.add("§cVocê já tem essa habilidade");
				itemStack = itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§cHabilidade " + kit.getName()).lore(lore).build();
			} else {
				itemStack = itemBuilder.type(kit.getDisplay()).name("§aHabilidade " + kit.getName()).lore(lore).build();
				lore.add("§cVocê ainda não possuí essae habilidade!");

			}
			lore.clear();
			inventory.setItem(slot, itemStack);

		}

		p.openInventory(inventory);

	}

	public static boolean hasHability(BukkitPlayer bukkitPlayer, Hability hability) {
		return bukkitPlayer.hasPermission("swhab." + hability.getName().replace(" ", "_"));
	}

	@EventHandler
	public void onPlayerClick(InventoryClickEvent event) {
		if (event.getInventory().getName().equals("Lojas")) {
			event.setCancelled(true);
			if (event.getCurrentItem().getType() == Material.ENDER_CHEST) {
				Player player = (Player) event.getWhoClicked();
				openHabilityMenu(player);
			}
			if (event.getCurrentItem().getType() == Material.CHEST) {
				Player player = (Player) event.getWhoClicked();
				openKitsMenu(player);
			}
		}
		if (event.getInventory().getName().equals("§8Loja de Habilidade")) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon()
					.getWeavenPlayer(((Player) event.getWhoClicked()).getUniqueId());
			if (event.getCurrentItem().getType() == Material.AIR)
				return;
			for (Hability kit : Hability.values()) {
				if (kit.getDisplay() == event.getCurrentItem().getType()) {
					if (bukkitPlayer.getMoney() < kit.getPrice()) {
						player.sendMessage("§cVocê não possui coins!");
						break;
					}

					bukkitPlayer.removeMoney(kit.getPrice());
					bukkitPlayer.addPermission("swhab." + kit.getName().replace(" ", "_"), -1);
					bukkitPlayer.save(DataCategory.ACCOUNT, DataCategory.BALANCE);

					player.closeInventory();
					player.sendMessage("§aVocê comprou a habilidade §e" + kit.getName() + " §acom sucesso!");

					break;
				}
			}
		}
		if (event.getInventory().getName().equals("§8Loja de Kits")) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon()
					.getWeavenPlayer(((Player) event.getWhoClicked()).getUniqueId());
			if (event.getCurrentItem().getType() == Material.AIR)
				return;
			for (Kits kit : Kits.values()) {
				if (kit.getDisplay() == event.getCurrentItem().getType()) {
					if (bukkitPlayer.getMoney() < kit.getPrice()) {
						player.sendMessage("§cVocê não possui coins!");
						break;
					}

					bukkitPlayer.removeMoney(kit.getPrice());
					bukkitPlayer.addPermission("swkit." + kit.getName().replace(" ", "_"), -1);
					bukkitPlayer.save(DataCategory.ACCOUNT, DataCategory.BALANCE);

					player.closeInventory();
					player.sendMessage("§aVocê comprou o kit §e" + kit.getName() + " §acom sucesso!");

					break;
				}
			}
		}
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

		builder = new ItemBuilder().name("§aCosméticos §7(Botão Direito)").type(Material.CHEST);
		p.getInventory().setItem(5, builder.build());
		builder = new ItemBuilder().name("§aLoja do SkyWars §7(Botão Direito)").type(Material.EMERALD);
		p.getInventory().setItem(3, builder.build());

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
