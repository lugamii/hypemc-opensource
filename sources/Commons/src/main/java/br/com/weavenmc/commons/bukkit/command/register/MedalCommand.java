package br.com.weavenmc.commons.bukkit.command.register;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.account.Medal;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class MedalCommand implements CommandClass {

	@Command(name = "medal", aliases = { "medalha" }, runAsync = true)
	public void medal(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
			if (args.length == 0) {

				TextComponent txtComponent = new TextComponent();
				TextComponent txtComponent2 = new TextComponent();
				txtComponent.setText("§aSuas medalhas: ");
				txtComponent2.setText("§7Nenhum§f, ");
				txtComponent2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
						TextComponent.fromLegacyText("§7Nenhum\n§f\n§fClique para selecionar!")));
				txtComponent2.setClickEvent(
						new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/medal remover"));
				TextComponent txt;
				int sla = 0;
				for (Medal medal : Medal.values()) {
					if (bP.hasGroupPermission(medal.getGroupToUse())) {
						String start = "";
						if (sla != 0)
							start = "§f, ";
						sla++;
 						txt = new TextComponent();
						txt.setText(start + medal.getColor() + medal.getSymbol());
						String medalha = medal.toString().toUpperCase();
						
						Character.toUpperCase(medalha.charAt(0));

						txt.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent
								.fromLegacyText(medal.getColor() + medalha + "\n§f\n§fClique para selecionar!")));
						txt.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
								"/medal " + medal.getNames()[0]));
						txtComponent.addExtra(txt);
					}
				}

				p.spigot().sendMessage(txtComponent);

			} else if (args[0].equalsIgnoreCase("remover") || args[0].equalsIgnoreCase("tirar")) {
				if (BukkitMain.getInstance().getTagManager().isUsingMedal(p)) {
					BukkitMain.getInstance().getTagManager().removeMedal(p);
					p.sendMessage("§6§lMEDALHAS§f Você §c§lREMOVEU§f a sua medalha.");
				} else {
					p.sendMessage("§6§lMEDALHAS§f Você não está §3§lUTILIZANDO§f nenhuma medalha.");
				}
			} else {
				Medal medal = Medal.fromString(args[0]);
				if (medal != null) {
					if (bP.hasGroupPermission(medal.getGroupToUse())) {
						BukkitMain.getInstance().getTagManager().setMedal(p, medal);
						p.sendMessage("§6§lMEDALHAS§f Você agora está §3§lUTILIZANDO§f a medalha " + medal.getColor()
								+ "§l" + medal.getSymbol() + " " + medal.name().replace("YIN_YANG", "YING&YANG"));
					} else {
						p.sendMessage("§6§lMEDALHAS§f Você precisa possuir o grupo "
								+ medal.getGroupToUse().getTagToUse().getName() + "§f para usar esta medalha!");
					}
				} else {
					p.sendMessage("§6§lMEDALHAS§f A medalha " + args[0] + " não foi encontrada.");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
}
