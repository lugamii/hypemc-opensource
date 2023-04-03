package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Scout extends Kit {

	public Scout(Manager manager) {
		super(manager);
		
		setPrice(43000);
		setCooldownTime(0D);
		setIcon(new ItemStack(new ItemBuilder(Material.POTION).setDurability(16418).getStack()));
		setFree(false);
		setDescription("Se transforme em um maratonista e tome suas poções para ganhar velocidade .");
		setRecent(false);
		setItems(new ItemBuilder(Material.POTION).setDurability(16418).setAmount(2).setName("§bVelocidade").getStack());
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (hasKit(p)) {
			for (ListIterator<ItemStack> item = event.getDrops().listIterator(); item.hasNext();) {
				ItemStack itemStack = item.next();
				if (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals("§bVelocidade")) {
					item.remove();
				}
			}
		}
	}

	public void giveItems(Player player) {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(getManager().getPlugin(), new Runnable() {
			public void run() {
				ItemStack Item = new ItemBuilder(Material.POTION).setDurability(16418).setAmount(2).setName("§bVelocidade").getStack();
				if (!player.getInventory().contains(Item))
					player.getInventory().addItem(Item);
				player.updateInventory();
			}
		}, 0, 20 * 600);
	}

}
