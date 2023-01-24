package br.com.weavenmc.commons.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class WorldEditCommand implements CommandClass {

	private AsyncWorldEdit asyncWorldEdit = AsyncWorldEdit.getInstance();

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	@Command(name = "cleargrif", groupToUse = Group.TRIAL)
	public void cleargrif(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length <= 1) {
				sender.sendMessage("§6§lCLEARGRIF§f Utilize: §6§l/cleargrif§f <lava, agua> (raio)");
			} else {
				if (args[0].equalsIgnoreCase("lava")) {
					try {
						int radius = Integer.valueOf(args[1]);
						if (radius <= 100) {
							Queue<Block> grifed = new ConcurrentLinkedQueue<>();
							for (Block nearby : getNearbyBlocks(sender.getPlayer().getLocation(), radius)) {
								if (nearby.getType() != Material.LAVA && nearby.getType() != Material.STATIONARY_LAVA
										&& nearby.getType() != Material.FIRE)
									continue;
								grifed.add(nearby);
							}
							if (!grifed.isEmpty()) {
								int finded = grifed.size();
								while (!grifed.isEmpty()) {
									Block next = grifed.poll();
									asyncWorldEdit.setAsyncBlock(next.getWorld(), next.getLocation(), 0);
								}
								sender.sendMessage(
										"§6§lCLEARGRIF§f Você limpou qualquer tipo de §e§lLAVA§f ou §e§lFOGO§f em um raio de "
												+ radius + " blocos (" + finded + ") blocos foram afetados.");
							} else {
								sender.sendMessage(
										"§6§lCLEARGRIF§f Não foi encontrado nenhuma §e§lLAVA§f ou §e§lFOGO§f!");
							}
						} else {
							sender.sendMessage(
									"§6§lCLEARGRIF§f Por questões de segurança, o valor máximo raio permitido é 100.");
						}
					} catch (NumberFormatException ex) {
						sender.sendMessage("§6§lCLEARGRIF§f " + ex.getMessage());
					}
				} else if (args[0].equalsIgnoreCase("agua") || args[0].equalsIgnoreCase("water")) {
					try {
						int radius = Integer.valueOf(args[1]);
						if (radius <= 100) {
							Queue<Block> grifed = new ConcurrentLinkedQueue<>();
							for (Block nearby : getNearbyBlocks(sender.getPlayer().getLocation(), radius)) {
								if (nearby.getType() != Material.WATER && nearby.getType() != Material.STATIONARY_WATER)
									continue;
								grifed.add(nearby);
							}
							if (!grifed.isEmpty()) {
								int finded = grifed.size();
								while (!grifed.isEmpty()) {
									Block next = grifed.poll();
									asyncWorldEdit.setAsyncBlock(next.getWorld(), next.getLocation(), 0);
								}
								sender.sendMessage(
										"§6§lCLEARGRIF§f Você limpou qualquer tipo de §e§lAGUA§f em um raio de "
												+ radius + " blocos (" + finded + ") blocos foram afetados.");
							} else {
								sender.sendMessage(
										"§6§lCLEARGRIF§f Não foi encontrado nenhuma §e§lAGUAf!");
							}
						} else {
							sender.sendMessage(
									"§6§lCLEARGRIF§f Por questões de segurança, o valor máximo raio permitido é 100.");
						}
					} catch (NumberFormatException ex) {
						sender.sendMessage("§6§lCLEARGRIF§f " + ex.getMessage());
					}
				} else {
					sender.sendMessage("§6§lCLEARGRIF§f Utilize: §6§l/cleargrif§f <lava, agua> (raio)");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@SuppressWarnings("deprecation")
	@Command(name = "worldedit", aliases = { "we" }, groupToUse = Group.MODPLUS)
	public void worldedit(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (BukkitMain.getInstance().getConfig().getBoolean("command_worldedit")) {
				Player player = sender.getPlayer();
				if (args.length == 0) {
					player.sendMessage("§6§lWORLDEDIT§f Utilize: §6§l/we§f <id> ou <id:data>");
				} else {
					if (asyncWorldEdit.hasFirstPosition(player) && asyncWorldEdit.hasSecondPosition(player)) {
						List<Location> areaToModify = asyncWorldEdit.fromTwoPoints(asyncWorldEdit.getPosition1(player),
								asyncWorldEdit.getPosition2(player));

						for (Location blockLocation : areaToModify) {
							try {
								asyncWorldEdit.setAsyncBlock(player.getWorld(), blockLocation, args[0]);
							} catch (Exception ex) {
								player.sendMessage("§6§lWORLDEDIT§f " + ex.getMessage());
								return;
							}
						}

						player.sendMessage("§6§lWORLDEDIT§f Você §e§lMODIFICOU§f a área selecionada para o bloco §e§l"
								+ Material.getMaterial(Integer.valueOf(args[0].split(":")[0])).name());
					} else {
						player.sendMessage(
								"§6§lWORLDEDIT§f Você precisa §e§lSELECIONAR§f as areas §e§l1§f e §e§l2§f com um machado de madeira!");
					}
				}
			} else {
				sender.sendMessage("§6§lWORLDEDIT§f Este comando está §c§lDESABILITADO§f neste servidor!");
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}
}
