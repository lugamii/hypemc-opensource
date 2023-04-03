package br.com.adlerlopes.bypiramid.hungergames.commands;

import java.lang.reflect.Field;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.GamerMode;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class GameCommand implements CommandClass {

	public static boolean minifeast = true;
	
	@Command(name = "config", aliases = { "gameconfig" }, groupToUse = Group.MOD)
	public void config(BukkitCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§e§lCONFIG§f Você deve utilizar §b§l/config (max_players)");
			return;
		}
		if (args[0].equalsIgnoreCase("max_players")) {
			if (!isInteger(args[1])) {
				sender.sendMessage("§e§lCONFIG§f Você deve utilizar §b§l/config max_players (max)");
				return;
			}
			try {
				setMaxPlayers(Integer.parseInt(args[1]));
				sender.sendMessage("§e§lCONFIG§f Você §a§lALTEROU§f o máximo de jogadores para §e§l" + args[1] + "§f!");
			} catch (NumberFormatException | ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				sender.sendMessage("§e§lCONFIG§f Não foi possível §a§lALTERAR§f o máximo de jogadores para §e§l"
						+ args[1] + "§f!");
				e.printStackTrace();
			}
		}
	}

	public static void setMaxPlayers(int maxPlayers) throws ReflectiveOperationException {
		String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer")
				.getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
		Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
		maxplayers.setAccessible(true);
		maxplayers.set(playerlist, maxplayers);
	}

	@Command(name = "score", aliases = { "scoreboard", "sidebar" })
	public void score(BukkitCommandSender sender, String label, String[] args) {
		sender.sendMessage("§6§lSCOREBOARD§f Comando §e§lDESABILITADO§f para manutenção!");
	}

	@Command(name = "specs")
	public void specs(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			Player player = commandSender.getPlayer();
			Gamer g = getManager().getGamerManager().getGamer(player);
			if (!g.isAlive())
				return;
			if (args.length == 0) {
				player.sendMessage("§2§lSPECS§f Utilize: /specs <on, off>");
			} else {
				if (args[0].equalsIgnoreCase("on")) {
					VanishAPI.getInstance().updateVanishToPlayer(player);
					for (Gamer specs : getManager().getGamerManager().getGamers().values()) {
						if (specs.isAlive())
							continue;
						if (!specs.isOnline() || specs.getPlayer() == null)
							continue;
						if (!specs.getPlayer().isOnline())
							continue;
						if (specs.isSpectating()) {
							if (AdminMode.getInstance().isAdmin(specs.getPlayer()))
								continue;
							player.showPlayer(specs.getPlayer());
						}
					}
					player.sendMessage("§2§lSPECS§f Você agora §a§lvê§f os espectadores.");
				} else if (args[0].equalsIgnoreCase("off")) {
					VanishAPI.getInstance().updateVanishToPlayer(player);
					for (Gamer specs : getManager().getGamerManager().getGamers().values()) {
						if (specs.isAlive())
							continue;
						if (!specs.isOnline() || specs.getPlayer() == null)
							continue;
						if (specs.isSpectating()) {
							if (AdminMode.getInstance().isAdmin(specs.getPlayer()))
								continue;
							player.hidePlayer(specs.getPlayer());
						}
					}
					player.sendMessage(
							"§2§lSPECS§f Você agora §c§lnão vê§f os espectadores (players no Modo Admin foram ignorados).");
				}
			}
		}
	}

	@Command(name = "spawn")
	public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			Player player = commandSender.getPlayer();
			Gamer g = HungerGames.getManager().getGamerManager().getGamer(player);

			if (!getManager().getGameManager().isPreGame()) {
				player.sendMessage("§e§lSPAWN §fVocê não pode usar isto §6§lAGORA§f!");
				return;
			}

			if (g.isOnPvpPregame())
				g.setPvpPregame(false);

			Random random = getManager().getRandom();
			HungerGames.getManager().getGamerManager().givePreGameItems(player);
			int x, z;

			x = random.nextInt(10) + 1;
			z = random.nextInt(10) + 1;

			if (random.nextBoolean()) {
				x = x * -1;
			}

			if (random.nextBoolean()) {
				z = z * -1;
			}

			HungerGames.teleportSpawn(player);
		}
	}

	@Command(name = "giveup", aliases = { "desistir" })
	public void giveup(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 0) {
				commandSender.sendMessage("§c§lDESISTO §fUse: /desisto");
				return;
			}

			Gamer gamer = getManager().getGamerManager().getGamer((Player) commandSender);
			BukkitPlayer bP = BukkitPlayer.getPlayer(gamer.getUUID());

			if (gamer.getMode() != GamerMode.ALIVE) {
				commandSender.sendMessage("§c§lDESISTO §fVocê não está em §4§lJOGO!");
				return;
			}

			if (getManager().getGameManager().isPreGame()) {
				commandSender.sendMessage("§c§lDESISTO §fVocê não pode §4§lDESISTIU AGORA!");
				return;
			}

			Location location = gamer.getPlayer().getLocation().clone().add(0, 0.5, 0);

			for (ItemStack items : gamer.getPlayer().getInventory().getContents()) {
				if (items == null || items.getType() == Material.AIR) {
					continue;
				}
				if (!getManager().getKitManager().isItemKit(items)) {
					gamer.getPlayer().getWorld().dropItemNaturally(location, items);
				}
			}

			for (ItemStack items : gamer.getPlayer().getInventory().getArmorContents()) {
				if (items == null || items.getType() == Material.AIR) {
					continue;
				}
				if (!getManager().getKitManager().isItemKit(items)) {
					gamer.getPlayer().getWorld().dropItemNaturally(location, items);
				}
			}

			if (bP.hasGroupPermission(Group.VIP) || gamer.isWinner()) {
				getManager().getGamerManager().checkWinner();
				getManager().getGamerManager().setSpectator(gamer);

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName()
						+ "] desistiu da partida. §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");
			} else {

				for (Player players : Bukkit.getOnlinePlayers()) {
					players.hidePlayer(gamer.getPlayer());
				}

				getManager().getGamerManager().setSpectator(gamer);
				getManager().getGamerManager().resetKits(gamer);
				getManager().getGamerManager().checkWinner();

				gamer.getPlayer().setHealth(20.0);
				gamer.getPlayer().setFoodLevel(20);
				gamer.getPlayer().getInventory().clear();
				gamer.getPlayer().getActivePotionEffects().clear();
				gamer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
				gamer.getPlayer().setFireTicks(0);
				gamer.getPlayer().setFoodLevel(20);
				gamer.getPlayer().setFlying(true);
				gamer.getPlayer().setAllowFlight(true);
				gamer.getPlayer().setSaturation(3.2F);

				Bukkit.broadcastMessage("§e" + gamer.getPlayer().getName() + "[" + gamer.getKit().getName()
						+ "] desistiu da partida. §4[" + getManager().getGamerManager().getAlivePlayers().size() + "]");

				gamer.getPlayer().kickPlayer("\n§c§lDESISTO §fVocê §4§lDESISTIU§f da §c§lPARTIDA§f.");
				return;
			}

			commandSender.sendMessage("§c§lDESISTO §fVocê §4§lDESISTIU§f da §c§lPARTIDA§F!");
		}
	}

	@Command(name = "fly", aliases = { "voar" }, groupToUse = Group.VIP)
	public void fly(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 0) {
				commandSender.sendMessage("§e§lFLY §fUse: /fly");
				return;
			}

			Gamer g = getManager().getGamerManager().getGamer((Player) commandSender);
			if (!getManager().getGameManager().isPreGame()) {
				commandSender.sendMessage("§e§lFLY §fVocê §5§lNAO PODE USAR§f isto agora.");
				return;
			}

			if (g.isOnPvpPregame()) {
				commandSender.sendMessage("§e§lFLY §fVocê não pode usar isto agora.");
				return;
			}

			g.getPlayer().setAllowFlight(!g.getPlayer().getAllowFlight());
			g.sendMessage("§e§lFLY §fVocê "
					+ (g.getPlayer().getAllowFlight() ? "§a§lhabilitou".toUpperCase() : "§c§ldesativou".toUpperCase())
					+ "§f o §6§lFLY");
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Command(name = "feast")
	public void feast(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			Player player = commandSender.getPlayer();
			if (getManager().getGameManager().getTimer().getFeast() == null) {
				player.sendMessage("§6§lFEAST §fO §e§lFEAST§f ainda não §6§lNASCEU!");
			} else {
				player.setCompassTarget(getManager().getGameManager().getTimer().getFeast().getLocation());
				player.sendMessage("§6§lFEAST §fBússola §e§lAPONTANDO§F para o §6§lFEAST§F!");
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Command(name = "ip")
	public void ip(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 0) {
			commandSender.sendMessage("§e§lIP §fUse: /ip");
		} else {
			commandSender
					.sendMessage("§e§lIP§f Você está no servidor §6§l" + WeavenMC.getServerId() + ".weavenmc.com.br");
		}
	}

	@Command(name = "info")
	public void info(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			sendInfo(getManager().getGamerManager().getGamer(commandSender.getPlayer()));
		}
	}

	public void sendInfo(Gamer gamer) {
		if (getManager().getGameManager().isPreGame()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage("§fComeçando em: §6"
					+ getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fJogadores: §e" + getManager().getGamerManager().getAliveGamers().size() + "/"
					+ Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKills: §a" + gamer.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			if (getManager().isDoubleKit()) {
				gamer.sendMessage("§fKit 2: §a" + gamer.getKit2().getName());
			}
			gamer.sendMessage("§fServidor: §a" + WeavenMC.getServerId().toUpperCase());
			gamer.sendMessage(" ");

		} else if (getManager().getGameManager().isInvencibility()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage("§fInvencibilidade acaba em: §6"
					+ getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fJogadores: §e" + getManager().getGamerManager().getAliveGamers().size() + "/"
					+ Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKills: §a" + gamer.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			if (getManager().isDoubleKit()) {
				gamer.sendMessage("§fKit 2: §a" + gamer.getKit2().getName());
			}
			gamer.sendMessage("§fServidor: §a" + WeavenMC.getServerId().toUpperCase());
			gamer.sendMessage(" ");
		} else if (getManager().getGameManager().isGame()) {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage(
					"§fJogo em: §6" + getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fPlayers: §e" + getManager().getGamerManager().getAliveGamers().size() + "/"
					+ Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			if (getManager().isDoubleKit()) {
				gamer.sendMessage("§fKit 2: §a" + gamer.getKit2().getName());
			}
			gamer.sendMessage("§fKills: §a" + gamer.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
			gamer.sendMessage("§fServidor: §a" + WeavenMC.getServerId().toUpperCase());
			gamer.sendMessage(" ");
		} else {
			gamer.sendMessage(" ");
			gamer.sendMessage("§e§lINFO §fInformações do game:");
			gamer.sendMessage(" ");
			gamer.sendMessage(
					"§fJogo em: §6" + getManager().getUtils().formatTime(getManager().getGameManager().getGameTime()));
			gamer.sendMessage("§fPlayers: §e" + getManager().getGamerManager().getAliveGamers().size() + "/"
					+ Bukkit.getMaxPlayers());
			gamer.sendMessage("§fKit: §a" + gamer.getKit().getName());
			if (getManager().isDoubleKit()) {
				gamer.sendMessage("§fKit 2: §a" + gamer.getKit2().getName());
			}
			gamer.sendMessage("§fKills: §a" + gamer.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
			gamer.sendMessage("§fServidor: §a" + WeavenMC.getServerId().toUpperCase());
			gamer.sendMessage(" ");
		}
	}

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public Manager getManager() {
		return HungerGames.getManager();
	}
}
