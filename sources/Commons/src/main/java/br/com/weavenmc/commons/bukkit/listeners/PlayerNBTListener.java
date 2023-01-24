package br.com.weavenmc.commons.bukkit.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;

public class PlayerNBTListener implements Listener {

	@EventHandler
	public void onUpdateListener(UpdateEvent event) {
		if (event.getType() != UpdateType.HOUR)
			return;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "debug");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		removePlayerFile(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		removePlayerFile(event.getPlayer().getUniqueId());
	}

	private void removePlayerFile(UUID uuid) {
		World world = Bukkit.getWorlds().get(0);
		File folder = new File(world.getWorldFolder(), "playerdata");
		if (folder.exists() && folder.isDirectory()) {
			File file = new File(folder, uuid.toString() + ".dat");
			Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitMain.getInstance(), () -> {
				if (file.exists() && !file.delete()) {
					removePlayerFile(uuid);
				}
			}, 2L);
		}
	}
}