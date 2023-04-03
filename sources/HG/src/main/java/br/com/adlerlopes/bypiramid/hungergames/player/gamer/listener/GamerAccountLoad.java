package br.com.adlerlopes.bypiramid.hungergames.player.gamer.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;

/**
* Copyright (C) Adler Lopes, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerAccountLoad extends HungerListener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void login(PlayerLoginEvent event) {
		if (event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED)
			return;

		Player player = event.getPlayer();
		Gamer gamer = getManager().getGamerManager().getGamer(player.getUniqueId());

		getManager().getLogger().log("The player with uuid " + player.getUniqueId() + " and nickname "
				+ player.getName() + " logged into the server, starting to load her status.");

		if (gamer != null) {
			getManager().getLogger().log("The player with uuid " + player.getUniqueId() + "(" + player.getName()
					+ ") its already loaded, skipping a few processes.");
		} else {
			gamer = new Gamer(player);
			getManager().getGamerManager().addGamer(gamer);
			getManager().getLogger().log("The player with uuid " + player.getUniqueId() + "(" + player.getName()
					+ ") was loaded correctly.");
			gamer.unload();
		}
		gamer.updatePlayer(player);
		
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			if (bP.load(DataCategory.HUNGERGAMES)) {
				Gamer g = getManager().getGamerManager().getGamer(bP.getUniqueId());
				if (!g.isWinnerChecked()) {
					g.setWinner(bP.getData(DataType.HG_HAS_PENDENT_WINNER).asBoolean());
					bP.getData(DataType.HG_HAS_PENDENT_WINNER).setValue(false);
					bP.save(DataCategory.HUNGERGAMES);
				}
			} else {
				player.kickPlayer("§4§lERRO§f Ocorreu um erro ao tentar carregar sua conta!");
			}
		});
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!AdminMode.getInstance().isAdmin(player)) {

			if (getManager().getGameManager().isPreGame()) {

				if (player.getLocation().getBlockX() > Bukkit.getWorld("world").getSpawnLocation().getBlockX() + 100
						|| player.getLocation()
								.getBlockX() < -(100 - Bukkit.getWorld("world").getSpawnLocation().getBlockX())
						|| player.getLocation().getBlockZ() > Bukkit.getWorld("world").getSpawnLocation().getBlockZ()
								+ 100
						|| player.getLocation()
								.getBlockZ() < -(100 - Bukkit.getWorld("world").getSpawnLocation().getBlockZ())) {

					player.sendMessage(
							"§c§lBORDA §fVocê foi §4§lTELETRANSPORTADO§f para o §5§lSPAWN§f! Não se vá para muito §c§lLONGE");
					getManager().getGamerManager().teleportSpawn(player);

				}
			}

		}
	}

}
