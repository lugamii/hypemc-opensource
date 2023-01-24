package br.com.weavenmc.commons.bukkit.api.vanish;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerHideEvent;
import br.com.weavenmc.commons.bukkit.event.vanish.PlayerShowEvent;
import br.com.weavenmc.commons.core.permission.Group;

public class VanishAPI {
	private HashMap<UUID, Group> vanishedToGroup;

	private final static VanishAPI instance = new VanishAPI();

	public VanishAPI() {
		vanishedToGroup = new HashMap<>();
	}

	public void setPlayerVanishToGroup(Player player, Group group) {
		if (group == null)
			vanishedToGroup.remove(player.getUniqueId());
		else
			vanishedToGroup.put(player.getUniqueId(), group);
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId()))
				continue;
			BukkitPlayer onlineP = BukkitPlayer.getPlayer(online.getUniqueId());
			if (group != null && onlineP.getGroup().getId() < group.getId()) {
				PlayerHideEvent event = new PlayerHideEvent(player, online);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					online.hidePlayer(player);
				}
				continue;
			}
			PlayerShowEvent event = new PlayerShowEvent(player, online);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				online.showPlayer(player);
			}
		}
	}

	public void updateVanishToPlayer(Player player) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId()))
				continue;
			Group group = vanishedToGroup.get(online.getUniqueId());
			if (group != null) {
				if (bP.getGroup().getId() < group.getId()) {
					PlayerHideEvent event = new PlayerHideEvent(online, player);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						player.hidePlayer(online);
					}
					continue;
				}
			}
			PlayerShowEvent event = new PlayerShowEvent(online, player);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				player.showPlayer(online);
			}
		}
	}

	public Group hidePlayer(Player player) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		setPlayerVanishToGroup(player, bP.getGroup());
		Group vanish = Group.fromId(bP.getGroup().getId() - 1);
		if (vanish == null)
			vanish = Group.MEMBRO;
		if (vanish == Group.DONO)
			vanish = Group.DIRETOR;
		return vanish;
	}

	public void showPlayer(Player player) {
		setPlayerVanishToGroup(player, null);
	}

	public void updateVanish(Player player) {
		setPlayerVanishToGroup(player, getVanishedToGroup(player.getUniqueId()));
	}

	public Group getVanishedToGroup(UUID uuid) {
		return vanishedToGroup.get(uuid);
	}

	public void removeVanish(Player p) {
		vanishedToGroup.remove(p.getUniqueId());
	}

	public static VanishAPI getInstance() {
		return instance;
	}
}
