package br.com.mcweaven.gladiator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.mcweaven.gladiator.command.HologramCommand;
import br.com.mcweaven.gladiator.fight.controller.FightManager;
import br.com.mcweaven.gladiator.gamer.controller.GamerManager;
import br.com.mcweaven.gladiator.hologram.SimpleTopHologram;
import br.com.mcweaven.gladiator.listener.DamagerFixer;
import br.com.mcweaven.gladiator.listener.FightListener;
import br.com.mcweaven.gladiator.listener.PlayerListener;
import br.com.mcweaven.gladiator.scoreboard.ScoreboardManager;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import lombok.Getter;

public class Gladiator extends JavaPlugin {

	@Getter
	private static Gladiator instance;

	@Getter
	private GamerManager gamerManager;
	@Getter
	private GladiatorManager gladiatorManager;
	@Getter
	private FightManager fightManager;
	@Getter
	private ScoreboardManager scoreboardManager;
	@Getter
	private SimpleTopHologram topWinStreak, topWins;

	@Override
	public void onLoad() {
		instance = this;
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		Location loc = new Location(getServer().getWorlds().get(0), 0, 80, 0, 0, 0);

		topWins = new SimpleTopHologram("§b§lTOP 10 WINS", "wins", loc, 12, DataCategory.GLADIATOR,
				DataType.GLADIATOR_WINS, 3600L);
		topWinStreak = new SimpleTopHologram("§b§lTOP 10 WINSSTREAKS", "WS", loc, 12, DataCategory.GLADIATOR,
				DataType.GLADIATOR_WINSTREAK, 3600L);
		
		/*
		 * Registering listeners
		 */

		PluginManager pluginManager = Bukkit.getPluginManager();

		pluginManager.registerEvents(new PlayerListener(), getInstance());
		pluginManager.registerEvents(new DamagerFixer(), getInstance());
		pluginManager.registerEvents(new FightListener(), getInstance());

		/*
		 * Constructors
		 */

		gamerManager = new GamerManager();
		gladiatorManager = new GladiatorManager();
		fightManager = new FightManager();
		scoreboardManager = new ScoreboardManager();

		/*
		 * Other
		 */
		try {

			double x = getConfig().getDouble("hologram.wins.location.x");
			double y = getConfig().getDouble("hologram.wins.location.y");
			double z = getConfig().getDouble("hologram.wins.location.z");
			float yaw = getConfig().getInt("hologram.wins.location.yaw");
			float pitch = getConfig().getInt("hologram.wins.location.pitch");
			loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);

			topWins = new SimpleTopHologram("§b§lTOP 10 §e§lWINS", "WINS", loc, 12, DataCategory.GLADIATOR,
					DataType.GLADIATOR_WINS, 3600L);
			
			x = getConfig().getDouble("hologram.ws.location.x");
			y = getConfig().getDouble("hologram.ws.location.y");
			z = getConfig().getDouble("hologram.ws.location.z");
			yaw = getConfig().getInt("hologram.ws.location.yaw");
			pitch = getConfig().getInt("hologram.ws.location.pitch");
			loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);
			topWinStreak = new SimpleTopHologram("§b§lTOP 10 §e§lWINSSTREAKS", "WINS", loc, 12, DataCategory.GLADIATOR,
					DataType.GLADIATOR_WINSTREAK, 3600L);
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		topWins.spawn();
		topWinStreak.spawn();
		gladiatorManager.registerRecipes();
		Class<?> c = HologramCommand.class;
		if (CommandClass.class.isAssignableFrom(c))
			try {
				new BukkitCommandFramework(this).registerCommands((CommandClass) c.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		super.onEnable();
	}

	public void loadChunks() {
		World world = Bukkit.getWorld("world");
		for (int x = -1423; x <= 1423; x += 16) {
			for (int z = -1423; z <= 1423; z += 16) {
				if (!world.getBlockAt(x, 64, z).getChunk().isLoaded())
					world.getBlockAt(x, 64, z).getChunk().load(true);

			}
		}
	}

}
