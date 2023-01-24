package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class AjninAbility extends Ability {

	private final HashMap<UUID, Player> ajnin = new HashMap<>();

	public AjninAbility() {
		setName("Ajnin");
		setHasItem(false);
		setGroupToUse(Group.PRO);
		setIcon(Material.NETHER_STAR);
		setDescription(new String[] { "§7Ao hitar seu oponente agache-se e", "§7teleporte ele para você." });
		setPrice(70000);
		setTempPrice(6000);
	}

	@Override
	public void eject(Player p) {
		if (ajnin.containsKey(p.getUniqueId())) 
			ajnin.remove(p.getUniqueId());
		for (UUID uuid : ajnin.keySet()) {
			if (!ajnin.get(uuid).equals(p))
				continue;
			ajnin.remove(uuid);
			break;
		}
	}

	@EventHandler
	public void onAjnin(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player e = (Player) event.getEntity();
			Player d = (Player) event.getDamager();
			if (hasKit(d)) {
				if (!event.isCancelled()) {
					ajnin.put(d.getUniqueId(), e);
				}
			}
			e = null;
			d = null;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSneak(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		if (!event.isSneaking()) {
			if (hasKit(p)) {
				if (ajnin.containsKey(p.getUniqueId())) {
					Player t = ajnin.get(p.getUniqueId());
					if (t != null && t.isOnline()) {
						Gamer gamer = gamer(t);
						if (!gamer.getWarp().isProtected(t)) {
							if (!inCooldown(p)) {
								if (p.getLocation().distance(t.getLocation()) <= 70.0D) {
									addCooldown(p, 14);
									t.teleport(p);
								} else {
									p.sendMessage("§5§lAJNIN§f O último jogador está §9§lMUITO LONGE");
								}
							} else {
								sendCooldown(p);
							}
						} else {
							p.sendMessage("§5§lAJNIN§f O último jogador está no §9§lSPAWN");
						}
						gamer = null;
						t = null;
					} else {
						p.sendMessage("§5§lAJNIN§f O último jogador está §9§lOFFLINE");
					}
				} else {
					p.sendMessage("§5§lAJNIN§f Você ainda não §9§lHITOU§f ninguém!");
				}
			}
		}
		p = null;
	}
}
