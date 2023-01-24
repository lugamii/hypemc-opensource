package br.com.weavenmc.commons.core.backend.redis;

import java.util.logging.Level;

import br.com.weavenmc.commons.WeavenMC;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener implements Runnable {
	private JedisPubSub jpsh;

	private final String[] channels;

	public PubSubListener(JedisPubSub s, String... channels) {
		this.jpsh = s;
		this.channels = channels;
	}

	@Override
	public void run() {
		boolean broken = false;
		try (Jedis rsc = WeavenMC.getCommonRedis().getPool().getResource()) {
			try {
				rsc.subscribe(jpsh, channels);
			} catch (Exception e) {
				WeavenMC.getLogger().log(Level.INFO, "PubSub error, attempting to recover.", e);
				try {
					jpsh.unsubscribe();
				} catch (Exception e1) {

				}
				broken = true;
			}
		}

		if (broken) {
			run();
		}
	}

	public void addChannel(String... channel) {
		jpsh.subscribe(channel);
	}

	public void removeChannel(String... channel) {
		jpsh.unsubscribe(channel);
	}

	public void poison() {
		jpsh.unsubscribe();
	}
}
