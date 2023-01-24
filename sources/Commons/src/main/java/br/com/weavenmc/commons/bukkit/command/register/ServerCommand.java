package br.com.weavenmc.commons.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.player.PlayerAPI;
import br.com.weavenmc.commons.bukkit.api.player.PingAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.Tag;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.commons.util.string.StringUtils;

public class ServerCommand implements CommandClass {

    private final String[] randomNicks = { "Ganewsolita", "gansetistico", "Sisneigewanso", "GansoeSpeto",
            "NutewellahG0D", "EmothiveeChaftter", "Vraaaaaau", "restodeaeborto", "goseivendoepeppa", "gansoblack",
            "jogadordepeweito", "zegotieenha", "Ludimilah", "xerecudoCRAFT_", "XxX_d4rk000_XXX", "suvacopeludo__",
            "macarraoISPETO", "option_000", "espacinhoPew", "mb_br455", "onONLINEpvp", "fontaBLOCOS",
            "spacyzmulhezinha", "spacyzCOLHER", "spacyzfeio", "spacyzLACOSTE" };

	@Command(name = "fakeremove")
	public void fakeremove(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (!player.getFakeName().isEmpty()) {
				player.resetFake();
				PlayerAPI.changePlayerName(p, player.getName());
				BukkitMain.getInstance().getTagManager().setTag(p, player.getGroup().getTagToUse());
				p.sendMessage("§e§lFAKEREMOVE§f Seu foi resetado com §a§lSUCESSO");
			} else {
				p.sendMessage("§e§lFAKEREMOVE§f Você não está usando um nick fake");
			}
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "fake", aliases = { "nick" }, runAsync = true)
	public void fake(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (!player.hasGroupPermission(Group.BETA) && !player.hasGroupPermission(Group.ULTRA)) {
				sender.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando!");
				return;
			}
			if (args.length == 0) {
				p.sendMessage("§1§lFAKE§f Utilize: §a§l/fake§f <name>");
			} else {
				String fakeName = (args[0].equals("random") ? randomNicks[new Random().nextInt(randomNicks.length - 1)]
						: args[0]);
				if (!fakeName.equals("list")) {
					if (WeavenMC.getProfile(fakeName) == null) {
						if (PlayerAPI.validateName(fakeName)) {
							if (Bukkit.getPlayer(fakeName) == null) {
								UUID uuid = WeavenMC.getUUIDOf(fakeName);
								if (uuid == null) {
									Bukkit.getScheduler().runTask(BukkitMain.getInstance(), () -> {
										player.setFakeName(fakeName);
										PlayerAPI.changePlayerName(p, fakeName);
										BukkitMain.getInstance().getTagManager().setTag(p, Tag.MEMBRO);
										p.sendMessage("§1§lFAKE§f Seu nick foi alterado com §a§lSUCESSO");
										p.sendMessage("§fUtilize §e§l'/fakeremove'§f para voltar ao normal");
									});
								} else {
									p.sendMessage("§1§lFAKE§f Você não pode usar um nick original");
								}
							} else {
								p.sendMessage("§1§lFAKE§f Você não pode usar nick de jogadores online");
							}
						} else {
							p.sendMessage("§1§lFAKE§f Utilize um nick §c§lválido");
						}
					} else {
						p.sendMessage("§1§lFAKE§f Este nick já possui registro no bando de dados");
					}
				} else {
					if (player.hasGroupPermission(Group.TRIAL)) {
						List<WeavenPlayer> withFake = new ArrayList<>();
						for (WeavenPlayer w : WeavenMC.getAccountCommon().getPlayers()) {
							if (((BukkitPlayer) w).getFakeName().isEmpty())
								continue;
							withFake.add(w);
						}
						if (withFake.size() > 0) {
							p.sendMessage("§3§lFAKELIST§f Lista de jogadores com fake:");
							for (WeavenPlayer inFake : withFake) {
								p.sendMessage("§7Nick: §f" + inFake.getName() + " §f- §7Fake: §f"
										+ ((BukkitPlayer) inFake).getFakeName());
							}
						} else {
							p.sendMessage("§3§lFAKELIST§f Nenhum jogador está usando fake");
						}
						withFake = null;
					} else {
						p.sendMessage(
								"§3§lFAKELIST§f Você precisa possuir o grupo TRIAL ou superior para usar este comando");
					}
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "rank", aliases = { "liga", "ligas", "ranks", "league" })
	public void rank(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			p.sendMessage(
					"§6§lLIGA§f A rede HypeMC possui um sistema de liga único que garante aos players uma competição e mais destaque no servidor.");
			p.sendMessage(
					"§fAo matar alguém, ganhar uma partida ou fazer ações específicas de cada Modo de Jogo você recebe uma quantidade de XP calculada por nossa rede para upar de nível.");
			for (League league : League.values()) {
				p.sendMessage(league.getColor() + league.getSymbol() + " " + league.toString());
			}
			League currentLeague = player.getLeague();
			League nextLeague = currentLeague.getNextLeague();
			p.sendMessage("§fSua liga atual é " + currentLeague.getColor() + "§l" + currentLeague.getSymbol() + " "
					+ currentLeague.toString());
			p.sendMessage("§fSeu XP atual é §4§l" + player.getXp() + "§f XPs");
			p.sendMessage("§fPróxima liga " + (currentLeague == League.ULTIMATO ? "§c§lVOCE JA ESTA NA ULTIMA LIGA"
					: nextLeague.getColor() + "§l" + nextLeague.getSymbol() + " " + nextLeague.toString()));
			p.sendMessage("§fXP necessário para proxima liga: §4§l"
					+ (currentLeague == League.ULTIMATO ? 0 : (currentLeague.getExperience() - player.getXp())));
			player.checkLeague();
			nextLeague = null;
			currentLeague = null;
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "reply", aliases = { "r", "responder" })
	public void reply(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§3§lTELL§f Utilize: /r <mensagem>");
			} else {
				if (player.getLastTell() != null) {
					Player t = Bukkit.getPlayer(player.getLastTell());
					if (t != null) {
						BukkitPlayer bp = BukkitPlayer.getPlayer(t.getUniqueId());
						if (bp.isTell()) {
							String message = StringUtils.createArgs(0, args, "", false);
							bp.setLastTell(p.getUniqueId());
							player.setLastTell(t.getUniqueId());
							t.sendMessage("§7[Mensagem de §f" + p.getName() + "§7] " + message);
							p.sendMessage("§7[Mensagem para §f" + t.getName() + "§7] " + message);
							message = null;
							bp = null;
							t = null;
						} else {
							p.sendMessage(
									"§3§lTELL§f Este jogador está com o recebimento de mensagens §c§lDESABILITADO");
						}
					} else {
						p.sendMessage("§3§lTELL§f O último jogador com quem conversou está offline");
					}
				} else {
					p.sendMessage("§3§lTELL§f Você não possui nenhuma mensagem recente");
				}
			}
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "tell", aliases = { "message", "msg", "whisper", "w" })
	public void tell(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§3§lTELL§f Utilize: /tell <player> <mensagem>");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("on")) {
					if (!player.isTell()) {
						player.setTell(true);
						player.getData(DataType.TELL).setValue(true);
						p.sendMessage("§3§lTELL§f Você §a§lHABILITOU§f o §b§lTELL!");
					} else {
						p.sendMessage("§3§lTELL§f O seu §b§lTELL§f já está §a§lHABILITADO");
					}
				} else if (args[0].equalsIgnoreCase("off")) {
					if (player.isTell()) {
						player.setTell(false);
						player.getData(DataType.TELL).setValue(false);
						p.sendMessage("§3§lTELL§f Você §c§lDESABILITOU§f o §b§lTELL!");
					} else {
						p.sendMessage("§3§lTELL§f O seu §b§lTELL§f já está §c§lDESABILITADO");
					}
				} else {
					p.sendMessage("§3§lTELL§f Utilize: /tell <player> <mensagem>");
				}
			} else {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					if (t.getUniqueId() != p.getUniqueId()) {
						BukkitPlayer bp = BukkitPlayer.getPlayer(t.getUniqueId());
						if (bp.isTell()) {
							String message = StringUtils.createArgs(1, args, "", false);
							bp.setLastTell(p.getUniqueId());
							player.setLastTell(t.getUniqueId());
							t.sendMessage("§7[Mensagem de §f" + p.getName() + "§7] " + message);
							p.sendMessage("§7[Mensagem para §f" + t.getName() + "§7] " + message);
							message = null;
						} else {
							p.sendMessage(
									"§3§lTELL§f Este jogador está com o recebimento de mensagens §c§lDESABILITADO");
						}
						bp = null;
					} else {
						p.sendMessage("§3§lTELL§f Indique outro jogador para enviar uma mensagem");
					}
					t = null;
				} else {
					p.sendMessage("§3§lTELL§f O jogador " + args[0] + " está offline");
				}
			}
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "doublexp", aliases = { "dxp", "double" })
	public void doublexp(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (!player.isDoubleXPActived()) {
				if (player.getDoubleXpMultiplier() > 0) {
					player.activateDoubleXp();
					player.save(DataCategory.BALANCE);
					p.sendMessage("§3§lDOUBLEXP§f Você §a§lATIVOU§f o §b§lDOUBLEXP§f por §a§l"
							+ StringTimeUtils.formatDifference(player.getLastActivatedMultiplier()));
				} else {
					p.sendMessage("§3§lDOUBLEXP§f Você §c§lNAO POSSUI NEHUM §3§lDOUBLEXP!");
				}
			} else {
				p.sendMessage("§3§lDOUBLEXP§f Você tem um pacote §a§lATIVO§f restando "
						+ StringTimeUtils.formatDifference(player.getLastActivatedMultiplier()));
			}
			player = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "ping", aliases = { "ms" })
	public void ping(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§aSeu ping é: " + PingAPI.getPing(p) + "ms");
			} else {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					p.sendMessage("§aO ping de " + t.getName() + " é: " + PingAPI.getPing(t) + "ms");
					t = null;
				} else {
					p.sendMessage("§cO jogador " + args[0] + " está offline");
				}
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
}
