package br.com.weavenmc.commons.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.Tag;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TagCommand implements CommandClass {

	@Command(name = "tag", runAsync = true)
	public void tag(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			League league = player.getLeague();
			player.updateTags();
			if (args.length == 0) {
				int max = player.getTags().size() * 2;
				TextComponent[] message = new TextComponent[max];
				message[0] = new TextComponent("§aSuas §ftags§a: ");
				int i = max - 1;
				for (Tag t : player.getTags()) {
					if (i < max - 1) {
						message[i] = new TextComponent("§f, ");
						i -= 1;
					}
					TextComponent component = new TextComponent(t.getName());
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new TextComponent[] { new TextComponent("§7Prévia: " + t.getPrefix() + p.getName() + " §7("
									+ league.getColor() + league.getSymbol() + "§7)") }));
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + t.name()));
					message[i] = component;
					i -= 1;
					component = null;
				}
				p.spigot().sendMessage(message);
				message = null;
			} else {
				Tag tag = Tag.fromString(args[0]);
				if (tag != null) {
					if (player.getTags().contains(tag)) {
						if (!BukkitMain.getInstance().getTagManager().currentTag(p, tag)) {
							BukkitMain.getInstance().getTagManager().setTag(p, tag);
							p.sendMessage("§9§lTAGS§f Você agora está §3§lUTILIZANDO§f a tag " + tag.getName());
						} else {
							p.sendMessage("§9§lTAGS§f Você já está §3§lUTILIZANDO§f a tag " + tag.getName());
						}
					} else {
						p.sendMessage("§9§lTAGS§f Você não possui a tag " + tag.getName());
					}
				} else {
					p.sendMessage("§9§lTAGS§f A tag " + args[0] + " não foi encontrada!");
				}
			}
			league = null;
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
	
	@Completer(name = "tag")
	public List<String> tagcompleter(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			player.updateTags();
			if (args.length == 1) {
				List<String> list = new ArrayList<>();
				for (Tag t : player.getTags())
					list.add(t.name());
				return list;
			}
		}
		return new ArrayList<>();
	}
}
