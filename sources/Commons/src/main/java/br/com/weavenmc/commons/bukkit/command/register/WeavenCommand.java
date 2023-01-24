package br.com.weavenmc.commons.bukkit.command.register;

public class WeavenCommand {	

//	private final Set<UUID> cooldown = new HashSet<>();

//	@Command(name = "weaven", aliases = { "copa", "copahg", "cp" }, runAsync = true)
//	public void weaven(BukkitCommandSender sender, String label, String[] args) {
//		if (sender.isPlayer()) {
//			Player p = sender.getPlayer();
//			BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
//			if (!cooldown.contains(p.getUniqueId())) {
//				cooldown.add(p.getUniqueId());
//				try {
//					PreparedStatement preparedStatement = WeavenMC.getCommonMysql().preparedStatement(
//							"SELECT * FROM `vip_test` WHERE `uuid`='" + player.getUniqueId().toString() + "';");
//					ResultSet resultSet = preparedStatement.executeQuery();
//					if (resultSet.next()) {
//						p.sendMessage("§e§lCOPA§f Você já ativou sua tag §eCOPA§f!");
//					} else {
//						PreparedStatement insertStatement = WeavenMC.getCommonMysql().preparedStatement(
//								"INSERT INTO `vip_test` (`uuid`) VALUES (?);");
//						insertStatement.setString(1, player.getUniqueId().toString());
//						insertStatement.execute();
//						insertStatement.close();
//						player.addGroup(Group.COPA, TimeUnit.DAYS.toMillis(365) + System.currentTimeMillis());
//						player.save(DataCategory.ACCOUNT);
//						player.organizeGroups();
//						player.updateTags();
//						if (!BukkitMain.getInstance().getTagManager().currentTag(p, Tag.COPA)) {
//							BukkitMain.getInstance().getTagManager().setTag(p, Tag.COPA);
//						}
//						TitleAPI.setTitle(p, "§e§COPA", "§fVocê ativou sua tag §eCOPA§f!");
//						p.sendMessage(
//								"§e§COPA§f Você ativou sua tag §eCOPA§f!!");
//					}
//					resultSet.close();
//					preparedStatement.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//					p.sendMessage("§6§COPA§f Erro ao tentar efetuar a operaçao, tente novamente mais tarde.");
//				}
//				cooldown.remove(p.getUniqueId());
//			}
//		} else {
//			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
//		}
//	}
}