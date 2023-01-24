package br.com.weavenmc.commons.bungee.listeners;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.core.clan.Clan;
import br.com.weavenmc.commons.core.data.punish.Mute;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

	@EventHandler
	public void onChatListener(ChatEvent event) {
		if (event.isCommand())
			return;
		if (event.isCancelled())
			return;
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;
		ProxiedPlayer p = (ProxiedPlayer) event.getSender();
		ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());
		if (player == null) {
			p.disconnect(TextComponent.fromLegacyText("§4§lERRO§f Sua conta está com problemas!"));
			return;
		}
		if (player.isStaffChatEnabled()) {
			if (player.isStaffer()) {
				if (player.getServerConnectedType() != ServerType.LOGIN) {
					event.setCancelled(true);
					sendStaffMessage(player, event.getMessage());
				} else {
					player.setStaffChatEnabled(false);
				}
			} else {
				player.setStaffChatEnabled(false);
			}
		} else if (player.isClanChatEnabled()) {
			Clan clan = WeavenMC.getClanCommon().getClanFromName(player.getClanName());
			if (clan != null) {
				if (player.getServerConnectedType() != ServerType.LOGIN) {
					if (clan.isMemberOfClan(player.getUniqueId())) {
						event.setCancelled(true);
						sendClanMessage(clan, player, event.getMessage());
					} else {
						player.setClanChatEnabled(false);
					}
				} else {
					player.setClanChatEnabled(false);
				}
			} else {
				player.setClanChatEnabled(false);
			}
		} else if (player.getRecentMute() != null) {
			Mute recent = player.getRecentMute();
			if (recent.isPermanent()) {
				event.setCancelled(true);
				p.sendMessage(TextComponent.fromLegacyText(
								"§fO jogador " + recent.getMutedBy() + " foi §3§lPERMANENTEMENTE MUTADO§f no servidor."));
			} else if (!recent.hasExpired()) {
				event.setCancelled(true);
				p.sendMessage(TextComponent.fromLegacyText("\n§4Você está temporariamente mutado.\n"
						+ "§fTempo restante: §e" + StringTimeUtils.formatDifference(recent.getMuteTime()) + "\n"
						+ "§fVocê pode ser desmutado adquirindo §bUNMUTE §fna nossa loja: loja.hypemc.com.br\n"));

			} else {
				player.setRecentMute(null);				
			}
		}
		p = null;
		player = null;
	}

	public void sendClanMessage(Clan clan, ProxyPlayer pP, String message) {
		String tag = "";
		if (clan.isOwner(pP.getUniqueId()))
			tag = " §f- §4DONO";
		else if (clan.isAdministrator(pP.getUniqueId()))
			tag = " §f- §cADMIN";
		String format = "§4[CLAN" + tag.toUpperCase() + "§4] §7" + pP.getName() + "§f: ";
		for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
			ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
			if (online == null)
				continue;
			if (!clan.isMemberOfClan(o.getUniqueId()))
				continue;
			o.sendMessage(TextComponent.fromLegacyText(format + message));
		}
	}

	public void sendStaffMessage(ProxyPlayer pP, String message) {
		String tag = pP.getGroup().getTagToUse().getPrefix();
		String format = tag + pP.getName() + "§f: ";
		for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
			ProxyPlayer online = ProxyPlayer.getPlayer(o.getUniqueId());
			if (online == null)
				continue;
			if (online.getGroup().getId() < Group.INVESTIDOR.getId())
				continue;
			if (online.getServerConnectedType() == ServerType.LOGIN)
				continue;
			if (!online.isStaffChatMessages())
				continue;
			o.sendMessage(TextComponent.fromLegacyText("§e§l[STAFF] " + format + message));
		}
	}
}
