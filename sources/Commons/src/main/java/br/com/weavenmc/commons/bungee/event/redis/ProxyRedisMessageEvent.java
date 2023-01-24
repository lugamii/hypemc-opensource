package br.com.weavenmc.commons.bungee.event.redis;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
@Getter
public class ProxyRedisMessageEvent extends Event {

	@NonNull
	private String channel, message;
}
