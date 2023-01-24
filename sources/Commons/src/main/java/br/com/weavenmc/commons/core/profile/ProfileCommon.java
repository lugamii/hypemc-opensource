package br.com.weavenmc.commons.core.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.base.Charsets;

import br.com.weavenmc.commons.WeavenMC;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;

public class ProfileCommon {

	private HashMap<String, Profile> cracked_profiles;
	@Getter
	private boolean profilesLoaded = true;

	public ProfileCommon() {
		cracked_profiles = new HashMap<>();
	}

	public void loadCrackedProfiles() {
		if (profilesLoaded)
			return;
		
		WeavenMC.debug("[Profile] Iniciando o carregamento de Todos os perfis piratas... Pode levar 1 século.");
		
		Connection myqlConnection = WeavenMC.getCommonMysql().getConnection();

		try {		
			PreparedStatement preparedStatement = myqlConnection
					.prepareStatement("CREATE TABLE IF NOT EXISTS `cracked_profiles` (`name` VARCHAR(17), `username` VARCHAR(17), `uniqueId` VARCHAR(50));");
			preparedStatement.execute();
			preparedStatement.close();
			
			preparedStatement = myqlConnection
					.prepareStatement("SELECT * FROM `cracked_profiles`");
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String name = resultSet.getString("name");
				String username = resultSet.getString("username");
				UUID uniqueId = UUID.fromString(resultSet.getString("uniqueId"));

				cracked_profiles.put(name, new Profile(username, uniqueId));
			}

			resultSet.close();
			preparedStatement.close();
			
			profilesLoaded = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			BungeeCord.getInstance().stop(ex.getMessage());
		}
	}

	public Profile createCrackedIfNotExists(String name) 
	{
		Profile profile = tryCached(name);
		
		if (profile == null) {
			profile = tryCracked(name);
		}
		
		if (profile == null) {
			return createCracked(name);
		}
		
		return profile;
	}
	
	public Profile createCracked(String username) 
	{
		UUID offlineId = UUID.nameUUIDFromBytes( ("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));

		try 
		{
			PreparedStatement preparedStatement = WeavenMC.getCommonMysql()
					.preparedStatement("INSERT INTO `cracked_profiles` (`name`, `username`, `uniqueId`) VALUES ('"
							+ username.toLowerCase() + "', '" + username + "', '" + offlineId.toString() + "');");
			preparedStatement.execute();
			preparedStatement.close();
			
			Profile profile = new Profile(username, offlineId);
			cracked_profiles.put(username.toLowerCase(), profile);
			
			return profile;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public Profile tryCached(UUID uuid) {

		for (Profile profile : getCrackedProfiles()) {

			if (!profile.getId().equals(uuid))
				continue;

			return profile;
		}
		return null;
	}

	public Profile tryCracked(UUID uuid) {
		Profile profile = null;

		try {
			PreparedStatement preparedStatement = WeavenMC.getCommonMysql()
					.preparedStatement("SELECT * FROM `cracked_profiles` WHERE `uniqueId`='" + uuid.toString() + "';");
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				profile = new Profile(resultSet.getString("username"),
						UUID.fromString(resultSet.getString("uniqueId")));
				cracked_profiles.put(profile.getName().toLowerCase(), profile);
			}

			resultSet.close();
			preparedStatement.close();

			return profile;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Profile tryCracked(String name) {
		Profile profile = null;

		try {
			PreparedStatement preparedStatement = WeavenMC.getCommonMysql()
					.preparedStatement("SELECT * FROM `cracked_profiles` WHERE `name`='" + name.toLowerCase() + "';");
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				profile = new Profile(resultSet.getString("username"),
						UUID.fromString(resultSet.getString("uniqueId")));
				cracked_profiles.put(profile.getName().toLowerCase(), profile);
			}

			resultSet.close();
			preparedStatement.close();

			return profile;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Profile tryCached(String name) {
		return cracked_profiles.get(name.toLowerCase());
	}

	public Profile tryPremium(UUID uuid) {
		String name = WeavenMC.getNameOf(uuid);

		if (name != null) {
			return new Profile(name, uuid);
		}

		return null;
	}

	public Profile tryPremium(String name) 
	{
		return tryPremium(name, true);
	}
	
	public Profile tryPremium(String name, boolean fetched) 
	{
		try {
			UUID uuid = WeavenMC.getUUIDOf(name);
			if (uuid == null)
				return null;
			String username = name;
			if (!fetched) 
				username = WeavenMC.getNameOf(uuid);			
			return new Profile(username, uuid);
		} catch (Exception e) {
			return null;
		}
	}

	public Profile getProfile(UUID uuid) 
	{
		Profile profile = tryPremium(uuid);

		if (profile == null) {
			profile = tryCached(uuid);
		}

		if (profile == null) {
			profile = tryCracked(uuid);
		}

		return profile;
	}

	public Profile getProfile(String name) 
	{
		Profile profile = tryPremium(name);

		if (profile == null) {
			profile = tryCached(name);
		}

		if (profile == null) {
			profile = tryCracked(name);
		}

		return profile;
	}

	public Collection<Profile> getCrackedProfiles() {
		return cracked_profiles.values();
	}
}
