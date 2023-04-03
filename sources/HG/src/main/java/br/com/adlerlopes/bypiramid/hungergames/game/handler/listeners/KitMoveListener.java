package br.com.adlerlopes.bypiramid.hungergames.game.handler.listeners;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KitMoveListener extends HungerListener {

	@EventHandler
	public void onPlayerKit(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		if (!getManager().getGameManager().isGame()) {
			return;
		}
		final Player player = (Player) e.getEntity();
		final Player playerDamager = (Player) e.getDamager();

		Gamer gamer = getManager().getGamerManager().getGamer(player);
		if (player == playerDamager) {
			return;
		}

		BossBarAPI.setBar(playerDamager,
				"§f" + player.getName() + " - " + gamer.getKit().getName() + " e " + gamer.getKit2().getName(), 6);
		BossBarAPI.setBar(player,
				"§f" + playerDamager.getName() + " - "
						+ getManager().getGamerManager().getGamer(playerDamager).getKit().getName() + " e "
						+ getManager().getGamerManager().getGamer(playerDamager).getKit2().getName(), 6);
	}
}
