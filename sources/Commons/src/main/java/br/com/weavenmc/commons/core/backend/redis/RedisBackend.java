package br.com.weavenmc.commons.core.backend.redis;

import br.com.weavenmc.commons.core.backend.Backend;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class RedisBackend implements Backend {
	
	@NonNull
	private final String hostname, password;
	private final int port;
	
	@Getter
	private JedisPool pool;
	@Getter
	private Jedis jedis;
	
	public RedisBackend() {
		this("localhost", "", 6379);
	}
	
	@Override
	public void openConnection() {
		//JedisPoolConfig config = new JedisPoolConfig();
		//config.setMaxTotal(128);
		pool = new JedisPool(hostname, port);
		jedis = pool.getResource();
	}

	@Override
	public void closeConnection() {
		if (pool != null) {
			pool.destroy();
		}
	}

	@Override
	public boolean isConnected() {
		if (jedis == null)
			return false;
		if (pool == null)
			return false;
		return !pool.isClosed();
	}

	@Override
	public void recallConnection() {
		
	}
}
