package br.com.weavenmc.commons.core.backend.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.weavenmc.commons.core.backend.Backend;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import lombok.Getter;

@Getter
public class MySQLBackend implements Backend {

	private MySQL mySQL;
	private Connection connection;

	public MySQLBackend(MySQL mySQL) {
		this.mySQL = mySQL;
	}

	private String createIfNotExistsQuery(DataCategory category, DataType... dataTypes) {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = this.preparedStatement(
					"CREATE TABLE IF NOT EXISTS `cracked_profiles` (`name` VARCHAR(17), `username` VARCHAR(17), `uniqueId` VARCHAR(50));");

			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder b = new StringBuilder();

		b.append("CREATE TABLE IF NOT EXISTS `" + category.getTableName() + "` (");

		int inx = 0;
		int max = dataTypes.length;

		b.append("`uniqueId` VARCHAR(50)");

		while (inx < max) {
			DataType current = dataTypes[inx];

			b.append(", `" + current.getField() + "` " + current.getTableType());

			inx++;
		}

		b.append(");");
		return b.toString();
	}

	public void createTablesIfNotExists() throws SQLException {
		for (DataCategory category : DataCategory.values()) {
			PreparedStatement s = preparedStatement(createIfNotExistsQuery(category, category.getDataTypes()));
			s.execute();
			s.close();
		}
	}

	@Override
	public void openConnection() throws SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://" + mySQL.getHostname() + ":" + mySQL.getPort() + "/"
				+ mySQL.getDatabase() + "?autoReconnect=true", mySQL.getUsername(), mySQL.getPassword());
	}

	public PreparedStatement preparedStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	@Override
	public void closeConnection() throws SQLException {
		if (connection == null)
			return;
		connection.close();
	}

	@Override
	public boolean isConnected() {
		if (connection == null)
			return false;

		try {
			return !connection.isClosed();
		} catch (SQLException ex) {
			return false;
		}
	}

	@Override
	public void recallConnection() throws SQLException {
		if (isConnected())
			return;
		openConnection();
	}
}
