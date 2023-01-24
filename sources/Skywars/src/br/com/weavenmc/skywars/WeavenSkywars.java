package br.com.weavenmc.skywars;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.scheduler.UpdateScheduler;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.skywars.automatic.ClassGetter;
import br.com.weavenmc.skywars.game.GameManager;
import br.com.weavenmc.skywars.nbt.Schematic;
import br.com.weavenmc.skywars.utils.ChestManager;
import br.com.weavenmc.skywars.utils.Debug;
import br.com.weavenmc.skywars.utils.Debug.TypeAlert;
import br.com.weavenmc.skywars.utils.SpawnsManager;
import br.com.weavenmc.skywars.utils.WorldChunk;
import br.com.weavenmc.timer.Iniciando;
import lombok.Getter;

public class WeavenSkywars extends JavaPlugin {

	private static WeavenSkywars instance;

	public static WeavenSkywars getInstance() {
		return instance;
	}

	private static GameManager gameManager;

	public static GameManager getGameManager() {
		return gameManager;
	}

	@Getter
	private static SpawnsManager spawnsManager;
	@Getter
	private static ChestManager chestManager;

	public File spawns1;
	@Getter
	public static YamlConfiguration spawns;

	public File chest1;
	@Getter
	public static YamlConfiguration chest;

	public static boolean enabled;
	public static ArrayList<Block> blockMap = new ArrayList<>();
	public Debug debug;

	@Override
	public void onLoad() {
		debug = new Debug();
		debug.sendMessage("O plugin esta em sua fase de load...", TypeAlert.ALERTA);
		delete(new File("mapa"));
	}

	@Override
	public void onEnable() {
		debug.sendMessage("O plugin esta em sua fase de enable...", TypeAlert.ALERTA);
		instance = this;
		enabled = true;
		saveDefaultConfig();
		makeWorld("lobby", false);
		makeWorld("mapa", true);
		gameManager = new GameManager(this);
		createFiles();
		saveConfig();
		spawnsManager = new SpawnsManager();
		spawnsManager.updateEnabled();
		chestManager = new ChestManager();
		chestManager.updateEnabled();
		new Iniciando();
		ClassGetter.loadListenerBukkit();
		ClassGetter.loadCommandBukkit();
		new UpdateScheduler().run();
		Schematic.setupShematics();
		getServer().getScheduler().runTaskTimer(this, new UpdateScheduler(), 1, 1);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		try {
			Schematic schematic = Schematic.getInstance()
					.carregarSchematics(new File(this.getDataFolder(), "waiting.schematic"));
			schematic.generateSchematic(Bukkit.getWorld("lobby"), new Location(Bukkit.getWorld("lobby"), 0, 100, 0),
					schematic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Schematic schematic = Schematic.getInstance()
					.carregarSchematics(new File(this.getDataFolder(), "skywars.schematic"));
			schematic.generateSchematic(Bukkit.getWorld("mapa"), new Location(Bukkit.getWorld("mapa"), 0, 100, 0),
					schematic, blockMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Bukkit.getWorlds().forEach(worlds -> {
			worlds.getEntities().forEach(entity -> {
				if (entity instanceof Item)
					entity.remove();
			});
		});
		gameManager.startConfig();

		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (Bukkit.getOnlinePlayers().isEmpty()) {
					try {
						PreparedStatement stm = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `" + DataCategory.HUNGERGAMES.getTableName()
										+ "` ORDER BY `" + DataType.HG_DEATHS.getField() + "` desc LIMIT 5;");
						stm.executeQuery().close();
						stm.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}.runTaskTimerAsynchronously(getInstance(), 0, 5);

	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(players -> {
			gameManager.sendLobby(players);
		});
		HandlerList.unregisterAll();
		instance = null;
	}

	public void createFiles() {
		File spawns = new File(getDataFolder(), "spawns.yml");
		if (!spawns.exists())
			saveResource("spawns.yml", false);
		spawns1 = new File(getDataFolder(), "spawns.yml");
		WeavenSkywars.spawns = YamlConfiguration.loadConfiguration(spawns1);

		File chest = new File(getDataFolder(), "chest.yml");
		if (!chest.exists())
			saveResource("chest.yml", false);
		chest1 = new File(getDataFolder(), "chest.yml");
		WeavenSkywars.chest = YamlConfiguration.loadConfiguration(chest1);
	}

	public void saveFiles() {
		try {
			spawns.save(spawns1);
			chest.save(chest1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
	}

	private void delete(File delete) {
		System.out.println("[World-Debug] Iniciando a remocao do mundo 'mapa'.");
		if (delete.isDirectory()) {
			String[] lista = delete.list();
			for (int i = 0; i < lista.length; i++) {
				delete(new File(delete, lista[i]));
			}
		}
		delete.delete();
		System.out.println("[World-Debug] Mundo 'mapa' deletado com sucesso.");
	}

	private void makeWorld(String world, boolean pvp) {
		String pvpMode = pvp == true ? "§aligado" : "§cdesligado";
		debug.sendMessage("Criando mundo: " + world, TypeAlert.ALERTA);
		WorldCreator wc = new WorldCreator(world);
		wc.generateStructures(false);
		wc.generator(new WorldChunk());
		Bukkit.getServer().createWorld(wc);
		debug.sendMessage("Mundo criado: §7" + world, TypeAlert.ALERTA);
		Bukkit.getServer().getWorld(world).setPVP(pvp);
		debug.sendMessage("PVP: " + pvpMode, TypeAlert.ALERTA);
		Bukkit.getServer().getWorld(world).setTime(1000L);
		debug.sendMessage("Tempo: §f1000L", TypeAlert.ALERTA);
		Bukkit.getServer().getWorld(world).setAutoSave(false);
		debug.sendMessage("Auto save: §cdesligado", TypeAlert.ALERTA);
		// Bukkit.getServer().getWorld(world).setGameRuleValue("doMobSpawning",
		// "false");
		debug.sendMessage("Spawn de mob's: §cdesligado", TypeAlert.ALERTA);
		Bukkit.getServer().getWorld(world).setGameRuleValue("doDaylightCycle", "false");
		debug.sendMessage("Clico de luz: §cdesligado", TypeAlert.ALERTA);
		debug.sendMessage("Mundo '§7" + world + "§a' criado com sucesso!", TypeAlert.SUCESSO);
	}

}
