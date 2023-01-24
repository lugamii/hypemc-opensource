package br.com.mcweaven.gladiator.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.mcweaven.gladiator.gamer.Gamer;
import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;

public class ScoreboardManager implements Listener {

	private static String title = "";
	private static AnimatedString animatedString;

	public ScoreboardManager() {
		animatedString = new AnimatedString("GLADIATOR", "§f§l", "§e§l", "§6§l", 3);
	}

	static {

		new BukkitRunnable() {

			@Override
			public void run() {
				title = animatedString.next();

				if (Gladiator.getInstance().getGamerManager().getGamers().size() > 0)

					for (Gamer gamer : Gladiator.getInstance().getGamerManager().getGamers()) {
						if (gamer.getSidebar() != null)
							update(gamer);
					}

			}
		}.runTaskTimerAsynchronously(Gladiator.getInstance(), 0, 4L);
	}

	public void create(Player player) {

		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		if (gamer.getSidebar() == null)
			gamer.setSidebar(new Sidebar(player.getScoreboard()));
		gamer.getSidebar().hide();
		gamer.getSidebar().show();

		Sidebar gamerSidebar = gamer.getSidebar();

		gamerSidebar.setTitle("§f§l" + title);

		if (gamer.getFight() == null) {

			gamerSidebar.setText(9, "");
			gamerSidebar.setText(8, "§fVitórias: §a" + gamer.getWins());
			gamerSidebar.setText(7, "§fDerrotas: §c" + gamer.getDeaths());
			gamerSidebar.setText(6, "§fWinStreak: §e" + gamer.getWinStreak());
			gamerSidebar.setText(5, "");
			gamerSidebar.setText(4, "§7Batalhando contra: ");
			gamerSidebar.setText(3, "§cNinguém");
			gamerSidebar.setText(2, "");
			gamerSidebar.setText(1, "§ewww.hypemc.com.br ");
		} else {

			gamerSidebar.setText(11, "");
			gamerSidebar.setText(10, "§fVitórias: §a" + gamer.getWins());
			gamerSidebar.setText(9, "§fDerrotas: §c" + gamer.getDeaths());
			gamerSidebar.setText(8, "§fWinStreak: §e" + gamer.getWinStreak());
			gamerSidebar.setText(7, "");
			gamerSidebar.setText(6, "§fBatalhando contra: ");
			String duel = gamer.getGladiatorEnemy() == null ? "§cNinguém"
					: "§c" + Bukkit.getPlayer(gamer.getGladiatorEnemy()).getName();
			gamerSidebar.setText(5, "§3" + duel);
			gamerSidebar.setText(4, "");
			gamerSidebar.setText(3, "§fTempo: §e" + formatOldTime(gamer.getFight().getFightTime()));
			gamerSidebar.setText(2, "");
			gamerSidebar.setText(1, "§ewww.hypemc.com.br ");

		}

	}

	private static void update(Gamer gamer) {
		if (gamer == null)
			return;

		Sidebar gamerSidebar = gamer.getSidebar();

		gamerSidebar.setTitle("§f§l" + title);

		if (gamer.getGladiatorEnemy() == null) {

			gamerSidebar.setText(9, "");
			gamerSidebar.setText(8, "§fVitórias: §a" + gamer.getWins());
			gamerSidebar.setText(7, "§fDerrotas: §c" + gamer.getDeaths());
			gamerSidebar.setText(6, "§fWinStreak: §e" + gamer.getWinStreak());
			gamerSidebar.setText(5, "");
			gamerSidebar.setText(4, "§fBatalhando contra: ");
			gamerSidebar.setText(3, "§cNinguém");
			gamerSidebar.setText(2, "");
			gamerSidebar.setText(1, "§ewww.hypemc.com.br ");

		} else {

			gamerSidebar.setText(11, "");
			gamerSidebar.setText(10, "§fVitórias: §a" + gamer.getWins());
			gamerSidebar.setText(9, "§fDerrotas: §c" + gamer.getDeaths());
			gamerSidebar.setText(8, "§fWinStreak: §e" + gamer.getWinStreak());
			gamerSidebar.setText(7, "");
			gamerSidebar.setText(6, "§fBatalhando contra: ");
			gamerSidebar.setText(5, "§c" + (gamer.getGladiatorEnemy() == null ? "Ninguém"
					: Bukkit.getPlayer(gamer.getGladiatorEnemy()).getName()));
			gamerSidebar.setText(4, "");
			gamerSidebar.setText(3, "§fTempo: §e" + formatOldTime(gamer.getFight().getFightTime()));
			gamerSidebar.setText(2, "");
			gamerSidebar.setText(1, "§ewww.hypemc.com.br ");

		}

		updateTab(Bukkit.getPlayer(gamer.getPlayerUuid()));

	}

	private static String formatOldTime(Integer i) {
		int minutes = i.intValue() / 60;
		int seconds = i.intValue() % 60;
		String disMinu = (minutes < 10 ? "" : "") + minutes;
		String disSec = (seconds < 10 ? "0" : "") + seconds;
		String formattedTime = disMinu + ":" + disSec;
		return formattedTime;
	}

	public static void updateTab(final Player p) {
		TabListAPI.setHeaderAndFooter(p, "§f\n§6§LHYPE\n  §eGladiator 1 §8: §bmc-hypemc.com.br\n§f",
				"§f\n§fWebsite §awww.hypemc.com.br\n§fLoja §aloja.hypemc.com.br\n§fDiscord §adiscord.hypemc.com.br\n§f");
	}

}
