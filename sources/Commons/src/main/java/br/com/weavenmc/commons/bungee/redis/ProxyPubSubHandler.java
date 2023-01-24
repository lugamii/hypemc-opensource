package br.com.weavenmc.commons.bungee.redis;

import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.event.redis.ProxyRedisMessageEvent;
import redis.clients.jedis.JedisPubSub;

public class ProxyPubSubHandler extends JedisPubSub {

	@Override
	public void onMessage(String channel, String message) {
		BungeeMain.getInstance().getProxy().getPluginManager()
				.callEvent(new ProxyRedisMessageEvent(channel, message));
	}
}
