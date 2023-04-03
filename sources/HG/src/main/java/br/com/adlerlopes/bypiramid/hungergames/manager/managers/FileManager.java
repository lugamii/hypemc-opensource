package br.com.adlerlopes.bypiramid.hungergames.manager.managers;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FileManager extends Management {

	private static final World world = Bukkit.getWorld("world");

	private static final String FINAL_ARENA_FILE_NAME = "final-arena.bo2";
	private static final String COLISEUM_FILE_NAME = "coliseum.bo2";
	private static final String FEAST_FILE_NAME = "feast.bo2";
	private static final String CARE_PACKET_FILE_NAME = "care-packet.bo2";
	private static final String MINI_FEAST_FILE_NAME = "mini-feast.bo2";
	private static final String CAKE_FILE_NAME = "cake-image.png";

	private File finalArenaFile, coliseumFile, feastFile, carePacketFile, miniFeastFile, cakeFile;

	public FileManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {

		this.finalArenaFile = new File(getManager().getPlugin().getDataFolder(), FINAL_ARENA_FILE_NAME);
		this.coliseumFile = new File(getManager().getPlugin().getDataFolder(), COLISEUM_FILE_NAME);
		this.feastFile = new File(getManager().getPlugin().getDataFolder(), FEAST_FILE_NAME);
		this.carePacketFile = new File(getManager().getPlugin().getDataFolder(), CARE_PACKET_FILE_NAME);
		this.miniFeastFile = new File(getManager().getPlugin().getDataFolder(), MINI_FEAST_FILE_NAME);
		this.cakeFile = new File(getManager().getPlugin().getDataFolder(), CAKE_FILE_NAME);

		if (!finalArenaFile.exists()) {
			getLogger().log(Level.WARNING, "The bo2 file '%s' don't exists. Creating...", FINAL_ARENA_FILE_NAME);
			getManager().getPlugin().saveResource(FINAL_ARENA_FILE_NAME, true);
		}

		if (!coliseumFile.exists()) {
			getLogger().log(Level.WARNING, "The bo2 file '%s' don't exists. Creating...", COLISEUM_FILE_NAME);
			getManager().getPlugin().saveResource(COLISEUM_FILE_NAME, true);
		}

		if (!feastFile.exists()) {
			getLogger().log(Level.WARNING, "The bo2 file '%s' don't exists. Creating...", FEAST_FILE_NAME);
			getManager().getPlugin().saveResource(FEAST_FILE_NAME, true);
		}

		if (!carePacketFile.exists()) {
			getLogger().log(Level.WARNING, "The bo2 file '%s' don't exists. Creating...", CARE_PACKET_FILE_NAME);
			getManager().getPlugin().saveResource(CARE_PACKET_FILE_NAME, true);
		}

		if (!miniFeastFile.exists()) {
			getLogger().log(Level.WARNING, "The bo2 file '%s' don't exists. Creating...", MINI_FEAST_FILE_NAME);
			getManager().getPlugin().saveResource(MINI_FEAST_FILE_NAME, true);
		}

		if (!cakeFile.exists()) {
			getLogger().log(Level.WARNING, "The file '%s' don't exists. Creating...", CAKE_FILE_NAME);
			getManager().getPlugin().saveResource(CAKE_FILE_NAME, true);
		}
		return true;
	}

	public File getFinalArenaFile() {
		return finalArenaFile;
	}

	public File getColiseumFile() {
		return coliseumFile;
	}

	public File getFeastFile() {
		return feastFile;
	}

	public File getCarePacketFile() {
		return carePacketFile;
	}

	public File getMiniFeastFile() {
		return miniFeastFile;
	}

	public File getServerImageFile() {
		return cakeFile;
	}

	public static World getWorld() {
		return world;
	}

}
