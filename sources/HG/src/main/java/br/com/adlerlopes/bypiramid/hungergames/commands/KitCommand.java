package br.com.adlerlopes.bypiramid.hungergames.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.GamerMode;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.Nenhum;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.Utils;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework;
import br.com.weavenmc.commons.core.command.CommandSender;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.ServerType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class KitCommand implements CommandClass {
	private final Set<Combination> combinations;
	private Set<SpecialKit> specialKits;

	public KitCommand() {
		this.combinations = new HashSet<Combination>();
		this.specialKits = new HashSet<SpecialKit>();
		this.combinations.add(new Combination("Stomper", "Grappler"));
		this.combinations.add(new Combination("Stomper", "Phantom"));
		this.combinations.add(new Combination("Stomper", "Launcher"));
		this.combinations.add(new Combination("Viking", "Boxer"));
		this.combinations.add(new Combination("Demoman", "Tank"));
	}

	@CommandFramework.Command(name = "removekit2", aliases = { "removerkit2" }, groupToUse = Group.YOUTUBERPLUS)
	public void removekit2(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 1) {
				commandSender.sendMessage("§4§lREMOVE §fUse: /removekit <player>");
				return;
			}
			final Player removed = Bukkit.getPlayer(args[0]);
			if (removed == null) {
				commandSender.sendMessage("§4§lREMOVE §fO player est\u00e1 §c§lOFFLINE");
				return;
			}
			commandSender.sendMessage("§4§lREMOVE §fVoc\u00ea removeu o kit §c§l"
					+ this.getManager().getGamerManager().getGamer(removed).getKit2().getName() + "§F do player §C§l"
					+ removed.getName());
			this.getManager().getGamerManager().getGamer(removed)
					.setKit2(this.getManager().getKitManager().getKit("Nenhum"));
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "removekit", aliases = { "removerkit" }, groupToUse = Group.YOUTUBERPLUS)
	public void removekit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 1) {
				commandSender.sendMessage("§4§lREMOVE §fUse: /removekit <player>");
				return;
			}
			final Player removed = Bukkit.getPlayer(args[0]);
			if (removed == null) {
				commandSender.sendMessage("§4§lREMOVE §fO player est\u00e1 §c§lOFFLINE");
				return;
			}
			commandSender.sendMessage("§4§lREMOVE §fVoc\u00ea removeu o kit §c§l"
					+ this.getManager().getGamerManager().getGamer(removed).getKit().getName() + "§F do player §C§l"
					+ removed.getName());
			this.getManager().getGamerManager().getGamer(removed)
					.setKit(this.getManager().getKitManager().getKit("Nenhum"));
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "givekit", aliases = { "darkit" }, groupToUse = Group.YOUTUBERPLUS)
	public void givekit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (args.length == 0) {
			this.sendHelp(commandSender);
		} else if (args.length == 1) {
			Bukkit.getScheduler().runTaskAsynchronously((Plugin) this.getManager().getPlugin(),
					(Runnable) new AsyncPermissionSetTask((CommandSender) commandSender, args));
		} else {
			this.sendHelp(commandSender);
		}
	}

	public boolean validString(final String str) {
		return str.matches("[a-zA-Z0-9_]+") && str.length() >= 2 && str.length() <= 6;
	}

	private void sendHelp(final BukkitCommandSender commandSender) {
		commandSender.sendMessage("§3§lGIVEKIT §fUse: /givekit <nick/all> ");
	}

	@CommandFramework.Command(name = "skit", aliases = { "specialkit" }, groupToUse = Group.YOUTUBERPLUS)
	public void skit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			final Player player = commandSender.getPlayer();
			if (args.length <= 1) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("lista")) {
						player.sendMessage("§3§lSKIT§f Lista de todos os kits criados:");
						for (final SpecialKit kit : this.specialKits) {
							player.sendMessage("§7- §b" + kit.getName());
						}
					} else {
						player.sendMessage("§3§lSKIT§f Utilize: §3§l/skit§f <aplicar|criar> <nome> <raio|all>");
					}
				} else {
					player.sendMessage("§3§lSKIT§f Utilize: §3§l/skit§f <aplicar|criar> <nome> <raio|all>");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("criar")) {
					final SpecialKit kit = this.getKit(args[1]);
					if (kit == null) {
						final SpecialKit k = new SpecialKit(args[1], player.getInventory().getContents(),
								player.getInventory().getArmorContents());
						this.createKit(k);
						player.sendMessage("§3§lSKIT§f Voc\u00ea §3§lCRIOU§f o skit §b§l" + k.getName());
					} else {
						player.sendMessage("§3§lSKIT§f O skit §b§l" + kit.getName() + "§f j\u00e1 existe!");
					}
				} else if (args[0].equalsIgnoreCase("aplicar") || args[0].equalsIgnoreCase("apply")) {
					player.sendMessage(
							"§3§lSKIT§f Sintaxe incompleta: §3§l/skit <" + args[0] + "> <" + args[1] + ">§f <raio|all>");
				} else {
					player.sendMessage("§3§lSKIT§f Utilize: §3§l/skit§f <aplicar|criar> <nome> <raio|all>");
				}
			} else if (args[0].equalsIgnoreCase("aplicar") || args[0].equalsIgnoreCase("apply")) {
				final SpecialKit i = this.getKit(args[1]);
				if (i != null) {
					if (args[2].equalsIgnoreCase("all")) {
						Bukkit.getOnlinePlayers().forEach(find -> {
							find.getInventory().setContents(i.getContents());
							find.getInventory().setArmorContents(i.getArmourContents());

							find.sendMessage("§3§lSKIT§f Voc\u00ea recebeu o kit §b§l" + i.getName());
							return;
						});

						player.sendMessage(
								"§3§lSKIT§f Voc\u00ea §3§lAPLICOU§f o kit §b§l" + i.getName() + "§f para §3§lTODOS§f!");
						return;
					}
					try {
						final int radius = Integer.valueOf(args[2]);
						if (radius > 1000) {
							player.sendMessage("§3§lSKIT§f O raio n\u00e3o pode ser maior que 1000.");
							return;
						}
						final ArrayList<Player> inRadius = new ArrayList<Player>();
						final Location loc = player.getLocation();
						for (final Player o : Bukkit.getOnlinePlayers()) {
							if (o.getLocation().distance(loc) > radius) {
								continue;
							}
							if (o.getUniqueId() == player.getUniqueId()) {
								continue;
							}
							final Gamer gamer = this.getManager().getGamerManager().getGamer(o);
							if (gamer.isSpectating()) {
								continue;
							}
							if (AdminMode.getInstance().isAdmin(o)) {
								continue;
							}
							inRadius.add(o);
						}
						final int finded = inRadius.size();
						if (finded > 0) {
							for (final Player find : inRadius) {
								find.getInventory().setContents(i.getContents());
								find.getInventory().setArmorContents(i.getArmourContents());
								find.sendMessage("§3§lSKIT§f Voc\u00ea recebeu o kit §b§l" + i.getName());
							}
							player.sendMessage("§3§lSKIT§f Voc\u00ea §3§lAPLICOU§f o kit §b§l" + i.getName()
									+ "§f em um raio de §b§l" + radius + "§f onde §b§l" + finded
									+ "§f jogadores receberam o kit.");
							return;
						}
						player.sendMessage("§3§lSKIT§f Nenhum jogador foi encontrado neste raio.");
						return;
					} catch (NumberFormatException ex) {
						player.sendMessage("§3§lSKIT§f O raio precisa ser um valor v\u00e1lido!");
						return;
					}
				}
				player.sendMessage("§3§lSKIT§f O kit §b§l" + args[1] + "§f n\u00e3o foi encontrado!");
			} else {
				player.sendMessage("§3§lSKIT§f Utilize: §3§l/skit§f <aplicar|criar> <nome> <raio>");
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "forcekit2", groupToUse = Group.YOUTUBERPLUS)
	public void forcekit2(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 2) {
				commandSender.sendMessage("§d§lFORCEKIT§f Use: /forcekit2 <player/all> <kit>");
				return;
			}
			boolean all = false;
			Player player = null;
			if (args[0].equalsIgnoreCase("all")) {
				all = true;
			} else {
				player = Bukkit.getPlayer(args[0]);
			}
			if (player == null && !all) {
				commandSender.sendMessage("§d§lFORCEKIT§f O sujeito est\u00e1 §5§OFFLINE");
				return;
			}
			final Kit kit = this.getManager().getKitManager().getKit(args[1]);
			if (kit == null) {
				commandSender.sendMessage("§d§lFORCEKIT§f O kit §5§l" + args[1] + "§f n\u00e3o existe!");
				return;
			}
			if (all) {
				for (final Gamer players : this.getManager().getGamerManager().getAliveGamers()) {
					players.setKit2(kit);
					players.getKit2().give(players.getPlayer());
				}
			} else {
				this.getManager().getGamerManager().getGamer(player).setKit2(kit);
				this.getManager().getGamerManager().getGamer(player).getKit2().give(player);
			}
			if (args[0].equalsIgnoreCase("all")) {
				commandSender.sendMessage(
						"§d§lFORCEKIT§f Voc\u00ea setou o kit §5§l" + kit.getName() + "§f para os §5§lPLAYERS");
			} else {
				commandSender.sendMessage("§d§lFORCEKIT§f Voc§ setou o kit §5§l" + kit.getName()
						+ "§f para o player §5§l" + player.getName());
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "forcekit", groupToUse = Group.YOUTUBERPLUS)
	public void forcekit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 2) {
				commandSender.sendMessage("§d§lFORCEKIT§f Use: /forcekit <player/all> <kit>");
				return;
			}
			boolean all = false;
			Player player = null;
			if (args[0].equalsIgnoreCase("all")) {
				all = true;
			} else {
				player = Bukkit.getPlayer(args[0]);
			}
			if (player == null && !all) {
				commandSender.sendMessage("§d§lFORCEKIT§f O sujeito est\u00e1 §5§OFFLINE");
				return;
			}
			final Kit kit = this.getManager().getKitManager().getKit(args[1]);
			if (kit == null) {
				commandSender.sendMessage("§d§lFORCEKIT§f O kit §5§l" + args[1] + "§f n\u00e3o existe!");
				return;
			}
			if (all) {
				for (final Gamer players : this.getManager().getGamerManager().getAliveGamers()) {
					players.setKit(kit);
					players.getKit().give(players.getPlayer());
				}
			} else {
				this.getManager().getGamerManager().getGamer(player).setKit(kit);
				this.getManager().getGamerManager().getGamer(player).getKit().give(player);
			}
			if (args[0].equalsIgnoreCase("all")) {
				commandSender.sendMessage(
						"§d§lFORCEKIT§f Voc\u00ea setou o kit §5§l" + kit.getName() + "§f para os §5§lPLAYERS");
			} else {
				commandSender.sendMessage("§d§lFORCEKIT§f Voc\u00ea setou o kit §5§l" + kit.getName()
						+ "§f para o player §5§l" + player.getName());
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "kit")
	public void kit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			final Player player = commandSender.getPlayer();
			final List<String> kits = new ArrayList<String>();
			for (final Kit kit : this.getManager().getKitManager().getPlayerKits(player)) {
				kits.add(kit.getName());
			}
			if (args.length == 0) {
				final TextComponent tagsMessage = new TextComponent("§eVoc\u00ea tem §9§n"
						+ this.getManager().getKitManager().getPlayerKits(player).size() + "§e kits: ");
				for (int i = 0; i < kits.size(); ++i) {
					final String kit2 = kits.get(i);
					tagsMessage.addExtra((i == 0) ? "" : ", ");
					tagsMessage.addExtra(this.buildKitComponent(this.getManager().getKitManager().getKit(kit2)));
				}
				player.spigot().sendMessage((BaseComponent) tagsMessage);
				player.sendMessage("§eMais kits em: §9loja.mc-hype.com.br");
				player.sendMessage("§7§nDICA: §7Escolha o kit clicando no chat");
				return;
			}
			final String name = args[0];
			final Kit kit3 = this.isInteger(name) ? this.getManager().getKitManager().getKit(Integer.valueOf(name))
					: this.getManager().getKitManager().getKit(name);
			if (kit3 == null) {
				commandSender.sendMessage("§b§lKITS §fO kit §3§l" + name + "§f n\u00e3o existe!");
				return;
			}
			final String kitname = this.getManager().getGamerManager().getGamer(player).getKit2().getName();
			for (final Combination combination : this.combinations) {
				if (!combination.isCombination(kit3.getName(), kitname)) {
					continue;
				}
				commandSender.sendMessage("§b§lKITS§f Voc\u00ea n\u00e3o pode usar esta combina\u00e7\u00e3o!");
				return;
			}
			if (!kit3.isActive()) {
				commandSender.sendMessage("§b§lKITS §fO kit §3§l" + kit3.getName() + "§f não est\u00e1 ativado!");
				return;
			}
			final Gamer gamer = this.getManager().getGamerManager().getGamer(player);
			if (gamer.getKit().getName().equalsIgnoreCase(kit3.getName())) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit().getName() + "!");
				return;
			}
			if (gamer.getKit2().getName().equalsIgnoreCase(kit3.getName())) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit2().getName() + "!");
				return;
			}
			if (gamer.getKit().getName().equalsIgnoreCase(gamer.getKit2().getName())
					&& !(gamer.getKit() instanceof Nenhum) && !(gamer.getKit2() instanceof Nenhum)) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit2().getName() + "!");
				return;
			}
			if (!this.getManager().getGameManager().isPreGame()) {
				if (!gamer.getMode().equals(GamerMode.ALIVE)) {
					commandSender.sendMessage(
							"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 mais participando da partida, portanto Voc\u00ea nao pode pegar kits!");
					return;
				}
				if (!gamer.getKit().getName().equals("Nenhum")) {
					commandSender.sendMessage("§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 com um kit!");
					return;
				}
				if (this.getManager().getGameManager().getGameTime() > 300 && !player.hasPermission("hunger.cmd.kit")) {
					commandSender.sendMessage("§b§lKITS §fVoce nao pode pegar kit agora!");
					return;
				}
				gamer.setKit(kit3);
				gamer.getKit().give(player);
				player.sendMessage("§b§lKITS §fVoce selecionou o kit §3§l" + kit3.getName());
			} else {
				player.sendMessage("§b§lKITS §fVoce selecionou o kit §3§l" + kit3.getName());
				gamer.setKit(kit3);
			}
		}
	}

	@CommandFramework.Command(name = "kit2")
	public void kit2(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (WeavenMC.getServerType() == ServerType.TOURNAMENT)
			return;
		if (commandSender.isPlayer()) {
			final Player player = commandSender.getPlayer();
			final List<String> kits = new ArrayList<String>();
			for (final Kit kit : this.getManager().getKitManager().getPlayerKits(player)) {
				kits.add(kit.getName());
			}
			if (!this.getManager().isDoubleKit()) {
				return;
			}
			if (args.length == 0) {
				final TextComponent tagsMessage = new TextComponent("§eVoc\u00ea tem §9§n"
						+ this.getManager().getKitManager().getPlayerKits(player).size() + "§e kits: ");
				for (int i = 0; i < kits.size(); ++i) {
					final String kit2 = kits.get(i);
					tagsMessage.addExtra((i == 0) ? "" : ", ");
					tagsMessage.addExtra(this.buildKit2Component(this.getManager().getKitManager().getKit(kit2)));
				}
				player.spigot().sendMessage((BaseComponent) tagsMessage);
				player.sendMessage("§eMais kits em: §9loja.mc-hype.com.br");
				player.sendMessage("§7§nDICA: §7Escolha o kit clicando no chat");
				return;
			}
			final String name = args[0];
			final Kit kit3 = this.isInteger(name) ? this.getManager().getKitManager().getKit(Integer.valueOf(name))
					: this.getManager().getKitManager().getKit(name);
			if (kit3 == null) {
				commandSender.sendMessage("§b§lKITS §fO kit §3§l" + name + "§f n\u00e3o existe!");
				return;
			}
			final String kitname = this.getManager().getGamerManager().getGamer(player).getKit().getName();
			for (final Combination combination : this.combinations) {
				if (!combination.isCombination(kit3.getName(), kitname)) {
					continue;
				}
				commandSender.sendMessage("§b§lKITS§f Voc\u00ea n\u00e3o pode usar esta combina\u00e7\u00e3o!");
				return;
			}
			if (!kit3.isActive()) {
				commandSender.sendMessage("§b§lKITS §fO kit §3§l" + kit3.getName() + "§f n\u00e3o est\u00e1 ativado!");
				return;
			}
			if (!this.hasKit(kit3, player)) {
				commandSender.sendMessage("§b§lKITS §fVoc\u00ea n\u00e3o possui o kit §3§l" + kit3.getName()
						+ "§f, compre-o em nossa loja §fwww.mc-hype.com.br");
				return;
			}
			final Gamer gamer = this.getManager().getGamerManager().getGamer(player);
			if (gamer.getKit().getName().equalsIgnoreCase(kit3.getName())) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit().getName() + "!");
				return;
			}
			if (gamer.getKit2().getName().equalsIgnoreCase(kit3.getName())) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit2().getName() + "!");
				return;
			}
			if (gamer.getKit().getName().equalsIgnoreCase(gamer.getKit2().getName())
					&& !(gamer.getKit() instanceof Nenhum) && !(gamer.getKit2() instanceof Nenhum)) {
				commandSender.sendMessage(
						"§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 utilizando o kit " + gamer.getKit2().getName() + "!");
				return;
			}
			if (!this.getManager().getGameManager().isPreGame()) {
				if (!gamer.getMode().equals(GamerMode.ALIVE)) {
					commandSender.sendMessage(
							"§b§lKITS §fVoc\u00ea n\u00e3o est\u00e1 mais participando da partida, portanto Voc\u00ea n\u00e3o pode pegar kits!");
					return;
				}
				if (!gamer.getKit2().getName().equals("Nenhum")) {
					commandSender.sendMessage("§b§lKITS §fVoc\u00ea j\u00e1 est\u00e1 com um kit!");
					return;
				}
				if (this.getManager().getGameManager().getGameTime() > 300
						&& !player.hasPermission("hunger.cmd.kit5m")) {
					commandSender.sendMessage("§b§lKITS §fVoc\u00ea n\u00e3o pode pegar kit agora!");
					return;
				}
				gamer.setKit2(kit3);
				gamer.getKit2().give(player);
				player.sendMessage("§b§lKITS §fVoc\u00ea selecionou o kit §3§l" + kit3.getName());
			} else {
				player.sendMessage("§b§lKITS §fVoc\u00ea selecionou o kit §3§l" + kit3.getName());
				gamer.setKit2(kit3);
			}
		}
	}

	public boolean hasKit(final Kit kit, final Player player) {

		if (kit.isFree()) {
			return true;
		}
		if (player.hasPermission("hgkit." + kit.getName().toLowerCase())
				|| player.hasPermission("hgkit2." + kit.getName().toLowerCase())) {
			return true;
		}
		final BukkitPlayer bP = BukkitPlayer.getPlayer(player.getUniqueId());
		return bP.getGroup().getId() >= Group.VIP.getId()
				|| this.getManager().getGamerManager().getGamer(player).isWinner()
				|| bP.getPermissions().contains("hgkit." + kit.getName().toLowerCase());
	}

	public void togglekit2(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 2) {
				commandSender.sendMessage("§a§lTOGGLEKIT §fUse: /togglekit2 <kit/all> <on/off>");
				return;
			}
			final Kit kit = this.getManager().getKitManager().getKit(args[0]);
			if (!args[0].equalsIgnoreCase("all") && kit == null) {
				commandSender.sendMessage("§a§lTOGGLEKIT §fO kit §a" + args[0] + "§f nao existe!");
				return;
			}
			final boolean active = args[1].equalsIgnoreCase("on");
			if (args[0].equalsIgnoreCase("all")) {
				for (final Kit kits : this.getManager().getKitManager().getKits()) {
					this.getManager().getKitManager().getKit(kits.getName()).setActive(active);
				}
				if (!active) {
					for (final Gamer gamers : this.getManager().getGamerManager().getGamers().values()) {
						gamers.setKit2(this.getManager().getKitManager().getKit("Nenhum"));
					}
				}
				Bukkit.broadcastMessage("§7Todos os kits (2): "
						+ (active ? "§aativados".toLowerCase() : "§cdesativados".toLowerCase()));
			} else {
				if (!active) {
					for (final Gamer gamers : this.getManager().getGamerManager().getGamers().values()) {
						if (gamers.getKit2().getName().equalsIgnoreCase(kit.getName())) {
							gamers.setKit2(this.getManager().getKitManager().getKit("Nenhum"));
						}
					}
				}
				this.getManager().getKitManager().getKit(kit.getName()).setActive(active);
				Bukkit.broadcastMessage("§7Kit (2) " + kit.getName() + ": "
						+ (active ? "§aativado".toUpperCase() : "§cdesativado".toUpperCase()));
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@CommandFramework.Command(name = "togglekit", groupToUse = Group.YOUTUBERPLUS)
	public void togglekit(final BukkitCommandSender commandSender, final String label, final String[] args) {
		if (commandSender.isPlayer()) {
			if (args.length != 2) {
				commandSender.sendMessage("§a§lTOGGLEKIT §fUse: /togglekit <kit/all> <on/off>");
				return;
			}
			final Kit kit = this.getManager().getKitManager().getKit(args[0]);
			if (!args[0].equalsIgnoreCase("all") && kit == null) {
				commandSender.sendMessage("§a§lTOGGLEKIT §fO kit §a" + args[0] + "§f n\u00e3o existe!");
				return;
			}
			final boolean active = args[1].equalsIgnoreCase("on");
			if (args[0].equalsIgnoreCase("all")) {
				for (final Kit kits : this.getManager().getKitManager().getKits()) {
					this.getManager().getKitManager().getKit(kits.getName()).setActive(active);
				}
				if (!active) {
					for (final Gamer gamers : this.getManager().getGamerManager().getGamers().values()) {
						gamers.setKit(this.getManager().getKitManager().getKit("Nenhum"));
						gamers.setKit2(this.getManager().getKitManager().getKit("Nenhum"));
					}
				}
				Bukkit.broadcastMessage(
						"§7Todos os kits: " + (active ? "§aativados".toLowerCase() : "§cdesativados".toLowerCase()));
			} else {
				if (!active) {
					for (final Gamer gamers : this.getManager().getGamerManager().getGamers().values()) {
						if (gamers.getKit().getName().equalsIgnoreCase(kit.getName())) {
							gamers.setKit(this.getManager().getKitManager().getKit("Nenhum"));
							gamers.setKit2(this.getManager().getKitManager().getKit("Nenhum"));
						}
					}
				}
				this.getManager().getKitManager().getKit(kit.getName()).setActive(active);
				Bukkit.broadcastMessage("§7Kit " + kit.getName() + ": "
						+ (active ? "§aativado".toUpperCase() : "§cdesativado".toUpperCase()));
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	public boolean isInteger(final String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public Manager getManager() {
		return HungerGames.getManager();
	}

	private BaseComponent buildKit2Component(final Kit kit) {
		final BaseComponent baseComponent = (BaseComponent) new TextComponent(
				String.valueOf(kit.isActive() ? "§e" : "§c") + kit.getName());
		final BaseComponent descComponent = (BaseComponent) new TextComponent("§9Informa\u00e7\u00f5es:");
		descComponent.addExtra("\n");
		for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
			descComponent.addExtra(String.valueOf(lore) + "\n");
		}
		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { descComponent, (BaseComponent) new TextComponent("\n"),
						(BaseComponent) new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kit2 " + kit.getName()));
		return baseComponent;
	}

	private BaseComponent buildKitComponent(final Kit kit) {
		final BaseComponent baseComponent = (BaseComponent) new TextComponent(
				String.valueOf(kit.isActive() ? "§e" : "§c") + kit.getName());
		final BaseComponent descComponent = (BaseComponent) new TextComponent("§9Informa\u00e7\u00f5es:");
		descComponent.addExtra("\n");
		for (final String lore : Utils.getFormattedLore(kit.getDescription())) {
			descComponent.addExtra(String.valueOf(lore) + "\n");
		}
		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { descComponent, (BaseComponent) new TextComponent("\n"),
						(BaseComponent) new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kit " + kit.getName()));
		return baseComponent;
	}

	private void createKit(final SpecialKit kit) {
		this.specialKits.add(kit);
	}

	private SpecialKit getKit(final String name) {
		for (final SpecialKit kit : this.specialKits) {
			if (!kit.getName().equals(name)) {
				continue;
			}
			return kit;
		}
		return null;
	}

	private final class AsyncPermissionSetTask implements Runnable {
		private final CommandSender commandSender;
		private final String[] args;

		private AsyncPermissionSetTask(final CommandSender sender, final String[] args) {
			this.commandSender = sender;
			this.args = args;
		}

		@Override
		public void run() {
			final String name = this.args[0];
			Player suject = null;
			if (this.args[0].equalsIgnoreCase("all")) {
				for (final Player all : Bukkit.getOnlinePlayers()) {
					final Gamer gamer = KitCommand.this.getManager().getGamerManager().getGamer(all);
					if (gamer.isAlive()) {
						all.getInventory().addItem(gamer.getKit().getItems());
					}
				}
				((Player) this.commandSender).sendMessage("§3§lGIVEKIT §fFoi adicionado ao PLAYERS");
			} else {
				if (Bukkit.getPlayer(name) != null) {
					suject = Bukkit.getPlayer(name);
				}
				final Gamer gamer2 = KitCommand.this.getManager().getGamerManager().getGamer(suject);
				if (!gamer2.isAlive()) {
					((Player) this.commandSender).sendMessage("§3§lGIVEKIT §fO PLAYER nao esta vivo!");
					return;
				}
				suject.getInventory().addItem(gamer2.getKit().getItems());
				((Player) this.commandSender).sendMessage("§3§lGIVEKIT §fFoi adicionado ao PLAYER");
			}
		}
	}

	private class SpecialKit {
		private String name;
		private ItemStack[] contents;
		private ItemStack[] armourContents;

		public SpecialKit(final String name, final ItemStack[] contents, final ItemStack[] armourContents) {
			this.name = name;
			this.contents = contents;
			this.armourContents = armourContents;
		}

		public String getName() {
			return this.name;
		}

		public ItemStack[] getContents() {
			return this.contents;
		}

		public ItemStack[] getArmourContents() {
			return this.armourContents;
		}
	}

	private class Combination {
		private String name1;
		private String name2;

		public Combination(final String name1, final String name2) {
			this.name1 = name1;
			this.name2 = name2;
		}

		public boolean isCombination(final String a, final String b) {
			return (this.name1.equalsIgnoreCase(a) && this.name2.equalsIgnoreCase(b))
					|| (this.name2.equalsIgnoreCase(a) && this.name1.equalsIgnoreCase(b));
		}
	}
}
