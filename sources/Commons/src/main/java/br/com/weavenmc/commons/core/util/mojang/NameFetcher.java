package br.com.weavenmc.commons.core.util.mojang;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.com.weavenmc.commons.WeavenMC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NameFetcher {

	private List<String> apis = new ArrayList<>();

	private LoadingCache<UUID, String> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS)
			.build(new CacheLoader<UUID, String>() {
				@Override
				//@ParametersAreNonnullByDefault
				public String load(UUID uuid) throws Exception {
					return request(uuid);
				}
			});

	public NameFetcher() {
		apis.add("https://api.mojang.com/user/profiles/%s/names");
		apis.add("https://sessionserver.mojang.com/session/minecraft/profile/%s");
		apis.add("https://api.mcuuid.com/json/name/%s");
		apis.add("https://api.minetools.eu/uuid/%s");
	}

	private String request(UUID uuid) {
		return request(0, apis.get(0), uuid);
	}

	private String request(int idx, String api, UUID uuid) {
		try {
			URLConnection con = new URL(String.format(api, uuid.toString().replace("-", ""))).openConnection();
			JsonElement element = WeavenMC.getParser()
					.parse(new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
			if (element instanceof JsonArray) {
				JsonArray names = (JsonArray) element;
				JsonObject name = (JsonObject) names.get(names.size() - 1);
				if (name.has("name")) {
					return name.get("name").getAsString();
				}
			} else if (element instanceof JsonObject) {
				JsonObject object = (JsonObject) element;
				if (object.has("error") && object.has("errorMessage")) {
					throw new Exception(object.get("errorMessage").getAsString());
				} else if (object.has("name")) {
					return object.get("name").getAsString();
				}
			}
		} catch (Exception e) {
			idx++;
			if (idx < apis.size()) {
				api = apis.get(idx);
				return request(idx, api, uuid);
			}
		}

		return null;
	}

	public String getName(UUID uuid) {
		try {
			return cache.get(uuid);
		} catch (Exception e) {
			return null;
		}
	}

}
