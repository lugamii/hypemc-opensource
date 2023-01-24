package br.com.mcweaven.gladiator.hologram;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.hologram.Hologram;
import org.inventivetalent.hologram.HologramAPI;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.profile.Profile;

public class SimpleTopHologram {

	private String name, suffix;
	private Hologram[] handle;
	private boolean spawned;
	private Location location;

	private long update;

	private DataCategory category;
	private DataType dataType;

	private List<WeavenPlayer> topPlayers = new LinkedList<>();

	public SimpleTopHologram(String name, String suffix, Location location, int size, DataCategory category,
			DataType type, long update) {
		this.name = name;
		this.suffix = suffix;
		this.location = location;
		this.handle = new Hologram[size];
		this.category = category;
		this.dataType = type;
		this.update = update;
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
		(handle[0] = HologramAPI.createHologram(location, name)).spawn();
		handle[1] = handle[0].addLineBelow("");
		int j = 1;
		for (int i = 2; i < handle.length; i++) {
			handle[i] = handle[j].addLineBelow("§a" + j + "° §7Ninguém - §60 " + suffix);
			++j;
		}
		spawned = true;

		new BukkitRunnable() {
			@Override
			public void run() {
				new RunnableHologramUpdater().run();
			}
		}.runTaskAsynchronously(Gladiator.getInstance());

		Bukkit.getScheduler().runTaskTimerAsynchronously(Gladiator.getInstance(), new RunnableHologramUpdater(), update,
				update);
	}

	public void update() {
		int j = 2;
		for (int i = 0; i < topPlayers.size(); i++) {
			if (j >= handle.length)
				break;
			WeavenPlayer nextTop = topPlayers.get(i);
			String tag = nextTop.getGroup().getTagToUse().getPrefix().substring(0, 2);
			handle[j].setText(
					"§a" + (i + 1) + "° " + tag + nextTop.getName() + " §7- §6§l" + nextTop.getCash() + " " + suffix);
			++j;
		}
	}

	public class RunnableHologramUpdater implements Runnable {

		private Queue<WeavenPlayer> toppers = new ConcurrentLinkedQueue<>();

		@Override
		public void run() {
			try {
				synchronized (this) {
					PreparedStatement s = WeavenMC.getCommonMysql().preparedStatement("SELECT * FROM `"
							+ category.getTableName() + "` ORDER BY `" + dataType.getField() + "` desc LIMIT 100;");
					ResultSet r = s.executeQuery();

					int j = handle.length - 2;
					while (r.next()) {
						if (j <= 0)
							break;

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

						WeavenPlayer player = new WeavenPlayer(profile.getId(), profile.getName());
						if (!player.load(DataCategory.ACCOUNT)) {
							continue;
						} else {
							player.organizeGroups();
							player.addCash(r.getInt(dataType.getField()));
						}

						toppers.add(player);
						--j;
					}

					r.close();
					s.close();
					update();
				}
			} catch (SQLException e) {
				WeavenMC.debug("ERROR TRYING TO UPDATE HOLOGRAM > " + e.getMessage());
			}
		}

		public void update() {
			topPlayers.clear();
			while (!toppers.isEmpty())
				topPlayers.add(toppers.poll());
			SimpleTopHologram.this.update();
		}
	}
}
