package br.com.weavenmc.commons.bukkit.event.vanish;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;

/**
 * 
 * Evento chamado quando um jogador vai executar o metodo showPlayer()
 * durante o sistema de Vanish do modo admin
 *
 */
public class PlayerShowEvent extends PlayerCancellableEvent {
	
	private Player toShow;

	public PlayerShowEvent(Player player, Player toShow) {
		super(player);
		this.toShow = toShow;
	}

	/**
	 * Player que irá executar o metodo showPlayer()
	 * Ex: toShow.showPlayer( player )
	 */
	public Player getToShow() {
		return toShow;
	}
}
