package br.com.mcweaven.gladiator;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GladiatorManager {

	public void giveJoinItens(Player player) {

		player.getInventory().setItem(Itens.JOIN.getSlots()[0], Itens.JOIN.getStacks()[0]);
//		player.getInventory().setItem(Itens.JOIN.getSlots()[1], Itens.JOIN.getStacks()[1]);
		player.getInventory().setItem(Itens.JOIN.getSlots()[2], Itens.JOIN.getStacks()[2]);

	}
	
	public void giveSearch1v1Item(Player player) {

		player.getInventory().setItem(Itens.JOIN.getSlots()[2], Itens.JOIN.getStacks()[2]);
		
	}

	@SuppressWarnings("deprecation")
	public void registerRecipes() {
		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);

		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
		
	}
	
	public void newShapelessRecipe(ItemStack result, List<MaterialData> materials) {
		ShapelessRecipe recipe = new ShapelessRecipe(result);
		for (MaterialData mat : materials) {
			recipe.addIngredient(mat);
		}
		Bukkit.addRecipe(recipe);
	}
	
	public void giveBattleItens(Player player) {

		/*
		 * Soups
		 */

		for (int i = 0, length = player.getInventory().getSize(); i < length; i++) 
			player.getInventory().addItem(Itens.SOUP.getStacks()[0]);
		
			
		/*
		 * Full Iron
		 */

		for (int i = 0, length = Itens.FULL_IRON.getSlots().length; i < length; i++)
			player.getInventory().setItem(Itens.FULL_IRON.getSlots()[i], Itens.FULL_IRON.getStacks()[i]);

		/*
		 * Trap Itens
		 */

		for (int i = 0, length = Itens.TRAP_ITENS.getSlots().length; i < length; i++)
			player.getInventory().setItem(Itens.TRAP_ITENS.getSlots()[i], Itens.TRAP_ITENS.getStacks()[i]);

		/*
		 * Duel Itens
		 */

		for (int i = 0, length = Itens.DIAMOND_SWORD.getSlots().length; i < length; i++)
			player.getInventory().setItem(Itens.DIAMOND_SWORD.getSlots()[i], Itens.DIAMOND_SWORD.getStacks()[i]);

		
		/*
		 * Recraft itens
		 */
		
		for (int i = 0, lenght = Itens.RECRAFT.getSlots().length; i < lenght; i++)
			player.getInventory().setItem(Itens.RECRAFT.getSlots()[i], Itens.RECRAFT.getStacks()[i]);

		
		/*
		 * Substitute Armor
		 */
		
		for (int i = 0, lenght = Itens.SUB_FULL_IRON.getSlots().length; i < lenght; i++)
			player.getInventory().setItem(Itens.SUB_FULL_IRON.getSlots()[i], Itens.SUB_FULL_IRON.getStacks()[i]);
			
		
		/*
		 * Update player inventory
		 */
		
		player.updateInventory();

		
	}

	public void respawnPlayer(Player player) {

		player.getInventory().clear();
		player.setHealth(20);
		player.getInventory().setArmorContents(null);

		giveJoinItens(player);

		player.teleport(player.getWorld().getSpawnLocation());
		
	}
	

	public static void debug(String msg) {
		System.out.println("[DEBUG] " + msg);
	}
	
	@Getter
	@AllArgsConstructor
	public enum Itens {

		JOIN(new ItemStack[] {
				new ItemBuilder().type(Material.IRON_FENCE).name("§fDesafio §7(Interaja para desafiar)").build(),
				new ItemBuilder().type(Material.IRON_FENCE).name("§fDesafio Customizado §7(Interaja para desafiar)")
						.build(),
				new ItemBuilder().type(Material.INK_SACK).durability(8).name("§fDesafio Rápido §7(§cProcurando§7)")
						.build() },
				new Integer[] { 3, 4, 5 }),

		SOUP(new ItemStack[] { new ItemStack(Material.MUSHROOM_SOUP) }, new Integer[] { 0 }),
		
		RECRAFT(new ItemStack[] { new ItemStack(Material.BOWL, 64), 
				new ItemStack(Material.INK_SACK, 64, (short) 3), new ItemStack(Material.INK_SACK, 64, (short) 3),
				new ItemStack(Material.BOWL, 64), 
				new ItemStack(Material.INK_SACK, 64, (short) 3), new ItemStack(Material.INK_SACK,  64, (short) 3), new ItemStack(Material.INK_SACK,  64, (short) 3), new ItemStack(Material.INK_SACK,  64, (short) 3)}, new Integer[] { 13, 14, 15, 22, 23, 24, 16, 25}),

		FULL_IRON(
				new ItemStack[] { new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE),
						new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS) },
				new Integer[] { 39, 38, 37, 36 }),

		SUB_FULL_IRON(
				new ItemStack[] { new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE),
						new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS),
						new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE),
						new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS) },
				new Integer[] { 9, 10, 11, 12, 18, 19, 20, 21 }),

		DIAMOND_SWORD(
				new ItemStack[] {
						new ItemBuilder().type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build() },
				new Integer[] { 0 }),

		TRAP_ITENS(new ItemStack[] { 
				new ItemBuilder().type(Material.STONE_PICKAXE).build(),
				new ItemBuilder().type(Material.STONE_AXE).build(),
				new ItemBuilder().type(Material.LAVA_BUCKET).build(),
				new ItemBuilder().type(Material.LAVA_BUCKET).build(),
				new ItemBuilder().type(Material.WATER_BUCKET).build(),
				new ItemBuilder().type(Material.LAVA_BUCKET).build(), 
				new ItemBuilder().type(Material.WOOD).amount(64).build(),
				new ItemBuilder().type(Material.COBBLE_WALL).amount(64).build(), }, 
				new Integer[] { 17, 26, 8, 35, 07, 34, 2, 1 });

		ItemStack[] stacks;
		Integer[] slots;

	}

}
