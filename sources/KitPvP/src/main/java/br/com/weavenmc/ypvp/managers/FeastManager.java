package br.com.weavenmc.ypvp.managers;

import org.bukkit.scheduler.*;

import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.util.FeastConfig;

import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.block.*;
import java.util.*;

public class FeastManager {
	private int feastTimer;
	private BukkitTask feastTask;
	private BukkitTask subFeastTask;

	private FeastConfig getFeastConfig() {
		return yPvP.getPlugin().getFeastConfig();
	}

	public FeastManager() {
		this.feastTimer = 900;
	}

	  public void broadcast(final int timeBeforeSpawn) {
	        final int minutes = timeBeforeSpawn % 3600 / 60;
	        final int seconds = timeBeforeSpawn % 3600 % 60;
	        if (minutes <= 2 && seconds == 0) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + minutes
	                    + " §fminutos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 30) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " §fsegundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 10) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " §fsegundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 5) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " segundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 4) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " segundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 3) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " segundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 2) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " segundos§f. Digite: §b/feast§f para apontar a b\u00fassola.");
	        } else if (minutes < 1 && seconds == 1) {
	            Bukkit.broadcastMessage("§a§lFEAST§f ir\u00e1 spawnar em §e" + seconds
	                    + " §fsegundo. Digite: §b/feast§f para apontar a b\u00fassola.");
	        }
	    }
	public void start() {
		this.clear();
		this.feastTask = new BukkitRunnable() {
			public void run() {
				final FeastManager this$0 = FeastManager.this;
				FeastManager.access$1(this$0, this$0.feastTimer - 1);
				if (FeastManager.this.feastTimer <= 0) {
					FeastManager.this.items();
					Bukkit.broadcastMessage("§a§lFEAST§f spawnou§f. Digite: §b/feast§f para apontar a b\u00fassola.");
					FeastManager.access$2(FeastManager.this, new BukkitRunnable() {
						public void run() {
							FeastManager.this.start();
						}
					}.runTaskLater((Plugin) yPvP.getPlugin(), 600L));
					FeastManager.this.stop();
				} else {
					FeastManager.this.broadcast(FeastManager.this.feastTimer);
				}
			}
		}.runTaskTimer((Plugin) yPvP.getPlugin(), 20L, 20L);
	}

	public void setTimer(final int timer) {
		this.feastTimer = timer;
	}

	public void stop() {
		if (this.feastTask != null) {
			this.feastTask.cancel();
		}
		this.feastTimer = 241;
	}

	public void items() {
		final List<Chest> listOfChest = new LinkedList<Chest>();
		if (!getFeastConfig().hasLocation()) {
			return;
		}
		for (final Location localLocation : getFeastConfig().getAllChestLocations()) {
			if (localLocation == null) {
				continue;
			}
			final Block localBlock = localLocation.getBlock();
			if (localBlock.getType() != Material.CHEST) {
				continue;
			}
			final Chest localChest = (Chest) localBlock.getState();
			listOfChest.add(localChest);
		}
		for (final Chest localChest2 : listOfChest) {
			localChest2.update(true);
			for (final ItemStack stack : FeastItems.generateItems()) {

				if (new Random().nextBoolean())
					localChest2.getInventory().addItem(new ItemStack[] { stack });
			}
		}
	}

	public void clear() {
		final List<Chest> listOfChest = new LinkedList<Chest>();
		if (!getFeastConfig().hasLocation()) {
			return;
		}
		for (final Location localLocation : getFeastConfig().getAllChestLocations()) {
			final Block localBlock = localLocation.getBlock();
			if (localBlock.getType() == Material.CHEST) {
				final Chest localChest = (Chest) localBlock.getState();
				listOfChest.add(localChest);
			}
		}
		for (final Chest localChest2 : listOfChest) {
			localChest2.update(true);
			localChest2.getInventory().clear();
		}
	}

	static /* synthetic */ void access$1(final FeastManager feastManager, final int feastTimer) {
		feastManager.feastTimer = feastTimer;
	}

	static /* synthetic */ void access$2(final FeastManager feastManager, final BukkitTask subFeastTask) {
		feastManager.subFeastTask = subFeastTask;
	}

	public static class FeastItems {
		private static Random r;

		static {
			FeastItems.r = new Random();
		}

		public static List<ItemStack> generateItems() {
			final List<ItemStack> feastItems = new ArrayList<ItemStack>();
			feastItems.addAll(addItem(Material.IRON_SWORD, 0, 2));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.LEATHER_HELMET, 0, 1));
			feastItems.addAll(addItem(Material.LEATHER_CHESTPLATE, 0, 1));
			feastItems.addAll(addItem(Material.MUSHROOM_SOUP, 1, 2));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.ENDER_PEARL, 2, 4));
			feastItems.addAll(addItem(Material.FISHING_ROD, 0, 1));
			feastItems.addAll(addItem(Material.SNOW_BALL, 6, 10));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.ARROW, 12, 36));
			feastItems.addAll(addItem(Material.BOW, 1, 2));
			feastItems.addAll(addItem(Material.EXP_BOTTLE, 2, 4));
			feastItems.addAll(addItem(Material.GOLDEN_APPLE, 2, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.POTION, (short) 16385, 0, 2));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.POTION, (short) 16389, 0, 2));
			feastItems.addAll(addItem(Material.AIR, 1, 5));
			feastItems.addAll(addItem(Material.AIR, 1, 5));

			Collections.shuffle(feastItems);
			return feastItems;
		}

		private static List<ItemStack> addItem(final Material mat, final int min, final int max) {
			return addItem(mat, (short) 0, min, max);
		}

		private static List<ItemStack> addItem(final Material mat, final short durability, final int min,
				final int max) {
			final List<ItemStack> items = new ArrayList<ItemStack>();
			for (int i = 0; i <= min + FeastItems.r.nextInt(max - min); ++i) {
				items.add(new ItemStack(mat, 1, durability));
			}

			return items;
		}
	}
}
