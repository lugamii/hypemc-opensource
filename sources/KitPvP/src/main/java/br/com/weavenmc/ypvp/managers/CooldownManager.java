package br.com.weavenmc.ypvp.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import br.com.weavenmc.ypvp.Management;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class CooldownManager extends Management {

	Map<Player, Cooldown> cooldown1;
	Map<Player, Cooldown> cooldown2;

	public CooldownManager(yPvP plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public void removeCooldown(Player player) {

		if (cooldown1.containsKey(player)) {
			cooldown1.get(player).nullable();
			cooldown1.remove(player);
		}

		if (cooldown2.containsKey(player)) {
			cooldown2.get(player).nullable();
			cooldown2.remove(player);

		}
	}

	public void setCooldown(Player player, Ability ability, int time) {
		if (cooldown1.containsKey(player)) {
			if (cooldown1.get(player).has()) {

				cooldown2.put(player, new Cooldown(player, ability, time));
			} else {
				cooldown1.put(player, new Cooldown(player, ability, time));
			}
		} else if (cooldown2.containsKey(player)) {
			if (cooldown2.get(player).has()) {
				cooldown1.put(player, new Cooldown(player, ability, time));
			} else {
				cooldown2.put(player, new Cooldown(player, ability, time));
			}
		} else {
			cooldown1.put(player, new Cooldown(player, ability, time));

		}
	}

	public boolean hasCooldown(Player p, Ability ability) {
		if (cooldown1.containsKey(p)) {
			if (cooldown1.get(p).getAbility() == ability) {
				if (cooldown1.get(p).has() == true) {
					return true;
				} else {
					cooldown1.get(p).nullable();
					cooldown1.remove(p);
					return false;
				}
			} else {
				if (cooldown2.containsKey(p)) {
					if (cooldown2.get(p).getAbility() == ability) {
						if (cooldown2.get(p).has()) {
							return true;
						} else {
							cooldown2.get(p).nullable();
							cooldown2.remove(p);
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
		cooldown1.clear();
		cooldown2.clear();
	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub
		cooldown1 = new HashMap<Player, Cooldown>();
		cooldown2 = new HashMap<Player, Cooldown>();
	}

}
