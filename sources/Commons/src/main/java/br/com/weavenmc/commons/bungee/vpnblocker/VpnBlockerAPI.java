package br.com.weavenmc.commons.bungee.vpnblocker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.weavenmc.commons.WeavenMC;

public class VpnBlockerAPI {

	private final static Map<String, Proxy> proxies = new ConcurrentHashMap<>();
	public final static AtomicBoolean fullyLoaded = new AtomicBoolean();
	private final static Object sync = new Object();

	public static Proxy getOrCreateProxie(String ipAddress) {
		if (proxies.containsKey(ipAddress))
			return proxies.get(ipAddress);
		return proxies.computeIfAbsent(ipAddress, p -> new Proxy(ipAddress));
	}
	
	public static Proxy getProxie(String ipAddress) {
		return proxies.get(ipAddress);
	}

	public static void updateProxy(Proxy proxy, boolean update) {
		saveProxie(proxy, update);
	}

	public static void saveProxie(Proxy proxy, boolean i) {
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
			try {
				if (i) {
					PreparedStatement update = WeavenMC.getCommonMysql()
							.preparedStatement("UPDATE `proxies` SET `allowed`='" + String.valueOf(proxy.allowed)
									+ "' WHERE `address`='" + proxy.ipAddress + "';");
					update.execute();
					update.close();
				} else {
					PreparedStatement insert = WeavenMC.getCommonMysql()
							.preparedStatement("INSERT INTO `proxies` (`address`, `allowed`) VALUES ('"
									+ proxy.ipAddress + "', '" + String.valueOf(proxy.allowed) + "');");
					insert.execute();
					insert.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
	}
	
	public static Collection<Proxy> getProxies() {
		return proxies.values();
	}

	public static void load() throws SQLException {
		synchronized (sync) {
			WeavenMC.debug("Iniciando o carregamento de Todos os proxies...");

			PreparedStatement create = WeavenMC.getCommonMysql().preparedStatement(
					"CREATE TABLE IF NOT EXISTS `proxies` (`address` VARCHAR(40), `allowed` VARCHAR(5));");
			create.execute();
			create.close();

			PreparedStatement selecting = WeavenMC.getCommonMysql().preparedStatement("SELECT * FROM `proxies`");
			ResultSet selectResult = selecting.executeQuery();
			while (selectResult.next()) {
				String ipAddress = selectResult.getString("address");
				boolean allowed = Boolean.valueOf(selectResult.getString("allowed"));
				Proxy proxy = new Proxy(ipAddress);
				proxy.allowed = allowed;
				proxy.checked = true;
				proxy.checkError = false;
				proxy.saved = true;
				proxies.put(ipAddress, proxy);
			}
			selectResult.close();
			selecting.close();

			WeavenMC.debug("Todos os proxies foram carregados com sucesso!");
			fullyLoaded.set(true);
		}
	}
}
