package br.com.weavenmc.commons.bungee.command.register;

import java.util.ArrayList;
import java.util.List;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.command.BungeeCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.punish.AccountBan;
import br.com.weavenmc.commons.core.data.punish.IpBan;
import br.com.weavenmc.commons.core.data.punish.MacBan;
import br.com.weavenmc.commons.core.data.punish.Mute;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.profile.Profile;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.commons.core.twitter.Twitter;
import br.com.weavenmc.commons.core.twitter.TwitterAccount;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.commons.util.string.StringUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import twitter4j.TwitterException;

public class PunishCommand implements CommandClass {

	@Command(name = "unban", aliases = { "desbanir", "pardon" }, groupToUse = Group.ADMIN, runAsync = true)
	public void unban(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§3§lUNBAN§f Utililize: §b§l/unban§f <player> <reason ...>");
		} else {
			Profile profile = WeavenMC.getProfileCommon().getProfile(args[0]);
			if (profile != null) {
				ProxyPlayer pP = new ProxyPlayer(profile.getId(), profile.getName());
				if (pP.load(DataCategory.CONNECTION)) {
					boolean atLeastOneBan = false;

					AccountBan ban = WeavenMC.getPunishHistory().getAccountBan(pP.getUniqueId());
					if (ban != null) {
						atLeastOneBan = true;
					}

					IpBan ipBan = null;
					if (!pP.getIpAddress().isEmpty()) {
						ipBan = WeavenMC.getPunishHistory().getIpBan(pP.getIpAddress());
						if (ipBan != null && !atLeastOneBan) {
							atLeastOneBan = true;
						}
					}

					MacBan macBan = null;
					if (!pP.getComputerAddress().isEmpty()) {
						macBan = WeavenMC.getPunishHistory().getMacBan(pP.getComputerAddress());
						if (macBan != null && !atLeastOneBan) {
							atLeastOneBan = true;
						}
					}

					if (atLeastOneBan) {
						if (ban != null) {
							if (!WeavenMC.getPunishHistory().unbanAccount(ban)) {
								sender.sendMessage(
										"§3§lUNBAN§f Não foi possível desbanir a conta, tente novamente mais tarde.");
								return;
							}
						}

						if (ipBan != null) {
							if (!WeavenMC.getPunishHistory().unbanIp(ipBan)) {
								sender.sendMessage(
										"§3§lUNBAN§f Não foi possível desbanir o ip da conta, tente novamente mais tarde.");
								return;
							}
						}

						if (macBan != null) {
							if (!WeavenMC.getPunishHistory().unbanMac(macBan)) {
								sender.sendMessage(
										"§3§lUNBAN§f Não foi possivel desbanir o ip mac da conta, tente novamente mais tarde.");
								return;
							}
						}

						String unbanReason = StringUtils.createArgs(1, args, "Unbanned", false);

						sender.sendMessage("§3§lUNBAN§f Você §b§lDESBANIU§f o jogador " + pP.getName() + "("
								+ pP.getUniqueId() + ")");

						for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
							ProxyPlayer p = ProxyPlayer.getPlayer(o.getUniqueId());
							if (p == null)
								continue;
							if (!p.isStaffer())
								continue;
							o.sendMessage(TextComponent.fromLegacyText("§3§lUNBAN§f O jogador " + pP.getName() + "("
									+ pP.getUniqueId() + ") foi §b§lDESBANIDO§f por " + sender.getName() + "! Motivo: "
									+ unbanReason));
						}
					} else {
						sender.sendMessage("§3§lUNBAN§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
								+ ") não possui nenhum §b§lBANIMENTO ATIVO.");
					}
				} else {
					sender.sendMessage(
							"§3§lUNBAN§f Não foi possível carregar dados da conta banida, tente novamente mais tarde.");
				}
			} else {
				sender.sendMessage("§3§lUNBAN§f Esta conta não possui nenhum perfil registrado no servidor.");
			}
		}
	}

	@Command(name = "computerban", aliases = { "macban" }, groupToUse = Group.TRIAL, runAsync = true)
	public void computerban(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§4§lMACBAN§f Utilize: §4§l/macban§f <player> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;
			String serverName = null;

			if (p != null) {
				pP = ProxyPlayer.getPlayer(p.getUniqueId());
				serverName = p.getServer().getInfo().getName().toLowerCase() + ".mc-weaven.com.br";
			} else {
				Profile profile = WeavenMC.getProfileCommon().getProfile(args[0]);
				if (profile != null) {
					pP = new ProxyPlayer(profile.getId(), profile.getName());
				}
			}

			if (serverName == null) {
				if (sender.isPlayer()) {
					serverName = sender.getPlayer().getServer().getInfo().getName().toLowerCase() + ".mc-weaven.com.br";
				} else {
					serverName = "informação não disponivel";
				}
			}

			if (sender.isPlayer()) {
				ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
				if (!player.hasGroupPermission(pP.getGroup())) {
					sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
					return;
				}
			}

			if (pP != null) {
				if (p == null) {
					if (!pP.load(DataCategory.CONNECTION)) {
						sender.sendMessage(
								"§4§lMACBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
						return;
					}
				}

				String ipAddress = pP.getComputerAddress();
				if (ipAddress.isEmpty()) {
					sender.sendMessage(
							"§4§lMACBAN§f Este jogador não possui nenhum endereço mac registrado no servidor, provavelmente nunca o acessou.");
					return;
				}

				MacBan ban = WeavenMC.getPunishHistory().getMacBan(ipAddress);
				if (ban != null) {
					sender.sendMessage("§4§lMACBAN§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
							+ ") já possui o endereço de ip banido do servidor. Caso o mesmo entre novamente, repita o processo, pois o ip da conta terá sido atualizado.");
					return;
				}

				String reason = StringUtils.createArgs(1, args, "não informado", false);
				ban = new MacBan(ipAddress, sender.getName(), reason, System.currentTimeMillis());

				if (WeavenMC.getPunishHistory().banMac(ban)) {
					for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
						ProxyPlayer staffer = ProxyPlayer.getPlayer(o.getUniqueId());
						if (staffer == null)
							continue;
						if (!staffer.isStaffer())
							continue;

						o.sendMessage(TextComponent.fromLegacyText(
								"§4§lMACBAN§f O jogador " + pP.getName() + "(" + pP.getUniqueId() + ") [" + ipAddress
										+ "] foi §c§lBANIDO§f por " + sender.getName() + "! Motivo: " + reason));
					}

					BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
							"§fO jogador " + pP.getName() + " foi §c§lBANIDO PERMANENTEMENTE§f do servidor."));

					if (p != null) {
						p.disconnect(TextComponent.fromLegacyText(
								"§6§lHYPE§f§lMC" + "\n\n" + "§7Você está permanentemente banido do servidor.\n"
										+ "§cMotivo: §7" + ban.getReason() + "\n" + "§cBanido por: §7"
										+ sender.getName() + "" + "\n\n" + "§cAdquira Unban em: loja.hypemc.com.br\n"));
					}

					try {
						Twitter.tweet(TwitterAccount.WEAVENBANS, "Jogador banido: " + pP.getName() + "\nBanido por: "
								+ sender.getName() + "\nMotivo: " + ban.getReason() + "\n\nServidor: " + serverName);
					} catch (TwitterException ex) {
						WeavenMC.debug("Não foi possivel atualizar o Twitter de Banimentos");
					}
				} else {
					sender.sendMessage("§4§lMACBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			} else {
				sender.sendMessage(
						"§4§lMACBAN§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Command(name = "ipban", aliases = { "addressban" }, groupToUse = Group.TRIAL, runAsync = true)
	public void ipban(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§4§lIPBAN§f Utilize: §4§l/ipban§f <player> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;
			String serverName = null;

			if (p != null) {
				pP = ProxyPlayer.getPlayer(p.getUniqueId());
				serverName = p.getServer().getInfo().getName().toLowerCase() + ".mc-weaven.com.br";
			} else {
				Profile profile = WeavenMC.getProfileCommon().getProfile(args[0]);
				if (profile != null) {
					pP = new ProxyPlayer(profile.getId(), profile.getName());
				}
			}

			if (serverName == null) {
				if (sender.isPlayer()) {
					serverName = sender.getPlayer().getServer().getInfo().getName().toLowerCase() + ".mc-weaven.com.br";
				} else {
					serverName = "informação não disponivel";
				}
			}

			if (pP != null) {
				if (p == null) {
					if (!pP.load(DataCategory.ACCOUNT, DataCategory.CONNECTION)) {
						sender.sendMessage(
								"§4§lIPBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
						return;
					}
				}

				pP.organizeGroups();

				if (sender.isPlayer()) {
					ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
					if (!player.hasGroupPermission(pP.getGroup())) {
						sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
						return;
					}
				}

				String ipAddress = pP.getIpAddress();
				if (ipAddress.isEmpty()) {
					sender.sendMessage(
							"§4§lIPBAN§f Este jogador não possui nenhum endereço de ip registrado no servidor, provavelmente nunca o acessou.");
					return;
				}

				IpBan ban = WeavenMC.getPunishHistory().getIpBan(ipAddress);
				if (ban != null) {
					sender.sendMessage("§4§lIPBAN§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
							+ ") já possui o endereço de ip banido do servidor. Caso o mesmo entre novamente, repita o processo, pois o ip da conta terá sido atualizado.");
					return;
				}

				String reason = StringUtils.createArgs(1, args, "não informado", false);
				ban = new IpBan(ipAddress, sender.getName(), reason, System.currentTimeMillis());

				if (WeavenMC.getPunishHistory().banIp(ban)) {
					for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
						ProxyPlayer staffer = ProxyPlayer.getPlayer(o.getUniqueId());
						if (staffer == null)
							continue;
						if (!staffer.isStaffer())
							continue;

						o.sendMessage(TextComponent.fromLegacyText(
								"§4§lIPBAN§f O jogador " + pP.getName() + "(" + pP.getUniqueId() + ") [" + ipAddress
										+ "] foi §c§lBANIDO§f por " + sender.getName() + "! Motivo: " + reason));
					}

					BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
							"§fO jogador " + pP.getName() + " foi §c§lBANIDO PERMANENTEMENTE§f do servidor."));

					if (p != null) {
						p.disconnect(TextComponent.fromLegacyText(
								"§6§lHYPE§f§lMC" + "\n\n" + "§7Você está permanentemente banido do servidor.\n"
										+ "§cMotivo: §7" + ban.getReason() + "\n" + "§cBanido por: §7"
										+ sender.getName() + "" + "\n\n" + "§cAdquira Unban em: loja.hypemc.com.br\n"));
					}

					try {
						Twitter.tweet(TwitterAccount.WEAVENBANS, "Jogador banido: " + pP.getName() + "\nBanido por: "
								+ sender.getName() + "\nMotivo: " + ban.getReason() + "\n\nServidor: " + serverName);
					} catch (TwitterException ex) {
						WeavenMC.debug("Não foi possivel atualizar o Twitter de Banimentos");
					}
				} else {
					sender.sendMessage("§4§lIPBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			} else {
				sender.sendMessage(
						"§4§lIPBAN§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Command(name = "ban", aliases = { "banir", "punish", "punir" }, groupToUse = Group.TRIAL, runAsync = true)
	public void ban(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§4§lBAN§f Utilize: §4§l/ban§f <player> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;
			Profile profile = null;
			String serverName = null;

			if (p != null) {
				profile = new Profile(p.getName(), p.getUniqueId());
				serverName = p.getServer().getInfo().getName().toLowerCase() + ".hypemc.com.br";
			} else {
				profile = WeavenMC.getProfile(args[0]);
			}

			if (serverName == null) {
				if (sender.isPlayer()) {
					serverName = sender.getPlayer().getServer().getInfo().getName().toLowerCase() + ".hypemc.com.br";
				} else {
					serverName = "informação não disponivel";
				}
			}

			if (profile != null) {
				AccountBan ban = WeavenMC.getPunishHistory().getAccountBan(profile.getId());
				if (ban != null) {
					sender.sendMessage("§4§lBAN§f O jogador " + profile.getName() + "(" + profile.getId()
							+ ") já possui um §c§lBANIMENTO ATIVO.");
					return;
				} else {
					pP = new ProxyPlayer(profile.getId(), profile.getName());
					if (!pP.load(DataCategory.ACCOUNT)) {
						sender.sendMessage(
								"§4§lBAN§f Erro ao tentar efetuar esta operação, tente novamente mais tarde.");
						return;
					}
					pP.organizeGroups();
					if (sender.isPlayer()) {
						ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
						if (!player.hasGroupPermission(pP.getGroup())) {
							sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
							return;
						}
					}

					String reason = StringUtils.createArgs(1, args, "não informado", false);
					if (reason.equalsIgnoreCase("cheating")) {
						reason = "Uso de hack";
					}
					ban = new AccountBan(profile.getId(), sender.getName(), reason, System.currentTimeMillis(), -1);

					if (WeavenMC.getPunishHistory().banAccount(ban)) {
						for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
							ProxyPlayer staffer = ProxyPlayer.getPlayer(o.getUniqueId());
							if (staffer == null)
								continue;
							if (!staffer.isStaffer())
								continue;

							o.sendMessage(TextComponent
									.fromLegacyText("§4§lBAN§f O jogador " + profile.getName() + "(" + profile.getId()
											+ ") foi §c§lBANIDO§f por " + sender.getName() + "! Motivo: " + reason));
						}

						BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
								"§fO jogador " + profile.getName() + " foi §c§lBANIDO PERMANENTEMENTE§f do servidor."));

						if (p != null) {
							p.disconnect(TextComponent.fromLegacyText("§6§lHYPE§f§lMC" + "\n\n"
									+ "§7Você está permanentemente banido do servidor.\n" + "§cMotivo: §7"
									+ ban.getReason() + "\n" + "§cBanido por: §7" + sender.getName() + "" + "\n\n"
									+ "§cAdquira Unban em: loja.hypemc.com.br\n"));
						}

						try {
							Twitter.tweet(TwitterAccount.WEAVENBANS,
									"Jogador banido: " + profile.getName() + "\nBanido por: " + sender.getName()
											+ "\nMotivo: " + ban.getReason() + "\n\nServidor: " + serverName);
						} catch (TwitterException ex) {
							WeavenMC.debug("Não foi possivel atualizar o Twitter de Banimentos");
						}
					} else {
						sender.sendMessage("§4§lBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
					}
				}
			} else {
				sender.sendMessage(
						"§4§lBAN§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Command(name = "tempban", aliases = { "tempbanir" }, groupToUse = Group.INVESTIDOR, runAsync = true)
	public void tempban(BungeeCommandSender sender, String label, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage("§c§lTEMPBAN§f Utilize: §c§l/tempban§f <player> <time> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			Profile profile = null;
			String serverName = null;

			if (p != null) {
				profile = new Profile(p.getName(), p.getUniqueId());
				serverName = p.getServer().getInfo().getName().toLowerCase() + ".mc-hype.com.br";
			} else {
				profile = WeavenMC.getProfile(args[0]);

			}

			if (serverName == null) {
				if (sender.isPlayer()) {
					serverName = sender.getPlayer().getServer().getInfo().getName().toLowerCase() + ".mc-weaven.com.br";
				} else {
					serverName = "informação não disponivel";
				}
			}

			if (sender.isPlayer()) {
				ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
				ProxyPlayer pP = new ProxyPlayer(profile.getId(), profile.getName());
				if (!player.hasGroupPermission(pP.getGroup())) {
					sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
					return;
				}
			}

			if (profile != null) {
				AccountBan ban = WeavenMC.getPunishHistory().getAccountBan(profile.getId());
				if (ban != null) {
					sender.sendMessage("§c§lTEMPBAN§f O jogador " + profile.getName() + "(" + profile.getId()
							+ ") já possui um §c§lBANIMENTO ATIVO.");
					return;
				} else {
					String reason = StringUtils.createArgs(2, args, "não informado", false);

					final long time;

					try {
						time = StringTimeUtils.parseDateDiff(args[1], true);
					} catch (Exception ex) {
						sender.sendMessage("§c§lTEMPBAN§f " + ex.getMessage());
						return;
					}

					ban = new AccountBan(profile.getId(), sender.getName(), reason, System.currentTimeMillis(), time);

					if (WeavenMC.getPunishHistory().banAccount(ban)) {
						for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
							ProxyPlayer staffer = ProxyPlayer.getPlayer(o.getUniqueId());
							if (staffer == null)
								continue;
							if (!staffer.isStaffer())
								continue;

							o.sendMessage(TextComponent.fromLegacyText(
									"§c§lTEMPBAN§f O jogador " + profile.getName() + "(" + profile.getId()
											+ ") foi §c§lBANIDO§f por " + sender.getName() + "! Motivo: §c" + reason
											+ "§f durante " + StringTimeUtils.formatDifference(time)));
						}

						BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
								"§fO jogador " + profile.getName() + " foi §c§lBANIDO TEMPORARIAMENTE§f do servidor."));

						if (p != null) {
							p.disconnect(TextComponent.fromLegacyText("§6§lHYPE§f§lMC" + "\n\n"
									+ "§7Você está temporariamente banido do servidor.\n" + "§cMotivo: §7"
									+ ban.getReason() + "\n" + "§cBanido por: §7" + sender.getName() + "\n"
									+ "§cTempo restante: §7" + StringTimeUtils.formatDifference(time) + "" + "\n\n"
									+ "§cAdquira Unban em: loja.hypemc.com.br\n"));
						}

						try {
							Twitter.tweet(TwitterAccount.WEAVENBANS,
									"Jogador banido: " + profile.getName() + "\nBanido por: " + sender.getName()
											+ "\nMotivo: " + ban.getReason() + "\n\nServidor: " + serverName);
						} catch (TwitterException ex) {
							WeavenMC.debug("Não foi possivel atualizar o Twitter de Banimentos");
						}
					} else {
						sender.sendMessage(
								"§c§lTEMPBAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
					}
				}
			} else {
				sender.sendMessage(
						"§c§lTEMPBAN§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Command(name = "mute", aliases = { "mutar" }, groupToUse = Group.INVESTIDOR, runAsync = true)
	public void mute(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§3§lMUTE§f Utilize: §b§l/mute§f <player> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;

			if (p != null) {
				pP = ProxyPlayer.getPlayer(p.getUniqueId());
			} else {
				Profile profile = WeavenMC.getProfile(args[0]);
				if (profile != null) {
					pP = new ProxyPlayer(profile.getId(), profile.getName());
				}
			}

			if (pP != null) {
				if (sender.isPlayer()) {
					ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
					if (!player.hasGroupPermission(pP.getGroup())) {
						sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
						return;
					}
				}
				Mute mute = WeavenMC.getPunishHistory().getMute(pP.getUniqueId());
				if (mute != null) {
					sender.sendMessage("§3§lMUTE§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
							+ ") já possui um §b§lMUTE ATIVO.");
					return;
				}

				String reason = StringUtils.createArgs(1, args, "não informado", false);
				mute = new Mute(pP.getUniqueId(), sender.getName(), -1, reason, System.currentTimeMillis());

				if (WeavenMC.getPunishHistory().mute(mute)) {

					for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
						ProxyPlayer t = ProxyPlayer.getPlayer(o.getUniqueId());
						if (t == null)
							continue;
						if (!t.isStaffer())
							continue;

						o.sendMessage(TextComponent
								.fromLegacyText("§3§lMUTE§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
										+ ") foi §3§lMUTADO§f por " + sender.getName() + "! Motivo: " + reason));
					}

					if (p != null) {
						pP.setRecentMute(mute);
						p.sendMessage(TextComponent.fromLegacyText("\n" + "§4Você está permanentemente mutado.\n"
								+ "§fVocê pode ser desmutado adquirindo §bUNMUTE §fna nossa loja: loja.hypemc.com.br\n"));
					}

					BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
							"§fO jogador " + pP.getName() + " foi §3§lPERMANENTEMENTE MUTADO§f no servidor."));
				} else {
					sender.sendMessage("§3§lMUTE§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			} else {
				sender.sendMessage(
						"§3§lMUTE§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Command(name = "unmute", aliases = { "desmutar" }, groupToUse = Group.MODPLUS, runAsync = true)
	public void unmute(BungeeCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§5§lUNMUTE§f Utilize: §d§l/unmute§f <nick>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;

			if (p != null) {
				pP = ProxyPlayer.getPlayer(p.getUniqueId());
			} else {
				Profile profile = WeavenMC.getProfile(args[0]);
				pP = new ProxyPlayer(profile.getId(), profile.getName());
			}

			if (pP != null) {
				Mute mute = WeavenMC.getPunishHistory().getMute(pP.getUniqueId());
				if (mute != null) {
					if (WeavenMC.getPunishHistory().pardonMute(pP.getUniqueId())) {
						for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
							ProxyPlayer t = ProxyPlayer.getPlayer(o.getUniqueId());
							if (t == null)
								continue;
							if (!t.isStaffer())
								continue;
							if (t.getServerConnectedType() == ServerType.LOGIN)
								continue;
							o.sendMessage(TextComponent.fromLegacyText("§5§lUNMUTE§f " + pP.getName() + "("
									+ pP.getUniqueId() + ") foi §5§lDESMUTADO§f por " + sender.getName() + "."));
						}
						if (p != null) {
							if (pP.getRecentMute() != null) {
								pP.setRecentMute(null);
								p.sendMessage(
										TextComponent.fromLegacyText("§5§lUNMUTE§f O seu mute foi §d§lREVOGADO!"));
							}
						}
					} else {
						sender.sendMessage(
								"§5§lUNMUTE§f Erro ao tentar efetuar a operaçao, tente novamente mais tarde.");
					}
				} else {
					sender.sendMessage("§5§lUNMUTE§f O jogador " + args[1] + " não está §c§lMUTADO.");
				}
			} else {
				sender.sendMessage("§5§lUNMUTE§f Esta conta não possui nenhum perfil registrado no servidor.");
			}
		}
	}

	@Command(name = "tempmute", aliases = { "tempmutar" }, groupToUse = Group.TRIAL, runAsync = true)
	public void tempmute(BungeeCommandSender sender, String label, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage("§3§lTEMPMUTE§f Utilize: §b§l/tempmute§f <player> <time> <reason>");
		} else {
			ProxiedPlayer p = BungeeCord.getInstance().getPlayer(args[0]);
			ProxyPlayer pP = null;

			if (p != null) {
				pP = ProxyPlayer.getPlayer(p.getUniqueId());
			} else {
				Profile profile = WeavenMC.getProfile(args[0]);
				if (profile != null) {
					pP = new ProxyPlayer(profile.getId(), profile.getName());
				}
			}

			if (pP != null) {
				Mute mute = WeavenMC.getPunishHistory().getMute(pP.getUniqueId());
				if (mute != null) {
					sender.sendMessage("§3§lMUTE§f O jogador " + pP.getName() + "(" + pP.getUniqueId()
							+ ") já possui um §b§lMUTE ATIVO.");
					return;
				}

				String reason = StringUtils.createArgs(2, args, "não informado", false);

				final long time;

				try {
					time = StringTimeUtils.parseDateDiff(args[1], true);
				} catch (Exception ex) {
					sender.sendMessage("§3§lTEMPMUTE§f " + ex.getMessage());
					return;
				}
				if (sender.isPlayer()) {
					ProxyPlayer player = ProxyPlayer.getPlayer(sender.getUniqueId());
					if (!player.hasGroupPermission(pP.getGroup())) {
						sender.sendMessage("§4§lERRO§f Você não pode §c§lBANIR§f este jogador!");
						return;
					}
				}
				mute = new Mute(pP.getUniqueId(), sender.getName(), time, reason, System.currentTimeMillis());

				if (WeavenMC.getPunishHistory().mute(mute)) {
					sender.sendMessage(
							"§3§lTEMPMUTE§f Você §b§lMUTOU§f o jogador " + pP.getName() + "(" + pP.getUniqueId()
									+ ") por " + reason + " durante " + StringTimeUtils.formatDifference(time));

					for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
						ProxyPlayer t = ProxyPlayer.getPlayer(o.getUniqueId());
						if (t == null)
							continue;
						if (!t.isStaffer())
							continue;

						o.sendMessage(TextComponent.fromLegacyText("§3§lTEMPMUTE§f O jogador " + pP.getName() + "("
								+ pP.getUniqueId() + ") foi §3§lMUTADO§f por " + sender.getName() + "! Motivo: §b"
								+ reason + "§f durante " + StringTimeUtils.formatDifference(time)));
					}

					if (p != null) {
						pP.setRecentMute(mute);
						p.sendMessage(TextComponent.fromLegacyText("\n§4Você está temporariamente mutado.\n"
								+ "§fTempo restante: §e" + StringTimeUtils.formatDifference(mute.getMuteTime()) + "\n"
								+ "§fVocê pode ser desmutado adquirindo §bUNMUTE §fna nossa loja: loja.hypemc.com.br\n"));
					}

					BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(
							"§fO jogador " + pP.getName() + " foi §3§lTEMPORARIAMENTE MUTADO§f no servidor."));
				} else {
					sender.sendMessage("§3§lTEMPMUTE§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			} else {
				sender.sendMessage(
						"§3§lTEMPMUTE§f Este jogador não é original, não possui registros na máquina e o atual sistema não possui suporte para punir contas piratas que nunca acessaram o servidor."
								+ " §cCertifique-se de que o nick indicado não é do comando /fake, e se for indique o nick real");
			}
		}
	}

	@Completer(name = "ban", aliases = { "ipban", "addressban", "computerban", "macban", "banir", "tempbanir", "mute",
			"mutar", "tempmute", "tempmutar" })
	public List<String> bancompleter(ProxiedPlayer p, String label, String[] args) {
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

	@Completer(name = "tempban", aliases = { "tempbanir" })
	public List<String> tempbancompleter(ProxiedPlayer p, String label, String[] args) {
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
