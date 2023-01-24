package br.com.weavenmc.commons.core.clan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import br.com.weavenmc.commons.core.account.WeavenPlayer;
import lombok.Getter;

@Getter
public class Clan {

	private String name;
	private String abbreviation;
	private int xp;
	private UUID owner;
	private String ownerName;
	private HashMap<UUID, String> administrators;
	private HashMap<UUID, String> participants;

	public Clan(String name, String abbreviation, int xp, UUID owner, HashMap<UUID, String> admins,
			HashMap<UUID, String> members) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.xp = xp;
		this.owner = owner;
		this.administrators = admins;
		this.participants = members;
	}

	public void setOwnerName(String name) {
		this.ownerName = name;
	}
	
	public int getMaxSlot() {
		return 20;
	}
	
	public int getActualSlot() {
		return getAdminsSize() + getMembersSize() + 1;
	}
	
	public int getAdminsSize() {
		return administrators.keySet().size();
	}
	
	public int getMembersSize() {
		return participants.keySet().size();
	}

	public boolean isFull() {
		int admins = administrators.keySet().size();
		int members = participants.keySet().size();
		return admins + members + 1 >= getMaxSlot();
	}

	public boolean isOwner(UUID uuid) {
		return owner.equals(uuid);
	}

	public boolean isAdministrator(UUID uuid) {
		return administrators.keySet().contains(uuid);
	}

	public boolean isParticipant(UUID uuid) {
		return participants.keySet().contains(uuid);
	}

	public void addAdministrator(WeavenPlayer wP) {
		administrators.put(wP.getUniqueId(), wP.getName());
	}

	public void addParticipant(WeavenPlayer wP) {
		participants.put(wP.getUniqueId(), wP.getName());
	}

	public void removeAdministrator(UUID uuid) {
		administrators.remove(uuid);
	}

	public void removeParticipant(UUID uuid) {
		participants.remove(uuid);
	}
	
	public List<String> getAdminsNamesList() {
		return Arrays.asList(administrators.values().toArray(new String[] {}));
	}
	
	public List<String> getMembersNamesList() {
		return Arrays.asList(participants.values().toArray(new String[] {}));
	}
	
	public List<UUID> getAdminsUuidList() {
		return Arrays.asList(administrators.keySet().toArray(new UUID[] {}));
	}
	
	public void joinParticipant(UUID uuid, String name) {
		participants.put(uuid, name);
	}
	
	public void setAbbreviation(String tag) {
		this.abbreviation = tag;
	}
	
	public void kickMember(UUID uuid) {
		if (isAdministrator(uuid)) {
			administrators.remove(uuid);
		}
		
		if (isParticipant(uuid)) {
			participants.remove(uuid);
		}
	}
	
	public List<UUID> getMembersUuidList() {
		return Arrays.asList(participants.keySet().toArray(new UUID[] {}));
	}
	
	public boolean isMemberOfClan(UUID uuid) {
		return isOwner(uuid) || isAdministrator(uuid) || isParticipant(uuid);
	}

	public void addXp(int d) {
		xp += d;
	}

	public void removeXp(int d) {
		xp -= d;
		if (xp < 0) {
			setXp(0);
		}
	}

	private void setXp(int d) {
		xp = d;
	}
}
