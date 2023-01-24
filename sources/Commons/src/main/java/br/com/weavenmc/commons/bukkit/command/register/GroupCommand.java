package br.com.weavenmc.commons.bukkit.command.register;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerAction;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.bukkit.event.account.PlayerChangeGroupEvent;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.permission.GroupMessage;
import br.com.weavenmc.commons.core.profile.Profile;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.commons.util.string.StringUtils;

public class GroupCommand implements CommandClass {

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static ArrayList<Player> recuseRescue = new ArrayList<>();

	@SuppressWarnings("unused")
	@Command(name = "giftcode", aliases = { "reward" }, runAsync = true)
	public void giftcode(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player player = sender.getPlayer();
			BukkitPlayer bukkitPlayer = (BukkitPlayer) WeavenMC.getAccountCommon()
					.getWeavenPlayer(player.getUniqueId());

			if (bukkitPlayer.hasGroupPermission(Group.ADMIN)) {
				if (args.length == 0) {
					player.sendMessage(
							"§e§lGIFTCODE§f Veja exemplos de uso deste comando\n§f\n§b§l/giftcode generate (group) (time)\n§f\n§b§l/giftcode info (code)\n§f");
					return;
				}
				if (args[0].equalsIgnoreCase("generate")) {

					if (args.length < 3) {
						player.sendMessage(
								"§e§lGIFTCODE §fVocê deve utilizar §b§l/giftcode generate (group) (time)§f!");
						return;
					}

					String code = "";
					Random random;

					Group group = Group.fromString(args[1]);

					if (group == null) {
						player.sendMessage("§e§lGIFTCODE§f O grupo argumentado é inexistente!");
						return;
					}

					if (group.getId() > Group.BETA.getId()) {

						player.sendMessage("§e§lGIFTCODE§f Somente vips!");
						return;
					}

					// format time

					int timeInt = 0;
					String timeCase = null;
					try {
						System.out.println(args[2].substring(1));
						timeInt = Integer.parseInt(args[2].substring(0, 1));
						timeCase = args[2].replace(timeInt + "", "");
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						player.sendMessage("§e§lGIFTCODE§f O tempo informado é invalído!");
					}

					for (int i = 0; i < 9; i++) {
						random = new Random();
						if (i == 3 || i == 6)
							code += "-";
						code += ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length()));
						System.out.println(code);

					}
					try {
						PreparedStatement stm = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `category_rewards` WHERE `code` = ?");
						stm.setString(1, code);
						if (stm.executeQuery().next()) {
							// limpa o codigo já existente
							code = "";

							// gera outro
							for (int i = 0; i < 9; i++) {
								random = new Random();
								if (i == 3 || i == 6)
									code += "-";
								code += ALPHA_NUMERIC_STRING.charAt(random.nextInt(ALPHA_NUMERIC_STRING.length()));

							}

						}
						stm = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `category_rewards` WHERE `code` = ?");
						stm.setString(1, code);
						if (stm.executeQuery().next()) {
							player.sendMessage(
									"§e§lGIFTCODE§f Não foi possivel registrar o código em no banco de dados, tente novamente!");
							stm.close();
							return;
						}
						// code, groupName, groupExpire, redeemed, redeemedBy, redeemedIn
						stm = WeavenMC.getCommonMysql().preparedStatement(
								"INSERT INTO `category_rewards`(`code`, `groupName`, `groupTime`, `redeemed`, `redeemedBy`, `redeemedIn`) VALUES (?,?,?,?,?,?)");

						stm.setString(1, code);
						stm.setString(2, group.toString());
						stm.setString(3, args[2]);
						stm.setBoolean(4, false);
						stm.setString(5, "");
						stm.setLong(6, 0l);

						stm.executeUpdate();
						stm.close();

						player.sendMessage(
								"§e§lGIFTCODE§f O código gerado e registrado no banco de dados é §e§l" + code + "§f!");
					} catch (SQLException e) {
						player.sendMessage(
								"§e§lGIFTCODE§f Não foi possivel registrar o código em no banco de dados, tente novamente!");
						e.printStackTrace();
					}
				}

