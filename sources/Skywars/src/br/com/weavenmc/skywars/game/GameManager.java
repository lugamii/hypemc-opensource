package br.com.weavenmc.skywars.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.hability.HabilityAPI;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.kit.KitAPI;
import br.com.weavenmc.skywars.nametag.TagAPI;
import br.com.weavenmc.skywars.player.PlayerController;
import br.com.weavenmc.skywars.player.PlayerManager;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;
import br.com.weavenmc.skywars.utils.Debug.TypeAlert;
import br.com.weavenmc.timer.Jogo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;

public class GameManager {

	@Getter
	private GameState state;
	@Setter
	private EventsState eState;
	@Getter
	private String mapName;
	private WeavenSkywars main;
	@Getter
	private KitAPI kitAPI;
	@Getter
	private HabilityAPI habilityAPI;
	@Getter
	private Location feast;
	@Getter
	private Location lobby;

	@Getter
	private ArrayList<Location> spawnsPoints = new ArrayList<>();
	@Getter
	private ArrayList<Location> miniFeasts = new ArrayList<>();
	@Getter
	private ArrayList<Location> feasts = new ArrayList<>();
	@Getter
	private ArrayList<Chest> opened = new ArrayList<>();

	private ArrayList<Block> feastBlock = new ArrayList<>();
	@Getter
	private ArrayList<Chest> feastChest = new ArrayList<>();

	private ArrayList<Block> miniFeastBlock = new ArrayList<>();
	@Getter
	private ArrayList<Chest> miniFeastChest = new ArrayList<>();
	@Getter
	private HashMap<UUID, Location> fenixLocation = new HashMap<>();
	@Getter
	private Player winner;

	private int borda7;
	private int borda8;

	public GameManager(WeavenSkywars main) {
		this.main = main;
		main.debug.sendMessage("Habilitando classe 'game manager'.", TypeAlert.ALERTA);
		this.state = GameState.LOBBY;
		main.debug.sendMessage("Estagio inicial setado como lobby.", TypeAlert.ALERTA);
		this.eState = EventsState.NONE;
		main.debug.sendMessage("Estagio de eventos setado como vazio.", TypeAlert.ALERTA);
		this.borda8 = main.getConfig().getInt("R.borda8");
		main.debug.sendMessage("Raio da borda 1.8 setada com " + borda8 + ".", TypeAlert.ALERTA);
		this.borda7 = main.getConfig().getInt("R.borda7");
		main.debug.sendMessage("Raio da borda 1.7 setada com " + borda7 + ".", TypeAlert.ALERTA);
		this.kitAPI = new KitAPI();
		this.habilityAPI = new HabilityAPI();
		main.debug.sendMessage("Variaveis kitAPI e habilityAPI setadas em suas classes.", TypeAlert.ALERTA);
	}

	public void updatePlayerCheck(Player player) {
		PlayerManager playerManager = new PlayerManager(player);
		if (GameController.check.containsKey(player.getUniqueId()))
			GameController.check.remove(player.getUniqueId());
		GameController.check.put(player.getUniqueId(), playerManager);
		main.debug.sendMessage("Classe PlayerManager do jogador " + player.getName() + " foi atualizada com sucesso.",
				TypeAlert.ALERTA);
	}

