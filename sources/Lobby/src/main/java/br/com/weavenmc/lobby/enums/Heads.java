package br.com.weavenmc.lobby.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Heads {

	SLIME("Slime", "kobyjo"), BEACON("Beacon", "MFH_Beacon"), PUMPKIN("Abóbora", "Rovertdoow1st"),
	CHICKEN("Galinha", "Gaygus"), BOY("Garoto", "Hentalon");

	private String headName, skinHeadName;

}
