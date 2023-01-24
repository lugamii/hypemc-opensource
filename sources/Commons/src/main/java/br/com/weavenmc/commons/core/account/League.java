package br.com.weavenmc.commons.core.account;

public enum League {

	STAFF("§8", "o", "✘", "Staff", 0),
	UNRANKED("§f", "o", "-", "Unranked", 500),
	PRIMARY("§a", "n", "☰", "Primary", 1000),
	ADVANCED("§e", "m", "☲", "Advanced", 1500),
	EXPERT("§1", "l", "☷", "Expert", 2000),
	SILVER("§7", "k", "✶", "Silver", 2500),
	GOLD("§6", "j", "✳", "Gold", 3000),
	DIAMOND("§b", "i", "✦", "Diamond", 4000),
	EMERALD("§2", "h", "✥", "Emerald", 5000),
	CRYSTAL("§9", "g", "❉", "Crystal", 6500),
	EXTREME("§8", "f", "✵", "Extreme", 7500),
	ELITE("§5", "e", "✹", "Elite", 8500),
	MASTER("§c", "d", "✫", "Master", 10000),
	NINJA("§d", "c", "✬", "Ninja", 12000),
	LEGENDARY("§4", "b", "✪", "Legendary", 15000),
	ULTIMATO("§3", "a", "❂", "Ultimato", 20000);
	
	private String color;
	private String team;
	private String symbol;
	private String name;
	private int experience;
	
	private League(String color, String team, String symbol, String name, int experience) {
		this.color = color;
		this.team = team;
		this.symbol = symbol;
		this.name = name;
		this.experience = experience;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getTeam() {
		return team;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getName() {
		return name;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public League getPreviousLeague() {
		return this == UNRANKED ? UNRANKED : League.values()[ordinal() - 1];	
	}
	
	public League getNextLeague() {
		return this == ULTIMATO ? ULTIMATO : League.values()[ordinal() + 1];	
	}
	
	public static League fromLeagueAndExp(League now, int exp) {
		if (now != ULTIMATO && exp > now.getExperience()) {
			return now.getNextLeague();
		} else {
			return now;
		}
	}
}
