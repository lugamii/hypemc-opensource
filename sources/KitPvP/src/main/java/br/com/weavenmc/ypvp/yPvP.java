package br.com.weavenmc.ypvp;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.hologram.SimpleTopHologram;
import br.com.weavenmc.ypvp.hologram.TopRankingHologram;
import br.com.weavenmc.ypvp.listeners.DamagerFixer;
import br.com.weavenmc.ypvp.listeners.PlayerListener;
import br.com.weavenmc.ypvp.listeners.SignListener;
import br.com.weavenmc.ypvp.managers.AbilityManager;
import br.com.weavenmc.ypvp.managers.CooldownManager;
import br.com.weavenmc.ypvp.managers.FeastManager;
import br.com.weavenmc.ypvp.managers.GamerManager;
import br.com.weavenmc.ypvp.managers.ScoreboardManager;
import br.com.weavenmc.ypvp.managers.WarpManager;
import br.com.weavenmc.ypvp.minigame.LocationManager;
import br.com.weavenmc.ypvp.tournament.Tournament;
import br.com.weavenmc.ypvp.util.FeastConfig;
import lombok.Getter;

@Getter
public class yPvP extends JavaPlugin {
	
	@Getter
	private static yPvP plugin;

	private PvPType pvpType;

	private Tournament tournament;

	// managers
	private GamerManager gamerManager;
	private WarpManager warpManager;
	private LocationManager locationManager;
	private AbilityManager abilityManager;
	private ScoreboardManager scoreboardManager;
	private CooldownManager cooldownManager;
	private FeastConfig feastConfig;
	private FeastManager feastManager;

	@Override
	public void onLoad() {
		plugin = this;
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		pvpType = PvPType.valueOf(getConfig().getString("type"));
		getLogger().info("PvP Type: " + pvpType.name());
		tournament = new Tournament();
		registerManagements();
		enableManagements();
		//locationManager.saveLocation("spawn", getServer().getWorlds().get(0).getSpawnLocation());
		getServer().getPluginManager().registerEvents(new DamagerFixer(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		new CommandLoader(new BukkitCommandFramework(this)).loadCommandsFromPackage("br.com.weavenmc.ypvp.commands");
		
		initHolograms();
		
	}
	
	private SimpleTopHologram topPVPKills;
	private TopRankingHologram topHologram;
	
	public void initHolograms() {
		if (getConfig().get("hologram.pvpkills.location") != null) {
			double x = getConfig().getDouble("hologram.pvpkills.location.x");
			double y = getConfig().getDouble("hologram.pvpkills.location.y");
			double z = getConfig().getDouble("hologram.pvpkills.location.z");
			float yaw = getConfig().getInt("hologram.pvpkills.location.yaw");
			float pitch = getConfig().getInt("hologram.pvpkills.location.pitch");
			Location loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);

			topPVPKills = new SimpleTopHologram("§B§lTOP 10 §e§lPVP KILLS", "KILLS", loc, 12, DataCategory.KITPVP,
					DataType.PVP_KILLS, 2400L);
			topPVPKills.spawn();
		} else {
			topPVPKills = new SimpleTopHologram("§B§lTOP 10 §e§lPVP KILLS", "KILLS",
					getServer().getWorlds().get(0).getSpawnLocation(), 12, DataCategory.KITPVP, DataType.PVP_KILLS,
					2400L);
			topPVPKills.spawn();
		}
		
		if (getConfig().get("hologram.toprank.location") != null) {
			double x = getConfig().getDouble("hologram.toprank.location.x");
			double y = getConfig().getDouble("hologram.toprank.location.y");
			double z = getConfig().getDouble("hologram.toprank.location.z");
			float yaw = getConfig().getInt("hologram.toprank.location.yaw");
			float pitch = getConfig().getInt("hologram.toprank.location.pitch");
			Location loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);

			topHologram = new TopRankingHologram("§B§lTOP 10 §e§lRANK", loc, 12, 1200L);
			topHologram.spawn();
		} else {
			topHologram = new TopRankingHologram("§B§lTOP 10 §e§lRANK",
					getServer().getWorlds().get(0).getSpawnLocation(), 12, 1200L);
			topHologram.spawn();
		}
	}

	@Override
	public void onDisable() {
		disableManagements();
	}

	private void registerManagements() {
		gamerManager = new GamerManager(this);
		warpManager = new WarpManager(this);
		locationManager = new LocationManager(this);
		abilityManager = new AbilityManager(this);
		scoreboardManager = new ScoreboardManager(this);
		cooldownManager = new CooldownManager(this);
		feastConfig = new FeastConfig(this, "yPvP");
		feastManager = new FeastManager();
		
	}
	
	private void enableManagements() {
		gamerManager.enable();
		warpManager.enable();
		locationManager.enable();
		abilityManager.enable();
		scoreboardManager.enable();
		cooldownManager.enable();
		feastManager.start();
	}

	private void disableManagements() {
		gamerManager.disable();
		warpManager.disable();
		locationManager.disable();
		abilityManager.disable();
		scoreboardManager.disable();
		cooldownManager.disable();
	}

	@Override
	public File getFile() {
		return super.getFile();
	}

	public enum PvPType {
		SIMULATOR, FULLIRON
	}
}
