package br.com.weavenmc.ypvp.tournament;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;

public class TournamentBattle {

	private UUID player1;
	private UUID player2;

	public TournamentBattle(Player player1, Player player2) {
		this.player1 = player1.getUniqueId();
		this.player2 = player2.getUniqueId();
		prepareToBattle(player1);
		prepareToBattle(player2);
	}
	
	public Player getBattlePlayer1() {
		return Bukkit.getPlayer(player1);
	}
	
	public Player getBattlePlayer2() {
		return Bukkit.getPlayer(player2);
	}
	
	public boolean isBattlePlayer(UUID uuid) {
		return player1 == uuid || player2 == uuid;
	}

	private void prepareToBattle(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		for (int i = 0; i < 36; i++) {
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
		ItemBuilder builder = new ItemBuilder().type(Material.DIAMOND_SWORD).name("§1§lTournament Battle")
				.enchantment(Enchantment.DAMAGE_ALL, 1).unbreakable();
		p.getInventory().setItem(0, builder.build());
		builder = new ItemBuilder().type(Material.IRON_HELMET);
		p.getInventory().setHelmet(builder.build());
		builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
		p.getInventory().setChestplate(builder.build());
		builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
		p.getInventory().setLeggings(builder.build());
		builder = new ItemBuilder().type(Material.IRON_BOOTS);
		p.getInventory().setBoots(builder.build());
		p.updateInventory();
		builder = null;
	}
}
