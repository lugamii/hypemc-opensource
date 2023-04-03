package br.com.adlerlopes.bypiramid.hungergames.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.bo2.BO2Constructor;
import br.com.adlerlopes.bypiramid.hungergames.commands.ModeratorCommand;
import br.com.adlerlopes.bypiramid.hungergames.game.arena.ArenaManager;
import br.com.adlerlopes.bypiramid.hungergames.game.handler.GameManager;
import br.com.adlerlopes.bypiramid.hungergames.game.handler.listeners.SoupListener;
import br.com.adlerlopes.bypiramid.hungergames.logger.Logger;
import br.com.adlerlopes.bypiramid.hungergames.manager.managers.ClassManager;
import br.com.adlerlopes.bypiramid.hungergames.manager.managers.FileManager;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.GamerManager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.KitManager;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.AwardManager;
import br.com.adlerlopes.bypiramid.hungergames.player.scoreboard.Scoreboarding;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Item;

public class Manager {

	private HungerGames plugin;
	private Utils utils;
	private BO2Constructor bo2;
	private final String serverName;
	private final String serverIP;
	private FileManager fileManager;
	private GamerManager gamerManager;
	private GameManager gameManager;
	private KitManager kitManager;
	@Getter
	@Setter
	private Scoreboarding scoreboardManager;
	private AwardManager awardManager;
	private ArenaManager arenaManager;
	private ClassManager classManager;
	private Random random;
	private int time;
	private boolean doubleKit = true;
	public static String serverId;
	public static long delay;
	public static boolean send = true;
	
	public static Location spawnarArenaFinal() {
		int x = 0, y = 0, z = 0;
		Location loc = Bukkit.getWorld("world").getBlockAt(0, 15, 0).getLocation();
		List<Location> cuboid = new ArrayList<>();
		cuboid.clear();
		for (x = -30; x < 30; x++)
			for (y = 0; y < 80; y++)
				for (z = -30; z < 30; z++)
					loc.clone().add(x, y, z).getBlock().setType(Material.AIR);

		for (int bX = -30; bX <= 30; bX++)
			for (int bZ = -30; bZ <= 30; bZ++)
				for (int bY = -1; bY <= 130; bY++)
					if (bY == -1) {
						cuboid.add(loc.clone().add(bX, bY, bZ));
					} else if ((bX == -30) || (bZ == -30) || (bX == 30) || (bZ == 30)) {
						cuboid.add(loc.clone().add(bX, bY, bZ));
					}

		for (Location loc1 : cuboid)
			loc1.getBlock().setType(Material.BEDROCK);
		
		ModeratorCommand.generateArena(loc, 130, 500, Material.BEDROCK.getId());
		
		return loc;
		
		
	}
	public Manager(HungerGames hungerGames) {
		this.plugin = ((HungerGames) HungerGames.getPlugin(HungerGames.class));
		this.plugin.saveDefaultConfig();

		doubleKit = this.plugin.getConfig().getBoolean("doublekit");

		serverId = this.plugin.getConfig().getString("server");

		getLogger().log("Starting the plugin " + this.plugin.getName() + " version "
				+ this.plugin.getDescription().getVersion() + "...");
		if (!this.plugin.TEST_SERVER()) {
			getLogger().log("Starting to loading all the chunks of the world.");

			getLogger().log("The chunks that will be used, was loaded.");
		}
		this.serverName = new File(getPlugin().getDataFolder().getParentFile().getAbsolutePath()).getParentFile()
				.getName().toUpperCase();

		this.serverIP = (serverId + ".weavenmc.com.br");

		getLogger().log("Making connection with plugin " + getPlugin().getName() + " version "
				+ getPlugin().getDescription().getVersion() + ".");

		this.utils = new Utils();

		this.random = new Random();

		this.bo2 = new BO2Constructor(this);
		if (!this.bo2.correctlyStart()) {
			return;
		}
		this.fileManager = new FileManager(this);
		if (!this.fileManager.correctlyStart()) {
			return;
		}
		this.kitManager = new KitManager(this);
		if (!this.kitManager.correctlyStart()) {
			return;
		}

		this.gamerManager = new GamerManager(this);
		if (!this.gamerManager.correctlyStart()) {
			return;
		}
		this.gameManager = new GameManager(this);
		if (!this.gameManager.correctlyStart()) {
			return;
		}
		this.awardManager = new AwardManager(this);
		if (!this.awardManager.correctlyStart()) {
			return;
		}
		this.arenaManager = new ArenaManager(this);
		if (!this.arenaManager.correctlyStart()) {
			return;
		}

		this.classManager = new ClassManager(this);
		if (!this.classManager.correctlyStart()) {
			return;
		}
		SoupListener.createSoups();
		getPlugin().getServer().setWhitelist(false);

		Random random = new Random();
		int rnd = random.nextInt(600);

		this.time = (3600 + rnd);

		new BukkitRunnable() {
			public void run() {
				if ((time <= 0) && (getGameManager().isPreGame()) && (Bukkit.getOnlinePlayers().size() == 0)) {
					Bukkit.shutdown();
					cancel();
					return;
				}
				time -= 1;
			}
		}.runTaskTimer(getPlugin(), 0L, 20L);

		new BukkitRunnable() {
			public void run() {
				Bukkit.getPluginManager().callEvent(new ServerTimeEvent());
			}
		}.runTaskTimer(getPlugin(), 20L, 20L);

		getKitManager().registerKits();
		getLogger().log("The plugin " + this.plugin.getName() + " version " + this.plugin.getDescription().getVersion()
				+ " was started correcly.");
	}

	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, getPlugin());
	}

	public String getServerIP() {
		return this.serverIP;
	}

	public boolean isDoubleKit() {
		return doubleKit;
	}

	public BO2Constructor getBO2() {
		return this.bo2;
	}

	public String getServerName() {
		return this.serverName;
	}

	public ArenaManager getArenaManager() {
		return this.arenaManager;
	}

	public AwardManager getSurpriseKitManager() {
		return this.awardManager;
	}

	public Logger getLogger() {
		return getPlugin().getLoggerSecon();
	}

	public FileConfiguration getConfig() {
		return this.plugin.getConfig();
	}

	public HungerGames getPlugin() {
		return this.plugin;
	}

	public Utils getUtils() {
		return this.utils;
	}

	public GamerManager getGamerManager() {
		return this.gamerManager;
	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	public KitManager getKitManager() {
		return this.kitManager;
	}

	public Random getRandom() {
		return this.random;
	}

	public ClassManager getClassLoader() {
		return this.classManager;
	}

	public FileManager getFileManager() {
		return this.fileManager;
	}
}
