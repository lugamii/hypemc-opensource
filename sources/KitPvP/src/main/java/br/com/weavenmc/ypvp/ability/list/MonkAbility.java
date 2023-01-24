package br.com.weavenmc.ypvp.ability.list;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class MonkAbility extends Ability {

	public MonkAbility() {
		setName("Monk");
		setHasItem(true);
		setGroupToUse(Group.MEMBRO);
		setIcon(Material.BLAZE_POWDER);
		setDescription(new String[] { "§7Bagunçe o inventáro de seu inimigo", "§7e tenha chance de matá-lo." });
		setPrice(35000);
		setTempPrice(3500);
	}

	@Override
	public void eject(Player p) {

	}

	@EventHandler
	public void onMonk(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		if (hasKit(p)) {
			if (isItem(p.getItemInHand())) {
				if (event.getRightClicked() instanceof Player) {
					Player target = (Player) event.getRightClicked();
					Gamer gamer = gamer(target);
					if (!gamer.getWarp().isProtected(target)) {
						if (!inCooldown(p)) {
							addCooldown(p, 12);

							int random = new Random().nextInt(target.getInventory().getSize() - 10 + 1 + 10);

							ItemStack selected = target.getInventory().getItem(random);
							ItemStack ItemMudado = target.getItemInHand();

							target.setItemInHand(selected);
							target.getInventory().setItem(random, ItemMudado);
							target.updateInventory();

							p.sendMessage("§5§lMONK§f Você monkou o jogador §9§l" + target.getName());
							target.sendMessage("§5§lMONK§f Você foi monkado pelo §9§l" + p.getName());
						} else {
							sendCooldown(p);
						}
					} else {
						p.sendMessage("§5§lMONK§f Este jogador está com proteção de spawn.");
					}
				}
			}
		}
	}
}
