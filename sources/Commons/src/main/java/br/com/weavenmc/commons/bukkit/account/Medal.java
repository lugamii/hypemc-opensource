package br.com.weavenmc.commons.bukkit.account;

import br.com.weavenmc.commons.core.permission.Group;
import lombok.Getter;

@Getter
public enum Medal {

	SMILE("§a", "ツ", Group.VIP, "sorriso"), 
	PEACE("§b", "☮", Group.VIP, "paz"), 
	KING("§4", "♛", Group.BLADE, "rei"),
	YIN_YANG("§3", "☯", Group.BLADE, "ying&yang", "yingyang", "yingeyang", "yingandyang"),
	STAR("§2", "✰", Group.BLADE, "estrela"), 
	TOXIC("§e", "☣", Group.BLADE, "tóxico", "toxico"),
	KILLER("§c", "☠", Group.BLADE, "mataddor"),
	LOVE("§c", "❤", Group.ULTRA, "amor"),
	DANGER("§c", "☠ ", Group.BETA, "perigoso", "perigo");

	private String color;
	private String symbol;
	private Group groupToUse;
	private String[] names;

	private Medal(String color, String symbol, Group groupToUse, String... names) {
		this.color = color;
		if (symbol.contains("☠") && symbol.length() > 1) {
			this.symbol = symbol.substring(0, 1);
		} else {
			this.symbol = symbol;
		}
		this.groupToUse = groupToUse;
		this.names = names;
	}

	public static Medal fromString(String name) {
		for (Medal medal : values()) {
			if (!name.equalsIgnoreCase(medal.name())) {
				for (String n : medal.getNames()) {
					if (!name.equalsIgnoreCase(n))
						continue;
					return medal;
				}
			} else {
				return medal;
			}
		}
		return null;
	}
}
