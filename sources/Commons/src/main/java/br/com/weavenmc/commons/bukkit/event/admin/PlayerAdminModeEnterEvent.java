package br.com.weavenmc.commons.bukkit.event.admin;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;

/**
 * 
 * Evento chamado quando um jogador vai entrar no Modo Admin
 *
 */
public class PlayerAdminModeEnterEvent extends PlayerCancellableEvent {

	public PlayerAdminModeEnterEvent(Player player) {
		super(player);
	}

}
