package br.com.weavenmc.commons.core.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.data.player.Data;
import br.com.weavenmc.commons.core.data.player.DataHandler;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.commons.util.GeoIpUtils.IpInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
public class WeavenPlayer {

	private String name;
	private UUID uniqueId;
	private Group group = Group.MEMBRO;
	private List<HashMap<Group, Long>> ranks = new ArrayList<>();
	private List<HashMap<String, Long>> perms = new ArrayList<>();
	private DataHandler dataHandler;
	private IpInfo ipInfo;
	@Setter
	private boolean tell = true, clan = true;

	public WeavenPlayer(UUID uniqueId, String name) {
		dataHandler = new DataHandler(this.uniqueId = uniqueId, this.name = name);
	}

	public Data getData(DataType dataType) {
		return dataHandler.getData(dataType);
	}

	public void loadPermissions() {
		ArrayList<String> permissions = getData(DataType.PLAYER_PERMISSIONS).asList();
		perms.clear();
		for (String permAndTime : permissions) {
			String[] array = permAndTime.split(";");
			Long time = Long.valueOf(array[1]);
			if (time != -1 && time < System.currentTimeMillis())
				continue;
			HashMap<String, Long> permMap = new HashMap<>();
			permMap.put(array[0], time);
			perms.add(permMap);
		}
		updatePermissionsData();
	}

	public List<String> getPermissions() {
		ArrayList<String> permissions = new ArrayList<String>();
		for (HashMap<String, Long> permMap : perms) {
			String permission = permMap.keySet().toArray(new String[] {})[0];
			if (!permissions.contains(permission.toLowerCase())) {
				permissions.add(permission.toLowerCase());
			}
		}
		return permissions;
	}

	public boolean hasPermission(String perm) {
		return getPermissions().contains(perm.toLowerCase());
	}

	public void addPermission(String perm, long time) {
		HashMap<String, Long> permMap = new HashMap<>();
		permMap.put(perm.toLowerCase(), time);
		perms.add(permMap);
		updatePermissionsData();
	}

	private void updatePermissionsData() {
		ArrayList<String> list = new ArrayList<>();
		for (HashMap<String, Long> permMap : perms) {
			String permission = permMap.keySet().toArray(new String[] {})[0];
			Long time = permMap.values().toArray(new Long[] {})[0];
			list.add(permission + ";" + time);
		}
		getData(DataType.PLAYER_PERMISSIONS).setValue(list);
	}

	public void organizeGroups(List<String> playerRanks) {
		ranks.clear();
		group = Group.MEMBRO;
		for (int i = 0; i < playerRanks.size(); i++) {
			String rankAndTime = playerRanks.get(i);
			HashMap<Group, Long> map = new HashMap<>();
			Group serverGroup = Group.fromString(rankAndTime.split(";")[0]);
			if (serverGroup != null) {
				Long groupTime = Long.valueOf(rankAndTime.split(";")[1]);
				map.put(serverGroup, groupTime);
				if (serverGroup.getId() >= group.getId())
					group = serverGroup;
				ranks.add(map);
				continue;
			} else {
				playerRanks.remove(i);
				map = null;
				continue;
			}
		}
		getData(DataType.GROUPS).setValue(playerRanks);
	}

	public List<Integer> getExpiredGroupsIds() {
		List<Integer> IDs = new ArrayList<>();
		List<String> playerRanks = getData(DataType.GROUPS).asList();
		for (int i = 0; i < playerRanks.size(); i++) {
			Long groupTime = Long.valueOf(playerRanks.get(i).split(";")[1]);
			if (groupTime != -1 && groupTime < System.currentTimeMillis()) {
				IDs.add(i);
			}
		}
		if (IDs.size() == 0)
			IDs = null;
		return IDs;
	}

