package br.com.weavenmc.skywars.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.permission.Group;

public abstract class BaseCommand extends Command {

	public boolean enabled = true;

	public BaseCommand(String name) {
		super(name);
	}

	public BaseCommand(String name, String description) {
		super(name, description, "", new ArrayList<>());
	}

	public BaseCommand(String name, String description, List<String> aliases) {
		super(name, description, "", aliases);
	}

	public abstract boolean execute(CommandSender commandSender, String label, String[] args);
	
	public boolean isPermission(Player sender, Group group) {
		return BukkitPlayer.getPlayer(sender.getUniqueId()).hasGroupPermission(group);
	}
	
	public void sendError(CommandSender commandSender, String message) {
		getPlayer(commandSender).sendMessage("§c§lERROR §f" + message);
	}
	
	protected int getInteger(CommandSender commandSender, String value, int min) {
		return getInteger(commandSender, value, min, Integer.MAX_VALUE);
	}

	public int getInteger(CommandSender commandSender, String value, int min, int max) {
		return getInteger(commandSender, value, min, max, false);
	}

	public int getInteger(CommandSender commandSender, String value, int min, int max, boolean Throws) {
		int i = min;
		try {
			i = Integer.valueOf(value).intValue();
		} catch (NumberFormatException ex) {
			if (Throws) {
				throw new NumberFormatException(String.format("%s is not a valid number", new Object[] { value }));
			}
		}
		if (i < min) {
			i = min;
		} else if (i > max) {
			i = max;
		}
		return i;
	}
	public Player getPlayer(String args) {
		return Bukkit.getPlayer(args);
	}
	
	public String toIntegerFromString(int i) {
		if (i == 1) return "a";
		else if (i == 2) return "b";
		else if (i == 3) return "c";
		else if (i == 4) return "d";
		else if (i == 5) return "e";
		else if (i == 6) return "f";
		else if (i == 7) return "g";
		else if (i == 8) return "h";
		else if (i == 9) return "i";
		else if (i == 10) return "j";
		else if (i == 11) return "l";
		else if (i == 12) return "m";
		else if (i == 13) return "n";
		else if (i == 14) return "o";
		else if (i == 15) return "p";
		else if (i == 16) return "q";
		else if (i == 17) return "r";
		else if (i == 18) return "s";
		else if (i == 19) return "u";
		else if (i == 20) return "v";
		else if (i == 21) return "w";
		else if (i == 22) return "x";
		else if (i == 23) return "y";
		else if (i == 24) return "z";
		else return null;
	}
	
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer(String args) {
		return Bukkit.getOfflinePlayer(args);
	}
	
	public Player getPlayer(CommandSender sender) {
		return (Player)sender;
	}
	
	public boolean isOnline(Player target) {
		return target != null;
	}

	public boolean isPlayer(CommandSender commandSender) {
		return commandSender instanceof Player;
	}

	public boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String getArgs(String[] args, int starting) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = starting; i < args.length; i++) {
			stringBuilder.append(args[i] + " ");
		}
		return stringBuilder.toString();
	}

	public void sendWarning(String warning) {
		//
	}

}
