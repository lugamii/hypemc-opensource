package br.com.weavenmc.skywars.scoreboard;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.player.PlayerController;
import br.com.weavenmc.timer.Iniciando;
import br.com.weavenmc.timer.Jogo;

public class Scoreboarding {

	/**
	 * @shooyutt
	 */

	static HashMap<Player, Sidebar> scoreboard = new HashMap<>();
	private static boolean colorList = false;
	private final static String[] colors = { "a", "b", "c", "d", "e", "f" };

	static {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {
					WeavenSkywars.getGameManager().checkWinner();
					
					if (Bukkit.getOnlinePlayers().size() == 1 && !WeavenSkywars.getGameManager().checkWinner())  {
						Bukkit.shutdown();
						
					}
					
				}
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (!scoreboard.containsKey(player))
						return;
					Sidebar sidebar = scoreboard.get(player);
					colorList = !colorList;
					sidebar.setTitle(
							"§" + (colorList ? colors[new Random().nextInt(colors.length)] : new Random().nextInt(9))
									+ "§lSKYWARS");

					if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
							|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
						sidebar.setText(9, "");
						sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
						sidebar.setText(7, "");
						sidebar.setText(6, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
						sidebar.setText(5, "§fModo: §aSolo");
						sidebar.setText(4, "");
						sidebar.setText(3, "§fInicia em §a" + getTimerFormat(Iniciando.timer));
						sidebar.setText(2, "");
						sidebar.setText(1, "§ehypemc.com.br");
					} else if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {

						sidebar.setText(12, "");
						sidebar.setText(11, "§fProximo Evento:");
						sidebar.setText(10,
								"§a" + WeavenSkywars.getGameManager().getEState().getName() + " " + getTimerFormat(Jogo.timer));
						sidebar.setText(9, "");
						sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
						sidebar.setText(7, "");
						sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
						sidebar.setText(5, "");
						sidebar.setText(4, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
						sidebar.setText(3, "§fModo: §aSolo");
						sidebar.setText(2, "");
						sidebar.setText(1, "§ehypemc.com.br");

					} else if (WeavenSkywars.getGameManager().getState() == GameState.END) {
						if (WeavenSkywars.getGameManager().getWinner() != null) {
							sidebar.setText(12, "");
							sidebar.setText(11, "§fVencedor:");
							sidebar.setText(10, "§a" + WeavenSkywars.getGameManager().getWinner().getName());
							sidebar.setText(9, "");
							sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
							sidebar.setText(7, "");
							sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
							sidebar.setText(5, "");
							sidebar.setText(4, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
							sidebar.setText(3, "§fModo: §aSolo");
							sidebar.setText(2, "");
							sidebar.setText(1, "§ehypemc.com.br");

						} else {
							sidebar.setText(12, "");
							sidebar.setText(11, "§fVencedor:");
							sidebar.setText(10, "§cNinguém");
							sidebar.setText(9, "");
							sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
							sidebar.setText(7, "");
							sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
							sidebar.setText(5, "");
							sidebar.setText(4, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
							sidebar.setText(3, "§fModo: §aSolo");
							sidebar.setText(2, "");
							sidebar.setText(1, "§ehypemc.com.br");
							Bukkit.shutdown();
						}
					}

				});
			}
		}.runTaskTimerAsynchronously(WeavenSkywars.getInstance(), 0, 15);
	}

	public static void setScoreboard(Player player) {
		sendScoreboard(player); // '-' nao aparece a aba de pesquisa ctrl + f
		setTab(player);
	}

	public static void setTab(Player player) {
		TabListAPI.setHeaderAndFooter(player, "§f\n§6§LHYPE\n  §eSkyWars §8: §bhypemc.com.br\n§f",
				"§f\n§fWebsite §awww.hypemc.com.br\n§fLoja §aloja.hypemc.com.br\n§fDiscord §adiscord.hypemc.com.br\n§f");
	}

	public static String getMod(String name) {
		if (name.length() == 16) {
			String shorts = name.substring(0, name.length() - 4);
			return shorts;
		}
		if (name.length() == 15) {
			String shorts = name.substring(0, name.length() - 3);
			return shorts;
		}
		if (name.length() == 14) {
			String shorts = name.substring(0, name.length() - 2);
			return shorts;
		}
		if (name.length() == 13) {
			String shorts = name.substring(0, name.length() - 1);
			return shorts;
		}
		return name;
	}

	public static void sendScoreboard(Player player) { // pera ai
		Scoreboard sb = player.getScoreboard();
		Sidebar sidebar = scoreboard.containsKey(player) ? scoreboard.get(player) : new Sidebar(sb);
		sidebar.hide();
		sidebar.show();

		sidebar.setTitle("§6§lSKYWARS");

		if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY
				|| WeavenSkywars.getGameManager().getState() == GameState.JAIL) {
			sidebar.setText(9, "");
			sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
			sidebar.setText(7, "");
			sidebar.setText(6, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
			sidebar.setText(5, "§fModo: §aSolo");
			sidebar.setText(4, "");
			sidebar.setText(3, "§fInicia em §a" + getTimerFormat(Iniciando.timer));
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");
		} else if (WeavenSkywars.getGameManager().getState() == GameState.GAME) {

			sidebar.setText(12, "");
			sidebar.setText(11, "§fProximo Evento:");
			sidebar.setText(10,
					"§a" + WeavenSkywars.getGameManager().getEState().getName() + " " + getTimerFormat(Jogo.timer));
			sidebar.setText(9, "");
			sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
			sidebar.setText(7, "");
			sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
			sidebar.setText(5, "");
			sidebar.setText(4, "§fMapa: §e" + WeavenSkywars.getGameManager().getMapName());
			sidebar.setText(3, "§fModo: §aSolo");
			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");

		} else if (WeavenSkywars.getGameManager().getState() == GameState.END) {
			if (WeavenSkywars.getGameManager().getWinner() != null) {
				sidebar.setText(12, "");
				sidebar.setText(11, "§fVencedor:");
				sidebar.setText(10, "§a" + WeavenSkywars.getGameManager().getWinner().getName());
				sidebar.setText(9, "");
				sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
				sidebar.setText(7, "");
				sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
				sidebar.setText(5, "");
				sidebar.setText(4, "§fMapa: " + WeavenSkywars.getGameManager().getMapName());
				sidebar.setText(3, "§fModo: §aSolo");
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");

			} else {
				sidebar.setText(12, "");
				sidebar.setText(11, "§fVencedor:");
				sidebar.setText(10, "§cNinguém");
				sidebar.setText(9, "");
				sidebar.setText(8, "§fJogadores: §a" + GameController.player.size() + "/12");
				sidebar.setText(7, "");
				sidebar.setText(6, "§fKills: §c" + PlayerController.kills.get(player.getUniqueId()));
				sidebar.setText(5, "");
				sidebar.setText(4, "§fMapa: " + WeavenSkywars.getGameManager().getMapName());
				sidebar.setText(3, "§fModo: §aSolo");
				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
			}
		}
		scoreboard.put(player, sidebar);
		return;
	}

	public static String givetTimer(Integer i) {
		int minutes = i.intValue() / 60;
		int seconds = i.intValue() % 60;
		String disMinu = (minutes < 10 ? "" : "") + minutes;
		String disSec = (seconds < 10 ? "0" : "") + seconds;
		String formattedTime = disMinu + "m " + disSec + "s";
		return formattedTime;
	}

	public static void removeScoreboard(Player player) {
		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
	}

	public static String getTimerFormat(int timer) {
		int a = timer / 60, b = timer % 60;
		String c = null;
		String d = null;
		if (a >= 10) {
			c = "" + a;
		} else {
			c = "0" + a;
		}
		if (b >= 10) {
			d = "" + b;
		} else {
			d = "0" + b;
		}
		return c + ":" + d;
	}

}
