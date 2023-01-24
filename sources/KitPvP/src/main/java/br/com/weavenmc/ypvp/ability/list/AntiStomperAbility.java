package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class AntiStomperAbility extends Ability {

	public AntiStomperAbility() {
		setName("AntiStomper");
		setHasItem(false);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.DIAMOND_HELMET);
		setDescription(new String[] { "§7Receba dano reduzido de stompers." });
		setPrice(30000);
		setTempPrice(3500);
	}

	@Override
	public void eject(Player p) {
		
	}
}
