package br.com.weavenmc.skywars.commands.staffer;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.commands.BaseCommand;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;
import br.com.weavenmc.timer.Iniciando;

public class StartCommand extends BaseCommand {

	public StartCommand() {
		super("start", "Start the game", Arrays.asList("iniciar"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			Player player = getPlayer(commandSender);
			if (!isPermission(player, Group.ADMIN)) {
				sendError(player, "§fVocê não tem permissão para usar esse comando.");
				return true;
			}
			if (args.length > 0) {
				player.sendMessage("§3§lSTART §fUtilize o comando: /start");
				return true;
			}
			if (args.length == 0) {
				if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY) {
					player.sendMessage("§c§lSTART §fO estágio precisa ser lobby!");
					return true;
				}
				Bukkit.getOnlinePlayers().forEach(players -> {
					Scoreboarding.sendScoreboard(players);
				});
				WeavenSkywars.getGameManager().startPartida();
				Iniciando.timer = 6;
				player.sendMessage("§a§lSTART §fPartida iniciando com sucesso!");
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

}
