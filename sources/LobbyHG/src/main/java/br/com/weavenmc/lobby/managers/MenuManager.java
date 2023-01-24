package br.com.weavenmc.lobby.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.weavenmc.commons.bukkit.messenger.NetworkManager;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.server.HardcoreGames;
import br.com.weavenmc.commons.core.server.HardcoreGames.GameState;
import br.com.weavenmc.commons.core.server.NetworkServer;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.Management;
import br.com.weavenmc.lobby.managers.MenuManager.MenuType;

public class MenuManager extends Management implements Listener {

	private HashMap<MenuType, Inventory> MENUS = new HashMap<>();

	public MenuManager(Lobby plugin) {
		super(plugin);
	}

	public Inventory getMenu(MenuType menuType) {
		return MENUS.get(menuType);
	}

	public void updateCopa() {
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
		item.setDurability((short) 5);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b§lTORNEIO COPAHG");

		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add("§7Olá caro jogador, nós da rede HypeMC");
		lore.add("§7iremos realizar um torneio valendo um mouse");
		lore.add("§7no valor de §a180,00 R$ §7no qual a marca é");
		lore.add("");
		lore.add("§7Mouse Logitech Gamer G403.");
		lore.add("");
		lore.add("§7Para você participar desse §1§lTORNEIO§7 basta");
		lore.add("§7adqurir aqui neste NPC o ingresso.");
		lore.add("");
		lore.add("§fValor: §645 mil coins.");
		lore.add("§eClique para comprar.");

		meta.setLore(lore);

		item.setItemMeta(meta);

		MENUS.get(MenuType.COPA_MENU).setItem(13, item);
	}

	@Override
	public void enable() {
		MENUS.put(MenuType.COPA_MENU, Bukkit.createInventory(null, 3 * 9, "§7§nCopa"));
		updateCopa();

		int onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.LOBBY);
		Inventory menu = Bukkit.createInventory(null, 3 * 9, "§7§nEscolha um Modo de Jogo");
		ItemBuilder builder;

		builder = new ItemBuilder().name("§6Lobby Principal").type(Material.BOOKSHELF).lore("§eClique para entrar!");
		menu.setItem(10, builder.build());
		//
		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.PVP_FULLIRON);
		builder = new ItemBuilder().name("§aKitPvP").type(Material.DIAMOND_SWORD).lore("§8Competitivo.", "§7",
				"§7Treine com seus amigos um modo", "§7de jogo competitivo cheio de aventuras",
				"§7para todos os jogadores de nossa rede", "§7batalha em warps, tire 1v1 com seu colega",
				"§7batalha em warps, tire 1v1 com seu colega", "§7e ganhe recompensas por estar treinando!", "§7",
				"§a" + onlineCount + "§7 jogadores conectados.", "§aClique para entrar nesse servidor.");
		menu.setItem(12, builder.build());
		//

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.DOUBLEKITHG)
				+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.HG_LOBBY);
		builder = new ItemBuilder().name("§aHardcoreGames").glow().type(Material.MUSHROOM_SOUP).lore("§8Competitivo.",
				"", "§7Esse modo de jogo é o mais competitivo", "§7de todos os tempos! aqui é um battle royale",
				"§7misturado com muitas coisas, entre 80 jogadores", "§7eaí? vai encarar essa batalha?", "",
				"§a" + onlineCount + " §7jogadores conecetados", "§aClique para entrar nesse servidor.");
		menu.setItem(13, builder.build());

		//

		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.GLADIATOR);
		builder = new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE).lore("§8Competitivo.", "",
				"§7Tire 1v1 com seu amigo", "§7no modo GLADIATOR, com itens prontos já",
				"§7para desafiar seu amigo e ser o top 1", "§7desse minigame e chegar ao top 1 global de todos",
				"§7os tempos! venha jogar com seu amigo agora!", "", "§a" + onlineCount + " §7jogadores conectados.",
				"§aClique para entrar nesse servidor.");
		menu.setItem(14, builder.build());
		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS);
		builder = new ItemBuilder().name("§aSkyWars").type(Material.ARROW).lore("§8MiniGame.", "",
				"§7Você tem medo de altura? Então este jogo não é para você!", "§7no Sky Wars você deve eliminar seus",
				"§7adversários com ", "§7a ajuda de diversos Kits e Habilidades.", "",
				"§a" + onlineCount + " §7jogadores conectados.", "§aClique para entrar nesse servidor.");
		menu.setItem(15, builder.build());

