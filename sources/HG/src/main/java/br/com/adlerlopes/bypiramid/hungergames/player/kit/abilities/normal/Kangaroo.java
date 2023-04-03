package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;


/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Kangaroo extends Kit {

	private static HashMap<UUID, Long> hitNerf = new HashMap<>();
	private static List<UUID> kangarooUses = new ArrayList<>();

	public Kangaroo(Manager manager) {
		super(manager);
		
		setPrice(41000);
		setCooldownTime(30D);
		setIcon(new ItemStack(Material.FIREWORK));
		setFree(true);
		setDescription("Transforme-se em um canguru dê pulos duplos e mova-se mais rapido");
		setRecent(false);
		setItems(new ItemBuilder(Material.FIREWORK).setName("§aKangaroo").getStack());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (hasKit(player) && event.getAction() != Action.PHYSICAL && isKitItem(event.getItem(), Material.FIREWORK, "§aKangaroo")) {
			event.setCancelled(true);

			if (kangarooUses.contains(player.getUniqueId()))
				return;

			if (hitNerf.containsKey(player.getUniqueId()) && hitNerf.get(player.getUniqueId()) > System.currentTimeMillis()) {
				player.setVelocity(new Vector(0, -1.0, 0));
				player.sendMessage("§c§lKANGAROO §fVocê está em §4§lCOMBATE§f, aguarde para usar sua habilidade.");
				return;
			}

			Vector vector = player.getEyeLocation().getDirection();
			if (player.isSneaking()) {
				vector = vector.multiply(1.8F).setY(0.5F);
			} else {
				vector = vector.multiply(0.5F).setY(1F);
			}

			player.setFallDistance(-1.0F);
			player.setVelocity(vector);
			kangarooUses.add(player.getUniqueId());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (kangarooUses.contains(e.getPlayer().getUniqueId()) && (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR || e.getPlayer().isOnGround())) {
			kangarooUses.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!hasKit((Player) event.getEntity()))
			return;
		if (event.isCancelled())
			return;
		if (!(event.getDamager() instanceof LivingEntity))
			return;
		if (getManager().getGameManager().isInvencibility())
			return;
		if (event.getDamager() instanceof Player && getGamer((Player) event.getDamager()).isSpectating())
			return;

		hitNerf.put(event.getEntity().getUniqueId(), System.currentTimeMillis() + 3000L);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && hasKit((Player) e.getEntity()) && e.getCause() == DamageCause.FALL && e.getDamage() > 7.0D) {
			e.setDamage(7.0D);
		}
	}
}
