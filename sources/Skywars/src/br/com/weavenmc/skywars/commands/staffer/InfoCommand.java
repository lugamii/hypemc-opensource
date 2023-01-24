package br.com.weavenmc.skywars.commands.staffer;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.commands.BaseCommand;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;

public class InfoCommand extends BaseCommand {

	public InfoCommand() {
		super("info", "Start the game", Arrays.asList("information"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			commandSender.sendMessage(" §a§lSKYWARS");
			commandSender.sendMessage("");
			commandSender.sendMessage("§7Mapa: §b" + WeavenSkywars.getGameManager().getMapName());
			commandSender.sendMessage("§7Estado: §b" + WeavenSkywars.getGameManager().getState());
			if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
				commandSender.sendMessage("§7Evento: §3" + WeavenSkywars.getGameManager().getEState());
			}
			commandSender.sendMessage("§7Jogadores online: §2" + Bukkit.getOnlinePlayers().size());
			commandSender.sendMessage("§7Jogadores vivos: §a" + GameController.player.size());
			commandSender.sendMessage("§7Jogadores espectando: §8" + GameController.spectador.size());
		} else {
			commandSender.sendMessage("§cComando criado somente para o console.");
			return true;
		}
		return false;
	}

}
