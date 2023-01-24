package br.com.weavenmc.commons.bungee;

import br.com.weavenmc.commons.core.AsynchronousExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

@Getter
@AllArgsConstructor
public class BungeeAsynchronousExecutor implements AsynchronousExecutor {
	
	private ProxyServer proxy;
	private BungeeMain plugin;
	
	@Override
	public void runAsync(Runnable r) {
		getProxy().getScheduler().runAsync(getPlugin(), r);
	}	
}
