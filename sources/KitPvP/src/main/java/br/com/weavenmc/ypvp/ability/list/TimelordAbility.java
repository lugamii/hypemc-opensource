package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.weavenmc.ypvp.gamer.Gamer;

public class TimelordAbility {

	private HashMap<UUID, Long> frozenTime = new HashMap<>();

	public TimelordAbility() {
		//setName("Timelord");
		//setHasItem(true);
		//setGroupToUse(Group.LIGHT);
		//setIcon(Material.WATCH);
		//setDescription(new String[] { "§7Faça todos os seus oponentes proximos", "§7de você congelarem." });
		//setPrice(75000);
		//setTempPrice(6500);
	}

	//@Override
	public void eject(Player p) {
		frozenTime.remove(p.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMoveTimelord(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (frozenTime.containsKey(p.getUniqueId()) && frozenTime.get(p.getUniqueId()) >= System.currentTimeMillis()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onTimeLord(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (true) {
			if (false) {
				event.setCancelled(true);
				if (false) {
					List<Entity> entities = p.getNearbyEntities(6.0D, 6.0D, 6.0D);
					if (entities.size() > 0) {
						//addCooldown(p, 35);

						for (Entity e : entities) {
							if (!(e instanceof Player))
								continue;

							Player target = (Player) e;
							Gamer gamer = new Gamer(null);
							
							if (gamer.getWarp().isProtected(target))
								continue;
							
							frozenTime.put(target.getUniqueId(), (11 * 1000L + System.currentTimeMillis()));
							target.sendMessage("§5§lTIMELORD§f Você foi §9§lCONGELADO§f pelo §9§l" + p.getName());
						}

						p.sendMessage("§5§lTIMELORD§f Você §9§lCONGELOU§f ao todo §9§l" + entities.size()
								+ " JOGADORES PROXIMOS");
					} else {
						p.sendMessage("§5§lTIMELORD§f Não há nenhum inimigo proximo (6 blocos)");
					}
				} else {
					//sendCooldown(p);
				}
			}
		}
	}
}
