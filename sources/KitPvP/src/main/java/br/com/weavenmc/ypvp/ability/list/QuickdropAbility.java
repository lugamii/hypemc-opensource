package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;

public class QuickdropAbility extends Ability {

	public QuickdropAbility() {
		setName("Quickdrop");
		setHasItem(false);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.BOWL);
		setDescription(new String[] { "§7Ao tomar sopa drope os potes automaticamente." });
		setPrice(45000);
		setTempPrice(4000);
	}

	@Override
	public void eject(Player p) {
		
	}
}
