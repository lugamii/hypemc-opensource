package br.com.adlerlopes.bypiramid.hungergames.player.gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.handler.item.CacheItems;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.MessagesConstructor;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.player.PingAPI;
import br.com.weavenmc.commons.bukkit.api.tablist.TabListAPI;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;

public class GamerManager extends Management {

	private static final HashMap<UUID, Gamer> gamers = new HashMap<>();
	private static final List<Gamer> afkGamers = new ArrayList<>();
	private boolean endGame = false;

	private String winner = "not-found";

	public GamerManager(Manager manager) {
		super(manager);
	}

	public String getWinner() {
		return winner;
	}

	public boolean initialize() {
		return true;
	}

	public void addGamer(Gamer gamer) {
		gamers.put(gamer.getUUID(), gamer);
	}

	public boolean isEndgame() {
		return this.endGame;
	}

	public Gamer getGamer(UUID uuid) {
		return (Gamer) gamers.get(uuid);
	}

	public Gamer getGamer(Player player) {
		return (Gamer) gamers.get(player.getUniqueId());
	}

	public HashMap<UUID, Gamer> getGamers() {
		return gamers;
	}

	public List<Gamer> getAFKGamers() {
		return afkGamers;
	}

	public List<Gamer> getAliveGamers() {
		List<Gamer> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (getGamer(player).isAlive().booleanValue()) {
				gamers.add(getGamer(player));
			}
		}
		return gamers;
	}

	public Collection<? extends Player> getAlivePlayers() {
		List<Player> gamers = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (getGamer(player).isAlive().booleanValue()) {
				gamers.add(player);
			}
		}
		return gamers;
	}

	public void givePreGameItems(Player player) {
		player.getInventory().clear();
		if (WeavenMC.getServerType() != ServerType.TOURNAMENT) {
			CacheItems.JOIN_SECKIT.build(player);
		} else {
			CacheItems.JOIN_ONEKIT.build(player);
		}
		player.updateInventory();
	}

	public void hideSpecs(Player player) {
		Gamer gamer = getManager().getGamerManager().getGamer(player);
		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if ((gamer.isSpecs()) && (toHide.isSpectating().booleanValue())) {
				player.hidePlayer(playerToHide);
			}
		}
		for (Player players : Bukkit.getOnlinePlayers()) {
			if ((!gamer.isSpecs())
					&& (getManager().getGamerManager().getGamer(players).isSpectating().booleanValue())) {
				player.hidePlayer(players);
			}
		}
		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if ((toHide.isSpecs()) && (toHide.isSpectating().booleanValue())) {
				player.hidePlayer(playerToHide);
			}
		}
		VanishAPI.getInstance().updateVanishToPlayer(player);
	}

	public void setSpectator(final Gamer gamer) {
		resetKits(gamer);
		gamer.setMode(GamerMode.SPECTING);
		gamer.setItemsGive(Boolean.valueOf(true));
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.hidePlayer(gamer.getPlayer());
		}

		final Player player = gamer.getPlayer();
		final BukkitPlayer bP = BukkitPlayer.getPlayer(gamer.getUUID());

		player.setAllowFlight(true);
		player.setFlying(true);
		for (Player playerToHide : Bukkit.getOnlinePlayers()) {
			Gamer toHide = getManager().getGamerManager().getGamer(playerToHide);
			if ((gamer.isSpecs()) && (toHide.isSpectating().booleanValue())) {
				player.hidePlayer(playerToHide);
			}
		}
		hideSpecs(player);

		new BukkitRunnable() {
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);
				if (!AdminMode.getInstance().isAdmin(player) && bP.getGroup().getId() >= Group.INVESTIDOR.getId())
					AdminMode.getInstance().setAdmin(player);
				player.closeInventory();
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.getInventory().setArmorContents(new ItemStack[4]);
				player.setAllowFlight(true);
				player.setFlying(true);
				CacheItems.SPEC.build(player);
				for (Player players : Bukkit.getOnlinePlayers())
					players.hidePlayer(gamer.getPlayer());
				GamerManager.this.hideSpecs(player);
			}
		}.runTaskLater(getManager().getPlugin(), 2L);
	}

	public void setDied(Gamer gamer) {
		resetKits(gamer);
		gamer.setMode(GamerMode.DEAD);
		gamer.setItemsGive(Boolean.valueOf(true));
	}

	public void respawnPlayer(Gamer gamer) {
		gamer.getPlayer().setHealth(20.0D);
	}

	public void setRespawn(final Gamer gamer) {
		gamer.setMode(GamerMode.ALIVE);
		new BukkitRunnable() {
			public void run() {
				gamer.getPlayer().setHealth(20.0D);
				gamer.getPlayer().setFoodLevel(20);
				gamer.getPlayer().getActivePotionEffects().clear();
				gamer.getPlayer().setFireTicks(0);
				gamer.getPlayer().setFoodLevel(20);
				gamer.getPlayer().setFlying(false);
				gamer.getPlayer().setAllowFlight(false);
				gamer.getPlayer().setSaturation(3.2F);
				gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
				gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.COMPASS) });

				gamer.getKit().give(gamer.getPlayer());
				gamer.getKit2().give(gamer.getPlayer());

				gamer.setItemsGive(Boolean.valueOf(true));
				GamerManager.this.teleportRandom(gamer.getPlayer());
			}
		}.runTaskLater(getManager().getPlugin(), 5L);
	}

	public void resetKits(Gamer gamer) {
		gamer.setKit(getManager().getKitManager().getKit("Nenhum"));
		gamer.setKit2(getManager().getKitManager().getKit("Nenhum"));
	}

	public void updateTab(Player player) {
		if (player == null) {
			return;
		}
		TabListAPI.setHeaderAndFooter(player,
				"§f\n§6§LHYPE\n  §eHG " + WeavenMC.getServerId().replace("hg-a", "") + " §8: §bmc-hypemc.com.br\n§f",
				"§f\n§fWebsite §awww.hypemc.com.br\n§fLoja §aloja.hypemc.com.br\n§fDiscord §adiscord.hypemc.com.br\n§f");
	}

	public void teleportRandom(Player player) {
		World world = player.getWorld();
		int x = getManager().getRandom().nextInt(200) + 100;
		int z = getManager().getRandom().nextInt(200) + 100;
		if (getManager().getRandom().nextBoolean()) {
			x = -x;
		}
		if (getManager().getRandom().nextBoolean()) {
			z = -z;
		}
		player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 7, z));
	}

	public void teleportSpawn(Player player) {
		player.teleport(HungerGames.getSpawn());
	}

