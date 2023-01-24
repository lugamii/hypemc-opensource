package br.com.weavenmc.skywars.hability.events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;
import br.com.weavenmc.skywars.hability.cooldown.Cooldown;
import br.com.weavenmc.skywars.hability.cooldown.CooldownAPI;

public class SpartanEvent implements Listener {
	
	private HashMap<UUID, Integer> kills = new HashMap<>();
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getEntity().getKiller() instanceof Player) {
				Player killer = e.getEntity().getKiller();
				if (kills.containsKey(killer.getUniqueId())) {
					kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onBlock(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (player.getInventory().getItemInHand().getType() == Material.WOOL
				&& player.getInventory().getItemInHand().getItemMeta().getDisplayName() == "§c§lSpartan"
				&& WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.SPARTAN)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (player.getInventory().getItemInHand().getType() == Material.WOOL && WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.SPARTAN)) {
			e.setCancelled(true);
			if (CooldownAPI.hasCooldown(player, "Spartan")) {
				player.sendMessage(CooldownAPI.getMessage(player));
				return;
			}
			CooldownAPI.addCooldown(player, new Cooldown("Spartan", 35L));
			player.getNearbyEntities(6.0D, 7.0D, 6.0D).forEach(entity -> {
				if ((entity instanceof Player)) {
					Player players = (Player) entity;
					players.sendMessage("§4§lSPARTAN!!!");
				}
			});
			player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 5f, 5f);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10*20, 1), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10*20, 0), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10*20, 0), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10*20, 1), true);
			kills.put(player.getUniqueId(), 1);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					int i = 5 * kills.get(player.getUniqueId());
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, i*20, 1), true);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, i*20, 0), true);
					
				}
			}.runTaskLater(WeavenSkywars.getInstance(), 10*20);
		}
	}

}
