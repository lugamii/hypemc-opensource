package br.com.weavenmc.lobby.npcs;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Packet;

public class Reflections {
	public static void setValue(String name, Object obj, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception exception) {
		}
	}

	public static Object getValue(String name, Object obj) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception exception) {
			return null;
		}
	}

	public static void sendPacket(Packet<?> packet, Player player) {
		(((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
	}

	public static void sendPacket(Packet<?> packet) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendPacket(packet, player);
	}
}