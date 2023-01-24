package br.com.weavenmc.commons.bukkit.chat;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import lombok.Getter;

@Getter
public class ChatEvent {

	private final Player player;
	private final Set<Player> recipients;
	private String message;
	private String format;
	private boolean cancelled;
	
	public ChatEvent(final AsyncPlayerChatEvent event) {
		player = event.getPlayer();
		recipients = event.getRecipients();
		message = event.getMessage();
		format = event.getFormat();
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
