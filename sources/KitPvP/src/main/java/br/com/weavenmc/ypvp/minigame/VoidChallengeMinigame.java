package br.com.weavenmc.ypvp.minigame;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class VoidChallengeMinigame extends Minigame {

	private HashMap<UUID, Integer> voidTimers = new HashMap<>();

	public VoidChallengeMinigame() {
		setName("Void");
		setOtherNames(new String[] {});
		setTopKillStreakMinigame(false);
	}

	@Override
	public void join(Player p) {
		BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lVoid Challenge");
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		if (gamer.getWarp() != null)
			gamer.getWarp().quit(p);
		joinPlayer(p.getUniqueId());
		yPvP.getPlugin().getCooldownManager().removeCooldown(p);//
		yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));//
		gamer.setWarp(this);
		gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());

		teleport(p);
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		p.getInventory().setArmorContents(null);
		p.getInventory().clear();
		for (int i = 0; i < 36; i++)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		p.updateInventory();
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	public String timerFormat(int time) {
		if (time >= 3600) {
			int hours = (time / 3600), minutes = (time % 3600) / 60, seconds = (time % 3600) % 60;
			return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":"
					+ (seconds < 10 ? "0" : "") + seconds;
		} else {
			int minutes = (time / 60), seconds = (time % 60);
			return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
		}
	}

	public String getTimeFormat(int time) {
		if (time >= 3600) {
			int hours = (time / 3600), minutes = (time % 3600) / 60, seconds = (time % 3600) % 60;
			return (hours == 1 ? hours + " hora" : hours + " horas") + " "
					+ (minutes == 1 ? minutes + " minuto" : minutes + " minutos")
					+ (seconds <= 0 ? "" : " e " + (seconds == 1 ? seconds + " segundo" : seconds + " segundos"));
		} else if (time < 60) {
			return (time == 1 ? time + " segundo" : time + " segundos");
		} else {
			int minutes = (time / 60), seconds = (time % 60);
			return (minutes == 1 ? minutes + " minuto" : minutes + " minutos")
					+ (seconds <= 0 ? "" : " e " + (seconds == 1 ? seconds + " segundo" : seconds + " segundos"));
		}
	}
	
	public String getTimeSurviving(Player p) {
		if (!voidTimers.containsKey(p.getUniqueId()))
			return timerFormat(0);
		return timerFormat(voidTimers.get(p.getUniqueId()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (voidTimers.containsKey(p.getUniqueId())) {
			int survivalTime = voidTimers.get(p.getUniqueId());
			p.sendMessage("§5§lVOID CHALLENGE§f Você sobreviveu por §9§l"
					+ getTimeFormat(survivalTime).toUpperCase() + "§f!");
			if (survivalTime >= 1) {
				BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
				bP.addMoney(survivalTime);
				p.sendMessage("§6§lMOEDAS§f Você recebeu §6§l" + survivalTime + " MOEDAS");
				bP.save(DataCategory.BALANCE);
			}
			voidTimers.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		for (Player o : Bukkit.getOnlinePlayers()) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(o.getUniqueId());
			if (gamer.getWarp() != this)
				continue;
			if (o.getLocation().getY() > -64d)
				continue;
			if (o.isDead() || o.getHealth() <= 0d)
				continue;
			if (!voidTimers.containsKey(o.getUniqueId()))
				voidTimers.put(o.getUniqueId(), 0);
			voidTimers.put(o.getUniqueId(), voidTimers.get(o.getUniqueId()) + 1);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(((Player) event.getEntity()).getUniqueId());
			if (gamer.getWarp() == this) {
				DamageCause cause = event.getCause();
				if (cause != DamageCause.VOID) {
					event.setCancelled(true);
				}
				cause = null;
			}
			gamer = null;
		}
	}

	@Override
	public void quit(Player p) {
		if (voidTimers.containsKey(p.getUniqueId())) {
			voidTimers.remove(p.getUniqueId());
		}
		quitPlayer(p.getUniqueId());
		unprotect(p);
	}
}