//	public void giveDamage(LivingEntity reciveDamage, Player giveDamage, double damage, boolean bool) {
//		if ((reciveDamage == null) || (reciveDamage.isDead()) || (giveDamage == null) || (giveDamage.isDead())) {
//			return;
//		}
//		if (bool) {
//			if (((Damageable) reciveDamage).getHealth() < damage) {
//				reciveDamage.setHealth(1.0D);
//				giveDamage.setMetadata("custom",
//						new FixedMetadataValue(HungerGames.getPlugin(HungerGames.class), null));
//				reciveDamage.damage(6.0D, giveDamage);
//			} else {
//				reciveDamage.damage(damage);
//			}
//		} else {
//			giveDamage.setMetadata("custom", new FixedMetadataValue(HungerGames.getPlugin(HungerGames.class), null));
//			reciveDamage.damage(damage, giveDamage);
//		}
//	}

	public void checkWinner1() {
		if (getManager().getGameManager().isEnded().booleanValue()) {
			return;
		}
		if (getAliveGamers().size() == 1) {
			getManager().getGameManager().setEnded(Boolean.valueOf(true));
			Gamer gamer = (Gamer) getAliveGamers().get(0);
			if (gamer.getPlayer().isOnline()) {
				makeWin(gamer.getPlayer(), gamer);
			}
			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 35;

				public void run() {
					if (this.cancel == 0) {
						for (Player players : Bukkit.getOnlinePlayers()) {
							sendPlayerToLobby(players);
						}
					}
					this.cancel -= 1;
				}
			}, 0L, 20L);

			Bukkit.getScheduler().runTaskTimer(getManager().getPlugin(), new Runnable() {
				int cancel = 45;

				public void run() {
					if (this.cancel == 0) {
						Bukkit.shutdown();
					}
					this.cancel -= 1;
				}
			}, 0L, 20L);
		} else if (getAliveGamers().size() == 0) {
			Bukkit.shutdown();
		}
	}

	public void sendPlayerToLobby(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF("lobby");
		player.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

	public void checkWinner() {
		checkWinner1();
	}

	public void makeWin(Player player, Gamer gamer) {
		winner = player.getName();

		player.setGameMode(GameMode.CREATIVE);
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());

		int hgWins = bP.getData(DataType.HG_WINS).asInt();
		bP.getData(DataType.HG_WINS).setValue(hgWins += 1);

		bP.getData(DataType.HG_HAS_PENDENT_WINNER).setValue(true);

		int xp = bP.isDoubleXPActived() ? 120 * 2 : 120;

		bP.addXp(xp);
		bP.addMoney(2000);

		int silver = bP.getData(DataType.SILVER_CRATES).asInt();
		bP.getData(DataType.SILVER_CRATES).setValue(silver += 1);

		boolean fiveKs = false;
		if (hgWins > 0) {
			if (String.valueOf(hgWins).endsWith("0") || String.valueOf(hgWins).endsWith("5")) {
				fiveKs = true;
				int gold = bP.getData(DataType.GOLD_CRATES).asInt();
				bP.getData(DataType.GOLD_CRATES).setValue(gold += 1);
			}
		}

		boolean tenKs = false;
		if (hgWins >= 10) {
			if (String.valueOf(hgWins).endsWith("0") || String.valueOf(hgWins).endsWith("5")) {
				tenKs = true;
				int diamond = bP.getData(DataType.DIAMOND_CRATES).asInt();
				bP.getData(DataType.DIAMOND_CRATES).setValue(diamond += 1);
			}
		}

		TitleAPI.setTitle(player, "§6§lHype§f§lHG", "§7Você venceu o torneio!");
		player.sendMessage("§6§lHype§f§lHG§7 Você venceu o torneio!");

		player.sendMessage("§9§lXP§f Você recebeu §9§l" + xp + " XPs");
		player.sendMessage("§6§lMONEY§f Você recebeu §6§l2000 MOEDAS");

		player.sendMessage("§7Você recebeu uma caixa de prata");
		if (fiveKs)
			player.sendMessage("§6Você consegui mais 5 killstreak de wins e recebeu 1 caixa de ouro");
		if (tenKs)
			player.sendMessage("§bVocê consegui mais 10 killstreak de wins e recebeu 1 caixa de diamante");

		bP.save(DataCategory.HUNGERGAMES, DataCategory.BALANCE, DataCategory.CRATES);

		getManager().getGameManager().setGameStage(GameStage.WINNING);

		Location cake = player.getLocation().clone();
		cake.setY(156.0D);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				cake.clone().add(x, 0.0D, z).getBlock().setType(Material.GLASS);
				cake.clone().add(x, 1.0D, z).getBlock().setType(Material.CAKE_BLOCK);
			}
		}
		player.setGameMode(GameMode.CREATIVE);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		player.teleport(cake.clone().add(0.0D, 4.0D, 0.0D));
		player.getInventory().setArmorContents(null);

		player.getInventory().setItem(0, new ItemBuilder(Material.WATER_BUCKET).setName("§bMlg").getStack());
		player.updateInventory();

		this.endGame = true;

		startFirework(player, player.getLocation(), getManager().getRandom());

		String tempo = getManager().getUtils().toTime(getManager().getGameManager().getGameTime().intValue());

		Bukkit.broadcastMessage(
				"§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");
		Bukkit.broadcastMessage("§6" + player.getName() + " §eganhou"
				+ (getManager().getGameManager().getTimer().isEvent() ? " o evento" : "") + "!");
		Bukkit.broadcastMessage("§eMatou §2" + player.getStatistic(Statistic.PLAYER_KILLS) + "§e players com o kit §6"
				+ gamer.getKit().getName() + "§e em §6" + tempo);
		Bukkit.broadcastMessage(
				"§7§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-§7=§8-=");

		Bukkit.getWorld("world").setTime(13000L);

		MessagesConstructor.sendTitleMessage(gamer.getPlayer(), "§6Parabêns!", "§7Você foi o ultimo sobrevivente!");
	}

	public void startFirework(final Player player, Location location, Random random) {
		for (int i = 0; i < 5; i++) {
			spawnRandomFirework(location.add(-10 + random.nextInt(20), 0.0D, -10 + random.nextInt(20)));
		}
		new BukkitRunnable() {
			public void run() {
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, -10.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-10.0D, 0.0D, 10.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, -10.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(10.0D, 0.0D, 10.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, -5.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-5.0D, 0.0D, 5.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, -5.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(5.0D, 0.0D, 5.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-4.0D, 0.0D, -3.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(-3.0D, 0.0D, 4.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(2.0D, 0.0D, -6.0D));
				GamerManager.this.spawnRandomFirework(player.getLocation().add(1.0D, 0.0D, 9.0D));
			}
		}.runTaskTimer(getManager().getPlugin(), 10L, 30L);
	}

	public void spawnRandomFirework(Location location) {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = firework.getFireworkMeta();

		int rt = getManager().getRandom().nextInt(4) + 1;

		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) {
			type = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
			type = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
			type = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
			type = FireworkEffect.Type.STAR;
		}
		FireworkEffect effect = FireworkEffect.builder().flicker(getManager().getRandom().nextBoolean())
				.withColor(Color.WHITE).withColor(Color.ORANGE).withFade(Color.FUCHSIA).with(type)
				.trail(getManager().getRandom().nextBoolean()).build();
		fwm.addEffect(effect);
		fwm.setPower(getManager().getRandom().nextInt(2) + 1);

		firework.setFireworkMeta(fwm);
	}
}
