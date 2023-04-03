package br.com.adlerlopes.bypiramid.hungergames.player.kitaward;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.PermissionAttachment;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors.Reward;
import br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors.Surprise;

public class AwardManager extends Management {

	public ArrayList<Player> suject = new ArrayList<Player>();

	public AwardManager(Manager manager) {
		super(manager);
	}

	public boolean initialize() {
		for (Entity ent : Bukkit.getWorld("world").getEntities()) {
			ent.remove();
		}

		return true;
	}

	public boolean generate(Player player) {
		createInventory(player);
		return true;
	}

	public void createInventory(Player player) {
		if (getManager().getGameManager().getTimer().getTime() < 5) {
			player.closeInventory();
			return;
		}
		if (getManager().getGamerManager().getGamer(player).isBlockFunction()) {
			player.closeInventory();
			return;
		}

		Inventory inventory = Bukkit.createInventory(player, 18, "Kit surpresa da partida");

		for (int i = 0; i <= 17; i++) {
			if (inventory.getItem(i) != null)
				continue;
			new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a").setBreakable().setDurability(15).build(inventory,
					i);
			if (getManager().getGameManager().getTimer().getTime() < 5) {
				player.closeInventory();
				return;
			}
			if (getManager().getGamerManager().getGamer(player).isBlockFunction()) {
				player.closeInventory();
				return;
			}
		}

		new ItemBuilder(Material.HOPPER).setName("§3Prêmio").setBreakable().build(inventory, 4);

		createAnimation(player, inventory);
		if (!getManager().getGamerManager().getGamer(player).isBlockFunction()) {
			player.openInventory(inventory);
		}
	}

	private void givePrize(Player player, Surprise crate) {

		if (getManager().getSurpriseKitManager().suject.contains(player)) {
			return;
		}
		
		if (getManager().getGameManager().getTimer().getTime() < 5) {
			player.closeInventory();
			return;
		}
		if (getManager().getGamerManager().getGamer(player).isBlockFunction()) {
			player.closeInventory();
			return;
		}

		Gamer gamer = getManager().getGamerManager().getGamer(player);

		if (gamer.getSurpriseKit() != null) {
			return;
		}

		Reward reward = crate.getReward();

		PermissionAttachment attachment = player.addAttachment(getManager().getPlugin());
		attachment.setPermission("hgkit2." + reward.getReward().toLowerCase(), true);

		suject.add(gamer.getPlayer());
		gamer.setSurpriseKit(getManager().getKitManager().getKit(reward.getReward().toLowerCase()));

		player.sendMessage("§6§lKIT SURPRESA §fVocê ganhou o kit " + reward.getReward().toUpperCase() + "!");
	}

	private void createAnimation(Player player, Inventory inventory) {
		AtomicInteger startIndex = new AtomicInteger(0);
		Surprise[] boxes = new Surprise[50];

		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = createBoxByType();
		}

		new Thread(new Runnable() {
			int time = 0;
			Surprise winSurprise;

			@Override
			public void run() {
				while (startIndex.get() < boxes.length - 1) {
					startIndex.incrementAndGet();
					int currentIndex = 0;
					for (int i = startIndex.get(); i <= startIndex.get() + 6; i++) {
						if (i >= boxes.length - 1) {
							break;
						}
						inventory.setItem(10 + currentIndex, boxes[i].getRewardIcon());
						player.playSound(player.getLocation(), Sound.CLICK, 5, 5);

						if (currentIndex == 3)
							winSurprise = boxes[i];

						currentIndex++;
					}

					if (!inventory.getViewers().contains(player)) {

						if (getManager().getGameManager().getTimer().getTime() < 5) {
							player.closeInventory();
							return;
						}
						if (getManager().getGamerManager().getGamer(player).isBlockFunction()) {
							player.closeInventory();
							return;
						}
						
						player.openInventory(inventory);
					}

					if (startIndex.get() >= boxes.length - 1) {
						player.closeInventory();
					}

					if (time * 5 >= 500) {
						time += 500;
						givePrize(player, winSurprise);
						break;
					} else {
						time += 5;
					}
					try {
						Thread.sleep(time * 5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private Surprise createBoxByType() {
		return new AwardKit(getManager());
	}

}
