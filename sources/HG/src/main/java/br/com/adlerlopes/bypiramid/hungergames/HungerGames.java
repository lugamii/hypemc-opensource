package br.com.adlerlopes.bypiramid.hungergames;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;

import br.com.adlerlopes.bypiramid.hungergames.game.handler.listeners.GameListener;
import br.com.adlerlopes.bypiramid.hungergames.logger.Logger;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.scoreboard.Scoreboarding;
import br.com.adlerlopes.bypiramid.hungergames.schematic.DataException;
import br.com.adlerlopes.bypiramid.hungergames.schematic.Schematic;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.server.ServerType;

/**
 * Copyright (C) LittleMC, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class HungerGames extends JavaPlugin {

	private static Manager manager;
	private boolean TEST_SERVER;

	public static final ArrayList<Block> coliseu = new ArrayList<>();
	private static Location spawn;

	private static Logger logger;
	public static boolean SINGLE_KIT = false;
	public static Schematic mini_feast;
	public void onEnable() {
		super.onEnable();

		logger.log("Removing all entitys...");
		removingAllEntitys();

		logger.log("Loading chunks...");
		loadChunks();

		manager = new Manager(this);

		Schematic s;

		logger.log("Loading the coliseum file...");
		try {
			s = Schematic.getInstance().loadSchematic(new File(getDataFolder(), "coliseu.schematic"));
		} catch (DataException | IOException e) {
			e.printStackTrace();
			getServer().shutdown();
			return;
		}
		logger.log("Coliseum file loaded!");

		if (s != null) {
			logger.log("Spawning the coliseu...");
			Schematic.getInstance().newSchematic(getServer().getWorlds().get(0),
					new Location(getServer().getWorlds().get(0), -27.5D, 80.0D, -27.5D), s, coliseu);
			logger.log("Coliseum spawned!");

			findSpawn();
		}
		try {
			mini_feast = Schematic.getInstance().loadSchematic(
					new File(HungerGames.getManager().getPlugin().getDataFolder(), "minifeast.schematic"));
		} catch (IOException | DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new CommandLoader(new BukkitCommandFramework(this))
				.loadCommandsFromPackage("br.com.adlerlopes.bypiramid.hungergames.commands");

		findDoors();

		getManager().setScoreboardManager(new Scoreboarding());

		/*
		 * Clearing entitys
		 */

		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				removingAllEntitys();
			}
		}.runTaskTimerAsynchronously(this, 0, 10 * 40l);
	}

	public void onDisable() {
		super.onDisable();
	}

	@Override
	public File getFile() {
		return super.getFile();
	}

	public void removingAllEntitys() {
		if (GameListener.dropedByPlayer.isEmpty()) {
			for (Entity entity : Bukkit.getWorld("world").getEntities()) {
				if (entity instanceof Player)
					return;
				entity.remove();
			}
		} else {
			for (Entity entity : Bukkit.getWorld("world").getEntities()) {
				if (entity instanceof Player)
					return;
				if (!GameListener.dropedByPlayer.contains(entity))
					entity.remove();
			}
		}

	}

	public void onLoad() {
		super.onLoad();

		logger = new Logger(getLogger(), null, true);
		logger.log("Starting to load the plugin.");

		TEST_SERVER = getConfig().getBoolean("test_server");
		if (!TEST_SERVER) {
			getServer().unloadWorld("world", false);
			deleteDir(new File("world"));

			String worldName = "arena";
			File file = new File(worldName);
			deleteDir(file);

			String worldName2 = "arena_bk";
			File file2 = new File(worldName2);

			copyFolder(file2, file);

			for (World world : Bukkit.getWorlds()) {
				world.setThundering(false);
				world.setStorm(false);
				world.setAutoSave(false);
				world.setTime(0L);
			}
		}
	}

	public boolean copyFolder(File src, File dest) {
		return false;
	}

	public boolean deleteFile(Path path) {
		Preconditions.checkNotNull(path);
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file, IOException e) {
					return handleException(e);
				}

				private FileVisitResult handleException(IOException e) {
					e.printStackTrace();
					return FileVisitResult.TERMINATE;
				}

				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if (e != null)
						return handleException(e);
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
			return true;
		} catch (IOException e) {
			getManager().getLogger().error("Error when the plugin is trying to delete the path " + path, e);
			return false;
		}
	}

//	public void findSpawn() {
//		if (spawn == null) {
//			spawn = new Location(Bukkit.getWorld("world"), 0, 120, 0);
//		}
//	}

	static ArrayList<Block> doors = new ArrayList<Block>();
	static ArrayList<Block> breakBlocks = new ArrayList<Block>();

	@SuppressWarnings("deprecation")
	public void findDoors() {
		for (Block b : HungerGames.coliseu) {

			if (b.getTypeId() == 33) {
				doors.add(b);
			}
			if (b.getType() != Material.AIR)
				breakBlocks.add(b);
		}
	}

	public void findSpawn() {

		if (HungerGames.spawn == null) {
			for (final Block b : HungerGames.coliseu) {
				if (b.getChunk().isLoaded() == false)
					b.getChunk().load(true);
				if (b.getType() == Material.EMERALD_BLOCK) {
					HungerGames.spawn = new Location(b.getWorld(), (double) b.getLocation().getBlockX(),
							(double) b.getLocation().getBlockY(), (double) b.getLocation().getBlockZ()).add(0.0, 5.0,
									0.0);
					return;
				}
			}
			if (HungerGames.spawn == null) {
				HungerGames.spawn = Bukkit.getWorld("world").getSpawnLocation();
			}

		}
	}

	public static Location getSpawn() {
		return spawn;
	}

	public static void teleportSpawn(Player player) {
		player.teleport(spawn);
	}

	public static void removeColiseu2() {
		int a = 0;
		boolean hasOtherBlock = false;
		for (Block b : breakBlocks) {
			if (a >= 2500)
				break;
			if (b.getType() != Material.AIR) {
				if (!hasOtherBlock)
					hasOtherBlock = true;
				getManager().getBO2().setBlockFast(b.getLocation(), Material.AIR, (byte) 0);
				a++;
			}
		}

		if (!hasOtherBlock)
			coliseu.clear();
	}

	public static void removeColiseu(int time) {
		if (time % 30 == 0) {
			removeColiseu2();
		}
	}

	public static void openColiseu() {
		for (Block door : doors)
			door.setType(Material.AIR);
	}

	public boolean TEST_SERVER() {
		return TEST_SERVER;
	}

	public static Manager getManager() {
		return manager;
	}

	public Logger getLoggerSecon() {
		return logger;
	}

	public void loadChunks() {
		World world = Bukkit.getWorld("world");
		for (int x = -523; x <= 523; x += 16) {
			for (int z = -623; z <= 523; z += 16) {
				world.getBlockAt(x, 64, z).getChunk().load(true);

			}
		}
	}

	public void deleteDir(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(file, children[i]));
			}
		}
		file.delete();
	}

}
