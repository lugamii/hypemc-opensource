package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class NinjaAbility extends Ability {

	private HashMap<UUID, UUID> ninjaMap = new HashMap<>();

	public NinjaAbility() {
		setName("Ninja");
		setHasItem(false);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.EMERALD);
		setDescription(new String[] { "§7Ao hitar seu oponente agache-se e", "§7teleporte-se para ele." });
		setPrice(70000);
		setTempPrice(0);
	}

	@Override
	public void eject(Player p) {
		if (ninjaMap.containsKey(p.getUniqueId())) {
			ninjaMap.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onEntityDamageListener(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (!hasKit(player)) {
				return;
			}
			if (event.isCancelled())
				return;
			ninjaMap.put(player.getUniqueId(), ((Player) event.getEntity()).getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		if (event.isSneaking()) {
			Player p = event.getPlayer();
			if (!hasKit(p))
				return;
			if (!ninjaMap.containsKey(p.getUniqueId())) {
				p.sendMessage("§5§lNINJA§f Você ainda não §9§lHITOU§f ninguém");
				return;
			}
			Player last = Bukkit.getPlayer(ninjaMap.get(p.getUniqueId()));
			if (last == null) {
				p.sendMessage("§5§lNINJA§f O player não está §9§lONLINE!");
				return;
			}
			Gamer gamer = gamer(last);
			if (gamer.getWarp().isProtected(last)) {
				p.sendMessage("§5§lNINJA§f O player está no §9§lSPAWN!");
				return;
			}
			double distance = p.getLocation().distance(last.getLocation());
			if (distance > 70.0D) {
				p.sendMessage("§5§lNINJA§f O player está §9§lMUITO LONGE! §b(" + ((int) distance)
						+ " blocos §3[minimo 70]§b)");
				return;
			}
			if (!inCooldown(p)) {
				addCooldown(p, 10);
				p.teleport(last);
			} else {
				sendCooldown(p);
			}
		}
	}
}
