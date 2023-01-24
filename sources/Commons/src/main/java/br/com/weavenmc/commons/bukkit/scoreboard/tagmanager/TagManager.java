package br.com.weavenmc.commons.bukkit.scoreboard.tagmanager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitCommon;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.account.Medal;
import br.com.weavenmc.commons.bukkit.event.account.PlayerChangeTagEvent;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.Tag;

public class TagManager extends BukkitCommon {

	private HashMap<UUID, Tag> ntag = new HashMap<>();
	private HashMap<UUID, Medal> medal = new HashMap<>();

	public TagManager(BukkitMain plugin) {
		super(plugin);
	}

	@Override
	public void onEnable() {
		ntag.clear();
		for (Player o : Bukkit.getOnlinePlayers()) {
			BukkitPlayer player = BukkitPlayer.getPlayer(o.getUniqueId());
			if (player == null)
				continue;
			ntag.put(o.getUniqueId(), player.getGroup().getTagToUse());
			update(o);
		}
		registerListener(new TagListener(this));
	}

	public void setMedal(Player p, Medal m) {
		medal.put(p.getUniqueId(), m);
		update(p);
	}

	public boolean isUsingMedal(Player p) {
		return getMedal(p) != null;
	}

	public Medal getMedal(Player p) {
		return medal.get(p.getUniqueId());
	}

	public void removeMedal(Player p) {
		if (medal.containsKey(p.getUniqueId())) {
			medal.remove(p.getUniqueId());
			update(p);
		}
	}

	public Tag getTag(Player p) {
		return ntag.get(p.getUniqueId());
	}

	public void removePlayerTag(Player p) {
		if (p.getScoreboard() != null) {
			for (Team team : p.getScoreboard().getTeams()) {
				team.unregister();
				team = null;
			}
		}
		ntag.remove(p.getUniqueId());
	}

	public boolean currentTag(Player p, Tag tag) {
		if (ntag.get(p.getUniqueId()) == null)
			return false;
		return ntag.get(p.getUniqueId()) == tag;
	}

	public void setTag(Player p, Tag tag) {
		ntag.put(p.getUniqueId(), tag);
		update(p);
	}

	public void updateCurrent(Player p) {
		update(p);
	}

	public void update(Player p) {
		BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
		League league = player.getLeague();
		Tag tag = ntag.get(p.getUniqueId());
		if (tag == null)
			tag = Tag.MEMBRO;
		Medal medal = getMedal(p);
		if (medal != null && !player.hasGroupPermission(medal.getGroupToUse())) {
			removeMedal(p);
			medal = null;
		}

		String teamID = tag.getTeam() + league.getTeam() + p.getUniqueId().toString().substring(0, 14);
		String prefix = tag.getPrefix(), suffix = " §7[" + league.getColor() + league.getSymbol() + "§7]"
						+ (medal != null ? " " + medal.getColor() + "§l" + medal.getSymbol() : "");
		PlayerChangeTagEvent event = new PlayerChangeTagEvent(p, teamID, prefix, suffix);
		if (!event.isCancelled()) {
			Team now = createTeamIfNotExists(p, p.getName(), event.getTeamID(), event.getTagPrefix(),
					event.getTagSuffix());
			for (Team old : p.getScoreboard().getTeams()) {
				if (old.hasEntry(p.getName()) && !old.getName().equals(now.getName())) {
					old.unregister();
					old = null;
				}
			}
			p.setDisplayName(event.getTagPrefix() + p.getName() + event.getTagSuffix());
			for (Player o : Bukkit.getOnlinePlayers()) {
				if (o.getUniqueId() == p.getUniqueId())
					continue;
				BukkitPlayer online = BukkitPlayer.getPlayer(o.getUniqueId());
				League l = online.getLeague();
				String cl = null;
				Tag t = ntag.get(o.getUniqueId());
				if (t == null)
					t = Tag.MEMBRO;
				Medal omedal = getMedal(o);
				String id = t.getTeam() + l.getTeam() + o.getUniqueId().toString().substring(0, 14);
				String pre = t.getPrefix(), suf = cl == null
						? " §7[" + l.getColor() + l.getSymbol() + "§7]"
								+ (omedal != null ? " " + omedal.getColor() + "§l" + omedal.getSymbol() : "")
						: ((cl + (omedal != null ? " " + omedal.getColor() + "§l" + omedal.getSymbol() : ""))
								.length() > 16 ? " §7[" + l.getColor() + l.getSymbol() + "§7]"
										+ (omedal != null ? " " + omedal.getColor() + "§l" + omedal.getSymbol() : "")
										: cl + (omedal != null ? " " + omedal.getColor() + "§l" + omedal.getSymbol()
												: ""));

				Team to = createTeamIfNotExists(p, o.getName(), id, pre, suf);
				for (Team old : p.getScoreboard().getTeams()) {
					if (old.hasEntry(o.getName()) && !old.getName().equals(to.getName())) {
						old.unregister();
						old = null;
					}
				}
				createTeamIfNotExists(o, p.getName(), now.getName(), now.getPrefix(), now.getSuffix());
				t = null;
				l = null;
				online = null;
				pre = null;
				id = null;
			}
		}
		league = null;
		tag = null;
		teamID = null;
		prefix = null;
		suffix = null;
		event = null;
		player = null;
	}

	public Team createTeamIfNotExists(Player p, String entrie, String teamID, String prefix, String suffix) {
		if (p.getScoreboard() == null) {
			p.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
		}
		Team team = p.getScoreboard().getTeam(teamID);
		if (team == null) {
			team = p.getScoreboard().registerNewTeam(teamID);
		}
		if (!team.hasEntry(entrie)) {
			team.addEntry(entrie);
		}
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		return team;
	}

	@Override
	public void onDisable() {
		for (Player o : Bukkit.getOnlinePlayers())
			removePlayerTag(o);
		ntag.clear();
		ntag = null;
	}
}
