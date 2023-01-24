package br.com.weavenmc.skywars.combatlog;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.utils.Debug.TypeAlert;

public class Combat {
	
	public static HashMap<UUID, UUID> combatLogs = new HashMap<>();
	
	public static void setCombat(Player player, Player target) {
		combatLogs.put(player.getUniqueId(), target.getUniqueId());
		combatLogs.put(target.getUniqueId(), player.getUniqueId());
		checkCombat(player);
		checkCombat(target);
		WeavenSkywars.getInstance().debug.sendMessage(player.getName() + " adicionado em combate!", TypeAlert.ALERTA);
	}
	
	public static void removeCombat(Player player) {
		if (inCombat(player)) {
			combatLogs.remove(player.getUniqueId());
			WeavenSkywars.getInstance().debug.sendMessage(player.getName() + " removido do combate!", TypeAlert.ALERTA);
		}
	}
	
	public static void checkCombat(Player player) {
		new BukkitRunnable() {
			int i = 20;
			@Override
			public void run() {
				if (i > 0) {
					if (i == 0) {
						removeCombat(player);;
						cancel();
					}
					i--;
				}
				
			}
		}.runTaskTimer(WeavenSkywars.getInstance(), 20L, 20L);
	}
	
	public static boolean inCombat(Player player) {
		if (!combatLogs.containsKey(player.getUniqueId()))
			return false;
		WeavenSkywars.getInstance().debug.sendMessage(player.getName() + " esta em combate!", TypeAlert.ALERTA);
		return true;
	}

}
