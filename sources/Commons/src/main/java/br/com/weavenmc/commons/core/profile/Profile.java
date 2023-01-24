package br.com.weavenmc.commons.core.profile;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Profile {
	
	private String name;
	private UUID id;
	
}
