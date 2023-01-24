package br.com.weavenmc.lobby;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.hologram.Hologram;
import org.inventivetalent.hologram.HologramAPI;

import br.com.weavenmc.commons.bukkit.command.BukkitCommandFramework;
import br.com.weavenmc.commons.core.command.CommandLoader;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.lobby.hologram.SimpleTopHologram;
import br.com.weavenmc.lobby.hologram.TopRankingHologram;
import br.com.weavenmc.lobby.listeners.PlayerListener;
import br.com.weavenmc.lobby.managers.CosmeticManager;
import br.com.weavenmc.lobby.managers.GamerManager;
import br.com.weavenmc.lobby.managers.MenuManager;
import br.com.weavenmc.lobby.managers.NpcManager;
import br.com.weavenmc.lobby.managers.ScoreboardManager;
import br.com.weavenmc.lobby.managers.StoreManager;
import lombok.Getter;

@Getter
public class Lobby extends JavaPlugin {

	@Getter
	private static Lobby plugin;

	private boolean wasUpdatedLastTry = false;
	private Location hologramInfoLocation;

	private Hologram seasonInfoHologram;
	private Hologram[] seasonInfoHologramLines;

	private TopRankingHologram topHologram;
	private SimpleTopHologram topHGWins, topPVPKills;

	private String lobbyId;
	


	private GamerManager gamerManager;
	private ScoreboardManager scoreboardManager;
	private MenuManager menuManager;
	private CosmeticManager cosmeticManager;
	private StoreManager storeManager;
	private NpcManager npcManager;

	@Override
	public void onLoad() {
		plugin = this;
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		lobbyId = getConfig().getString("lobby_id");
		registerManagements();
		enableManagements();
		
		new CommandLoader(new BukkitCommandFramework(this)).loadCommandsFromPackage("br.com.weavenmc.lobby.commands");
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		checkTopHologram();
		checkTopHolograms();
		// checkInfoHologram();
	}

	@Override
	public void onDisable() {
		disableManagements();
	}

	public File getFile() {
		return super.getFile();
	}

	public void checkTopHolograms() {
		if (getConfig().get("hologram.pvpkills.location") != null) {
			double x = getConfig().getDouble("hologram.pvpkills.location.x");
			double y = getConfig().getDouble("hologram.pvpkills.location.y");
			double z = getConfig().getDouble("hologram.pvpkills.location.z");
			float yaw = getConfig().getInt("hologram.pvpkills.location.yaw");
			float pitch = getConfig().getInt("hologram.pvpkills.location.pitch");
			Location loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);

//			topPVPKills = new SimpleTopHologram("§B§lTOP 10 §e§lPVP KILLS", "KILLS", loc, 12, DataCategory.KITPVP,
//					DataType.PVP_KILLS, 2400L);
//			topPVPKills.spawn();
		} else {
//			topPVPKills = new SimpleTopHologram("§B§lTOP 10 §e§lPVP KILLS", "KILLS",
//					getServer().getWorlds().get(0).getSpawnLocation(), 12, DataCategory.KITPVP, DataType.PVP_KILLS,
//					2400L);
//			topPVPKills.spawn();
		}

		if (getConfig().get("hologram.hgwins.location") != null) {
			double x = getConfig().getDouble("hologram.hgwins.location.x");
			double y = getConfig().getDouble("hologram.hgwins.location.y");
			double z = getConfig().getDouble("hologram.hgwins.location.z");
			float yaw = getConfig().getInt("hologram.hgwins.location.yaw");
			float pitch = getConfig().getInt("hologram.hgwins.location.pitch");
			Location loc = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);

