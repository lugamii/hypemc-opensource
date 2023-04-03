package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Fisherman extends Kit {

	public Fisherman(Manager manager) {
		super(manager);
		
		setPrice(50000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.FISHING_ROD));
		setFree(false);
		setDescription("Trabalhe com sua vara de pesca e fisgue seus oponentes para a morte.");
		setRecent(false);
		setItems(new ItemStack(Material.FISHING_ROD));
	}

	@EventHandler
	public void onFisherman(PlayerFishEvent event) {
		Player player = event.getPlayer();
		if (!hasKit(player))
			return;

		player.getItemInHand().setDurability((short) 0);

		if (isInvencibility()) {
			player.sendMessage("§6§lFISHERMAN §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
			return;
		}

		if (event.getState() == State.CAUGHT_ENTITY)
			event.getCaught().teleport(player.getLocation().add(0.5D, 0, 0.5D));
	}
}
