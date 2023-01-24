package br.com.weavenmc.commons.bungee.listeners;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.login.LoginCache;
import br.com.weavenmc.commons.bungee.login.LoginCacheAPI;
import br.com.weavenmc.commons.core.clan.Clan;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.punish.AccountBan;
import br.com.weavenmc.commons.core.data.punish.IpBan;
import br.com.weavenmc.commons.core.data.punish.MacBan;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.util.GeoIpUtils;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyAccountListener implements Listener {

	private ServerInfo lobby, login;

	public ProxyAccountListener() {
		ProxyServer proxy = ProxyServer.getInstance();

		lobby = proxy.getServerInfo("lobby");
		login = proxy.getServerInfo("login");

		if (lobby == null || login == null) {
			Exception ex = new NullPointerException("O Server lobby ou login nao foi encontrado");
			ex.printStackTrace();
			proxy.stop(ex.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST) 
	public void onLoginListener(LoginEvent event) {
		PendingConnection connection = event.getConnection();

		event.registerIntent(BungeeMain.getInstance());
		BungeeCord.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), () -> {
			long start = System.currentTimeMillis();

			String name = connection.getName();
			UUID uuid = connection.getUniqueId();

			ProxyPlayer pP = new ProxyPlayer(uuid, name); // login start
			WeavenMC.getAccountCommon().loadWeavenPlayer(pP.getUniqueId(), pP);

			// WeavenMC.debug("[" + (System.currentTimeMillis() - start) + "ms] ProxyPlayer
			// loading -> " + pP.getName()
			// + "(" + pP.getUniqueId() + ")");
			if (pP.load(DataCategory.ACCOUNT, DataCategory.BALANCE, DataCategory.CONNECTION,
					DataCategory.PERSONAL_DATA)) {
				WeavenMC.debug("[" + (System.currentTimeMillis() - start) + "ms] ProxyPlayer load completed -> "
						+ pP.getName() + "(" + pP.getUniqueId() + ")");

				pP.organizeGroups();
				pP.loadPermissions();

				pP.setLastIpAddress(pP.getIpAddress());
				pP.setIpAddress(connection.getAddress().getHostString());

				if (pP.getFirstLoggedIn() == 0L) {
					pP.setFirstLoggedIn(System.currentTimeMillis());
				}
				pP.setLastLoggedIn(System.currentTimeMillis());

				try {
					NetworkInterface network = NetworkInterface.getByInetAddress(connection.getAddress().getAddress());

					if (network != null) {
						byte[] macAddress = network.getHardwareAddress();

						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < macAddress.length; i++) {
							sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
						}

						if (!pP.getComputerAddress().isEmpty()) {
							pP.setLastComputerAddress(pP.getComputerAddress());
						}

						pP.setComputerAddress(sb.toString());
					}
				} catch (SocketException ex) {
					// cannot get mac address, ignore it
				}

				if (!pP.getClanName().equals("Nenhum")) {
					Clan clan = WeavenMC.getClanCommon().tryInCacheFromName(pP.getClanName());

					if (clan == null) {

						clan = WeavenMC.getClanCommon().tryInDatabaseFromName(pP.getClanName());

						if (clan == null) {
							pP.setClanName("Nenhum");
						}
					}
				}

				try {
					pP.setIpInfo(GeoIpUtils.getIpInfo(pP.getIpAddress()));

					pP.setCity(pP.getIpInfo().getCity());
					pP.setCountry(pP.getIpInfo().getCountry_name());
				} catch (IOException ex) {
					// cannot get ip info, ignore it
				}

				pP.save(DataCategory.ACCOUNT, DataCategory.CONNECTION);
			} else {
				WeavenMC.debug("[" + (System.currentTimeMillis() - start) + "] ProxyPlayer load failed -> "
						+ pP.getName() + "(" + pP.getUniqueId() + ")");

				event.setCancelReason("§4§lERRO§f Ocorreu um erro ao tentar carregar sua conta.");
				event.setCancelled(true);
			}

			if (!event.isCancelled()) // bans start
			{
				MacBan macBan = WeavenMC.getPunishHistory().getMacBan(pP.getComputerAddress());

				if (macBan == null) {
					IpBan ipBan = WeavenMC.getPunishHistory().getIpBan(pP.getIpAddress());

					if (ipBan == null) {
						AccountBan ban = WeavenMC.getPunishHistory().getAccountBan(pP.getUniqueId());

						if (ban != null) {
							if (!ban.isPermanent()) {
								if (!ban.hasExpired()) {
									event.setCancelReason("§6§lHYPE§f§lMC" + "\n\n"
											+ "§7Você está temporariamente banido do servidor.\n" + "§cMotivo: §7"
											+ ban.getReason() + "\n" + "§cBanido por: §7" + ban.getAuthor() + "\n"
											+ "§cTempo restante: §7"
											+ StringTimeUtils.formatDifference(ban.getDuration()) + "" + "\n\n"
											+ "§cAdquira Unban em: loja.hypemc.com.br\n"); // temporary
									event.setCancelled(true);
								} else {
									event.setCancelled(false);
									// ban has expired, so will allow joining
									BungeeCord.getInstance().getPluginManager().dispatchCommand(
											BungeeCord.getInstance().getConsole(),
											"unban " + pP.getName() + " AutoUnban - Ban expirado.");
								}
							} else {
								event.setCancelReason("§6§lHYPE§f§lMC" + "\n\n"
										+ "§7Você está permanentemente banido do servidor.\n" + "§cMotivo: §7"
										+ ban.getReason() + "\n" + "§cBanido por: §7" + ban.getAuthor() + "" + "\n\n"
										+ "§cAdquira Unban em: loja.hypemc.com.br\n"); // temporary);
																						// //
																						// permanent
								event.setCancelled(true);
							}
						} else {
							event.setCancelled(false);
							// dont find anybans, so will allow joining
						}
					} else {
						event.setCancelReason(
								"§6§lHYPE§f§lMC" + "\n\n" + "§7Você está permanentemente banido do servidor.\n"
										+ "§cMotivo: §7" + ipBan.getReason() + "\n" + "§cBanido por: §7"
										+ ipBan.getAuthor() + "" + "\n\n" + "§cAdquira Unban em: loja.hypemc.com.br\n"); // permanent
						event.setCancelled(true);
					}
				} else {
					event.setCancelReason(
							"§6§lHYPE§f§lMC" + "\n\n" + "§7Você está permanentemente banido do servidor.\n"
									+ "§cMotivo: §7" + macBan.getReason() + "\n" + "§cBanido por: §7"
									+ macBan.getAuthor() + "" + "\n\n" + "§cAdquira Unban em: loja.hypemc.com.br\n"); // permanent
					event.setCancelled(true);
				}
			} // bans end

			if (event.isCancelled()) {
				WeavenMC.getAccountCommon().unloadWeavenPlayer(connection.getUniqueId());
			} else {
				pP.setRecentMute(WeavenMC.getPunishHistory().getMute(pP.getUniqueId()));
			}

			event.completeIntent(BungeeMain.getInstance()); // login end
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLoginListener(PostLoginEvent event) {
		ProxiedPlayer p = event.getPlayer();
		ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());
		if (pP != null) {
			BungeeCord.getInstance().getScheduler().schedule(BungeeMain.getInstance(), () -> {
				if (p.getPendingConnection().isOnlineMode()) {
//					p.sendMessage(TextComponent.fromLegacyText("§aAutenticado como jogador original."));
				}
			}, 3, TimeUnit.SECONDS);
		} else {
			p.disconnect(TextComponent.fromLegacyText("§4§lERRO§f Sua conta está com problemas!"));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerConnected(ServerConnectedEvent event) {
		ProxiedPlayer p = event.getPlayer();
		ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());
		if (pP == null)
			return;
		NetworkServer server = BungeeMain.getInstance().getServerManager().getWeavenServer(event.getServer().getInfo());
		if (server != null) {
			pP.setServerConnected(server.getServerId());
			pP.setServerConnectedType(server.getServerType());
		}

		if (pP.getParty() == null)
			return;
		if (pP.getParty().getOwner() == p) {
			pP.getParty().sendMessage(
					"§aTodos jogadores foram puxados para §e" + event.getServer().getInfo().getName() + "§a!");
			pP.getParty().connectAll(event.getServer().getInfo().getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerConnectListener(ServerConnectEvent event) {
		ProxiedPlayer p = event.getPlayer();
		ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());
		if (pP != null) {
			ServerInfo current = (p.getServer() == null ? null : p.getServer().getInfo());
			ServerInfo target = event.getTarget();
			if (target.getName().toLowerCase().equals("login")) {
				if (!p.getPendingConnection().isOnlineMode()) {
					LoginCache cache = LoginCacheAPI.getCache(p.getAddress().getHostString());
					if (cache != null) {
						cache.removeSession(p.getName());
					}
				}
			} else if (!p.getPendingConnection().isOnlineMode()) {
				LoginCache cache = LoginCacheAPI.getCache(p.getAddress().getHostString());
				if (cache != null) {
					if (!cache.hasSession(p.getName())) {
						if (current != null && current.getName().toLowerCase().equals("login"))
							event.setCancelled(true);
						else
							event.setTarget(BungeeCord.getInstance().getServerInfo("login"));
						p.sendMessage(TextComponent
								.fromLegacyText("§4§lLOGIN§f Você não possui uma §c§lSESSAO§f ou ela §c§lEXPIROU!"));
					}
				} else {
					if (current != null && current.getName().toLowerCase().equals("login"))
						event.setCancelled(true);
					else
						event.setTarget(BungeeCord.getInstance().getServerInfo("login"));
					p.sendMessage(TextComponent
							.fromLegacyText("§4§lLOGIN§f Você não possui uma §c§lSESSAO§f ou ela §c§lEXPIROU!"));
				}
			}
		} else {
			p.disconnect(TextComponent.fromLegacyText("§4§lERRO§f Sua conta está com problemas!"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeaveListener(PlayerDisconnectEvent event) {
		WeavenMC.getAccountCommon().unloadWeavenPlayer(event.getPlayer().getUniqueId());
	}
}
