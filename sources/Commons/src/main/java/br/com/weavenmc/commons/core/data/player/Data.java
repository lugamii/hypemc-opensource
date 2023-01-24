package br.com.weavenmc.commons.core.data.player;

import java.util.ArrayList;

public class Data {

	private Object data;

	public Data(Object data){
		this.data = data;
	}
	
	public void setValue(Object data) {
		this.data = data;
	}

	public Object asObject() {
		return data;
	}

	public String asString() {
		return (String) data;
	}

	public Integer asInt() {
		return (Integer) data;
	}

	public Long asLong() {
		return (Long) data;
	}

	public Boolean asBoolean() {
		return (Boolean) data;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> asList() {
		return (ArrayList<String>) data;
	}
	
	@Override
	public String toString() {
		if (data != null)
			return data.toString();
		return "null";
	}
}
