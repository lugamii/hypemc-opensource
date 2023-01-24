package br.com.weavenmc.ypvp.ability.list;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;

public class ThorAbility extends Ability {

	public ThorAbility() {
		setName("Thor");
		setHasItem(true);
		setGroupToUse(Group.PRO);
		setIcon(Material.GOLD_AXE);
		setDescription(new String[] { "§7Tenha o próprio Mjolnir de Thor", "§7em sua versão mais poderosa." });
		setPrice(65000);
		setTempPrice(2500);
	}

	@Override
	public void eject(Player p) {
		
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onThor(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand())) {
				event.setCancelled(true);
				if (!inCooldown(p)) {
					addCooldown(p, 10);
					Block target = p.getTargetBlock((HashSet<Byte>) null, 200).getRelative(BlockFace.UP);
					for (int i = 0; i < 2; i++) {
						new BukkitRunnable() {
							@Override
							public void run() {
								p.getWorld().strikeLightning(target.getLocation());							
							}					
						}.runTaskLater(yPvP.getPlugin(), i * 10L);
					}
				} else {
					sendCooldown(p);
				}
			}
		}
	}
}
