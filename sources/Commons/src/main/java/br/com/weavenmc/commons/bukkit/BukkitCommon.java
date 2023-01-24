package br.com.weavenmc.commons.bukkit;

import org.bukkit.Server;
import org.bukkit.event.Listener;

public abstract class BukkitCommon {
	
	private BukkitMain plugin;
	
	public BukkitCommon(BukkitMain plugin) {
		this.plugin = plugin;
	}
	
	public abstract void onEnable();
	
	public abstract void onDisable();

	public BukkitMain getPlugin() {
		return plugin;
	}
	
	public Server getServer() {
		return plugin.getServer();
	}
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, plugin);
	}
}
