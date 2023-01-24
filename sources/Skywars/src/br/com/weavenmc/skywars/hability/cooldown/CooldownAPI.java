package br.com.weavenmc.skywars.hability.cooldown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.actionbar.BarAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;

public class CooldownAPI implements Listener {

	public static final char CHAR = '|';
	public static final Map<UUID, List<Cooldown>> map = new ConcurrentHashMap<>();

	public static void addCooldown(Player player, Cooldown cooldown) {
		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());
			list.add(cooldown);
		}
	}

	public static boolean removeCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			Iterator<Cooldown> it = list.iterator();
			while (it.hasNext()) {
				Cooldown cooldown = it.next();
				if (cooldown.getName().equals(name)) {
					it.remove();
					return true;
				}
			}
			player.sendMessage("§eAgora pode usar o §b" + name + "§e novamente.");
		}
		return false;
	}

	public static boolean hasCooldown(Player player, String name) {
		if (map.containsKey(player.getUniqueId())) {
			List<Cooldown> list = map.get(player.getUniqueId());
			for (Cooldown cooldown : list)
				if (cooldown.getName().equals(name))
					return true;
		}
		return false;
	}

	public static String getMessage(Player player) {
		Cooldown cooldown = getCooldown(player);
		return "§eCooldown de §b" + cooldown.getName() + "§e irá acabar em §c"
				+ String.format(Locale.US, "%.1fs", cooldown.getRemaining());
	}

	@EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK)
            return;
        if (event.getCurrentTick() % 2 > 0)
            return;
        for (UUID uuid : map.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                List<Cooldown> list = map.get(uuid);
                Iterator<Cooldown> it = list.iterator();

                /* Found Cooldown */
                Cooldown found = null;
                while (it.hasNext()) {
                    Cooldown cooldown = it.next();
                    if (!cooldown.expired()) {
                        if (cooldown instanceof ItemCooldown) {
                            ItemStack hand = player.getItemInHand();
                            if (hand != null && hand.getType() != Material.AIR) {
                                ItemCooldown item = (ItemCooldown) cooldown;
                                if (hand.equals(item.getItem())) {
                                    item.setSelected(true);
                                    found = item;
                                    break;
                                }
                            }
                            continue;
                        }
                        found = cooldown;
                        continue;
                    } else {
                    	 player.sendMessage("§a§lCOOLDOWN §fAgora pode usar o " + cooldown.getName() + " novamente.");
                    }
                    it.remove();
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                    CooldownFinshEvent e = new CooldownFinshEvent(player, cooldown);
                    Bukkit.getServer().getPluginManager().callEvent(e);
                }

                /* Display Cooldown */
                if (found != null) {
                    display(player, found);
                } else if (list.isEmpty()) {
                   BarAPI.send(player, " ");
                   map.remove(uuid);
                } else {
                    Cooldown cooldown = list.get(0);
                    if (cooldown instanceof ItemCooldown) {
                        ItemCooldown item = (ItemCooldown) cooldown;
                        if (item.isSelected()) {
                            item.setSelected(false);
                            BarAPI.send(player, " ");
                        }
                    }
                }
            }
        }
    }

	public static Cooldown getCooldown(Player player) {
		return map.get(player.getUniqueId()).get(0);
	}

	public static void display(Player player, Cooldown cooldown) {
		StringBuilder bar = new StringBuilder();
		double percentage = cooldown.getPercentage();
		double remaining = cooldown.getRemaining();
		double count = 20 - Math.max(percentage > 0D ? 1 : 0, percentage / 5);
		for (int a = 0; a < count; a++)
			bar.append("§a" + CHAR);
		for (int a = 0; a < 20 - count; a++)
			bar.append("§c" + CHAR);
		String name = cooldown.getName();
		BarAPI.send(player, name + " " + bar.toString() + "§f " + String.format(Locale.US, "%.1f", remaining) + "§f "
				+ (remaining > 1 ? "segundos" : "segundo"));
	}

}
