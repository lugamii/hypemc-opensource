package br.com.weavenmc.commons.core.command;

import java.util.UUID;

public interface CommandSender {

	public abstract UUID getUniqueId();
	
	public abstract boolean isPlayer();
}
