package br.com.weavenmc.commons.bungee.vpnblocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.BungeeMain;

public class Proxy {

	public String ipAddress;
	public AtomicBoolean checking;
	public long checkingtime = 0L;
	public boolean checked = false;
	public boolean checkError = false;
	public boolean allowed = false;
	public boolean saved = false;

	public Proxy(String ipAddress) {
		this.ipAddress = ipAddress;
		checking = new AtomicBoolean();
	}

	public void check() {
		checking.set(true);
		checkvpn();
	}

	public synchronized void checkvpn() {
		checkingtime = (15 * 1000L) + System.currentTimeMillis();
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			try {
				Response response = BungeeMain.getDetection().getResponse(ipAddress);
				if (response != null && response.status.equals("success")) {
					boolean result = !response.hostip;
					if (result) {
						checkproxy();
					} else {
						allowed = false;
						checked = true;
						checkError = false;
						checking.set(false);
					}
				} else {
					checked = true;
					checkError = true;
					checking.set(false);
				}
			} catch (Exception ex) {
				WeavenMC.debug("Proxy[" + ipAddress + "]" + " error -> " + ex.getMessage());
				checked = true;
				checkError = true;
				checking.set(false);
			}
		});
	}

	public void checkproxy() {
		try {
			String result = BungeeMain.getDetection().query("http://check.getipintel.net/check.php?ip=" + ipAddress
					+ "&contact=juiz@gmail.com&flags=m", 15000, "Proxy-Detection");
			if (result.equals("1")) {
				allowed = false;
				checked = true;
				checkError = false;
				checking.set(false);
			} else {
				allowed = true;
				checked = true;
				checkError = false;
				checking.set(false);
			}
		} catch (Exception ex) {
			WeavenMC.debug("Proxy[" + ipAddress + "]" + " error -> " + ex.getMessage());
			checked = true;
			checkError = true;
			checking.set(false);
		}
	}
	
	@Override
	public String toString() {
		return "Proxy[" + ipAddress + "]";
	}

	public static class VPNDetection {

		private String api_key = BungeeMain.getInstance().getConfig().getString("bungeecord.vpnblockerkey");
		private String api_url = "http://api.vpnblocker.net/v2/json/";
		private int api_timeout = 15000;

		public VPNDetection() {
			this.api_key = null;
		}

		public VPNDetection(String key) {
			this.api_key = key;
		}

		public VPNDetection(String key, int timeout) {
			this.api_key = key;
			this.api_timeout = timeout;
		}

		/**
		 * You can obtain a API key from: https://vpnblocker.net (optional)
		 *
		 * @param key
		 */
		public void set_api_key(String key) {
			this.api_key = key;
		}

		/**
		 * Units are in milliseconds Allows you to set the timeout of the API web
		 * request
		 *
		 * @param timeout
		 */
		public void set_api_timeout(int timeout) {
			this.api_timeout = timeout;
		}

		/**
		 * Allows you to use SSL on the API Query, you must have the appropriate package
		 * from the API provider in order to use this feature.
		 */
		public void useSSL() {
			this.api_url = this.api_url.replace("http://", "https://");
		}

		/**
		 * Queries the API server, gets the JSON result and parses using Gson.
		 *
		 * @param ip
		 * @return
		 * @throws IOException
		 */
		public Response getResponse(String ip) throws IOException {
			String query_url = this.get_query_url(ip);
			String query_result = this.query(query_url, this.api_timeout, "Java-VPNDetection Library");
			return new Gson().fromJson(query_result, Response.class);
		}

		/**
		 * The Generated API Query URL
		 *
		 * @param ip
		 * @return
		 */
		public String get_query_url(String ip) {
			String query_url;
			if (this.api_key == null) {
				query_url = this.api_url + ip;
			} else {
				query_url = this.api_url + ip + "/" + this.api_key;
			}
			return query_url;
		}

		/**
		 * Function that reads and returns the contents of a URL. Using the specified
		 * user agent and timeout when making the URL request.
		 *
		 * @param url
		 * @param timeout
		 * @param userAgent
		 * @return
		 * @throws MalformedURLException
		 * @throws IOException
		 */
		public String query(String url, int timeout, String userAgent) throws MalformedURLException, IOException {
			StringBuilder response = new StringBuilder();
			URL website = new URL(url);
			URLConnection connection = website.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setRequestProperty("User-Agent", userAgent);
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				while ((url = in.readLine()) != null) {
					response.append(url);
				}
				in.close();
			}

			return response.toString();
		}
	}

	public class Response {

		public String status;
		public String msg;

		@SerializedName("package")
		public String getPackage;

		public String remaining_requests;
		public String ipaddress;

		@SerializedName("host-ip")
		public boolean hostip;

		public String hostname;
		public String org;

		public CS country;
		public CS subdivision;

		public String city;
		public String postal;

		public latlon location;

		public class CS {
			public String name;
			public String code;
		}

		public class latlon {
			public double lat;

			@SerializedName("long")
			public double lon;
		}

	}
}
