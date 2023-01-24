package br.com.weavenmc.lobby.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.hologram.Hologram;
import org.inventivetalent.hologram.HologramAPI;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.managers.MenuManager.MenuType;
import br.com.weavenmc.lobby.npcs.NPC;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ItemStack;

@Getter
public class NpcManager {
	@Getter
	static NPC hgNpc;
	@Getter
	static NPC pvpNpc;
	@Getter
	static NPC gladiatorNpc;
	@Getter
	static NPC eventoNpc;
	@Getter
	static NPC skywarsNpc;

	private static Location getLocation(NPCS npc) {
		double x = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.x");
		double y = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.y");
		double z = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.z");

		return new Location(Bukkit.getWorld("world"), x, y, z);
	}

	static int onlineCount;

	static {

		//

//		if (BukkitMain.getInstance() == null) {
//			System.out.println("bukkit  main nulo");
//		}

//
//		if (BukkitMain.getInstance().getNetworkManager() == null) {
//			System.out.println("network manager nulo");
//		}

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.DOUBLEKITHG)
				+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.HG_LOBBY);

		Location locHgTwo = getLocation(NPCS.HG);
		locHgTwo.setY(locHgTwo.getY() + 2.4);

		Hologram holoHg = HologramAPI.createHologram(locHgTwo, "§e" + onlineCount + " jogadores conectados.");
		holoHg.spawn();

		//

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.PVP_FULLIRON);

		Location locPvPTwo = getLocation(NPCS.PVP);
		locPvPTwo.setY(locPvPTwo.getY() + 2.4);

		Hologram holoPvp = HologramAPI.createHologram(locPvPTwo, "§e" + onlineCount + " jogadores conectados.");

		holoPvp.spawn();
		//

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.GLADIATOR);

		Location locGladiatorNpc = getLocation(NPCS.GLADIATOR);
		locGladiatorNpc.setY(locGladiatorNpc.getY() + 2.4);

		Hologram holoGlad = HologramAPI.createHologram(locGladiatorNpc, "§e" + onlineCount + " jogadores conectados.");
		holoGlad.spawn();
		//

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.TOURNAMENT);

		Location locEventoNpc = getLocation(NPCS.EVENTO);
		locEventoNpc.setY(locEventoNpc.getY() + 2.4);

		Hologram holoEvento = HologramAPI.createHologram(locEventoNpc, "§e" + onlineCount + " jogadores conectados.");

		holoEvento.spawn();

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS)
				+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS_LOBBY);

		Location locSwNpc = getLocation(NPCS.SKYWARS);
		locSwNpc.setY(locSwNpc.getY() + 2.4);

		Hologram holoSw = HologramAPI.createHologram(locSwNpc, "§e" + onlineCount + " jogadores conectados.");
		holoSw.spawn();

		new BukkitRunnable() {

			@Override
			public void run() {

				//

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.DOUBLEKITHG);
				holoHg.setText("§e" + onlineCount + " jogadores conectados.");

				//

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.PVP_FULLIRON);
				holoPvp.setText("§e" + onlineCount + " jogadores conectados.");

				//

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.GLADIATOR);
				holoGlad.setText("§e" + onlineCount + " jogadores conectados.");

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);
				holoSw.setText("§e" + onlineCount + " jogadores conectados.");

				//
