package br.com.mcweaven.gladiator.gamer.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.mcweaven.gladiator.gamer.Gamer;

public class GamerManager {

	Map<UUID, Gamer> gamers;

	public GamerManager() {
		gamers = new HashMap<>();
	}

	public void registerGamer(UUID uuid) {
		gamers.put(uuid, new Gamer(uuid));
	}


	public void unregisterGamer(UUID uuid) {
		gamers.remove(uuid);
	}
	
	public Gamer getGamer(UUID uuid) {
		if (!gamers.containsKey(uuid))
			registerGamer(uuid);
		return gamers.get(uuid);
	}
	
	public Collection<Gamer> getGamers() {
		return gamers.values();
	}
	
	public void clear() {
		gamers.clear();
	}
}
