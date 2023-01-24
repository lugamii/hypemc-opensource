package br.com.weavenmc.commons.bukkit.command.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.api.chat.ChatAPI;
import br.com.weavenmc.commons.bukkit.api.chat.ChatAPI.ChatState;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class StaffCommand implements CommandClass {


	@Command(name = "inventorysee", aliases = { "invsee", "inv" }, groupToUse = Group.TRIAL)
	public void inventorysee(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§6§lINVSEE§f Utilize: /invsee <player>");
			} else {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					p.openInventory(t.getInventory());
					p.sendMessage("§6§lINVSEE§f Visualizando o inventário de §e§l" + t.getName());
					t = null;
				} else {
					p.sendMessage("§6§lINVSEE§f " + args[0] + " está offline");
				}
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
	
	@Command(name = "chat", groupToUse = Group.TRIAL)
	public void chat(BukkitCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§3§lCHAT§f Utilize: /chat <on, off, clear>");
		} else {
			if (args[0].equalsIgnoreCase("off")) {
				if (ChatAPI.getInstance().getChatState() != ChatState.DISABLED) {
					ChatAPI.getInstance().setChatState(ChatState.DISABLED);
					Bukkit.broadcastMessage("§3§lCHAT§f O chat foi §c§lDESABILITADO");
				} else {
					sender.sendMessage("§3§lCHAT§f O chat já está §c§lDESABILITADO");
				}
			} else if (args[0].equalsIgnoreCase("on")) {
				if (ChatAPI.getInstance().getChatState() != ChatState.ENABLED) {
					ChatAPI.getInstance().setChatState(ChatState.ENABLED);
					Bukkit.broadcastMessage("§3§lCHAT§f O chat foi §a§lHABILITADO");
				} else {
					sender.sendMessage("§3§lCHAT§f O chat já está §a§lHABILITADO");
				}
			} else {
				sender.sendMessage("§3§lCHAT§f Utilize: /chat <on, off, clear>");
			}
		}
	}
	
	@Command(name = "clearchat", aliases = { "limparchat", "cc" }, groupToUse = Group.TRIAL)
	public void clearchat(BukkitCommandSender sender, String label, String[] args) {
		for (int i = 0; i < 100; i++) {
			Bukkit.broadcastMessage("§f");
		}
		Bukkit.broadcastMessage("§3§lCHAT§f O chat foi limpo com sucesso por §a§l" + sender.getName());
	}
}
