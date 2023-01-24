package br.com.weavenmc.commons.bungee.party;

import java.util.ArrayList;
import java.util.List;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Party {
	@Getter
	private ProxiedPlayer owner;
	@Getter
	private List<ProxiedPlayer> members;

	public Party(ProxiedPlayer owner) {
		this.owner = owner;
		members = new ArrayList<>();
	}

	public int getSize() {
		return members.size();
	}

	public void addMember(ProxiedPlayer player) {
		members.add(player);
	}

	public void connectAll(String server) {
		members.forEach(player -> {
			player.connect(BungeeMain.getInstance().getProxy().getServerInfo(server));
		});
	}

	public boolean inParty(ProxiedPlayer proxiedPlayer) {
		return members.contains(proxiedPlayer);
	}

	public void disband() {
		sendMessage("O lider acabou com a party!");
		ProxyPlayer pp = (ProxyPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(owner.getUniqueId());
		pp.setParty(null);
		members.forEach(player -> {
			ProxyPlayer proxyPlayer = (ProxyPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
			proxyPlayer.setParty(null);
		});
		owner = null;
		members.clear();
		members = null;
	}

	public void removeMember(ProxiedPlayer player) {
		members.remove(player);
	}

	public void sendMessage(String string) {
		owner.sendMessage(TextComponent.fromLegacyText("§d§l[PARTY] §f" + string));
		members.forEach(ps -> ps.sendMessage(TextComponent.fromLegacyText("§d§l[PARTY] §f" + string)));
	}

}
