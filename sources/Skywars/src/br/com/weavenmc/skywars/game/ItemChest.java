package br.com.weavenmc.skywars.game;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemChest {
	
	public enum Items {
		CHESTPLATE,
		HELMET,
		BOOTS,
		LEGGINGS,
		SWORD,
		BLOCKS,
		FOOD,
		POTION,
		OTHER
	}
	
	public enum Type {
		DIAMOND,
		CHAINMAIL,
		IRON,
		LEATHER,
		GOLD,
		STONE,
		WOODEN,
		COBBLESTONE,
		GOLDEN_APPLE,
		ENDER_PEARL,
		EGG,
		SNOW_BALL,
		ARROW,
		BOW,
		COOKED_BEEF
	}
	
	public enum Potions {
		SPEED, POISON, REGENERATION, FIRE_RESISTANCE, NIGHT_VISION,
		STRENGTH, INVISIBILITY, WEAKNESS, SLOWNESS;
	}
	
	public ItemChest() {
	}
	
	public ItemStack getPotion(Potions potion) {
		int r = new Random().nextInt(2);
		ItemStack stack = new ItemStack(Material.AIR);
		if (potion.equals(Potions.SPEED)) {
			stack = new ItemStack(Material.POTION, 1, (short)16418);
		}
		if (potion.equals(Potions.WEAKNESS)) {
			stack = new ItemStack(Material.POTION, 1, (short)16456);
		}
		if (potion.equals(Potions.SLOWNESS)) {
			stack = new ItemStack(Material.POTION, 1, (short)16394);
		}
		if (potion.equals(Potions.INVISIBILITY)) {
			switch (r) {
			case 2:
				stack = new ItemStack(Material.POTION, 1, (short)8206);
				break;
			case 1:
				stack = new ItemStack(Material.POTION, 1, (short)8270);
				break;
			default:
				stack = new ItemStack(Material.POTION, 1, (short)8206);
				break;
			}
		}
		if (potion.equals(Potions.POISON)) {
			stack = new ItemStack(Material.POTION, 1, (short)16418);
		}
		if (potion.equals(Potions.REGENERATION)) {
			switch (r) {
			case 2:
				stack = new ItemStack(Material.POTION, 1, (short)8193);
				break;
			case 1:
				stack = new ItemStack(Material.POTION, 1, (short)8228);
				break;
			default:
				stack = new ItemStack(Material.POTION, 1, (short)8196);
				break;
			}
		}
		if (potion.equals(Potions.FIRE_RESISTANCE)) {
			switch (r) {
			case 2:
				stack = new ItemStack(Material.POTION, 1, (short)16419);
				break;
			case 1:
				stack = new ItemStack(Material.POTION, 1, (short)16451);
				break;
			default:
				stack = new ItemStack(Material.POTION, 1, (short)16419);
				break;
			}
		}
		if (potion.equals(Potions.NIGHT_VISION)) {
			switch (r) {
			case 2:
				stack = new ItemStack(Material.POTION, 1, (short)8198);
				break;
			case 1:
				stack = new ItemStack(Material.POTION, 1, (short)8262);
				break;
			default:
				stack = new ItemStack(Material.POTION, 1, (short)8198);
				break;
			}
		}
		if (potion.equals(Potions.STRENGTH)) {
			switch (r) {
			case 2:
				stack = new ItemStack(Material.POTION, 1, (short)8201);
				break;
			case 1:
				stack = new ItemStack(Material.POTION, 1, (short)8297);
				break;
			default:
				stack = new ItemStack(Material.POTION, 1, (short)8201);
				break;
			}
		}
		return stack;
	}
	
	public ItemStack getItem(Items itens, Type type, int i) {
		ItemStack stack = new ItemStack(Material.AIR);
		if (itens.equals(Items.CHESTPLATE)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_CHESTPLATE, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.CHAINMAIL_CHESTPLATE, i);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_CHESTPLATE, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_CHESTPLATE, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.LEATHER_CHESTPLATE, i);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.BOOTS)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_BOOTS, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.CHAINMAIL_BOOTS, i);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_BOOTS, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_BOOTS, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.LEATHER_BOOTS, i);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.HELMET)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_HELMET, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.CHAINMAIL_HELMET, i);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_HELMET, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_HELMET, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.LEATHER_HELMET, i);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.LEGGINGS)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_LEGGINGS, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.CHAINMAIL_LEGGINGS, i);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_LEGGINGS, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_LEGGINGS, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.LEATHER_LEGGINGS, i);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.SWORD)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_SWORD, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_SWORD, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_SWORD, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.AIR);
			if (type == Type.STONE)
				stack = new ItemStack(Material.STONE_SWORD, i);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.WOOD_SWORD, i);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.BLOCKS)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.DIAMOND_BLOCK, i);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.IRON)
				stack = new ItemStack(Material.IRON_BLOCK, i);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.GOLD_BLOCK, i);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.AIR);
			if (type == Type.STONE)
				stack = new ItemStack(Material.STONE, i);
			if (type == Type.COBBLESTONE)
				stack = new ItemStack(Material.COBBLESTONE, i);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.WOOD, i);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.FOOD)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.AIR);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.IRON)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.AIR);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.AIR);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.GOLDEN_APPLE, i);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.COOKED_BEEF, i);
			if (type == Type.EGG)
				stack = new ItemStack(Material.AIR);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.BOW)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.AIR);
		}
		if (itens.equals(Items.OTHER)) {
			if (type == Type.DIAMOND)
				stack = new ItemStack(Material.AIR);
			if (type == Type.CHAINMAIL)
				stack = new ItemStack(Material.AIR);
			if (type == Type.IRON)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLD)
				stack = new ItemStack(Material.AIR);
			if (type == Type.LEATHER)
				stack = new ItemStack(Material.AIR);
			if (type == Type.STONE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.WOODEN)
				stack = new ItemStack(Material.AIR);
			if (type == Type.GOLDEN_APPLE)
				stack = new ItemStack(Material.AIR);
			if (type == Type.COOKED_BEEF)
				stack = new ItemStack(Material.AIR);
			if (type == Type.ENDER_PEARL)
				stack = new ItemStack(Material.ENDER_PEARL, i);
			if (type == Type.EGG)
				stack = new ItemStack(Material.EGG, i);
			if (type == Type.SNOW_BALL)
				stack = new ItemStack(Material.SNOW_BALL, i);
			if (type == Type.ARROW)
				stack = new ItemStack(Material.ARROW, i);
			if (type == Type.BOW)
				stack = new ItemStack(Material.BOW, i);
		}
		if (itens.equals(Items.POTION)) {
			int r = new Random().nextInt(2);
			switch (r) {
			case 2:
				stack = getPotion(Potions.SPEED);
				break;
			case 1:
				stack = getPotion(Potions.WEAKNESS);
				break;
			default:
				stack = getPotion(Potions.SLOWNESS);
				break;
			}
		}
		return stack;
	}
	
	public ItemStack getItem(Items itens) {
		ItemStack stack = new ItemStack(Material.AIR);
		if (itens.equals(Items.CHESTPLATE)) {
			int r = new Random().nextInt(100);
			if (r <= 15) {
				stack = getItem(itens, Type.DIAMOND, 1);
			} else if (r > 15 && r <= 70) {
				stack = getItem(itens, Type.IRON, 1);
			} else if (r > 70) {
				stack = getItem(itens, Type.ARROW, 1);
			}
		}
		if (itens.equals(Items.BOOTS)) {
			int r = new Random().nextInt(100);
			if (r <= 15) {
				stack = getItem(itens, Type.DIAMOND, 1);
			} else if (r > 15 && r <= 70) {
				stack = getItem(itens, Type.IRON, 1);
			} else if (r > 70) {
				stack = getItem(itens, Type.ARROW, 1);
			}
		}
		if (itens.equals(Items.HELMET)) {
			int r = new Random().nextInt(100);
			if (r <= 15) {
				stack = getItem(itens, Type.DIAMOND, 1);
			} else if (r > 15 && r <= 70) {
				stack = getItem(itens, Type.IRON, 1);
			} else if (r > 70) {
				stack = getItem(itens, Type.ARROW, 1);
			}
		}
		if (itens.equals(Items.LEGGINGS)) {
			int r = new Random().nextInt(100);
			if (r <= 15) {
				stack = getItem(itens, Type.DIAMOND, 1);
			} else if (r > 15 && r <= 70) {
				stack = getItem(itens, Type.IRON, 1);
			} else if (r > 70) {
				stack = getItem(itens, Type.ARROW, 1);
			}
		}
		if (itens.equals(Items.SWORD)) {
			int r = new Random().nextInt(3);
			switch (r) {
			case 3:
				stack = getItem(itens, Type.DIAMOND, 1);
				break;
			case 2:
				stack = getItem(itens, Type.DIAMOND, 1);
				break;
			case 1:
				stack = getItem(itens, Type.DIAMOND, 1);
				break;
			default:
				stack = getItem(itens, Type.IRON, 1);
				break;
			}
		}
		if (itens.equals(Items.BLOCKS)) {
			int r = new Random().nextInt(2);
			switch (r) {
			case 2:
				stack = getItem(itens, Type.STONE, 32);
				break;
			case 1:
				stack = getItem(itens, Type.COBBLESTONE, 32);
				break;
			default:
				stack = new ItemStack(Material.WOOD, 32);
				break;
			}
		}
		if (itens.equals(Items.FOOD)) {
			stack = getItem(itens, Type.COOKED_BEEF, maxAndMin(20, 12));
		}
		if (itens.equals(Items.OTHER)) {
			int r = new Random().nextInt(4);
			switch (r) {
			case 4:
				stack = new ItemStack(Material.EGG, maxAndMin(16, 12));
				break;
			case 3:
				stack = new ItemStack(Material.SNOW_BALL, maxAndMin(16, 12));
				break;
			default:
				stack = new ItemStack(Material.EGG, maxAndMin(16, 12));
				break;
			}
		}
		if (itens.equals(Items.POTION)) {
			int r = new Random().nextInt(3);
			switch (r) {
			case 1:
				stack = getPotion(Potions.SPEED);
				break;
			case 2:
				stack = getPotion(Potions.FIRE_RESISTANCE);
				break;
			case 3:
				stack = getPotion(Potions.REGENERATION);
				break;
			default:
				stack = getPotion(Potions.SLOWNESS);
				break;
			}
		}
		return stack;
	}
	
	public int maxAndMin(int max, int min) {
		int r = new Random().nextInt(max);
		if (r < min) { // Min = 2 ; r = 1 | 1 < 2 | r = 2;
			r = min;
		}
		return r;
	}

}