	public void startConfig() {
		if (WeavenSkywars.enabled == false) {
			main.debug.sendMessage("Esta faltando setar spawns ou minifeast para iniciar o plugin!", TypeAlert.AVISO);
			return;
		}
		mapName = main.getConfig().getString("nameMap");
		main.debug.sendMessage("Nome do mapa: §e" + mapName, TypeAlert.SUCESSO);
		try {
			String[] values = main.getConfig().getString("lobbyPoint").split(","); // [X:0, Y:0, Z:0]
			double x = Double.parseDouble(values[0].split(":")[1]); // X:0 -> X, 0 -> 0
			double y = Double.parseDouble(values[1].split(":")[1]);
			double z = Double.parseDouble(values[2].split(":")[1]);
			Location location = new Location(Bukkit.getWorld("lobby"), x, y, z);
			this.lobby = location.clone().add(0.493, 0, 0.447);
			// Bukkit.getConsoleSender().sendMessage(""+lobbyLocation);
		} catch (Exception ex) {
			main.debug.sendMessage("Falha ao carregar o spawnpoint com metadata lobby para o mapa: '" + mapName
					+ "'. Tipo de exceção: " + ex, TypeAlert.ERROR);
		}
		main.debug.sendMessage("Lobby setado com sucessos no mapa §e" + mapName, TypeAlert.SUCESSO);
		try {
			String[] values = main.getConfig().getString("feast").split(","); // [X:0, Y:0, Z:0]
			double x = Double.parseDouble(values[0].split(":")[1]); // X:0 -> X, 0 -> 0
			double y = Double.parseDouble(values[1].split(":")[1]);
			double z = Double.parseDouble(values[2].split(":")[1]);
			Location location = new Location(Bukkit.getWorld("mapa"), x, y, z);
			this.feast = location;
			// Bukkit.getConsoleSender().sendMessage(""+feast);
		} catch (Exception ex) {
			main.debug.sendMessage("Falha ao carregar o spawnpoint com metadata feast para o mapa: '" + mapName
					+ "'. Tipo de exceção: " + ex, TypeAlert.ERROR);
		}
		main.debug.sendMessage("Feast setado com sucesso no mapa §e" + mapName, TypeAlert.SUCESSO);
		for (Location point : WeavenSkywars.getSpawnsManager().getSpawn()) {
			spawnsPoints.add(point);
		}
		main.debug.sendMessage("Spawn point's setado com sucesso no mapa §e" + mapName, TypeAlert.SUCESSO);
		for (Location point : WeavenSkywars.getChestManager().getMiniFeastChest()) {
			miniFeasts.add(point);
		}
		main.debug.sendMessage("Mini feasts setado com sucesso no mapa §e" + mapName, TypeAlert.SUCESSO);
		createJail();
		setMiniFeast();
		inializeMiniFeast();
		setFeast();
		inializeFeast();
		vaiTomarNoCUKing();
		main.debug.sendMessage("GameManager do mapa §e" + mapName + " §ahabilitado com sucesso.", TypeAlert.SUCESSO);
	}

	public void inializeMiniFeast() {
		for (Iterator<?> iterator = miniFeastBlock.iterator(); iterator.hasNext();) {
			Block block = (Block) iterator.next();
			if (block.getType() == Material.CHEST) {
				Chest chest = (Chest) block.getState();
				miniFeastChest.add(chest);
				System.out.println("Mini feast chest adicionado na array list.");
			}
		}
		main.debug.sendMessage("Sistema de minifeast funcionando com sucesso.", TypeAlert.SUCESSO);
	}

	public void setMiniFeast() {
		for (Location location : miniFeasts) {
			for (Block block : getNearbyBlocks(location, 2)) {
				miniFeastBlock.add(block);
			}
		}
	}

	public void inializeFeast() {
		for (Iterator<?> iterator = feastBlock.iterator(); iterator.hasNext();) {
			Block block = (Block) iterator.next();
			if (block.getType() == Material.CHEST) {
				Chest chest = (Chest) block.getState();
				feastChest.add(chest);
				System.out.println("Feast chest adicionado na array list.");
			}
		}
		main.debug.sendMessage("Sistema de feast funcionando com sucesso.", TypeAlert.SUCESSO);
	}

	public void setFeast() {
		for (Block block : getNearbyBlocks(feast, WeavenSkywars.getInstance().getConfig().getInt("R.feast"))) {
			feastBlock.add(block);
		}
	}
	
	
	public void vaiTomarNoCUKing() {
		for (int i = 0; i < 12; i++) {
			if (!WeavenSkywars.getChest().contains("feast."+i))
				return;
			String[] feastSla = WeavenSkywars.getChest().getString("feast."+i).split(",");
			int x = Integer.valueOf(feastSla[0]);
			int y = Integer.valueOf(feastSla[1]);
			int z = Integer.valueOf(feastSla[2]);
			Chest bloco = (Chest) new Location(Bukkit.getWorld("mapa"), x, y, z).getBlock().getState();
			feastChest.add(bloco);

		}
	}

