package br.com.weavenmc.lobby.managers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.account.Tag;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.Management;
import br.com.weavenmc.lobby.gamer.Gamer;

public class ScoreboardManager extends Management implements Listener {
	public ScoreboardManager(final Lobby plugin) {
		super(plugin);
	}

	private boolean colorList = false;
	private final String[] colors = { "a", "b", "c", "d", "e", "f" };

	@Override
	public void enable() {
		for (final Player o : Bukkit.getOnlinePlayers()) {
			this.createScoreboard(o);
		}
		new BukkitRunnable() {
			public void run() {
				final int onlineCount = BukkitMain.getInstance().getNetworkManager().getNetworkOnlineCount();
				for (final Player o : Bukkit.getOnlinePlayers()) {
					Gamer gamer = ScoreboardManager.this.getPlugin().getGamerManager().getGamer(o.getUniqueId());
					if (gamer == null) {
					}
					Sidebar sidebar = gamer.getSidebar();
					if (sidebar == null) {
						continue;
					}
					BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(o.getUniqueId());
					int wins = bP.getData(DataType.SKYWARS_SOLO_WINS).asInt();
					int kills = bP.getData(DataType.SKYWARS_SOLO_KILLS).asInt();
//					League league = bP.getLeague();
					if (sidebar.isHided()) {
						sidebar.show();
					}
					colorList = !colorList;
					sidebar.setTitle(
							"§" + (colorList ? colors[new Random().nextInt(colors.length)] : new Random().nextInt(9))
									+ "§lSKY WARS");
					sidebar.setText(7, " §fAbates: §a" + kills);
					sidebar.setText(6, " §fVitórias: §2" + wins);
					sidebar.setText(4, "§fCoins: §6" + bP.getMoney());
//					sidebar.setText(4, "");
					sidebar.setText(3, "§fJogadores: §e" + onlineCount);
//					sidebar.setText(2, "");
//					sidebar.setText(1, "§ehypemc.com.br");
					ScoreboardManager.this.updateTab(o);
					sidebar = null;
					gamer = null;
					bP = null;
				}
			}
		}.runTaskTimer((Plugin) this.getPlugin(), 15L, 15L);
	}

	public void createScoreboard(final Player p) {
		Gamer gamer = this.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer == null) {
			return;
		}
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (bP == null) {
			return;
		}
		Sidebar sidebar = gamer.getSidebar();
		if (sidebar == null) {
			sidebar = new Sidebar(p.getScoreboard());
		}
		gamer.setSidebar(sidebar);
		sidebar.show();
		sidebar.setTitle(
				"§" + (colorList ? colors[new Random().nextInt(colors.length)] : new Random().nextInt(9)) + "§lSKYWARS");
		sidebar.setText(9, "");
		sidebar.setText(8, "§eSolo:");
		sidebar.setText(7, " §fAbates: §a" + 0);
		sidebar.setText(6, " §fVitórias: §2" + 0);
		sidebar.setText(5, "");
		sidebar.setText(4, "§fCoins: §6" + bP.getMoney());
		sidebar.setText(3, "§fJogadores: §a" + 0);
		sidebar.setText(2, "");
		sidebar.setText(1, "§ehypemc.com.br");
		this.updateTab(p);
		sidebar = null;
		gamer = null;
		bP = null;
	}

	public void onUpdate(final UpdateEvent event) {
	}

	public String format(final Tag tag) {
		final String part1 = tag.name().substring(0, 1);
		final String part2 = tag.name().toLowerCase().substring(1);
		return String.valueOf(tag.getPrefix().substring(0, 2)) + part1 + part2;
	}

	public void updateTab(final Player p) {
		TabListAPI.setHeaderAndFooter(p, "§f\n§6§LHYPE\n  §eLobby SW §8: §bhypemc.com.br\n§f",
				"§f\n§fWebsite §awww.hypemc.com.br\n§fLoja §aloja.hypemc.com.br\n§fDiscord §adiscord.hypemc.com.br\n§f");
	}

	@Override
	public void disable() {
	}
}
