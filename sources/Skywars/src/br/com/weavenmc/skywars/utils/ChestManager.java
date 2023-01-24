package br.com.weavenmc.skywars.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import br.com.weavenmc.skywars.WeavenSkywars;

public class ChestManager {

	public enum typeChest {
		NORMAL, MINIFEAST, FEAST;
	}

	public void addChest(Block block, int i, typeChest chest) {
		setConfig(chest, i, block.getLocation().getBlockX() + "," + block.getLocation().getY() + ","
				+ block.getLocation().getBlockZ());
		WeavenSkywars.getInstance().getConfig().set("Max." + chest.name().toLowerCase(), i);
		WeavenSkywars.getInstance().saveFiles();
	}

	public void addChest(Player player, int i, typeChest chest) {
		int y = player.getLocation().getBlockY() - 1;
		setConfig(chest, i, player.getLocation().getBlockX() + "," + y + "," + player.getLocation().getBlockZ());
		WeavenSkywars.getInstance().getConfig().set("Max." + chest.name().toLowerCase(), i);
		WeavenSkywars.getInstance().saveFiles();
	}

	public ArrayList<Location> getInformation(String key) {
		ArrayList<Location> array = new ArrayList<>();
		ConfigurationSection section = WeavenSkywars.getChest().getConfigurationSection(key);
		// System.out.println(" ");
		// System.out.println(" ");
		// System.out.println(" ");
		// System.out.println("Chests " + key);
		if (section != null) {
			// System.out.println("Section != null");
			for (String id : section.getKeys(false)) {
				if (WeavenSkywars.getChest().get(key + "." + id) == null) {
					// System.out.println("getConfig().get(key + id) == null");
					continue;
				}
				// System.out.println("getConfig().get(key + id) != null");
				String location = WeavenSkywars.getChest().getString(key + "." + id);
				array.add(getParseLocation(location));
			}
		} else {
			System.out.println("Section == null");
		}
		return array;
	}

	public ArrayList<Location> getMiniFeastChest() {
		return getInformation("Chest.minifeast");
	}

	public void updateEnabled() {
		if (getMiniFeastChest() == null) {
			System.out.println("Minifeast: null");
			WeavenSkywars.enabled = false;
			return;
		}
	}

	public int getMax(typeChest type) {
		return WeavenSkywars.getInstance().getConfig().getInt("Max." + type.name().toLowerCase());
	}

	private void setConfig(typeChest type, int i, String location) {
		WeavenSkywars.getChest().set("Chest." + type.toString().toLowerCase() + "." + i, location);
	}

	public Location getChest(Block block) {
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		World world = block.getLocation().getWorld();
		return new Location(world, x, y, z);
	}

	public Location getBox(String type, int i) {
		Location location = new Location(Bukkit.getWorld("mapa"), 0, 0, 0);
		int x = getBlock(typeChest.valueOf(type.toUpperCase()), i, "X");
		int y = getBlock(typeChest.valueOf(type.toUpperCase()), i, "Y");
		int z = getBlock(typeChest.valueOf(type.toUpperCase()), i, "Z");
		World world = Bukkit.getWorld("mapa");
		System.out.println(location);
		location = new Location(world, x, y, z);
		System.out.println(location);
		return location;
	}

	private static int getBlock(typeChest type, int i, String location) {
		int r = WeavenSkywars.getChest().getInt("Chest." + type.toString().toLowerCase() + "." + i + "." + location);
		return r;
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

	public boolean hasBox(Block block, typeChest type) {
		if (getMax(type) == 1) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
		}
		if (getMax(type) == 2) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
		}
		if (getMax(type) == 3) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
		}
		if (getMax(type) == 4) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
		}
		if (getMax(type) == 5) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
		}
		if (getMax(type) == 6) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 6))) {
				return true;
			}
		}
		if (getMax(type) == 7) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 6))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 7))) {
				return true;
			}
		}
		if (getMax(type) == 8) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 6))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 7))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 8))) {
				return true;
			}
		}
		if (getMax(type) == 9) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 6))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 7))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 8))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 9))) {
				return true;
			}
		}
		if (getMax(type) == 10) {
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 1))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 2))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 3))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 4))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 5))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 6))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 7))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 8))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 9))) {
				return true;
			}
			if (getChest(block).equals(getBox(type.name().toLowerCase(), 10))) {
				return true;
			}
		}
		return false;
	}

}
