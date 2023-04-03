package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class Stomper extends Kit {

	public Stomper(Manager manager) {
		super(manager);

		setPrice(51000);
		setCooldownTime(0D);
		setIcon(new ItemStack(Material.ANVIL));
		setFree(false);
		setDescription("Converta toda energia potencial gravitacional em dano a seus oponentes.");
		setRecent(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStomp(EntityDamageEvent event) {
		stompar(event, 6.0D);
	}

	public void stompar(EntityDamageEvent event, double radius) {
		if (((event.getEntity() instanceof Player)) && (hasKit((Player) event.getEntity()))
				&& (event.getCause() == EntityDamageEvent.DamageCause.FALL) && (!isInvencibility())) {
			Player player = (Player) event.getEntity();
			if (getGamer(player).isSpectating().booleanValue()) {
				return;
			}
			if (Endermage.invencible.contains(player.getUniqueId())) {
				return;
			}

			if (Launcher.getNoFallList().contains(player)) {
				event.setCancelled(true);
				Launcher.getNoFallList().remove(player);
				return;
			}
			if (event.getDamage() > 4.0D) {
				event.setCancelled(true);
				player.damage(4.0D);
			} else {
				event.setCancelled(true);
				player.damage(event.getDamage());
			}

			double dmg = event.getDamage();
			boolean hasPlayer = false;
			for (Player stompado : Bukkit.getOnlinePlayers()) {
				if (stompado.getUniqueId() == player.getUniqueId())
					continue;
				if (AdminMode.getInstance().isAdmin(stompado))
					continue;
				if (getGamer(stompado).isSpectating().booleanValue())
					continue;
				if (getGamer(stompado).getKit().getName().equalsIgnoreCase("AntiTower")) 
					continue;
				if (getGamer(stompado).getKit2().getName().equalsIgnoreCase("AntiTower")) 
					continue;
				if (stompado.getLocation().distance(player.getLocation()) > 6)
					continue;
				double dmg2 = dmg * (10 / 10d);
				if ((stompado.isSneaking()) && dmg2 > 4d)
					dmg2 = 4d;
				stompado.damage(dmg2, player);
				hasPlayer = true;
			}
			if (hasPlayer) {
				player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
			}
		}
	}
}
