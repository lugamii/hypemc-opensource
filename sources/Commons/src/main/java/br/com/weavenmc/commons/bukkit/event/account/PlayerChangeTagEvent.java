package br.com.weavenmc.commons.bukkit.event.account;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerChangeTagEvent extends PlayerCancellableEvent {
	
	private String teamID;
	private String tagPrefix;
	private String tagSuffix;

	public PlayerChangeTagEvent(Player player, String teamID, String tagPrefix, String tagSuffix) {
		super(player);
		this.teamID = teamID;
		this.tagPrefix = tagPrefix;
		this.tagSuffix = tagSuffix;
	}
}
