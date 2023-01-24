package br.com.weavenmc.commons.bukkit.redis;

import org.bukkit.Bukkit;

import br.com.weavenmc.commons.bukkit.event.redis.BukkitRedisMessageEvent;
import redis.clients.jedis.JedisPubSub;

public class BukkitPubSubHandler extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        Bukkit.getPluginManager().callEvent(new BukkitRedisMessageEvent(channel, message));
    }
}
