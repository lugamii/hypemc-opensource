package br.com.weavenmc.commons.core.clan;

public enum ClanRank {

	INITIALS(0, 999, "§f"),
	INTERMEDIARYS(1000, 9999, "§e"),
	EXPERTS(10000, 14999, "§1"),
	LEGENDS(15000, 24999, "§c"),
	CHEATERS(25000, Integer.MAX_VALUE, "§4");
	
	private int minxp;
	private int maxxp;
	private String color;
	
	private ClanRank(int minxp, int maxxp, String color) {
		this.minxp = minxp;
		this.maxxp = maxxp;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public int getMinXp() {
		return minxp;
	}
	
	public int getMaxXp() {
		return maxxp;
	}
	
	public ClanRank getNextRank() {
		if (this == CHEATERS)
			return this;
		else
			return values()[ordinal() + 1];
	}
	
	public static ClanRank fromXp(int xp) {
		for (ClanRank clan : values()) {
			if (xp <= 0) {
				return INITIALS;
			} else if (xp >= clan.getMinXp() && xp <= clan.getMaxXp()) {
				return clan;
			}
		}
		return CHEATERS;
	}
}
