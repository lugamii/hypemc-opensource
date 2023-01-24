package br.com.weavenmc.commons.bukkit.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.account.Medal;
import br.com.weavenmc.commons.bukkit.api.chat.ChatAPI;
import br.com.weavenmc.commons.bukkit.api.chat.ChatAPI.ChatState;
import br.com.weavenmc.commons.bukkit.chat.ChatEvent;
import br.com.weavenmc.commons.bukkit.chat.ChatHandlerAPI;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.core.clan.Clan;
import br.com.weavenmc.commons.core.permission.Group;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChatEnabled(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
		if (ChatAPI.getInstance().getChatState() == ChatState.DISABLED && !player.isStaffer()) {
			event.setCancelled(true);
			p.sendMessage("§3§lCHAT§f O chat está §c§lDESABILITADO!");
		}
		player = null;
		p = null;
	}

	private final HashMap<UUID, Integer> cooldown = new HashMap<>();

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCooldown(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		Player p = event.getPlayer();
		BukkitPlayer player = BukkitPlayer.getPlayer(p.getUniqueId());
		if (player != null) {
			if (cooldown.containsKey(p.getUniqueId())) {
				String seconds = cooldown.get(p.getUniqueId()) == 1 ? " SEGUNDO" : " SEGUNDOS";
				p.sendMessage("§3§lCHAT §fAguarde §c§l" + cooldown.get(p.getUniqueId()) + seconds
						+ "§f para falar novamente.");
				event.setCancelled(true);
				return;
			}
			if (!player.isStaffer()) {
				cooldown.put(p.getUniqueId(), 3);
			}
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (cooldown.containsKey(player.getUniqueId())) {
					cooldown.put(player.getUniqueId(), cooldown.get(player.getUniqueId()) - 1);
					if (cooldown.get(player.getUniqueId()) == 0) {
						cooldown.remove(player.getUniqueId());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		if (!ChatHandlerAPI.hasHandler()) {
			event.setCancelled(true);
			BukkitPlayer player = BukkitPlayer.getPlayer(event.getPlayer().getUniqueId());
			if (player == null)
				return;
			TextComponent clan = null;
			TextComponent league = null;
			TextComponent medal = null;
			int text = 2;
			Medal currentMedal = BukkitMain.getInstance().getTagManager().getMedal(event.getPlayer());
			if (currentMedal != null) {
				medal = new TextComponent(currentMedal.getColor() + "§l" + currentMedal.getSymbol() + " ");
				medal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new TextComponent[] { new TextComponent("§7Medalha: " + currentMedal.getColor() + "§l"
								+ currentMedal.getSymbol() + " " + currentMedal.name().replace("YIN_YANG", "YING&YANG")) }));
				text += 1;
			}
			if (!player.getClanName().equals("Nenhum")) {
				Clan c = WeavenMC.getClanCommon().getClanFromName(player.getClanName());
				if (c != null) {
					clan = new TextComponent("§7[§8" + c.getAbbreviation() + "§7] ");
					text += 1;
				}
			}
			league = new TextComponent(" §7[" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7]");
			league.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
					player.getLeague().getColor() + player.getLeague().getSymbol() + " " + player.getLeague().name())));
			text += 1;
			TextComponent[] textTo = new TextComponent[text + event.getMessage().split(" ").length];
			String tag = BukkitMain.getInstance().getTagManager().getTag(event.getPlayer()).getPrefix();
			TextComponent account = new TextComponent(tag + event.getPlayer().getName());
			account.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/account " + player.getName()));
			account.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§eClique para mais informações")));
			int i = 0;
			if (medal != null) {
				textTo[i] = medal;
				++i;
			}
			if (clan != null) {
				textTo[i] = clan;
				++i;
			}
			textTo[i] = account;
			++i;
			if (league != null) {
				textTo[i] = league;
				++i;
			}
			textTo[i] = new TextComponent("§f:");
			++i;
			for (String msg : event.getMessage().split(" ")) {
				msg = " " + msg;
				TextComponent text2 = new TextComponent(
						(player.hasGroupPermission(Group.VIP) ? msg.replace("&", "§") : msg));
				List<String> url = extractUrls(msg);
				if (url.size() > 0) {
					text2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.get(0)));
				}
				textTo[i] = text2;
				++i;
			}
			for (Player r : event.getRecipients())
				r.spigot().sendMessage(textTo);
			player = null;
		} else {
			ChatEvent chatEvent = ChatHandlerAPI.callChatEvent(event);
			event.setCancelled(chatEvent.isCancelled());
			if (event.isCancelled())
				return;
			event.setMessage(chatEvent.getMessage());
			event.setFormat(chatEvent.getFormat());
		}
	}

	private Pattern urlFinderPattern = Pattern.compile("((https?):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
			Pattern.CASE_INSENSITIVE);

	public List<String> extractUrls(String text) {
		List<String> containedUrls = new ArrayList<String>();
		Matcher urlMatcher = urlFinderPattern.matcher(text);
		while (urlMatcher.find()) {
			containedUrls.add(urlMatcher.group(1));
		}
		return containedUrls;
	}
}
