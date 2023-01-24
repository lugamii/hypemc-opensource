package br.com.weavenmc.ypvp.ability.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class FishermanAbility extends Ability {

	public FishermanAbility() {
		setName("Fisherman");
		setHasItem(true);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.FISHING_ROD);
		setDescription(
				new String[] { "§7Fisgue seus oponentes e traga-os", "§7até você." });
		setPrice(40000);
		setTempPrice(1500);
	}

	@Override
	public void eject(Player p) {
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerFish(PlayerFishEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			Entity entity = event.getCaught();
			boolean cancel = false;
			if (entity instanceof Player) {
				Gamer gamer = gamer((Player) entity);
				if (gamer.getWarp().isProtected((Player) entity)) {
					cancel = true;
				}
				gamer = null;
			}
			if (!cancel) {
				Block block = event.getHook().getLocation().getBlock();
				if (entity != null && entity != block) {
					entity.teleport(p.getPlayer().getLocation());
				}
				block = null;
			}
			entity = null;
		}
		p = null;
	}
}
