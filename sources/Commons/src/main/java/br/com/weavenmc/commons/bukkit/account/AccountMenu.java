package br.com.weavenmc.commons.bukkit.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringTimeUtils;

public class AccountMenu implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClickListener(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getInventory().getName().startsWith("§7Conta")) {
			event.setCancelled(true);
			if (event.getCurrentItem().getType() == Material.PAPER) {

				p.closeInventory();
				openStatus(p, (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId()));
			}
			if (event.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
				p.closeInventory();
				openPreferences(p, (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId()));
			}
		}

		if (event.getInventory().getName().startsWith("§7Status")) {
			event.setCancelled(true);

			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			if (event.getCurrentItem().getType() == Material.ARROW) {
				p.closeInventory();
				open(p, bP);
			}
		}
		if (event.getInventory().getName().startsWith("Vantagens")) {
			event.setCancelled(true);
		}
		if (event.getInventory().getName().startsWith("§7Preferências")) {
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			if (event.getSlot() == 19) {
				p.closeInventory();
				p.chat("/tell " + (bP.isTell() ? "off" : "on"));
			}
			if (event.getSlot() == 20) {
				p.closeInventory();

				bP.setClan(!bP.isClan());
				bP.getData(DataType.CLAN).setValue(!bP.isClan());

				WeavenMC.getCommonRedis().getJedis().set(WeavenMC.CLAN_PREF, bP.getName());

				p.sendMessage(
						"§e§lCLAN §fAgora você " + (bP.isClan() ? "irá" : "não irá") + " receber convites para clan!");
			}

			if (event.getCurrentItem().getType() == Material.ARROW) {
				p.closeInventory();
				open(p, bP);
			}
			event.setCancelled(true);
		}
	}

	protected String getTimerFormat(long millis) {
		long days = TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.DAYS.toMillis(days);

		long hours = TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);

		StringBuilder sb = new StringBuilder();
		if (days != 0L)
			sb.append(days + (days == 1 ? " dia, " : " dias, "));
		if (hours != 0L && hours != 0)
			sb.append(hours + (hours == 1 ? " hora, " : " horas, "));
		if (minutes != 0L && minutes != 0)
			sb.append(minutes + (minutes == 1 ? " minuto, " : " minutos, "));
		if (seconds != 0L && seconds != 0)
			sb.append(seconds + (seconds == 1 ? " segundo" : " segundos"));

		return sb.toString().endsWith(", ") ? sb.toString().substring(0, sb.toString().length() - 2) : sb.toString();
	}

	public static String getDate(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(time);
	}

