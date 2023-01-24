package br.com.weavenmc.commons.bukkit.chat;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandlerAPI {

	private static final Queue<ChatHandler> chathandlers = new ConcurrentLinkedQueue<>();
	
	public static boolean hasHandler() {
		return chathandlers.size() > 0;
	}
	
	public static ChatEvent callChatEvent(final AsyncPlayerChatEvent event) {
		ChatEvent chatevent = new ChatEvent(event);
		Iterator<ChatHandler> handlers = chathandlers.iterator();
		while (handlers.hasNext()) 
			handlers.next().onChat(chatevent);		
		return chatevent;
	}
	
	public static void addHandler(ChatHandler handler) {
		chathandlers.add(handler);
	}
}
