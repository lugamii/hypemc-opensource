package br.com.weavenmc.ypvp.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.yPvP.PvPType;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.gamer.Gamer;
import br.com.weavenmc.ypvp.minigame.BattleMinigame;
import br.com.weavenmc.ypvp.minigame.FramesMinigame;
import br.com.weavenmc.ypvp.minigame.LavaChallengeMinigame;
import br.com.weavenmc.ypvp.minigame.Minigame;
import br.com.weavenmc.ypvp.minigame.SpawnMinigame;

public class GameCommand implements CommandClass {

	@Command(name = "hologram", aliases = { "hl" }, groupToUse = Group.DONO)
	public void hologram(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			if (args.length == 0) {
				sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <ranking, pvpkills>");
			} else {
				if (args[0].equalsIgnoreCase("ranking")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = yPvP.getPlugin().getConfig();
					config.set("hologram.toprank.location.x", playerLocation.getX());
					config.set("hologram.toprank.location.y", playerLocation.getY());
					config.set("hologram.toprank.location.z", playerLocation.getZ());
					config.set("hologram.toprank.location.yaw", playerLocation.getYaw());
					config.set("hologram.toprank.location.pitch", playerLocation.getPitch());
					yPvP.getPlugin().saveConfig();

					if (yPvP.getPlugin().getTopHologram() != null)
						yPvP.getPlugin().getTopHologram().setLocation(playerLocation);
					else
						yPvP.getPlugin().initHolograms();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP RANKING§f foi §b§lATUALIZADO§f!");
				} else if (args[0].equalsIgnoreCase("pvpkills")) {
					Player p = sender.getPlayer();
					Location playerLocation = p.getLocation();
					FileConfiguration config = yPvP.getPlugin().getConfig();
					config.set("hologram.pvpkills.location.x", playerLocation.getX());
					config.set("hologram.pvpkills.location.y", playerLocation.getY());
					config.set("hologram.pvpkills.location.z", playerLocation.getZ());
					config.set("hologram.pvpkills.location.yaw", playerLocation.getYaw());
					config.set("hologram.pvpkills.location.pitch", playerLocation.getPitch());
					yPvP.getPlugin().saveConfig();

					if (yPvP.getPlugin().getTopPVPKills() != null)
						yPvP.getPlugin().getTopPVPKills().setLocation(playerLocation);
					else
						yPvP.getPlugin().initHolograms();

					sender.sendMessage("§3§lHOLOGRAM§f O local do §b§lTOP PVP KILLS§f foi §b§lATUALIZADO§f!");
				} else {
					sender.sendMessage("§3§lHOLOGRAM§f Utilize: /hologram <ranking, hgwins, hgkills, pvpkills>");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "espectar", aliases = { "spec" }, groupToUse = Group.INVESTIDOR)
	public void espectar(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§b§lESPECTAR§f Utilize: /espectar <player>");
			} else {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					if (!t.getUniqueId().equals(p.getUniqueId())) {
						if (AdminMode.getInstance().isAdmin(p)) {
							Gamer tGamer = yPvP.getPlugin().getGamerManager().getGamer(t.getUniqueId());
							if (tGamer.getWarp().getName().equals("1v1")) {
								BattleMinigame battle = ((BattleMinigame) tGamer.getWarp());
								if (battle.isBattling(t)) {
									gamer.setSpectator(t.getUniqueId());
									Player battling = battle.getCurrentBattlePlayer(t);
									for (Player o : Bukkit.getOnlinePlayers()) {
										if (p.getUniqueId().equals(o.getUniqueId()))
											continue;
										p.hidePlayer(o);
									}
									p.teleport(t);
									p.showPlayer(t);
									p.showPlayer(battling);
									p.sendMessage("§b§lESPECTAR§f Você agora está espectando a luta.");
								} else {
									p.sendMessage("§b§lESPECTAR§f O player não está em 1v1.");
								}
							} else {
								p.sendMessage("§b§lESPECTAR§f Voce so pode espectar jogadores em 1v1.");
							}
						} else {
							p.sendMessage("§b§lESPECTAR§f Voce precisa estar no Modo Admin.");
						}
					} else {
						p.sendMessage("§b§lESPECTAR§f Indique outro player.");
					}
				} else {
					p.sendMessage("§b§lESPECTAR§f O player " + args[0] + " nao esta online.");
				}
			}
		}
	}

	@Command(name = "feast")
	public void feastcompass(BukkitCommandSender sender, String label, String[] args) {
		if (!sender.isPlayer())
			return;
		Player player = sender.getPlayer();
		player.setCompassTarget(yPvP.getPlugin().getFeastConfig().getAllChestLocations().get(0));
		player.sendMessage("§a§lFEAST §fSua bússola está apontando para o §efeast§f!");
	}
	
	@Command(name = "feastcontrol", groupToUse = Group.DONO)
	public void feast(BukkitCommandSender sender, String label, String[] args) {
		if (!sender.isPlayer())
			return;
		Player localPlayer = sender.getPlayer();
		if (args.length == 0) {
			localPlayer.sendMessage(
					"§a§lFEAST§f Utilize: /feastcontrol <addchest|removechest>");
			return;
			
		}
		if (args[0].equalsIgnoreCase("addchest")) {
			Block targetBlock = localPlayer.getTargetBlock((Set<Material>) null, 200);
			if (targetBlock.getType() != Material.CHEST) {
				localPlayer.sendMessage("§cVoc\u00ea n\u00e3o est\u00e1 §fOLHANDO§c para um §fBAU§c!");
				return;
			}
			yPvP.getPlugin().getFeastConfig().addChest(targetBlock.getLocation());
			localPlayer.sendMessage("§2Voc\u00ea §fADICIONOU§2 um §fBAU§2 para o feast!");
			return;
		} else {
			if (!args[0].equalsIgnoreCase("removechest")) {
				localPlayer.sendMessage(
						"§a§lFEAST§f Utilize: /feastcontrol <addchest|removechest>");
				return ;
			}
			Block targetBlock = localPlayer.getTargetBlock((Set<Material>) null, 200);
			if (targetBlock.getType() != Material.CHEST) {
				localPlayer.sendMessage("§cVoc\u00ea n\u00e3o est\u00e1 §fOLHANDO§c para um §fBAU§c!");
				return;
			}
			yPvP.getPlugin().getFeastConfig().removeChest(targetBlock.getLocation());
			localPlayer.sendMessage("§2Voc\u00ea §fREMOVEU§2 um §fBAU§2 do feast!");
			return;
		}
	}

	@Command(name = "kit")
	public void kit(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			if (args.length == 0) {
				p.sendMessage("§b§lKITS§f Utilize: /kit <kit>");
			} else {
				if (gamer.getWarp().getName().equals("Spawn")) {
					if (gamer.getAbility().getName().equals("Nenhum")) {
						Ability ability = yPvP.getPlugin().getAbilityManager().getAbility(args[0]);
						if (ability != null) {
							if (bP.hasGroupPermission(Group.BLADE) || bP.hasGroupPermission(ability.getGroupToUse())
									|| hasKit(bP, ability)) {

								gamer.setAbility(ability);
								setInventory(p);
								p.sendMessage("§b§lKITS§f Você selecionou o kit §3§l" + ability.getName());
								TitleAPI.setTitle(p, "§bKit " + ability.getName(), "§fescolhido com sucesso");
								
								SpawnMinigame.teleportToWar(p);
								p.setVelocity(p.getVelocity().setY(0.3));
//								if (!gamer.getAbility2().getName().equals("Nenhum")) {
//									int arena = new Random().nextInt(10);
//									p.teleport(yPvP.getPlugin().getLocationManager().getLocation("arena" + arena));
//									SpawnMinigame.setInventory(p);
//									org.bukkit.util.Vector velocity = p.getEyeLocation().getDirection();
//									velocity = velocity.multiply(0.2F);
//									p.setVelocity(velocity);
//								}

							} else {
								p.sendMessage("§b§lKITS§f Você não possui o kit §3§l" + ability.getName().toUpperCase()
										+ "§f!");
							}
						} else {
							p.sendMessage("§b§lKITS§f O kit " + args[0] + " não existe!");
						}
					} else {
						p.sendMessage("§b§lKITS§f Você já está usando um kit!");
					}
				} else {
					p.sendMessage("§b§lKITS§f Você só pode usar kits no Spawn!");
				}
			}
			gamer = null;
//			bP = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	public boolean hasKit(BukkitPlayer bP, Ability ability) {
		return bP.hasPermission("pvpkit." + ability.getName().toLowerCase());
	}

	@Command(name = "set", aliases = { "setwarp" }, groupToUse = Group.DIRETOR)
	public void set(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§3§lWARPS§f Utilize: /setwarp <warp>");
			} else {
				yPvP.getPlugin().getLocationManager().saveLocation(args[0], p.getLocation());
				p.sendMessage("§3§lWARPS§f Você setou a warp §b§l" + args[0] + "§f.");
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "spawn")
	public void spawn(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			yPvP.getPlugin().getWarpManager().getWarp(SpawnMinigame.class).join(p);
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "score", aliases = { "sidebar", "scoreboard" })
	public void score(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
			Sidebar sidebar = gamer.getSidebar();
			if (sidebar != null) {
				if (sidebar.isHided()) {
					sidebar.show();
					yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
					p.sendMessage("§6§lSCOREBOARD§f Você §e§lATIVOU§f a Scoreboard!");
				} else {
					sidebar.hide();
					p.sendMessage("§6§lSCOREBOARD§f Você §e§lDESATIVOU§f a Scoreboard!");
				}
			} else {
				yPvP.getPlugin().getScoreboardManager().createScoreboard(p);
				p.sendMessage("§6§lSCOREBOARD§f Você §e§lATIVOU§f a Scoreboard!");
			}
			sidebar = null;
			gamer = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "fps")
	public void frames(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(FramesMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "1v1")
	public void battle(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(BattleMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "lava")
	public void lava(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			yPvP.getPlugin().getWarpManager().getWarp(LavaChallengeMinigame.class).join(sender.getPlayer());
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "warp", aliases = { "minigame" })
	public void warp(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§3§lWARPS§f Utilize: §b§l/warp§f <nome>");
			} else {
				Minigame minigame = yPvP.getPlugin().getWarpManager().getWarp(args[0]);
				if (minigame != null) {
					minigame.join(p);
					minigame = null;
					//System.out.println("foi sim");
				} else {
					p.sendMessage("§9§lTELEPORTE§f Esta warp não existe!");
					//System.out.println("foi nao");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	public static void setInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(p.getUniqueId());
		Ability ability = gamer.getAbility();
		for (int i = 0; i < 36; i++)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		if (yPvP.getPlugin().getPvpType() == PvPType.FULLIRON) {
			ItemBuilder builder;
//			p.getInventory().setHelmet(builder.build());
//			builder = new ItemBuilder().type(Material.IRON_CHESTPLATE);
//			p.getInventory().setChestplate(builder.build());
//			builder = new ItemBuilder().type(Material.IRON_LEGGINGS);
//			p.getInventory().setLeggings(builder.build());
//			builder = new ItemBuilder().type(Material.IRON_BOOTS);
//			p.getInventory().setBoots(builder.build());
			builder = new ItemBuilder().type(Material.STONE_SWORD);
			if (ability.getName().equals("PvP"))
				builder.enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());

			if (ability.isHasItem()) {

				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
				p.getInventory().setItem(1, builder.build());

				builder = new ItemBuilder().type(Material.COMPASS).name("§3§lBussola");
				p.getInventory().setItem(8, builder.build());
				return;
			}

		} else {
			ItemBuilder builder = new ItemBuilder().type(Material.STONE_SWORD);
			if (ability.getName().equals("PvP"))
				builder.enchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().setItem(0, builder.build());
			if (ability.isHasItem()) {
				builder = new ItemBuilder().name("§e§l" + ability.getName()).type(ability.getIcon());
				p.getInventory().setItem(1, builder.build());
			}

			builder = null;
		}
		ItemBuilder builder = new ItemBuilder().type(Material.BOWL).amount(32);
		p.getInventory().setItem(13, builder.build());
		builder = new ItemBuilder().type(Material.RED_MUSHROOM).amount(32);
		p.getInventory().setItem(14, builder.build());
		builder = new ItemBuilder().type(Material.BROWN_MUSHROOM).amount(32);
		p.getInventory().setItem(15, builder.build());
		builder = new ItemBuilder().type(Material.COMPASS).name("§3§lBussola");
		p.getInventory().setItem(8, builder.build());
		p.updateInventory();
		builder = null;
	}

	@Completer(name = "kit")
	public List<String> kitcompleter(BukkitCommandSender sender, String label, String[] args) {
		List<String> list = new ArrayList<>();
		Player p = sender.getPlayer();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		if (args.length == 1) {
			for (Ability ability : yPvP.getPlugin().getAbilityManager().getAbilities()) {
				if (bP.hasGroupPermission(ability.getGroupToUse()) || bP.getData(DataType.PLAYER_PERMISSIONS).asList()
						.contains("pvpkit." + ability.getName().toLowerCase())) {
					if (args[0].toLowerCase().startsWith(ability.getName().substring(0, 1))) {
						list.add(ability.getName());
					} else {
						list.add(ability.getName());
					}
				}
			}
		}
		bP = null;
		p = null;
		return list;
	}
}
