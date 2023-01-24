package br.com.weavenmc.commons.bungee.command.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.command.BungeeCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffCommand implements CommandClass {

	@Command(name = "pull", groupToUse = Group.DONO)
	public void pull(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("Utilize: /pull <server>");
			} else {
				ServerInfo s = ProxyServer.getInstance().getServerInfo(args[0]);

				if (s != null) {
					Queue<ProxiedPlayer> list = new ConcurrentLinkedQueue<>();
					for (ProxiedPlayer o : ProxyServer.getInstance().getPlayers()) {
						if (o.getServer().getInfo().equals(s))
							continue;

						list.add(o);
					}
					while (!list.isEmpty()) {
						list.poll().connect(s);
					}

					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());

						if (pP == null)
							continue;

						if (!pP.isStaffer())
							continue;

						p.sendMessage(TextComponent.fromLegacyText(
								sender.getName() + " puxou todos os jogadores para o servidor " + args[0]));
					}
				} else {
					sender.sendMessage("Este servidor não existe");
				}
			}
		}
	}

	@Command(name = "finder", aliases = { "find", "buscar", "go" }, groupToUse = Group.TRIAL)
	public void finder(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§3§lFINDER§f Utilize: §b§l/finder§f <player>");
		} else {
			ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[0]);
			if (t != null) {
				ProxyPlayer target = ProxyPlayer.getPlayer(t.getUniqueId());
				TextComponent playerMessage = new TextComponent(
						target.getGroup().getTagToUse().getPrefix() + target.getName());
				TextComponent space = new TextComponent(" §f- ");
				TextComponent ip = new TextComponent("§9" + t.getServer().getInfo().getName().toUpperCase());
				ip.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + t.getServer().getInfo().getName()));
				ip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new TextComponent[] { new TextComponent("§9Clique para se conectar ate o servidor") }));
				sender.sendMessage(playerMessage, space, ip);
			}
		}
	}

	@Completer(name = "finder", aliases = { "find", "buscar", "go" })
	public List<String> findercompleter(ProxiedPlayer p, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
				if (o.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(o.getName());
				}
			}
		}
		return list;
	}

	@Command(name = "staffsee", aliases = { "stafflist" })
	public void staffsee(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());
			if (player.isStaffer()) {
				for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
					ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
					if (online == null)
						continue;
					if (!online.isStaffer())
						continue;
					TextComponent playerMessage = new TextComponent(
							online.getGroup().getTagToUse().getPrefix() + online.getName());
					TextComponent space = new TextComponent(" §f- ");
					TextComponent ip = new TextComponent("§9" + o.getServer().getInfo().getName().toUpperCase());
					ip.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/connect " + o.getServer().getInfo().getName()));
					ip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new TextComponent[] { new TextComponent("§9Clique para se conectar ate o servidor") }));
					sender.sendMessage(playerMessage, space, ip);
				}
			} else {
				sender.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando!");
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "sc", aliases = { "staffchat", "s" }, groupToUse = Group.INVESTIDOR)
	public void staffchat(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());

			if (player.isStaffChatEnabled()) {
				player.setStaffChatEnabled(false);
				sender.sendMessage("§e§lSTAFFCHAT§f Você §c§lDESABILITOU§f o modo staff-chat.");
			} else {
				player.setStaffChatEnabled(true);
				sender.sendMessage("§e§lSTAFFCHAT§f Você §a§lHABILITOU§f o modo staff-chat.");
			}

		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "silentreports", aliases = { "togglereport" }, groupToUse = Group.YOUTUBERPLUS)
	public void report(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());
			if (player.isStaffer()) {
				if (player.isReceiveReportEnabled()) {
					player.setReceiveReportEnabled(false);
					sender.sendMessage("§3§lREPORT§f Você §c§lDESABILITOU§f as mensagens!");
				} else {
					player.setReceiveReportEnabled(true);
					sender.sendMessage("§3§lREPORT§f Você §a§lHABILITOU§f as mensagens!");
				}
			} else {
				sender.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando!");
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "silent", aliases = { "togglesc" }, groupToUse = Group.YOUTUBERPLUS)
	public void silent(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());
			if (player.isStaffer()) {
				if (player.isStaffChatMessages()) {
					player.setStaffChatMessages(false);
					sender.sendMessage("§e§lSTAFFCHAT§f Você §c§lDESABILITOU§f as mensagens!");
				} else {
					player.setStaffChatMessages(true);
					sender.sendMessage("§e§lSTAFFCHAT§f Você §a§lHABILITOU§f as mensagens!");
				}
			} else {
				sender.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando!");
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	public static HashMap<UUID, Long> reportMute = new HashMap<>();

	public static boolean hasMute(ProxiedPlayer p) {
		if (!reportMute.containsKey(p.getUniqueId()))
			return false;
		return reportMute.get(p.getUniqueId()) >= System.currentTimeMillis();
	}

	@Command(name = "mutereport", groupToUse = Group.TRIAL)
	public void mutereport(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§c§lREPORT§f Utilize: /mutereport <player>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			if (p != null) {
				reportMute.put(p.getUniqueId(), (1200 * 60) * 1000L + System.currentTimeMillis());
				sender.sendMessage(
						"§c§lREPORT§f Você impossibilitou o player §c§l" + p.getName() + "§f de mutar por §c§l1 HORA!");
				p.sendMessage(TextComponent
						.fromLegacyText("§c§lMUTE§f VocÊ foi §c§lIMPOSSIBILITADO§f de reportar por §c§l1 HORA!"));
			} else {
				sender.sendMessage("§c§lREPORT§f O player §c§l" + args[0] + "§f não foi encontrado.");
			}
		}
	}

	@Command(name = "ss", aliases = { "screenshare" }, groupToUse = Group.MODGC)
	public void screenshare(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			if (args.length == 0) {
				sender.sendMessage("§a§lSCREENSHARE§f Utilize: §2§l/ss§f <player>");
			} else {
				ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[0]);
				if (t != null) {
					ProxyPlayer tP = ProxyPlayer.getPlayer(t.getUniqueId());
					if (tP != null) {
						if (tP.getServerConnectedType() != ServerType.SCREENSHARE) {
							ServerInfo ssServer = BungeeMain.getInstance().getServerManager()
									.getAvailableServer(ServerType.SCREENSHARE);
							if (ssServer != null) {
								t.connect(BungeeCord.getInstance().getServerInfo(ssServer.getName()));
								if (!p.getServer().getInfo().getName().equals(ssServer.getName())) {
									p.connect(BungeeCord.getInstance().getServerInfo(ssServer.getName()));
								}
								for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
									ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
									if (!online.isStaffer())
										continue;
									o.sendMessage(TextComponent.fromLegacyText("§a§lSCREENSHARE§f " + t.getName() + "("
											+ t.getUniqueId().toString().replace("-", "")
											+ ") agora está em screenshare via requisiçao de §a§l" + p.getName()));
									online = null;
								}
								ssServer = null;
							} else {
								p.sendMessage(TextComponent.fromLegacyText(
										"§a§lSCREENSHARE§f Nenhum servidor disponivel de §2§lSCREENSHARE§f foi encontrado!"));
							}
						} else {
							ServerInfo lobbyServer = BungeeMain.getInstance().getProxy().getServerInfo("lobby");
							if (lobbyServer != null) {
								t.connect(lobbyServer);
								if (!p.getServer().getInfo().equals(lobbyServer)) {
									p.connect(lobbyServer);
								}
								p.sendMessage(TextComponent
										.fromLegacyText("§a§lSCREENSHARE§f Você §2§lCONCLUIU§f a screenshare"));
								t.sendMessage(TextComponent.fromLegacyText("§a§lSCREENSHARE§f Você foi §2§lLIBERADO"));
							} else {
								p.sendMessage(TextComponent.fromLegacyText(
										"§a§lSCREENSHARE§f Nenhum servidor disponivel de §2§lLOBBY§f foi encontrado"));
							}
						}
						t = null;
					} else {
						t.disconnect(TextComponent.fromLegacyText("§4§lERRO§f Sua conta está com problemas!"));
						sender.sendMessage("§a§lSCREENSHARE§f A conta do jogador estava com §c§lPROBLEMAS!");
					}
				} else {
					p.sendMessage(TextComponent
							.fromLegacyText("§a§lSCREENSHARE§f O jogador " + args[0] + " não foi encontrado!"));
				}
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Completer(name = "ss", aliases = { "screenshare" })
	public List<String> sscompleter(ProxiedPlayer p, String label, String[] args) {
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
}
