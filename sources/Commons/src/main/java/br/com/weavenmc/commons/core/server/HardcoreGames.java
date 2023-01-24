package br.com.weavenmc.commons.core.server;

import com.google.gson.JsonObject;

import br.com.weavenmc.commons.WeavenMC;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HardcoreGames extends NetworkServer {

	private GameState state;
	private int time;
	private int playersLeft;
	private String winner;

	public HardcoreGames(String serverId, int port, ServerType serverType, int maxPlayers, int players,
			boolean joinEnabled, boolean online, long lastData, int time, int playersLeft, String winner, GameState state) {
		super(serverId, port, serverType, maxPlayers, players, joinEnabled, online, lastData);
		this.time = time;
		this.playersLeft = playersLeft;
		this.winner = winner;
		this.state = state;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("id", WeavenMC.getGson().toJsonTree(getServerId()));
		json.add("port", WeavenMC.getGson().toJsonTree(getPort()));
		json.add("type", WeavenMC.getGson().toJsonTree(getServerType().toString()));
		json.add("maxPlayers", WeavenMC.getGson().toJsonTree(getMaxPlayers()));
		json.add("players", WeavenMC.getGson().toJsonTree(getPlayers()));
		json.add("joinEnabled", WeavenMC.getGson().toJsonTree(String.valueOf(isJoinEnabled())));
		json.add("online", WeavenMC.getGson().toJsonTree(String.valueOf(isOnline())));
		json.add("lastData", WeavenMC.getGson().toJsonTree(String.valueOf(getLastData())));
		json.add("state", WeavenMC.getGson().toJsonTree(state.name()));
		json.add("time", WeavenMC.getGson().toJsonTree(time));
		json.add("playersLeft", WeavenMC.getGson().toJsonTree(playersLeft));
		json.add("winner", WeavenMC.getGson().toJsonTree(winner));
		return json;
	}

	@Override
	public boolean canBeSelected() {
		return super.canBeSelected() && !isInProgress()
				&& ((getState() == GameState.PREGAME && getTime() >= 1) || getState() == GameState.WAITING);
	}

	public boolean isInProgress() {
		return getState() == GameState.GAMETIME || getState() == GameState.INVINCIBILITY;
	}

	public enum GameState {

		WAITING, PREGAME, INVINCIBILITY, GAMETIME, ENDING, NONE
	}
}
