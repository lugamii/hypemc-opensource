package br.com.weavenmc.ypvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;

public class SignListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock() != null) {
				if (event.getClickedBlock().getType() == Material.WALL_SIGN
						|| (event.getClickedBlock().getType() == Material.SIGN_POST)) {
					Sign s = (Sign) event.getClickedBlock().getState();
					String[] lines = s.getLines();
					if (lines.length > 3) {
						if (lines[0].equals("§6Hype§fMC") && lines[2].equals("§6§m>-----<") && lines[3].equals(" ")) {
							if (lines[1].equals("§bSopas")) {
								event.setCancelled(true);
								Inventory soup = inv(54, "§bSopas");
								for (int i = 0; i < 54; i++)
									soup.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
								player.openInventory(soup);
							} else if (lines[1].equals("§eRecraft")) {
								event.setCancelled(true);
								Inventory recraft = inv(9, "§eRecraft");
								recraft.setItem(3, new ItemStack(Material.BOWL, 64));
								recraft.setItem(4, new ItemStack(Material.RED_MUSHROOM, 64));
								recraft.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 64));
								player.openInventory(recraft);
							} else if (lines[1].equals("§cCocoabean")) {
								event.setCancelled(true);
								Inventory cocoa = inv(9, "§cCocoabean");
								cocoa.setItem(3, new ItemStack(Material.BOWL, 64));
								cocoa.setItem(4, new ItemStack(Material.getMaterial(351), 64, (byte) 3));
								cocoa.setItem(5, new ItemStack(Material.getMaterial(351), 64, (byte) 3));
								player.openInventory(cocoa);
							} else if (lines[1].equals("§aCactus")) {
								event.setCancelled(true);
								Inventory cactu = inv(9, "§aCactus");
								cactu.setItem(3, new ItemStack(Material.BOWL, 64));
								cactu.setItem(4, new ItemStack(Material.CACTUS, 64));
								cactu.setItem(5, new ItemStack(Material.CACTUS, 64));
								player.openInventory(cactu);
							}
						} else if (lines[2].equals(" ") && lines[3].equals("§a§lClique!")) {
							BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
							if (lines[0].equals("§6§lMOEDAS")) {
								String input = ChatColor.stripColor(lines[1]);
								try {
									Integer quantity = Integer.valueOf(input);
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									bP.addMoney(quantity);
									bP.save(DataCategory.BALANCE);
									player.sendMessage("§6§lMONEY§f Você recebeu §6§l" + quantity + " MOEDAS");
								} catch (NumberFormatException ex) {
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									player.sendMessage("§cEsta placa não possuia validade ou estava com erro!");
								}
							} else if (lines[0].equals("§b§lTICKET")) {
								String input = ChatColor.stripColor(lines[1]);
								try {
									Integer quantity = Integer.valueOf(input);
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									bP.addTickets(quantity);
									bP.save(DataCategory.BALANCE);
									player.sendMessage("§3§lTICKETS§f Você recebeu §3§l" + quantity + " TICKETS");
								} catch (NumberFormatException ex) {
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									player.sendMessage("§cEsta placa não possuia validade ou estava com erro!");
								}
							} else if (lines[0].equals("§3§lDOUBLEXP")) {
								String input = ChatColor.stripColor(lines[1]);
								try {
									Integer quantity = Integer.valueOf(input);
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									bP.addDoubleXpMultiplier(quantity);
									bP.save(DataCategory.BALANCE);
									player.sendMessage("§3§lDOUBLEXP§f Você recebeu §3§l" + quantity + " DOUBLEXPS");
								} catch (NumberFormatException ex) {
									event.setCancelled(true);
									event.getClickedBlock().breakNaturally();
									player.sendMessage("§cEsta placa não possuia validade ou estava com erro!");
								}
							}
						}
					}
				}
			}
		}
	}

	protected Inventory inv(int size, String title) {
		return Bukkit.createInventory(null, size, title);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).contains("&")) {
			event.setLine(0, event.getLine(0).replace("&", "§"));
		}
		if (event.getLine(1).contains("&")) {
			event.setLine(1, event.getLine(1).replace("&", "§"));
		}
		if (event.getLine(2).contains("&")) {
			event.setLine(2, event.getLine(2).replace("&", "§"));
		}
		if (event.getLine(3).contains("&")) {
			event.setLine(3, event.getLine(3).replace("&", "§"));
		}

		String line = event.getLine(0);

		if (line.equalsIgnoreCase("sopa") || line.equalsIgnoreCase("sopas")) {
			event.setLine(0, "§6Hype§fMC");
			event.setLine(1, "§bSopas");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("recraft") || line.equalsIgnoreCase("recrafts")) {
			event.setLine(0, "§6Hype§fMC");
			event.setLine(1, "§eRecraft");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("cocoa") || line.equalsIgnoreCase("cocoabean")) {
			event.setLine(0, "§6Hype§fMC");
			event.setLine(1, "§cCocoabean");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.equalsIgnoreCase("cactu") || line.equalsIgnoreCase("cactus")) {
			event.setLine(0, "§6Hype§fMC");
			event.setLine(1, "§aCactus");
			event.setLine(2, "§6§m>-----<");
			event.setLine(3, " ");
		} else if (line.contains(":")) {
			String[] code = line.split(":");
			if (code.length > 1) {
				if (code[0].equalsIgnoreCase("money")) {
					try {
						int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§6§lMOEDAS");
						event.setLine(1, "§e§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex) {

					}
				} else if (code[0].equalsIgnoreCase("ticket")) {
					try {
						int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§b§lTICKET");
						event.setLine(1, "§3§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex) {

					}
				} else if (code[0].equalsIgnoreCase("doublexp")) {
					try {
						int quantity = Integer.valueOf(code[1]);
						event.setLine(0, "§3§lDOUBLEXP");
						event.setLine(1, "§b§l" + quantity);
						event.setLine(2, " ");
						event.setLine(3, "§a§lClique!");
					} catch (NumberFormatException ex) {

					}
				}
			}
		}
	}
}
