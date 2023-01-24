package br.com.weavenmc.lobby.hologram;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.hologram.Hologram;
import org.inventivetalent.hologram.HologramAPI;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.profile.Profile;
import br.com.weavenmc.lobby.Lobby;

public class TopRankingHologram {

	private Hologram[] handle;
	private String title;
	private Location location;
	private long update = 1200L;
	private boolean spawned = false;
	private List<WeavenPlayer> topPlayers = new LinkedList<>();

	public TopRankingHologram(String s, Location loc, int size, long update) {
		this.title = s;
		this.location = loc;
		this.handle = new Hologram[size];
		this.update = update;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		handle[0].move(location);
		Location last = handle[0].getLocation();
		for (int i = 1; i < handle.length; i++) {
			handle[i].move(last = last.subtract(0.0D, 0.25D, 0.0D));
		}
	}

	public void spawn() {
		if (spawned)
			return;
		(handle[0] = HologramAPI.createHologram(location, title)).spawn();
		handle[1] = handle[0].addLineBelow("");
		int j = 1;
		for (int i = 2; i < handle.length; i++) {
			handle[i] = handle[j].addLineBelow("§a" + j + "° §7Ninguém - §60 §7- §f- UNRANKED");
			++j;
		}
		spawned = true;

		new BukkitRunnable() {
			@Override
			public void run() {
				new ThreadHologramUpdater().run();
			}
		}.runTaskAsynchronously(Lobby.getPlugin());

		Bukkit.getScheduler().runTaskTimerAsynchronously(Lobby.getPlugin(), new ThreadHologramUpdater(), update,
				update);
	}

	public void updateLines() {
		int j = 2;
		for (int i = 0; i < topPlayers.size(); i++) {
			if (j >= handle.length)
				break;
			WeavenPlayer nextTop = topPlayers.get(i);
			String tag = nextTop.getGroup().getTagToUse().getPrefix().substring(0, 2);
			int xp = nextTop.getXp();
			League league = nextTop.getLeague();
			handle[j].setText("§a" + (i + 1) + "° " + tag + nextTop.getName() + " §7- §6" + xp + " §7- "
					+ league.getColor() + league.getSymbol() + " " + league.name());
			++j;
		}
	}

	public class ThreadHologramUpdater implements Runnable {

		private Set<WeavenPlayer> a = new HashSet<>();
		private HashMap<League, Set<WeavenPlayer>> b = new HashMap<>();
		private Queue<WeavenPlayer> topers = new ConcurrentLinkedQueue<>();

		@Override
		public void run() {
			synchronized (this) {
				int j = TopRankingHologram.this.handle.length;
				for (int i = League.values().length; i > 0; i--) {
					if (j <= 0)
						break;
					League next = League.values()[i - 1];
					b.put(next, new HashSet<>());
					try {
						PreparedStatement s = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `" + DataCategory.BALANCE.getTableName() + "` WHERE `"
										+ DataType.CURRENT_LEAGUE.getField() + "`='" + next.name() + "';");
						ResultSet r = s.executeQuery();
						while (r.next()) {
							UUID id = UUID.fromString(r.getString("uniqueId"));
							Profile profile = WeavenMC.getProfileCommon().tryPremium(id);
							if (profile == null) {
								profile = WeavenMC.getProfileCommon().tryCached(id);
								if (profile == null) {
									profile = WeavenMC.getProfileCommon().tryCracked(id);
									if (profile == null) {
										continue;
									}
								}
							}
							b.get(next).add(new WeavenPlayer(profile.getId(), profile.getName()));
							--j;
						}
						r.close();
						s.close();
					} catch (SQLException e) {
						WeavenMC.debug("ERRO NO QUERY PARA TOP RANK > " + next.name() + " > " + e.getMessage());
						continue;
					}
				}

				for (int i = League.values().length; i > 0; i--) {
					League next = League.values()[i - 1];
					a.clear();

					if (!b.containsKey(next))
						continue;

					for (WeavenPlayer player : b.get(next)) {
						if (!player.load(DataCategory.ACCOUNT, DataCategory.BALANCE)) {
							b.get(next).remove(player);
						} else {
							player.organizeGroups();
							a.add(player);
						}
					}

					List<Integer> list = new ArrayList<>();

					for (WeavenPlayer player : b.get(next)) {
						list.add(player.getXp());
					}

					while (!a.isEmpty()) {
						Integer max = Collections.max(list);

						for (WeavenPlayer player : a) {
							if (player.getXp() != max)
								continue;
							list.remove(max);
							topers.add(player);
							a.remove(player);
							break;
						}
					}

					b.get(next).clear();
					b.remove(next);
				}

				organizePlayers();
			}
		}

		public void organizePlayers() {
			TopRankingHologram.this.topPlayers.clear();
			while (!topers.isEmpty())
				TopRankingHologram.this.topPlayers.add(topers.poll());
			TopRankingHologram.this.updateLines();
		}
	}
}