//
				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.TOURNAMENT);
				holoEvento.setText("§e" + onlineCount + " jogadores conectados.");

				//

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS_LOBBY)
						+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);
				holoSw.setText("§e" + onlineCount + " jogadores conectados.");

				//

				holoHg.update();
				holoPvp.update();
				holoEvento.update();
				holoGlad.update();
				holoSw.update();

				hgNpc.addToTablist();
				pvpNpc.addToTablist();
				gladiatorNpc.addToTablist();
				eventoNpc.addToTablist();
				skywarsNpc.addToTablist();
				
				hgNpc.rmvFromTablist();
				pvpNpc.rmvFromTablist();
				gladiatorNpc.rmvFromTablist();
				eventoNpc.rmvFromTablist();
				skywarsNpc.rmvFromTablist();

			}
		}.runTaskTimerAsynchronously(Lobby.getPlugin(), 0, 30);

	}

	public NpcManager() {

		// constructor

		hgNpc = new NPC("§7Clique aqui", getLocation(NPCS.HG));
		pvpNpc = new NPC("§7Clique aqui", getLocation(NPCS.PVP));
		gladiatorNpc = new NPC("§7Clique aqui", getLocation(NPCS.GLADIATOR));
		eventoNpc = new NPC("§7Clique aqui", getLocation(NPCS.EVENTO));
		skywarsNpc = new NPC("§7Clique aqui", getLocation(NPCS.SKYWARS));

		// define texturas

		hgNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5MjE3NTQwNTc3OSwKICAicHJvZmlsZUlkIiA6ICIwYTQ3NDJjOWQ3YWY0NmM4OTllODNlNTdiYWE0MTNmYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDZWxsYml0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM4ZDEzMWQ5YmQ2ZjJhNTIwY2EyMTJjYWY1MDU0MGJhZGE5MWUyNzA0YmM1YmFhNmIzYzRkNjM3OTE4MWM1NjQiCiAgICB9CiAgfQp9",
				"vvTZ1CDju3ERu1Ghzelh9Lolpr9sX135jzFQM1oXTXMHCjL5+FL8tO2Rk6GVjudiYsCF8FhjMIQI8zuerAj1SpP7XPODAUZOzFmn8LUS+NBMQPqTp9E64YyBcMTPMmWbZCd+B8WKv6bVf+1W0za6vhDlb014YCYP13o/w+Ggusb89iWHXMZAT7LqisdZC948tCVuk7fjDt5xKNR7MJsJPBEDQvO1pOPLj+lDlbrswa5ovcz2RRccHfVazV2H9C84phCvI3O63ROZDd7Hp5PAywspXKCfOzRvpRe9+vICzNvQ8RuG+NEBfw6FyWPYSUWKZYitzOz3GITehsLPt/LNr67ySAsuLvkH144Sc5IFmamGN5jkGXzsq4VUlxMkl2kG5Dycekp2qplfbv8gzbjR3biLWbakeIKuj8BdIMOWdZE1VDS4704VpQ4qGcgYVMZx5l6sIVzVsprLCtb5sDC9iI0dza/IM5pt68YJq6w0gg/utX58iOwWlSOSFTMlIaWv2o5txRwY2ppFmwi7obAF7Onlk00PSujZFSe57jCpEhOYbf1yvfZztu8GvUeCrztMj65zSTsDUVjDwe0yWO37aPyQoYBiQv/B+6RBaUjNpyfzUY+7qlGvrCsBG9s/Rcz62ZtpQq+th3crGq5LQaLFPcN51QvEoHcl5kEFD1OcwkY=");
		pvpNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5MjE3NTQ0OTkwOCwKICAicHJvZmlsZUlkIiA6ICI4YjY3M2VkZmFjODU0OGVmYjNhMDkyNmM2OWViMGU0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOeWh0cmV4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVjNDk1ZWI3ZTVjZThkN2Q5ZDBiMzAzYTEwZDM3YTcyOGU0ZDNkMWM0MWRmYTEzZWQ4MmNjMjkwNjg2YTEzNTAiCiAgICB9CiAgfQp9",
				"DUFkVsdpAGhRE/Ebi9oaX+CcYc8Y3ZHXsAy6C6uH5MQwmAFFe6xPvRst3cXR9/TvcGlWh5Ll29q0bCeOgwr8ika/3PSKpH/fQv5dNd2W70E19oolQjbjuJKAclLAJfh2iVeO7cJLL9RpxrTi5wzHOX6Lqof2068uzgZhrk1AxLIeOQlG9+ocZPm9A7c9+KaMqQtMxy4FWCyGt+OeS00Pr4fqvTufbJoU4A4lfj7Z8X0qELs61i+jLMk6bknPpcEbQ3neu0MueNYE3JY1xzKqDTcOHMXIfv9ZzOaajwzy+ChD8VEmRjZTlcSCZnSsoWJfRRx67pPK4oNbu6iTBxxagtXxnp/AvGlHZcj+QjWV6CsMoa4eCpZu/ZGmt/rHmrFArR6jWDvehZsC6p3Y4dMCUdGXJyy/DN5W1/UQ9ubmkD5awD727MIxjjQ5+NYTcnfd9EaoNc5T/x6tww4abLFTjxEegz/e5D93ueaATSbS5fSDQeZGSCq4F/WM6hH2b60OOgxRxofdoMXAWPKopHdW7dmLi4naOXYzeOUEcqDw8Vs8CrVnQDV8fbnStw+XuSllyiwAch3Ykj2b7Q8e8GLR11MEVplhALBdfsrhHCrytnf+C2/AMH/W0VGfbBI4YyaJBME2B7juYTBTo6T1eaKGoPJMbwxAQmpB+PPe0wuKlNQ=");
		gladiatorNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5MjE3NTQ4NTA2OSwKICAicHJvZmlsZUlkIiA6ICI1YWU1Mzc1MzMzZWI0NDU4ODQwMDE3OTEwOTE2NTc1MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJwYW5kYXNhZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hOThlZjE3MzYyY2Y0YWNiYmY1NDcxYjgzZWVmN2ZkMmM4YzU4MWFiOGJlYzkyNjM0MGY2ZGVjNTUzOWY3MTAiCiAgICB9CiAgfQp9",
				"g7gCSGYZ9sDT77lod93BG2+uPyh/B5zGCq+GtpBZ34D8Q58Nu+A5zjhWPiExuqGHroz4y+1UxNBTK5vTwRq2rsC06Py4b3DH7ZvCJE8rzYKF2ItykvE2c5vQzsrVAWM/gKP4An1Qu7bosqvO1lCY12qmMyR/6b94Ytnga+Wp/uS5Yj/IhGkRgFm9NsjWFht2RSum8AnME5sSRhnbaVv14IYVa9aWWD4R1E1/616ai8Nj/3CIXOVOHMEYMIqxI5ljprcctpjfaOrtRMOsKGY7VbyynRnCfTG/nX0tkNHUu3z3OvyMyWbID0MPeeKVwNPREfOj+Q3K3Nu0qzXgiBYSjsF8RbnUMxAyontzIVkXA7uRGztOVCkfIPmrAe0J8vZbnQ2cgtC9NABMGzbZX/iA+8izGCXe7bcMW6nPgbg27ROZNPX/PDuclPCDy2FaWuieKkf3Bl4GE9/Y6MmsGkzdtR55sbYLaX+J/h9UCZvqg1d34qtjkLsDsGrUqsMJv8OiPuemrL7ZY9Tr9180laAD2bfm2h8chAixA75mTsTsblL32YXwvfG+TzlIS9ijwSxLdq+dnCvVXk2TSncE8XKyDLFv5Wnh7QsX0DxRFE+qHfLJ4N6IGWxMMkxnHCpbHK9476tGNitP9rsfM7VaOx9LuYXAyWyg6sEq7rw5IU4m1eg=");
		eventoNpc.changeSkin(
				"eyJ0aW1lc3RhbXAiOjE1ODcxOTI3MTc5NjcsInByb2ZpbGVJZCI6IjkxNDc2ODY4NGYyZjQwNzFhYWE3NzFkMDViZWY5OTJkIiwicHJvZmlsZU5hbWUiOiJzcG9wXyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWE2NmQxNGUzZTViZjE4YzM2ZjlhMjhlOTAyYjdjYzBmNzdlOWZkOWU4ZTc2YTU5MTc5ZTU5MTdiNDQzZjkxMyJ9fX0=",
				"CJJi7OWpezBFlHMjok+Bb7X9K6+6EzcvOQDJIHaHl7OfMmcuBTuEsjYJp9VNMzH7a3dhS9WxD+1jRLlXX3n/PI3Eksq2fY3zbdp3z0zGUrqrKLiFOBUPvbYWkumoWZzvn2bpuSK63M/iXxZW+kLIROy4iSbodG9appFWyvamUBU39p1Z8G+Ze44b/j3OalGJwTBRz4n9ojJoY1Wq5X750H1Ch/GP6ev4Wm2dLITihKCzZpHxTBUp5eusMclJyLdeS76J9OMD7UvcJpRs0N++Cqvdq4lz2pvOb9QjNbWfCwwPj1RwOBy366+NQLWXwiScMqdg2JqB1pMR1svf4/Q4h9IK3eH5cuG9v3EJu2hVderdwNH759rQWsNohtXqOrZP0ELXVNUIjvx6CTzd5A8zhnkYwiBmUfNAuERDu3c3IOzMdDgFK1bBS1IBgJw2J7LuKlKTPsJPLyfdqUpTwqUIz7ehPTMNuS9YNnOxgZ0mroHu9pZQd6hUvXB7ZhOzmZkQ6PCBddQjf09iobYprroi+44KG6vcKKrNRvooJCVonqgrFX+I+t2K75/P8K/xpcskyYc3SKZzCTwgEgRy5iY0OtprXbe79BQy5LEokhIfiWI6ZorjSOQ+Wpt0sv+BUDFy6u1CozTOHRvMSfR71J/2Y4D9/tTXU+IjeYc/7Dg/MPY=");

		skywarsNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5MjE3NTM0NzY1MSwKICAicHJvZmlsZUlkIiA6ICJiZGExNzQ4MzM5NmY0NmUwOTZlNGVkYmQ4NzUwZGQ4NiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwRXRoaXVzT25Ub3AiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ4ZmIxODdkMjQ0MThiZGZjZWJhOTVkOGUyY2Y0OWVkMzcwY2RkNTA5YzZkZDA1NzRmMmMyM2UxZjIwMjk2MCIKICAgIH0KICB9Cn0=",
				"ksaSLatxQNwmg4jM+CdxgrEzMPN7gEreZwk5kRvi8SaRxuiwrNWqD+J+yPu+Osa9PqP2NVirAVbgKWLJvCA+tT2bEauHULrVXfBX77GAzB+extdM9HXSv2Ff6wEVlGbB/l2pRbpPxLyVxnmu5NpajPlEKSPVDmiRVU3sTwxry3WewtyhbmFCjqdj9PevQEjcF0MBQymR4wkt54bvUf2HTjEYCzxaXtJ4WqtO+re6VH1BLBOW/iZiKHsP6Okz3PC3wQtfk3klTaJKQNOn6UfKiUWXg8BvB2z35fJXCVx46JCNPryU4AKGrCRxp0KDNzDds/r+/b3EG6r3xB9fkEJYU7+VUldPz4YLTbeP+syeCKLikerEycukA90q2ursilp9GUdfu4guR/VR+WskiIoE8lbXwzIGgYddYaY5oXlcK/AL7kr+8aNGzM/fKDgBgr5TI237bD/bDwJ/Y+x7VcnrxY8NQdDQ7GSiRSHKrjjUpe4hJQVvT9rDH6og+AQqBbH2yL3YTt78lYn7l9aTf9GPZVvDavARNRFP4YigDSgHvTKS8J2ivdOqGRO0UeUcIHJ/C/8a5PeHFXKqAnmiFXFZBExMwXmdObe0fxjfoHyxiVz8yxeBLt1Z2QlAS25Ik/J3tA0I5Tyr+a9mDvNSE8lF8b35VgAqu5T6575tcpr5bFg=");
		net.minecraft.server.v1_8_R3.Item item = net.minecraft.server.v1_8_R3.Item.getById(381);
		net.minecraft.server.v1_8_R3.ItemStack itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		skywarsNpc.equip(0, itemStack);
		// head rotation
		// 170 -5
		item = net.minecraft.server.v1_8_R3.Item.getById(307);
		itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		pvpNpc.equip(0, itemStack);

		hgNpc.headRotation(-90, 0);
		pvpNpc.headRotation(-90, 0);
		gladiatorNpc.headRotation(-90, 0);
