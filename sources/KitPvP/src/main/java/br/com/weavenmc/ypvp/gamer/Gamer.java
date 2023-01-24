package br.com.weavenmc.ypvp.gamer;

import java.util.UUID;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.minigame.Minigame;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Gamer {

	private String name;
	private UUID uniqueId;
	@Setter
	private Minigame warp;
	@Setter
	private Sidebar sidebar;
	@Setter
	private Ability ability;
	
	private long lastCombatTime = 0L;
	@Setter
	private UUID lastCombat = null, spectator = null;
	
	public Gamer(BukkitPlayer bP) {
		this.name = bP.getName();
		this.uniqueId = bP.getUniqueId();
	}
	
	public boolean hasSpectator() {
		return spectator != null;
	}
	
	public void resetCombat() {
		lastCombatTime = 0L;
		lastCombat = null;
	}
	
	public void addCombat(UUID uuid, int time) {
		lastCombatTime = (time * 1000L) + System.currentTimeMillis();
		lastCombat = uuid;
	}
	
	public boolean inCombat() {
		return lastCombatTime >= System.currentTimeMillis();
	}
}