//		onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.TOURNAMENT);
//		builder = new ItemBuilder().name("§9§lTorneio").type(Material.MAGMA_CREAM).lore(
//				"§7Servidor reservado para eventos.", "", "§3§l" + onlineCount + "§7 jogadores conectados.", "",
//				"§b§lClique para conectar.");
//		menu.setItem(16, builder.build());

		MENUS.put(MenuType.GAMES_MODE, menu);// games mode end

		menu = Bukkit.createInventory(null, 36, "§7§nServidores do HypeHG");// hunger games start
		NetworkManager networkManager = BukkitMain.getInstance().getNetworkManager();

		ItemBuilder itemBuilder = null;
		NetworkServer server;
		for (int i = 1; i < 11; i++) {
			server = networkManager.getServer("hg-a" + i);
			if (server != null) {
				if (server instanceof HardcoreGames) {
					HardcoreGames hg = (HardcoreGames) server;

					if (hg.getLastData() >= System.currentTimeMillis()) {
						if (hg.getState() == GameState.WAITING) {
							itemBuilder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§aDoubleKit #" + i).lore(
									"", "§aAguardando jogadores",
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}
						if (hg.getState() == GameState.PREGAME) {
							itemBuilder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§aDoubleKit #" + i).lore(
									"", "§aIniciando em " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}

						if (hg.getState() == GameState.INVINCIBILITY) {
							itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
									"§eInvencibildade: " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}

						if (hg.getState() == GameState.GAMETIME) {
							itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
									"§cEm jogo: " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}
					}
					if (itemBuilder == null) {
						WeavenMC.debug("nulo");
						itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
								"§cEsta sala está indisponível no momento,", "§ctente novamente mais tarde!", "");

					}
				}
				menu.setItem(slots[i], itemBuilder.build());
				itemBuilder = null;
			}
		}
		menu.setItem(31, new ItemBuilder().type(Material.EYE_OF_ENDER).name("§ePartida Rápida").build());
		menu.setItem(35, new ItemBuilder().type(Material.ARROW).name("§cVoltar").build());
		MENUS.put(MenuType.DOUBLEKITHG, menu);

