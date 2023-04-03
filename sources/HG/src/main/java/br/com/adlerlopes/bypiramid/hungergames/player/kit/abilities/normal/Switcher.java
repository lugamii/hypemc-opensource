package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Switcher extends Kit {

	public Switcher(Manager manager) {
		super(manager);
		
		setPrice(45000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.SNOW_BALL));
		setFree(false);
		setDescription("Use sua habilidade para trocar de localização com seus oponentes.");
		setRecent(false);
		setItems(new ItemBuilder(Material.SNOW_BALL).setAmount(10).getStack());
	}

	@EventHandler
	public void snowball(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Snowball && event.getEntity() instanceof LivingEntity) {
			Snowball snowball = (Snowball) event.getDamager();
			Player shooter = (Player) snowball.getShooter();
			if (!hasKit(shooter))
				return;

			if (isInvencibility()) {
				shooter.sendMessage("§6§lSWITCHER §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
				return;
			}

			if (!(snowball.getShooter() instanceof Player))
				return;

			if (Gladiator.inGladiator(shooter)) {
				shooter.sendMessage("§6§lSWITCHER §fVocê não pode usar seu kit no §e§lGLADIATOR");
				event.setCancelled(true);
				shooter.updateInventory();
				return;
			}

			Location shooterLoc = shooter.getLocation();
			shooter.teleport(event.getEntity().getLocation());
			event.getEntity().teleport(shooterLoc);
		}
	}

}
