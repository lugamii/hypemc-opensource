package br.com.weavenmc.skywars.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.weavenmc.skywars.game.GameController;

public class PlayerController {
	
	public static boolean live;
	public static HashMap<UUID, Integer> kills = new HashMap<>();
	
	public static PlayerManager getPlayer(Player player) {
		return (PlayerManager) GameController.check.get(player.getUniqueId());
	}

}
