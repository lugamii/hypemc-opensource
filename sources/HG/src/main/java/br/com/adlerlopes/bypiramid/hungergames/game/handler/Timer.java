package br.com.adlerlopes.bypiramid.hungergames.game.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.types.BonusFeast;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.types.Feast;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.types.FinalArena;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.types.MiniFeast;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;
import br.com.adlerlopes.bypiramid.hungergames.manager.managers.FileManager;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.api.actionbar.BarAPI;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.event.redis.ServerStatusUpdateEvent;
import br.com.weavenmc.commons.core.server.HardcoreGames;
import br.com.weavenmc.commons.core.server.HardcoreGames.GameState;
import br.com.weavenmc.commons.core.server.NetworkServer;

public class Timer extends Management implements Listener {

	protected Boolean star;
	private Feast feast;
	private BonusFeast bonusFeast;
	private FinalArena finalArena;
	private boolean start;
	private boolean event;
	private boolean prizeEvent;
	public boolean startg;

	public Timer(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		this.finalArena = new FinalArena(getManager(), new Location(FileManager.getWorld(), 0.0D, 10.0D, 0.0D));
		this.feast = null;
		this.bonusFeast = null;
		this.start = false;
		this.event = false;
		this.prizeEvent = false;

		borderSpawn(500);

		FileManager.getWorld().setSpawnLocation(0, FileManager.getWorld().getHighestBlockYAt(0, 0) + 15, 0);
		return true;
	}

