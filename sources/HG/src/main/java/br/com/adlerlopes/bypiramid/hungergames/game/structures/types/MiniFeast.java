package br.com.adlerlopes.bypiramid.hungergames.game.structures.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.Structure;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.schematic.DataException;
import br.com.adlerlopes.bypiramid.hungergames.schematic.Schematic;
import br.com.weavenmc.commons.WeavenMC;

/**
 * Copyright (C) LittleMC, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class MiniFeast extends Structure {

	@SuppressWarnings("unused")
	private int x, z, y, size;
	private ArrayList<Block> chestsData;
	private ItemStack[] feastStacks = { new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.FLINT_AND_STEEL),
			new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET),
			new ItemStack(Material.ENDER_PEARL, getRandom(12)), new ItemStack(Material.EXP_BOTTLE, getRandom(24)),
			new ItemStack(Material.DIAMOND, getRandom(3)), new ItemStack(Material.POTION, 1, (short) 16421),
			new ItemStack(Material.BOW), new ItemStack(Material.ARROW, getRandom(64)),
			new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.DIAMOND_PICKAXE) };
	Schematic s = HungerGames.mini_feast;
	private ArrayList<Block> minifeastBlocks = new ArrayList<Block>();

	public MiniFeast(Manager manager, int size) {
		super(manager);

		this.size = size;
		this.chestsData = new ArrayList<>();

		x = getCoord(150);
		z = getCoord(150);

		this.location = Bukkit.getWorlds().get(0).getHighestBlockAt(x, z).getLocation();

		y = Bukkit.getWorlds().get(0).getHighestBlockAt(x, z).getLocation().getBlockY();

		createStructure();
		
		try {
			s = Schematic.getInstance().loadSchematic(
					new File(HungerGames.getManager().getPlugin().getDataFolder(), "minifeast.schematic"));
		} catch (IOException | DataException e) {
			e.printStackTrace();
		}
		
		Schematic.getInstance().newSchematic(HungerGames.getManager().getPlugin().getServer().getWorlds().get(0),
				location, s, minifeastBlocks);
		
		for (Block block : minifeastBlocks) 
			if (block.getType() == Material.CHEST) {
				chestsData.add(block);		
				WeavenMC.debug("novo bau");
			}
		

		for (Block chests : chestsData) {
			if (chests.getType() == Material.CHEST) {
				chests.setType(Material.CHEST);

				Chest chest = (Chest) chests.getLocation().getBlock().getState();

				addChestItems(chest, feastStacks);
				chest.update();
			}
		}

		Bukkit.broadcastMessage(
				"§4§lMINIFEAST §7Spawnou aproximadamente em §c(x: " + getInteger(x) + " e z: " + getInteger(z) + ")");

	}

	public Location getLocation() {
		return location;
	}

//	public void spawnMiniFeast() {
//		forceMiniFeast();
//	}
//
//	public void forceMiniFeast() {
////		createArea();
//		Bukkit.broadcastMessage(
//				"§4§lMINIFEAST §7Spawnou aproximadamente em §c(x: " + getInteger(x) + " e z: " + getInteger(z) + ")");

//	}

	public int getInteger(int i) {
		int ad = new Random().nextBoolean() ? -50 : 50;
		return ad + i;
	}

	public void createStructure() {
		createArea();
	}

	private void createArea() {
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				if (location.clone().add(x, 0, z).distance(location) > size + 10.D)
					continue;
				for (int y = 0; y <= 20; y++) {
					getManager().getBO2().setBlockFast(location.clone().add(x, y, z).getBlock().getLocation(),
							Material.AIR, (byte) 0);
				}
			}
		}
	}

	private int getRandom(int i) {
		return Math.max(1, new Random().nextInt(i + 1));
	}

	private int getCoord(int range) {
		int cord = new Random().nextInt(range) + 200;
		cord = (new Random().nextBoolean() ? -cord : cord);

		return cord;
	}

	public void addChestItems(Chest chest, ItemStack[] stacks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (int i = 0; i < stacks.length; i++)
					if (new Random().nextInt(100) < 30)
						chest.getInventory().setItem(new Random().nextInt(27), stacks[i]);
				chest.update();
			}
		}.runTaskLater(getManager().getPlugin(), 30L);
	}

}
