package br.com.adlerlopes.bypiramid.hungergames.bo2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;
import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;
import net.minecraft.server.v1_8_R3.BlockPosition;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class BO2Constructor extends Management {

	private static HashSet<Location> blocksForUpdate = new HashSet<>();
	private ArenaConstructor arena;
	private AsyncWorldEdit asyncWorldEdit = AsyncWorldEdit.getInstance();

	public BO2Constructor(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		this.arena = new ArenaConstructor(this);
		//startUpdate();
		return true;
	}

	public ArenaConstructor getArena() {
		return arena;
	}

	public void addBlockUpdate(Location location) {
		blocksForUpdate.add(location);
	}

	public void startUpdate() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (blocksForUpdate.isEmpty())
					return;
				
				if (!blocksForUpdate.isEmpty()) {
					net.minecraft.server.v1_8_R3.World world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
					for (Location location : blocksForUpdate) {
						world.notify(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
					}
					blocksForUpdate.clear();
				}

				if (!blocksForUpdate.isEmpty()) {
					if (Bukkit.getWorld("gladiator") != null) {
						net.minecraft.server.v1_8_R3.World world = ((CraftWorld) Bukkit.getWorlds().get(1)).getHandle();
						for (Location location : blocksForUpdate) {
							world.notify(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
						}
						blocksForUpdate.clear();
					}
					if (Bukkit.getWorld("arena") != null) {
						net.minecraft.server.v1_8_R3.World world = ((CraftWorld) Bukkit.getWorlds().get(1)).getHandle();
						for (Location location : blocksForUpdate) {
							world.notify(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
						}
						blocksForUpdate.clear();
					}
				}
			}
		}.runTaskTimer(HungerGames.getPlugin(HungerGames.class), 1, 1);
	}

	public List<Block> spawn(Location location, File file) {
		BufferedReader reader;
		ArrayList<Block> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}
				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");

				setBlockFast(location.getWorld(), location.getBlockX() + Integer.valueOf(coordinates[0]),
						location.getBlockY() + Integer.valueOf(coordinates[2]),
						location.getBlockZ() + Integer.valueOf(coordinates[1]), Integer.valueOf(blockData[0]),
						blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0);
				blocks.add(location.getWorld().getBlockAt(location.getBlockX() + Integer.valueOf(coordinates[0]),
						location.getBlockY() + Integer.valueOf(coordinates[2]),
						location.getBlockZ() + Integer.valueOf(coordinates[1])));
			}
			reader.close();
		} catch (Exception e) {
			getManager().getLogger().error(
					"Error to spawn the bo2file " + file.getName() + " in the location " + location.toString(), e);
		}
		return blocks;
	}

	public List<FutureBlock> load(Location location, File file) {
		BufferedReader reader;
		ArrayList<FutureBlock> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}
				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");
				blocks.add(new FutureBlock(
						location.clone().add(Integer.valueOf(coordinates[0]), Integer.valueOf(coordinates[2]),
								Integer.valueOf(coordinates[1])),
						Integer.valueOf(blockData[0]), blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0));
			}
			reader.close();
		} catch (Exception e) {
			getManager().getLogger().error("Error to load the bo2file " + file.getName() + " in the location " + location.toString(),
					e);
		}
		return blocks;
	}

	public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
		if (y >= 255 || y < 0) {
			return false;
		}
		asyncWorldEdit.setAsyncBlock(world, x,  y, z, blockId, data);
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean setBlockFast(Location location, Material material, byte data) {
		return setBlockFast(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
				material.getId(), data);
	}

	public class FutureBlock {
		private Location location;
		private int id;
		private byte data;

		public FutureBlock(Location location, int id, byte data) {
			this.location = location;
			this.id = id;
			this.data = data;
		}

		public byte getData() {
			return data;
		}

		public Location getLocation() {
			return location;
		}

		public int getId() {
			return id;
		}

		@SuppressWarnings("deprecation")
		public void place() {
			location.getBlock().setTypeIdAndData(id, data, true);
		}
	}

}
