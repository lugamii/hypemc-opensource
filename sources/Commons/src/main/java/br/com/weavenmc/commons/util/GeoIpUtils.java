package br.com.weavenmc.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import br.com.weavenmc.commons.WeavenMC;
import lombok.Getter;

public class GeoIpUtils {

	private static final Map<String, IpInfo> CACHE = new HashMap<>();

	public static IpInfo getIpInfo(String ip) throws IOException {
		if (CACHE.containsKey(ip))
			return CACHE.get(ip);
		URLConnection con = new URL("https://geoip-db.com/json/" + ip).openConnection();
		IpInfo ipInfo = WeavenMC.getGson()
				.fromJson(new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8")), IpInfo.class);
		return CACHE.computeIfAbsent(ip, v -> ipInfo);
	}

	@Getter
	public static class IpInfo {
		private String country_code;
		private String country_name;
		private String city;
		private String postal;
		private String latitude;
		private String longitude;
		private String IPv4;
		private String state;
	}
}
