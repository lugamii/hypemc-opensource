package br.com.weavenmc.commons.core.account;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {

	private Map<UUID, WeavenPlayer> players;
	
	public AccountManager(){
		players = new ConcurrentHashMap<>();
	}
	
	public void loadWeavenPlayer(UUID uuid, WeavenPlayer player) {
		players.put(uuid, player);
	}
	
	public WeavenPlayer getWeavenPlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	public void unloadWeavenPlayer(UUID uuid) {
		players.remove(uuid);
	}
	
	public Collection<WeavenPlayer> getPlayers() {
		return players.values();
	}
}
