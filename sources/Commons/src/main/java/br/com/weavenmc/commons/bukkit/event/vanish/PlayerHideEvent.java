package br.com.weavenmc.commons.bukkit.event.vanish;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;

/**
 * 
 * Evento chamado quando um jogador vai executar o metodo hidePlayer()
 * durante o sistema de Vanish do modo admin
 *
 */
public class PlayerHideEvent extends PlayerCancellableEvent {
	
	private Player toHide;

	public PlayerHideEvent(Player player, Player toHide) {
		super(player);
		this.toHide = toHide;
	}

	/**
	 * Player que irá executar o metodo hidePlayer()
	 * Ex: toHide.hidePlayer( player )
	 */
	public Player getToHide() {
		return toHide;
	}
}
