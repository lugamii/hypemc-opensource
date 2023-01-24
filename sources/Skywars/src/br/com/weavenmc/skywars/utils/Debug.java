package br.com.weavenmc.skywars.utils;

import org.bukkit.Bukkit;

public class Debug {
	
	public enum TypeAlert {
		AVISO, SUCESSO, ALERTA, ERROR
	}
	
	public void sendMessage(String message, TypeAlert typeAlert) {
		if (typeAlert == TypeAlert.ALERTA) {
			Bukkit.getConsoleSender().sendMessage("§6[WeavenSkyWars] §e"+message);
		}
		else if (typeAlert == TypeAlert.AVISO) {
			Bukkit.getConsoleSender().sendMessage("§6[WeavenSkyWars] §c"+message);
		}
		else if (typeAlert == TypeAlert.SUCESSO) {
			Bukkit.getConsoleSender().sendMessage("§6[WeavenSkyWars] §a"+message);
		}
		else if (typeAlert == TypeAlert.ERROR) {
			Bukkit.getConsoleSender().sendMessage("§6[WeavenSkyWars] §c"+message);
		}
	}

}
