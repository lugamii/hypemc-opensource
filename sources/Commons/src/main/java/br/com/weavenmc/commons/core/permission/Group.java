package br.com.weavenmc.commons.core.permission;

import br.com.weavenmc.commons.core.account.Tag;
import lombok.Getter;

@Getter
public enum Group {
	
	DONO(21, Tag.DONO),
	DIRETOR(20, Tag.DIRETOR),
	ADMIN(19, Tag.ADMIN),
	MODPLUS(18, Tag.MODPLUS),
	MODGC(17, Tag.MODGC),
	MOD(16, Tag.MOD),
	YOUTUBERPLUS(15, Tag.YOUTUBERPLUS, true),
	TRIAL(14, Tag.TRIAL),
	INVESTIDORPLUS(13, Tag.INVESTIDORPLUS),
	INVESTIDOR(12, Tag.INVESTIDOR),
	BUILDER(11, Tag.BUILDER, true),
	STAFF(10, Tag.STAFF),
	DESIGNER(9, Tag.DESIGNER, true),
	YOUTUBER(8, Tag.YOUTUBER, true),
	BETA(7, Tag.BETA),
	ULTRA(6, Tag.ULTRA),
	BLADE(5, Tag.BLADE),
	PRO(4, Tag.PRO),
	COPA(3, Tag.COPA),
	VIP(3, Tag.VIP),
	WINNER(2, Tag.WINNER, true),
	MEMBRO(1, Tag.MEMBRO);
	
	private int id;
	private boolean exclusive;
	
	private Group(int id, Tag tagToUse) {
		this(id, tagToUse, false);
	}
	
	private Group(int id, Tag tagToUse, boolean exclusive) {
		this.id = id;
		this.exclusive = exclusive;
	}
	
	public Tag getTagToUse() {
		return Tag.valueOf(name());
	}
	
	public static Group fromId(int id) {
		for (Group group : values()) {
			if (group.getId() != id)
				continue;
			return group;
		}
		return null;
	}
	
	public static Group fromString(String name) {
		Tag tag = Tag.fromString(name);
		if (tag != null)
			return tag.getGroupToUse();
		return null;
	}
}
