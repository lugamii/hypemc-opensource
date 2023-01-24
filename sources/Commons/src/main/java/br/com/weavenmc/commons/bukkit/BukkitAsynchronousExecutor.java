package br.com.weavenmc.commons.bukkit;

import org.bukkit.Server;

import br.com.weavenmc.commons.core.AsynchronousExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BukkitAsynchronousExecutor implements AsynchronousExecutor {
	
	private Server server;
	private BukkitMain plugin;

	@Override
	public void runAsync(Runnable r) {
		getServer().getScheduler().runTaskAsynchronously(getPlugin(), r);
	}
}
