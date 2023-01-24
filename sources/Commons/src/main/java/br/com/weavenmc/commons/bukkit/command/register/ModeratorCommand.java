package br.com.weavenmc.commons.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.command.CommandFramework.Completer;
import br.com.weavenmc.commons.core.permission.Group;

public class ModeratorCommand implements CommandClass {

	@Command(name = "kick", aliases = { "kickar" }, groupToUse = Group.INVESTIDOR)
	public void kick(BukkitCommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§c§lKICK§f Utilize: /kick <player> <motivo>");
		} else {
			Player t = Bukkit.getPlayer(args[0]);
			if (t != null) {
				StringBuilder builder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					String space = " ";
					if (i >= args.length - 1)
						space = "";
					builder.append(args[i] + space);
				}
				if (builder.length() == 0) {
					builder.append("Não informado");
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
					if (!player.hasGroupPermission(Group.TRIAL))
						continue;
					p.sendMessage("§c§lKICK§f " + t.getName() + "(" + t.getUniqueId().toString()
							+ ") foi §c§lKICKADO§f por " + sender.getName() + ". Motivo: " + builder.toString());
				}
				t.kickPlayer("§6§lHYPE§f§lMC" + "\n" + "§fVocê foi §e§lKICKADO.\n" + "§fKickado por: §7"+ sender.getName() + "\n"
						+ "§fMotivo: §7" + builder.toString());
				t = null;
				builder = null;
			} else {
				sender.sendMessage("§c§lKICK§f " + args[0] + " esta offline");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Command(name = "gamemode", aliases = { "gm" }, groupToUse = Group.INVESTIDOR)
	public void gamemode(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§3§lGAMEMODE§f Utilize: /gamemode <modo, id> [player]");
			} else if (args.length == 1) {
				GameMode gameMode = null;
				for (GameMode mode : GameMode.values()) {
					if (args[0].toLowerCase().startsWith(mode.name().toLowerCase())
							|| args[0].equals(String.valueOf(mode.getValue()))) {
						gameMode = mode;
						break;
					}
				}
				if (gameMode != null) {
					if (p.getGameMode() != gameMode) {
						p.setGameMode(gameMode);
						p.sendMessage("§3§lGAMEMODE§f Seu §3§lGAMEMODE§f foi alterado para §b§l" + gameMode.name());
					} else {
						p.sendMessage("§3§lGAMEMODE§f Voce ja esta no §b§l" + gameMode.name());
					}
					gameMode = null;
				} else {
					p.sendMessage("§3§lGAMEMODE§f O modo " + args[0] + " é invalido");
				}
			} else if (args.length == 2) {
				Player t = Bukkit.getPlayer(args[1]);
				if (t != null) {
					if (t.getUniqueId() != p.getUniqueId()) {
						GameMode gameMode = null;
						for (GameMode mode : GameMode.values()) {
							if (args[0].toLowerCase().startsWith(mode.name().toLowerCase())
									|| args[0].equals(String.valueOf(mode.getValue()))) {
								gameMode = mode;
								break;
							}
						}
						if (gameMode != null) {
							if (t.getGameMode() != gameMode) {
								t.setGameMode(gameMode);
								p.sendMessage("§3§lGAMEMODE§f Voce alterou o §3§lGAMEMODE§f de " + t.getName()
										+ " para §b§l" + gameMode.name());
							} else {
								p.sendMessage("§3§lGAMEMODE§f " + t.getName() + " ja esta no §b§l" + gameMode.name());
							}
							gameMode = null;
						} else {
							p.sendMessage("§3§lGAMEMODE§f O modo " + args[0] + " é invalido");
						}
					} else {
						p.performCommand("gamemode " + args[0]);
					}
				} else {
					p.sendMessage("§3§lGAMEMODE§f " + args[1] + " esta offline.");
				}
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Completer(name = "gamemode", aliases = { "gm" })
	public List<String> gamemodecompleter(BukkitCommandSender sender, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
			if (player.hasGroupPermission(Group.INVESTIDOR)) {
				if (args.length == 1) {
					for (GameMode mode : GameMode.values()) {
						list.add(mode.name());
					}
				} else if (args.length == 2) {
					for (Player o : Bukkit.getOnlinePlayers()) {
						if (args[0].toLowerCase().startsWith(o.getName().toLowerCase())) {
							list.add(o.getName());
						}
					}
				}
			}
			player = null;
			p = null;
		}
		return list;
	}

	@Command(name = "teleport", aliases = { "tp", "teleportar" }, groupToUse = Group.INVESTIDOR)
	public void teleport(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				p.sendMessage("§6§lTELEPORTE§f Utilize: /teleport <player> [alvo] e/ou (x) (y) (z)");
			} else if (args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					p.teleport(t.getLocation());
					p.sendMessage("§6§lTELEPORTE§f Voce foi §e§lTELEPORTADO§f para " + t.getName() + ".");
					t = null;
				} else {
					p.sendMessage("§6§lTELEPORTE§f " + args[0] + " esta offline");
				}
			} else if (args.length == 2) {
				Player t1 = Bukkit.getPlayer(args[0]);
				Player t2 = Bukkit.getPlayer(args[1]);
				if (t1 != null) {
					if (t2 != null) {
						t1.teleport(t2.getLocation());
						p.sendMessage("§6§lTELEPORTE§f Voce §e§lTELEPORTOU§f " + t1.getName() + " para " + t2.getName()
								+ ".");
						t2 = null;
					} else {
						p.sendMessage("§6§lTELEPORTE§f " + args[1] + " esta offline");
					}
					t1 = null;
				} else {
					p.sendMessage("§6§lTELEPORTE§f " + args[0] + " esta offline");
				}
			} else if (args.length == 3) {
				final int x, y, z;
				try {
					x = Integer.valueOf(args[0]);
					y = Integer.valueOf(args[1]);
					z = Integer.valueOf(args[2]);
					Location teleport = new Location(p.getWorld(), x, y, z);
					p.teleport(teleport);
					p.sendMessage("§6§lTELEPORTE§f Voce foi §e§lTELEPORTADO§f para " + x + ", " + y + ", " + z);
					teleport = null;
				} catch (NumberFormatException e) {
					p.sendMessage("§6§lTELEPORTE§f As coordenadas informadas sao invalidas.");
				}
			} else if (args.length >= 4) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					final int x, y, z;
					try {
						x = Integer.valueOf(args[1]);
						y = Integer.valueOf(args[2]);
						z = Integer.valueOf(args[3]);
						Location teleport = new Location(p.getWorld(), x, y, z);
						t.teleport(teleport);
						p.sendMessage("§6§lTELEPORTE§f Voce §e§lTELEPORTOU§f " + t.getName() + " para " + x + ", " + y
								+ ", " + z);
						teleport = null;
					} catch (NumberFormatException e) {
						p.sendMessage("§6§lTELEPORTE§f As coordenadas informadas sao invalidas.");
					}
					t = null;
				} else {
					p.sendMessage("§6§lTELEPORTE§f " + args[0] + " esta offline");
				}
			}
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Command(name = "teleportall", aliases = { "tpall" }, groupToUse = Group.MOD)
	public void tpall(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				new BukkitRunnable() {
					@Override
					public void run() {
						for (Player o : Bukkit.getOnlinePlayers()) {
							if (o != null && o.isOnline() && o.getUniqueId() != p.getUniqueId()) {
								o.teleport(p.getLocation());
								o.setFallDistance(0.0f);
							}
						}
						Bukkit.broadcastMessage(
								"§3§lTPALL§f Todos os jogadores foram §b§lTELEPORTADOS§f para " + p.getName());
					}
				}.runTask(BukkitMain.getInstance());
			} else if (args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				if (t != null) {
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Player o : Bukkit.getOnlinePlayers()) {
								if (o != null && o.isOnline() && o.getUniqueId() != t.getUniqueId()) {
									o.teleport(t.getLocation());
									o.setFallDistance(0.0f);
								}
							}
							Bukkit.broadcastMessage(
									"§3§lTPALL§f Todos os jogadores foram §b§lTELEPORTADOS§f para " + t.getName());
						}
					}.runTask(BukkitMain.getInstance());
				} else {
					p.sendMessage("§3§lTPALL§f " + args[0] + " esta offline");
				}
			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@Command(name = "steleportall", aliases = { "'" }, groupToUse = Group.MOD)
	public void stpall(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			if (args.length == 0) {
				List<Player> stpall = new ArrayList<>();

				Bukkit.getOnlinePlayers().forEach(players -> stpall.add(players));
				Location loc = p.getLocation();
				new BukkitRunnable() {
					int i = 0;
					int exc = 0;

					@Override
					public void run() {
						// TODO Auto-generated method stub

						if (exc > 3) {
							cancel();
							if (p.isOnline())
								p.sendMessage("§c/stpall cancelado!");
							return;
						}

						try {

							if (i >= stpall.size()) {
								p.sendMessage("§a/stpall cancelado! Todos puxados (" + stpall.size() + ")");
								stpall.clear();
								cancel();
								return;
							}
							if (stpall.get(i) == null) {
								i++;
								return;
							}

							stpall.get(i).teleport(loc);
							i++;
						} catch (Exception e) {
							exc++;
						}
					}
				}.runTaskTimerAsynchronously(BukkitMain.getInstance(), 0, 8l);

			}
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}
}
