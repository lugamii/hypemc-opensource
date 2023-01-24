package br.com.weavenmc.skywars.nametag;

public class Tag {
	
	private String prefix,suffix,teamString;
	
	public Tag(String prefix, String suffix,String teamString) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamString = teamString;
	}
	
	public String getTeamString() {
		return teamString;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
