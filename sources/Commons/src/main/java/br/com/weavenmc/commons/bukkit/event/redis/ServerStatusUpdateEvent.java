package br.com.weavenmc.commons.bukkit.event.redis;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.weavenmc.commons.core.server.NetworkServer;
import lombok.Getter;
import lombok.Setter;

public class ServerStatusUpdateEvent extends Event implements Cancellable {

	private final static HandlerList handlers = new HandlerList();
	
	@Setter
	@Getter
	private NetworkServer weavenServer;
	@Setter
	@Getter
	private String messageChannel;
	private boolean cancel = false;
	
	public ServerStatusUpdateEvent(NetworkServer server, String messageChannel) {
		this.weavenServer = server;
		this.messageChannel = messageChannel;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
