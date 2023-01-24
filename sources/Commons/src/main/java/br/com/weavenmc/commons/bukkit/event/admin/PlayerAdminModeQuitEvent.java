package br.com.weavenmc.commons.bukkit.event.admin;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.PlayerCancellableEvent;

/**
 * 
 * Evento chamado quando um jogador vai sair do Modo Admin
 *
 */
public class PlayerAdminModeQuitEvent extends PlayerCancellableEvent {

	public PlayerAdminModeQuitEvent(Player player) {
		super(player);
	}

}
