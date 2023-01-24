package br.com.weavenmc.skywars.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Packet;

public class Reflection {
	
	public static void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendPacket(Packet<?> packet) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			sendPacket(packet, player);
		});
	}

}