	public void organizeGroups() {
		ranks.clear();
		group = Group.MEMBRO;
		List<String> playerRanks = new ArrayList<>(getData(DataType.GROUPS).asList());
		for (int i = 0; i < playerRanks.size(); i++) {
			String rankAndTime = playerRanks.get(i);
			HashMap<Group, Long> map = new HashMap<>();
			Group serverGroup = Group.fromString(rankAndTime.split(";")[0]);
			if (serverGroup != null) {
				Long groupTime = Long.valueOf(rankAndTime.split(";")[1]);
				map.put(serverGroup, groupTime);
				if (serverGroup.getId() >= group.getId())
					group = serverGroup;
				ranks.add(map);
				continue;
			} else {
				playerRanks.remove(i);
				map = null;
				continue;
			}
		}
		getData(DataType.GROUPS).setValue(playerRanks);
	}

	public void addGroup(Group serverGroup, Long groupTime) {
		List<String> playerRanks = new ArrayList<>();
		for (HashMap<Group, Long> map : ranks) {
			for (Entry<Group, Long> entrie : map.entrySet()) {
				playerRanks.add(entrie.getKey().name() + ";" + entrie.getValue());
			}
		}
		playerRanks.add(serverGroup.name() + ";" + groupTime);
		getData(DataType.GROUPS).setValue(playerRanks);
		organizeGroups(playerRanks);
	}

	public void removeGroup(String rankIndex) {
		List<String> playerRanks = getData(DataType.GROUPS).asList();
		playerRanks.remove(rankIndex);
		if (playerRanks.size() <= 0) {
			playerRanks.add(Group.MEMBRO.name() + ";-1");
		}
		getData(DataType.GROUPS).setValue(playerRanks);
	}

	public void removeGroup(Group group) {
		List<String> playerRanks = getData(DataType.GROUPS).asList();
		for (String rankAndTime : playerRanks) {
			Group serverGroup = Group.valueOf(rankAndTime.split(";")[0]);
			if (group == serverGroup) {
				playerRanks.remove(rankAndTime);
			}
		}
		if (playerRanks.size() <= 0) {
			playerRanks.add(Group.MEMBRO.toString() + ";-1");
		}
		getData(DataType.GROUPS).setValue(playerRanks);
		organizeGroups();
	}

