package br.com.weavenmc.ypvp.minigame;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;

public class FishermanMinigame extends Minigame {

	public FishermanMinigame() {
		setName("Fisherman");
		setOtherNames(new String[] { "Fish" });
		setTopKillStreakMinigame(true);
	}

	@Override
	public void join(Player p) {
		BossBarAPI.removeBar(p);
		
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		
		p.setFallDistance(-5);
		p.setNoDamageTicks(30);
		
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lFISHERMAN");
		
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
		
		yPvP.getPlugin().getTournament().quitPlayer(p);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		ItemBuilder builder = new ItemBuilder().type(Material.FISHING_ROD).name("§6§lFISHERMAN").unbreakable();
		p.getInventory().setItem(0, builder.build());
		builder = null;
		
		p.updateInventory();
		
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
		
		teleportToRandomLocation(p);
		unprotect(p);
	}
	
	public void teleportToRandomLocation(Player p) {
		p.setFallDistance(-5);
		int a = new Random().nextInt(9);
		p.teleport(yPvP.getPlugin().getLocationManager().getLocation("fisherman" + a) == null
				? yPvP.getPlugin().getLocationManager().getLocation("fisherman" + 1)
				: yPvP.getPlugin().getLocationManager().getLocation("fisherman" + a));
	}

	@Override
	public void quit(Player p) {
		quitPlayer(p.getUniqueId());
		unprotect(p);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		
		if (yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId()).getWarp() == this) {
			
			if (event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile)event.getDamager();
				
				if (projectile instanceof FishHook) {
					
					if (projectile.getShooter() instanceof Player) {
						Player shooter = (Player) projectile.getShooter();
						
						if (shooter == player) {
							event.setCancelled(true);
						} else {
							gamer.addCombat(shooter.getUniqueId(), 5);
						}
						shooter = null;
					}
					
				}
				projectile = null;
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerHitFishingRodEvent(final PlayerFishEvent event) {
		final Player player = event.getPlayer();
	    if (event.getCaught() instanceof Player) {
	        final Player caught = (Player) event.getCaught();
	        if (caught == player) {
	        	event.setCancelled(true);
	        	return;
	        }
			Gamer g = yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId());
			if (g.getWarp() != this)
				return;
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(caught.getUniqueId());
	        gamer.addCombat(player.getUniqueId(), 7);
	        
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
			
			int valor = bP.getData(DataType.PVP_FISHERMAN_HOOKEDS).asInt() + 1;
			
			bP.getData(DataType.PVP_FISHERMAN_HOOKEDS).setValue(valor);
			
			bP.save(DataCategory.KITPVP);
			
	        yPvP.getPlugin().getScoreboardManager().createScoreboard(player);
	    }
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
	    	
		if (yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId()).getWarp() != this) {
	    	return;
	    }
	    	
	    if (player.getItemInHand().getType() != null && player.getItemInHand().getType() == Material.FISHING_ROD) {
	    	event.setCancelled(true);
	    }
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {   
	    if (!(event.getEntity() instanceof Player))
	    	return;
	        
	    Player player = (Player) event.getEntity();
	    
	    if (yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId()).getWarp() == this) {
			if (event.getCause() == DamageCause.VOID) {
				event.setCancelled(true);
				
				Player derrubou = Bukkit.getPlayer(yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId()).getLastCombat());
				
				if (derrubou != null) {
					handleStats(derrubou, player);
					derrubou = null;
				}
				
				join(player);
			}
	    }
	}

	private void handleStats(Player derrubou, Player player) {
		BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(player.getUniqueId());
		
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(derrubou.getUniqueId());
		bP.addXp(5);
		bP.addMoney(80);
		int kills = bP.getData(DataType.PVP_KILLS).asInt();
		bP.getData(DataType.PVP_KILLS).setValue(kills += 1);
		int killStreak = bP.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
		int maxStreak = bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
		
		if (killStreak > maxStreak)
			bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
		
		bP.getData(DataType.PVP_KILLSTREAK).setValue(killStreak);
		
		derrubou.sendMessage("§e§lKILL§f Você matou §e§l" + player.getName());
		derrubou.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
		derrubou.sendMessage(
				"§9§lXP§f Você recebeu §9§l5" + " XPs" + (bP.isDoubleXPActived() ? " §7(doublexp)" : ""));
		
		int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
		bPLoser.getData(DataType.PVP_DEATHS).setValue(deaths += 1);
		bPLoser.getData(DataType.PVP_KILLSTREAK).setValue(0);
		bPLoser.removeMoney(1);
		
		player.sendMessage("§c§lMORTE§f Você morreu para §e§l" + derrubou.getName());
		player.sendMessage("§4§lMONEY§f Você perdeu §4§l1 MOEDA");
		
		bPLoser.save(DataCategory.KITPVP, DataCategory.BALANCE);
		bP.save(DataCategory.BALANCE, DataCategory.KITPVP);
		
		yPvP.getPlugin().getScoreboardManager().createScoreboard(derrubou);
	}
}