//	private static String translateNumberToM(int m) {
//		if (m == 1)
//			return "janeiro";
//		if (m == 2)
//			return "fevereiro";
//		if (m == 3)
//			return "março";
//		if (m == 4)
//			return "abril";
//		if (m == 5)
//			return "maio";
//		if (m == 6)
//			return "junho";
//		if (m == 7)
//			return "julho";
//		if (m == 8)
//			return "agosto";
//		if (m == 9)
//			return "setembro";
//		if (m == 10)
//			return "outubro";
//		if (m == 11)
//			return "novembro";
//		if (m == 12)
//			return "dezembro";
//		return "janeiro";
//	}
//
//	private static String horar(String a) {
//		String[] hrs = a.split(":");
//		return hrs[0] + " horas e " + hrs[1] + " minutos";
//	}
//
//	private static String fullDate(long time) {
//
//		String formated = getDate(time);
//		String[] dates = formated.split("/");
//		String y = dates[2].substring(6);
//
//		String hrs = dates[2].substring(6, 0);
//
//		String local = dates[0] + " de " + translateNumberToM(Integer.valueOf(dates[1])) + y + " às " + horar(hrs);
//
//		return local;
//
//	}
	static long sla = 0;

	public static void open(Player player, BukkitPlayer bP) {
		Inventory menu = Bukkit.createInventory(player, 3 * 9, "§7Conta - " + bP.getName());

		ItemBuilder builder = new ItemBuilder().type(Material.SKULL_ITEM).skin(bP.getName()).durability(3)
				.name("§7" + bP.getName());

		List<String> lore = new ArrayList<String>();

		for (HashMap<Group, Long> map : bP.getRanks()) {
			for (java.util.Map.Entry<Group, Long> entrie : map.entrySet()) {
				Group group = entrie.getKey();
				Long time = entrie.getValue();
				lore.add(group.getTagToUse().getName() + " §f- "
						+ (time > 0 ? StringTimeUtils.formatDifference(time).replace("-", "") : "ETERNO"));
			}
		}

		String ranks = "";
		for (String sla : lore)
			ranks += "\n" + sla;

		builder.lore("§aInformações Pessoais", "§7", "§fRanks: " + ranks, "",
				"§fCadastrado em: §e" + getDate(bP.getFirstLoggedIn()),
				"§fUltimo Login: §e" + getDate(bP.getLastLoggedIn()));
		menu.setItem(10, builder.build());

		builder.type(Material.PAPER).name("§aVer estatísticas").lore("§7Veja seus status em toda rede!");
		menu.setItem(11, builder.build());

		builder.type(Material.REDSTONE_COMPARATOR).name("§aPreferências")
				.lore("§7Desabilite ou habilidade algumas opções.");

		menu.setItem(14, builder.build());

		builder.type(Material.BOOK).name("§aMedalhas").lore("§7Veja suas medalhas.");

		menu.setItem(15, builder.build());

		player.openInventory(menu);
	}

	public static void openVantagens(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Vantagens");
		inventory.setItem(11,
				new ItemBuilder().type(Material.IRON_BLOCK).name("§eVip Blade").lore(
						"Recebe 3 Medalhas (Tóxic, King e Paz e Amor)", "Pode Voar no Lobby",
						"Consegue usar os cosméticos: Todos os chapéus, Todas as partículas", "",
						"§7Mais em §ahttps://hypemc.com.br").build());
		inventory.setItem(13,
				new ItemBuilder().type(Material.GOLD_BLOCK).name("§eVip ").lore(
						"Recebe 3 Medalhas (Tóxic, King e Paz e Amor)", "Pode Voar no Lobby",
						"Consegue usar os cosméticos: Todos os chapéus, Todas as partículas", "",
						"§7Mais em §ahttps://hypemc.com.br").build());
		inventory.setItem(15,
				new ItemBuilder().type(Material.DIAMOND_BLOCK).name("§eVip Blade").lore(
						"Recebe 3 Medalhas (Tóxic, King e Paz e Amor)", "Pode Voar no Lobby",
						"Consegue usar os cosméticos: Todos os chapéus, Todas as partículas", "",
						"§7Mais em §ahttps://hypemc.com.br").build());
	}

	public static void openStatus(Player player, BukkitPlayer bP) {

		Inventory menu = Bukkit.createInventory(player, 3 * 9, "§7Status - " + bP.getName());

		ItemBuilder itemBuilder = new ItemBuilder();

		menu.setItem(12,
				itemBuilder.type(Material.PAPER).name("§aKitPvP")
						.lore("§fKills: §a" + bP.getData(DataType.PVP_KILLS).asInt(),
								"§fKillStreak: §e" + bP.getData(DataType.PVP_KILLSTREAK).asInt(),
								"§fDeaths: §c" + bP.getData(DataType.PVP_DEATHS).asInt())
						.build());
		menu.setItem(13,
				itemBuilder.type(Material.PAPER).name("§aHardcoreGames")
						.lore("§fKills: §a" + bP.getData(DataType.HG_KILLS).asInt(),
								"§fWins: §e" + bP.getData(DataType.HG_WINS).asInt(),
								"§fDeaths: §c" + bP.getData(DataType.HG_DEATHS).asInt())
						.build());
		menu.setItem(14,
				itemBuilder.type(Material.PAPER).name("§aGladiator")
						.lore("§fWins: §a" + bP.getData(DataType.GLADIATOR_WINS).asInt(),
								"§fWinStreak: §e" + bP.getData(DataType.GLADIATOR_WINSTREAK).asInt(),
								"§fLoses: §c" + bP.getData(DataType.GLADIATOR_LOSES).asInt())
						.build());

		menu.setItem(22, itemBuilder.type(Material.ARROW).name("§cVoltar").lore("§7Volte ao menu inicial!").build());
		player.openInventory(menu);

	}

	public static void openPreferences(Player player, BukkitPlayer bP) {

		Inventory menu = Bukkit.createInventory(player, 4 * 9, "§7Preferências - " + bP.getName());

		ItemBuilder itemBuilder = new ItemBuilder();

		menu.setItem(10, itemBuilder.type(Material.PAPER).name("§aMensagens Privadas")
				.lore("§7Receba mensagens privadas.").build());
		menu.setItem(19, itemBuilder.type(Material.INK_SACK).name(bP.isTell() ? "§cDesativar" : "§aAtivar")
				.durability(bP.isTell() ? 10 : 1).build());

		menu.setItem(11, itemBuilder.type(Material.PAPER).name("§aConvite de Clan").lore("§7Receba convites para clan.")
				.build());
		menu.setItem(20, itemBuilder.type(Material.INK_SACK).name(bP.isClan() ? "§cDesativar" : "§aAtivar")
				.durability(bP.isClan() ? 10 : 1).build());

		menu.setItem(31, itemBuilder.type(Material.ARROW).name("§cVoltar").lore("§7Volte ao menu inicial!").build());

		player.openInventory(menu);

	}
}
