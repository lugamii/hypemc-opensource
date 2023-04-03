package br.com.adlerlopes.bypiramid.hungergames.player.scoreboard;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.server.ServerType;

public class Scoreboarding {

	private String title;
	private String id = WeavenMC.getServerId().replace("hg-a", "");
	private String text = WeavenMC.getServerType() == ServerType.TOURNAMENT ? "EVENTO" : "DOUBLEKIT-" + id;
	public Scoreboarding() {
		title = "§6§l" + text;

		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				title = "§6§l" + text;

				Bukkit.getOnlinePlayers().forEach(players -> {

					if (players == null)
						return;
					update(players);

					HungerGames.getManager().getGamerManager().updateTab(players);
				});

			}
		}.runTaskTimerAsynchronously(HungerGames.getManager().getPlugin(), 0, 15l);

	}

	public void create(Player player) {

		Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(player);
		GameStage gameStage = HungerGames.getManager().getGameManager().getGameStage();

//		League league = WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId()).getLeague();
		Sidebar sidebar = gamer.getSidebar();
//		String rank = league.getColor() + league.getSymbol() + " " + league.getName().toUpperCase();
		String clan = WeavenMC.getClanCommon().getClanFromName(
				WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId()).getClanName()) != null
						? "§7[" + WeavenMC.getClanCommon()
								.getClanFromName(
										WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId()).getClanName())
								.getAbbreviation() + "]"
						: "§7Nenhum";

		if (sidebar != null) {

			sidebar.hide();

		}

		gamer.setSidebar(sidebar = new Sidebar(player.getScoreboard()));

		int time = HungerGames.getManager().getGameManager().getTimer().getTime();

		sidebar.show();

		sidebar.setTitle("DOUBLEKIT-1");

		sidebar.setTitle(title);

		if (gameStage == GameStage.PREGAME) {

			if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
				sidebar.setText(9, "");
				sidebar.setText(8, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(7,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(6, "");
				sidebar.setText(5, "§fClan: " + clan);
				sidebar.setText(4, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(3, "§fKit 2: §a" + gamer.getKit2().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");
			} else {
				sidebar.setText(8, "");
				sidebar.setText(7, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(6,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(5, "");
				sidebar.setText(4, "§fClan: " + clan);
				sidebar.setText(3, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");

			}

			return;
		}

		if (gameStage == GameStage.INVENCIBILITY) {

			if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
				sidebar.setText(9, "");
				sidebar.setText(8, "§fInvencibilidade: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(7,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(6, "");
				sidebar.setText(5, "§fClan: " + clan);
				sidebar.setText(4, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(3, "§fKit 2: §a" + gamer.getKit2().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");
			} else {
				sidebar.setText(8, "");
				sidebar.setText(7, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(6,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(5, "");
				sidebar.setText(4, "§fClan: " + clan);
				sidebar.setText(3, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");

			}

			return;
		}

		if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
			sidebar.setText(11, "");
			sidebar.setText(10, "§fTempo de jogo: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
			sidebar.setText(9, "§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

			sidebar.setText(8, "");
			sidebar.setText(7, "§fClan: " + clan);
			sidebar.setText(6, "§fKit: §a" + gamer.getKit().getName());
			sidebar.setText(5, "§fKit 2: §a" + gamer.getKit2().getName());
			sidebar.setText(4, "");
			sidebar.setText(3, "§fKills: §e" + player.getStatistic(Statistic.PLAYER_KILLS));
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehg.hypemc.com.br");
		} else {
			sidebar.setText(10, "");
			sidebar.setText(9, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
			sidebar.setText(8, "§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

			sidebar.setText(7, "");
			sidebar.setText(6, "§fClan: " + clan);
			sidebar.setText(5, "§fKit: §a" + gamer.getKit().getName());
			sidebar.setText(4, "");
			sidebar.setText(3, "§fKills: §e" + player.getStatistic(Statistic.PLAYER_KILLS));
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehg.hypemc.com.br");
		}
	}

	public void update(Player player) {

		Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(player);

		if (gamer.getSidebar() == null || gamer.getSidebar().isHided())
			return;

		GameStage gameStage = HungerGames.getManager().getGameManager().getGameStage();
		Sidebar sidebar = gamer.getSidebar();
		int time = HungerGames.getManager().getGameManager().getTimer().getTime();
		sidebar.setTitle(title);
//		League league = WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId()).getLeague();
//		String rank = league.getColor() + league.getSymbol() + " " + league.getName().toUpperCase();

		if (gameStage == GameStage.PREGAME) {

			if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
				sidebar.setText(9, "");
				sidebar.setText(8, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(7,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(6, "");
				sidebar.setText(4, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(3, "§fKit 2: §a" + gamer.getKit2().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");
			} else {
				sidebar.setText(8, "");
				sidebar.setText(7, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(6,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(5, "");
				sidebar.setText(3, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");

			}

			return;
		}

		if (gameStage == GameStage.INVENCIBILITY) {

			if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
				sidebar.setText(9, "");
				sidebar.setText(8, "§fInvencibilidade: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(7,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(6, "");
				sidebar.setText(4, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(3, "§fKit 2: §a" + gamer.getKit2().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");
			} else {
				sidebar.setText(8, "");
				sidebar.setText(7, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
				sidebar.setText(6,
						"§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

				sidebar.setText(5, "");
				sidebar.setText(3, "§fKit: §a" + gamer.getKit().getName());
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehg.hypemc.com.br");

			}

			return;
		}

		if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
			sidebar.setText(11, "");
			sidebar.setText(10, "§fTempo de jogo: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
			sidebar.setText(9, "§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

			sidebar.setText(8, "");
			sidebar.setText(6, "§fKit: §a" + gamer.getKit().getName());
			sidebar.setText(5, "§fKit 2: §a" + gamer.getKit2().getName());
			sidebar.setText(4, "");
			sidebar.setText(3, "§fKills: §e" + player.getStatistic(Statistic.PLAYER_KILLS));
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehg.hypemc.com.br");
		} else {
			sidebar.setText(10, "");
			sidebar.setText(9, "§fIniciando em: §e" + HungerGames.getManager().getUtils().formatOldTime(time));
			sidebar.setText(8, "§fJogadores: §e" + HungerGames.getManager().getGamerManager().getAliveGamers().size());

			sidebar.setText(7, "");
			sidebar.setText(5, "§fKit: §a" + gamer.getKit().getName());
			sidebar.setText(4, "");
			sidebar.setText(3, "§fKills: §e" + player.getStatistic(Statistic.PLAYER_KILLS));
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehg.hypemc.com.br");
		}
	}

}
