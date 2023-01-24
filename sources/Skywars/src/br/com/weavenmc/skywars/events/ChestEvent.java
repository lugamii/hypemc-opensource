package br.com.weavenmc.skywars.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager;
import br.com.weavenmc.skywars.game.GameManager.EventsState;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.game.ItemChest;
import br.com.weavenmc.skywars.game.ItemChest.Items;
import br.com.weavenmc.skywars.game.ItemChest.Type;
import br.com.weavenmc.skywars.utils.ChestManager.typeChest;

public class ChestEvent implements Listener {

	public HashMap<Chest, typeChest> Types = new HashMap<>();
	public HashMap<Player, Integer> openedChests = new HashMap<>();

	public ArrayList<Chest> armorNormal = new ArrayList<>();
	public static ArrayList<Block> placedByPlayer = new ArrayList<>();

	List<ItemStack> forcenormalItens = new ArrayList<>();
	List<ItemStack> normalItens = new ArrayList<>();
	List<ItemStack> forceMinifeastItens = new ArrayList<>();
	List<ItemStack> minifeastItens = new ArrayList<>();
	List<ItemStack> feastItens = new ArrayList<>();

	public ChestEvent() {

		forcenormalItens.add(new ItemStack(Material.STONE, 64));
		forcenormalItens.add(new ItemStack(Material.WOOD, 32));
		forcenormalItens.add(new ItemStack(Material.STONE, 64));
		forcenormalItens.add(new ItemStack(Material.EGG, 16));
		forcenormalItens.add(new ItemStack(Material.SNOW_BALL, 8));
		forcenormalItens.add(new ItemStack(Material.COOKED_BEEF, 8));

		normalItens.add(new ItemStack(Material.IRON_PICKAXE));
		normalItens.add(new ItemStack(Material.IRON_AXE));
		normalItens.add(new ItemStack(Material.IRON_AXE));
		normalItens.add(new ItemStack(Material.POTION, 1, (short) 16424));
		normalItens.add(new ItemStack(Material.POTION, 1, (short) 16421));
		normalItens.add(new ItemStack(Material.POTION, 1, (short) 8225));
		normalItens.add(new ItemStack(Material.LAVA_BUCKET));
		normalItens.add(new ItemStack(Material.WATER_BUCKET));

		forceMinifeastItens.add(new ItemStack(Material.ENDER_PEARL, new Random().nextInt(2) + 1));
		forceMinifeastItens.add(new ItemStack(Material.WOOD, 16 + new Random().nextInt(16)));
		minifeastItens.add(new ItemStack(Material.GOLDEN_APPLE));
		forceMinifeastItens.add(new ItemStack(Material.ARROW, 8 + new Random().nextInt(16)));

		feastItens.add(new ItemStack(Material.ENDER_PEARL, new Random().nextInt(2) + 1));
		feastItens.add(new ItemStack(Material.GOLDEN_APPLE, new Random().nextInt(2) + 1));
		feastItens.add(new ItemStack(Material.POTION, 1, (short) 8225));
		feastItens.add(new ItemStack(Material.POTION, 1, (short) 16425));
		ItemStack itemStack = new ItemStack(Material.BOW);
		itemStack.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		minifeastItens.add(itemStack);
		itemStack = new ItemStack(Material.DIAMOND_SWORD);
		itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		minifeastItens.add(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_SWORD);
		itemStack.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		feastItens.add(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
		itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		feastItens.add(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_LEGGINGS);
		itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		feastItens.add(itemStack);

		itemStack = new ItemStack(Material.DIAMOND_HELMET);
		itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		feastItens.add(itemStack);
		itemStack = new ItemStack(Material.DIAMOND_BOOTS);
		itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		feastItens.add(itemStack);

		itemStack = new ItemStack(Material.BOW);
		itemStack.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		itemStack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		feastItens.add(itemStack);

	}

	public void putArmor(Inventory inventory, int a) {

		if (a % 2 == 0) {
			if (new Random().nextBoolean()) {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_CHESTPLATE));
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_LEGGINGS));
			} else {
				if (new Random().nextBoolean()) {
					inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_LEGGINGS));
				} else {
					inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_LEGGINGS));
				}
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_CHESTPLATE));
			}
		} else {
			inventory.setItem(new Random().nextInt(27), forcenormalItens.get(3 + new Random().nextInt(1)));
			inventory.setItem(new Random().nextInt(27), forcenormalItens.get(4));
			inventory.setItem(new Random().nextInt(27), forcenormalItens.get(5));
			if (new Random().nextBoolean()) {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_BOOTS));
			} else {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_BOOTS));
			}
			if (new Random().nextBoolean()) {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_HELMET));
			} else {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_HELMET));
			}
		}
		if (a == 1) {

			if (new Random().nextBoolean()) {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_SWORD));
			} else {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_SWORD));
			}
			inventory.setItem(new Random().nextInt(27), forcenormalItens.get(new Random().nextInt(2)));
		}
		if (a == 1 || (a > 3 && a % 2 == 0)) {

			if (new Random().nextBoolean()) {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.DIAMOND_SWORD));
			} else {
				inventory.setItem(new Random().nextInt(27), new ItemStack(Material.IRON_SWORD));
			}
			inventory.setItem(new Random().nextInt(27), forcenormalItens.get(new Random().nextInt(2)));

		}
	}

	@EventHandler
	public void onChestOpen(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY) {
			e.setCancelled(true);
			return;
		}
		if (!GameController.player.contains(player.getUniqueId()))
			e.setCancelled(true);
		handle(e, WeavenSkywars.getGameManager());
	}

	private void handle(PlayerInteractEvent event, GameManager game) {
		if (event.hasBlock() && event.getClickedBlock() != null
				&& event.getClickedBlock().getState() instanceof Chest) {
			Chest chest = (Chest) event.getClickedBlock().getState();

			if (game.getOpened().contains(chest)) {
				return;
			}
			if (openedChests.containsKey(event.getPlayer())) {

				openedChests.put(event.getPlayer(), openedChests.get(event.getPlayer()) + 1);
			} else {
				openedChests.put(event.getPlayer(), 1);
			}
			if (placedByPlayer.contains(event.getClickedBlock()))
				return;

			if (WeavenSkywars.getGameManager().getMiniFeastChest().contains(chest)) {
				Types.put(chest, typeChest.MINIFEAST);
			} else if (WeavenSkywars.getGameManager().getFeastChest().contains(chest)) {
				Types.put(chest, typeChest.FEAST);
			} else {
				Types.put(chest, typeChest.NORMAL);
			}
			encherBau(chest, Types.get(chest), event.getPlayer());
			game.getOpened().add(chest);
		}
	}

	private void encherBau(Chest chest, typeChest type, Player player) {
		if (type == typeChest.NORMAL) {
			setNormal(chest, player);
			System.out.println("Normal itens!");
		}
		if (type == typeChest.MINIFEAST) {
			System.out.println("Mini feast itens!");
			setMinifeast(chest);
		}
		if (type == typeChest.FEAST) {
			System.out.println("Feast itens!");
			setFeast(chest);
		}
		chest.update();
	}

	private void setFeast(Chest chest) {
		for (ItemStack itemStack : feastItens) {

			if (new Random().nextInt(100) > 40) {
				chest.getInventory().setItem(new Random().nextInt(27), itemStack);
			}
		}
	}

	private void setMinifeast(Chest chest) {
		for (ItemStack itemStack : forceMinifeastItens) {

			if (new Random().nextInt(100) > 20) {
				chest.getInventory().setItem(new Random().nextInt(27), itemStack);
			}
		}
		for (ItemStack itemStack : minifeastItens) {
			if (new Random().nextInt(100) > 30) {
				chest.getInventory().setItem(new Random().nextInt(27), itemStack);
			}
		}
	}

	private void setNormal(Chest chest, Player player) {

		putArmor(chest.getInventory(), openedChests.get(player));

		for (ItemStack itemStack : normalItens) {
			if (new Random().nextInt(100) > 80) {
				chest.getInventory().setItem(new Random().nextInt(27), itemStack);
			}
		}
	}

}
