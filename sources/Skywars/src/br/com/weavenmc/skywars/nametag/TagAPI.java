package br.com.weavenmc.skywars.nametag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import br.com.weavenmc.skywars.utils.Reflection;

public class TagAPI extends Reflection {

	// AQUI e na outra classe chamada Tag
	public static final Map<UUID, Tag> TAGS = new HashMap<>();

	public static void setFriendlyTag(Player player) {
		createTeamIfNotExists(player, player.getName(), "A" + player.getUniqueId().toString().substring(0, 15),
				"§a[A] ", "");
	}

	public static void setEnemyTag(Player player, Player enemy) {
		createTeamIfNotExists(player, enemy.getName(), "B" + enemy.getUniqueId().toString().substring(0, 15), "§c[I] ",
				"");
	}
	
	public static void setSpecTag(Player player) {
		createTeamIfNotExists(player, player.getName(), "C" + player.getUniqueId().toString().substring(0, 15),
				"§8[E] ", "");
	}
	
	public static void setSpecTag(Player player, Player enemy) {
		createTeamIfNotExists(player, enemy.getName(), "C" + enemy.getUniqueId().toString().substring(0, 15),
				"§8[E] ", "");
	}

	public static void update(Player player) {
		setFriendlyTag(player);
		
		for (Player o : Bukkit.getOnlinePlayers()) {
			if (o.getUniqueId() == player.getUniqueId())
				continue;
			setEnemyTag(player, o);
			setEnemyTag(o, player);
		}
	}

	protected static void createTeamIfNotExists(Player player, String entrie, String teamID, String prefix,
			String suffix) {
		Team team = player.getScoreboard().getTeam(teamID);
		if (team == null) {
			team = player.getScoreboard().registerNewTeam(teamID);
		}
		if (!team.hasEntry(entrie))
			team.addEntry(entrie);
		team.setPrefix(prefix.length() > 16 ? prefix.substring(0, 16) : prefix);
		team.setSuffix(suffix.length() > 16 ? suffix.substring(0, 16) : suffix);
	}
}
