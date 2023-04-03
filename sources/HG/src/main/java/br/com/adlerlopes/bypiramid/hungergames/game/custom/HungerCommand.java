package br.com.adlerlopes.bypiramid.hungergames.game.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class HungerCommand extends Command {

	private Manager manager;
	public boolean enabled = true;
	public static final String ERROR = "§c§lERROR §f";
	public static final String NO_PERMISSION = "§c§lPERMISSAO §f";
	public static final String OFFLINE = "§c§lOFFLINE §f";

	public HungerCommand(String name) {
		super(name);
	}

	public HungerCommand(String name, String description) {
		super(name, description, "", new ArrayList<String>());
	}

	public HungerCommand(String name, String description, List<String> aliases) {
		super(name, description, "", aliases);
	}

	public abstract boolean execute(CommandSender commandSender, String label, String[] args);

	public Manager getManager() {
		this.manager = HungerGames.getManager();
		return manager;
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		return sender.hasPermission("hunger.cmd." + perm);
	}

	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static String getError() {
		return ERROR;
	}

	public static String getOffline() {
		return OFFLINE;
	}

	public static String getNoPermission() {
		return NO_PERMISSION;
	}

	public void sendNumericMessage(CommandSender commandSender) {
		commandSender.sendMessage("§cVocê informou um caractere com um número. Números não são permitidos.");
	}

	public void sendPermissionMessage(CommandSender commandSender) {
		commandSender.sendMessage("§cVocê não tem permissão.");
	}

	public void sendExecutorMessage(CommandSender commandSender) {
		commandSender.sendMessage("ERRO: Somente players podem usar esse comando.");
	}

	public void sendArgumentMessage(CommandSender commandSender, String command, String args) {
		commandSender.sendMessage("§aUse: §f" + args);
	}
	
	public void sendWarning(String warning) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("hunger.alerts.admin")) {
				player.sendMessage("§7(!) " + warning + "");
			}
		}
	}
}