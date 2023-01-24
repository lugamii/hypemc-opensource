package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class StomperAbility extends Ability {

	public StomperAbility() {
		setName("Stomper");
		setHasItem(false);
		setGroupToUse(Group.BETA);
		setIcon(Material.DIAMOND_BOOTS);
		setDescription(
				new String[] { "§7Pule de uma altura e faça os inimigos", "§7abaixo receberem o seu dano de queda." });
		setPrice(90000);
		setTempPrice(9000);
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		Entity entityStomper = event.getEntity();
		if (!(entityStomper instanceof Player))
			return;
		Player stomper = (Player) entityStomper;
		if (!hasKit(stomper))
			return;
		DamageCause cause = event.getCause();
		if (cause != DamageCause.FALL)
			return;
		double dmg = event.getDamage();
		boolean hasPlayer = false;
		for (Player stompado : Bukkit.getOnlinePlayers()) {
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(stompado.getUniqueId());
			if (stompado.getUniqueId() == stomper.getUniqueId())
				continue;
			if (stompado.getLocation().distance(stomper.getLocation()) > 6) {
				continue;
			}
			double dmg2 = dmg * (10 / 10d);
			if ((stompado.isSneaking() || gamer.getAbility().getName().equals("AntiStomper")) && dmg2 > 4d)
				dmg2 = 4d;
			stomper.sendMessage("§5§lSTOMPER§f Você §9§lSTOMPOU§f o §9§l" + stompado.getName());
			stompado.sendMessage("§5§lSTOMPER§f Você foi §9§lSTOMPADO§f pelo §9§l" + stomper.getName());
			gamer.setLastCombat(stomper.getUniqueId());
			stompado.damage(dmg2, stomper);
			hasPlayer = true;
		}
		if (hasPlayer) {
			stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1, 1);
		}
		if (event.getDamage() > 4d) {
			event.setDamage(4d);
		}
	}
}
