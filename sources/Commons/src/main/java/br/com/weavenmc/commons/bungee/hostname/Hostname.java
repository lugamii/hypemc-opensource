package br.com.weavenmc.commons.bungee.hostname;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Hostname {

	private String hostname;
	private int connections;

	public void addConnections(int i) {
		connections  += i;
	}
	
	
}
