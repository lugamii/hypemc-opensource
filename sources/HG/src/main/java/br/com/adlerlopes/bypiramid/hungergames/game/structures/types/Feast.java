package br.com.adlerlopes.bypiramid.hungergames.game.structures.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.game.structures.Structure;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Feast extends Structure {

	private int feastTime;
	private boolean isSpawned;
	private ArrayList<Block> chestsData;
	private ArrayList<Location> blockData;

	private ItemStack[] feastStacks = { new ItemStack(Material.DIAMOND_HELMET),
			new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS),
			new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_SWORD),
			new ItemStack(Material.COOKED_BEEF, getRandom(37)), new ItemStack(Material.FLINT_AND_STEEL),
			new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET),
			new ItemStack(Material.ENDER_PEARL, getRandom(12)), new ItemStack(Material.GOLDEN_APPLE, getRandom(12)),
			new ItemStack(Material.EXP_BOTTLE, getRandom(24)), new ItemStack(Material.WEB, getRandom(9)),
			new ItemStack(Material.TNT, getRandom(32)), new ItemStack(Material.POTION, 1, (short) 16418),
			new ItemStack(Material.POTION, 1, (short) 16424), new ItemStack(Material.POTION, 1, (short) 16420),
			new ItemStack(Material.POTION, 1, (short) 16428), new ItemStack(Material.POTION, 1, (short) 16426),
			new ItemStack(Material.POTION, 1, (short) 16417), new ItemStack(Material.POTION, 1, (short) 16419),
			new ItemStack(Material.POTION, 1, (short) 16421), new ItemStack(Material.BOW),
			new ItemStack(Material.ARROW, getRandom(64)), new ItemStack(Material.DIAMOND_AXE),
			new ItemStack(Material.DIAMOND_PICKAXE) };

	public Feast(Manager manager, int size) {
		super(manager);

		this.feastTime = 300;
		this.chestsData = new ArrayList<>();
		this.blockData = new ArrayList<>();

		location = Bukkit.getWorlds().get(0).getHighestBlockAt(getCoord(150), getCoord(150)).getLocation();
		if (location.getY() >= 90) {
			location.setY(72);
		}
	}

	public Location getLocation() {
		return location;
	}

	public ArrayList<Location> getBlockData() {
		return blockData;
	}

	public void spawnFeast() {
		createArea();
		createStructure();

		new BukkitRunnable() {
			public void run() {
				if (!isSpawned) {
					isSpawned = true;
					Bukkit.broadcastMessage("§4§lFEAST §7Irá spawnar em §c(" + location.getX() + ", " + location.getY()
							+ ", " + location.getZ() + ") §eem " + getManager().getUtils().formatTime(feastTime)
							+ "! Use /feast para apontar sua bússola para o feast!");

				} else if (feastTime % 30 == 0 && feastTime != 0) {
					Bukkit.broadcastMessage("§4§lFEAST §7Irá spawnar em §c(" + location.getX() + ", " + location.getY()
							+ ", " + location.getZ() + ") §eem " + getManager().getUtils().formatTime(feastTime)
							+ "! Use /feast para apontar sua bússola para o feast!");
				} else if (feastTime == 0) {

					Bukkit.broadcastMessage("§4§lFEAST §7Spawnou em §c(" + location.getX() + ", " + location.getY()
							+ ", " + location.getZ() + ")!§e Use /feast para apontar sua bússola para o feast!");
					spawnChests();
					cancel();
				}

				if (feastTime > 0)
					feastTime--;
				else
					cancel();
			}
		}.runTaskTimer(getManager().getPlugin(), 0L, 20L);
	}

	public void forceFeast() {
		createArea();
		createStructure();
		spawnChests();
		Bukkit.broadcastMessage("§4§lFEAST §7Um feast adicional nasceu! Em §c(" + location.getX() + ", " + location.getY()
				+ ", " + location.getZ() + ")! §eDica: Seja rápido! Colete o máximo de recursos e ganhe o jogo!");
	}

	@SuppressWarnings("unused")
	public void createStructure() {
		for (int x = -20; x <= 20; x++) {
			for (int z = -20; z <= 20; z++) {
				Location feast = new Location(location.getWorld(), location.getX() + x, location.getY(),
						location.getZ() + z);
				if (feast.distance(location) <= 20) {

					Location loc = feast.getBlock().getLocation().add(0, 1, 0);

					Block remover = loc.getBlock();
					do {
						getManager().getBO2().setBlockFast(loc, Material.AIR, (byte) 0);
						loc.setY(loc.getY() + 1);
						remover = loc.getBlock();
					} while (loc.getY() < loc.getWorld().getMaxHeight());

					getManager().getBO2().setBlockFast(feast, Material.GRASS, (byte) 0);

					blockData.add(feast);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void spawnChests() {

		((World) Bukkit.getServer().getWorlds().get(0)).strikeLightningEffect(location.clone().add(0, 1, 0));

		for (Block chests : chestsData) {

			location.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
			chests.setType(Material.CHEST);
			Chest chestData = (Chest) chests.getLocation().getBlock().getState();

			int[] itens = { 310, 311, 312, 313, 276, 261, 262, 278, 279, 322, 282, 326, 327, 384, 264, 265, 364, 30, 46,
					259, };

			addChestItems((Chest) chests.getLocation().getBlock().getState(), feastStacks);
			
			for (int item : itens) {
				if (new Random().nextInt(200) < 9) {
					chestData.getInventory().setItem(new Random().nextInt(27), new ItemStack(item));
				}
			}
		}

	}

	private void createArea() {

		for (int x = -20; x <= 20; x++) {
			for (int z = -20; z <= 20; z++) {
				Location feast = new Location(location.getWorld(), location.getX() + x, location.getY(),
						location.getZ() + z);
				if (feast.distance(location) <= 20) {

					Location loc = feast.getBlock().getLocation().add(0, 1, 0);
					Block remover = loc.getBlock();
					do {
						remover.setType(Material.AIR);
						loc.setY(loc.getY() + 1);
						remover = loc.getBlock();
					} while (loc.getY() < loc.getWorld().getMaxHeight());

					feast.getBlock().setType(Material.GRASS);
				}
			}
		}

		Location[] baus = { location.clone().add(1, 1, 1), location.clone().add(-1, 1, -1),
				location.clone().add(1, 1, -1), location.clone().add(-1, 1, 1), location.clone().add(-2, 1, 2),
				location.clone().add(-2, 1, -2), location.clone().add(-2, 1, 0), location.clone().add(2, 1, 0),
				location.clone().add(0, 1, -2), location.clone().add(0, 1, 2), location.clone().add(+2, 1, -2),
				location.clone().add(2, 1, 2) };
		for (Location blocos : baus) {
			chestsData.add(blocos.getBlock());
		}
	}

	public void addChestItems(Chest chest, ItemStack[] stacks) {
		for (int i = 0; i < stacks.length; i++)
			if (new Random().nextInt(100) < 9)
				chest.getInventory().setItem(new Random().nextInt(27), stacks[i]);
		chest.update();
	}

	public boolean isSpawned() {
		return isSpawned;
	}

	private int getRandom(int i) {
		return Math.max(1, getManager().getRandom().nextInt(i + 1));
	}

	private int getCoord(int range) {
		return getManager().getRandom().nextBoolean() ? -getManager().getRandom().nextInt(range)
				: getManager().getRandom().nextInt(range + 1);
	}

}
