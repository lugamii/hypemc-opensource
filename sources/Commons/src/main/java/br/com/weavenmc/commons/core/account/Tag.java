package br.com.weavenmc.commons.core.account;

import java.util.Arrays;
import java.util.List;

import br.com.weavenmc.commons.core.permission.Group;
import lombok.Getter;

@Getter
public enum Tag {

	DONO(22, "§4§lDONO §4", "§4§lDONO", "A", Group.DONO, "owner", "master", "ceo"),
	DIRETOR(21, "§4§lDIRETOR §4", "§4§lDIRETOR", "B", Group.DIRETOR, "diretor"),
	ADMIN(20, "§c§lADMIN §c", "§c§lADMIN", "C", Group.ADMIN, "administrador"),
	MODPLUS(19, "§5§lMOD+ §5", "§5§lMOD+", "G", Group.MODPLUS, "mod+"),
	MODGC(18, "§5§lMODGC §5", "§5§lMODGC", "H", Group.MODGC, "gc", "gcdetector"),
	MOD(17, "§5§lMOD §5", "§5§lMOD", "I", Group.MOD, "moderador"),
	YOUTUBERPLUS(16, "§3§lYT+ §3", "§3§lYT+", "J", Group.YOUTUBERPLUS, "yt+", "ytplus", "youtuber+"),
	TRIAL(15, "§5§lTRIAL §5", "§5§lTRIAL", "K", Group.TRIAL),
	INVESTIDORPLUS(14, "§2§lINVST+ §2", "§2§lINVESTIDOR+", "L", Group.INVESTIDORPLUS, "investidor+"),
	INVESTIDOR(13, "§2§lINVST §2", "§2§lINVESTIDOR", "L", Group.INVESTIDOR),
	BUILDER(11, "§e§lBUILDER §e", "§e§lBUILDER", "M", Group.BUILDER, "construtor"),
	STAFF(10, "§e§lSTAFF §e", "§e§lSTAFF", "N", Group.STAFF, "staffer"),
	DESIGNER(9, "§2§lDESIGNER §2", "§2§lDESIGNER", "O", Group.DESIGNER, "dzn"),
	YOUTUBER(8, "§b§lYT §b", "§b§lYT", "P", Group.YOUTUBER, "yt"),
	BETA(7, "§1§lBETA §1", "§1§lBETA", "R", Group.BETA),
	ULTRA(6, "§d§lULTRA §d", "§d§lULTRA", "Q", Group.ULTRA),
	BLADE(5, "§e§lBLADE §e", "§e§lBLADE", "S", Group.BLADE),
	PRO(4, "§6§lPRO §6", "§6§lPRO", "T", Group.PRO),
	COPA(4, "§e§lCOPA §e", "§6§lCOPA", "W", Group.PRO),
	VIP(3, "§a§lVIP §a", "§a§lVIP", "X", Group.VIP),
	WINNER(2, "§2§lWINNER §2", "§2§lWINNER", "Y", Group.WINNER, "vencedor"),
	MEMBRO(1, "§7", "§7§lMEMBRO", "Z", Group.MEMBRO, "normal");

	private int id;
	private String prefix;
	private String name, team;
	private List<String> aliases;

	private Tag(int id, String prefix, String name, String team, Group groupToUse, String... aliases) {
		this.id = id;
		this.prefix = prefix;
		this.name = name;
		this.team = team;
		this.aliases = Arrays.asList(aliases);
	}

	public Group getGroupToUse() {
		return Group.valueOf(name());
	}

	public static Tag fromString(String name) {
		for (Tag tag : values()) {
			if (!name.equalsIgnoreCase(tag.name())) {
				for (String alias : tag.getAliases()) {
					if (!alias.equalsIgnoreCase(name))
						continue;
					return tag;
				}
			} else {
				return tag;
			}
		}
		return null;
	}

	public static Tag fromId(int id) {
		for (Tag tag : values()) {
			if (tag.getId() != id)
				continue;
			return tag;
		}
		return null;
	}
}
