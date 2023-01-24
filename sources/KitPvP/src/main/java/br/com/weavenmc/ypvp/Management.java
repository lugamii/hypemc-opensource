package br.com.weavenmc.ypvp;

import org.bukkit.Server;
import org.bukkit.event.Listener;

import lombok.Getter;

@Getter
public abstract class Management {

	private yPvP plugin;
	
	public Management(yPvP plugin) {
		this.plugin = plugin;
	}
	
	public abstract void enable();
	
	public abstract void disable();
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, getPlugin());
	}
	
	public Server getServer() {
		return plugin.getServer();
	}
}
