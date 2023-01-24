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
import br.com.weavenmc.lobby.npcs.NPC;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ItemStack;

@Getter
public class NpcManager {
	@Getter
	static NPC hgNpc, slaNpc;

	private static Location getLocation(NPCS npc) {
		double x = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.x");
		double y = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.y");
		double z = Lobby.getPlugin().getConfig().getDouble("npc." + npc.toString().toLowerCase() + ".location.z");

		return new Location(Bukkit.getWorld("world"), x, y, z);
	}

	static int onlineCount;

	static {

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);

		Location locHgTwo = getLocation(NPCS.SKYWARS);
		locHgTwo.setY(locHgTwo.getY() + 2.4);

		Hologram holoHg = HologramAPI.createHologram(locHgTwo, "§b" + onlineCount + " jogando...");
		holoHg.spawn();
		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);

		locHgTwo = getLocation(NPCS.RANKED);
		locHgTwo.setY(locHgTwo.getY() + 2.4);
		Hologram holoSla = HologramAPI.createHologram(locHgTwo, "§b" + 0 + " jogando.");
		holoSla.spawn();

	
		new BukkitRunnable() {

			@Override
			public void run() {

				//

				onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);
				holoHg.setText("§b" + onlineCount + " jogando...");
				holoSla.setText("§b" + 0 + " jogando...");

				//

				holoHg.update();
				holoSla.update();
				hgNpc.rmvFromTablist();
				hgNpc.addToTablist();
				
				slaNpc.rmvFromTablist();

				slaNpc.addToTablist();

			}
		}.runTaskTimerAsynchronously(Lobby.getPlugin(), 0, 15);

	}

	public NpcManager() {

		// constructor

		slaNpc = new NPC("§7Clique aqui", getLocation(NPCS.RANKED));
		hgNpc = new NPC("§7Clique aqui", getLocation(NPCS.SKYWARS));

		// define texturas

		hgNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5NDY2NzMzMjM4OCwKICAicHJvZmlsZUlkIiA6ICIzZTg4MmI4NzZkYmY0ZmFjODNlNzAwYTJmM2Q2N2VlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzYWtvYmkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZmNDZkMzgxZWNhY2MwNDc0YTUyZjYxMTBkZjdmMGY4NTk2OTdmZjFkYWU3NjQyMjY5ZGZlOTMxMzM5YWU1ZSIKICAgIH0KICB9Cn0=",
				"ryfPUBg4WY2dZPbAqhoNUgdNU9goUxRGw+OBQDA9/+wnXHo9XBHP6IHvUOknfGedgrk3Qo8W9YskvpAES5iA3lXYCJXW4EvTFFfV3ZVeFVi4SfSiVaRuDLCC5fg86yX2wb8pOk9PhdTwmctBP40Yp0qobpS2g49kHTzwomUN9Q1NwAkIS48oaXRAQs7wyyVEYIzMAEgP8KweV1TAUigRn2Ml4aKm4M705IXKNe6W1AkiI7xrFXOnA7iMeb4Q3YEWvKnrSaYYrvWB7X8WHzWDL23cML/4i4iFElwrtLkznDvdKpsQHnnQojq5n+d7kpwA8BvrJM1T0MCkb5TOTDNB/srvQsaxlFQNRzFwRpxGaXPY/75mNZKkY9Ud3UdNSeOoXoEPOO++Te7V/fdmqDI38cmKkTnxSAFFzCG8uVLCpbciCAC41PMmbhKrWGUl7qjJZ8DmZI71G12VlRHxpK/gJHWOPx8Bu8WZ7J5QsJ4CvvXrTuMpvgbP2CvpIo54T3qfkaiyGs0yTMPkgTIHE7VEOebY3VG2a99MbIeC3/j0LUBaXMODXvkrzNeYL+Fqm2kqTQ1pn1wuX0A5cJ+7zIrvE8ayYECH/6r1kE2GDTnjvJ2RFC9MwFZG7Gp4gfqLV9PzLPM8fYRPAQVESXTkFfXjzykWujEjbvT6YuuoGGFA3/0=");
		// holo pvp
		Location locHgOne3 = getLocation(NPCS.SKYWARS);
		locHgOne3.setY(locHgOne3.getY() + 2.8);

		HologramAPI.createHologram(locHgOne3, "§eModo Solo").spawn();

		Location locHgOne = getLocation(NPCS.RANKED);
		locHgOne.setY(locHgOne.getY() + 2.7);

		locHgOne3 = getLocation(NPCS.RANKED);
		locHgOne3.setY(locHgOne3.getY() + 3);
		slaNpc.changeSkin(
				"ewogICJ0aW1lc3RhbXAiIDogMTU5NDY2NzUyNjk4MSwKICAicHJvZmlsZUlkIiA6ICIxMTliMmFkMzg4NzU0MGM2YWNiZThlN2EzMjJiOTFmMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJub3RjYXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkyZWM4ZDNhZTkzYmEyYjEzZThiZTBlNzRlY2FhNThkYzNkMDU4MDI4MjlmZjA3MDE1Njc2YjQwOTAzMjQwZCIKICAgIH0KICB9Cn0=",
				"sqGTNmxin1BhwTNJy9su+yu6SNcXNmJ0yJxNweN1/VsMD3eqxTUTDJKIQb8s/mUQ5Mvba0omkvWk37D6CF7m5FQ/u4oBb+PZt4UWlmXmeTTyuyAlrHG1bXQAhVV0op1TsqYcggrnK/N+68gD/TgAvM29IujHxIwkN4V3kjMIsfaCYSne7XEcZoSmvKL4JfCU6s/UzmZUt0UHXKuAG5Yyk/tksJagrBR5siBBYnlGi/4npR+U5FYFWmElDInpHCPQ92hOsg+hT3m0GJRPovTX3PSMzafyph9RNqOutJf4YUAT20zHuV+Q28EFpfKLAOA2gd0O3NNsW2SX9ODBqOckXI6TliXTwnAKnXSr9CUXBvz+o8CsaaRnBio83hV7qYI+URXvnAHecm9NtpG24fO2q+iZ0ZhVyswM19Ztx1N0XoEYMIm4NdkxdHH/JTu8ogvg91+1eIfJVHSHD7oiLM2/Y8j6gI13KP8QOCKX5gin+bPBr/domZj+MjAmNge3FoQ/8+VGD6xJKiNihlzSRV4cwWVppRJ1lUhdIcRgK9bdsCSiP1cpWpQnky5yI9hsa/ZlqD0HdV1OqQLwHKQJP+bTyL0RKmVY66Y08qBKd/4JC9M8kDGotZ8ZQC66opRJLaUD3A9Knp0rbouImNY5d3vzI+I+QL90/0UEofR7CKsz+88=");

		HologramAPI.createHologram(locHgOne, "§eModo Ranqueado").spawn();

		hgNpc.addToTablist();

		hgNpc.rmvFromTablist();

	}

	public void spawnNpcs(Player forPlayer) {
		net.minecraft.server.v1_8_R3.Item item = net.minecraft.server.v1_8_R3.Item.getById(282);
		ItemStack itemStack = new ItemStack(item);
		hgNpc.equip(0, itemStack);
		item = net.minecraft.server.v1_8_R3.Item.getById(101);
		itemStack = new ItemStack(item);
		hgNpc.spawn(forPlayer);
		hgNpc.headRotation(-90, 3);
		slaNpc.spawn(forPlayer);
		slaNpc.headRotation(-90, 3);

	}

	public enum ClickType {
		RIGHT, LEFT;
	}

	public enum NPCS {
		SKYWARS, RANKED;
	}

	public static java.util.List<NPCS> getNpcs() {
		java.util.List<NPCS> list = new ArrayList<>();
		for (NPCS npc : NPCS.values()) {
			list.add(npc);
		}
		return list;
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
