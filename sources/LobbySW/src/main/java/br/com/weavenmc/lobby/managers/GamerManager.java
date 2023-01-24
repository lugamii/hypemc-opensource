package br.com.weavenmc.lobby.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.Management;
import br.com.weavenmc.lobby.gamer.Gamer;

public class GamerManager extends Management {

	private HashMap<UUID, Gamer> gamers;

	public GamerManager(Lobby plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		gamers = new HashMap<>();
	}

	public void loadGamer(UUID uuid, Gamer gamer) {
		gamers.put(uuid, gamer);
	}

	public Gamer getGamer(UUID uuid) {
		return gamers.get(uuid);
	}

	public Collection<Gamer> getGamers() {
		if (gamers == null)
			return null;

		if (gamers.isEmpty())
			return null;
		return gamers.values();
	}

	public void unloadGamer(UUID uuid) {
		gamers.remove(uuid);
	}

	@Override
	public void disable() {
		gamers.clear();
		gamers = null;
	}
}
