package br.com.weavenmc.ypvp.minigame;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Minigame implements Listener {
	
	@Setter
	private String name;
	@Setter
	private String[] otherNames;
	private Set<UUID> protection = new HashSet<>();
	private Set<UUID> players = new HashSet<>();
		
	private UUID topperKs = null;
	private int topKs = 0;
	@Setter
	private boolean topKillStreakMinigame = false;
	
	public abstract void join(Player p);
	
	public abstract void quit(Player p);
	
	public void updateTopKs() {
		int maxKillStreak = 0;
		UUID topper = null;
		for (Player p : Bukkit.getOnlinePlayers()) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() != this)
				continue;
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			if (bP == null)
				continue;
			int killStreak = bP.getData(DataType.PVP_KILLSTREAK).asInt();
			if (killStreak <= maxKillStreak)
				continue;
			maxKillStreak = killStreak;
			topper = p.getUniqueId();
		}
		if (topper != null) {
			topKs = maxKillStreak;
			topperKs = topper;
		} else {
			topKs = 0;
			topperKs = null;
		}
	}
	
	public String getTopKsName() {
		if (topperKs == null)
			return "Ninguém";
		Player p = Bukkit.getPlayer(topperKs);
		if (p != null) 
			return p.getName();
		return "Ninguém";
	}
	
	public int getTopKs() {
		if (topperKs == null)
			return 0;
		Player p = Bukkit.getPlayer(topperKs);
		if (p != null) 
			return topKs;
		return 0;
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getCurrentTick() % 40 != 0)
			return;
		if (!topKillStreakMinigame)
			return;
		updateTopKs();
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		quit(event.getPlayer());
	}
	
	public void protect(Player p) {
		if (!protection.contains(p.getUniqueId())) {
			protection.add(p.getUniqueId());
		}
	}
	
	public void teleport(Player p) {
		Location loc = yPvP.getPlugin().getLocationManager().getLocation(name);
		if (loc != null) {
			p.teleport(loc);
			loc = null;
		}
	}
	
	public boolean isProtected(Player p) {
		return protection.contains(p.getUniqueId());
	}
	
	public void unprotect(Player p) {
		if (protection.contains(p.getUniqueId())) {
			protection.remove(p.getUniqueId());
		}
	}
	
	public void joinPlayer(UUID uuid) {
		if (!players.contains(uuid)) {
			players.add(uuid);
		}
	}
	
	public void quitPlayer(UUID uuid) {
		if (players.contains(uuid)) {
			players.remove(uuid);
		}
	}
	
	public int getPlaying() {
		return players.size();
	}
}
