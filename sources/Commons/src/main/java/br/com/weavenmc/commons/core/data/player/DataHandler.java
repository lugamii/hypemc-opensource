package br.com.weavenmc.commons.core.data.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;

public class DataHandler {

	private final Map<DataType, Data> datas;
	private final Map<DataCategory, Boolean> loadedCategories;

	protected String name;
	protected UUID uuid;

	public DataHandler(UUID uuid, String name){
		this.uuid = uuid;
		this.name = name;

		datas = new ConcurrentHashMap<>();
		loadedCategories = new ConcurrentHashMap<>();

		for (DataCategory dataCategory : DataCategory.values()) 
		{
			loadedCategories.put(dataCategory, false);
		}

		for (DataType dataType : DataType.values()) 
		{

			if (dataType.isUsername()) {
				datas.put(dataType, new Data(this.name));
				continue;
			}

			if (dataType.isUUID()) {
				datas.put(dataType, new Data(this.uuid));
				continue;
			}

			datas.put(dataType, new Data(dataType.getDefaultValue()));
		}
	}

	public boolean isCategoryLoaded(DataCategory category) {
		return loadedCategories.get(category);
	}

	public Data getData(DataType dataType) {
		return datas.get(dataType);
	}

	public PreparedStatement sql(String sql) throws SQLException {
		return WeavenMC.getCommonMysql().preparedStatement(sql);
	}

	public boolean load(DataCategory... categories){
		Callable<Boolean> callable = new Callable<Boolean>() 
		{
			@Override
			public Boolean call() throws Exception 
			{
				int inx = 0;
				int max = categories.length;

				while (inx < max) 
				{
					DataCategory current = categories[inx];
					selectFromOrInsert(current, current.getDataTypes());

					loadedCategories.put(current, true);
					inx++;
				}

				return true;
			}
		};

		try {
			return callable.call();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public void save(DataCategory... categories){
		WeavenMC.getAsynchronousExecutor().runAsync(() -> 
		{
			int inx = 0;
			int max = categories.length;
			
			while (inx < max) 
			{
				DataCategory current = categories[inx];			
				try 
				{
					PreparedStatement s = sql(createUpdateStringQuery(current, current.getDataTypes()));
					s.execute();
					s.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}			
				inx++;
			}		
		});
	}
	
	public String createUpdateStringQuery(DataCategory category, DataType... dataTypes) {
		StringBuilder b = new StringBuilder();
		b.append("UPDATE `" + category.getTableName() + "` SET ");
		
		int inx = 0;
		int max = dataTypes.length;
		
		while (inx < max) {
			DataType current = dataTypes[inx];
			
			Object data = getData(current);
			
			if (current == DataType.GROUPS || current == DataType.PLAYER_PERMISSIONS) {
				data = data.toString();
			}
			
			if (inx == 0) {
				b.append("`" + current.getField() + "`='" + data + "'");
			} else {
				b.append(", `" + current.getField() + "`='" + data + "'");
			}
			
			inx++;
		}
		
		b.append(" WHERE `uniqueId`='" + uuid.toString() + "';");
		
		return b.toString();
	}

	public String createInsertIntoStringQuery(DataCategory category, DataType... dataTypes) {
		StringBuilder b = new StringBuilder();
		b.append("INSERT INTO `" + category.getTableName() + "` (");

		int inx = 0;
		int max = dataTypes.length;

		b.append("`uniqueId`");
		
		while (inx < max) {
			DataType current = dataTypes[inx];

			b.append(", `" + current.getField() + "`");

			inx++;
		}

		b.append(") VALUES (");

		inx = 0;

		b.append("'" + uuid.toString() + "'");
		
		while (inx < max) {
			DataType current = dataTypes[inx];

			Object value = current.getDefaultValue();

			if (current == DataType.GROUPS || current == DataType.PLAYER_PERMISSIONS) {
				value = "[]";
			}
			
			if (value == null) {
				value = "";
			}

			b.append(", '" + value + "'");

			inx++;
		}

		b.append(");");
		return b.toString();
	}

	public void insert(DataCategory category, DataType... dataTypes) throws SQLException {
		PreparedStatement p = sql(createInsertIntoStringQuery(category, dataTypes));
		p.execute();
		p.close();
	}
	
	public void selectFromOrInsert(DataCategory category, DataType... dataTypes) throws SQLException {
		PreparedStatement s = sql(
				"SELECT * FROM `" + category.getTableName() + "` WHERE `uniqueId`='" + uuid.toString() + "';");
		ResultSet r = s.executeQuery();

		if (category == DataCategory.COPA) {
			if (r.next()) {
				getData(DataType.PARTICIPANDO).setValue("true");
			} else {
				getData(DataType.PARTICIPANDO).setValue(null);
			}
			r.close();
			s.close();
			return;
		}
		
		if (r.next()) {
			int inx = 0;
			int max = dataTypes.length;

			while (inx < max) {
				DataType current = dataTypes[inx];
				boolean fromStringToList = false;

				if (current == DataType.GROUPS || current == DataType.PLAYER_PERMISSIONS) {
					fromStringToList = true;
				}

				getData(current).setValue(
						getDataFromResultSet(r, current.getField(), current.getClassExpected(), fromStringToList));
				inx++;
			}
		} else {
			PreparedStatement p = sql(createInsertIntoStringQuery(category, dataTypes));
			p.execute();
			p.close();
		}

		r.close();
		s.close();
	}

	public Object getDataFromResultSet(ResultSet resultSet, String fieldName, String classExpected,
			boolean fromStringToList) throws SQLException {
		if (classExpected.equals("String"))
			return (fromStringToList ? fromStringToList(resultSet.getString(fieldName))
					: resultSet.getString(fieldName));
		else if (classExpected.equals("Int"))
			return resultSet.getInt(fieldName);
		else if (classExpected.equals("Long"))
			return resultSet.getLong(fieldName);
		else if (classExpected.equals("Boolean"))
			return resultSet.getBoolean(fieldName);
		return "0";
	}

	public ArrayList<String> fromStringToList(String str) {
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
