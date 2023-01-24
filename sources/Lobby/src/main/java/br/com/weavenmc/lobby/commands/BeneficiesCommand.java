package br.com.weavenmc.lobby.commands;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.gamer.Gamer;

public class BeneficiesCommand implements CommandClass {

	@Command(name = "fly", aliases = { "voar" }, groupToUse = Group.VIP)
	public void fly(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (!gamer.isFlying()) {
				gamer.setFlying(true);
				while (!p.getAllowFlight()) 
					p.setAllowFlight(true);
				p.setFlying(true);
				p.sendMessage("§6§lFLY§f Você §a§lHABILITOU§f o modo §e§lFLY.");
			} else {
				gamer.setFlying(false);
				while (p.getAllowFlight())
					p.setAllowFlight(false);
				p.setFlying(false);
				p.sendMessage("§6§lFLY§f Você §c§lDESABILITOU§f o modo §e§lFLY.");
			}
			gamer = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
}