//		eventoNpc.headRotation(-180, 5);
		skywarsNpc.headRotation(-90, 0);

		// holo pvp
		Location locHgOne = getLocation(NPCS.HG);
		locHgOne.setY(locHgOne.getY() + 2.7);

		HologramAPI.createHologram(locHgOne, "§bHardcoreGames").spawn();

		// holo pvp

		Location locPvPOne = getLocation(NPCS.PVP);
		locPvPOne.setY(locPvPOne.getY() + 2.7);

		HologramAPI.createHologram(locPvPOne, "§bKitPvP").spawn();

		// holo glad

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.GLADIATOR);

		Location locGladiatorOne = getLocation(NPCS.GLADIATOR);
		locGladiatorOne.setY(locGladiatorOne.getY() + 2.7);

		HologramAPI.createHologram(locGladiatorOne, "§bGladiator").spawn();

		// holo evento

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.TOURNAMENT);

		Location locEventoOne = getLocation(NPCS.EVENTO);
		locEventoOne.setY(locEventoOne.getY() + 2.7);

		HologramAPI.createHologram(locEventoOne, "§bEvento").spawn();

		// holo sw

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);

		Location locSWOne = getLocation(NPCS.SKYWARS);
		locSWOne.setY(locSWOne.getY() + 2.7);
		Location locSWOne2 = getLocation(NPCS.SKYWARS);
		locSWOne2.setY(locSWOne2.getY() + 3.1);

		HologramAPI.createHologram(locSWOne2, "§d§lNOVO").spawn();

		HologramAPI.createHologram(locSWOne, "§bSkyWars").spawn();

	}

	public void spawnNpcs(Player forPlayer) {
		hgNpc.spawn(forPlayer);
		pvpNpc.spawn(forPlayer);
		gladiatorNpc.spawn(forPlayer);
		eventoNpc.spawn(forPlayer);
		skywarsNpc.spawn(forPlayer);
		net.minecraft.server.v1_8_R3.Item item = net.minecraft.server.v1_8_R3.Item.getById(282);
		net.minecraft.server.v1_8_R3.ItemStack itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		hgNpc.equip(0, itemStack);
		hgNpc.headRotation(-90, 0);
		pvpNpc.headRotation(-90, 0);
		eventoNpc.headRotation(-90, 0);
		gladiatorNpc.headRotation(-90, 0);
		skywarsNpc.headRotation(-90, 0);
		item = net.minecraft.server.v1_8_R3.Item.getById(381);
		itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		skywarsNpc.equip(0, itemStack);
		item = net.minecraft.server.v1_8_R3.Item.getById(267);
		itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		gladiatorNpc.equip(0, itemStack);
		item = net.minecraft.server.v1_8_R3.Item.getById(307);
		itemStack = new net.minecraft.server.v1_8_R3.ItemStack(item);
		pvpNpc.equip(0, itemStack);
		item = net.minecraft.server.v1_8_R3.Item.getById(101);
		itemStack = new ItemStack(item);
		eventoNpc.equip(0, itemStack);

	}

	public enum ClickType {
		RIGHT, LEFT;
	}

	public enum NPCS {
		HG, PVP, GLADIATOR, EVENTO, SKYWARS, HG_DOUBLEKIT, HG_EVENTO;
	}

	public static java.util.List<NPCS> getNpcs() {
		java.util.List<NPCS> list = new ArrayList<>();
		for (NPCS npc : NPCS.values()) {
			list.add(npc);
		}
		return list;
	}

	public void actionBy(NPCS npc, ClickType type, Player forPlayer) {

		if (type == ClickType.RIGHT && npc == NPCS.HG) {
			forPlayer.openInventory(Lobby.getPlugin().getMenuManager().getMenu(MenuType.DOUBLEKITHG));
			return;
		}

		if (npc == NPCS.HG) {
			findServer(forPlayer, ServerType.DOUBLEKITHG);
		} else if (npc == NPCS.PVP) {
			findServer(forPlayer, ServerType.PVP_SIMULATOR);
		} else if (npc == NPCS.GLADIATOR) {
			findServer(forPlayer, ServerType.GLADIATOR);
		}

	}

	public void findServer(Player p, ServerType serverType) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("FindServer");
		out.writeUTF(serverType.name());
		p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

	public void connectMessage(Player p, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

}
