package br.com.weavenmc.commons.bungee.manager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.event.redis.ProxyRedisMessageEvent;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.ServerType;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@Getter
public class ProxyManager implements Listener {

	private final BungeeMain plugin;
	private final HashMap<String, NetworkServer> servers = new HashMap<>();
	private final HashMap<String, ServerStatus> serversStatus = new HashMap<>();

	public ProxyManager(BungeeMain plugin) 
	{
		this.plugin = plugin;
		this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
		this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> 
		{
			plugin.getProxy().getScheduler().runAsync(getPlugin(), () -> 
			{
				ping(); //
				NetworkServer server = new NetworkServer("BungeeCord", -1, ServerType.NETWORK, 1000,
						getPlugin().getProxy().getOnlineCount(), true, true, (10 * 1000L) + System.currentTimeMillis());
				WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.SERVER_INFO_CHANNEL, server.toJson().toString());
			});
		}, 2, 2, TimeUnit.SECONDS);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRedisPubSub(ProxyRedisMessageEvent event) 
	{
		String channel = event.getChannel();
		String message = event.getMessage();
		
		if (channel.equals(WeavenMC.BROADCAST_SERVER_STARTING)) 
		{
			
			NetworkServer server = NetworkServer.fromJsonString(message);
			TextComponent connectMessage = new TextComponent("§e§nClique aqui para se conectar!");
			connectMessage
					.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + server.getServerId()));
			connectMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new TextComponent[] { new TextComponent("§6Clique para se conectar") }));
		} else {
			NetworkServer server = null;
			
			if (channel.equals(WeavenMC.SERVER_INFO_CHANNEL))
				server = NetworkServer.fromJsonString(message);
			else if (channel.equals(WeavenMC.HG_SERVER_INFO_CHANNEL))
				server = NetworkServer.byJsonString(message);
			else if (channel.equals(WeavenMC.SW_SERVER_INFO_CHANNEL))
				server = NetworkServer.fromSkywarsJson(message);
			else if (channel.equals(WeavenMC.CLAN_PREF)) {
				WeavenPlayer wp = WeavenMC.getAccountCommon().getWeavenPlayer(BungeeCord.getInstance().getPlayer(message).getUniqueId());
				wp.setClan(!wp.isClan());
				wp.getData(DataType.CLAN).setValue(wp.isClan());
			}
			if (server != null) 
			{
				servers.put(server.getServerId(), server);
			}
		}
	}

	public ServerInfo getAvailableServer(ServerType serverType) 
	{
		NetworkServer weavenServer = getAvailable(serverType);
		
		if (weavenServer != null)
			return getPlugin().getProxy().getServerInfo(weavenServer.getServerId());
		
		return null;
	}

	public NetworkServer getWeavenServer(ServerInfo serverInfo) 
	{
		for (NetworkServer server : servers.values()) 
		{
			if (!server.getServerId().equals(serverInfo.getName()))
				continue;
			return server;
		}
		return null;
	}

	public NetworkServer getAvailable(ServerType serverType)
	{
		NetworkServer weavenServer = null;		
		Integer mostConnections = -1;
		
		for (NetworkServer server : servers.values()) 
		{
			if (!server.getServerType().equals(serverType))
				continue;
			if (getServerStatus(server.getServerId()).equals(ServerStatus.OFFLINE))
				continue;
			if (!server.canBeSelected())
				continue;
			if (server.getPlayers() > mostConnections) {
				mostConnections = server.getPlayers();
				weavenServer = server;
			}
		}
		return weavenServer;
	}

	public ServerStatus getServerStatus(String serverId) {
		return serversStatus.get(serverId);
	}

	public NetworkServer getWeavenServer(String serverId) {
		return servers.get(serverId);
	}

	public enum ServerStatus {
		ONLINE, OFFLINE;
	}

	@SuppressWarnings("deprecation")
	public void ping(){
		try {
			for (ServerInfo server : getPlugin().getProxy().getServers().values()) {
				server.ping(new Callback<ServerPing>() {
					public void done(ServerPing ping, Throwable throwable) 
					{					
						if (throwable != null) 
						{
							serversStatus.put(server.getName(), ServerStatus.OFFLINE);
							
						} else {
							serversStatus.put(server.getName(), ServerStatus.ONLINE);
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
