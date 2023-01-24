package br.com.weavenmc.commons.bungee.login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginCacheAPI {

	private final static Map<String, LoginCache> CACHES = new ConcurrentHashMap<>();
	
	public static LoginCache getCache(String ipAddress) {
		return CACHES.get(ipAddress);
	}
	
	public static LoginCache addCache(String ipAddress, String username) {
		LoginCache cache = new LoginCache();
		cache.addSession(username);
		return CACHES.computeIfAbsent(ipAddress, c -> cache);
	}
}
