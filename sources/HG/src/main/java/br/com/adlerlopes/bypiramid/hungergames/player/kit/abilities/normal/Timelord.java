package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Timelord extends Kit {

	private static List<UUID> lockPlayer = new ArrayList<>();
	private static ItemBuilder timelordItem = new ItemBuilder(Material.WATCH).setName("§eTimelord");

	public Timelord(Manager manager) {
		super(manager);
		
		setPrice(40000);
		setCooldownTime(30D);
		setIcon(new ItemStack(Material.WATCH));
		setFree(false);
		setDescription("Paralize seus inimigos no espaço-tempo!");
		setRecent(false);
		setItems(timelordItem.getStack());
	}

	public void freeze(Player player) {
		if (!lockPlayer.contains(player.getUniqueId())) {
			lockPlayer.add(player.getUniqueId());
			player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 10.0F, 10.0F);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 4));
			player.sendMessage("§6§lTIMELORD §fVocê foi §E§LPARALIZADO§f por §c§l10 segundos§f, aguarde para ser §e§lDESCONGELADO");
		}
	}

	public void unfreeze(Player player) {
		if (lockPlayer.contains(player.getUniqueId())) {
			lockPlayer.remove(player.getUniqueId());
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

	public void playEffect(Player player, Location location, boolean bool) {
		for (int i = 0; i <= 50; i += ((!bool && (i == 50)) ? 2 : 1))
			location.getWorld().playEffect(location, Effect.SMOKE, i);
	}


	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (lockPlayer.contains(p.getUniqueId())) {
			if (((event.getTo().getX() != event.getFrom().getX()) || (event.getTo().getZ() != event.getFrom().getZ()))) {
				event.setTo(event.getFrom());
				return;
			}
		}
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (hasKit(player)) {
				if (isKitItem(player.getItemInHand(), "§eTimelord")) {

					if (isInvencibility()) {
						player.sendMessage("§6§lTIMELORD §fVocê não pode usar seu kit na §e§lINVENCIBILIDADE");
						return;
					}

					if (inCooldown(player)) {
						sendCooldown(player);
						return;
					}

					addCooldown(player);
					player.getWorld().playSound(player.getLocation(), Sound.AMBIENCE_CAVE, 1.0F, 1.0F);

					for (Entity entity : player.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
						if (entity instanceof Player) {
							Player targetPlayer = (Player) entity;

							if (getGamer(targetPlayer).isSpectating())
								continue;

							freeze(targetPlayer);

							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getManager().getPlugin(), new Runnable() {
								public void run() {
									unfreeze(targetPlayer);
								}
							}, 20 * 10);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void combatPlayer(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			unfreeze((Player) event.getEntity());
		}
	}
}
