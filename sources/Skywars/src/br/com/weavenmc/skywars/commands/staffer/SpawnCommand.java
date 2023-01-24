package br.com.weavenmc.skywars.commands.staffer;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.commands.BaseCommand;

public class SpawnCommand extends BaseCommand {

	public SpawnCommand() {
		super("setspawn", "Set spawns of map", Arrays.asList("spawnset"));
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
				player.sendMessage("§3§lSPAWN §fUtilize o comando: /setspawn (1/12)");
				return true;
			}
			if (args.length == 1) {
				if (!isInteger(args[0])) {
					player.sendMessage("§c§lSPAWN §fUtilize somente números para setar!");
					return true;
				}
				int i = Integer.valueOf(args[0]);
				if (i <= 0) {
					player.sendMessage("§c§lSPAWN §fNão pode ser menor ou igual à zero.");
					return true;
				}
				if (i > 12) {
					player.sendMessage("§c§lSPAWN §fNão há possibilidade de ser maior que 12 spawns.");
					return true;
				}
				WeavenSkywars.getSpawnsManager().addSpawn(player, i);
				player.sendMessage("§a§lSPAWN §f");
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

}
