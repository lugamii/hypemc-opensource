package br.com.weavenmc.skywars.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;

import br.com.weavenmc.skywars.player.PlayerManager;

public class GameController {
	
	public static ArrayList<UUID> player = new ArrayList<>();
	public static ArrayList<UUID> spectador = new ArrayList<>();
	public static ArrayList<Block> jailBlock = new ArrayList<>();
	
	public static HashMap<UUID, PlayerManager> check = new HashMap<>();
	

}
