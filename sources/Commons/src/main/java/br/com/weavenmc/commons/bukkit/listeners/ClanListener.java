package br.com.weavenmc.commons.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.redis.BukkitRedisMessageEvent;
import br.com.weavenmc.commons.core.clan.Clan;
import br.com.weavenmc.commons.core.clan.ClanMessage;

public class ClanListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClanMessage(BukkitRedisMessageEvent event) {
		if (!event.getChannel().equals(WeavenMC.CLAN_FIELD_UPDATE))
			return;
		ClanMessage msg = WeavenMC.getGson().fromJson(event.getMessage(), ClanMessage.class);
		switch (msg.readType()) {
		case LEAVE: {
			Clan clan = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
			if (clan != null) {
				for (Player o : Bukkit.getOnlinePlayers()) {
					BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
					if (!bP.getName().equalsIgnoreCase(msg.getUserToLeave()))
						continue;
					clan.kickMember(bP.getUniqueId());
				}
			}
			for (Player o : Bukkit.getOnlinePlayers()) {
				BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
				if (!bP.getName().equalsIgnoreCase(msg.getUserToLeave()))
					continue;
				bP.setClanName("Nenhum");
			}
			break;
		}
		case KICK: {
			Clan clan = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
			if (clan != null) {
				for (Player o : Bukkit.getOnlinePlayers()) {
					BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
					if (!bP.getName().equalsIgnoreCase(msg.getUserToLeave()))
						continue;
					clan.kickMember(bP.getUniqueId());
					bP.setClanName("Nenhum");
				}
			}
			for (Player o : Bukkit.getOnlinePlayers()) {
				BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
				if (!bP.getName().equalsIgnoreCase(msg.getUserKicked()))
					continue;
				bP.setClanName("Nenhum");
			}
			break;
		}
		case JOIN: {
			Clan clan = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
			if (clan != null) {
				for (Player o : Bukkit.getOnlinePlayers()) {
					BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
					if (!bP.getName().equalsIgnoreCase(msg.getUserToLeave()))
						continue;
					clan.addParticipant(bP);
				}
			}
			for (Player o : Bukkit.getOnlinePlayers()) {
				BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
				if (!bP.getName().equalsIgnoreCase(msg.getUserJoined()))
					continue;
				bP.setClanName(clan.getName());
			}
			break;
		}
		case CREATE: {
			WeavenMC.getAsynchronousExecutor().runAsync(() -> {
				Clan created = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
				if (created != null) {
					created.setOwnerName(msg.getUserMessageResponser());
					WeavenMC.getClanCommon().loadClan(created);
				}
				for (Player o : Bukkit.getOnlinePlayers()) {
					BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
					if (!bP.getName().equalsIgnoreCase(msg.getUserMessageResponser()))
						continue;
					bP.setClanName(msg.getClanName());
					bP.removeMoney(5000);
				}
			});
			break;
		}
		case DISBAND: {
			Clan disband = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
			if (disband != null)
				WeavenMC.getClanCommon().unloadClan(disband.getName());
			for (Player o : Bukkit.getOnlinePlayers()) {
				BukkitPlayer bP = BukkitPlayer.getPlayer(o.getUniqueId());
				if (!bP.getClanName().equalsIgnoreCase(msg.getClanName()))
					continue;
				bP.setClanName("Nenhum");
			}
			break;
		}
		case CHANGEABB: {
			Clan clan = WeavenMC.getClanCommon().getClanFromName(msg.getClanName());
			if (clan != null)
				clan.setAbbreviation(msg.getClanTag());
			for (Player o : Bukkit.getOnlinePlayers()) {
				BukkitPlayer bP = (BukkitPlayer) BukkitPlayer.getPlayer(o.getUniqueId());
				if (!bP.getName().equalsIgnoreCase(msg.getUserMessageResponser()))
					continue;
				bP.removeMoney(3000);
			}
			break;
		}
		default:
		}
	}
}
