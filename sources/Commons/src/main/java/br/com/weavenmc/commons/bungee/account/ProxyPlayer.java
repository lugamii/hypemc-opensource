package br.com.weavenmc.commons.bungee.account;

import java.util.UUID;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.party.Party;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.punish.Mute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyPlayer extends WeavenPlayer {
	
	private boolean staffChatMessages = true;
	private boolean staffChatEnabled = false;
	private boolean receiveReportEnabled = true;
	private boolean clanChatEnabled = false;
	private long reportCooldown = 0L;
	private Party party;
	private Mute recentMute;

	public ProxyPlayer(UUID uniqueId, String name) {
		super(uniqueId, name);
	}
	
	public void addMuteReport(int time) {
		reportCooldown = (time * 1000L + System.currentTimeMillis());
	}
	
	public boolean hasReportMute() {
		return reportCooldown >= System.currentTimeMillis();
	}
	
	public static ProxyPlayer getPlayer(UUID uuid) {
		return (ProxyPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(uuid);
	}
}
