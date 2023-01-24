package br.com.weavenmc.skywars.hability.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.hability.HabilityAPI.Hability;

public class EnderLordEvent implements Listener {
	
    @EventHandler
    public void onEntityDamage(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
 
        if (WeavenSkywars.getGameManager().getHabilityAPI().isHabilidade(player, Hability.ENDER_LORD)) {
        	if (event.getCause() == TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
                player.teleport(event.getTo());
            }
        	return;
        }
    }

}
