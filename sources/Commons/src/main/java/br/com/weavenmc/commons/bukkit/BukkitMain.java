package br.com.weavenmc.commons.bukkit;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.hologram.Hologram;
import org.inventivetalent.hologram.HologramAPI;
import org.inventivetalent.hologram.HologramListeners;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.AccountMenu;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.bukkit.event.redis.ServerStatusUpdateEvent;
import br.com.weavenmc.commons.bukkit.injector.CustomPayloadInjector;
import br.com.weavenmc.commons.bukkit.injector.PacketLimiterInjector;
import br.com.weavenmc.commons.bukkit.listeners.AccountListener;
import br.com.weavenmc.commons.bukkit.listeners.ChatListener;
import br.com.weavenmc.commons.bukkit.listeners.ClanListener;
import br.com.weavenmc.commons.bukkit.listeners.PlayerListener;
import br.com.weavenmc.commons.bukkit.listeners.PlayerNBTListener;
import br.com.weavenmc.commons.bukkit.messenger.NetworkManager;
import br.com.weavenmc.commons.bukkit.protocol.ProtocolGetter;
import br.com.weavenmc.commons.bukkit.redis.BukkitPubSubHandler;
import br.com.weavenmc.commons.bukkit.scheduler.UpdateScheduler;
import br.com.weavenmc.commons.bukkit.scoreboard.tagmanager.TagManager;
import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;
import br.com.weavenmc.commons.core.backend.mysql.MySQL;
import br.com.weavenmc.commons.core.backend.mysql.MySQLBackend;
import br.com.weavenmc.commons.core.backend.redis.PubSubListener;
import br.com.weavenmc.commons.core.backend.redis.RedisBackend;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.data.punish.PunishHistory;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.ServerType;
import lombok.Getter;

@Getter
public class BukkitMain extends JavaPlugin {
	@Getter
	private static BukkitMain instance;

	private ProtocolManager procotolManager;
	private PubSubListener pubSubListener;

	private NetworkManager networkManager;

	private boolean willAnnounce = false;
	
	private AsyncWorldEdit worldEdit;

	private String mysqlAddress;
	private String mysqlDatabase;
	private String mysqlUsername;
	private String mysqlPassword;
	private int mysqlPort;

	@Getter
	private TagManager tagManager;

	@Override
	public void onLoad() {
		instance = this;
		saveDefaultConfig();
		loadConfigurations();

		WeavenMC.setLogger(getLogger());
		WeavenMC.setCommonMysql(
				new MySQLBackend(new MySQL(mysqlAddress, mysqlDatabase, mysqlUsername, mysqlPassword, mysqlPort)));
		
		String localhost = getConfig().getString("redis.hostname");
		String password = getConfig().getString("redis.password");
		int port = getConfig().getInt("redis.port");
		
		WeavenMC.setCommonRedis(new RedisBackend(localhost, password, port));
		procotolManager = ProtocolLibrary.getProtocolManager();
	}

