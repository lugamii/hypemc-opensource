package br.com.weavenmc.skywars.kit;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class KitAPI {

	@AllArgsConstructor
	@Getter
	public enum Kits {
		PADRAO("Padrão",
				new String[] { "§7Itens: ", "§f- Picareta de ferro", "§f- Machado de ferro", "§f- Pá de ferro" },
				Group.MEMBRO, Material.STONE_PICKAXE, 0),
		ARCHER("Archer", new String[] { "§7Itens:", "§f- Arco", "§f- Flecha §8x32" }, Group.VIP, Material.ARROW, 20000),
		ECOLOGISTA("Ecologista", new String[] { "§7Itens:", "§f- Tronco de carvalho §8x32", "§f- Machado de ferro" },
				Group.VIP, Material.IRON_AXE, 20000),
		ENGENHEIRO("Engenheiro",
				new String[] { "§7Itens:", "§f- Bloco de redstone §8x6", "§f- Pistão §8x3", "§f- Pistão grudento §8x3",
						"§f- TNT §8x4", "§f- Placa de pressão" },
				Group.PRO, Material.REDSTONE_BLOCK, 20000),
		ENCHANTER("Enchanter", new String[] { "§7Itens:", "§f- Mesa de Encatamento §8x1" }, Group.PRO,
				Material.ENCHANTMENT_TABLE, 20000),
		FIGHTER("Fighter",
				new String[] { "§7Armadura:", "§f- Capacete de ouro", "§f- Peitoral de ouro", "§f- Botas de ouro", " ",
						"§f- Capacete de ouro", "§7Itens:", "§f- Espada de madeira com afiação II" },
				Group.PRO, Material.IRON_SWORD, 40000),
		RUSH("Rush", new String[] { "§7Armadura:", "§f- Peitoral de ferro", " ", "§7Itens:", "§f- Terra §8x32" },
				Group.ULTRA, Material.STONE_SWORD, 50000),
		PALADINO("Paladino",
				new String[] { "§7Armadura:", "§f- Peitoral de diamante", " ", "§7Itens:", "§f- Espada de diamante" },
				Group.ULTRA, Material.DIAMOND_CHESTPLATE, 50000),
		GRANDPA("Grandpa", new String[] { "§7Itens:", "§f- Graveto com knockback II" }, Group.ULTRA, Material.STICK,
				60000),
		SCOUT("Scout", new String[] { "§7Itens:", "§f- Poção de velocidade §8x2" }, Group.BETA, Material.POTION, 40000),

		GOLDEN_BOY("Golden Boy", new String[] { "§7Itens:", "§f- Maçã dourada §8x4" }, Group.BETA,
				Material.GOLDEN_APPLE, 50000),

		VIKING("Viking", new String[] { "§7Itens:", "§f- Machado de Diamante com SHARPNESS 2", "§f - Poção de Força" },
				Group.BETA, Material.DIAMOND_AXE, 50000),

		FISHERMAN("Fisherman", new String[] { "§7Itens:", "§f- Vara de Pesca" }, Group.BETA, Material.FISHING_ROD,
				50000),

		MINER("Miner", new String[] { "§7Itens:", "§f- Picareta de Ferro", "§f- Pedra §8x64" }, Group.BETA,
				Material.IRON_PICKAXE, 50000),

		SNOWMAN("Snowman", new String[] { "§7Itens:", "§f- Bola de Neve §816x", "§f- Full Couro", "§f- Pá de Ferro" },
				Group.BETA, Material.SNOW_BALL, 50000),

		PYRO("Pyro", new String[] { "§7Itens:", "§f- Balde de Lava", "§f- Isqueiro", "§f- Poção contra fogo" },
				Group.BETA, Material.FLINT_AND_STEEL, 50000);

		String name;
		String[] description;
		Group group;
		Material display;
		int price;
	}

	private HashMap<UUID, Kits> kit = new HashMap<>();

	public void setKit(Player player, Kits kit) {
		this.kit.put(player.getUniqueId(), kit);
	}

	public String getKit(Player player) {
		return kit.get(player.getUniqueId()).getName();
	}

	public Kits checkKit(Player player) {
		return kit.get(player.getUniqueId());
	}

	public boolean isKit(Player player, Kits kit) {
		return checkKit(player) == kit;
	}

	public boolean hasPermission(Player player, Group group) {
		BukkitPlayer bPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
		return bPlayer.hasGroupPermission(group);
	}

	public void setItens(Player player) {
		if (isKit(player, Kits.PADRAO)) {
			player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
			player.getInventory().addItem(new ItemStack(Material.IRON_SPADE));
		} else if (isKit(player, Kits.ARCHER)) {
			player.getInventory().addItem(new ItemStack(Material.BOW));
			player.getInventory().addItem(new ItemStack(Material.ARROW, 32));
		} else if (isKit(player, Kits.ECOLOGISTA)) {
			player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
			player.getInventory().addItem(new ItemStack(Material.LOG, 32));
		} else if (isKit(player, Kits.ENGENHEIRO)) {
			player.getInventory().addItem(new ItemStack(Material.REDSTONE_BLOCK, 6));
			player.getInventory().addItem(new ItemStack(Material.PISTON_BASE, 3));
			player.getInventory().addItem(new ItemStack(Material.PISTON_STICKY_BASE, 3));
			player.getInventory().addItem(new ItemStack(Material.TNT, 4));
		} else if (isKit(player, Kits.ENCHANTER)) {
			player.getInventory().addItem(new ItemStack(Material.ANVIL));
			player.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 32));
			if (new Random().nextBoolean()) {
				player.getInventory().addItem(new ItemStack(Material.ENCHANTED_BOOK, 1, (short) 0));
			} else {
				player.getInventory().addItem(new ItemStack(Material.ENCHANTED_BOOK, 1, (short) 16));
			}
		} else if (isKit(player, Kits.GOLDEN_BOY)) {
			player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 4));
		} else if (isKit(player, Kits.RUSH)) {
			player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			player.getInventory().addItem(new ItemStack(Material.DIRT, 32));
		} else if (isKit(player, Kits.PALADINO)) {
			player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
		} else if (isKit(player, Kits.FIGHTER)) {
			player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
			player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
			player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
			player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
			player.getInventory().addItem(
					new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
		} else if (isKit(player, Kits.GRANDPA)) {
			player.getInventory()
					.addItem(new ItemBuilder().type(Material.STICK).enchantment(Enchantment.KNOCKBACK, 1).build());
		} else if (isKit(player, Kits.SCOUT)) {
			ItemStack item = new ItemStack(Material.POTION);
			item.setAmount(2);
			item.setDurability((short) 16418);

			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("Scout");

			item.setItemMeta(itemMeta);

			player.getInventory().addItem(item);
		} else if (isKit(player, Kits.VIKING)) {
			player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_AXE).name("Machado de Diamante")
					.enchantment(Enchantment.DAMAGE_ALL, 2).unbreakable().build());

			ItemStack item = new ItemStack(Material.POTION);
			item.setDurability((short) 8201);

			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName("Viking");

			item.setItemMeta(itemMeta);

			player.getInventory()
					.addItem(new ItemBuilder().type(Material.POTION).name("Viking").durability(8201).build());
			player.getInventory().addItem(item);
		} else if (isKit(player, Kits.FISHERMAN)) {
			player.getInventory().addItem(new ItemBuilder().type(Material.FISHING_ROD).name("Vara de pesca")
					.unbreakable().enchantment(Enchantment.KNOCKBACK, 1).build());

		} else if (isKit(player, Kits.MINER)) {
			player.getInventory().addItem(new ItemBuilder().type(Material.IRON_PICKAXE).name("Miner")
					.enchantment(Enchantment.DIG_SPEED, 5).unbreakable().build());
			player.getInventory().addItem(new ItemBuilder().type(Material.STONE).name("Miner").amount(64).build());
		} else if (isKit(player, Kits.SNOWMAN)) {
			player.getInventory()
					.addItem(new ItemBuilder().type(Material.SNOW_BALL).name("Snowman").amount(64).build());
			player.getInventory().addItem(new ItemBuilder().type(Material.IRON_SPADE).name("Snowman")
					.enchantment(Enchantment.DIG_SPEED, 1).unbreakable().build());
			player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		} else if (isKit(player, Kits.PYRO)) {
			player.getInventory().addItem(new ItemBuilder().type(Material.FLINT_AND_STEEL).name("Pyro").build());
			player.getInventory().addItem(new ItemBuilder().type(Material.LAVA_BUCKET).name("Pyro").build());
			player.getInventory()
					.addItem(new ItemBuilder().type(Material.POTION).durability(16451).name("Pyro").build());
		}

		player.sendMessage("§eVocê recebeu os itens do kit §c" + kit.get(player.getUniqueId()).getName() + "§e.");
		player.updateInventory();
	}

}
