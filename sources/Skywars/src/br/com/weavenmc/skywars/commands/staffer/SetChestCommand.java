package br.com.weavenmc.skywars.commands.staffer;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.commands.BaseCommand;
import br.com.weavenmc.skywars.utils.ChestManager.typeChest;

public class SetChestCommand extends BaseCommand {

	public SetChestCommand() {
		super("setminifeast", "Set minifeast of map", Arrays.asList("minifeastset"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			Player player = getPlayer(commandSender);
			if (!isPermission(player, Group.DIRETOR)) {
				sendError(player, "§fVocê não tem permissão para usar esse comando.");
				return true;
			}
			if (args.length < 2) {
				player.sendMessage("§3§lMINIFEAST §fUtilize o comando: /setchest (type) (1/...)");
				return true;
			}
			if (args.length == 2) {
				if (!isInteger(args[1])) {
					player.sendMessage("§c§lMINIFEAST §fUtilize somente números para setar!");
					return true;
				}
				typeChest type = typeChest.NORMAL;
				try {
					type = typeChest.valueOf(args[0].toUpperCase());
				} catch (Exception e) {
					player.sendMessage("§c§lCHEST §fEsse tipo de baú não existe!");
					return true;
				}
				int i = Integer.valueOf(args[1]);
				Block targetBlock = player.getTargetBlock((Set<Material>) null, 200);
				if (targetBlock.getType() != Material.CHEST) {
					player.sendMessage("§c§lCHEST §fEsse bloco não é um baú!");
					return true;
					
				}
				WeavenSkywars.getChestManager().addChest(targetBlock, i, type);
				player.sendMessage("§a§lCHEST §fBaú " + i + " do tipo " + type.name() + " setado com sucesso!");
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

}
