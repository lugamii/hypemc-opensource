package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Reaper extends Kit {

	public Reaper(Manager manager) {
		super(manager);
		
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.WOOD_HOE));
		setFree(false);
		setDescription("Ceife a vida de seus inimgos, deixando-os com um veneno mortal.");
		setRecent(false);
		setItems(createItemStack("§4Reaper", Material.WOOD_HOE));
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getItemInHand().getType() == Material.WOOD_HOE && hasKit(player)) {
				if (isInvencibility()) {
					player.sendMessage("§6§lREAPER §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
					return;
				}
				if (event.getEntity() instanceof LivingEntity) {
					((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 6 * 20, 0), true);
				}
			}
		}
	}

}
