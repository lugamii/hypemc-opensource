package br.com.weavenmc.commons.bukkit.worldedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.core.permission.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;

public class AsyncWorldEdit {

	private static AsyncWorldEdit instance;

	private HashMap<UUID, Location> firstPositions, secondPositions;

	private Queue<AsyncBlock> asyncBlocks;
	private ThreadBlockUpdate currentUpdate;

	protected AsyncWorldEdit() {

		if (instance != null)
			throw new IllegalStateException("Cannot initialize AsyncWorldEdit twice!");

		this.firstPositions = new HashMap<>();
		this.secondPositions = new HashMap<>();
		this.asyncBlocks = new ConcurrentLinkedQueue<>();

		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler()
			public void onInteractListener(PlayerInteractEvent event) {
				Player p = event.getPlayer();
				BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
				if (bP.getGroup().getId() < Group.YOUTUBERPLUS.getId())
					return;
				if (p.getItemInHand().getType() != Material.WOOD_AXE)
					return;
				if (p.getGameMode() != GameMode.CREATIVE)
					return;
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					event.setCancelled(true);
					Block firstPosition = event.getClickedBlock();
					firstPositions.put(p.getUniqueId(), firstPosition.getLocation());
					p.sendMessage("§6§lWORLDEDIT§f Posição 1 marcada em §e[" + firstPosition.getLocation().getBlockX()
							+ ", " + firstPosition.getLocation().getBlockY() + ", "
							+ firstPosition.getLocation().getBlockZ() + "] §6(" + firstPosition.getType().name() + ")");
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					event.setCancelled(true);
					Block secondPosition = event.getClickedBlock();
					secondPositions.put(p.getUniqueId(), secondPosition.getLocation());
					p.sendMessage("§6§lWORLDEDIT§f Posição 2 marcada em §e[" + secondPosition.getLocation().getBlockX()
							+ ", " + secondPosition.getLocation().getBlockY() + ", "
							+ secondPosition.getLocation().getBlockZ() + "] §6(" + secondPosition.getType().name()
							+ ")");
				}
			}

			@EventHandler
			public void onAsyncBlocks(UpdateEvent event) {
				if (event.getType() != UpdateType.TICK)
					return;

				if (currentUpdate != null && currentUpdate.isAlive())
					return;

				if (asyncBlocks.isEmpty())
					return;

				(currentUpdate = new ThreadBlockUpdate()).start();
			}

		}, BukkitMain.getInstance());
	}

	public int getCurrentUpdates() {
		if (currentUpdate != null)
			return currentUpdate.getUpdates();
		return 0;
	}
	
	public void setAsyncBlock(World world, Location location, String blockIdAndData) throws Exception {
		String[] idAndData = blockIdAndData.split(":");

		if (idAndData.length == 0)
			throw new IllegalStateException(
					"Nao foi possivel encontrar o ID ou DATA na string '" + blockIdAndData + "'");

		final int blockId;

		try {
			blockId = Integer.valueOf(idAndData[0]);
		} catch (Exception ex) {
			throw new NumberFormatException("NumberFormatException para a string '" + idAndData[0] + "'");
		}

		final byte blockData;

		if (idAndData.length > 1) {
			try {
				blockData = Byte.valueOf(idAndData[1]);
			} catch (Exception ex) {
				throw new NumberFormatException("NumberFormatException para a string '" + idAndData[1] + "'");
			}
		} else {
			blockData = (byte) 0;
		}

		setAsyncBlock(world, location, blockId, blockData);
	}

	public void setAsyncBlock(World world, Location location, int blockId) {
		setAsyncBlock(world, location, blockId, (byte) 0);
	}

	public void setAsyncBlock(World world, Location location, int blockId, byte data) {
		setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
	}

	public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		BlockPosition bp = new BlockPosition(x, y, z);
		int i = blockId + (data << 12);
		IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
		chunk.a(bp, ibd);
		asyncBlocks.add(new AsyncBlock(world, bp));
	}

	public Queue<AsyncBlock> getAsyncBlocks() {
		return asyncBlocks;
	}

	public Location getPosition1(Player player) {
		return firstPositions.get(player.getUniqueId());
	}

	public boolean hasFirstPosition(Player player) {
		return getPosition1(player) != null;
	}

	public Location getPosition2(Player player) {
		return secondPositions.get(player.getUniqueId());
	}

	public boolean hasSecondPosition(Player player) {
		return getPosition2(player) != null;
	}

	public static void setInstance(AsyncWorldEdit asyncWorldEdit) {
		if (instance != null)
			throw new IllegalStateException("Cannot set AsyncWorldEdit instance twice!");

		instance = asyncWorldEdit;
	}

	public List<Location> fromTwoPoints(Location firstPoint, Location secondPoint) {
		List<Location> result = new ArrayList<>();

		int bX = (firstPoint.getBlockX() < secondPoint.getBlockX() ? secondPoint.getBlockX() : firstPoint.getBlockX());
		int bBX = (firstPoint.getBlockX() > secondPoint.getBlockX() ? secondPoint.getBlockX() : firstPoint.getBlockX());

		int bY = (firstPoint.getBlockY() < secondPoint.getBlockY() ? secondPoint.getBlockY() : firstPoint.getBlockY());
		int bBY = (firstPoint.getBlockY() > secondPoint.getBlockY() ? secondPoint.getBlockY() : firstPoint.getBlockY());

		int bZ = (firstPoint.getBlockZ() < secondPoint.getBlockZ() ? secondPoint.getBlockZ() : firstPoint.getBlockZ());
		int bBZ = (firstPoint.getBlockZ() > secondPoint.getBlockZ() ? secondPoint.getBlockZ() : firstPoint.getBlockZ());

		for (int x = bBX; x <= bX; x++) {
			for (int z = bBZ; z <= bZ; z++) {
				for (int y = bBY; y <= bY; y++) {
					result.add(new Location(firstPoint.getWorld(), x, y, z));
				}
			}
		}
		return result;
	}

	public static AsyncWorldEdit getInstance() {
		if (instance != null)
			return instance;

		AsyncWorldEdit asyncWorldEdit = new AsyncWorldEdit();
		setInstance(asyncWorldEdit);

		return getInstance();
	}
	
	protected class ThreadBlockUpdate extends Thread {
		
		private int updates = 0;
		
		@Override
		public void run() {
			while (!asyncBlocks.isEmpty()) {
				AsyncBlock asyncBlock = asyncBlocks.poll();
				((CraftWorld) asyncBlock.getWorld()).getHandle().notify(asyncBlock.getBlockposition());
				updates++;
			}
		}
		
		public int getUpdates() {
			return updates;
		}
	}

	@Getter
	@AllArgsConstructor
	protected class AsyncBlock {

		private World world;
		private BlockPosition blockposition;
	}
}
