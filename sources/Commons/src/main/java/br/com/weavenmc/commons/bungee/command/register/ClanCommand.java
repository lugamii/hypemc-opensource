package br.com.weavenmc.commons.bungee.command.register;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.command.BungeeCommandSender;
import br.com.weavenmc.commons.bungee.party.Party;
import br.com.weavenmc.commons.core.clan.Clan;
import br.com.weavenmc.commons.core.clan.ClanMessage;
import br.com.weavenmc.commons.core.clan.ClanMessage.MessageType;
import br.com.weavenmc.commons.core.clan.ClanRank;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.profile.Profile;
import br.com.weavenmc.commons.util.string.StringUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class ClanCommand implements CommandClass {

	private HashMap<String, HashMap<String, ScheduledTask>> invite = new HashMap<>();

	@Command(name = "clan", aliases = { "gang" }, runAsync = true)
	public void clan(BungeeCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			ProxiedPlayer p = sender.getPlayer();
			ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());
			Clan clan = WeavenMC.getClanCommon().getClanFromName(pP.getClanName());
			if (args.length == 0) {
				if (clan != null) {
					int clanXp = clan.getXp();
					ClanRank rank = ClanRank.fromXp(clanXp);
					sender.sendMessage(
							"§f======= §4§l" + clan.getName() + " §f[§8§l" + clan.getAbbreviation() + "§f] =======");
					sender.sendMessage("");
					sender.sendMessage("§e§lXP§f: " + clanXp);
					sender.sendMessage("§3§lLIGA " + rank.getColor() + rank.name());
					sender.sendMessage("");
					sender.sendMessage("§4§lDONO: " + color(clan.getOwnerName()));
					if (clan.getAdminsSize() > 0) {
						List<String> adminList = clan.getAdminsNamesList();
						StringBuilder adminBuilder = new StringBuilder();
						for (int i = 0; i < adminList.size(); i++)
							adminBuilder.append(color(adminList.get(i)))
									.append((i + 1 >= adminList.size() ? "" : "§f, "));
						sender.sendMessage("§c§lADMINISTRADORES: " + adminBuilder.toString());
					} else {
						sender.sendMessage("§c§lADMINISTRADORES: §7Este Clan não possui administradores");
					}
					if (clan.getMembersSize() > 0) {
						List<String> memberList = clan.getMembersNamesList();
						StringBuilder memberBuilder = new StringBuilder();
						for (int i = 0; i < memberList.size(); i++) {
							memberBuilder.append(color(memberList.get(i)))
									.append((i + 1 >= memberList.size() ? "" : "§f, "));
						}
						sender.sendMessage("§f§lPARTICIPANTES: " + memberBuilder.toString());
					} else {
						sender.sendMessage("§f§lPARTICIPANTES: §7Este Clan não possui participantes");
					}
					sender.sendMessage("");
					sender.sendMessage("§1§lSLOT: §f(" + clan.getActualSlot() + "/" + clan.getMaxSlot() + ")");
					sender.sendMessage("");
					sender.sendMessage("§fPara mais informações digite: §c§l/clan help");
				} else {
					sender.sendMessage(
							"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage("§fSe você já um §4§lDONO§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan promote §f<player> - Você pode promover alguém a §c§lADMINISTRADOR§f do clan, assim ele pode convidar pessoas para seu Clan e ajudar a upá-lo.");
					sender.sendMessage(
							"§3§l/clan demote §f<player> - Você rebaixa um §c§lADMINISTRADOR§f do seu time á §f§lPARTICIPANTE");
					sender.sendMessage(
							"§3§l/clan changeabb §f<Sigla> - Você pode mudar a sigla do seu Clan se ela estiver disponível por §6§l3000 MOEDAS");
					sender.sendMessage(
							"§3§l/clan disband§f - Para você sair do Clan você precisa excluir ele, utilize este comando. §c§lATENÇÃO§f: Você não receberá suas moedas novamente.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é um §c§lADMINISTRADOR§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage("§3§l/clan invite §f<player> - Você pode convidar alguém para entrar no Clan.");
					sender.sendMessage(
							"§3§l/clan removeinvite §f<player> - Você remove o Convite dele, caso seja alguém errado ou não há mais uma proposta.");
					sender.sendMessage(
							"§3§l/clan kick §f<player> - Você pode expulsar um §f§lPARTICIPANTE§f do seu clan, caso ele não seja mais §c§lBEM-VINDO§f.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é apenas um §f§lJOGADOR§f e tem ou não um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan create §f<Nome do Clan> <Sigla> - Você cria um Clan utilizando §6§l6000 MOEDAS§f.");
					sender.sendMessage(
							"§3§l/clan join §f<Nome do Clan> - Você pode entrar em um Clan que alguém tenha criado e convidou você.");
					sender.sendMessage(
							"§3§l/clan chat §f- Você entra no chat do seu Clan que está disponível na rede inteira.");
					sender.sendMessage(
							"§3§l/clan info §f[Nome do Clan] - Você pode receber informações do seu Clan ou definir um Clan para pesquisar.");
					sender.sendMessage(
							"§3§l/clan leave §f- Você vai sair do seu Clan e estará disponível para entrar em outro novamente.");
					sender.sendMessage("§3§l/clan list §f- Veja uma lista de todos os Clans criados na Rede");
					sender.sendMessage("");
					sender.sendMessage("§6§lBOM JOGO");
				} else if (args[0].equalsIgnoreCase("leave")) {
					if (clan != null) {
						if (!clan.isOwner(p.getUniqueId())) {
							clan.kickMember(p.getUniqueId());
							WeavenMC.getClanCommon().updateClan(clan);
							pP.setClanChatEnabled(false);
							pP.setClanName("Nenhum");
							pP.save(DataCategory.ACCOUNT);
							ClanMessage message = new ClanMessage().writeClanName(clan.getName())
									.writeClanTag(clan.getAbbreviation()).writeResponse(sender.getName())
									.writeUserToLeave(sender.getName()).writeType(MessageType.LEAVE);
							WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.CLAN_FIELD_UPDATE,
									WeavenMC.getGson().toJson(message));
							sendClanMessage(clan.getName(), "§4§l[" + clan.getName() + "] §c§l[LEAVE] §fO jogador §c"
									+ sender.getName() + "§f saiu do Clan!");
							sender.sendMessage("§4§lCLAN§f Você saiu do Clan " + clan.getName() + ".");
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Você é o §4§lDONO§f do Clan e não pode sair, digite §c§l/clan help");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
					if (clan != null) {
						if (pP.isClanChatEnabled()) {
							pP.setClanChatEnabled(false);
							sender.sendMessage("§4§lCLAN§f Você §c§lDESATIVOU§f o chat do Clan");
						} else {
							pP.setClanChatEnabled(true);
							sender.sendMessage("§4§lCLAN§f Você §a§lATIVOU§f o chat do Clan");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("disband") || args[0].equalsIgnoreCase("deletar")) {
					if (clan != null) {
						if (clan.isOwner(p.getUniqueId())) {
							if (WeavenMC.getClanCommon().deletClan(clan)) {
								for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
									ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
									if (online == null)
										continue;
									if (!online.getClanName().equalsIgnoreCase(clan.getName()))
										continue;
									o.sendMessage(TextComponent.fromLegacyText("§4§l[" + clan.getName()
											+ "] §c§l[DISBAND] §fO Clan foi §c§lDELETADO§f pelo §4§lDONO"));
									online.setClanName("NRE");
									online.save(DataCategory.ACCOUNT);
								}
								ClanMessage m = new ClanMessage().writeResponse(sender.getName())
										.writeClanName(clan.getName()).writeClanTag(clan.getAbbreviation())
										.writeType(MessageType.DISBAND);
								WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.CLAN_FIELD_UPDATE,
										WeavenMC.getGson().toJson(m));
								sender.sendMessage(
										"§4§lCLAN§f O Clan " + clan.getName() + " foi deletado com §a§lSUCESSO");
								WeavenMC.getClanCommon().unloadClan(clan.getName());
							} else {
								sender.sendMessage(
										"§4§lCLAN§f Erro ao tentar efetuar a operação, tente novamente mais tarde.");
							}
						} else {
							sender.sendMessage("§4§lCLAN§f Você precisa ser o §4§lDONO§f do Clan para §c§lDELETA-LO");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					Collection<Clan> clans = WeavenMC.getClanCommon().getClans();
					if (!clans.isEmpty()) {
						for (Clan c : clans) {
							int xp = c.getXp();
							ClanRank r = ClanRank.fromXp(xp);
							sender.sendMessage("§f[§4§l" + clan.getName() + "§f] §9- §f[§8§l" + c.getAbbreviation()
									+ "§f] §9- §bXp: §f" + xp + " §9- §bRank: " + r.getColor() + r.name());
						}
					} else {
						sender.sendMessage("§4§lCLANS§f Nenhum Clan foi encontrado!");
					}
				} else {
					sender.sendMessage("§fSe você já um §4§lDONO§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan promote §f<player> - Você pode promover alguém a §c§lADMINISTRADOR§f do clan, assim ele pode convidar pessoas para seu Clan e ajudar a upá-lo.");
					sender.sendMessage(
							"§3§l/clan demote §f<player> - Você rebaixa um §c§lADMINISTRADOR§f do seu time á §f§lPARTICIPANTE");
					sender.sendMessage(
							"§3§l/clan changeabb §f<Sigla> - Você pode mudar a sigla do seu Clan se ela estiver disponível por §6§l3000 MOEDAS");
					sender.sendMessage(
							"§3§l/clan disband§f - Para você sair do Clan você precisa excluir ele, utilize este comando. §c§lATENÇÃO§f: Você não receberá suas moedas novamente.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é um §c§lADMINISTRADOR§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage("§3§l/clan invite §f<player> - Você pode convidar alguém para entrar no Clan.");
					sender.sendMessage(
							"§3§l/clan removeinvite §f<player> - Você remove o Convite dele, caso seja alguém errado ou não há mais uma proposta.");
					sender.sendMessage(
							"§3§l/clan kick §f<player> - Você pode expulsar um §f§lPARTICIPANTE§f do seu clan, caso ele não seja mais §c§lBEM-VINDO§f.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é apenas um §f§lJOGADOR§f e tem ou não um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan create §f<Nome do Clan> <Sigla> - Você cria um Clan utilizando §6§l5000 MOEDAS§f.");
					sender.sendMessage(
							"§3§l/clan join §f<Nome do Clan> - Você pode entrar em um Clan que alguém tenha criado e convidou você.");
					sender.sendMessage(
							"§3§l/clan chat §f- Você entra no chat do seu Clan que está disponível na rede inteira.");
					sender.sendMessage(
							"§3§l/clan info §f[Nome do Clan] - Você pode receber informações do seu Clan ou definir um Clan para pesquisar.");
					sender.sendMessage(
							"§3§l/clan leave §f- Você vai sair do seu Clan e estará disponível para entrar em outro novamente.");
					sender.sendMessage("§3§l/clan list §f- Veja uma lista de todos os Clans criados na Rede");
					sender.sendMessage("");
					sender.sendMessage("§6§lBOM JOGO");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("promover")) {
					if (clan != null) {
						if (clan.isOwner(p.getUniqueId())) {
							ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
							ProxyPlayer target = null;
							if (t != null) {
								target = ProxyPlayer.getPlayer(t.getUniqueId());
							} else {
								Profile profile = WeavenMC.getProfile(args[1]);
								if (profile != null) {
									target = new ProxyPlayer(profile.getId(), profile.getName());
								}
							}
							if (target == null) {
								sender.sendMessage("§4§lERRO§f Não foi possivel carregar o perfil do jogador alvo.");
								return;
							}
							if (clan.isMemberOfClan(target.getUniqueId())) {
								if (!clan.isAdministrator(target.getUniqueId())
										&& !clan.isOwner(target.getUniqueId())) {
									clan.kickMember(target.getUniqueId());
									sendClanMessage(clan.getName(),
											"§4§l[" + clan.getName() + "] §a§l[PROMOTE] §fO jogador " + target.getName()
													+ " foi promovido para §c§lADMINISTRADOR");
									clan.addAdministrator(target);
									WeavenMC.getClanCommon().updateClan(clan);
								} else {
									sender.sendMessage(
											"§4§lCLAN§f Você pode promover apenas um §f§lPARTICIPANTE§f para §c§lADMINISTRADOR§f.");
								}
							} else {
								sender.sendMessage("§4§lCLAN§f Você pode promover apenas §c§lPARTICIPANTES§f do Clan.");
							}
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Apenas o §4§lDONO§f do Clan pode promover §f§lPARTICIPANTES§f.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("demote") || args[0].equalsIgnoreCase("demotar")) {
					if (clan != null) {
						if (clan.isOwner(p.getUniqueId())) {
							ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
							ProxyPlayer target = null;
							if (t != null) {
								target = ProxyPlayer.getPlayer(t.getUniqueId());
							} else {
								Profile profile = WeavenMC.getProfile(args[1]);
								if (profile != null) {
									target = new ProxyPlayer(profile.getId(), profile.getName());
								}
							}
							if (target == null) {
								sender.sendMessage("§4§lERRO§f Não foi possivel carregar o perfil do jogador alvo.");
								return;
							}
							if (clan.isMemberOfClan(target.getUniqueId())) {
								if (clan.isAdministrator(target.getUniqueId()) && !clan.isOwner(target.getUniqueId())
										&& !clan.isParticipant(target.getUniqueId())) {
									clan.removeAdministrator(target.getUniqueId());
									sendClanMessage(clan.getName(),
											"§4§l[" + clan.getName() + "] §c§l[DEMOTE] §fO jogador " + target.getName()
													+ " foi rebaixado para §f§lPARTICIPANTE");
									clan.addParticipant(target);
									WeavenMC.getClanCommon().updateClan(clan);
								} else {
									sender.sendMessage(
											"§4§lCLAN§f Você pode demotar apenas um §c§lADMINISTRADOR§f para §f§lPARTICIPANTE§f.");
								}
							} else {
								sender.sendMessage(
										"§4§lCLAN§f Você pode demotar apenas §c§lADMINISTRADORES§f do Clan.");
							}
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Apenas o §4§lDONO§f do Clan pode demotar §c§lADMINISTRADORES§f.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("changeabb")) {
					if (clan != null) {
						if (clan.isOwner(p.getUniqueId())) {
							synchronized (this) {
								int money = pP.getMoney();
								if (money >= 3000) {
									if (WeavenMC.getClanCommon().getClanFromAbbreviation(args[1]) == null) {
										String sigla = args[1];
										if (isLegal(sigla) && !sigla.equalsIgnoreCase("nenhum")) {
											sender.sendMessage("§4§lCLAN§f Alterando a Sigla do Clan...");
											pP.removeMoney(3000);
											pP.save(DataCategory.BALANCE);
											clan.setAbbreviation(sigla);
											WeavenMC.getClanCommon().updateClan(clan);
											ClanMessage m = new ClanMessage().writeResponse(sender.getName())
													.writeClanName(clan.getName()).writeClanTag(clan.getAbbreviation())
													.writeMoneyUsed(3000).writeType(MessageType.CHANGEABB);
											WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.CLAN_FIELD_UPDATE,
													WeavenMC.getGson().toJson(m));
											sender.sendMessage(
													"§4§lCLAN§f A Sigla do Clan foi alterada para: §8§l" + sigla);
										} else {
											sender.sendMessage(
													"§4§lCLAN§f O nome da sigla é ilegal, e precisa ter entre 3 e 16 caracteres");
										}
									}
								} else {
									sender.sendMessage("§4§lCLAN§f Você precisa se §6§l" + (3000 - pP.getMoney())
											+ " MOEDAS§f, para mudar a Sigla do Clan.");
								}
							}
						} else {
							sender.sendMessage("§4§lCLAN§f Apenas o §4§lDONO§f do Clan pode mudar a Sigla.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("convidar")) {
					if (clan != null) {
						if (clan.isAdministrator(p.getUniqueId()) || clan.isOwner(p.getUniqueId())) {
							if (!clan.isFull()) {
								ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
								if (t != null) {
									if (!clan.isMemberOfClan(t.getUniqueId())) {

										if (!WeavenMC.getAccountCommon().getWeavenPlayer(t.getUniqueId()).isClan()) {

											sender.sendMessage("§4§lCLAN§f Este jogador não pode ser convidado.");
											return;
										}

										addInvite(t.getName(), clan.getName());
										sender.sendMessage(
												"§4§lCLAN§f Você §a§lENVIOU§f um convite para o Clan para o jogador "
														+ t.getName());
										sendClanMessage(clan.getName(),
												"§4§l[" + clan.getName() + "] §a§l[INVITE] §fO jogador §2§l"
														+ t.getName() + "§f foi convidado para o Clan");
										TextComponent t1 = new TextComponent(
												"§4§lCLAN§f Você foi §a§lCONVIDADO§f para o Clan! ");
										TextComponent t2 = new TextComponent(
												"§aClique aqui para aceitar ou digite /clan join "
														+ clan.getName().toLowerCase());
										t2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/clan join " + clan.getName().toLowerCase()));
										t2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new TextComponent[] { new TextComponent(
														"§fClique para entrar no Clan " + clan.getName()) }));
										t.sendMessage(t1, t2);
									} else {
										sender.sendMessage("§4§lCLAN§f Este jogador já faz parte do Clan.");
									}
								} else {
									sender.sendMessage("§4§lCLAN§f Este jogador não está online.");
								}
							} else {
								sender.sendMessage("§4§lCLAN§f O Clan já está com os §1§lSLOTS§f cheios.");
							}
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Você precisa ser o §4§lDONO§f ou um §c§lADMINISTRADOR§f do Clan para enviar convites.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("removeinvite")) {
					if (clan != null) {
						if (clan.isAdministrator(p.getUniqueId()) || clan.isOwner(p.getUniqueId())) {
							if (hasInvite(args[1], clan.getName())) {
								cancelInvite(args[1], clan.getName());
								sender.sendMessage(
										"§4§lCLAN§f Você §c§lCANCELOU§f o convite para o Clan enviado para o jogador.");
								sendClanMessage(clan.getName(),
										"§4§l[" + clan.getName() + "] §c§l[INVITE] §fO convite para o jogador "
												+ args[1] + " foi §c§lCANCELADO");
							} else {
								sender.sendMessage("§4§lCLAN§f Este jogador não possui nenhum convite do Clan.");
							}
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Você precisa ser o §4§lDONO§f ou um §c§lADMINISTRADOR§f do Clan para remover convites.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("kickar")) {
					if (clan != null) {
						if (clan.isOwner(p.getUniqueId()) || clan.isAdministrator(p.getUniqueId())) {
							ProxiedPlayer t = BungeeCord.getInstance().getPlayer(args[1]);
							ProxyPlayer target = null;
							if (t != null) {
								target = ProxyPlayer.getPlayer(t.getUniqueId());
							} else {
								Profile profile = WeavenMC.getProfile(args[1]);
								if (profile != null) {
									target = new ProxyPlayer(profile.getId(), profile.getName());
								}
							}
							if (target == null) {
								sender.sendMessage("§4§lERRO§f Não foi possivel carregar o perfil do jogador alvo.");
								return;
							}
							if (clan.isMemberOfClan(target.getUniqueId())) {
								if (!clan.isOwner(target.getUniqueId())) {
									if (clan.isAdministrator(target.getUniqueId())
											|| clan.isOwner(target.getUniqueId())) {
										clan.kickMember(target.getUniqueId());
										WeavenMC.getClanCommon().updateClan(clan);
										ClanMessage m = new ClanMessage().writeResponse(sender.getName())
												.writeClanName(clan.getName()).writeClanTag(clan.getAbbreviation())
												.writeUserKicked(args[1]).writeType(MessageType.KICK);
										WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.CLAN_FIELD_UPDATE,
												WeavenMC.getGson().toJson(m));
										sendClanMessage(clan.getName(),
												"§4§l[" + clan.getName() + "] §c§l[KICK] §fO jogador "
														+ target.getName() + " foi §c§lKICKADO§f do Clan");
									} else {
										sender.sendMessage(
												"§4§lCLAN§f Para kickar um §c§lADMINISTRADOR§f você precisa ser o §4§lDONO§f do Clan.");
									}
								} else {
									sender.sendMessage("§4§lCLAN§f O §4§lDONO§f do Clan não pode ser kickado.");
								}
							} else {
								sender.sendMessage("§4§lCLAN§f Você pode kickar apenas §f§lPARTICIPANTES§f do Clan.");
							}
						} else {
							sender.sendMessage(
									"§4§lCLAN§f Você precisa ser o §4§lDONO§f ou §c§lADMINISTRADOR§f do Clan para kickar §f§lPARTICIPANTES§f.");
						}
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Você não possui nenhum Clan, para criar ou mais informações digite §c§l/clan help");
					}
				} else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("entrar")) {
					if (clan == null) {
						if (hasInvite(p.getName(), args[1])) {
							Clan join = WeavenMC.getClanCommon().getClanFromName(args[1]);
							if (join != null) {
								cancelInvite(p.getName(), join.getName());
								join.addParticipant(pP);
								pP.setClanName(join.getName());
								pP.save(DataCategory.ACCOUNT);
								WeavenMC.getClanCommon().updateClan(join);
								ClanMessage m = new ClanMessage().writeResponse(p.getName())
										.writeClanName(join.getName()).writeClanTag(join.getAbbreviation())
										.writeUserJoined(p.getName()).writeType(MessageType.JOIN);
								WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.CLAN_FIELD_UPDATE,
										WeavenMC.getGson().toJson(m));
								sender.sendMessage("§4§lCLAN§f Você §2§lENTROU§f para o Clan §a§l" + join.getName());
								sendClanMessage(join.getName(), "§4§l[" + join.getName() + "] §a§l[JOIN] §fO jogador "
										+ p.getName() + " entrou para o Clan");
							} else {
								sender.sendMessage("§4§lCLAN§f Este Clan não existe.");
							}
						} else {
							sender.sendMessage("§4§lCLAN§f Você não tem nenhum convite do Clan " + args[1] + ".");
						}
					} else {
						sender.sendMessage("§4§lCLAN§f Você precisa sair do Clan atual, para entrar em outro.");
					}
				} else if (args[0].equalsIgnoreCase("info")) {
					Clan info = WeavenMC.getClanCommon().getClanFromName(args[1]);
					if (info == null)
						info = WeavenMC.getClanCommon().getClanFromAbbreviation(args[1]);
					if (info != null) {
						int clanXp = info.getXp();
						ClanRank rank = ClanRank.fromXp(clanXp);
						sender.sendMessage("§f======= §4§l" + info.getName() + " §f[§8§l" + info.getAbbreviation()
								+ "§f] =======");
						sender.sendMessage("");
						sender.sendMessage("§e§lXP§f: " + clanXp);
						sender.sendMessage("§3§lLIGA " + rank.getColor() + rank.name());
						sender.sendMessage("");
						sender.sendMessage("§4§lDONO: " + color(info.getOwnerName()));
						if (info.getAdminsSize() > 0) {
							List<String> adminList = info.getAdminsNamesList();
							StringBuilder adminBuilder = new StringBuilder();
							for (int i = 0; i < adminList.size(); i++)
								adminBuilder.append(color(adminList.get(i)))
										.append((i + 1 >= adminList.size() ? "" : "§f, "));
							sender.sendMessage("§c§lADMINISTRADORES: " + adminBuilder.toString());
						} else {
							sender.sendMessage("§c§lADMINISTRADORES: §7Este Clan não possui administradores");
						}
						if (info.getMembersSize() > 0) {
							List<String> memberList = info.getMembersNamesList();
							StringBuilder memberBuilder = new StringBuilder();
							for (int i = 0; i < memberList.size(); i++)
								memberBuilder.append(color(memberList.get(i)))
										.append((i + 1 >= memberList.size() ? "" : "§f, "));
							sender.sendMessage("§f§lPARTICIPANTES: " + memberBuilder.toString());
						} else {
							sender.sendMessage("§f§lPARTICIPANTES: §7Este Clan não possui participantes");
						}
						sender.sendMessage("");
						sender.sendMessage("§1§lSLOT: §f(" + info.getActualSlot() + "/" + info.getMaxSlot() + ")");
						sender.sendMessage("");
						sender.sendMessage("§fPara mais informações digite: §c§l/clan help");
					} else {
						sender.sendMessage(
								"§4§lCLAN§f Este Clan não existe! Para ver todos os clans existentes digite §3§l/clan list");
					}
				} else {
					sender.sendMessage("§fSe você já um §4§lDONO§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan promote §f<player> - Você pode promover alguém a §c§lADMINISTRADOR§f do clan, assim ele pode convidar pessoas para seu Clan e ajudar a upá-lo.");
					sender.sendMessage(
							"§3§l/clan demote §f<player> - Você rebaixa um §c§lADMINISTRADOR§f do seu time á §f§lPARTICIPANTE");
					sender.sendMessage(
							"§3§l/clan changeabb §f<Sigla> - Você pode mudar a sigla do seu Clan se ela estiver disponível por §6§l3000 MOEDAS");
					sender.sendMessage(
							"§3§l/clan disband§f - Para você sair do Clan você precisa excluir ele, utilize este comando. §c§lATENÇÃO§f: Você não receberá suas moedas novamente.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é um §c§lADMINISTRADOR§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage("§3§l/clan invite §f<player> - Você pode convidar alguém para entrar no Clan.");
					sender.sendMessage(
							"§3§l/clan removeinvite §f<player> - Você remove o Convite dele, caso seja alguém errado ou não há mais uma proposta.");
					sender.sendMessage(
							"§3§l/clan kick §f<player> - Você pode expulsar um §f§lPARTICIPANTE§f do seu clan, caso ele não seja mais §c§lBEM-VINDO§f.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é apenas um §f§lJOGADOR§f e tem ou não um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan create §f<Nome do Clan> <Sigla> - Você cria um Clan utilizando §6§l5000 MOEDAS§f.");
					sender.sendMessage(
							"§3§l/clan join §f<Nome do Clan> - Você pode entrar em um Clan que alguém tenha criado e convidou você.");
					sender.sendMessage(
							"§3§l/clan chat §f- Você entra no chat do seu Clan que está disponível na rede inteira.");
					sender.sendMessage(
							"§3§l/clan info §f[Nome do Clan] - Você pode receber informações do seu Clan ou definir um Clan para pesquisar.");
					sender.sendMessage(
							"§3§l/clan leave §f- Você vai sair do seu Clan e estará disponível para entrar em outro novamente.");
					sender.sendMessage("§3§l/clan list §f- Veja uma lista de todos os Clans criados na Rede");
					sender.sendMessage("");
					sender.sendMessage("§6§lBOM JOGO");
				}
			} else if (args.length >= 3) {
				if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("criar")) {
					if (clan == null) {
						synchronized (this) {
							String clanName = args[1];
							String clanTag = args[2];
							if (clanName.length() <= 2 || clanName.length() > 10) {
								sender.sendMessage(
										"§4§lCLAN§f O nome do Clan precisa ter no minimo 2 carateres e no maximo 12.");
								return;
							}
							if (clanTag.length() <= 3 || clanTag.length() > 8) {
								sender.sendMessage(
										"§4§lCLAN§f A tag do Clan precisa ter no minimo 2 carateres e no maximo 85.");
								return;
							}
							if (WeavenMC.getClanCommon().getClanFromName(clanName) == null
									&& WeavenMC.getClanCommon().getClanFromAbbreviation(clanTag) == null) {
								if (!clanName.equalsIgnoreCase("nenhum") && !clanTag.equalsIgnoreCase("nenhum")) {
									int money = pP.getMoney();
									if (money >= 5000) {
										if (isLegal(clanName)) {
											if (isLegal(clanTag)) {
												Clan toCreate = new Clan(clanName, clanTag, 0, p.getUniqueId(),
														new HashMap<UUID, String>(), new HashMap<UUID, String>());
												toCreate.setOwnerName(pP.getName());
												if (WeavenMC.getClanCommon().createClan(p.getUniqueId(), toCreate)) {
													pP.removeMoney(5000);
													pP.setClanName(toCreate.getName());
													pP.save(DataCategory.ACCOUNT, DataCategory.BALANCE);
													ClanMessage m = new ClanMessage().writeResponse(p.getName())
															.writeClanName(toCreate.getName())
															.writeClanTag(toCreate.getAbbreviation())
															.writeMoneyUsed(5000).writeType(MessageType.CREATE);
													WeavenMC.getCommonRedis().getJedis().publish(
															WeavenMC.CLAN_FIELD_UPDATE, WeavenMC.getGson().toJson(m));
													sender.sendMessage(
															"§4§lCLAN§f Parabéns, você §a§lCRIOU§f o Clan §f[§4§l"
																	+ toCreate.getName() + "§f] §f[§8§l"
																	+ toCreate.getAbbreviation() + "§f]");
												} else {
													sender.sendMessage(
															"§4§lCLAN§f Não foi possivel criar o Clan, tente novamente mais tarde.");
												}
											} else {
												sender.sendMessage(
														"§4§lCLAN§f A tag do Clan escolhida é ilegal e precisa tambem ter entre 3 e 16 caracteres");
											}
										} else {
											sender.sendMessage(
													"§4§lCLAN§f O nome do Clan escolhido é ilegal e precisa tambem ter entre 3 e 16 caracteres");
										}
									} else {
										sender.sendMessage("§4§lCLAN§f Você precisa de mais §6§l"
												+ (5000 - pP.getMoney()) + " MOEDAS§f para criar o Clan.");
									}
								} else {
									sender.sendMessage("§4§lCLAN§f Nome ou Tag de Clan não disponível!");
								}
							} else {
								sender.sendMessage("§4§lCLAN§f Um Clan com esta Tag ou Nome já existe!");
							}
						}
					} else {
						sender.sendMessage("§4§lCLAN§f Você já tem um Clan, digite §c§l/clan help");
					}
				} else {
					sender.sendMessage("§fSe você já um §4§lDONO§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan promote §f<player> - Você pode promover alguém a §c§lADMINISTRADOR§f do clan, assim ele pode convidar pessoas para seu Clan e ajudar a upá-lo.");
					sender.sendMessage(
							"§3§l/clan demote §f<player> - Você rebaixa um §c§lADMINISTRADOR§f do seu time á §f§lPARTICIPANTE");
					sender.sendMessage(
							"§3§l/clan changeabb §f<Sigla> - Você pode mudar a sigla do seu Clan se ela estiver disponível por §6§l3000 MOEDAS");
					sender.sendMessage(
							"§3§l/clan disband§f - Para você sair do Clan você precisa excluir ele, utilize este comando. §c§lATENÇÃO§f: Você não receberá suas moedas novamente.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é um §c§lADMINISTRADOR§f de um Clan você pode utilizar estes comandos:");
					sender.sendMessage("§3§l/clan invite §f<player> - Você pode convidar alguém para entrar no Clan.");
					sender.sendMessage(
							"§3§l/clan removeinvite §f<player> - Você remove o Convite dele, caso seja alguém errado ou não há mais uma proposta.");
					sender.sendMessage(
							"§3§l/clan kick §f<player> - Você pode expulsar um §f§lPARTICIPANTE§f do seu clan, caso ele não seja mais §c§lBEM-VINDO§f.");
					sender.sendMessage("");
					sender.sendMessage(
							"§fSe você é apenas um §f§lJOGADOR§f e tem ou não um Clan você pode utilizar estes comandos:");
					sender.sendMessage(
							"§3§l/clan create §f<Nome do Clan> <Sigla> - Você cria um Clan utilizando §6§l5000 MOEDAS§f.");
					sender.sendMessage(
							"§3§l/clan join §f<Nome do Clan> - Você pode entrar em um Clan que alguém tenha criado e convidou você.");
					sender.sendMessage(
							"§3§l/clan chat §f- Você entra no chat do seu Clan que está disponível na rede inteira.");
					sender.sendMessage(
							"§3§l/clan info §f[Nome do Clan] - Você pode receber informações do seu Clan ou definir um Clan para pesquisar.");
					sender.sendMessage(
							"§3§l/clan leave §f- Você vai sair do seu Clan e estará disponível para entrar em outro novamente.");
					sender.sendMessage("§3§l/clan list §f- Veja uma lista de todos os Clans criados na Rede");
					sender.sendMessage("");
					sender.sendMessage("§6§lBOM JOGO");
				}
			}
		} else

		{
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	private HashMap<ProxiedPlayer, Party> partyInvites = new HashMap<>();

	@Command(name = "party")
	public void party(BungeeCommandSender sender, String label, String[] args) {
		if (!sender.isPlayer())
			return;

		ProxiedPlayer proxiedPlayer = sender.getPlayer();
		ProxyPlayer proxyPlayer = (ProxyPlayer) WeavenMC.getAccountCommon()
				.getWeavenPlayer(proxiedPlayer.getUniqueId());
		if (args.length == 0) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage(
						"§eSistema de party: \n§a/party invite (jogador) - Convide alguém para sua party\n§a/party acccept - Aceite um convite para entrar em alguma party\n§a/party reject - Rejeite um convite para entrar em alguma party");
			} else {
				if (proxyPlayer.getParty().getOwner() == proxiedPlayer) {
					sender.sendMessage(
							"§eSistema de party: \n§a/party invite (jogador) - Convide alguém para sua party\n§a/party kick (jogador) - Remova este jogador da sua party\n§a/party disband - Exclua sua party\n§a/party info - Informações da sua party");
				} else {
					sender.sendMessage(
							"§eSistema de party: \n§a/party info - Informações da sua party\n§a/party leave - Saia da sua party atual");

				}
			}
			return;
		}

		if (args[0].equalsIgnoreCase("disband")) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage("§cVocê não está em alguma party!");
				return;
			}
			if (proxyPlayer.getParty().getOwner() != proxiedPlayer) {
				sender.sendMessage("§cVocê não pode acabar com uma party que não é sua!");
				return;
			}

			proxyPlayer.getParty().disband();

		}

		if (args[0].equalsIgnoreCase("leave")) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage("§cVocê não está em alguma party!");
				return;
			}
			if (proxyPlayer.getParty().getOwner() == proxiedPlayer) {
				sender.sendMessage("§cVocê não pode sair da sua propia party!");
				return;
			}
			sender.sendMessage("§cVocê saiu da party de §a" + proxyPlayer.getParty().getOwner().getName() + "§c!");
			proxyPlayer.getParty().sendMessage("§c" + proxiedPlayer.getName() + " saiu da party!");
			proxyPlayer.getParty().removeMember(proxiedPlayer);
			proxyPlayer.setParty(null);

		}

		if (args[0].equalsIgnoreCase("reject")) {

			if (!partyInvites.containsKey(proxiedPlayer)) {
				sender.sendMessage("§cO convite não existe ou expirou!");
				return;
			}

			partyInvites.remove(proxiedPlayer);
			sender.sendMessage(
					"§aVocê rejeitou o convite para party de §e" + proxyPlayer.getParty().getOwner().getName() + "§a!");
			proxyPlayer.getParty().sendMessage("§a" + proxiedPlayer.getName() + " rejeitou o convite da party!");
		}

		if (args[0].equalsIgnoreCase("chat")) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage("§cVocê não está em alguma party!");
				return;
			}
			String message = StringUtils.createArgs(1, args, "", true);
			proxyPlayer.getParty().sendMessage("§f" + proxyPlayer.getName() + ": " + message);

		}
		if (args[0].equalsIgnoreCase("kick")) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage("§cVocê não está em alguma party!");
				return;
			}
			if (proxyPlayer.getParty().getOwner() != proxiedPlayer) {
				sender.sendMessage("§cVocê não pode kickar este jogador da party!");
				return;
			}

			ProxiedPlayer target = BungeeMain.getInstance().getProxy().getPlayer(args[1]);
			if (target == null) {
				sender.sendMessage("§cEste jogador está offline!");
				return;
			}

			ProxyPlayer proxyTarget = (ProxyPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(target.getUniqueId());

			if (proxyPlayer.getParty().inParty(target)) {
				sender.sendMessage("§cEste jogador não é da sua party!");
				return;
			}

			sender.sendMessage("§cVocê kickou o jogador §a" + target.getName() + " §cda party!");
			target.sendMessage(
					TextComponent.fromLegacyText("§cVocê foi kickado da party §e" + sender.getName() + "§c!"));
			proxyPlayer.getParty().sendMessage("§c" + target.getName() + " foi kickado da party!");
			proxyPlayer.getParty().removeMember(target);
			proxyTarget.setParty(null);

		}

		if (args[0].equalsIgnoreCase("info")) {
			if (proxyPlayer.getParty() == null) {
				sender.sendMessage("§cVocê não está em nenhuma party!");
				return;
			}

			Party party = proxyPlayer.getParty();

			sender.sendMessage("§aLider da sua party: §e" + party.getOwner().getName());
			sender.sendMessage("§aMembros da sua party: ");

			party.getMembers().forEach(pps -> sender.sendMessage("§c" + pps.getName()));

		}
		if (args[0].equalsIgnoreCase("accept")) {
			if (proxyPlayer.getParty() != null) {
				sender.sendMessage("§cVocê já está em alguma party!");
				return;
			}

			if (!partyInvites.containsKey(proxiedPlayer)) {
				sender.sendMessage("§cO convite não existe ou expirou!");
				return;
			}

			proxyPlayer.setParty(partyInvites.get(proxiedPlayer));
			proxyPlayer.getParty().addMember(proxiedPlayer);
			partyInvites.remove(proxiedPlayer);
			sender.sendMessage(
					"§aVocê aceitou o convite para party de §e" + proxyPlayer.getParty().getOwner().getName() + "§a!");
			proxyPlayer.getParty().sendMessage("§a" + proxiedPlayer.getName() + " entrou na party!");
		}

		if (args[0].equalsIgnoreCase("invite")) {
			if (proxyPlayer.getParty() != null)
				if (proxyPlayer.getParty().getOwner() != proxiedPlayer) {
					sender.sendMessage("§cVocê não tem permissão para convidar jogadores para sua party atual!");
					return;
				}

			if (args.length < 2) {
				sender.sendMessage("§aVocê deve utilizar §e/party invite (jogador)");
				return;
			}

			ProxiedPlayer target = BungeeMain.getInstance().getProxy().getPlayer(args[1]);
			if (target == null) {
				sender.sendMessage("§cEste jogador está offline!");
				return;
			}

			ProxyPlayer proxyTarget = (ProxyPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(target.getUniqueId());

			if (proxyTarget.getParty() != null) {
				sender.sendMessage("§c" + target.getName() + "§c já está em uma party!");
				return;
			}

			if (partyInvites.containsKey(target))
				if (partyInvites.get(target).getOwner() == proxiedPlayer) {
					sender.sendMessage("§cAguarde para convidar este jogador!");
					return;
				}

			if (proxyPlayer.getParty() == null)
				proxyPlayer.setParty(new Party(proxiedPlayer));

			sender.sendMessage("§aVocê convidou §e" + target.getName() + "§a para sua party!");

			TextComponent textComponent = new TextComponent();
			textComponent.addExtra("§a§n------------------------\n§aVocê recebeu um convite para party de §e"
					+ sender.getName() + "§a!");
			TextComponent acc = new TextComponent();
			acc.setText("\n§ePara §a§lACEITAR§e o convite utilize §a/party accept");
			acc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party accept"));
			textComponent.addExtra(acc);
			TextComponent reject = new TextComponent();
			reject.setText("\n§ePara §c§lREJEITAR§e o convite utilize §a/party reject");
			reject.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party reject"));
			textComponent.addExtra(reject);
			textComponent.addExtra("\n§a§n------------------------");
			target.sendMessage(textComponent);
			partyInvites.put(target, proxyPlayer.getParty());
			BungeeMain.getInstance().getProxy().getScheduler().schedule(BungeeMain.getInstance(), new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					partyInvites.remove(target);
				}
			}, 1, TimeUnit.MINUTES);
		}
	}

	@Completer(name = "clan", aliases = { "gang" })
	public List<String> clancompleter(ProxiedPlayer p, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("info")) {
				for (Clan clan : WeavenMC.getClanCommon().getClans()) {
					if (args[1].toLowerCase().startsWith(clan.getName().toLowerCase())) {
						list.add(clan.getName());
					}
				}
			}
		}
		return list;
	}

	public boolean isLegal(String clanName) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]{3,16}");
		Matcher matcher = pattern.matcher(clanName);
		return matcher.matches();
	}

	public boolean hasInvite(String playerName, String clanName) {
		HashMap<String, ScheduledTask> map = invite.get(playerName.toLowerCase());
		return map != null && map.containsKey(clanName.toLowerCase());
	}

	public void addInvite(String playerName, String clanName) {
		HashMap<String, ScheduledTask> map = invite.containsKey(playerName.toLowerCase())
				? invite.get(playerName.toLowerCase())
				: new HashMap<>();
		if (!invite.containsKey(playerName.toLowerCase()))
			invite.put(playerName.toLowerCase(), map);
		map.put(clanName.toLowerCase(),
				BungeeCord.getInstance().getScheduler().schedule(BungeeMain.getInstance(), () -> {
					if (invite.get(playerName.toLowerCase()).containsKey(clanName.toLowerCase())) {
						invite.get(playerName.toLowerCase()).remove(clanName.toLowerCase());
					}
				}, 60, TimeUnit.SECONDS));
	}

	public void cancelInvite(String playerName, String clanName) {
		HashMap<String, ScheduledTask> map = invite.get(playerName.toLowerCase());
		if (map != null && map.containsKey(clanName.toLowerCase())) {
			map.get(clanName.toLowerCase()).cancel();
			map.remove(clanName.toLowerCase());
		}
	}

	public void sendClanMessage(String clanName, String message) {
		for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
			if (o.getServer().getInfo().getName().toLowerCase().equals("login"))
				continue;
			ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
			if (online == null)
				continue;
			if (!online.getClanName().equalsIgnoreCase(clanName))
				continue;
			o.sendMessage(TextComponent.fromLegacyText(message));
		}
	}

	public String color(String name) {
		ProxiedPlayer p = BungeeCord.getInstance().getPlayer(name);
		if (p != null)
			return "§a" + p.getName();
		return "§c" + name;
	}
}
