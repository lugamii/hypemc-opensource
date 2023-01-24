package br.com.weavenmc.commons.bungee.listeners;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.bungee.login.LoginCache;
import br.com.weavenmc.commons.bungee.login.LoginCacheAPI;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.profile.Profile;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProfileLookupListener implements Listener {

	public static boolean joinEnabled = true;
	@SuppressWarnings("unused")
	private void sendOwnerMessage(String msg) {
		for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
			ProxyPlayer pP = ProxyPlayer.getPlayer(p.getUniqueId());
			if (!pP.hasGroupPermission(Group.DONO))
				continue;
			p.sendMessage(TextComponent.fromLegacyText(msg));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProfileLookupListener(PreLoginEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!WeavenMC.getClanCommon().isClansLoaded() || !WeavenMC.getProfileCommon().isProfilesLoaded()) {
			event.setCancelled(true);
			event.setCancelReason("§c§lSTARTUP§f Aguarde o término da inicialização do sistema...");
			return;
		}
		
		PendingConnection connection = event.getConnection();


		if (connection.getName().length() < 3 || connection.getName().length() > 16
				|| !validateName(connection.getName()) || connection.getName().contains(" ")) {
			event.setCancelled(true);
			event.setCancelReason("§4§lERRO§f O nome de usuário '" + connection.getName() + "' é §c§lINVALIDO!");
			return;
		}

		if (!connection.isOnlineMode()) {
			event.registerIntent(BungeeMain.getInstance());
			BungeeCord.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), () -> {
				UUID online = WeavenMC.getUUIDOf(connection.getName());
				if (online != null) {
					connection.setOnlineMode(true);
				} else {
					boolean authenticated = false;
					LoginCache cache = LoginCacheAPI.getCache(connection.getAddress().getHostString());

					if (cache != null) {
						authenticated = cache.hasSession(connection.getName());
					}

					if (!joinEnabled && !authenticated) {
						event.setCancelled(true);
						event.setCancelReason(
								"§4§lPIRATE AUTHENTICATION\n\n§fA §c§lAUTENTICAÇAO§f para piratas está §c§lTEMPORARIAMENTE§f desativada!\n§fHá muitas autenticações sendo feitas por segundo!\n\n§4§lwww.mc-hype.com.br");
					} else {
						Profile profile = WeavenMC.getProfileCommon().createCrackedIfNotExists(connection.getName());
						if (profile == null) {
							event.setCancelReason("§4§lERRO§f Ocorreu um erro ao tentar criar ou carregar seu perfil!");
							event.setCancelled(true);
						} else {
							connection.setUniqueId(profile.getId());
						}
					}
				}
				event.completeIntent(BungeeMain.getInstance());
			});
		}
	}

	public boolean validateName(String username) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}
}