				if (args[0].equalsIgnoreCase("info")) {
					if (args.length < 2) {
						player.sendMessage("§e§lGIFTCODE §fVocê deve utilizar §b§l/giftcode info (code)§f!");
						return;
					}
					try {
						PreparedStatement stm = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `category_rewards` WHERE `code` = ?");
						stm.setString(1, args[1]);
						ResultSet resultSet = stm.executeQuery();
						if (!resultSet.next()) {
							player.sendMessage("§e§lGIFTCODE§f O código informado é inválido!");
							resultSet.close();
							stm.close();
							return;
						}

						player.sendMessage("§e§lGIFTCODE §fInformações do código " + args[1]);
						player.sendMessage(
								"\n§f\n§fResgatado: " + (resultSet.getBoolean("redeemed") ? "§aSim" : "§cNão"));
						player.sendMessage("§fGrupo a ser resgatado: "
								+ Group.fromString(resultSet.getString("groupName")).getTagToUse().getPrefix());
						player.sendMessage("§fValidade do grupo: " + resultSet.getString("groupTime"));
						player.sendMessage(
								"\n§f\n§fResgatado por: " + (resultSet.getString("redeemedBy").equals("") ? "§cNinguém"
										: "§a" + resultSet.getString("redeemedBy")));
						player.sendMessage(
								"\n§fResgatado em: " + (resultSet.getLong("redeemedIn") == 0l ? "§cAinda não resgatado"
										: "§a" + StringTimeUtils.formatDifference(resultSet.getLong("redeemedIn"))
												.replace("-", "") + " §fatrás!"));
						resultSet.close();

					} catch (SQLException e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}

			} else {
				if (args.length < 2) {
					player.sendMessage("§e§lGIFTCODE §fVocê deve utilizar §b§l/" + label + " rescue (code)§f!");
					return;
				}

				if (args[0].equalsIgnoreCase("rescue")) {
					if (args[1].equalsIgnoreCase("cancel")) {
						if (recuseRescue.contains(player)) {
							recuseRescue.remove(player);
							player.sendMessage("§e§lGIFTCODE§f Agora você não resgatará o giftcode!");
						} else {
							player.sendMessage("§e§lGIFTCODE §fVocê não está resgatando nenhum giftcode!");
						}
						return;
					}
					String code = args[1];
					if (recuseRescue.contains(player)) {

						player.sendMessage("§e§lGIFTCODE§f Agora você já esta resgatando um giftcode!");
						return;

					}

					try {
						PreparedStatement stm = WeavenMC.getCommonMysql()
								.preparedStatement("SELECT * FROM `category_rewards` WHERE `code` = ?");
						stm.setString(1, code);
						ResultSet resultSet = stm.executeQuery();
						if (!resultSet.next()) {
							player.sendMessage("§e§lGIFTCODE§f O código informado é inválido!");
							resultSet.close();
							stm.close();
							return;
						}

						if (resultSet.getBoolean("redeemed")) {
							player.sendMessage("§e§lGIFTCODE§f O código citado já foi resgatado!");
							return;
						}

						player.sendMessage("§e§lGIFTCODE §fInformações do código " + code);
						player.sendMessage(
								"\n§f\n§fResgatado: " + (resultSet.getBoolean("redeemed") ? "§aSim" : "§cNão"));
						player.sendMessage("§fGrupo a ser resgatado: "
								+ Group.fromString(resultSet.getString("groupName")).getTagToUse().getPrefix());
						player.sendMessage("§fValidade do grupo: " + resultSet.getString("groupTime"));
						player.sendMessage(
								"\n§f\n§fResgatado por: " + (resultSet.getString("redeemedBy").equals("") ? "§cNinguém"
										: "§a" + resultSet.getString("redeemedBy")));
						player.sendMessage(
								"\n§fResgatado em: " + (resultSet.getLong("redeemedIn") == 0l ? "§cAinda não resgatado"
										: "§a" + StringTimeUtils.formatDifference(resultSet.getLong("redeemedIn"))
												.replace("-", "") + " §fatrás!"));

						if (resultSet.getString("redeemedBy").equals("")) {
							player.sendMessage(
									"§e§lGIFTCODE §fVocê está resgatando um código válido, ele será recompensado em sua conta em §A§L3 SEGUNDOS§f! Para §ccancelar§f digite /"
											+ label + " rescue cancel");
							recuseRescue.add(player);
							new BukkitRunnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (recuseRescue.contains(player)) {
										PreparedStatement stm;
										try {
											stm = WeavenMC.getCommonMysql().preparedStatement(
													"SELECT * FROM `category_rewards` WHERE `code` = ?");

											stm.setString(1, code);
											ResultSet resultSet = stm.executeQuery();
											if (!resultSet.next()) {
												player.sendMessage("§e§lGIFTCODE§f O código informado é inválido!");
												resultSet.close();
												stm.close();
												return;
											}

											if (!resultSet.getString("redeemedBy").equals("")) {
												player.sendMessage(
														"§e§lGIFTCODE§f Ops, parece que alguem foi mais rápido. Tente na próxima! Código §cinvalído§f.");
												return;
											}

											Group group = Group.fromString(resultSet.getString("groupName"));
											String time = resultSet.getString("groupTime");

											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group "
													+ player.getName() + " add " + group.toString() + " " + time);

											player.sendMessage(
													"§e§lGIFTCODE§f Você §a§lRESGATOU §fo código com sucesso!");

											stm = WeavenMC.getCommonMysql().preparedStatement(
													"UPDATE `category_rewards` SET `redeemedBy` = ? WHERE `code` = ?");

											stm.setString(1, player.getName());
											stm.setString(2, code);

											stm.executeUpdate();
											stm.close();

											stm = WeavenMC.getCommonMysql().preparedStatement(
													"UPDATE `category_rewards` SET `redeemedIn` = ? WHERE `code` = ?");

											stm.setLong(1, new Date().getTime());
											stm.setString(2, code);

											stm.executeUpdate();
											stm.close();

											stm = WeavenMC.getCommonMysql().preparedStatement(
													"UPDATE `category_rewards` SET `redeemed` = ? WHERE `code` = ?");

											stm.setBoolean(1, true);
											stm.setString(2, code);

											stm.executeUpdate();
											stm.close();

											recuseRescue.remove(player);
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									} else {
										cancel();
									}
								}

							}.runTaskLaterAsynchronously(BukkitMain.getInstance(), 3 * 20l);

						}

					} catch (SQLException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} else {
					player.sendMessage("§e§lGIFTCODE §fVocê deve utilizar §b§l/" + label + " rescue (code)§f!");
				}
			}
		}
	}

	@Command(name = "group", aliases = { "grupo" }, groupToUse = Group.ADMIN, runAsync = true)
	public void group(BukkitCommandSender sender, String label, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage("§6§lGROUPSET§f Utilize: /group <player> <add, remove> <rank, id> <tempo>");
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("add")) {
				sender.sendMessage("§6§lGROUPSET§f Utilize: /group " + args[0] + " add <rank> <tempo>");
			} else if (args[1].equalsIgnoreCase("remove")) {
				sender.sendMessage("§6§lGROUPSET§f Utilize: /group " + args[0] + " remove <id>");
			} else {
				sender.sendMessage("§6§lGROUPSET§f Utilize: /group <player> <add, remove> <rank, id> <tempo>");
			}
		} else {
			Profile profile = WeavenMC.getProfile(args[0]);
			if (profile == null)
				profile = WeavenMC.getProfileCommon().createCracked(args[0]);
			if (profile != null) {
				Player t = Bukkit.getPlayer(args[0]);
				BukkitPlayer player = null;
				if (t != null)
					player = BukkitPlayer.getPlayer(t.getUniqueId());
				else {
					player = new BukkitPlayer(profile.getId(), profile.getName());
					if (!player.load(DataCategory.ACCOUNT)) {
						player = null;
						sender.sendMessage(
								"§6§lSETGROUP§f Erro ao tentar efetuar a operação, tente novamente mais tarde. O comando sera salvo na config.yml");

						if (BukkitMain.getInstance().getInConfig("setgroup.failed") == null) {
							BukkitMain.getInstance().registerInConfig("setgroup.failed", new ArrayList<String>());
							BukkitMain.getInstance().saveConfig();
						}

						List<String> failedCommands = BukkitMain.getInstance().getConfig()
								.getStringList("setgroup.failed");
						failedCommands.add("/group " + StringUtils.createArgs(0, args, "", false) + " ; sender = "
								+ sender.getName());

						BukkitMain.getInstance().registerInConfig("setgroup.failed", failedCommands);
						BukkitMain.getInstance().saveConfig();
					}
				}
				if (player != null) {
					if (args[1].equalsIgnoreCase("add")) {
						Group group = Group.fromString(args[2]);
						if (group != null) {
							if (sender.isPlayer()) {
								Player p = sender.getPlayer();
								BukkitPlayer bp = BukkitPlayer.getPlayer(p.getUniqueId());
								if (!bp.hasGroupPermission(player.getGroup()) && !p.isOp()) {
									sender.sendMessage(
											"§6§lSETGROUP§f Você não possui §c§lPERMISSAO§f para manipular o grupo "
													+ group.getTagToUse().getName());
									bp = null;
									group = null;
									p = null;
									player = null;
									t = null;
									return;
								}
								bp = null;
								p = null;
							}
							long groupTime = -1;
							if (args.length >= 4) {
								if (!args[3].equals("-1") && !args[3].startsWith("c")) {
									try {
										groupTime = StringTimeUtils.parseDateDiff(args[3], true);
									} catch (Exception e) {
										sender.sendMessage("§6§lGROUPSET§f O §e§lTEMPO§f está incorreto!");
										group = null;
										player = null;
										t = null;
										return;
									}
								}
								if (args[3].startsWith("c")) {
									try {
										groupTime = Long.valueOf(args[3].replace("c", ""));
									} catch (Exception e) {
										sender.sendMessage("§6§lGROUPSET§f O §e§lTEMPO§f está incorreto!");
										group = null;
										player = null;
										t = null;
										return;
									}
								}
							}
							player.organizeGroups();
							player.addGroup(group, groupTime);
							player.save(DataCategory.ACCOUNT);
							GroupMessage message = new GroupMessage().writeUser(player.getName())
									.writeResponse(sender.getName()).writeGroup(group).writeTime(groupTime)
									.writeAdd(true);
							sender.sendMessage("§6§lGROUPSET§f Você adicionou o grupo " + group.getTagToUse().getName()
									+ "§f na conta do jogador " + player.getName() + "("
									+ player.getUniqueId().toString().replace("-", "") + ") com duração "
									+ (groupTime == -1 ? "§e§lETERNA"
											: "de §e§l" + StringTimeUtils.formatDifference(groupTime)));
							if (t != null) {
								Bukkit.getPluginManager().callEvent(new PlayerChangeGroupEvent(t, group, groupTime));
								message.writeFindPlayer(false);
							} else {
								message.writeFindPlayer(true);
							}
							WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.GROUP_MANAGEMENT_CHANNEL,
									WeavenMC.getGson().toJson(message));
							group = null;
						} else {
							sender.sendMessage("§6§lGROUPSET§f O grupo " + args[2] + " não foi encontrado");
						}
					} else if (args[1].equalsIgnoreCase("remove")) {
						final int id;
						try {
							id = Integer.valueOf(args[2]);
							HashMap<Group, Long> map = player.getGroupById(id);
							if (map != null) {
								Group group = map.keySet().toArray(new Group[] {})[0];
								player.removeGroup(id);
								player.save(DataCategory.ACCOUNT);
								GroupMessage message = new GroupMessage().writeUser(player.getName())
										.writeResponse(sender.getName()).writeGroup(group).writeGroupId(id)
										.writeAdd(false);
								sender.sendMessage("§6§lSETGROUP§f Você removeu o grupo "
										+ group.getTagToUse().getName() + "§f da conta " + player.getName() + "("
										+ player.getUniqueId().toString().replace("-", "") + ")");
								if (t != null) {
									player.updateTags();
									if (BukkitMain.getInstance().getTagManager().currentTag(t, group.getTagToUse())) {
										BukkitMain.getInstance().getTagManager().setTag(t, player.getTags().get(0));
									}
									t.sendMessage("§6§lGRUPO§f O grupo " + group.getTagToUse().getName()
											+ "§f foi §c§lREMOVIDO§f de sua conta!");
									message.writeFindPlayer(false);
								} else {
									message.writeFindPlayer(true);
								}
								WeavenMC.getCommonRedis().getJedis().publish(WeavenMC.GROUP_MANAGEMENT_CHANNEL,
										WeavenMC.getGson().toJson(message));
							} else {
								sender.sendMessage(
										"§6§lGROUPSET§f Não foi encontrado um grupo com este §e§lID§f na conta "
												+ player.getName() + "("
												+ player.getUniqueId().toString().replace("-", "") + ")");
							}
						} catch (NumberFormatException e) {
							sender.sendMessage("§6§lSETGROUP§f O §e§lID§f precisa ser um número!");
						}
					} else {
						sender.sendMessage("§6§lGROUPSET§f Utilize: /group <player> <add, remove> <rank, id> <tempo>");
					}
				}
			} else {
				sender.sendMessage(
						"§6§lSETGROUP§f Não foi possivel criar ou carregar o perfil para este jogador! O comando sera salvo na config.yml.");

				if (BukkitMain.getInstance().getInConfig("setgroup.failed") == null) {
					BukkitMain.getInstance().registerInConfig("setgroup.failed", new ArrayList<String>());
					BukkitMain.getInstance().saveConfig();
				}

				List<String> failedCommands = BukkitMain.getInstance().getConfig().getStringList("setgroup.failed");
				failedCommands.add(
						"/group " + StringUtils.createArgs(0, args, "", false) + " ; sender = " + sender.getName());

				BukkitMain.getInstance().registerInConfig("setgroup.failed", failedCommands);
				BukkitMain.getInstance().saveConfig();
			}
		}
	}

}
