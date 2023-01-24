package br.com.weavenmc.commons.core.data.punish;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import br.com.weavenmc.commons.WeavenMC;
import lombok.Getter;

@Getter
public class PunishHistory {

	public PunishHistory() throws SQLException {
		PreparedStatement a = sql(
				"CREATE TABLE IF NOT EXISTS `accountBans` (`id` VARCHAR(50), `author` VARCHAR(17), `reason` VARCHAR(8000), `appliedTime` VARCHAR(100), `duration` VARCHAR(100));");
		a.execute();
		a.close();

		PreparedStatement b = sql(
				"CREATE TABLE IF NOT EXISTS `macBans` (`macAddress` VARCHAR(50), `author` VARCHAR(17), `reason` VARCHAR(8000), `appliedTime` VARCHAR(100));");
		b.execute();
		b.close();

		PreparedStatement c = sql(
				"CREATE TABLE IF NOT EXISTS `ipBans` (`ipAddress` VARCHAR(50), `author` VARCHAR(17), `reason` VARCHAR(8000), `appliedTime` VARCHAR(100));");
		c.execute();
		c.close();
		
		PreparedStatement d = sql(
				"CREATE TABLE IF NOT EXISTS `mutes` (`id` VARCHAR(50), `author` VARCHAR(16), `time` VARCHAR(100), `reason` VARCHAR(200), `date` VARCHAR(30))");
		d.executeUpdate();
		d.close();
	}
	
	public AccountBan getAccountBan(UUID uuid) 
	{
		AccountBan ban = null;
		try 
		{
			PreparedStatement accSelect = sql("SELECT * FROM `accountBans` WHERE `id`='" + uuid.toString() + "';");
			ResultSet accQuery = accSelect.executeQuery();

			if (accQuery.next()) 
			{
				String author = accQuery.getString("author");
				String reason = accQuery.getString("reason");
				Long appliedTime = accQuery.getLong("appliedTime");
				Long duration = accQuery.getLong("duration");
				ban = new AccountBan(uuid, author, reason, appliedTime, duration);
			}
			
			accQuery.close();
			accSelect.close();
			
			return ban;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public Mute getMute(UUID id) {
		Mute mute = null;
		try {
			PreparedStatement statement = sql("SELECT * FROM `mutes` WHERE `id`='" + id.toString() + "';");
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				mute = new Mute(id, result.getString("author"), result.getLong("time"), result.getString("reason"),
						result.getLong("date"));
			}
			result.close();
			statement.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return mute;
	}
	
	public boolean mute(Mute mute) {
		try {
			pardonMute(mute.getAccountId());
			PreparedStatement statement = sql(
					"INSERT INTO `mutes` (`id`, `author`, `time`, `reason`, `date`) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, mute.getAccountId().toString());
			statement.setString(2, mute.getMutedBy());
			statement.setLong(3, mute.getMuteTime());
			statement.setString(4, mute.getReason());
			statement.setLong(5, mute.getAppliedTime());
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	public boolean pardonMute(UUID uuid) {
		try {
			PreparedStatement statement = sql("DELETE FROM `mutes` WHERE `id`='" + uuid.toString() + "';");
			statement.executeUpdate();
			statement.close();
			return true;
		} catch (SQLException exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	public MacBan getMacBan(String address) 
	{
		MacBan ban = null;	
		try 
		{
			PreparedStatement macSelect = sql("SELECT * FROM `macBans` WHERE `macAddress`='" + address + "';");
			ResultSet macQuery = macSelect.executeQuery();

			if (macQuery.next()) 
			{
				String macAddress = macQuery.getString("macAddress");
				String author = macQuery.getString("author");
				String reason = macQuery.getString("reason");
				Long appliedTime = macQuery.getLong("appliedTime");

				ban = new MacBan(macAddress, author, reason, appliedTime);
			}
			
			macQuery.close();
			macSelect.close();
			
			return ban;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public IpBan getIpBan(String address)
	{
		IpBan ban = null;	
		try {
			PreparedStatement ipSelect = sql("SELECT * FROM `ipBans` WHERE `ipAddress`='" + address + "';");
			ResultSet ipQuery = ipSelect.executeQuery();

			if (ipQuery.next()) 
			{
				String ipAddress = ipQuery.getString("ipAddress");
				String author = ipQuery.getString("author");
				String reason = ipQuery.getString("reason");
				Long appliedTime = ipQuery.getLong("appliedTime");

				ban = new IpBan(ipAddress, author, reason, appliedTime);
			}
			
			ipQuery.close();
			ipSelect.close();
			
			return ban;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean banAccount(AccountBan ban) {
		try {
			PreparedStatement s = sql("INSERT INTO `accountBans` (`id`, `author`, `reason`, `appliedTime`, `duration`) VALUES ('" + ban.getAccountId().toString() + "', '" + ban.getAuthor() + "', '" + ban.getReason() + "', '" + ban.getAppliedTime() + "', '" + ban.getDuration() + "');");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean banMac(MacBan ban) {
		try {
			PreparedStatement s = sql("INSERT INTO `macBans` (`macAddress`, `author`, `reason`, `appliedTime`) VALUES ('" + ban.getMacAddress() + "', '" + ban.getAuthor() + "', '" + ban.getReason() + "', '" + ban.getAppliedTime() + "');");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean banIp(IpBan ban) {
		try {
			PreparedStatement s = sql("INSERT INTO `ipBans` (`ipAddress`, `author`, `reason`, `appliedTime`) VALUES ('" + ban.getAddress() + "', '" + ban.getAuthor() + "', '" + ban.getReason() + "', '" + ban.getAppliedTime() + "');");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean unbanMac(MacBan macBan) {
		try {
			PreparedStatement s = sql("DELETE FROM `macBans` WHERE `macAddress`='" + macBan.getMacAddress() + "';");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean unbanAccount(AccountBan accountBan) {
		try {
			PreparedStatement s = sql(
					"DELETE FROM `accountBans` WHERE `id`='" + accountBan.getAccountId().toString() + "';");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean unbanIp(IpBan ipBan) {
		try {
			PreparedStatement s = sql("DELETE FROM `ipBans` WHERE `ipAddress`='" + ipBan.getAddress() + "';");
			s.execute();
			s.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private PreparedStatement sql(String sql) throws SQLException {
		return WeavenMC.getCommonMysql().preparedStatement(sql);
	}
}
