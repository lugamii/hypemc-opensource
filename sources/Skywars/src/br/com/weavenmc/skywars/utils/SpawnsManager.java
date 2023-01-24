package br.com.weavenmc.skywars.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import br.com.weavenmc.skywars.WeavenSkywars;

public class SpawnsManager {

	public void addSpawn(Player player, int i) {
		setConfig(i, player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ());
		WeavenSkywars.getInstance().saveFiles();
	}
	
	public ArrayList<Location> getInformation(String key) {
		ArrayList<Location> array = new ArrayList<>();
		ConfigurationSection section = WeavenSkywars.getSpawns().getConfigurationSection(key);
		if (section != null) {
			//System.out.println("Section != null");
			for (String id : section.getKeys(false)) {
				if (WeavenSkywars.getSpawns().get(key + "." + id) == null) {
					//System.out.println("getConfig().get(key + id) == null");
					continue;
				}
				//System.out.println("getConfig().get(key + id) != null");
				String location = WeavenSkywars.getSpawns().getString(key + "." + id);
				array.add(getParseLocation(location));
			}
		} else {
			System.out.println("Section == null");
		}
		return array;
	}
	
	public ArrayList<Location> getSpawn() {
		return getInformation("Spawns");
	}
	
	public void updateEnabled() {
		if (getSpawn() == null) {
			System.out.println("null");
			WeavenSkywars.enabled = false;
			return;
		}
		System.out.println(getSpawn().size());
		if (getSpawn().size() < 12) {
			System.out.println("Menor que 12");
			WeavenSkywars.enabled = false;
			return;
		}
	}
	
	private void setConfig(int i, String location) {
		WeavenSkywars.getSpawns().set("Spawns." + i, location);
	}
	
	private Location getParseLocation(String spawn) {
		String[] spawns = spawn.split(",");
		double x = Double.valueOf(spawns[0]);
		double y = Double.valueOf(spawns[1]);
		double z = Double.valueOf(spawns[2]);
		float yaw = 0;
		float pitch = 0;

		if (spawns.length >= 5) {
			yaw = Float.valueOf(spawns[3]);
			pitch = Float.valueOf(spawns[4]);
		}

		World world = Bukkit.getWorld("mapa");
		return new Location(world, x, y, z, yaw, pitch);
	}

}