	public boolean isEvent() {
		return this.event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public boolean isPrizeEvent() {
		return this.prizeEvent;
	}

	public void setPrizeEvent(boolean prizeEvent) {
		this.prizeEvent = prizeEvent;
	}

	public boolean borderSpawn(int borderSize) {
		for (int y = 0; y <= 200; y++) {
			for (int x = -borderSize; x <= borderSize; x++) {
				Location loc = new Location(Bukkit.getWorld("world"), x, y, borderSize);
				if (!loc.getChunk().isLoaded()) {
					loc.getChunk().load(true);
				}
				getManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int x = -borderSize; x <= borderSize; x++) {
				Location loc = new Location(Bukkit.getWorld("world"), x, y, -borderSize);
				if (!loc.getChunk().isLoaded()) {
					loc.getChunk().load(true);
				}
				getManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int z = -borderSize; z <= borderSize; z++) {
				Location loc = new Location(Bukkit.getWorld("world"), borderSize, y, z);
				if (!loc.getChunk().isLoaded()) {
					loc.getChunk().load(true);
				}
				getManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
			for (int z = -borderSize; z <= borderSize; z++) {
				Location loc = new Location(Bukkit.getWorld("world"), -borderSize, y, z);
				if (!loc.getChunk().isLoaded()) {
					loc.getChunk().load(true);
				}
				getManager().getBO2().setBlockFast(loc, getBorderMaterial(), (byte) 0);
			}
		}
		return true;
	}

	public Material getBorderMaterial() {
		if (getManager().getRandom().nextBoolean()) {
			return Material.GLASS;
		}
		if (getManager().getRandom().nextBoolean()) {
			return Material.QUARTZ_BLOCK;
		}
		return Material.GLASS;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerUpdate(ServerStatusUpdateEvent event) {
		if (!WeavenMC.getClanCommon().isClansLoaded() || !WeavenMC.getProfileCommon().isProfilesLoaded()) {
			event.setCancelled(true);
			return;
		}

		NetworkServer server = event.getWeavenServer();

		int time = getManager().getGameManager().getGameTime();

		GameState state = null;
		if (getManager().getGameManager().isPreGame()) {
			if (!this.start)
				state = GameState.WAITING;
			else
				state = GameState.PREGAME;
		} else if (getManager().getGameManager().isInvencibility()) {
			state = GameState.INVINCIBILITY;
		} else if (getManager().getGameManager().isGame()) {
			state = GameState.GAMETIME;
		} else if (getManager().getGameManager().isEnded()) {
			state = GameState.ENDING;
		} else {
			state = GameState.NONE;
		}

		int leftPlayers = 0;
		if (state == GameState.WAITING) {
			leftPlayers = 5 - getManager().getGamerManager().getAliveGamers().size();
		}

		String winner = getManager().getGamerManager().getWinner();

		event.setWeavenServer(new HardcoreGames(server.getServerId(), server.getPort(), server.getServerType(),
				server.getMaxPlayers(), server.getPlayers(), server.isJoinEnabled(), true,
				(8 * 1000L) + System.currentTimeMillis(), time, leftPlayers, winner, state));
		event.setMessageChannel(WeavenMC.HG_SERVER_INFO_CHANNEL);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onTimer(ServerTimeEvent event) {
		if (getManager().getGameManager().isPreGame()) {

			((World) Bukkit.getWorlds().get(0)).setTime(0L);

			if (this.start) {
				if (getManager().getGamerManager().getAliveGamers().size() < 5) {
					this.start = false;
					getManager().getGameManager().setGameTime(Integer.valueOf(300));
					return;
				}
			}

			if (!this.start) {
				if (getManager().getGamerManager().getAliveGamers().size() >= 5) {
					this.start = true;
				}
			}
			if (!this.start) {
				return;
			}

			if (getTime().intValue() <= 0) {
				if (Bukkit.getOnlinePlayers().size() < 5) {
					getManager().getGameManager().setGameTime(Integer.valueOf(300));
					return;
				}
				for (Player data : Bukkit.getOnlinePlayers()) {
					Gamer gamer = getManager().getGamerManager().getGamer(data);
					gamer.setBlockFunction(true);
					data.closeInventory();

				}
				startGame();
			}

			getManager().getGameManager().setGameTime(Integer.valueOf(getTime().intValue() - 1));
			if ((getTime().intValue() > 40) && (Bukkit.getOnlinePlayers().size() >= 80) && (!this.startg)) {
				this.startg = true;

				getManager().getGameManager().setGameTime(Integer.valueOf(30));
				Bukkit.broadcastMessage("§3§lTORNEIO §fO tempo foi reduzido pois a partida está lotada!");
			}
			if (60 <= getBorderTime().intValue()) {
				getManager().getGameManager().setBorderTime(getBorderTime().intValue() - 5);
			}

			if (((getTime().intValue() % 30 == 0) || (getTime().intValue() % 60 == 0))
					&& (getTime().intValue() >= 30)) {
				Bukkit.broadcastMessage("§3§lTORNEIO §fO torneio iniciará em §c§l"
						+ getManager().getUtils().formatTime(getTime().intValue()));
				playSound(Sound.CLICK);
			}
			if ((getTime().intValue() % 5 == 0) && (getTime().intValue() >= 10) && (getTime().intValue() <= 20)) {
				Bukkit.broadcastMessage("§3§lTORNEIO §fO torneio iniciará em §c§l"
						+ getManager().getUtils().formatTime(getTime().intValue()));
				playSound(Sound.CLICK);
			}
			if ((getTime().intValue() % 1 == 0) && (getTime().intValue() <= 5) && (getTime().intValue() != 0)) {
				Bukkit.broadcastMessage("§3§lTORNEIO §fO torneio iniciará em §c§l"
						+ getManager().getUtils().formatTime(getTime().intValue()));
				playSound(Sound.NOTE_PLING);
			}

		} else if (getManager().getGameManager().isInvencibility()) {
			if (getTime().intValue() < 300)
				HungerGames.removeColiseu(getTime());
			if (((getTime().intValue() % 30 == 0) || (getTime().intValue() % 60 == 0)) && (getTime().intValue() >= 30)
					&& (getTime().intValue() != getManager().getConfig().getInt("game.invencibility"))) {
				Bukkit.broadcastMessage(
						"§e§lINVENCIBILIDADE§f acabará em " + getManager().getUtils().formatTime(getTime().intValue()));
			}
			if ((getTime().intValue() % 1 == 0) && (getTime().intValue() <= 5) && (getTime().intValue() != 0)) {
				playSound(Sound.CLICK);
				Bukkit.broadcastMessage(
						"§e§lINVENCIBILIDADE§f acabará em " + getManager().getUtils().formatTime(getTime().intValue()));
			}
			getManager().getGameManager().setGameTime(Integer.valueOf(getTime().intValue() - 1));

			for (Player player : Bukkit.getOnlinePlayers()) {
				BarAPI.send(player, "§e§lINVENCIBILIDADE §c§l"
						+ getManager().getUtils().formatOldTime(getManager().getGameManager().getGameTime()));
			}
			if (getTime().intValue() <= 0) {
				Bukkit.broadcastMessage("§e§lINVENCIBILIDADE§c§l ACABOU");
				getManager().getGameManager().setGameStage(GameStage.GAME);
				Bukkit.getOnlinePlayers().forEach(players -> {
					players.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
					this.getManager().getScoreboardManager().create(players);
				});

				getManager().getGameManager().setGameTime(Integer.valueOf(120));
			}
		} else if (getManager().getGameManager().isGame()) {
			getManager().getGameManager().setGameTime(Integer.valueOf(getTime().intValue() + 1));
			getManager().getGamerManager().checkWinner();

			HungerGames.removeColiseu(getTime());

			if (getTime().intValue() % 300 == 0) {
				new MiniFeast(getManager(), 10);
			}
			if (getTime().intValue() == 600) {
				this.feast = new Feast(getManager(), 15);
				this.feast.spawnFeast();
			}
			if (getTime().intValue() == 1320) {
				this.bonusFeast = new BonusFeast(getManager(), 15);
				this.bonusFeast.spawnFeast();
			}

			if (getTime().intValue() == 2100) {
				Location loc = Manager.spawnarArenaFinal();
				Bukkit.getOnlinePlayers().forEach(players -> {
					players.teleport(loc);
				});
			}

		} else {
			getManager().getGameManager().setGameTime(Integer.valueOf(getTime().intValue() + 1));
		}
	}

	public void startGame() {
		HungerGames.openColiseu();

		getManager().getGameManager().setGameStage(GameStage.INVENCIBILITY);
		getManager().getGameManager()
				.setGameTime(Integer.valueOf(getManager().getConfig().getInt("game.invencibility")));

		playSound(Sound.ENDERDRAGON_GROWL);

		for (Player player : Bukkit.getOnlinePlayers()) {

			startGamer(player);

			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			HungerGames.getManager().getScoreboardManager().create(player);
			if (!AdminMode.getInstance().isAdmin(player))
				continue;
			getManager().getGamerManager().setDied(HungerGames.getManager().getGamerManager().getGamer(player));
			getManager().getGamerManager().checkWinner();

		}

		Bukkit.broadcastMessage("§cO torneio iniciou!");
		Bukkit.broadcastMessage("§cTodos estão invencíveis por "
				+ getManager().getUtils().formatTime(getManager().getConfig().getInt("game.invencibility")));
		Bukkit.broadcastMessage("§bQue a sorte esteja ao seu favor");

	}

	public void startGamer(Player player) {
		// TODO Auto-generated method stub

		player.getInventory().clear();
		player.getActivePotionEffects().clear();
		player.getInventory().setArmorContents(null);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setSaturation(3.2F);
		// player.addPotionEffect(new
		// PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
		player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COMPASS) });

		player.updateInventory();

		Gamer gamer = HungerGames.getManager().getGamerManager().getGamer(player);

		gamer.setItemsGive(Boolean.valueOf(true));

		gamer.getKit().give(player);
		gamer.getKit2().give(player);

	}

	public Feast getFeast() {
		return this.feast;
	}

	public FinalArena getFinalArena() {
		return this.finalArena;
	}

	public int getBorderSize() {
		return 500;
	}

	public Integer getTime() {
		return getManager().getGameManager().getGameTime();
	}

	public Integer getBorderTime() {
		return Integer.valueOf(getManager().getGameManager().getBorderTime());
	}

	public void playSound(Sound sound) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), sound, 5.0F, 5.0F);
		}
	}
}
