package br.com.weavenmc.lobby.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;

public class AdministratrorCommand implements CommandClass {

	@Command(name = "hologram", aliases = { "hl" }, groupToUse = Group.DONO)
	public void hologram(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <swwins, swkills>");
			} else {
				if (args[0].equalsIgnoreCase("swkills")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Lobby.getPlugin().getConfig();
					config.set("hologram.swkills.location.x", playerLocation.getX());
					config.set("hologram.swkills.location.y", playerLocation.getY());
					config.set("hologram.swkills.location.z", playerLocation.getZ());
					config.set("hologram.swkills.location.yaw", playerLocation.getYaw());
					config.set("hologram.swkills.location.pitch", playerLocation.getPitch());
					Lobby.getPlugin().saveConfig();

					if (Lobby.getPlugin().getTopSwKills() != null)
						Lobby.getPlugin().getTopSwKills().setLocation(playerLocation);
	

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP SWKILLS§f foi §b§lATUALIZADO§f!");
				} else if (args[0].equalsIgnoreCase("swwins")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Lobby.getPlugin().getConfig();
					config.set("hologram.swwins.location.x", playerLocation.getX());
					config.set("hologram.swwins.location.y", playerLocation.getY());
					config.set("hologram.swwins.location.z", playerLocation.getZ());
					config.set("hologram.swwins.location.yaw", playerLocation.getYaw());
					config.set("hologram.swwins.location.pitch", playerLocation.getPitch());
					Lobby.getPlugin().saveConfig();

					if (Lobby.getPlugin().getTopSwWins() != null)
						Lobby.getPlugin().getTopSwWins().setLocation(playerLocation);
					else
						Lobby.getPlugin().checkTopHolograms();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP SWWINS§f foi §b§lATUALIZADO§f!");
				} else {
					sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <swwins, swwkills>");
				}
			}
		}
	}

	@Command(name = "statu2s", aliases = { "stats2" }, groupToUse = Group.DONO)
	public void status(BukkitCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(
					"/staus (xp|moedas) (uuid do maluco) (tanto a ser adicionado fodase mo preguiça de fazer o resto)");
			return;
		}
		WeavenPlayer weavenPlayer = WeavenMC.getAccountCommon().getWeavenPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
		if (args[0].equalsIgnoreCase("xp")) {

			String uuid = args[1];
			int a = Integer.valueOf(args[2]);

			WeavenMC.getAccountCommon().getWeavenPlayer(Bukkit.getPlayer(uuid).getUniqueId()).addXp(a);
			;
			sender.sendMessage("foi caraio");

		}

		if (args[0].equalsIgnoreCase("moedas")) {

			String uuid = args[1];
			int a = Integer.valueOf(args[2]);

			weavenPlayer.addMoney(a);
			weavenPlayer.save(DataCategory.BALANCE);
			sender.sendMessage("foi caraio");
		}
	}

	@Command(name = "setnpc", aliases = { "npc" }, groupToUse = Group.DONO)
	public void npc(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§e§lNPC§f Você deve utilizar §e/npc (SKYWARS)");
				return;
			}

			Location playerLocation = sender.getPlayer().getLocation();
			FileConfiguration config = Lobby.getPlugin().getConfig();
			config.set("npc." + args[0].toLowerCase() + ".location.x", playerLocation.getX());
			config.set("npc." + args[0].toLowerCase() + ".location.y", playerLocation.getY());
			config.set("npc." + args[0].toLowerCase() + ".location.z", playerLocation.getZ());
			config.set("npc." + args[0].toLowerCase() + ".location.yaw", playerLocation.getYaw());
			config.set("npc." + args[0].toLowerCase() + ".location.pitch", playerLocation.getPitch());
			Lobby.getPlugin().saveConfig();
			sender.sendMessage("§e§lNPC§f Você definiu a localização do npc §e§l" + args[0].toUpperCase() + "§f!");

		}
	}
}
