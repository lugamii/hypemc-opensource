	package br.com.weavenmc.commons.bukkit.command.register;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class AdminCommand implements CommandClass {

	@Command(name = "admin", aliases = { "adm", "v", "vanish" }, groupToUse = Group.INVESTIDOR)
	public void admin(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (AdminMode.getInstance().isAdmin(p)) {
				AdminMode.getInstance().setPlayer(p);
			} else {
				AdminMode.getInstance().setAdmin(p);
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
}