			topHGWins = new SimpleTopHologram("§B§lTOP 10 §e§lHG WINS", "WINS", loc, 12, DataCategory.HUNGERGAMES,
					DataType.HG_WINS, 3600L);
			topHGWins.spawn();
		} else {
			topHGWins = new SimpleTopHologram("§B§lTOP 10 §e§lHG WINS", "WINS",
					getServer().getWorlds().get(0).getSpawnLocation(), 12, DataCategory.HUNGERGAMES, DataType.HG_WINS,
					3600L);
			topHGWins.spawn();
		}
	}

	public void checkTopHologram() {
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

	public void checkInfoHologram() {
		try {
			double x = getConfig().getDouble("hologram_info_season3.x");
			double y = getConfig().getDouble("hologram_info_season3.y");
			double z = getConfig().getDouble("hologram_info_season3.z");
			float yaw = getConfig().getInt("hologram_info_season3.yaw");
			float pitch = getConfig().getInt("hologram_info_season3.pitch");
			hologramInfoLocation = new Location(getServer().getWorlds().get(0), x, y, z, yaw, pitch);
			if (seasonInfoHologram == null) {
				seasonInfoHologram = HologramAPI.createHologram(hologramInfoLocation,
						"§b§m---§8]§f §e§lSEASON 3.0 §6§lWEAVEN§f§lMC §4§lNEWS §8[§b§m---§f");
				seasonInfoHologram.spawn();
				seasonInfoHologramLines = new Hologram[15];
				seasonInfoHologramLines[0] = seasonInfoHologram.addLineBelow("");
				seasonInfoHologramLines[1] = seasonInfoHologramLines[0]
						.addLineBelow("§fAgora os §c§lCLANS§f podem possuir §4§lRANKEAMENTO");
				seasonInfoHologramLines[2] = seasonInfoHologramLines[1]
						.addLineBelow("§fNovas §6§lLIGAS§f foram adicionadas");
				seasonInfoHologramLines[3] = seasonInfoHologramLines[2]
						.addLineBelow("§fNovo sistema que calcula o §9§lXP§f ganho em cada §6§lMODO DE JOGO");
				seasonInfoHologramLines[4] = seasonInfoHologramLines[3]
						.addLineBelow("§fAgora é possível possuir mais de 1 §6§lGRUPO§f por §e§lCONTA");
				seasonInfoHologramLines[5] = seasonInfoHologramLines[4]
						.addLineBelow("§fNova §a§lFACILIDADE§f de informações no §2§l/account");
				seasonInfoHologramLines[6] = seasonInfoHologramLines[5]
						.addLineBelow("§fNovo minigame §5§lVoid Challenge§f nos servidores de §6§lPvP");
				seasonInfoHologramLines[7] = seasonInfoHologramLines[6]
						.addLineBelow("§fNovo sistema §a§lCosmeticos§f no §5§lLOBBY");
				seasonInfoHologramLines[8] = seasonInfoHologramLines[7]
						.addLineBelow("§fNovo §1§lTorneio§f de §1§l1v1 automatico§f no §9§lFullIron");
				seasonInfoHologramLines[9] = seasonInfoHologramLines[8]
						.addLineBelow("§fNovos §b§lKITS§f nos servidores de §3§lPvP");
				seasonInfoHologramLines[10] = seasonInfoHologramLines[9]
						.addLineBelow("§fNovo comando §c§l/gravar§f para §b§lYOUTUBERS");
				seasonInfoHologramLines[11] = seasonInfoHologramLines[10]
						.addLineBelow("§fO §3§lKNOCKBACK§f e §3§lHIT DETECTION§f agora mais §b§lSUAVIZADOS");
				seasonInfoHologramLines[12] = seasonInfoHologramLines[11].addLineBelow(
						"§fNovo comando §6§l/perfil§f para setar §b§lRedes Sociais§f em sua conta vista do §e§l/account");
				seasonInfoHologramLines[13] = seasonInfoHologramLines[12].addLineBelow("");
				seasonInfoHologramLines[14] = seasonInfoHologramLines[13]
						.addLineBelow("§fMais informações em: §e§lwww.weavenmc.com.br");
			} else {
				seasonInfoHologram.move(hologramInfoLocation);
				Location last = seasonInfoHologram.getLocation();
				for (int i = 0; i < 15; i++)
					seasonInfoHologramLines[i].move(last = last.subtract(0.0D, 0.25D, 0.0D));
				last = null;
			}
			if (!wasUpdatedLastTry) {
				wasUpdatedLastTry = true;
			}
		} catch (Exception e) {
			wasUpdatedLastTry = false;
			getLogger().info("Nao foi possivel carregar o HologramInfo da Season 3");
		}
	}

	private void registerManagements() {
		gamerManager = new GamerManager(this);
		scoreboardManager = new ScoreboardManager(this);
		menuManager = new MenuManager(this);
		cosmeticManager = new CosmeticManager(this);
		storeManager = new StoreManager(this);
		npcManager = new NpcManager();
	}

	private void enableManagements() {
		gamerManager.enable();
		scoreboardManager.enable();
		menuManager.enable();
		cosmeticManager.enable();
		storeManager.enable();
	}

	private void disableManagements() {
		gamerManager.disable();
		scoreboardManager.disable();
		menuManager.disable();
		cosmeticManager.disable();
		storeManager.disable();
	}
}
