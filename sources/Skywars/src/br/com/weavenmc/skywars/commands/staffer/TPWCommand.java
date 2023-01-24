package br.com.weavenmc.skywars.commands.staffer;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.commands.BaseCommand;

public class TPWCommand extends BaseCommand {

	public TPWCommand() {
		super("tpw", "Teleport to other world", Arrays.asList("teleportworld", "teleportw", "tpworld"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			Player player = getPlayer(commandSender);
			if (!isPermission(player, Group.DIRETOR)) {
				sendError(player, "§fVocê não tem permissão para usar esse comando.");
				return true;
			}
			if (args.length == 0) {
				player.sendMessage("§3§lTELEPORT §fUtilize o comando: /tpw (mundo)");
				return true;
			}
			if (args.length == 1) {
				String worldName = args[0];
				if (Bukkit.getWorld(worldName) == null) {
					player.sendMessage("§c§lTELEPORT §fO mundo " + args[0] + " não existe.");
					return true;
				}
				player.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
				player.sendMessage("§a§lTELEPORT §fIndo até o §e" + worldName);
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

}
