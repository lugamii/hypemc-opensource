package br.com.weavenmc.ypvp.minigame;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import br.com.weavenmc.ypvp.Management;
import br.com.weavenmc.ypvp.yPvP;
import lombok.Getter;

@Getter
public class LocationManager extends Management {
	
	private FileConfiguration config;

	public LocationManager(yPvP plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		config = getPlugin().getConfig();
	}
	
	public void saveLocation(String name, Location loc) {
		config.set("Locations." + name.toLowerCase() + ".x", loc.getX());
		config.set("Locations." + name.toLowerCase() + ".y", loc.getY());
		config.set("Locations." + name.toLowerCase() + ".z", loc.getZ());
		config.set("Locations." + name.toLowerCase() + ".yaw", loc.getYaw());
		config.set("Locations." + name.toLowerCase() + ".pitch", loc.getPitch());
		getPlugin().saveConfig();
	}
	
	public Location getLocation(String name) {
		if (config.get("Locations." + name.toLowerCase()) != null) {
			double x = config.getDouble("Locations." + name.toLowerCase() + ".x");
			double y = config.getDouble("Locations." + name.toLowerCase() + ".y");
			double z = config.getDouble("Locations." + name.toLowerCase() + ".z");
			float yaw = config.getInt("Locations." + name.toLowerCase() + ".yaw");
			float pitch = config.getInt("Locations." + name.toLowerCase() + ".pitch");
			return new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);
		} 
		return null;
	}

	@Override
	public void disable() {
		config = null;
	}
}
