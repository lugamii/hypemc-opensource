package br.com.weavenmc.commons.bungee.listeners;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.event.redis.ProxyRedisMessageEvent;
import br.com.weavenmc.commons.bungee.login.LoginCache;
import br.com.weavenmc.commons.bungee.login.LoginCacheAPI;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.permission.GroupMessage;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MessageListener implements Listener {
// tudo que o bungeee registra, nada achou porra
	@EventHandler
	public void onGroupMessage(ProxyRedisMessageEvent event) {
		if (event.getChannel().equals(WeavenMC.GROUP_MANAGEMENT_CHANNEL)) {
			GroupMessage msg = WeavenMC.getGson().fromJson(event.getMessage(), GroupMessage.class);
			Group group = msg.readGroup();
			if (msg.isAdd()) {
				for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
					ProxyPlayer pP = ProxyPlayer.getPlayer(o.getUniqueId());
					if (pP == null)
						continue;
					if (!pP.isStaffer())
						continue;
					if (pP.getServerConnectedType() == ServerType.LOGIN)
						continue;
					o.sendMessage(TextComponent.fromLegacyText("§6§lGROUPSET§f " + msg.getUserName()
							+ " recebeu o grupo " + group.getTagToUse().getName() + "§f com duração "
							+ (msg.getGroupTime() == -1 ? "§e§lETERNA"
									: "§e§l" + StringTimeUtils.formatDifference(msg.getGroupTime()))
							+ "§f via requisição de " + msg.getMessageResponse()));
				}
				UUID uuid = null;
				for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
					if (!o.getName().equalsIgnoreCase(msg.getUserName()))
						continue;
					uuid = o.getUniqueId();
					break;
				}
				if (uuid != null) {
					ProxyPlayer pP = ProxyPlayer.getPlayer(uuid);
					if (pP != null) {
						pP.addGroup(msg.readGroup(), msg.getGroupTime());
						pP.organizeGroups();
						pP = null;
					}
					uuid = null;
				}
			} else {
				for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
					ProxyPlayer pP = ProxyPlayer.getPlayer(o.getUniqueId());
					if (pP == null)
						continue;
					if (!pP.isStaffer())
						continue;
					if (pP.getServerConnectedType() == ServerType.LOGIN)
						continue;
					o.sendMessage(TextComponent.fromLegacyText(
							"§6§lGROUPSET§f " + msg.getUserName() + " teve o grupo " + group.getTagToUse().getName()
									+ "§f removido da conta via requisição de " + msg.getMessageResponse()));
				}
				UUID uuid = null;
				for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
					if (!o.getName().equalsIgnoreCase(msg.getUserName()))
						continue;
					uuid = o.getUniqueId();
					break;
				}
				if (uuid != null) {
					ProxyPlayer pP = ProxyPlayer.getPlayer(uuid);
					if (pP != null) {
						HashMap<Group, Long> map = pP.getGroupById(msg.getGroupId());
						if (map != null && map.keySet().toArray(new Group[] {})[0] == msg.readGroup()) {
							pP.removeGroup(msg.getGroupId());
							pP.organizeGroups();
						} else {
							ProxiedPlayer p = BungeeCord.getInstance().getPlayer(uuid);
							p.disconnect(TextComponent.fromLegacyText(
									"§4§lERRO§f Ocorreu um erro ao tentar atualizar dados em sua conta!"));
							p = null;
						}
						map = null;
						pP = null;
					}
					uuid = null;
				}
			}
			group = null;
			msg = null;
		}
	}

	@EventHandler
	public void onPluginMessageListener(PluginMessageEvent event) {
		if (!event.getTag().equals("BungeeCord") && !event.getTag().equals("Commons"))
			return;
		if (!(event.getSender() instanceof Server))
			return;
		if (!(event.getReceiver() instanceof ProxiedPlayer))
			return;
		ProxiedPlayer p = (ProxiedPlayer) event.getReceiver();
		ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
		String subChannel = in.readUTF();
		if (subChannel.equals("AuthenticationRequest")) {
			LoginCache cache = LoginCacheAPI.getCache(p.getAddress().getHostString());
			if (cache != null)
				cache.addSession(p.getName());
			else
				cache = LoginCacheAPI.addCache(p.getAddress().getHostString(), p.getName());
			p.sendMessage(TextComponent.fromLegacyText(
					"§4§lLOGIN§f Você agora sera §c§lAUTOMATICAMENTE AUTENTICADO§f neste nick por §c§l24 HORAS§f caso seu endereço de ip permaneça o mesmo."));
			NetworkServer networkserver = BungeeMain.getInstance().getServerManager().getAvailable(ServerType.LOBBY);
			if (networkserver != null) {
				ServerInfo serverinfo = BungeeCord.getInstance().getServerInfo(networkserver.getServerId());
				if (serverinfo != null) {
					callConnection(p, serverinfo);
				} else {
//					p.sendMessage(TextComponent
//							.fromLegacyText("§4§lERRO§f Nenhum servidor disponivel de §c§lLOBBY§f foi encontrado!"));
				}
			} else {
//				p.sendMessage(TextComponent
//						.fromLegacyText("§4§lERRO§f Nenhum servidor disponivel de §c§lLOBBY§f foi encontrado!"));
			}
		} else if (subChannel.equals("AntiCheatBan")) {
			String banReason = in.readUTF();
			BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
					"tempban " + p.getName() + " 30m " + banReason);
		} else if (subChannel.equals("FindServer")) {
			String serverTypeName = in.readUTF();
			ServerType serverType = ServerType.valueOf(serverTypeName);
			ServerInfo serverInfo = BungeeMain.getInstance().getServerManager().getAvailableServer(serverType);
			if (serverInfo != null) {
				p.connect(serverInfo);
			} else {
				p.sendMessage(TextComponent.fromLegacyText(
						"§4§lERRO §fNenhum servidor disponível de §c§l" + serverType.name() + "§f foi encontrado!"));
			}
		} else if (subChannel.equals("FastHG")) {
			ServerInfo fastHG = BungeeMain.getInstance().getServerManager().getAvailableServer(ServerType.DOUBLEKITHG);
			if (fastHG != null) {
				p.connect(fastHG);
			} else {
				ServerInfo lobby = BungeeCord.getInstance().getServerInfo("lobby");
				if (lobby != null) {
					p.connect(lobby);
				} else {
					lobby = BungeeMain.getInstance().getServerManager().getAvailableServer(ServerType.LOBBY);
					if (lobby != null) {
						p.connect(lobby);
					} else {
						p.disconnect(TextComponent.fromLegacyText(
								"§4§lDISCONNECTED\n\n§fNenhum servidor disponivel de §c§lDOUBLEKITHG§f ou §c§lLOBBY§f foi encontrado!\n\n§c§lwww.mc-hype.com.br"));
					}
				}
			}
		}
	}

	public void callConnection(ProxiedPlayer player, ServerInfo serverinfo) {
		BungeeCord.getInstance().getScheduler().schedule(BungeeMain.getInstance(), () -> {
			player.connect(serverinfo);
		}, 4, TimeUnit.SECONDS);
	}
}
