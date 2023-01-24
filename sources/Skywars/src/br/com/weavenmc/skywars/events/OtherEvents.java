package br.com.weavenmc.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.event.redis.ServerStatusUpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.Skywars;
import br.com.weavenmc.commons.core.server.Skywars.SkywarsState;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.nametag.TagAPI;
import br.com.weavenmc.timer.Iniciando;
import br.com.weavenmc.timer.Jogo;

public class OtherEvents implements Listener {

	@EventHandler
	public void update(UpdateEvent event) {
		if (event.getType() != UpdateType.TICK)
			return;
		if (Bukkit.getOnlinePlayers().size() == 0)
			return;
		if (WeavenSkywars.getGameManager().getState() != GameState.LOBBY) {
			for (Player o : Bukkit.getOnlinePlayers()) {
				TagAPI.update(o);
			}
		}
	}

	@EventHandler
	public void onUpdateServer(ServerStatusUpdateEvent e) {// olha aqui os espec para fazer se tem algum bug"
		NetworkServer server = e.getWeavenServer();
		int players = GameController.player.size();
		int leftPlayers = 12 - players;
		int time = WeavenSkywars.getGameManager().getState() == GameState.LOBBY ? Iniciando.timer : Jogo.timer;
		SkywarsState skywarsState = SkywarsState.valueOf(WeavenSkywars.getGameManager().getState().name().toUpperCase());
		if (skywarsState != SkywarsState.LOBBY)
			leftPlayers = 0;
		Skywars skywars = new Skywars(server.getServerId(), server.getPort(), server.getServerType(), 30,
				server.getPlayers(), true, true, System.currentTimeMillis() + (8 * 1000L), time, leftPlayers, skywarsState);
		e.setWeavenServer(skywars); //
		e.setMessageChannel(WeavenMC.SW_SERVER_INFO_CHANNEL); // 
	}

}