//		menu = Bukkit.createInventory(null, 36, "§7§nServidores de §fDoubleKit");
//		for (int i = 1; i < 11; i++) {
//			NetworkServer server = networkManager.getServer("ta" + i);
//			if (server != null) {
//				if (server instanceof HardcoreGames) {
//					HardcoreGames hg = (HardcoreGames) server;
//					if (hg.getLastData() >= System.currentTimeMillis()) {
//						if (hg.getState() == GameState.WAITING) {
//							builder = new ItemBuilder().name("§9§l> §a§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.BOWL).amount(hg.getPlayers())
//									.lore("§7Aguardando §a§ljogadores§7 para iniciar!",
//											"§7Precisa-se de mais §a§l" + hg.getPlayersLeft() + "§7 jogadores!", "",
//											"§3§l" + hg.getPlayers() + "§7 jogadores conectados", "",
//											"§b§lClique para conectar.")
//								);
//						} else if (hg.getState() == GameState.PREGAME) {
//							builder = new ItemBuilder().name("§9§l> §a§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.BOWL).amount(hg.getPlayers())
//									.lore("§7A partida §a§linicia§7 em §a§l" + getTimeFormat(hg.getTime()), "",
//											"§3§l" + hg.getPlayers() + "§7 jogadores conectados", "",
//											"§b§lClique para conectar.")
//									;
//						} else if (hg.getState() == GameState.INVINCIBILITY) {
//							builder = new ItemBuilder().name("§9§l> §e§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.INK_SACK).amount(hg.getPlayers())
//									.lore("§7A §e§linvencibilidade§7 acaba em §e§l" + getTimeFormat(hg.getTime()), "",
//											"§3§l" + hg.getPlayers() + "§7 jogadores conectados", "",
//											"§b§lClique para conectar.")
//									.durability(11);
//						} else if (hg.getState() == GameState.GAMETIME) {
//							builder = new ItemBuilder().name("§9§l> §6§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.INK_SACK).amount(hg.getPlayers())
//									.lore("§7A partida está em §6§lprogresso§7!",
//											"§7A partida está em §6§l" + getTimeFormat(hg.getTime()), "",
//											"§3§l" + hg.getPlayers() + "§7 jogadores conectados", "",
//											"§b§lClique para conectar.")
//									.durability(14);
//						} else if (hg.getState() == GameState.ENDING) {
//							builder = new ItemBuilder().name("§9§l> §2§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.INK_SACK).amount(hg.getPlayers())
//									.lore("§7A partida está §2§lfinalizando§7!",
//											"§7O time vencedor foi §2§l" + hg.getWinner(), "",
//											"§3§l" + hg.getPlayers() + "§7 jogadores conectados", "",
//											"§b§lClique para conectar.")
//									.durability(2);
//						} else {
//							builder = new ItemBuilder().name("§9§l> §c§lta" + i + ".mc-hype.com §9§l<")
//									.type(Material.INK_SACK).lore("§7Aguardando o envio de §c§linformaçoes§7...")
//									.durability(1);
//						}
//					} else {
//						builder = new ItemBuilder().name("§9§l> §c§lta" + i + ".mc-hype.com §9§l<")
//								.type(Material.INK_SACK).lore("§7Aguardando o envio de §c§linformaçoes§7...")
//								.durability(1);
//					}
//				} else {
//					builder = new ItemBuilder().name("§9§l> §c§lta" + i + ".mc-hype.com §9§l<")
//							.type(Material.INK_SACK).lore("§7Aguardando o envio de §c§linformaçoes§7...").durability(1);
//				}
//			} else {
//				builder = new ItemBuilder().name("§9§l> §c§lta" + i + ".mc-hype.com §9§l<").type(Material.INK_SACK)
//						.lore("§7Aguardando o envio de §c§linformaçoes§7...").durability(1);
//			}
//			menu.setItem(slots[i], builder.build());
//		}
//		MENUS.put(MenuType.DOUBLEKITHG, menu);

		menu = Bukkit.createInventory(null, 3 * 9, "§7Lobbys");
		int index = 11;
		builder = new ItemBuilder().name("§aLobby principal #1").lore("", "§fJogadores §a0/80").type(Material.INK_SACK)
				.durability(10);
		menu.setItem(index, builder.build());
		index++;

		MENUS.put(MenuType.LOBBYS, menu);

		registerListener(this);
	}

	public String getTimeFormat(int time) {
		if (time >= 3600) {
			int hours = (time / 3600), minutes = (time % 3600) / 60, seconds = (time % 3600) % 60;
			return (hours == 1 ? hours + " hora" : hours + " horas") + " "
					+ (minutes == 1 ? minutes + " minuto" : minutes + " minutos")
					+ (seconds <= 0 ? "" : " e " + (seconds == 1 ? seconds + " segundo" : seconds + " segundos"));
		} else if (time < 60) {
			return (time == 1 ? time + " segundo" : time + " segundos");
		} else {
			int minutes = (time / 60), seconds = (time % 60);
			return (minutes == 1 ? minutes + " minuto" : minutes + " minutos")
					+ (seconds <= 0 ? "" : " e " + (seconds == 1 ? seconds + " segundo" : seconds + " segundos"));
		}
	}

	int[] slots = { 10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24 };

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		update();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			ItemStack current = event.getCurrentItem();

			if (event.getInventory().getName().equals(getMenu(MenuType.COPA_MENU).getName())
					&& event.getCurrentItem() != null) {
				event.setCancelled(true);

				if (current.getType() == Material.STAINED_GLASS_PANE) {
					event.setCancelled(true);
					BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());

					if (bP.getMoney() < 45000) {
						p.sendMessage("§cVocê não possui moedas suficientes para participar do torneio.");
						return;
					}

					if (!bP.getGroups().contains(Group.COPA)) {
						bP.removeMoney(45000);
						bP.getData(DataType.PARTICIPANDO).setValue(true);

						p.sendMessage(
								"§aParabéns! você comprou o ingresso para nosso torneio, desejamos uma boa sorte para você.");

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add copa");

						p.closeInventory();

						bP.save(DataCategory.BALANCE);

					} else {
						p.closeInventory();
						p.sendMessage("§cVocê já está participando de nosso torneio.");
					}
				}
				return;
			}

			if (event.getInventory().getName().equals(getMenu(MenuType.GAMES_MODE).getName())
					&& event.getCurrentItem() != null) {
				event.setCancelled(true);

				if (current.getType() == Material.ARROW) {
					connectMessage(p, "lobby-sw");
				} else if (current.getType() == Material.DIAMOND_SWORD) {
					connectMessage(p, "pvp");
				} else if (current.getType() == Material.IRON_FENCE) {
					connectMessage(p, "gladiator");
				} else if (current.getType() == Material.MAGMA_CREAM) {
					connectMessage(p, "evento");
				} else {
					if (current.getType() == Material.MUSHROOM_SOUP) {
						p.openInventory(getMenu(MenuType.DOUBLEKITHG));
					}
					if (current.getType() == Material.BOOKSHELF) {
						connectMessage(p, "lobby");
					} else if (current.getType() == Material.POTION) {

					}
					connectMessage(p, "practice");

				}
			} else if (event.getInventory().getName().equals(getMenu(MenuType.DOUBLEKITHG).getName())
					&& event.getCurrentItem() != null) {
				event.setCancelled(true);
				if (current.getType() == Material.ARROW) {
					p.closeInventory();
					p.openInventory(getMenu(MenuType.GAMES_MODE));
				}
				if (event.getCurrentItem().getType() == Material.EYE_OF_ENDER) {
					findServer(p, ServerType.DOUBLEKITHG);
					p.closeInventory();
					p.sendMessage("§6§lHG§f Procurando salas de §6§lHG §fdisponíveis no momento");
				}
				if (event.getCurrentItem().hasItemMeta()) {
					for (int i = 1; i < 11; i++) {
						if (event.getCurrentItem().getItemMeta().getDisplayName().replace("§aDoubleKit #", "")
								.replace("§a", "").contains(String.valueOf(i))) {
							p.closeInventory();
							connectMessage(p, "hg-" + (i == 10 ? 10 : i));
							p.sendMessage("§6§lHG§f Conectando ao servidor §aDoubleKit #" + i + "!");
							return;
						}
					}
				}
			} else if (event.getInventory().getName().equals(getMenu(MenuType.LOBBYS).getName())
					&& event.getCurrentItem() != null) {
				event.setCancelled(true);
				if (event.getCurrentItem().hasItemMeta()) {
					connectMessage(p, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
				}
			}
		}
	}

	public void findServer(Player p, ServerType serverType) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("FindServer");
		out.writeUTF(serverType.name());
		p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

	public void connectMessage(Player p, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

	public void update() {
		int onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.LOBBY);
		Inventory menu = MENUS.get(MenuType.GAMES_MODE);
		ItemBuilder builder;
		if (menu != null) {
			builder = new ItemBuilder().name("§6Lobby Principal").type(Material.BOOKSHELF)
					.lore("§eClique para entrar!");
			menu.setItem(10, builder.build());
			//
			onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.PVP_FULLIRON);
			builder = new ItemBuilder().name("§aKitPvP").type(Material.DIAMOND_SWORD).lore("§8Competitivo.", "§7",
					"§7Treine com seus amigos um modo", "§7de jogo competitivo cheio de aventuras",
					"§7para todos os jogadores de nossa rede", "§7para todos os jogadores de nossa rede",
					"§7e ganhe recompensas por estar treinando!", "§7", "§a" + onlineCount + "§7 jogadores conectados.",
					"§aClique para entrar nesse servidor.");
			menu.setItem(12, builder.build());
			//

			onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.DOUBLEKITHG)
					+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.HG_LOBBY);
			builder = new ItemBuilder().name("§aHardcoreGames").glow().type(Material.MUSHROOM_SOUP).lore(
					"§8Competitivo.", "", "§7Esse modo de jogo é o mais competitivo",
					"§7de todos os tempos! aqui é um battle royale",
					"§7misturado com muitas coisas, entre 80 jogadores",
					"§7misturado com muitas coisas, entre 80 jogadores", "§7eaí? vai encarar essa batalha?", "",
					"§a" + onlineCount + " §7jogadores conecetados", "§aClique para entrar nesse servidor.");
			menu.setItem(13, builder.build());

			//

			onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.GLADIATOR);
			builder = new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE).lore("§8Competitivo.", "",
					"§7Tire 1v1 com seu amigo", "§7no modo GLADIATOR, com itens prontos já",
					"§7para desafiar seu amigo e ser o top 1", "§7desse minigame e chegar ao top 1 global de todos",
					"§7os tempos! venha jogar com seu amigo agora!", "",
					"§a" + onlineCount + " §7jogadores conectados.", "§aClique para entrar nesse servidor.");
			menu.setItem(14, builder.build());
			//
			onlineCount = BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS)
					+ BukkitMain.getInstance().getNetworkManager().getOnlineCount(ServerType.SKYWARS_LOBBY);
			builder = new ItemBuilder().name("§aSkyWars").type(Material.ARROW).lore("§8MiniGame.", "",
					"§7Você tem medo de altura? Então este jogo não é para você!",
					"§7no Sky Wars você deve eliminar seus", "§7adversários com ",
					"§7a ajuda de diversos Kits e Habilidades.", "", "§a" + onlineCount + " §7jogadores conectados.",
					"§aClique para entrar nesse servidor.");
			menu.setItem(15, builder.build());
		}

		menu = MENUS.get(MenuType.DOUBLEKITHG);// hunger games start
		NetworkManager networkManager = BukkitMain.getInstance().getNetworkManager();
		ItemBuilder itemBuilder = null;
		for (int i = 1; i < 11; i++) {
			NetworkServer server = networkManager.getServer("hg-a" + i);
			if (server != null) {
				if (server instanceof HardcoreGames) {
					HardcoreGames hg = (HardcoreGames) server;
					if (hg.getLastData() >= System.currentTimeMillis()) {
						if (hg.getState() == GameState.WAITING) {
							itemBuilder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§aDoubleKit #" + i).lore(
									"", "§aAguardando jogadores",
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}
						if (hg.getState() == GameState.PREGAME) {
							itemBuilder = new ItemBuilder().type(Material.MUSHROOM_SOUP).name("§aDoubleKit #" + i).lore(
									"", "§aIniciando em " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}

						if (hg.getState() == GameState.INVINCIBILITY) {
							itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
									"§eInvencibildade: " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}

						if (hg.getState() == GameState.GAMETIME) {
							itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
									"§cEm jogo: " + getTimeFormat(hg.getTime()),
									"§fJogadores: §a" + hg.getPlayers() + "/" + hg.getMaxPlayers(), "");
						}
					}

				}
			}
			if (itemBuilder == null) {
				itemBuilder = new ItemBuilder().type(Material.BOWL).name("§aDoubleKit #" + i).lore("",
						"§cEsta sala está indisponível no momento,", "§ctente novamente mais tarde!", "");

			}
			menu.setItem(slots[i], itemBuilder.build());
			itemBuilder = null;
		}

		menu = MENUS.get(MenuType.LOBBYS);
		int index = 11;
		NetworkServer lobby = BukkitMain.getInstance().getNetworkManager().getServer("lobby");
		builder = new ItemBuilder().name("§aLobby principal #1").lore("", "§fJogadores §a" + lobby.getPlayers() + "/80")
				.type(Material.INK_SACK).durability(10);
		menu.setItem(index, builder.build());
		index++;
	}

	@Override
	public void disable() {
		//
	}

	public enum MenuType {
		GAMES_MODE, DOUBLEKITHG, TEAMHG, LOBBYS, COPA_MENU;
	}
}
