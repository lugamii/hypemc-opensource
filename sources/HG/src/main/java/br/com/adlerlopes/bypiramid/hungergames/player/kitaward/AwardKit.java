package br.com.adlerlopes.bypiramid.hungergames.player.kitaward;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors.Reward;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors.Surprise;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors.Reward.RewardType;

public class AwardKit extends Surprise {

	public List<String> hgKits = Arrays.asList("Anchor", "Barbarian", "Blink", "Boxer", "Camel",
			"Cannibal", "Cultivator", "Demoman", "Endermage", "Fireman", "Fisherman", "Forger", "Gladiator");

	public AwardKit(Manager manager) {
		super(manager, null, new ItemStack(Material.AIR));

		Reward reward = null;

		String kit = hgKits.get(getRandom().nextInt(hgKits.size()));
		reward = new Reward(kit, 1, RewardType.KIT);

		Kit kitExample = getManager().getKitManager().getKit("Endermage");

		if (getManager().getKitManager().getKit(kit) != null) {
			kitExample = getManager().getKitManager().getKit(kit);
		}

		setRewardIcon(kitExample.getIcon());

		setReward(reward);
	}

}