	@Override
	public void onEnable() {
		WeavenMC.setAsynchronousExecutor(new BukkitAsynchronousExecutor(getServer(), this));
		procotolManager.addPacketListener(new CustomPayloadInjector(this));
		PacketLimiterInjector packetLimiterInjector = new PacketLimiterInjector();
		packetLimiterInjector.inject(this);
		
		MySQLBackend commonMysql = WeavenMC.getCommonMysql();

		getLogger().info("Tentando abrir conexao com o MySQL");
		try {
			commonMysql.openConnection(); // mySQL Start
			
			PreparedStatement preparedStatement = commonMysql
					.preparedStatement("CREATE TABLE IF NOT EXISTS `vip_test` (`uuid` VARCHAR(50));");
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			getServer().shutdown();
			return;
		}

		WeavenMC.getCommonRedis().openConnection(); // Redis start

		if (!WeavenMC.getCommonRedis().isConnected()) {
			getServer().shutdown();
			return;
		}

		try {
			WeavenMC.setPunishHistory(new PunishHistory());
		} catch (SQLException ex) {
			ex.printStackTrace();
			getServer().shutdown();
			return;
		}

		try {
			commonMysql.createTablesIfNotExists();
		} catch (SQLException ex) {
			ex.printStackTrace();
			getServer().shutdown();
			return;
		}

		try {
			WeavenMC.getClanCommon().createTableIfNotExists();
		} catch (SQLException ex) {
			ex.printStackTrace();
			getServer().shutdown();
			return;
		}

		getServer().getScheduler().runTaskAsynchronously(this, () -> {
			WeavenMC.getProfileCommon().loadCrackedProfiles(); // asynchronous load of all Cracked Profiles
		});

		WeavenMC.getClanCommon().loadClans(); // asynchronous load of All Clans

		WeavenMC.setServerAddress(getServer().getIp() + ":" + getServer().getPort());
		WeavenMC.setServerId(getConfig().getString("server.id"));
		WeavenMC.setServerType(ServerType.valueOf(getConfig().getString("server.type")));
		WeavenMC.debug("Weaven Server carregado. ServerId: " + WeavenMC.getServerId());
		WeavenMC.debug("Server type: " + WeavenMC.getServerType().name());
		ProtocolGetter.foundDependencies();
		if (ProtocolGetter.isProtocolSupport())
			HologramAPI.enableProtocolSupport();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "Commons");
		getServer().getMessenger().registerIncomingPluginChannel(this, "Commons",
				networkManager = new NetworkManager(this));
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", networkManager);
		
		registerManagements();
		enableManagements();
		registerListener();
		getServer().getScheduler().runTaskLater(this, () -> {
			for (OfflinePlayer operator : getServer().getOperators()) {
				if (operator.getPlayer() == null || !operator.isOnline()) {
					operator.setOp(false);
				}
			}
		}, 30L);
		
		willAnnounce = getConfig().getBoolean("server.announce_start");
		getServer().getScheduler().runTaskTimer(this, new UpdateScheduler(), 1L, 1L);
		getServer().getScheduler().runTaskLater(this,
				() -> unregisterCommands("pl", "plugins", "icanhasbukkit", "ver", "version", "?", "help", "viaversion",
						"viaver", "vvbukkit", "protocolsupport", "ps", "holograms", "hd", "holo", "hologram", "ban",
						"me", "say", "about", "pardon", "pardon-ip", "ban-ip"),
				2L);
		
		getServer().getScheduler().runTaskAsynchronously(this,
				pubSubListener = new PubSubListener(new BukkitPubSubHandler(), WeavenMC.SERVER_INFO_CHANNEL,
						WeavenMC.HG_SERVER_INFO_CHANNEL, WeavenMC.SW_SERVER_INFO_CHANNEL, WeavenMC.BROADCAST_SERVER_STARTING, WeavenMC.CLAN_FIELD_UPDATE,
						WeavenMC.GROUP_MANAGEMENT_CHANNEL));
		
