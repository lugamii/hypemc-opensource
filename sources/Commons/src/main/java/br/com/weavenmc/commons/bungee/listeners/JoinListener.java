package br.com.weavenmc.commons.bungee.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.event.player.PlayerJoinEvent;

import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringMaker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinListener implements Listener {

	private String mainMotd;
	private List<String> motds;
	public static HashMap<String, Integer> ping = new HashMap<>();

	public JoinListener() {
		mainMotd = BungeeMain.getInstance().getConfig().getString("bungeecord.motds.main");
		motds = BungeeMain.getInstance().getConfig().getStringList("bungeecord.motds.random");
	}

	@EventHandler
	public void onPlayerPostLogin(PostLoginEvent event) {

		ProxyPlayer player = ProxyPlayer.getPlayer(event.getPlayer().getUniqueId());

		if (BungeeMain.maintenance) {
			if (!player.hasGroupPermission(Group.TRIAL)) {

				event.getPlayer().disconnect(TextComponent.fromLegacyText(
						"§4§lMAINTENANCE\n§f\n§fO servidor está em manutenção, tente novamente mais tarde!\n§f\n§6www.hypemc.com.br"));

			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event) {

		ProxyPlayer player = ProxyPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (player == null)
			return;
		if (player.getParty() != null) {
			if (player.getParty().getOwner() == event.getPlayer()) {
				player.getParty().disband();
			} else {
				player.getParty().sendMessage("§c" + event.getPlayer().getName() + " saiu da party!");
				player.getParty().removeMember(event.getPlayer());
				player.setParty(null);
			}
		}
	}

	@EventHandler
	public void onPlayerLogin(LoginEvent event) {

		if (event.getConnection().getVersion() > 48) {
			event.setCancelled(true);
			event.setCancelReason(TextComponent.fromLegacyText(
					"§6§lHYPE§F§LMC\n§F\n§fAtualmente versões maiores que a §a1.8§f estão instáveis, tente utilizar uma versão inferior!"));
		}
		String hostname = event.getConnection().getVirtualHost().getHostName();

		BungeeMain.getInstance().getHostnameManager().getHostname(hostname).addConnections(1);

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPing(ProxyPingEvent event) {
		String ipAddress = event.getConnection().getAddress().getHostString();
		if (ping.containsKey(ipAddress)) {
			ping.put(ipAddress, ping.get(ipAddress) + 1);
		} else {
			ping.put(ipAddress, 1);
		}

		if (ping.get(ipAddress) > 20) {
			event.setResponse(null);
			return;
		}

		if (!BungeeMain.maintenance) {

			String motd = motds.get(new Random().nextInt(motds.size() - 1)).replace("&", "§");
			event.setResponse(new ServerPing(event.getResponse().getVersion(),
					new Players(1000, BungeeCord.getInstance().getOnlineCount(),
							new PlayerInfo[] { new PlayerInfo("", "") }),
					StringMaker.makeCenteredMotd(mainMotd.replace("&", "§")) + "\n"
							+ StringMaker.makeCenteredMotd(motd),
					event.getResponse().getFaviconObject()));
		} else {
			Protocol protocol = event.getResponse().getVersion();
			protocol.setName("Manutenção");
			protocol.setProtocol(9999);
//			String motd = motds.get(new Random().nextInt(motds.size() - 1)).replace("&", "§");
			event.setResponse(new ServerPing(protocol,
					new Players(1000, BungeeCord.getInstance().getOnlineCount(),
							new PlayerInfo[] { new PlayerInfo("", "") }),
					StringMaker.makeCenteredMotd(mainMotd.replace("&", "§")) + "\n"
							+ StringMaker.makeCenteredMotd("§fEstamos em §4§lMANUTENÇÃO§f!"),
					event.getResponse().getFaviconObject()));
		}
	}
}
