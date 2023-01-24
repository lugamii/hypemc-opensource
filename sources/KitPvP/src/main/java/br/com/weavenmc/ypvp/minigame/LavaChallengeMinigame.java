package br.com.weavenmc.ypvp.minigame;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class LavaChallengeMinigame extends Minigame {

	public LavaChallengeMinigame() {
		setName("Lava");
		setOtherNames(new String[] { "Challenge" });
		setTopKillStreakMinigame(false);
	}

	@Override
	public void join(Player p) {
		BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lLava Challenge");
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		if (gamer.getWarp() != null)
			gamer.getWarp().quit(p);
		joinPlayer(p.getUniqueId());
		yPvP.getPlugin().getCooldownManager().removeCooldown(p);//
		yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));//
		gamer.setWarp(this);
		gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		p.getInventory().setArmorContents(null);
		p.getInventory().clear();
		for (int i = 0; i < 36; i++)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		p.getInventory().setItem(0, new ItemBuilder().type(Material.STONE_SWORD).name("§6§lLava Challenge").build());
		p.updateInventory();
		teleport(p);
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(((Player) event.getEntity()).getUniqueId());
			if (gamer.getWarp() == this) {
				DamageCause cause = event.getCause();
				if (cause != DamageCause.FIRE && cause != DamageCause.FIRE_TICK && cause != DamageCause.LAVA) {
					event.setCancelled(true);
				}
				cause = null;
			}
			gamer = null;
		}
	}

	@Override
	public void quit(Player p) {
		quitPlayer(p.getUniqueId());
		unprotect(p);
	}
}
