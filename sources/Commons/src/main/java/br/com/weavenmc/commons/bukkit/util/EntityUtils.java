package br.com.weavenmc.commons.bukkit.util;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

import com.comphenix.protocol.utility.MinecraftReflection;

public class EntityUtils {
	
	public static synchronized int next() {
        try {
            Class<?> clazz = MinecraftReflection.getEntityClass();
            Field field = clazz.getDeclaredField("entityCount");
            field.setAccessible(true);
            int id = field.getInt(null);
            field.set(null, id + 1);
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

	public static int clearDrops() {
		int drops = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Item) {
					entity.remove();
					++drops;
				}
			}
		}
		return drops;
	}

	public static int clearEntities() {
		int cleared = 0;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Item || entity instanceof Animals || entity instanceof Monster
						|| entity instanceof NPC) {
					entity.remove();
					++cleared;
				}
			}
		}
		return cleared;
	}
}
