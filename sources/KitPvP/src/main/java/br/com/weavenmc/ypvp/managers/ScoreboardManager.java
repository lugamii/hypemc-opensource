package br.com.weavenmc.ypvp.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.Management;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.BattleMinigame;
import br.com.weavenmc.ypvp.minigame.FramesMinigame;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SumoMinigame;
import br.com.weavenmc.ypvp.minigame.VoidChallengeMinigame;

public class ScoreboardManager extends Management implements Listener {

	private AnimatedString animatedString;
	private String title = "";

	public ScoreboardManager(yPvP plugin) {
		super(plugin);
	}

	public void enable() {
		animatedString = new AnimatedString("HYPE PVP", "§f§l", "§e§l", "§6§l", 3);
		title = "§f§l" + animatedString.next();
		registerListener(this);
	}

	public void createScoreboard(Player p) {
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		Gamer gamer = getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Sidebar sidebar = gamer.getSidebar();
		String league = bP.getLeague().getColor() + bP.getLeague().getSymbol() + " "
				+ bP.getLeague().getName().toUpperCase();
		if (sidebar == null) {
			gamer.setSidebar(sidebar = new Sidebar(p.getScoreboard()));
			sidebar.show();
		}
		if (sidebar.isHided())
			return;
		sidebar.hide();
		sidebar.show();
		sidebar.setTitle(title);
		Minigame minigame = gamer.getWarp();
		sidebar.setText(12, "");
		sidebar.setText(11, "§fKills: §a" + 0);
		sidebar.setText(10, "§fDeaths: §c" + 0);
		sidebar.setText(9, "§fKillStreak: §e" + 0);
		sidebar.setText(8, "");

		if (minigame.getName().equalsIgnoreCase("fisherman")) {
			sidebar.setText(11, "§fFisgadas: §a" + 0);
			sidebar.setText(10, "");
			
			sidebar.setText(9, "§fKills: §a" + 0);
			sidebar.setText(8, "§fDeaths: §c" + 0);
			sidebar.setText(7, "§fKillStreak: §e" + 0);

			sidebar.setText(6, "");
			sidebar.setText(5, "§fCoins: §b" + bP.getMoney());
			sidebar.setText(4, "§fPlayers: §a" + 0);

			sidebar.setText(3, "");
			sidebar.setText(2, "§ehypemc.com.br");
		}
		
		if (minigame.getName().equalsIgnoreCase("Sumo")) {
			sidebar.setText(7, "§fBatalhando contra: ");
			sidebar.setText(6, "§a" + ((SumoMinigame) minigame).getBattlePlayer(p));

			sidebar.setText(5, "");
			sidebar.setText(4, "§fRank: " + league);
			sidebar.setText(3, "§fPlayers: §a" + 0);

			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");
		}
		
		if (minigame.getName().equalsIgnoreCase("spawn")) {
			sidebar.setText(7, "§fKit: §a" + gamer.getAbility().getName());

			sidebar.setText(6, "");
			sidebar.setText(5, "§fRank: " + league);
			sidebar.setText(4, "§fXP: §b" + bP.getXp());
			sidebar.setText(3, "§fPlayers: §a" + 0);

			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");
		}
		
		if (minigame.getName().equalsIgnoreCase("1v1")) {

			sidebar.setText(7, "§fBatalhando contra: ");
			sidebar.setText(6, "§a" + ((BattleMinigame) minigame).getBattlePlayer(p));

			sidebar.setText(5, "");
			sidebar.setText(4, "§fRank: " + league);
			sidebar.setText(3, "§fPlayers: §a" + 0);

			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");

		}
		if (minigame.getName().equalsIgnoreCase("Fps")) {

			sidebar.setText(7, "§fTop KillStreak: ");
			sidebar.setText(6, "§a" + ((BattleMinigame) minigame).getTopKsName());

			sidebar.setText(5, "");
			sidebar.setText(4, "§fRank: " + league);
			sidebar.setText(3, "§fPlayers: §a" + 0);

			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");

		}
		if (minigame.getName().equalsIgnoreCase("lava")) {

			sidebar.setText(7, "§fXP: §6" + bP.getXp());
			sidebar.setText(6, "§fCoins: §b" + bP.getCash());

			sidebar.setText(5, "");
			sidebar.setText(4, "§fRank: " + league);
			sidebar.setText(3, "§fPlayers: §a" + 0);

			sidebar.setText(2, "");
			sidebar.setText(1, "§ehypemc.com.br");
		}
		
		minigame = null;
		sidebar = null;
		gamer = null;
		bP = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onUpdate(UpdateEvent event) {
		if (event.getCurrentTick() % 3l != 0L)
			return;
		title = "§f§l" + animatedString.next();
		int count = Bukkit.getOnlinePlayers().size();
		for (Player o : Bukkit.getOnlinePlayers()) {
			Gamer gamer = getPlugin().getGamerManager().getGamer(o.getUniqueId());
			Sidebar sidebar = gamer.getSidebar();
			if (sidebar == null)
				continue;
			if (sidebar.isHided())
				continue;
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(o.getUniqueId());
			String league = bP.getLeague().getColor() + bP.getLeague().getSymbol() + " "
					+ bP.getLeague().getName().toUpperCase();
			sidebar.setTitle(title);
			Minigame minigame = gamer.getWarp();
			
			sidebar.setText(12, "");
			sidebar.setText(11, "§fKills: §a" + bP.getData(DataType.PVP_KILLS));
			sidebar.setText(10, "§fDeaths: §c" + bP.getData(DataType.PVP_DEATHS));
			sidebar.setText(9, "§fKillStreak: §e" + bP.getData(DataType.PVP_KILLSTREAK));
			sidebar.setText(8, "");

			if (minigame.getName().equalsIgnoreCase("fisherman")) {
				sidebar.setText(11, "§fFisgadas: §a" + bP.getData(DataType.PVP_FISHERMAN_HOOKEDS));
				sidebar.setText(10, "");
				
				sidebar.setText(9, "§fKills: §a" + bP.getData(DataType.PVP_KILLS));
				sidebar.setText(8, "§fDeaths: §c" + bP.getData(DataType.PVP_DEATHS));
				sidebar.setText(7, "§fKillStreak: §e" + bP.getData(DataType.PVP_KILLSTREAK));

				sidebar.setText(6, "");
				sidebar.setText(5, "§fCoins: §b" + bP.getMoney());
				sidebar.setText(4, "§fPlayers: §a" + 0);

				sidebar.setText(3, "");
				sidebar.setText(2, "§ehypemc.com.br");
			}
			
			if (minigame.getName().equalsIgnoreCase("Sumo")) {

				sidebar.setText(7, "§fBatalhando contra: ");
				sidebar.setText(6, "§a" + ((SumoMinigame) minigame).getBattlePlayer(o));

				sidebar.setText(5, "");
				sidebar.setText(4, "§fRank: " + league);
				sidebar.setText(3, "§fPlayers: §a" + count);

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
			}
			
			if (minigame.getName().equalsIgnoreCase("spawn")) {
				sidebar.setText(7, "§fKit: §a" + gamer.getAbility().getName());

				sidebar.setText(6, "");
				sidebar.setText(5, "§fRank: " + league);
				sidebar.setText(4, "§fXP: §b" + bP.getXp());
				sidebar.setText(3, "§fPlayers: §a" + count);

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
			}
			if (minigame.getName().equalsIgnoreCase("1v1")) {

				sidebar.setText(7, "§fBatalhando contra: ");
				sidebar.setText(6, "§a" + ((BattleMinigame) minigame).getBattlePlayer(o));

				sidebar.setText(5, "");
				sidebar.setText(4, "§fRank: " + league);
				sidebar.setText(3, "§fPlayers: §a" + count);

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
			}
			
			if (minigame.getName().equalsIgnoreCase("Fps")) {

				sidebar.setText(7, "§fTop KillStreak: ");
				sidebar.setText(6, "§a" + ((FramesMinigame) minigame).getTopKsName() );

				sidebar.setText(5, "");
				sidebar.setText(4, "§fRank: " + league);
				sidebar.setText(3, "§fPlayers: §a" + count);

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");

			}
			if (minigame.getName().equalsIgnoreCase("lava")) {

				sidebar.setText(7, "§fXP: §6" + bP.getXp());
				sidebar.setText(6, "§fCoins: §b" + bP.getCash());

				sidebar.setText(5, "");
				sidebar.setText(4, "§fRank: " + league);
				sidebar.setText(3, "§fPlayers: §a" + count);

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
			}
			if (minigame.getName().equalsIgnoreCase("Void")) {

				sidebar.setText(7, "§fXP: §6" + bP.getXp());
				sidebar.setText(6, "§fCoins: §b" + bP.getCash());

				sidebar.setText(5, "");
				sidebar.setText(4, "§fTempo vivo: ");
				sidebar.setText(3, "§a" + ((VoidChallengeMinigame) minigame).getTimeSurviving(o));

				sidebar.setText(2, "");
				sidebar.setText(1, "§ehypemc.com.br");
				
				
			}
			updateTab(o);	
			minigame = null;
			sidebar = null;
			gamer = null;
			bP = null;
		}
	}

	public void updateTab(final Player p) {
		TabListAPI.setHeaderAndFooter(p, "§f\n§6§LHYPE\n  §ePvP 1 §8: §bmc-hypemc.com.br\n§f",
				"§f\n§fWebsite §awww.hypemc.com.br\n§fLoja §aloja.hypemc.com.br\n§fDiscord §adiscord.hypemc.com.br\n§f");
	}

	public void disable() {
	}
}