package br.com.weavenmc.skywars.player;

import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.weavenmc.skywars.game.GameController;

public class PlayerManager {
	
	private static UUID uuid;
	private static int kills;

	
	public PlayerManager(Player player) {
		uuid = player.getUniqueId();
		if (!GameController.player.contains(player.getUniqueId())) {
			PlayerController.live = false;
		} else {
			PlayerController.live = true;
		}
		PlayerManager.kills = PlayerController.kills.get(player.getUniqueId());
	}
	
	public void setHash() {
		PlayerController.kills.put(uuid, getKills());
	}
	
	public void setArray() {
		if (PlayerController.live == true) {
			GameController.player.add(uuid);
			GameController.spectador.remove(uuid);
		} else {
			GameController.player.remove(uuid);
			GameController.spectador.add(uuid);
		}
	}
	
	public int getKills() {
		return kills;
	}
	
	public UUID getUuid() {
		return uuid;
	}

}
