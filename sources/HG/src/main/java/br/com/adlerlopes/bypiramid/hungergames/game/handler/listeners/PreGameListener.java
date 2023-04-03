package br.com.adlerlopes.bypiramid.hungergames.game.handler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.player.events.PlayerInventoryOpenEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.events.ServerTimeEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.ServerOptions;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.core.permission.Group;

@SuppressWarnings("deprecation")
public class PreGameListener extends HungerListener {
	@EventHandler
	private void onServerListPing(ServerListPingEvent event) {
		String time = getManager().getUtils().toTime(getManager().getGameManager().getGameTime().intValue());
		if (getManager().getGameManager().isPreGame()) {
			event.setMotd("§cIniciando em " + time + "!\n§eVisite §ewww.mc-hype.com.br");
		} else if (getManager().getGameManager().isInvencibility()) {
			event.setMotd(
					"§cEm progresso na invencibilidade. Tente www.mc-hype.com.br.\n§eVisite §ewww.mc-hype.com.br");
		} else {
			event.setMotd("§cEm progresso. Tente www.mc-hype.com.br.\n§eVisite §ewww.mc-hype.com.br");
		}
	}

	@EventHandler
	private void onInventaryOpen(PlayerInventoryOpenEvent event) {
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
		BukkitPlayer bP = BukkitPlayer.getPlayer(gamer.getUUID());
		if ((gamer.isSpectating().booleanValue()) && (bP.getGroup().getId() < Group.YOUTUBERPLUS.getId())) {
			gamer.sendMessage("§6§lINVENTARIO §fVocê não pode §e§lABRIR§f isto agora.");
			event.setCancelled(true);
		}
	}
	
//	@EventHandler
//	private void onPlayerMove(PlayerMoveEvent event) {
//		if (getManager().getGameManager().isPreGame()) {
//			Player player = event.getPlayer();
//			if (event.getTo().distance(new Location(player.getWorld(), 0, player.getLocation().getY(), 0)) > 60) {
//				player.sendMessage("§e§lBORDA §fVocê está indo muito longe!");
//				player.teleport(event.getFrom());
//			}
//		}
//	}
	
	@EventHandler
	private void onSpawnEntity(EntitySpawnEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().toLowerCase();
		Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
		BukkitPlayer bP = BukkitPlayer.getPlayer(gamer.getUUID());
		if ((!getManager().getGameManager().isPreGame())
				&& ((command.startsWith("/admin")) || (command.startsWith("/gamemode"))
						|| (command.startsWith("/invsee")) || (command.startsWith("/effect"))
						|| (command.startsWith("/give")) || (command.startsWith("/enchant")))
				&& (!gamer.isSpectating().booleanValue()) && (bP.getGroup().getId() < Group.YOUTUBERPLUS.getId())) {
			event.setCancelled(true);
		}
		if ((!AdminMode.getInstance().isAdmin(gamer.getPlayer())) && (!gamer.isSpectating().booleanValue())
				&& (command.startsWith("/tp")) && (bP.getGroup().getId() < Group.YOUTUBERPLUS.getId())) {
			gamer.sendMessage("§6§lCOMMAND §fVocê não pode usar isso agora!");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.GHAST) {
			event.setCancelled(true);
		}
		if (getManager().getGameManager().isPreGame()) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void aoExplosion(ExplosionPrimeEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		if (getManager().getGameManager().isPreGame())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreakBlocks(BlockBreakEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		} else {
			int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
			Block block = event.getBlock();
			Location worldLocation = block.getWorld().getSpawnLocation();
			if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
					|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (getManager().getGameManager().isPreGame()) {
			e.setDeathMessage(null);

			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					e.getEntity().spigot().respawn();
				}
			}.runTaskLater(HungerGames.getManager().getPlugin(), 2l);
		}

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (getManager().getGameManager().isPreGame()) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					e.getPlayer().performCommand("spawn");
					
				}
			}.runTaskLater(HungerGames.getManager().getPlugin(), 2l);
		}
	}

	@EventHandler
	public void onPlaceBlocks(BlockPlaceEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		} else if (event.getPlayer().getWorld() == Bukkit.getWorld("world")) {
			int border = getManager().getGameManager().getTimer().getBorderSize() - 10;
			Block block = event.getBlock();
			Location worldLocation = block.getWorld().getSpawnLocation();
			if ((Math.abs(block.getLocation().getBlockX() + worldLocation.getBlockX()) >= border)
					|| (Math.abs(block.getLocation().getBlockZ() + worldLocation.getBlockZ()) >= border)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if (!(event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}
			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getEntity());
			if (!gamer.isOnPvpPregame().booleanValue()) {
				event.setCancelled(true);
			}
		} 
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			if ((!(event.getDamager() instanceof Player)) || (!(event.getEntity() instanceof Player))) {
				event.setCancelled(true);
				return;
			}
			Gamer gamer = getManager().getGamerManager().getGamer((Player) event.getDamager());
			if (!gamer.isOnPvpPregame().booleanValue()) {
				event.setCancelled(true);
			} else {
				event.setDamage(event.getDamage() - 1.7);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame().booleanValue()) {
				if (gamer.getPlayer().getItemInHand().getType().toString().contains("SWORD_")) {

					event.setCancelled(true);
				}
				event.setCancelled(true);
			}
		} else if ((!ServerOptions.DROPS.isActive().booleanValue())
				&& (bP.getGroup().getId() < Group.YOUTUBERPLUS.getId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (getManager().getGameManager().isPreGame()) {
			Gamer gamer = getManager().getGamerManager().getGamer(event.getPlayer());
			if (!gamer.isOnPvpPregame().booleanValue()) {
				event.setCancelled(true);
			}
			if (event.getPlayer().getItemInHand().getType().equals(Material.STONE_SWORD)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (((event.getEntity() instanceof Player)) && (getManager().getGameManager().isPreGame())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSecond(ServerTimeEvent event) {
		if (!getManager().getGameManager().isPreGame()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				int border = 500;
				Location location = player.getLocation();
				if (player.getLocation().getWorld() == Bukkit.getWorld("world")) {
					Location worldLocation = location.getWorld().getSpawnLocation();
					if ((Math.abs(location.getBlockX() + worldLocation.getBlockX()) >= border + 1)
							|| (Math.abs(location.getBlockZ() + worldLocation.getBlockZ()) >= border + 1)) {
						if (getManager().getGamerManager().getGamer(player).isAlive().booleanValue()) {
							player.damage(4.0D, player);
							player.sendMessage(
									"§4§lBORDA §fVocê §c§lULTRAPASSOU§f a borda do §6§lMUNDO§f! Volte para o §e§lSPAWN§f!");
							worldLocation.getWorld().strikeLightning(player.getLocation());
						} else {
							player.teleport(worldLocation);
							return;
						}
					}
					int value = getManager().getGameManager().getBorderTime();
					if (((location.getBlockX() > worldLocation.getBlockX() + value)
							|| (location.getBlockX() < -(value - worldLocation.getBlockX()))
							|| (location.getBlockZ() > worldLocation.getBlockZ() + value)
							|| (location.getBlockZ() < -(value - worldLocation.getBlockZ())))
							&& (getManager().getGameManager().isPreGame())) {
						getManager().getGamerManager().teleportSpawn(player);
						return;
					}
				}
			}
		}
	}
}
