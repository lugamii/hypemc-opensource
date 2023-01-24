package br.com.weavenmc.commons.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bungee.command.BungeeCommandFramework;
import br.com.weavenmc.commons.bungee.hostname.controller.HostnameManager;
import br.com.weavenmc.commons.bungee.listeners.ChatListener;
import br.com.weavenmc.commons.bungee.listeners.JoinListener;
import br.com.weavenmc.commons.bungee.listeners.MessageListener;
import br.com.weavenmc.commons.bungee.listeners.ProfileLookupListener;
import br.com.weavenmc.commons.bungee.listeners.ProxyAccountListener;
import br.com.weavenmc.commons.bungee.manager.ProxyManager;
import br.com.weavenmc.commons.bungee.redis.ProxyPubSubHandler;
import br.com.weavenmc.commons.bungee.vpnblocker.Proxy.VPNDetection;
import br.com.weavenmc.commons.core.backend.mysql.MySQL;
import br.com.weavenmc.commons.core.backend.mysql.MySQLBackend;
import br.com.weavenmc.commons.core.backend.redis.PubSubListener;
import br.com.weavenmc.commons.core.backend.redis.RedisBackend;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.data.punish.PunishHistory;
import br.com.weavenmc.commons.core.server.ServerType;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

@Getter
public class BungeeMain extends Plugin {
	@Getter
	private static BungeeMain instance;
	@Getter
	private static VPNDetection detection;

	private ProxyManager serverManager;

	private ProxyPubSubHandler redisPubSub;
	private PubSubListener pubSubListener;
	@Getter
	private HostnameManager hostnameManager;

	private int messagesIndex = 0;
	private String messagesPrefix;
	private List<String> messages;

	private MySQL mySQL;
	private Configuration config;

	public static boolean maintenance = false;

	@Override
	public void onLoad() {
		instance = this;
		loadConfigurations();
		detection = new VPNDetection(getConfig().getString("bungeecord.vpnblockerkey"));
		hostnameManager = new HostnameManager();

		WeavenMC.setLogger(getLogger());
		WeavenMC.setCommonMysql(new MySQLBackend(mySQL));

		String localhost = getConfig().getString("redis.hostname");
		String password = getConfig().getString("redis.password");
		int port = getConfig().getInt("redis.port");

		WeavenMC.setCommonRedis(new RedisBackend(localhost, password, port));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		WeavenMC.setAsynchronousExecutor(new BungeeAsynchronousExecutor(getProxy(), this));

		MySQLBackend commonMysql = WeavenMC.getCommonMysql();

		getLogger().info("Tentando abrir conexao com o MySQL");
		try {
			commonMysql.openConnection(); // mySQL Start
			getLogger().info("Conexão aberta com o MySQL");
		} catch (SQLException ex) {
			ex.printStackTrace();
			getProxy().stop(ex.getMessage());
			return;
		}

		WeavenMC.getCommonRedis().openConnection(); // Redis start

		if (!WeavenMC.getCommonRedis().isConnected()) {
			getProxy().stop("Redis connection closed!");
			return;
		}

		try {
			WeavenMC.setPunishHistory(new PunishHistory());
		} catch (SQLException ex) {
			ex.printStackTrace();
			getProxy().stop(ex.getMessage());
			return;
		}

		try {
			commonMysql.createTablesIfNotExists();
		} catch (SQLException ex) {
			ex.printStackTrace();
			getProxy().stop(ex.getMessage());
			return;
		}

		try {
			WeavenMC.getClanCommon().createTableIfNotExists();
		} catch (SQLException ex) {
			ex.printStackTrace();
			getProxy().stop(ex.getMessage());
			return;
		}

		getProxy().getScheduler().runAsync(this, () -> {
			WeavenMC.getProfileCommon().loadCrackedProfiles(); // asynchronous load of all Cracked Profiles
		});

		WeavenMC.getClanCommon().loadClans(); // asynchronous load of All Clans

		getProxy().registerChannel("Commons");
		ListenerInfo info = getProxy().getConfig().getListeners().iterator().next();
		WeavenMC.setServerAddress(info.getHost().getHostString() + ":" + info.getHost().getPort());
		WeavenMC.setServerId("network");
		WeavenMC.setServerType(ServerType.NETWORK);

		getProxy().getScheduler().runAsync(this,
				pubSubListener = new PubSubListener(redisPubSub = new ProxyPubSubHandler(),
						WeavenMC.SERVER_INFO_CHANNEL, WeavenMC.HG_SERVER_INFO_CHANNEL, WeavenMC.SW_SERVER_INFO_CHANNEL,
						WeavenMC.BROADCAST_SERVER_STARTING, WeavenMC.CLAN_FIELD_UPDATE,
						WeavenMC.GROUP_MANAGEMENT_CHANNEL));

		getProxy().getScheduler().schedule(this, () -> {
			serverManager = new ProxyManager(this);
		}, 1, TimeUnit.MILLISECONDS);

		registerListeners();

		new CommandLoader(new BungeeCommandFramework(this))
				.loadCommandsFromPackage("br.com.weavenmc.commons.bungee.command.register");

		messagesPrefix = config.getString("bungeecord.messages.prefix");//
		messages = config.getStringList("bungeecord.messages.broadcast");
		getProxy().getScheduler().schedule(this, () -> {
			JoinListener.ping.clear();
		}, 0, 1, TimeUnit.MINUTES);
		getProxy().getScheduler().schedule(this, () -> {
			if (messagesIndex >= messages.size())
				messagesIndex = 0;
			getProxy().broadcast(TextComponent
					.fromLegacyText(messagesPrefix.replace("&", "§") + messages.get(messagesIndex).replace("&", "§")));
			++messagesIndex;
		}, 2, 2, TimeUnit.MINUTES);
	}

	@Override
	public void onDisable() {
		try {
			WeavenMC.getCommonMysql().closeConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		WeavenMC.getCommonRedis().closeConnection();
	}

	private void registerListeners() {
		PluginManager pm = getProxy().getPluginManager();

		pm.registerListener(this, new ProfileLookupListener());
		pm.registerListener(this, new ProxyAccountListener());
		pm.registerListener(this, new JoinListener());
		pm.registerListener(this, new ChatListener());
		pm.registerListener(this, new MessageListener());
	}

	private void loadConfigurations() {
		try {

			if (!getDataFolder().exists()) {
				getDataFolder().mkdir();
			}
			File configFile = new File(getDataFolder(), "config.yml");
			if (!configFile.exists()) {
				try {
					configFile.createNewFile();
					try (InputStream is = getResourceAsStream("config.yml");
							OutputStream os = new FileOutputStream(configFile)) {
						ByteStreams.copy(is, os);
					}
				} catch (IOException e) {
					throw new RuntimeException("Unable to create configuration file", e);
				}
			}
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		String hostname = getConfig().getString("mysql.address");
		String database = getConfig().getString("mysql.database");
		String username = getConfig().getString("mysql.username");
		String password = getConfig().getString("mysql.password");
		int port = getConfig().getInt("mysql.port");

		mySQL = new MySQL(hostname, database, username, password, port);
	}
}
