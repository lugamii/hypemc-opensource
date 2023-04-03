package br.com.adlerlopes.bypiramid.hungergames.player.gamer.listener;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.game.handler.item.CacheItems;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.permission.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GamerSpectateListener extends HungerListener {
	private static final ArrayList<UUID> cantTouch = new ArrayList<>();

	@EventHandler
	public void onRight(PlayerInteractEntityEvent event) {
		if (((event.getRightClicked() instanceof Vehicle))
				&& (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue())) {
			event.setCancelled(true);
		} 
	}


	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if (cantTouch.contains(event.getWhoClicked().getUniqueId())) {
			BukkitPlayer bP = BukkitPlayer.getPlayer(event.getWhoClicked().getUniqueId());
			if (bP == null)
				return;
			if (bP.isStaffer()) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventory(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("Jogadores")) {
			event.setCancelled(true);

			Player p = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if ((item != null) && (item.getType() == Material.SKULL_ITEM) && (item.getDurability() == 3)
					&& (item.hasItemMeta()) && (item.getItemMeta().hasDisplayName())) {
				String display = item.getItemMeta().getDisplayName();
				Player player = null;
				for (Gamer g : getManager().getGamerManager().getGamers().values()) {
					if (g.isAlive().booleanValue()) {
						Player o = g.getPlayer();
						if (ChatColor.stripColor(o.getName()).equalsIgnoreCase(ChatColor.stripColor(display))) {
							player = o;
							break;
						}
					}
				}
				p.closeInventory();
				if (player == null) {
					p.sendMessage("§cEste jogador morreu ou saiu do servidor!");
				} else {
					p.teleport(player.getLocation());
					p.sendMessage("§aTeleportado para §f" + display);
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (cantTouch.contains(event.getPlayer().getUniqueId())) {
			cantTouch.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		if (getManager().getGamerManager().getGamer(player).isSpectating().booleanValue()) {
			if (bP.isStaffer()) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (getManager().getGamerManager().getGamer(event.getEntity().getUniqueId()) != null) && (getManager()
						.getGamerManager().getGamer(event.getEntity().getUniqueId()).isSpectating().booleanValue())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (((event.getEntity() instanceof Player)) && (getManager().getGamerManager()
				.getGamer((Player) event.getEntity()).isSpectating().booleanValue())) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if ((event.getTarget() instanceof Player)) {
			Player p = (Player) event.getTarget();
			BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
			if (bP.isStaffer()) {
				return;
			}
			if (getManager().getGamerManager().getGamer(p).isSpectating().booleanValue()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue())) {
			Block b = event.getClickedBlock();
			BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
			if ((((b.getState() instanceof DoubleChest)) || ((b.getState() instanceof Chest))
					|| ((b.getState() instanceof Hopper)) || ((b.getState() instanceof Dispenser))
					|| ((b.getState() instanceof Furnace)) || ((b.getState() instanceof Beacon)))
					&& (!bP.isStaffer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			Player player = (Player) event.getDamager();
			BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
			if (bP.getGroup().getId() >= Group.YOUTUBERPLUS.getId()) {
				return;
			}
			if (getManager().getGamerManager().getGamer(player).isSpectating().booleanValue()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (bP.isStaffer()) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (bP.isStaffer()) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (bP.isStaffer()) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue()) {
			event.setCancelled(true);
		}
	}

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((getManager().getGamerManager().getGamer(event.getPlayer()) != null)
				&& (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating().booleanValue())
				&& (event.getAction().name().contains("RIGHT"))) {
			Gamer g;
			if (itemBuilder.checkItem(event.getItem(),
					CacheItems.SPEC.getItem(0).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				Inventory inventory = Bukkit.createInventory(event.getPlayer(), 54, "Jogadores");

				int slot = 0;
				for (@SuppressWarnings("rawtypes")
				Iterator localIterator = getManager().getGamerManager().getAliveGamers().iterator(); localIterator
						.hasNext();) {
					g = (Gamer) localIterator.next();
					if ((g.getPlayer().isOnline()) && (slot <= 50)) {
						inventory.setItem(slot++, new ItemBuilder().setType(Material.SKULL_ITEM)
								.setName("§b" + g.getPlayer().getName())
								.setDescription(Arrays.asList(new String[] {
										"§aKit: §f" + g.getKit().getName() + ", §aKit2: §f" + g.getKit2().getName(),
										"§aKills: §f" + g.getPlayer().getStatistic(Statistic.PLAYER_KILLS), " ",
										"§eClique para teleportar-se!" }))
								.setDurability(3).getStack());
					}
				}
				event.getPlayer().openInventory(inventory);
			} else if (itemBuilder.checkItem(event.getItem(),
					CacheItems.SPEC.getItem(1).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				List<Gamer> pvp = new ArrayList<>();
				for (Gamer gamer : getManager().getGamerManager().getAliveGamers()) {
					if ((gamer.isFighting().booleanValue()) && (gamer.isAlive().booleanValue())) {
						pvp.add(gamer);
					}
				}
				if (pvp.size() == 0) {
					event.getPlayer().sendMessage("§cNenhum player está em combate.");
					return;
				}
				Gamer random = (Gamer) pvp.get(getManager().getRandom().nextInt(pvp.size()));
				event.getPlayer().teleport(random.getPlayer().getLocation());
				event.getPlayer().sendMessage("§aTeleportado para §f" + random.getPlayer().getName());
				return;
			}
		}
	}
}
