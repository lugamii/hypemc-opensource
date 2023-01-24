package br.com.weavenmc.commons.bungee.command.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.command.BungeeCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.commons.util.string.StringUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportCommand implements CommandClass {

	private HashMap<UUID, Long> reportCooldown = new HashMap<>();

	@Command(name = "report", aliases = { "reportar", "denunciar", "denuncia" })
	public void report(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer reporter = sender.getPlayer();
			if (args.length <= 1) {
				sender.sendMessage("§9§lREPORT§f Utilize: §3§l/report§f <jogador> <motivo>");
			} else {
				ProxiedPlayer reported = BungeeCord.getInstance().getPlayer(args[0]);
				if (StaffCommand.hasMute(reporter)) {
					sender.sendMessage("§c§lMUTE§f Você está §c§lTEMPORARIAMENTE§f impossibilitado de §c§l§oREPORTAR!");
					return;
				}
				if (reported != null) {
					if (reported.getUniqueId() != reporter.getUniqueId()) {
						if (!hasCooldown(reporter)) {
							addCooldown(reporter);

							String reporterName = reporter.getName();
							String reportedName = reported.getName();
							ServerInfo serverInfo = reported.getServer().getInfo();
							String reportMessage = StringUtils.createArgs(1, args, "", false);

							ProxyPlayer reporterAcc = ProxyPlayer.getPlayer(reporter.getUniqueId());
							ProxyPlayer reportedAcc = ProxyPlayer.getPlayer(reported.getUniqueId());

							for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
								ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
								if (online == null)
									continue;
								if (online.getServerConnectedType().equals(ServerType.LOGIN))
									continue;
								if (!online.isStaffer())
									continue;
								if (!online.isReceiveReportEnabled())
									continue;
								
								TextComponent component = new TextComponent(
										"§9§lREPORT§f O player " + reported.getName() + " foi REPORTADO!");
								if (o.getServer().getInfo().equals(serverInfo)) {
									component.setClickEvent(
											new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reportedName));
									component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("§9§lREPORT\n\n§fJogador reportado:\n§fNick: §3"
													+ reportedName + "\n§fGrupo: "
													+ reportedAcc.getGroup().getTagToUse().getName() + "\n§fMotivo: §3"
													+ reportMessage + "\n§fServidor: §3"
													+ serverInfo.getName().toUpperCase()
													+ "\n\n§fQuem reportou:\n§fNick: §3" + reporterName + "\n§fGrupo: "
													+ reporterAcc.getGroup().getTagToUse().getName()
													+ "\n\n§3[Clique para teleportar]").create()));
								} else {
									component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/connect " + serverInfo.getName()));
									component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("§9§lREPORT\n\n§fJogador reportado:\n§fNick: §3"
													+ reportedName + "\n§fGrupo: "
													+ reportedAcc.getGroup().getTagToUse().getName() + "\n§fMotivo: §3"
													+ reportMessage + "\n§fServidor: §3"
													+ serverInfo.getName().toUpperCase()
													+ "\n\n§fQuem reportou:\n§fNick: §3" + reporterName + "\n§fGrupo: "
													+ reporterAcc.getGroup().getTagToUse().getName()
													+ "\n\n§3[Clique para conectar]").create()));
								}
								o.sendMessage(component);
							}
							sender.sendMessage("§9§lREPORT§f Sua §3§lDENUNCIA§f foi enviada com §a§lSUCESSO!");
						} else {
							sendCooldown(reporter);
						}
					} else {
						sender.sendMessage("§9§lREPORT§f Indique outro jogador para §3§lDENUNCIAR!");
					}
				} else {
					sender.sendMessage("§9§lREPORT§f O jogador §3§l" + args[0] + "§f não foi encontrado.");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Completer(name = "report", aliases = { "reportar", "denunciar", "denuncia" })
	public List<String> reportcompleter(ProxiedPlayer p, String label, String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 1) {
			for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
				if (o.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(o.getName());
				}
			}
		}
		return list;
	}

	private void sendCooldown(ProxiedPlayer player) {
		String cooldown = StringTimeUtils
				.toMillis((double) (reportCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 10 / 100);
		player.sendMessage(
				TextComponent.fromLegacyText("§9§lREPORT§f Aguarde §3§l" + cooldown + "§f para reportar novamente!"));

	}

	private boolean hasCooldown(ProxiedPlayer player) {
		return reportCooldown.containsKey(player.getUniqueId())
				&& reportCooldown.get(player.getUniqueId()) >= System.currentTimeMillis();
	}

	private void addCooldown(ProxiedPlayer player) {
		reportCooldown.put(player.getUniqueId(), (30 * 1000L + System.currentTimeMillis()));
	}
}