	public HashMap<Group, Long> getGroupById(int id) {
		HashMap<Group, Long> map = new HashMap<>();
		List<String> playerRanks = getData(DataType.GROUPS).asList();
		if (id < 0 || id >= playerRanks.size())
			return null;
		String rankAndTime = playerRanks.get(id);
		try {
			map.put(Group.valueOf(rankAndTime.split(";")[0]), Long.valueOf(rankAndTime.split(";")[1]));
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	public void removeGroup(int id) {
		List<String> playerRanks = getData(DataType.GROUPS).asList();
		if (id < 0 || id >= playerRanks.size())
			return;
		playerRanks.remove(id);
		if (playerRanks.size() <= 0) {
			playerRanks.add(Group.MEMBRO.toString() + ";-1");
		}
		getData(DataType.GROUPS).setValue(playerRanks);
		organizeGroups();
	}

	public boolean isCategoryLoaded(DataCategory category) {
		return dataHandler.isCategoryLoaded(category);
	}

	public League getLeague() {
		return League.valueOf(getData(DataType.CURRENT_LEAGUE).asString());
	}

	public void setServerConnected(String id) {
		getData(DataType.SERVER_CONNECTED).setValue(id);
	}

	public void setServerConnectedType(ServerType type) {
		getData(DataType.SERVER_CONNECTED_TYPE).setValue(type.name());
	}

	public boolean isDoubleXPActived() {
		long last = getLastActivatedMultiplier();
		return last != -1 && last >= System.currentTimeMillis();
	}

	public String getClanName() {
		return getData(DataType.CLAN_NAME).asString();
	}

	public void setClanName(String clanName) {
		getData(DataType.CLAN_NAME).setValue(clanName);
	}

	public void setIpInfo(IpInfo ipInfo) {
		this.ipInfo = ipInfo;
	}

	public String getServerConnected() {
		return getData(DataType.SERVER_CONNECTED).asString();
	}

	public ServerType getServerConnectedType() {
		return ServerType.valueOf(getData(DataType.SERVER_CONNECTED_TYPE).asString());
	}

	public void setCity(String city) {
		getData(DataType.CITY).setValue(city);
	}

	public void setCountry(String country) {
		getData(DataType.COUNTRY).setValue(country);
	}

	public boolean load(DataCategory... categories) {
		return dataHandler.load(categories);
	}

	public void save(DataCategory... categories) {
		dataHandler.save(categories);
	}

	public boolean hasGroupPermission(Group group) {
		if (group.isExclusive()) {
			return getGroups().contains(group) || this.group.getId() >= Group.DIRETOR.getId();
		}

		return this.group.getId() >= group.getId();
	}

	public List<Group> getGroups() {
		List<Group> groups = new ArrayList<>();
		for (HashMap<Group, Long> map : ranks) {
			Group group = map.keySet().toArray(new Group[] {})[0];
			if (groups.contains(group))
				continue;
			groups.add(group);
		}
		return groups;
	}

	public int getMoney() {
		return getData(DataType.MONEY).asInt();
	}

	public int getTotalXp() {
		return getData(DataType.TOTAL_XP).asInt();
	}

	public int getXp() {
		return getData(DataType.XP).asInt();
	}

	public int getCash() {
		return getData(DataType.CASH).asInt();
	}

	public int getTickets() {
		return getData(DataType.TICKETS).asInt();
	}

	public int getDoubleXpMultiplier() {
		return getData(DataType.DOUBLEXPMULTIPLIER).asInt();
	}

	public long getLastActivatedMultiplier() {
		return getData(DataType.LASTACTIVATEDMULTIPLIER).asLong();
	}

	public boolean isMultiplierInUse() {
		long last = getLastActivatedMultiplier();
		return last != -1 && last >= System.currentTimeMillis();
	}

	public void setComputerAddress(String address) {
		getData(DataType.COMPUTER_ADDRESS).setValue(address);
	}

	public void setLastComputerAddress(String address) {
		getData(DataType.LAST_COMPUTER_ADDRES).setValue(address);
	}

	public void setIpAddress(String address) {
		getData(DataType.IP_ADDRESS).setValue(address);
	}

	public void setLastIpAddress(String address) {
		getData(DataType.LAST_IP_ADDRESS).setValue(address);
	}

	public void setOnlineTime(long onlineTime) {
		getData(DataType.ONLINE_TIME).setValue(onlineTime);
	}

	public void setJoinTime(long joinTime) {
		getData(DataType.JOIN_TIME).setValue(joinTime);
	}

	public void setFirstLoggedIn(long logged) {
		getData(DataType.FIRST_LOGGED_IN).setValue(logged);
	}

	public void setLastLoggedIn(long logged) {
		getData(DataType.LAST_LOGGED_IN).setValue(logged);
	}

	public int getSilverCrates() {
		return getData(DataType.SILVER_CRATES).asInt();
	}

	public int getGoldCrates() {
		return getData(DataType.GOLD_CRATES).asInt();
	}

	public int getDiamondCrates() {
		return getData(DataType.DIAMOND_CRATES).asInt();
	}

	public void addSilverCrates(int i) {
		addInt(i, DataType.SILVER_CRATES);
	}

	public void addGoldCrates(int i) {
		addInt(i, DataType.GOLD_CRATES);
	}

	public void addDiamondCrates(int i) {
		addInt(i, DataType.DIAMOND_CRATES);
	}

	public void removeSilverCrates(int i) {
		removeInt(i, DataType.SILVER_CRATES);
	}

	public void removeGoldCrates(int i) {
		removeInt(i, DataType.GOLD_CRATES);
	}

	public void removeDiamondCrates(int i) {
		removeInt(i, DataType.DIAMOND_CRATES);
	}

	public void addCash(int i) {
		addInt(i, DataType.CASH);
	}

	public void removeCash(int i) {
		removeInt(i, DataType.CASH);
	}

	public void addTickets(int i) {
		addInt(i, DataType.TICKETS);
	}

	public void removeTickets(int i) {
		removeInt(i, DataType.TICKETS);
	}

	public void addDoubleXpMultiplier(int i) {
		addInt(i, DataType.DOUBLEXPMULTIPLIER);
	}

	public void removeDoubleXpMultiplier(int i) {
		removeInt(i, DataType.DOUBLEXPMULTIPLIER);
	}

	public void addXp(int i) {
		addInt(i, DataType.TOTAL_XP);
		addInt(i, DataType.XP);
	}

	public void removeXp(int i) {
		removeInt(i, DataType.TOTAL_XP);
		removeInt(i, DataType.XP);
	}

	public void addMoney(int i) {
		addInt(i, DataType.MONEY);
	}

	public void removeMoney(int i) {
		removeInt(i, DataType.MONEY);
	}

	private void setInt(int a, DataType dataType) {
		if (a < 0)
			a = 0;
		getData(dataType).setValue(a);
	}

	private void removeInt(int a, DataType dataType) {
		int b = getData(dataType).asInt();
		b -= a;
		if (b < 0)
			b = 0;
		setInt(b, dataType);
	}

	private void addInt(int a, DataType dataType) {
		int b = getData(dataType).asInt();
		b += a;
		setInt(b, dataType);
	}

	public String getSkype() {
		return getData(DataType.SKYPE).asString();
	}

	public void setSkype(String a) {
		getData(DataType.SKYPE).setValue(a);
	}

	public String getTwitter() {
		return getData(DataType.TWITTER).asString();
	}

	public void setTwitter(String a) {
		getData(DataType.TWITTER).setValue(a);
	}

	public String getDiscord() {
		return getData(DataType.DISCORD).asString();
	}

	public void setDiscord(String a) {
		getData(DataType.DISCORD).setValue(a);
	}

	public String getYoutubeChannel() {
		return getData(DataType.YOUTUBE_CHANNEL).asString();
	}

	public void setYoutubeChannel(String a) {
		getData(DataType.YOUTUBE_CHANNEL).setValue(a);
	}

	public String getSteam() {
		return getData(DataType.STEAM).asString();
	}

	public void setSteam(String a) {
		getData(DataType.STEAM).setValue(a);
	}

	public String getTwitch() {
		return getData(DataType.TWITCH).asString();
	}

	public void setTwitch(String a) {
		getData(DataType.TWITCH).setValue(a);
	}

	public boolean isStaffer() {
		return group.getId() >= Group.STAFF.getId();
	}

	public long getOnlineTime() {
		return getData(DataType.ONLINE_TIME).asLong();
	}

	public long getJoinTime() {
		return getData(DataType.JOIN_TIME).asLong();
	}

	public long getFirstLoggedIn() {
		return getData(DataType.FIRST_LOGGED_IN).asLong();
	}

	public long getLastLoggedIn() {
		return getData(DataType.LAST_LOGGED_IN).asLong();
	}

	public String getIpAddress() {
		return getData(DataType.IP_ADDRESS).asString();
	}

	public String getLastIpAddress() {
		return getData(DataType.LAST_IP_ADDRESS).asString();
	}

	public String getComputerAddress() {
		return getData(DataType.COMPUTER_ADDRESS).asString();
	}

	public String getLastComputerAddress() {
		return getData(DataType.LAST_COMPUTER_ADDRES).asString();
	}

	public boolean isScreensharing() {
		return getData(DataType.IN_SCREENSHARE).asBoolean();
	}

	public void setScreensharing(boolean screensharing) {
		getData(DataType.IN_SCREENSHARE).setValue(screensharing);
	}

	public static WeavenPlayer getPlayer(UUID uuid) {
		return WeavenMC.getAccountCommon().getWeavenPlayer(uuid);
	}
}
