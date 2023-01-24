package br.com.weavenmc.commons.bukkit.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.event.PlayerMoveBlockEvent;
import br.com.weavenmc.commons.bukkit.event.PlayerMoveUpdateEvent;
import br.com.weavenmc.commons.bukkit.event.account.PlayerChangeGroupEvent;
import br.com.weavenmc.commons.bukkit.event.account.PlayerChangeLeagueEvent;
import br.com.weavenmc.commons.bukkit.event.redis.BukkitRedisMessageEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.bukkit.protocol.ProtocolGetter;
import br.com.weavenmc.commons.bukkit.protocol.ProtocolVersion;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.permission.GroupMessage;
import br.com.weavenmc.commons.util.string.StringTimeUtils;

public class PlayerListener implements Listener {

	protected final Map<UUID, Location> locations = new HashMap<>();
	private ItemStack lapis;

	public PlayerListener() {
		Dye d = new Dye();
		d.setColor(DyeColor.BLUE);
		this.lapis = d.toItemStack();
		this.lapis.setAmount(64);
	}

	/**@EventHandler(priority = EventPriority.MONITOR)
	public void onCommandExecute(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		if (bP.getGroup().getId() >= Group.TRIAL.getId()) {
			for (WeavenPlayer owners : WeavenMC.getAccountCommon().getPlayers()) {
				if (!owners.hasGroupPermission(Group.DONO))
					continue;
				Player owner = Bukkit.getPlayer(owners.getUniqueId());
				if (owner == null)
					continue;
//				owner.sendMessage("§7§o[" + bP.getGroup().name() + "] " + player.getName() + " executou o comando: "
//						+ event.getMessage());
			}
		}
	}*/

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player && event.getInventory() instanceof EnchantingInventory) {
			ProtocolVersion ver = ProtocolGetter.getVersion(((Player) event.getPlayer()));
			if (ver.getId() >= 47 && event.getInventory().getItem(1) != null) {
				event.getInventory().setItem(1, null);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player && event.getInventory() instanceof EnchantingInventory) {
			ProtocolVersion ver = ProtocolGetter.getVersion(((Player) event.getWhoClicked()));
			if (ver.getId() >= 47 && event.getSlot() == 1 && event.getInventory().getItem(1) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void openInventoryEvent(InventoryClickEvent e) {
		if (e.getInventory().getName().toLowerCase().startsWith("§7conta - ")) {
			Player player = (Player) e.getWhoClicked();
			if (e.getCurrentItem().getType() == Material.BOOK) {
				player.closeInventory();
				player.chat("/medal");
			}
		}
	}

	@EventHandler
	public void openInventoryEvent(InventoryOpenEvent e) {
		if (e.getPlayer() instanceof Player && e.getInventory() instanceof EnchantingInventory) {
			ProtocolVersion ver = ProtocolGetter.getVersion(((Player) e.getPlayer()));
			if (ver.getId() >= 47) {
				e.getInventory().setItem(1, this.lapis);
			}
		}
	}

	// @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerMove(UpdateEvent event) {
		if (event.getType() != UpdateType.TICK)
			return;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (locations.containsKey(p.getUniqueId())) {
				Location to = p.getLocation();
				Location from = locations.get(p.getUniqueId());
				if ((to.getX() != from.getX()) && (to.getY() != from.getY()) && (to.getZ() != from.getZ())) {
					PlayerMoveUpdateEvent moveUpdateEvent = new PlayerMoveUpdateEvent(p, from, to);
					Bukkit.getServer().getPluginManager().callEvent(moveUpdateEvent);
					if (moveUpdateEvent.isCancelled())
						p.teleport(from);
				}
				if ((to.getBlockX() != from.getBlockX()) && (to.getBlockY() != from.getBlockY())
						&& (to.getBlockZ() != from.getBlockZ())) {
					PlayerMoveBlockEvent moveBlockEvent = new PlayerMoveBlockEvent(p, from, to);
					Bukkit.getServer().getPluginManager().callEvent(moveBlockEvent);
					if (moveBlockEvent.isCancelled())
						p.teleport(from);
				}
			}
			locations.put(p.getUniqueId(), p.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoinListener(PlayerJoinEvent event) {
		BukkitPlayer bP = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
		if (bP == null) {
			event.getPlayer().kickPlayer("§4§lERRO§f Ocorreu um erro ao tentar carregar sua conta!");
			return;
		}
		VanishAPI.getInstance().updateVanishToPlayer(event.getPlayer());
		if (bP.hasGroupPermission(Group.TRIAL) && !AdminMode.getInstance().isAdmin(event.getPlayer())) {
			AdminMode.getInstance().setAdmin(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (AdminMode.getInstance().isAdmin(player)) {
			AdminMode.getInstance().setPlayer(player);
		}
	}

	@EventHandler
	public void onChangeGroup(PlayerChangeGroupEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
		Group serverGroup = event.getGroup();
		Long groupTime = event.getGroupTime();
		TitleAPI.setTitle(p, "§6§lGRUPO", "§fVocê recebeu o grupo " + serverGroup.getTagToUse().getName());
		player.updateTags();
		player.organizeGroups();
		if (groupTime == -1)
			p.sendMessage("§6§lGRUPO§f Você recebeu o grupo " + serverGroup.getTagToUse().getName()
					+ "§f com duração §e§lETERNA");
		else
			p.sendMessage("§6§lGRUPO§f Você recebeu o grupo " + serverGroup.getTagToUse().getName()
					+ "§f com duração de §e§l" + StringTimeUtils.formatDifference(groupTime));
		if (!BukkitMain.getInstance().getTagManager().currentTag(p, serverGroup.getTagToUse()))
			BukkitMain.getInstance().getTagManager().setTag(p, serverGroup.getTagToUse());
		if (player.getGroup() == Group.DONO) {
			if (!p.isOp()) {
				p.setOp(true);
			}
		} else {
			if (p.isOp()) {
				p.setOp(false);
			}
		}
		groupTime = null;
		serverGroup = null;
		player = null;
		p = null;
	}

	@EventHandler
	public void onGroupMessage(BukkitRedisMessageEvent event) {
		if (!event.getChannel().equals(WeavenMC.GROUP_MANAGEMENT_CHANNEL))
			return;
		GroupMessage msg = WeavenMC.getGson().fromJson(event.getMessage(), GroupMessage.class);
		if (msg.isFindPlayer()) {
			if (msg.isAdd()) {
				UUID uuid = null;
				for (WeavenPlayer wP : WeavenMC.getAccountCommon().getPlayers()) {
					BukkitPlayer bP = (BukkitPlayer) wP;
					if (!bP.getName().equalsIgnoreCase(msg.getUserName()))
						continue;
					uuid = bP.getUniqueId();
					break;
				}
				if (uuid == null) {
					for (Player o : Bukkit.getOnlinePlayers()) {
						if (!o.getName().equalsIgnoreCase(msg.getUserName()))
							continue;
						uuid = o.getUniqueId();
						break;
					}
				}
				if (uuid != null) {
					Player receiver = Bukkit.getPlayer(uuid);
					if (receiver != null) {
						BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon()
								.getWeavenPlayer(receiver.getUniqueId());
						bP.addGroup(msg.readGroup(), msg.getGroupTime());
						Bukkit.getPluginManager()
								.callEvent(new PlayerChangeGroupEvent(receiver, msg.readGroup(), msg.getGroupTime()));
						bP = null;
					}
					receiver = null;
					uuid = null;
				}
			} else {
				UUID uuid = null;
				for (WeavenPlayer wP : WeavenMC.getAccountCommon().getPlayers()) {
					BukkitPlayer bP = (BukkitPlayer) wP;
					if (!bP.getName().equalsIgnoreCase(msg.getUserName()))
						continue;
					uuid = bP.getUniqueId();
					break;
				}
				if (uuid == null) {
					for (Player o : Bukkit.getOnlinePlayers()) {
						if (!o.getName().equalsIgnoreCase(msg.getUserName()))
							continue;
						uuid = o.getUniqueId();
						break;
					}
				}
				if (uuid != null) {
					Player receiver = Bukkit.getPlayer(uuid);
					if (receiver != null) {
						BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon()
								.getWeavenPlayer(receiver.getUniqueId());
						HashMap<Group, Long> map = bP.getGroupById(msg.getGroupId());
						if (map != null && map.keySet().toArray(new Group[] {})[0] == msg.readGroup()) {
							bP.removeGroup(msg.getGroupId());
							bP.organizeGroups();
							bP.updateTags();
							if (BukkitMain.getInstance().getTagManager().currentTag(receiver,
									msg.readGroup().getTagToUse())) {
								BukkitMain.getInstance().getTagManager().setTag(receiver, bP.getGroup().getTagToUse());
							}
							receiver.sendMessage("§6§lGRUPO§f O grupo " + msg.readGroup().getTagToUse().getName()
									+ "§f foi §c§lREMOVIDO§f de sua conta!");
							if (bP.getGroup() != Group.DONO) {
								if (receiver.isOp()) {
									receiver.setOp(false);
								}
							}
						} else {
							receiver.kickPlayer("§4§lERRO§f Ocorreu um erro ao tentar atualizar dados em sua conta!");
						}
						bP = null;
						receiver = null;
					}
					uuid = null;
				}
			}
		}
		msg = null;
	}

	@EventHandler
	public void onLeagueUpdate(PlayerChangeLeagueEvent event) {
		Player player = event.getPlayer();
		League league = event.getFutureLeague();
		String format = league.getColor() + league.getSymbol() + " " + league.toString();
		Bukkit.broadcastMessage(
				"§6§lLIGA§f O player §6" + player.getName() + "§f acabou de §9§lUPAR§f sua §6§lLIGA§f para " + format);
		TitleAPI.setTitle(player, "§6§lLIGA", "§fVocê upou para " + format);
		player.sendMessage("§6§lLIGA§f Parabéns, você §9§lUPOU§f sua §6§lLIGA!§f Agora você é um " + format);
		Bukkit.getScheduler().runTaskLater(BukkitMain.getInstance(),
				() -> BukkitMain.getInstance().getTagManager().update(player), 5L);
	}

	@EventHandler
	public void onWeatherChangeListener(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;
		for (Player o : Bukkit.getOnlinePlayers()) {
			BukkitPlayer player = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(o.getUniqueId());
			if (player == null)
				continue;
			List<Integer> expiredIds = player.getExpiredGroupsIds();
			if (expiredIds == null)
				continue;
			List<Group> oldRanks = new ArrayList<>();
			for (Integer id : expiredIds) {
				String expiredFormat = player.getData(DataType.GROUPS).asList().get(id);
				Group serverGroup = Group.fromString(expiredFormat.split(";")[0]);
				if (serverGroup != null) {
					oldRanks.add(serverGroup);
					o.sendMessage("§6§lRANK§f O rank " + serverGroup.getTagToUse().getName()
							+ "§f de sua conta §e§lEXPIROU§f! Adquira-o novamente em nossa §6§lLOJA§f! §6§lhttp://loja.hypemc.com.br");
				}
				player.removeGroup(id);
			}
			// update permissions
			player.organizeGroups();
			player.save(DataCategory.ACCOUNT);
			player.updateTags();
			for (Group old : oldRanks) {
				if (BukkitMain.getInstance().getTagManager().currentTag(o, old.getTagToUse())) {
					BukkitMain.getInstance().getTagManager().setTag(o, player.getTags().get(0));
					break;
				}
			}
			oldRanks = null;
			expiredIds = null;
			player = null;
		}
	}
}