	public void startPartida() {
		state = GameState.JAIL;
		Bukkit.getOnlinePlayers().forEach(players -> {
			if (GameController.spectador.contains(players.getUniqueId())) {
				players.teleport(feast);
			}
		});
		assignSpawnPositions();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(players, "§c5 segundos", "§epara liberar as jaulas");
			});
		}, 1 * 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(players, "§c4 segundos", "§epara liberar as jaulas");
			});
		}, 2 * 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(players, "§c3 segundos", "§epara liberar as jaulas");
			});
		}, 3 * 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(players, "§c2 segundos", "§epara liberar as jaulas");
			});
		}, 4 * 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(players, "§c1 segundos", "§epara liberar as jaulas");
			});
		}, 5 * 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
			new Jogo();
			removeJails();
			Bukkit.broadcastMessage("§eO jogo iniciou!");
			Bukkit.getOnlinePlayers().forEach(players -> {
				if (GameController.player.contains(players.getUniqueId())) {
					TitleAPI.setTitle(players, "§cPartida iniciada", "§eBoa sorte!");
					state = GameState.GAME;
					Scoreboarding.setScoreboard(players);
					players.closeInventory();
					players.getActivePotionEffects().clear();
					players.getInventory().clear();
					players.getInventory().setArmorContents(null);
					players.setGameMode(GameMode.SURVIVAL);
					players.setAllowFlight(false);
					players.setHealth(20.0D);
					players.setFoodLevel(20);
					players.setFireTicks(0);
					players.setLevel(0);
					players.updateInventory();
					kitAPI.setItens(players);
					habilityAPI.setItens(players);
					players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 9999));
					if (habilityAPI.getHabilidade(players) != Hability.FENIX) {
						fenixLocation.remove(players.getUniqueId());
					}
					TagAPI.update(players);
				} else {
					BukkitPlayer bPlayer = BukkitPlayer.getPlayer(players.getUniqueId());
					if (!bPlayer.hasGroupPermission(Group.TRIAL)) {
						setSpectador(players);
					} else {
						AdminMode.getInstance().setAdmin(players);
					}
				}
				sendBorder(players, feast.getX(), feast.getZ(), borda8);
			});
		}, 6 * 20);
	}

	public void setSpectador(Player player) {
		if (!GameController.spectador.contains(player.getUniqueId())) {
			GameController.spectador.add(player.getUniqueId());
			TitleAPI.setTitle(player, "§cVocê morreu!", "§eVocê agora é um espectador.");
		} else {
			TitleAPI.setTitle(player, "§aSeja bem-vindo", "§eVocê agora é um espectador.");
		}
		updatePlayerCheck(player);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		player.getInventory().setItem(0, new ItemBuilder().type(Material.BOOK).name("§6Jogadores §7(Clique)").build());
		player.getInventory().setItem(1,
				new ItemBuilder().type(Material.PAPER).name("§aPartida rápida §7(Clique)").build());
		player.getInventory().setItem(8,
				new ItemBuilder().type(Material.BED).name("§cVoltar para o lobby §7(Clique)").build());
		player.updateInventory();
		player.setVelocity(new Vector(0, 1, 0));
		player.setGameMode(GameMode.ADVENTURE);
		new BukkitRunnable() {

			@Override
			public void run() {
				if (state == GameState.GAME || state == GameState.END)
					player.teleport(feast.clone().add(10, 5, 10));
			}
		}.runTaskLater(main, 3L);
		new BukkitRunnable() {

			@Override
			public void run() {
				player.setAllowFlight(true);
			}
		}.runTaskLater(main, 5L);
		player.sendMessage("§a§l   RECOMPENSAS   ");
		player.sendMessage("  ");
		player.sendMessage(" §6+5 moedas §fpor participação");
		BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
		int killsMoney = 5 * PlayerController.kills.get(player.getUniqueId());
		if (PlayerController.kills.get(player.getUniqueId()) > 0) {
			player.sendMessage(" §6+" + killsMoney + " moedas §fpor eliminação.");
			bPlayer.addMoney(5 + killsMoney);
		} else {
			bPlayer.addMoney(5);
		}
		player.sendMessage("  ");
		bPlayer.getDataHandler().save(DataCategory.BALANCE);
		Bukkit.getOnlinePlayers().forEach(players -> {
			if (GameController.player.contains(players.getUniqueId())) {
				players.hidePlayer(player);
				return;
			}
		});
	}

	public boolean checkWinner() {
		if (GameController.player.size() == 0) {
			Bukkit.shutdown();
			return false;
		}
		if (GameController.player.size() > 1) {
			return false;
		}
		if (winner != null) {
			return false;
		}
		if (GameController.player.size() == 1 && winner == null) {
			state = GameState.END;
			winner = Bukkit.getPlayer(GameController.player.get(0));
			winner.setAllowFlight(true);
			winner.getInventory().clear();
			winner.getInventory().setArmorContents(null);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
				spawnsPoints.forEach(locations -> {
					spawnRandomFirework(locations);
				});
				spawnRandomFirework(feast);
			}, 0L, 20L);
			Bukkit.getOnlinePlayers().forEach(players -> {
				TitleAPI.setTitle(winner, "§c" + winner.getName(), "§eganhou a partida!");
				players.teleport(feast.clone().add(10, 5, 10));
				Scoreboarding.setScoreboard(players);
				if (!AdminMode.getInstance().isAdmin(players)) {
					players.showPlayer(winner);
				}
			});
			winner.sendMessage("§a§l   RECOMPENSAS   ");
			winner.sendMessage("  ");
			winner.sendMessage(" §6+15 moedas §fpor vitória");
			winner.sendMessage(" §6+5 moedas §fpor participação");
			BukkitPlayer bPlayer = BukkitPlayer.getPlayer(winner.getUniqueId());
			int killsMoney = 5 * PlayerController.kills.get(winner.getUniqueId());
			if (PlayerController.kills.get(winner.getUniqueId()) > 0) {
				winner.sendMessage(" §6+" + killsMoney + " moedas §fpor eliminação.");
				bPlayer.addMoney(5 + killsMoney + 15);
			} else {
				bPlayer.addMoney(5 + 15);
			}
			winner.setHealth(20.0D);
			winner.setFoodLevel(20);
			winner.setFireTicks(0);
			winner.setLevel(0);
			winner.sendMessage("  ");
			bPlayer.getDataHandler().save(DataCategory.BALANCE);
			bPlayer.getData(DataType.SKYWARS_SOLO_WINS)
					.setValue(bPlayer.getData(DataType.SKYWARS_SOLO_WINS).asInt() + 1);
			bPlayer.getDataHandler().save(DataCategory.SKYWARS);
			winner.getInventory().setItem(0,
					new ItemBuilder().type(Material.PAPER).name("§aPartida rápida §7(Clique)").build());
			winner.getInventory().setItem(8,
					new ItemBuilder().type(Material.BED).name("§cVoltar para o lobby §7(Clique)").build());
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getOnlinePlayers().forEach(players -> {
						findServer(players, ServerType.SKYWARS);
					});

				}
			}.runTaskLater(main, 12 * 20);
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.getOnlinePlayers().forEach(players -> {
						sendLobby(players);
					});

				}
			}.runTaskLater(main, 14 * 20);
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.shutdown();

				}
			}.runTaskLater(main, 15 * 20);
			return true;
		}
		return false;
	}

	public List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	public void assignSpawnPositions() {
		int id = 0;
		for (UUID uuid : GameController.player) {
			try {
				Player gamePlayer = Bukkit.getPlayer(uuid);
				double x = spawnsPoints.get(id).getX(), z = spawnsPoints.get(id).getZ();
				double y = spawnsPoints.get(id).getY();
				System.out.println(id + ": " + spawnsPoints.get(id).getY());
				Location location = new Location(spawnsPoints.get(id).getWorld(), x, y, z);
				fenixLocation.put(uuid, location);
				gamePlayer.teleport(location.clone().add(0.6, 0, 0.5));
				id += 1;
				gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
				gamePlayer.getPlayer().setHealth(gamePlayer.getPlayer().getMaxHealth());
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createJail(Location location) {
		Block cima = location.clone().add(0, 3, 0).getBlock();
		cima.setType(Material.GLASS);
		GameController.jailBlock.add(cima);

		Block baixo = location.clone().add(0, -1, 0).getBlock();
		baixo.setType(Material.GLASS);
		GameController.jailBlock.add(baixo);

		// 1
		Block ba1 = location.clone().add(1, 2, 0).getBlock();
		ba1.setType(Material.GLASS);
		GameController.jailBlock.add(ba1);

		Block ba2 = location.clone().add(-1, 2, 0).getBlock();
		ba2.setType(Material.GLASS);
		GameController.jailBlock.add(ba2);

		Block ba3 = location.clone().add(0, 2, 1).getBlock();
		ba3.setType(Material.GLASS);
		GameController.jailBlock.add(ba3);

		Block ba4 = location.clone().add(0, 2, -1).getBlock();
		ba4.setType(Material.GLASS);
		GameController.jailBlock.add(ba4);

		// 1
		Block bb1 = location.clone().add(1, 1, 0).getBlock();
		bb1.setType(Material.GLASS);
		GameController.jailBlock.add(bb1);

		Block bb2 = location.clone().add(-1, 1, 0).getBlock();
		bb2.setType(Material.GLASS);
		GameController.jailBlock.add(bb2);

		Block bb3 = location.clone().add(0, 1, 1).getBlock();
		bb3.setType(Material.GLASS);
		GameController.jailBlock.add(bb3);

		Block bb4 = location.clone().add(0, 1, -1).getBlock();
		bb4.setType(Material.GLASS);
		GameController.jailBlock.add(bb4);

		// 0
		Block bc1 = location.clone().add(1, 0, 0).getBlock();
		bc1.setType(Material.GLASS);
		GameController.jailBlock.add(bc1);

		Block bc2 = location.clone().add(-1, 0, 0).getBlock();
		bc2.setType(Material.GLASS);
		GameController.jailBlock.add(bc2);

		Block bc3 = location.clone().add(0, 0, 1).getBlock();
		bc3.setType(Material.GLASS);
		GameController.jailBlock.add(bc3);

		Block bc4 = location.clone().add(0, 0, -1).getBlock();
		bc4.setType(Material.GLASS);
		GameController.jailBlock.add(bc4);

		location.clone().add(0, 0, 0).getBlock().setType(Material.AIR);
		location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
		location.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
	}

	public void removeJails() {
		for (Iterator<?> iterator = GameController.jailBlock.iterator(); iterator.hasNext();) {
			Block block = (Block) iterator.next();
			block.setType(Material.AIR);
		}
	}

	public void createJail() {
		for (Location location : spawnsPoints) {
			createJail(location.add(0, 9, 0));
		}
	}

	public void spawnRandomFirework(Location loc) {
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();
		int rt = r.nextInt(4) + 3;
		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) {
			type = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
			type = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
			type = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
			type = FireworkEffect.Type.CREEPER;
		} else if (rt == 5) {
			type = FireworkEffect.Type.STAR;
		} else if (rt > 5) {
			type = FireworkEffect.Type.BALL_LARGE;
		}
		Color c1 = Color.RED;
		Color c2 = Color.LIME;
		Color c3 = Color.SILVER;
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withColor(c2)
				.withFade(c3).with(type).trail(r.nextBoolean()).build();
		fwm.addEffect(effect);
		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);
		fw.setFireworkMeta(fwm);
	}

	public void sendLobby(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF("lobby-sw");
		player.sendPluginMessage(WeavenSkywars.getInstance(), "BungeeCord", out.toByteArray());
	}

	public void findServer(Player player, ServerType type) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("FindServer");
		out.writeUTF(type.name());
		player.sendPluginMessage(WeavenSkywars.getInstance(), "BungeeCord", out.toByteArray());
	}

	public static String getGroup(Player player) {
		BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
		if (bPlayer.getGroup() == Group.DONO) {
			return "§4" + player.getName();
		} else if (bPlayer.getGroup() == Group.DIRETOR) {
			return "§4" + player.getName();
		} else if (bPlayer.getGroup() == Group.ADMIN) {
			return "§c" + player.getName();
		} else if (bPlayer.getGroup() == Group.MODPLUS) {
			return "§5" + player.getName();
		} else if (bPlayer.getGroup() == Group.MODGC) {
			return "§5" + player.getName();
		} else if (bPlayer.getGroup() == Group.MOD) {
			return "§5" + player.getName();
		} else if (bPlayer.getGroup() == Group.YOUTUBERPLUS) {
			return "§3" + player.getName();
		} else if (bPlayer.getGroup() == Group.TRIAL) {
			return "§5" + player.getName();
		} else if (bPlayer.getGroup() == Group.BUILDER) {
			return "§e" + player.getName();
		} else if (bPlayer.getGroup() == Group.DESIGNER) {
			return "§2" + player.getName();
		} else if (bPlayer.getGroup() == Group.YOUTUBER) {
			return "§b" + player.getName();
		} else if (bPlayer.getGroup() == Group.ULTRA) {
			return "§d" + player.getName();
		} else if (bPlayer.getGroup() == Group.BETA) {
			return "§1" + player.getName();
		} else if (bPlayer.getGroup() == Group.PRO) {
			return "§6" + player.getName();
		} else if (bPlayer.getGroup() == Group.BLADE) {
			return "§e" + player.getName();
		} else if (bPlayer.getGroup() == Group.VIP) {
			return "§a" + player.getName();

		} else if (bPlayer.getGroup() == Group.WINNER) {
			return "§2" + player.getName();
		} else {
			return "§7" + player.getName();
		}
	}

	public void sendBorder(Player p, double x, double z, double radius) {
		final WorldBorder worldBorder = new WorldBorder();
		worldBorder.setCenter(x, z);
		worldBorder.setSize(radius * 2);
		worldBorder.setWarningDistance(0);
		final EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
		entityPlayer.playerConnection.sendPacket(
				new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
		entityPlayer.playerConnection.sendPacket(
				new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder,
				PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
		System.out.println(radius * 2);
	}

	public EventsState getEState() {
		return eState;
	}

	public enum GameState {
		LOBBY, JAIL, GAME, END
	}

	@Getter
	@AllArgsConstructor
	public enum EventsState {
		NONE("Nenhum"), REFIL1("Refil"), REFIL2("Refil"), ENDING("Fim");
		String name;
	}

}