		new CommandLoader(new BukkitCommandFramework(this))
		.loadCommandsFromPackage("br.com.weavenmc.commons.bukkit.command.register");

		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			if (WeavenMC.getCommonRedis().isConnected()) {
				NetworkServer server = new NetworkServer(WeavenMC.getServerId(), getServer().getPort(),
						WeavenMC.getServerType(), getServer().getMaxPlayers(), getServer().getOnlinePlayers().size(),
						!getServer().hasWhitelist(), true, (10 * 1000L) + System.currentTimeMillis());
				ServerStatusUpdateEvent event = new ServerStatusUpdateEvent(server, WeavenMC.SERVER_INFO_CHANNEL);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					WeavenMC.getCommonRedis().getJedis().publish(event.getMessageChannel(),
							event.getWeavenServer().toJson().toString());
					checkAnnounce(event.getWeavenServer());
				}
			}
		}, 20L, 20L);
	}
	
	@Override
	public File getFile() {
		return super.getFile();
	}
	
	public void setWorldEdit(AsyncWorldEdit worldEdit) {
		if (this.worldEdit != null)
			throw new IllegalStateException("Cannot set WorldEdit twice!");
		this.worldEdit = worldEdit;
	}

	@Override
	public void onDisable() {
		for (Player o : Bukkit.getOnlinePlayers())
			o.kickPlayer(
					"§3§lRESTART§f O servidor está sendo §b§lREINICIADO§f!\n\nProblemas técnicos ou manutenção pode ser a causa.");
		for (Hologram h : HologramAPI.getHolograms())
			HologramAPI.removeHologram(h);
		try {
			WeavenMC.getCommonMysql().closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disableManagements();
	}

	public Object getInConfig(String where) {
		return getConfig().get(where);
	}

	public void registerInConfig(String where, Object toSet) {
		getConfig().set(where, toSet);
		saveConfig();
	}

	private void loadConfigurations() {
		mysqlAddress = getConfig().getString("mysql.address");
		mysqlDatabase = getConfig().getString("mysql.database");
		mysqlUsername = getConfig().getString("mysql.username");
		mysqlPassword = getConfig().getString("mysql.password");
		mysqlPort = getConfig().getInt("mysql.port");
	}

	private void registerManagements() {
		tagManager = new TagManager(this);
	}

	private void enableManagements() {
		tagManager.onEnable();
	}

	private void disableManagements() {
		tagManager.onDisable();
		tagManager = null;
	}
	
	private void registerListener() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new AccountListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new ClanListener(), this);
		pm.registerEvents(new PlayerNBTListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new HologramListeners(), this);
		//pm.registerEvents(new AntiCheatModules(), this);
		pm.registerEvents(new AccountMenu(), this);

		pm.registerEvents(new BossBarAPI(), this);
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new CustomChunkGenerator();
	}

	private void checkAnnounce(NetworkServer server) {
		if (willAnnounce) {
			for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
				if (!plugin.isEnabled()) {
					return;
				}
			}
			if (!WeavenMC.getClanCommon().isClansLoaded() || !WeavenMC.getProfileCommon().isProfilesLoaded())
				return;
			willAnnounce = false;
			WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.BROADCAST_SERVER_STARTING,
					server.toJson().toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void unregisterCommands(String... commands) {
		try {
			Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f1.setAccessible(true);
			CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
			Field f2 = commandMap.getClass().getDeclaredField("knownCommands");
			f2.setAccessible(true);
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);
			for (String command : commands) {
				if (knownCommands.containsKey(command)) {
					knownCommands.remove(command);
					List<String> aliases = new ArrayList<>();
					for (String key : knownCommands.keySet()) {
						if (!key.contains(":"))
							continue;
						String substr = key.substring(key.indexOf(":") + 1);
						if (substr.equalsIgnoreCase(command)) {
							aliases.add(key);
						}
					}
					for (String alias : aliases) {
						knownCommands.remove(alias);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected class CustomChunkGenerator extends ChunkGenerator {

		@Override
		public List<BlockPopulator> getDefaultPopulators(World world) {
			return Arrays.asList(new BlockPopulator[0]);
		}

		@Override
		public boolean canSpawn(World world, int x, int z) {
			return true;
		}

		public int xyzToByte(int x, int y, int z) {
			return (x * 16 + z) * 128 + y;
		}

		@SuppressWarnings("deprecation")
		@Override
		public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
			byte[] result = new byte[32768];
			if ((chunkx == 0) && (chunkz == 0)) {
				result[xyzToByte(0, 64, 0)] = ((byte) Material.BEDROCK.getId());
			}
			return result;
		}

		@Override
		public Location getFixedSpawnLocation(World world, Random random) {
			return new Location(world, 0.0D, 66.0D, 0.0D);
		}
	}
}
