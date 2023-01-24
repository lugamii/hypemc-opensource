package br.com.weavenmc.skywars.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamagerFixerListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void asd(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			if (e.getDamage() > 1.0D) {
				e.setDamage(e.getDamage() - 1.0D);
			}
			if ((e.getDamager() instanceof Player)) {
				if ((player.getFallDistance() > 0.0F) && (!player.isOnGround())
						&& (!player.hasPotionEffect(PotionEffectType.BLINDNESS))) {
					double NewDamage = (e.getDamage() * 1.5D) - e.getDamage();
					if (e.getDamage() > 1.0D) {
						e.setDamage(e.getDamage() - NewDamage);
					}
				}
				if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
					for (PotionEffect Effect : player.getActivePotionEffects()) {
						if (Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
							double division = (Effect.getAmplifier() + 2) * 1.3D + 1.0D;
							double damage;
							if (e.getDamage() / division <= 1.0D)
								damage = (Effect.getAmplifier() + 2) * 3 + 3;
							else {
								damage = e.getDamage() / division;
							}
							
							e.setDamage(Double.valueOf(damage));
							break;
						}
					}
				}
				if (player.getItemInHand().getType() == Material.AIR) {
					e.setDamage(0.5D);
				} else if (player.getItemInHand().getType() == Material.WOOD_SWORD) {
					e.setDamage(2.0D);
				} else if (player.getItemInHand().getType() == Material.STONE_SWORD) {
					e.setDamage(3.0D);
				} else if (player.getItemInHand().getType() == Material.GOLD_SWORD) {
					e.setDamage(4.0D);
				} else if (player.getItemInHand().getType() == Material.IRON_SWORD) {
					e.setDamage(4.0D);
				} else if (player.getItemInHand().getType() == Material.DIAMOND_SWORD) {
					e.setDamage(5.0D);
				}
				
				for (PotionEffect Effect : player.getActivePotionEffects()) {
					if ((Effect.getType().equals(PotionEffectType.INCREASE_DAMAGE))
							&& (player.getItemInHand() != null)
							&& (player.getItemInHand().getType().name().contains("SWORD"))) {
						Random r = new Random();
						if (r.nextInt(3) == 0) {
							e.setDamage(e.getDamage() + 2.0D);
							break;
						}
						e.setDamage(e.getDamage() + 1.5D);
					}
				}
				
				if (player.getFallDistance() > 0.0F && !player.isOnGround() && !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					if (player.getItemInHand().getType() == Material.AIR) {
						e.setDamage(0.5D);
					}
					if (player.getItemInHand().getType() == Material.WOOD_SWORD) {
						e.setDamage(e.getDamage() + 1.0D);
					}
					if (player.getItemInHand().getType() == Material.STONE_SWORD) {
						e.setDamage(e.getDamage() + 1.0D);
					}
					if (player.getItemInHand().getType() == Material.GOLD_SWORD) {
						e.setDamage(e.getDamage() + 1.5D);
					}
					if (player.getItemInHand().getType() == Material.IRON_SWORD) {
						e.setDamage(e.getDamage() + 1.0D);
					}
					if (player.getItemInHand().getType() == Material.DIAMOND_SWORD) {
						e.setDamage(e.getDamage() + 1.0D);
					}
				}
			}
		}
	}

}
