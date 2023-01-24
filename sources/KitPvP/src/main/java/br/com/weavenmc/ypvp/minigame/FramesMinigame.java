package br.com.weavenmc.ypvp.minigame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import br.com.weavenmc.ypvp.yPvP.PvPType;

public class FramesMinigame extends Minigame {

	public FramesMinigame() {
		setName("Fps");
		setOtherNames(new String[] { "Frames" });
		setTopKillStreakMinigame(true);
	}

	@Override
	public void join(Player p) {
		BossBarAPI.removeBar(p);
		if (!TeleportManager.getInstance().canJoin(p, this))
			return;
		if (p.getAllowFlight() && !AdminMode.getInstance().isAdmin(p))
			p.setAllowFlight(false);
		p.sendMessage("§9§lTELEPORTE§f Você foi teleportado para §3§lFPS");
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		gamer.resetCombat();
		if (gamer.getWarp() != null)
			gamer.getWarp().quit(p);
		joinPlayer(p.getUniqueId());
		yPvP.getPlugin().getCooldownManager().removeCooldown(p);//
		yPvP.getPlugin().getAbilityManager().getAbilities().stream().forEach(ability -> ability.eject(p));//

		p.sendMessage("§8§lPROTEÇÃO§f Você §7§lRECEBEU§f sua proteção de spawn");
		gamer.setWarp(this);
		gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.getActivePotionEffects().clear();
		teleport(p);
		protect(p);
		yPvP.getPlugin().getTournament().quitPlayer(p);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		for (int i = 0; i < 36; i++)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		if (yPvP.getPlugin().getPvpType() == PvPType.FULLIRON) {
			ItemBuilder builder = new ItemBuilder().type(Material.IRON_HELMET);
			p.getInventory().setHelmet(builder.build());
			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
			p.getInventory().setChestplate(builder.build());
			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
			p.getInventory().setLeggings(builder.build());
			builder = new ItemBuilder().type(Material.IRON_BOOTS);
			p.getInventory().setBoots(builder.build());
			builder = new ItemBuilder().type(Material.DIAMOND_SWORD);
			builder.enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());
			builder = null;
		} else {
			ItemBuilder builder = new ItemBuilder().type(Material.STONE_SWORD);
			builder.enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());
			builder = null;
		}
		ItemBuilder builder = new ItemBuilder().type(Material.BOWL).amount(64);
		p.getInventory().setItem(13, builder.build());
		builder = new ItemBuilder().type(Material.RED_MUSHROOM).amount(64);
		p.getInventory().setItem(14, builder.build());
		builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).amount(64);
		p.getInventory().setItem(15, builder.build());
		p.updateInventory();
		yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
		builder = null;
	}

	@Override
	public void quit(Player p) {
		quitPlayer(p.getUniqueId());
		unprotect(p);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() == this) {
				if (isProtected(p)) {
					event.setCancelled(true);
				}
			}
			gamer = null;
			p = null;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (gamer.getWarp() == this) {
				if (!isProtected(p)) {
					if (event.getDamager() instanceof Player) {
						Player t = (Player) event.getDamager();
						Gamer game = yPvP.getPlugin().getGamerManager().getGamer(t.getUniqueId());
						if (game.getWarp() == this) {
							if (isProtected(t)) {
								event.setCancelled(false);
								unprotect(t);
								t.sendMessage("§8§lPROTEÇÃO§f Você §7§lPERDEU§f sua proteção de spawn");
							}
						}
					}
				} else if (isProtected(p)) {
					event.setCancelled(true);
				}
			}
			gamer = null;
			p = null;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSpawnMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (gamer.getWarp() == this) {
			if (isProtected(p)) {
				Location fps = yPvP.getPlugin().getLocationManager().getLocation("fps");
				if (fps != null) {
					if (p.getLocation().distance(fps) > 13) {
						unprotect(p);
						p.sendMessage("§8§lPROTEÇÃO§f Você §7§lPERDEU§f sua proteção de spawn");
					}
					fps = null;
				}
			}
		}
		gamer = null;
		p = null;
	}
}
