package br.com.weavenmc.commons.bukkit.messenger;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.redis.BukkitRedisMessageEvent;
import br.com.weavenmc.commons.core.server.HardcoreGames;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.ServerType;

public class NetworkManager implements Listener, PluginMessageListener {

	private HashMap<String, NetworkServer> servers = new HashMap<>();

	public NetworkManager(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServer(BukkitRedisMessageEvent event) {
		NetworkServer server = null;
		if (event.getChannel().equals(WeavenMC.SERVER_INFO_CHANNEL))
			server = NetworkServer.fromJsonString(event.getMessage());
		else if (event.getChannel().equals(WeavenMC.HG_SERVER_INFO_CHANNEL))
			server = NetworkServer.byJsonString(event.getMessage());
		else if (event.getChannel().equals(WeavenMC.SW_SERVER_INFO_CHANNEL))
			server = NetworkServer.fromSkywarsJson(event.getMessage());
		if (server != null) {
			servers.put(server.getServerId(), server);
		}
	}

	public boolean isHardcoreGames(NetworkServer server) {
		return server instanceof HardcoreGames;
	}

	public NetworkServer getServer(String serverId) {
		for (NetworkServer server : servers.values()) {
			if (!server.getServerId().equals(serverId))
				continue;
			return server;
		}
		return null;
	}

	public int getOnlineCount(String serverId) {
		for (NetworkServer server : servers.values()) {
			if (!server.getServerId().equals(serverId))
				continue;
			if (server.getLastData() < System.currentTimeMillis())
				continue;
			return server.getPlayers();
		}
		return 0;
	}

	public int getNetworkOnlineCount() {
		int onlineCount = 0;
		for (NetworkServer server : servers.values()) {
			if (!server.getServerType().equals(ServerType.NETWORK))
				continue;
			onlineCount += server.getPlayers();
		}
		return onlineCount;
	}

	public int getOnlineCount(ServerType serverType) {
		int onlineCount = 0;
		for (NetworkServer server : servers.values()) {
			if (!server.getServerType().equals(serverType))
				continue;
			if (server.getLastData() < System.currentTimeMillis())
				continue;
			onlineCount += server.getPlayers();
		}
		return onlineCount;
	}

	public Collection<NetworkServer> getServers() {
		return servers.values();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equals("Commons")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			if (subchannel.equals("PersonalStatus")) {
				BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
				String statusType = in.readUTF();
				if (statusType.equals("YoutubeChannel")) {
					bP.setYoutubeChannel(in.readUTF());
				} else if (statusType.equals("Twitter")) {
					bP.setTwitter(in.readUTF());
				} else if (statusType.equals("Skype")) {
					bP.setSkype(in.readUTF());
				} else if (statusType.equals("Discord")) {
					bP.setDiscord(in.readUTF());
				} else if (statusType.equals("Twitch")) {
					bP.setTwitch(in.readUTF());
				}
				statusType = null;
				bP = null;
			}
			subchannel = null;
			in = null;
		}
	}
}
