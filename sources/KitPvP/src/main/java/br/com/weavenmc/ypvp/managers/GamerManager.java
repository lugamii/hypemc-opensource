package br.com.weavenmc.ypvp.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import br.com.weavenmc.ypvp.Management;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class GamerManager extends Management {

	private HashMap<UUID, Gamer> gamers;
	
	public GamerManager(yPvP plugin) {
		super(plugin);
	}

	public void loadGamer(UUID uuid, Gamer gamer) {
		gamers.put(uuid, gamer);
	}
	
	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}
	
	public Collection<Gamer> getGamers() {
		return gamers.values();
	}
	
	public void unloadGamer(UUID uuid) {
		gamers.remove(uuid);
	}
	
	@Override
	public void enable() {
		gamers = new HashMap<>();
	}

	@Override
	public void disable() {
		gamers.clear();
		gamers = null;
	}
}
