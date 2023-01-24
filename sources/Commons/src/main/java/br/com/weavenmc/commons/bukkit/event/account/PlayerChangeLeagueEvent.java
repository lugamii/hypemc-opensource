package br.com.weavenmc.commons.bukkit.event.account;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;
import br.com.weavenmc.commons.core.account.League;
import lombok.Getter;

@Getter
public class PlayerChangeLeagueEvent extends PlayerCancellableEvent {

	private League currentLeague;
	private League futureLeague;
	
	public PlayerChangeLeagueEvent(Player player, League currentLeague, League futureLeague) {
		super(player);
		this.currentLeague = currentLeague;
		this.futureLeague = futureLeague;
	}
}
