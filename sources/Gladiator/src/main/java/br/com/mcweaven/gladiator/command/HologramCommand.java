package br.com.mcweaven.gladiator.command;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class HologramCommand implements CommandClass {

	@Command(name = "hologram", aliases = { "hl" }, groupToUse = Group.DONO)
	public void hologram(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <ws, wins>");
			} else {
				if (args[0].equalsIgnoreCase("ws")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Gladiator.getInstance().getConfig();
					config.set("hologram.ws.location.x", playerLocation.getX());
					config.set("hologram.ws.location.y", playerLocation.getY());
					config.set("hologram.ws.location.z", playerLocation.getZ());
					config.set("hologram.ws.location.yaw", playerLocation.getYaw());
					config.set("hologram.ws.location.pitch", playerLocation.getPitch());
					Gladiator.getInstance().saveConfig();
					Gladiator.getInstance().getTopWinStreak().setLocation(playerLocation);
					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP WINSTREAK§f foi §b§lATUALIZADO§f!");
				}
				if (args[0].equalsIgnoreCase("wins")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Gladiator.getInstance().getConfig();
					config.set("hologram.wins.location.x", playerLocation.getX());
					config.set("hologram.wins.location.y", playerLocation.getY());
					config.set("hologram.wins.location.z", playerLocation.getZ());
					config.set("hologram.wins.location.yaw", playerLocation.getYaw());
					config.set("hologram.wins.location.pitch", playerLocation.getPitch());
					Gladiator.getInstance().saveConfig();
					WeavenMC.debug("salvo");
					Gladiator.getInstance().getTopWins().setLocation(playerLocation);

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP WINS§f foi §b§lATUALIZADO§f!");
				}
			}
		}
	}

}
