package br.com.weavenmc.commons.bukkit.event.redis;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

@Getter
public class BukkitRedisMessageEvent extends Event {

	private final static HandlerList handlers = new HandlerList();
	
	private String channel, message;
	
	public BukkitRedisMessageEvent(String channel, String message) {
		this.channel = channel;
		this.message = message;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
