package br.com.weavenmc.commons.core.backend.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySQL {

	private String hostname;
	private String database;
	private String username;
	private String password;
	private int port;
}
