package br.com.weavenmc.ypvp.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarAPI;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.managers.TeleportManager;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SpawnMinigame;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class PlayerListener implements Listener {

	@SuppressWarnings("deprecation")
	public PlayerListener() {
		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CACTUS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.NETHER_STALK), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.SUGAR), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.PUMPKIN_SEEDS),
				new MaterialData(Material.PUMPKIN_SEEDS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CARROT_ITEM),
				new MaterialData(Material.POTATO_ITEM), new MaterialData(Material.BOWL)));
	}

	public void newShapelessRecipe(ItemStack result, List<MaterialData> materials) {
		ShapelessRecipe recipe = new ShapelessRecipe(result);
		for (MaterialData mat : materials) {
			recipe.addIngredient(mat);
		}
		Bukkit.addRecipe(recipe);
	}

	@EventHandler
	public void onLoginListener(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (bP != null) {
			Gamer gamer;
			yPvP.getPlugin().getGamerManager().loadGamer(bP.getUniqueId(), gamer = new Gamer(bP));
			gamer.setAbility(yPvP.getPlugin().getAbilityManager().getNone());
			WeavenMC.getAsynchronousExecutor().runAsync(() -> {
				bP.load(DataCategory.KITPVP, DataCategory.LAVA_CHALLENGE);
			});
		} else {
			event.disallow(Result.KICK_OTHER, "§4§lERRO§f Ocorreu um erro ao tentar carregar sua conta.");
		}
	}

	@EventHandler
	public void onJoinListener(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player p = event.getPlayer();
		yPvP.getPlugin().getWarpManager().getWarp(SpawnMinigame.class).join(p);
		p.sendMessage("");
		p.sendMessage("§fEscolha seu kit clicando no §e§lBAU§f da sua §e§lMAO");
		p.sendMessage("");
		p.sendMessage("§9§lTENHA UM BOM JOGO!");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuitListener(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		yPvP.getPlugin().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCombatLogout(PlayerQuitEvent event) {
		Player logout = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(logout.getUniqueId());
		if (gamer.inCombat()) {
			if ((!gamer.getWarp().getName().equalsIgnoreCase("1v1")) && (!gamer.getWarp().getName().equalsIgnoreCase("Fisherman"))) {
				Player winner = Bukkit.getPlayer(gamer.getLastCombat());
				if (winner != null) {
					BukkitPlayer bPLoser = (BukkitPlayer) WeavenMC.getAccountCommon()
							.getWeavenPlayer(logout.getUniqueId());
					BukkitPlayer bPWinner = (BukkitPlayer) WeavenMC.getAccountCommon()
							.getWeavenPlayer(winner.getUniqueId());
					int deaths = bPLoser.getData(DataType.PVP_DEATHS).asInt();
					bPLoser.getData(DataType.PVP_DEATHS).setValue(deaths += 1);

					winner.sendMessage("§c" + logout.getName() + " deslogou.");
					checkLostKs(logout, winner, bPLoser.getData(DataType.PVP_KILLSTREAK).asInt());
					bPLoser.getData(DataType.PVP_KILLSTREAK).setValue(0);
					int streak = bPWinner.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
					bPWinner.getData(DataType.PVP_KILLSTREAK).setValue(streak);
					int maxStreak = bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
					if (streak > maxStreak)
						bPWinner.getData(DataType.PVP_GREATER_KILLSTREAK).setValue(streak);
					int xp = calculateXp(bPWinner, bPLoser);
					bPWinner.addXp(xp);
					bPWinner.addMoney(80);
					winner.sendMessage("§e§lKILL§f Você matou §e§l" + logout.getName());
					winner.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
					winner.sendMessage("§9§lXP§f Você recebeu §9§l" + xp + " XPs"
							+ (bPWinner.isDoubleXPActived() ? " §7(doublexp)" : ""));
					int kills = bPWinner.getData(DataType.PVP_KILLS).asInt();
					bPWinner.getData(DataType.PVP_KILLS).setValue(kills += 1);
					checkKs(winner, streak);
					bPLoser.save(DataCategory.BALANCE, DataCategory.KITPVP);
					bPWinner.save(DataCategory.BALANCE, DataCategory.KITPVP);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventory(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();
			if (AdminMode.getInstance().isAdmin(player)) {
				player.openInventory(((Player) event.getRightClicked()).getInventory());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void combat(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			if (!event.isCancelled()) {
				Player damaged = (Player) event.getEntity();
				Player damager = (Player) event.getDamager();
				Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(damaged.getUniqueId());
				if (gamer.getWarp().getName().equalsIgnoreCase("spawn")
						|| gamer.getWarp().getName().equalsIgnoreCase("fps")) {
					gamer.addCombat(damager.getUniqueId(), 9);
					BossBarAPI.setBar(damager, damaged.getName() + " - " + gamer.getAbility().getName(), 5);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectile(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			event.getEntity().remove();
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.DISPENSE_EGG && event.getSpawnReason() != SpawnReason.CUSTOM
				&& event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void explode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickUp(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (AdminMode.getInstance().isAdmin(p)) {
			event.setCancelled(true);
			return;
		}
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Material material = event.getItem().getItemStack().getType();
		if (gamer.getWarp().getName().equalsIgnoreCase("1v1")) {
			event.setCancelled(true);
		} else if (!material.toString().contains("MUSHROOM") && material != Material.BOWL
				&& material != Material.ENDER_PEARL && material != Material.EXP_BOTTLE
				&& material != Material.GOLDEN_APPLE && material != Material.CACTUS && material != Material.COCOA
				&& material != Material.GLASS_BOTTLE) {
			event.setCancelled(true);
		}
		material = null;
		gamer = null;
		p = null;
	}

	public void repair(Player player) {
		for (ItemStack armour : player.getInventory().getArmorContents()) {
			if (armour == null)
				continue;
			armour.setDurability((byte) 0);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpongeJump(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Block block = event.getTo().getBlock();
		Location loc = block.getLocation();
		loc.setY(loc.getY() - 1.0);
		Block block2 = loc.getBlock();
		if (block2.getType() == Material.SPONGE) {
			p.setFallDistance(-50.0f);
			p.setVelocity(new Vector(0, 5, 0));
			p.setFallDistance(-50.0f);
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		}
		loc = null;
		block = null;
		block2 = null;
		p = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onIgnite(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.LIGHTNING) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSpawnEntity(EntitySpawnEvent event) {
		if (event.getEntity().getType() == EntityType.VILLAGER)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawnListener(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		TeleportManager.getInstance().allowJoin(p);

		p.setFireTicks(0);
		p.setHealth(p.getMaxHealth());

		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Minigame minigame = gamer.getWarp();
		minigame.join(p);
		p.sendMessage("§6§lRESPAWN§f Você morreu e resnaceu na Warp §e" + minigame.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStopDeath(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.isCancelled())
				return;

			Player p = (Player) event.getEntity();
			EntityPlayer handle = ((CraftPlayer) p).getHandle();

			if (p.getHealth() - event.getFinalDamage() <= 0) {
				event.setCancelled(true);
				p.setHealth(20.0D);
				// WeavenMC.debug(event.getCause().toString());

				Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
				UUID lastCombatUUID = gamer.getLastCombat();
				Player killer = null;

				if (lastCombatUUID != null && (killer = Bukkit.getPlayer(lastCombatUUID)) != null) {
					EntityPlayer entityhuman = ((CraftPlayer) killer).getHandle();
					handle.killer = entityhuman;
				} else {
					handle.killer = null;
				}

				List<ItemStack> items = new ArrayList<>();

				for (ItemStack content : p.getInventory().getContents()) {
					if (content == null)
						continue;
					if (content.getType() == Material.AIR)
						continue;
					items.add(content);
				}

				for (ItemStack content : p.getInventory().getArmorContents()) {
					if (content == null)
						continue;
					if (content.getType() == Material.AIR)
						continue;
					items.add(content);
				}

				Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p, items, 0, 0, null));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathListener(PlayerDeathEvent event) {
		Player p = event.getEntity();
		
		BukkitPlayer player = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		
		if (p.getKiller() != null && p.getKiller() instanceof Player) {
			Player killer = p.getKiller();
			repair(killer);
			event.getDrops().stream().forEach(drop -> killer.getWorld().dropItem(p.getLocation(), drop));
			event.getDrops().clear();
			checkLostKs(p, killer, player.getData(DataType.PVP_KILLSTREAK).asInt());
			
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(killer.getUniqueId());
			int xp = calculateXp(bP, player);
			bP.addXp(xp);
			bP.addMoney(80);
			int kills = bP.getData(DataType.PVP_KILLS).asInt();
			bP.getData(DataType.PVP_KILLS).setValue(kills += 1);
			int killStreak = bP.getData(DataType.PVP_KILLSTREAK).asInt() + 1;
			int maxStreak = bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
			if (killStreak > maxStreak)
				bP.getData(DataType.PVP_GREATER_KILLSTREAK).asInt();
			bP.getData(DataType.PVP_KILLSTREAK).setValue(killStreak);
			killer.sendMessage("§e§lKILL§f Você matou §e§l" + p.getName());
			killer.sendMessage("§6§lMONEY§f Você recebeu §6§l80 MOEDAS");
			killer.sendMessage(
					"§9§lXP§f Você recebeu §9§l" + xp + " XPs" + (bP.isDoubleXPActived() ? " §7(doublexp)" : ""));
			int deaths = player.getData(DataType.PVP_DEATHS).asInt();
			player.getData(DataType.PVP_DEATHS).setValue(deaths += 1);
			player.getData(DataType.PVP_KILLSTREAK).setValue(0);
			player.removeMoney(1);
			p.sendMessage("§c§lMORTE§f Você morreu para §e§l" + killer.getName());
			p.sendMessage("§4§lMONEY§f Você perdeu §4§l1 MOEDA");
			checkKs(killer, killStreak);
			player.save(DataCategory.KITPVP, DataCategory.BALANCE);
			bP.save(DataCategory.BALANCE, DataCategory.KITPVP);
		} else {
			event.getDrops().clear();
			p.sendMessage("§c§lMORTE§f Você morreu");
			player.getData(DataType.PVP_KILLSTREAK).setValue(0);
			player.save(DataCategory.KITPVP);
		}
		Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(p, p.getLocation(), false));
	}

	protected void forceRespawn(Player player) {
		EntityPlayer handle = ((CraftPlayer) player).getHandle();
		handle.u().getTracker().untrackEntity(handle);

		((CraftServer) Bukkit.getServer()).getServer().getPlayerList().moveToWorld(handle, 0, false);
	}

	public void checkKs(Player p, int ks) {
		if (ks < 10)
			return;
		if (String.valueOf(ks).endsWith("0") || String.valueOf(ks).endsWith("5")) {
			Bukkit.broadcastMessage("§4§lKILLSTREAK §1§l" + p.getName() + " §fconseguiu um §6§lKILLSTREAK DE " + ks);
		}
	}

	public void checkLostKs(Player p, Player k, int ks) {
		if (ks < 10)
			return;
		Bukkit.broadcastMessage("§4§lKILLSTREAK §1§l" + p.getName() + "§f perdeu seu §6§lKILLSTREAK DE " + ks
				+ " PARA §c§l" + k.getName());
	}

	public int calculateXp(WeavenPlayer receiver, WeavenPlayer wP) {
		double result = 5;
		// pvp calculating
		int kills = wP.getData(DataType.PVP_KILLS).asInt();
		int deaths = wP.getData(DataType.PVP_DEATHS).asInt();
		if (kills != 0 && deaths != 0)
			result += Double.valueOf(kills / deaths);
		int battleWins = wP.getData(DataType.PVP_1V1_KILLS).asInt();
		int battleLoses = wP.getData(DataType.PVP_1V1_DEATHS).asInt();
		if (battleWins != 0 && battleLoses != 0)
			result += battleWins / battleLoses;
		// league calculating
		result += Double.valueOf(wP.getLeague().ordinal() / 2);
		// hg calculating
		int hgWins = wP.getData(DataType.HG_WINS).asInt();
		int hgDeaths = wP.getData(DataType.HG_DEATHS).asInt();
		if (hgWins != 0 && hgDeaths != 0)
			result += hgWins / hgDeaths;
		int gladWins = wP.getData(DataType.GLADIATOR_WINS).asInt();
		int gladDeaths = wP.getData(DataType.GLADIATOR_LOSES).asInt();
		if (gladWins != 0 && gladDeaths != 0)
			result += gladWins / gladDeaths;
		if ((int) result <= 0)
			result = 5;
		if (receiver.isDoubleXPActived())
			result = result * 2;
		return (int) result;
	}

	@EventHandler
	public void onBucket(PlayerBucketEmptyEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
		if (!bP.hasGroupPermission(Group.ADMIN)) {
			Material bucket = event.getBucket();
			if (bucket.toString().contains("LAVA")) {
				event.setCancelled(true);
			} else if (bucket.toString().contains("WATER")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRepair(PlayerInteractEvent event) {
		Player p = (Player) event.getPlayer();
		ItemStack itemStack = p.getItemInHand();
		if (itemStack != null) {
			Action action = event.getAction();
			if (action.name().contains("LEFT")) {
				if (itemStack.getType() == Material.DIAMOND_SWORD || itemStack.getType() == Material.STONE_SWORD
						|| itemStack.getType() == Material.WOOD_SWORD || itemStack.getType() == Material.STONE_SWORD
						|| itemStack.getType() == Material.IRON_SWORD || itemStack.getType() == Material.GOLD_SWORD
						|| itemStack.getType() == Material.DIAMOND_AXE || itemStack.getType() == Material.GOLD_AXE
						|| itemStack.getType() == Material.STONE_AXE || itemStack.getType() == Material.WOOD_AXE
						|| itemStack.getType() == Material.IRON_AXE || itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
					p.updateInventory();
				}
			} else {
				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
					p.updateInventory();
				}
			}
			action = null;
			itemStack = null;
		}
		p = null;
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getWarp().getName().equals("1v1")) {
			if (event.getItemDrop().getItemStack().getType() == Material.BOWL) {
				event.getItemDrop().remove();
			} else {
				event.setCancelled(true);
			}
			
		} else {
			Material material = event.getItemDrop().getItemStack().getType();
			if (!material.toString().contains("MUSHROOM") && material != Material.CACTUS && material != Material.BOWL
					&& material != Material.ENDER_PEARL && material != Material.EXP_BOTTLE
					&& material != Material.GOLDEN_APPLE && material != Material.GLASS_BOTTLE
					&& !material.toString().contains("_BOOTS") && !material.toString().contains("_LEGGINGS")
					&& !material.toString().contains("_CHESTPLATE") && !material.toString().contains("_HELMET")) {
				event.setCancelled(true);
			}
			material = null;
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (p.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRegain(EntityRegainHealthEvent event) {
		if (event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCompass(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		if (!gamer.getAbility().getName().equals("Nenhum")) {
			Material material = p.getItemInHand().getType();
			if (material == null || material != Material.COMPASS) {
				return;
			}
			event.setCancelled(true);
			Player target = null;
			double distance = 500;
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (AdminMode.getInstance().isAdmin(players))
					continue;
				double distanceToVictim = p.getLocation().distance(players.getLocation());
				if (distanceToVictim < distance && distanceToVictim > 10) {
					distance = distanceToVictim;
					target = players;
				}
			}
			if (target == null) {
				p.sendMessage("§6§lBUSSOLA§f Nenhum player foi encontrado");
				p.setCompassTarget(p.getWorld().getSpawnLocation());
			} else {
				p.setCompassTarget(target.getLocation());
				p.sendMessage("§6§lBUSSOLA§f Apontando para §e§l" + target.getName());
				target = null;
			}
			material = null;
		}
		gamer = null;
		p = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSoup(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Material material = p.getItemInHand().getType();
		if (material == null || material != Material.MUSHROOM_SOUP) {
			return;
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (p.getHealth() < (p).getMaxHealth()) {
				int restores = 7;
				event.setCancelled(true);
				if (p.getHealth() + restores <= p.getMaxHealth())
					p.setHealth(p.getHealth() + restores);
				else
					p.setHealth(p.getMaxHealth());
				p.setItemInHand(new ItemBuilder().type(Material.BOWL).build());
				if (gamer.getAbility().getName().toLowerCase().equals("quickdrop")) {
					p.setItemInHand(new ItemBuilder().type(Material.AIR).build());
				}
			}
		}
		material = null;
		p = null;
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		Item localItem = event.getEntity();
		Bukkit.getScheduler().scheduleSyncDelayedTask(yPvP.getPlugin(), () -> localItem.remove(), 10 * 20);
	}
}
