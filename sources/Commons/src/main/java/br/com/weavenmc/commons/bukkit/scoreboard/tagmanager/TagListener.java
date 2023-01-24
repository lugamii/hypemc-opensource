package br.com.weavenmc.commons.bukkit.scoreboard.tagmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;

public class TagListener implements Listener {

	private TagManager manager;
	
	public TagListener(TagManager manager) {
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		manager.removePlayerTag(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinFirst(PlayerJoinEvent event) {
		event.getPlayer().setScoreboard(manager.getServer().getScoreboardManager().getNewScoreboard());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinListener(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
		if (player == null)
			return;
		player.updateTags();
		manager.setTag(p, player.getTags().get(0));
		player = null;
		p = null;
	}
}
