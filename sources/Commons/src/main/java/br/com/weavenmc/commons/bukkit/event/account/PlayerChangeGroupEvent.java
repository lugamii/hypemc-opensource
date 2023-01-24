package br.com.weavenmc.commons.bukkit.event.account;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;
import br.com.weavenmc.commons.core.permission.Group;
import lombok.Getter;

@Getter
public class PlayerChangeGroupEvent extends PlayerCancellableEvent {

	private Group group;
	private long groupTime;
	
	public PlayerChangeGroupEvent(Player player, Group group, long groupTime) {
		super(player);
		this.group = group;
		this.groupTime = groupTime;
	}
}
