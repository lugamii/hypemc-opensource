package br.com.weavenmc.ypvp.ability.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class KangarooAbility extends Ability {

	private final List<Player> kang = new ArrayList<>();

	public KangarooAbility() {
		setName("Kangaroo");
		setHasItem(true);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.FIREWORK);
		setDescription(new String[] { "§7Tenha a habilidade de double-jump", "§7e de se mover mais rápido." });
		setPrice(70000);
		setTempPrice(0);
	}

	@Override
	public void eject(Player p) {
		if (kang.contains(p)) {
			kang.remove(p);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p) & event.getAction() != Action.PHYSICAL && p.getItemInHand().getType() == getIcon()) {
			event.setCancelled(true);
			if (!kang.contains(p)) {
				if (!inCooldown(p)) {
					org.bukkit.util.Vector velocity = p.getEyeLocation().getDirection();
					if (p.isSneaking())
						velocity = velocity.multiply(1.8F).setY(0.5F);
					else
						velocity = velocity.multiply(0.5F).setY(1.0F);
					p.setFallDistance(-1.0F);
					p.setVelocity(velocity);
					kang.add(p);
					velocity = null;
				} else {
					sendCooldown(p);
				}
			}
		}
		p = null;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (kang.contains(event.getPlayer()))
			kang.remove(event.getPlayer());
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (hasKit(p)) {
				if (!event.isCancelled()) {
					if (event.getDamager() instanceof Player) {
						addCooldown(p, 7);
					}
				}
			}
			p = null;
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!hasKit(p))
			return;
		if (!kang.contains(p))
			return;
		if (!p.isOnGround())
			return;
		kang.remove(p);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (hasKit(p) && event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamage() > 12.0D) {
				event.setDamage(12.0D);
			}
		}
	}
}
