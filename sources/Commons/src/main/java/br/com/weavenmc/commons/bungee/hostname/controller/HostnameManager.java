package br.com.weavenmc.commons.bungee.hostname.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.weavenmc.commons.bungee.hostname.Hostname;

public class HostnameManager {

	Map<String, Hostname> hostname;

	public HostnameManager() {
		hostname = new HashMap<>();
	}

	public void registerHostname(String host) {
		hostname.put(host, new Hostname(host, 0));
	}

	public Hostname getHostname(String host) {
		if (!hostname.containsKey(host))
			registerHostname(host);
		return hostname.get(host);
	}

	public Collection<Hostname> getHostnames() {
		return hostname.values();
	}

	public void clear() {
		hostname.clear();
	}

}
