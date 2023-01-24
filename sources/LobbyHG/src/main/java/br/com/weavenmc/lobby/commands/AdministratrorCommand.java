package br.com.weavenmc.lobby.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;

public class AdministratrorCommand implements CommandClass {

	@Command(name = "hologram", aliases = { "hl" }, groupToUse = Group.DONO)
	public void hologram(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <ranking, hgwins, pvpkills>");
			} else {
				if (args[0].equalsIgnoreCase("ranking")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Lobby.getPlugin().getConfig();
					config.set("hologram.toprank.location.x", playerLocation.getX());
					config.set("hologram.toprank.location.y", playerLocation.getY());
					config.set("hologram.toprank.location.z", playerLocation.getZ());
					config.set("hologram.toprank.location.yaw", playerLocation.getYaw());
					config.set("hologram.toprank.location.pitch", playerLocation.getPitch());
					Lobby.getPlugin().saveConfig();

					if (Lobby.getPlugin().getTopHologram() != null)
						Lobby.getPlugin().getTopHologram().setLocation(playerLocation);
					else
						Lobby.getPlugin().checkTopHologram();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP RANKING§f foi §b§lATUALIZADO§f!");
				} else if (args[0].equalsIgnoreCase("hgwins")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Lobby.getPlugin().getConfig();
					config.set("hologram.hgwins.location.x", playerLocation.getX());
					config.set("hologram.hgwins.location.y", playerLocation.getY());
					config.set("hologram.hgwins.location.z", playerLocation.getZ());
					config.set("hologram.hgwins.location.yaw", playerLocation.getYaw());
					config.set("hologram.hgwins.location.pitch", playerLocation.getPitch());
					Lobby.getPlugin().saveConfig();

					if (Lobby.getPlugin().getTopHGWins() != null)
						Lobby.getPlugin().getTopHGWins().setLocation(playerLocation);
					else
						Lobby.getPlugin().checkTopHolograms();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP HG WINS§f foi §b§lATUALIZADO§f!");
				} else if (args[0].equalsIgnoreCase("pvpkills")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = Lobby.getPlugin().getConfig();
					config.set("hologram.pvpkills.location.x", playerLocation.getX());
					config.set("hologram.pvpkills.location.y", playerLocation.getY());
					config.set("hologram.pvpkills.location.z", playerLocation.getZ());
					config.set("hologram.pvpkills.location.yaw", playerLocation.getYaw());
					config.set("hologram.pvpkills.location.pitch", playerLocation.getPitch());
					Lobby.getPlugin().saveConfig();

					if (Lobby.getPlugin().getTopPVPKills() != null)
						Lobby.getPlugin().getTopPVPKills().setLocation(playerLocation);
					else
						Lobby.getPlugin().checkTopHolograms();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP PVP KILLS§f foi §b§lATUALIZADO§f!");
				} else {
					sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <ranking, hgwins, hgkills, pvpkills>");
				}
			}
		}
	}

	@Command(name = "statu2s", aliases = { "stats2" }, groupToUse = Group.DONO)
	public void status(BukkitCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("/staus (xp|moedas) (uuid do maluco) (tanto a ser adicionado fodase mo preguiça de fazer o resto)");
			return;
		}
		
		if (args[0].equalsIgnoreCase("xp")) {
	
			String uuid = args[1];
			int a = Integer.valueOf(args[2]);
			
			WeavenMC.getAccountCommon().getWeavenPlayer(Bukkit.getPlayer(uuid).getUniqueId()).addXp(a);;
			sender.sendMessage("foi caraio");
			
		}

		
		if (args[0].equalsIgnoreCase("moedas")) {
	
			String uuid = args[1];
			int a = Integer.valueOf(args[2]);
			
			WeavenMC.getAccountCommon().getWeavenPlayer(Bukkit.getPlayer(uuid).getUniqueId()).addMoney(a);;
			sender.sendMessage("foi caraio");
		}
	}
	@Command(name = "setnpc", aliases = { "npc" }, groupToUse = Group.DONO)
	public void npc(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§e§lNPC§f Você deve utilizar §e/npc (HG, EVENTO)");
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
