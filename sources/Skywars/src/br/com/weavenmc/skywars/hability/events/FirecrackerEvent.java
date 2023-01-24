package br.com.weavenmc.skywars.hability.events;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.hability.cooldown.Cooldown;
import br.com.weavenmc.skywars.hability.cooldown.CooldownAPI;

public class FirecrackerEvent implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (WeavenSkywars.getGameManager().getState() != GameState.GAME) {
			return;
		}
		if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.FOGUETE)) {
			if (player.getInventory().getItemInHand().getType().equals(Material.FIREWORK)) {
				e.setCancelled(true);
				if (CooldownAPI.hasCooldown(player, "Foguete")) {
					player.sendMessage(CooldownAPI.getMessage(player));
					return;
				}
				CooldownAPI.addCooldown(player, new Cooldown("Foguete", 20L));
				Firework fw = (Firework)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				fwm.setPower(1);
				fw.setPassenger(player);
				FireworkEffect.Type type = FireworkEffect.Type.BALL;
				Color c1 = Color.RED;
				Color c2 = Color.LIME;
				Color c3 = Color.SILVER;
				FireworkEffect effect = FireworkEffect.builder().flicker(new Random().nextBoolean()).withColor(c1).withColor(c2).withFade(c3).with(type).build();
				fwm.addEffect(effect);
				fw.setFireworkMeta(fwm);
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7*20, 9999));
				new BukkitRunnable() {
					
					@Override
					public void run() {
						fw.detonate();
						
					}
				}.runTaskLater(WeavenSkywars.getInstance(), 4*20);
			}
		}
	}

}
