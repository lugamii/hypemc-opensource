package br.com.weavenmc.commons.core.backend;

import java.sql.SQLException;

public interface Backend {

	void openConnection() throws SQLException;
	
	void closeConnection()  throws SQLException;
	
	boolean isConnected();
	
	void recallConnection() throws SQLException;
}
