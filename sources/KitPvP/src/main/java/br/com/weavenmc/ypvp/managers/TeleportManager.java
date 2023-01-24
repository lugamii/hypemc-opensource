package br.com.weavenmc.ypvp.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.Minigame;

public class TeleportManager implements Listener {

	private final static TeleportManager instance = new TeleportManager();

	private boolean listenerRegistered = false;
	private final HashMap<UUID, BukkitTask> teleportTask = new HashMap<>();

	public TeleportManager() {
		if (!listenerRegistered) {
			Bukkit.getPluginManager().registerEvents(this, yPvP.getPlugin());
			listenerRegistered = true;
		}
	}
	
	public void allowJoin(Player p) {
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		BukkitTask currentTask = teleportTask.get(p.getUniqueId());
		if (currentTask != null) {
			currentTask.cancel();
			teleportTask.remove(p.getUniqueId());
		}
	}

	@SuppressWarnings("deprecation")
	public boolean canJoin(Player p, Minigame minigame) {
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		BukkitTask currentTask = teleportTask.get(p.getUniqueId());
		if (currentTask != null) {
			currentTask.cancel();
			teleportTask.remove(p.getUniqueId());
			p.sendMessage("§9§lTELEPORTE§f Você §3§lCANCELOU§f o teleporte!");
			return false;
		} else {
			if (gamer.inCombat()) {
				if (!p.isOnGround()) {
					p.sendMessage("§9§lTELEPORTE§f Você precisa estar no §3§lCHAO§f para teleportar!");
					return false;
				} else {
					p.sendMessage("§9§lTELEPORTE§f Voce sera teleportado em §3§l5 SEGUNDOS§f. Não se mexa!");
					teleportTask.put(p.getUniqueId(), new BukkitRunnable() {
						@Override
						public void run() {
							BukkitTask task = teleportTask.get(p.getUniqueId());
							if (task != null) {
								gamer.resetCombat();
								minigame.join(p);
								teleportTask.remove(p.getUniqueId());
							}
						}
					}.runTaskLater(yPvP.getPlugin(), 5 * 20));
				}
				return false;
			} else {
				return true;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeleportMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!teleportTask.containsKey(p.getUniqueId()))
			return;
		BukkitTask currentTask = teleportTask.get(p.getUniqueId());
		currentTask.cancel();
		teleportTask.remove(p.getUniqueId());
		p.sendMessage("§9§lTELEPORTE§f Seu teleporte foi §3§lCANCELADO§f pois voce se mexeu!");
	}

	public static TeleportManager getInstance() {
		return instance;
	}
}
