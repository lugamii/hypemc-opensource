package br.com.weavenmc.commons.core.clan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.profile.Profile;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;

@Getter
public class ClanCommon {

	private HashMap<String, Clan> clans;
	private boolean clansLoaded = true, clansLoading = false, clansLoadFailed = false;

	public ClanCommon() {
		clans = new HashMap<>();
	}

	private PreparedStatement sql(String sql) throws SQLException {
		return WeavenMC.getCommonMysql().preparedStatement(sql);
	}

	public void createTableIfNotExists() throws SQLException {
		PreparedStatement s = sql(
				"CREATE TABLE IF NOT EXISTS `clans` (`name` VARCHAR(30), `name_lower` VARCHAR(30), `abbreviation` VARCHAR(16), `xp` INT(100), `abbreviation_lower` VARCHAR(16), `owner` VARCHAR(50), `owner_name` VARCHAR(17), `administrators` VARCHAR(8000), `participants` VARCHAR(8000));");
		s.execute();
		s.close();
	}

	public void loadClans() {
		if (clansLoaded || clansLoading)
			return;

		clansLoading = true;
		WeavenMC.debug("[CLANS LOADER] Iniciando o carregamento de todos os Clans.. Pode levar uma eternidade.");

		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			try {
				PreparedStatement s = sql("SELECT * FROM `clans`");
				ResultSet result = s.executeQuery();

				while (result.next()) {
					String name = result.getString("name");
					String abbreviation = result.getString("abbreviation");
					int xp = result.getInt("xp");
					UUID owner = UUID.fromString(result.getString("owner"));
					String ownerName = result.getString("owner_name");

					ArrayList<String> list = fromStringToList(result.getString("administrators"));
					List<UUID> uuids = new ArrayList<>();

					int inx = 0;
					int i = list.size();

					while (inx < i) {
						String uuid = list.get(inx);

						try {
							UUID next = UUID.fromString(uuid);
							uuids.add(next);
						} catch (Exception ex) {
							WeavenMC.debug("[CLANS] O UUID '" + uuid + "' para o Clan '" + name
									+ "' é invalido! Ignorando...");
						}

						inx++;
					}

					inx = 0;
					i = uuids.size();

					HashMap<UUID, String> adminMap = new HashMap<>();

					while (inx < i) {
						UUID next = uuids.get(inx);
						Profile profile = WeavenMC.getProfileCommon().getProfile(next);

						if (profile != null) {
							adminMap.put(profile.getId(), profile.getName());
						}

						inx++;
					}

					list.clear();
					uuids.clear();

					list = fromStringToList(result.getString("participants"));

					inx = 0;
					i = list.size();

					while (inx < i) {
						String uuid = list.get(inx);

						try {
							UUID next = UUID.fromString(uuid);
							uuids.add(next);
						} catch (Exception ex) {
							WeavenMC.debug("[CLANS] O UUID '" + uuid + "' para o Clan '" + name
									+ "' é invalido! Ignorando...");
						}

						inx++;
					}

					inx = 0;
					i = uuids.size();

					HashMap<UUID, String> memberMap = new HashMap<>();

					while (inx < i) {
						UUID next = uuids.get(inx);
						Profile profile = WeavenMC.getProfileCommon().getProfile(next);

						if (profile != null) {
							memberMap.put(profile.getId(), profile.getName());
						}

						inx++;
					}

					Clan clan = new Clan(name, abbreviation, xp, owner, adminMap, memberMap);
					clan.setOwnerName(ownerName);
					
					clans.put(clan.getName().toLowerCase(), clan);

					WeavenMC.debug(
							"[CLANS] Carregado o Clan -> " + clan.getName() + "(" + clan.getAbbreviation() + ")");
				}

				result.close();
				s.close();

				WeavenMC.debug("[CLANS] O carregamento dos Clans foi terminado! Foram carregados '" + clans.size()
						+ "' Clans.");
			} catch (SQLException ex) {
				ex.printStackTrace();
				BungeeCord.getInstance().stop(ex.getMessage());
				return;
			}
			clansLoaded = true;
			clansLoading = false;
		});
	}
	
	public void loadClan(Clan clan) {
		clans.put(clan.getName().toLowerCase(), clan);
	}

	public boolean createClan(UUID owner, Clan clan) 
	{
		try 
		{
			PreparedStatement s = sql(
					"INSERT INTO `clans` (`name`, `name_lower`, `abbreviation`, `abbreviation_lower`, `owner`, `owner_name`, `administrators`, `participants`) VALUES ('" + clan.getName() + "', '" + clan.getName().toLowerCase() + "', '" + clan.getAbbreviation() + "', '" + clan.getAbbreviation().toLowerCase() + "', '" + owner.toString() + "', '" + clan.getOwnerName() + "', '[]', '[]');");
			s.execute();
			s.close();
			clans.put(clan.getName(), clan);
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}

		// send Create Clan Redis Field
	}

	public void updateClan(Clan clan) {
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			try {
				PreparedStatement s = sql("UPDATE `clans` SET `abbreviation_lower`='"
						+ clan.getAbbreviation().toLowerCase() + "', `abbreviation`='" + clan.getAbbreviation()
						+ "', `xp`='" + clan.getXp() + "', `owner`='" + clan.getOwner() + "', `owner_name`='" + clan.getOwnerName() + "', `administrators`='"
						+ clan.getAdminsUuidList().toString() + "', `participants`='"
						+ clan.getMembersUuidList().toString() + "' WHERE `name_lower`='" + clan.getName().toLowerCase()
						+ "';");
				s.execute();
				s.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		// send Update Clan Redis Field
	}
	
	public void unloadClan(String clanName) {
		clans.remove(clanName.toLowerCase());
	}

	public Clan getClanFromName(String clanName) {
		Clan clan = tryInCacheFromName(clanName);

		if (clan == null) {
			clan = tryInDatabaseFromName(clanName);
		}

		return clan;
	}

	public Clan getClanFromAbbreviation(String clanAbbreviation) {
		Clan clan = tryInCacheFromAbbreviation(clanAbbreviation);

		if (clan == null) {
			clan = tryInDatabaseFromAbbreviation(clanAbbreviation);
		}

		return clan;
	}

	public Clan tryInCacheFromName(String clanName) {
		return clans.get(clanName.toLowerCase());
	}

	public Clan tryInCacheFromAbbreviation(String clanAbbreviation) {
		for (Clan clan : getClans()) {
			if (!clan.getAbbreviation().equalsIgnoreCase(clanAbbreviation))
				continue;

			return clan;
		}

		return null;
	}

	public Clan tryInDatabaseFromAbbreviation(String clanAbbreviation) {
		try {
			PreparedStatement s = sql(
					"SELECT * FROM `clans` WHERE `abbreviation_lower`='" + clanAbbreviation.toLowerCase() + "';");
			ResultSet result = s.executeQuery();

			Clan clan = null;

			if (result.next()) {
				String name = result.getString("name");
				String abbreviation = result.getString("abbreviation");
				int xp = result.getInt("xp");
				UUID owner = UUID.fromString(result.getString("owner"));
				String ownerName = result.getString("owner_name");

				ArrayList<String> list = fromStringToList(result.getString("administrators"));
				List<UUID> uuids = new ArrayList<>();

				int inx = 0;
				int i = list.size();

				while (inx < i) {
					String uuid = list.get(inx);

					try {
						UUID next = UUID.fromString(uuid);
						uuids.add(next);
					} catch (Exception ex) {
					}

					inx++;
				}

				inx = 0;
				i = uuids.size();

				HashMap<UUID, String> adminMap = new HashMap<>();

				while (inx < i) {
					UUID next = uuids.get(inx);
					Profile profile = WeavenMC.getProfileCommon().getProfile(next);

					if (profile != null) {
						adminMap.put(profile.getId(), profile.getName());
					}

					inx++;
				}

				list.clear();
				uuids.clear();

				list = fromStringToList(result.getString("participants"));

				inx = 0;
				i = list.size();

				while (inx < i) {
					String uuid = list.get(inx);

					try {
						UUID next = UUID.fromString(uuid);
						uuids.add(next);
					} catch (Exception ex) {
					}

					inx++;
				}

				inx = 0;
				i = uuids.size();

				HashMap<UUID, String> memberMap = new HashMap<>();

				while (inx < i) {
					UUID next = uuids.get(inx);
					Profile profile = WeavenMC.getProfileCommon().getProfile(next);

					if (profile != null) {
						memberMap.put(profile.getId(), profile.getName());
					}

					inx++;
				}

				clan = new Clan(name, abbreviation, xp, owner, adminMap, memberMap);
				clan.setOwnerName(ownerName);
				
				clans.put(clan.getName(), clan);
			}

			result.close();
			s.close();

			return clan;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Clan tryInDatabaseFromName(String clanName) {
		try {
			PreparedStatement s = sql("SELECT * FROM `clans` WHERE `name_lower`='" + clanName.toLowerCase() + "';");
			ResultSet result = s.executeQuery();

			Clan clan = null;

			if (result.next()) {
				String name = result.getString("name");
				String abbreviation = result.getString("abbreviation");
				int xp = result.getInt("xp");
				UUID owner = UUID.fromString(result.getString("owner"));
				String ownerName = result.getString("owner_name");

				ArrayList<String> list = fromStringToList(result.getString("administrators"));
				List<UUID> uuids = new ArrayList<>();

				int inx = 0;
				int i = list.size();

				while (inx < i) {
					String uuid = list.get(inx);

					try {
						UUID next = UUID.fromString(uuid);
						uuids.add(next);
					} catch (Exception ex) {
					}

					inx++;
				}

				inx = 0;
				i = uuids.size();

				HashMap<UUID, String> adminMap = new HashMap<>();

				while (inx < i) {
					UUID next = uuids.get(inx);
					Profile profile = WeavenMC.getProfileCommon().getProfile(next);

					if (profile != null) {
						adminMap.put(profile.getId(), profile.getName());
					}

					inx++;
				}

				list.clear();
				uuids.clear();

				list = fromStringToList(result.getString("participants"));

				inx = 0;
				i = list.size();

				while (inx < i) {
					String uuid = list.get(inx);

					try {
						UUID next = UUID.fromString(uuid);
						uuids.add(next);
					} catch (Exception ex) {
					}

					inx++;
				}

				inx = 0;
				i = uuids.size();

				HashMap<UUID, String> memberMap = new HashMap<>();

				while (inx < i) {
					UUID next = uuids.get(inx);
					Profile profile = WeavenMC.getProfileCommon().getProfile(next);

					if (profile != null) {
						memberMap.put(profile.getId(), profile.getName());
					}

					inx++;
				}

				clan = new Clan(name, abbreviation, xp, owner, adminMap, memberMap);
				clan.setOwnerName(ownerName);
				
				clans.put(clan.getName(), clan);
			}

			result.close();
			s.close();

			return clan;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public boolean deletClan(Clan clan) {
		try {
			PreparedStatement s = sql("DELETE FROM `clans` WHERE `name_lower`='" + clan.getName().toLowerCase() + "';");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public Collection<Clan> getClans() {
		return clans.values();
	}

	private ArrayList<String> fromStringToList(String str) {
		ArrayList<String> list = new ArrayList<>();
		String[] stringArray = str.replace("[", "").replace("]", "").trim().split(",");
		for (int i = 0; i < stringArray.length; i++) {
			String value = stringArray[i];
			if (value.equals("") || value.trim().equals(""))
				continue;
			list.add(value.trim());
		}
		stringArray = null;
		return list;
	}
}
