package br.com.weavenmc.commons.core.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.core.server.HardcoreGames.GameState;
import br.com.weavenmc.commons.core.server.Skywars.SkywarsState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NetworkServer {

	private String serverId;
	private int port;
	private final ServerType serverType;
	private int maxPlayers;
	private int players;
	private boolean joinEnabled, online;
	private long lastData;

	public boolean isFull() {
		return players >= maxPlayers;
	}

	public boolean canBeSelected() {
		return !isFull() && joinEnabled && online;
	}

	public void setServerId(String id) {
		this.serverId = id;
	}

	public void joinPlayer() {
		players++;
	}

	public void quitPlayer() {
		if (players <= 0) {
			players = 0;
			return;
		}
		players--;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("id", WeavenMC.getGson().toJsonTree(serverId));
		json.add("port", WeavenMC.getGson().toJsonTree(port));
		json.add("type", WeavenMC.getGson().toJsonTree(serverType.toString()));
		json.add("maxPlayers", WeavenMC.getGson().toJsonTree(maxPlayers));
		json.add("players", WeavenMC.getGson().toJsonTree(players));
		json.add("joinEnabled", WeavenMC.getGson().toJsonTree(String.valueOf(joinEnabled)));
		json.add("online", WeavenMC.getGson().toJsonTree(String.valueOf(online)));
		json.add("lastData", WeavenMC.getGson().toJsonTree(String.valueOf(lastData)));
		return json;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public static Skywars fromSkywarsJson(String json) {
		if (json == null || json.equals(""))
			return null;
		
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		return new Skywars(object.get("id").getAsString(), object.get("port").getAsInt(),
				ServerType.valueOf(object.get("type").getAsString()), object.get("maxPlayers").getAsInt(),
				object.get("players").getAsInt(), object.get("joinEnabled").getAsBoolean(),
				object.get("online").getAsBoolean(), object.get("lastData").getAsLong(), object.get("time").getAsInt(),
				object.get("playersLeft").getAsInt(), SkywarsState.valueOf(object.get("state").getAsString()));
	}

	public static PartyGames byJsonStringParty(String json) {
		if (json == null || json.equals(""))
			return null;
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		return new PartyGames(object.get("id").getAsString(), object.get("port").getAsInt(),
				ServerType.valueOf(object.get("type").getAsString()), object.get("maxPlayers").getAsInt(),
				object.get("players").getAsInt(), object.get("joinEnabled").getAsBoolean(),
				object.get("online").getAsBoolean(), object.get("lastData").getAsLong(), object.get("time").getAsInt(),
				object.get("playersLeft").getAsInt(), object.get("winner").getAsString(),
				br.com.weavenmc.commons.core.server.PartyGames.GameState.valueOf(object.get("state").getAsString()));
	}

	public static HardcoreGames byJsonString(String json) {
		if (json == null || json.equals(""))
			return null;
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		return new HardcoreGames(object.get("id").getAsString(), object.get("port").getAsInt(),
				ServerType.valueOf(object.get("type").getAsString()), object.get("maxPlayers").getAsInt(),
				object.get("players").getAsInt(), object.get("joinEnabled").getAsBoolean(),
				object.get("online").getAsBoolean(), object.get("lastData").getAsLong(), object.get("time").getAsInt(),
				object.get("playersLeft").getAsInt(), object.get("winner").getAsString(),
				GameState.valueOf(object.get("state").getAsString()));
	}

	public static NetworkServer fromJsonString(String json) {
		if (json == null || json.equals(""))
			return null;
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(json).getAsJsonObject();
		return new NetworkServer(object.get("id").getAsString(), object.get("port").getAsInt(),
				ServerType.valueOf(object.get("type").getAsString()), object.get("maxPlayers").getAsInt(),
				object.get("players").getAsInt(), object.get("joinEnabled").getAsBoolean(),
				object.get("online").getAsBoolean(), object.get("lastData").getAsLong());
	}
}
