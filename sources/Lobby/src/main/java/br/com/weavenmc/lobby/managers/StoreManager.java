package br.com.weavenmc.lobby.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.Management;
import lombok.Getter;

@Getter
public class StoreManager extends Management implements Listener {

	private Inventory categoryMenu;
	private Inventory storeDoubleXpMenu, storeBoxesMenu, storeVipMenu;

	public StoreManager(Lobby plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		categoryMenu = Bukkit.createInventory(null, 3*9, "§7Loja");
		ItemBuilder builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§aDoubleXP §7(Clique para abrir)")
				.lore("§7Clique para abrir a loja de DoubleXps");
		categoryMenu.setItem(11, builder.build());
		
		builder = new ItemBuilder().type(Material.CHEST).name("§aCaixas §7(Clique para abrir)")
				.lore("§7Clique para abrir a loja de Caixas");
		categoryMenu.setItem(13, builder.build());
		
		builder = new ItemBuilder().type(Material.NAME_TAG).name("§aVips §7(Clique para abrir)").lore("§7Clique para abrir a loja de Vips");
		categoryMenu.setItem(15, builder.build());

		storeDoubleXpMenu = Bukkit.createInventory(null, 27, "§7Loja - DoubleXp");	
		builder = new ItemBuilder().type(Material.PAPER).name("§9§lDoubleXp §7(O que são?)").lore(
				"§b§lSão §bpacotes§b§l que servem para §bduplicar", "§b§la quantidade de §bXP's§b§l recebidas em",
				"§b§ltoda a rede. Ao adquirir, para usá-lo", "§b§lbasta utilizar o comando §b/doublexp§b§l e",
				"§b§lo seu pacote §bficará ativo por 1 hora", "§b§lem §btoda a network§b§l antes de expirar.");
		storeDoubleXpMenu.setItem(18, builder.build());
		builder = new ItemBuilder().type(Material.ARROW).name("§cVoltar").lore("§7Clique para voltar ás categorias");
		storeDoubleXpMenu.setItem(26, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§l1 Pacote").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b1000 moedas", "");
		storeDoubleXpMenu.setItem(10, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§l2 Pacotes").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b1900 moedas", "");
		storeDoubleXpMenu.setItem(11, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§l3 Pacotes").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b2900 moedas", "");
		storeDoubleXpMenu.setItem(12, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§lCombo 6 Pacotes").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b5000 moedas", "");
		storeDoubleXpMenu.setItem(14, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§lCombo 9 Pacotes").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b8000 moedas", "");
		storeDoubleXpMenu.setItem(15, builder.build());
		builder = new ItemBuilder().type(Material.EXP_BOTTLE).name("§9§lCombo 12 Pacotes").lore("",
				"§3§lVIPs possuem 50% de desconto no preço", "", "§b§lCompre agora por §b11000 moedas", "");
		storeDoubleXpMenu.setItem(16, builder.build());

		storeBoxesMenu = Bukkit.createInventory(null, 27, "§7Loja - Caixas");
		builder = new ItemBuilder().type(Material.PAPER).name("§5§lCaixas §7(O que são?)").lore(
				"§d§lSão §dpacotes§d§l que sorteiam §dpremios", "§d§laleatorios de acordo com o tipo§d.",
				"§d§lPossuem 3 tipos de §dcaixas: §d§lprata§d, §d§louro§d e",
				"§d§ldiamante§d. §d§lAo adquirir, voce pode §dusá-las",
				"§d§lclicando com o botao §ddireito§d do seu mouse", "§d§lno §dbau do fim§d§l de sua §dhotbar.");
		storeBoxesMenu.setItem(18, builder.build());
		builder = new ItemBuilder().type(Material.ARROW).name("§cVoltar").lore("§7Clique para voltar ás categorias");
		storeBoxesMenu.setItem(26	, builder.build());
		builder = new ItemBuilder().type(Material.CHEST).name("§7§lPRATA").lore("",
				"§7Prêmios que esta caixa sorteia:", "", "§f- §6§l3000 MOEDAS", "§f- §6§l5000 MOEDAS",
				"§f- §9§lX3 DOUBLEXPs", "§f- §3§lX1 TICKETs", "§f- §6§l1 CAIXA DE OURO",
				"§f- §a§l1 VIP VIP POR 3 HORAS", "",
				"§d§lCompre agora por §d15000 moedas");
		storeBoxesMenu.setItem(10, builder.build());
		builder = new ItemBuilder().type(Material.CHEST).name("§6§lGOLD").lore("",
				"§7Prêmios que esta caixa sorteia:", "", "§f- §6§l7000 MOEDAS", "§f- §6§l9000 MOEDAS",
				"§f- §9§lX6 DOUBLEXPs", "§f- §3§lX2 TICKETs", "§f- §b§l1 CAIXA DE DIAMANTE",
				"§f- §6§l1 VIP PRO POR 3 HORAS", "",
				"§d§lCompre agora por §d30000 moedas");
		storeBoxesMenu.setItem(13, builder.build());
		builder = new ItemBuilder().type(Material.CHEST).name("§b§lDIAMANTE").lore("",
				"§7Prêmios que esta caixa sorteia:", "", "§f- §6§l10000 MOEDAS", "§f- §6§l15000 MOEDAS",
				"§f- §9§lX10 DOUBLEXPs", "§f- §3§lX4 TICKETs", "§f- §b§l+1 CAIXA DE DIAMANTE",
				"§f- §1§l1 VIP BETA POR 3 HORAS", "§f- §d§l1 VIP ULTRA POR 3 HORAS", "",
				"§d§lCompre agora por §d45000 moedas");
		storeBoxesMenu.setItem(16, builder.build());

		storeVipMenu = Bukkit.createInventory(null, 27, "§7Loja - Vip");
		builder = new ItemBuilder().type(Material.PAPER).name("§6§lVIPs §7(O que são?)").lore(
				"§6§lSão §6ranks§6§l ou §6grupos§6§l que possuem", "§6vantagens§6§l e §6beneficios§6§l exclusivos",
				"§6§lem toda a rede.", "§6§lLembrando, cada §6vip§6§l pode ter menos ou",
				"§6§lmais §6vantagens§6§l que outros.");
		storeVipMenu.setItem(18, builder.build());
		builder = new ItemBuilder().type(Material.ARROW).name("§cVoltar").lore("§7Clique para voltar ás categorias");
		storeVipMenu.setItem(26, builder.build());

		builder = new ItemBuilder().type(Material.NAME_TAG).name("§a§lVip VIP (3 dias)").lore("",
				"§7Receba o rank §a§lVIP§7 com duraçao de 3 dias!", "", "§6§lCompre agora por §660 tickets");
		storeVipMenu.setItem(10, builder.build());
		builder = new ItemBuilder().type(Material.NAME_TAG).name("§6§lVip PRO (3 dias)").lore("",
				"§7Receba o rank §6§lPRO§7 com duraçao de 3 dias!", "", "§6§lCompre agora por §6120 tickets");
		storeVipMenu.setItem(13, builder.build());
		builder = new ItemBuilder().type(Material.NAME_TAG).name("§d§lVip ULTRA (3 dias)").lore("",
				"§7Receba o rank §d§lULTRA§7 com duraçao de 3 dias!", "", "§6§lCompre agora por §6180 tickets");
		storeVipMenu.setItem(16, builder.build());
		registerListener(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
			String name = event.getInventory().getName();
			ItemStack current = event.getCurrentItem();
			if (name.equals(categoryMenu.getName()) && current != null) {
				event.setCancelled(true);
				if (current.getType() == Material.EXP_BOTTLE) {
					p.openInventory(storeDoubleXpMenu);
				} else if (current.getType() == Material.CHEST) {
					p.openInventory(storeBoxesMenu);
				} else if (current.getType() == Material.NAME_TAG) {
					p.openInventory(storeVipMenu);
				}
			} else if (name.equals(storeDoubleXpMenu.getName()) && current != null) {
				event.setCancelled(true);
				if (current.getType() == Material.ARROW) {
					p.openInventory(categoryMenu);
				} else if (current.hasItemMeta()) {
					String n = current.getItemMeta().getDisplayName();

					int price = 0;
					int quantity = 0;

					if (n.equals("§9§l1 Pacote")) {
						quantity = 1;
						price = 1000;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else if (n.equals("§9§l2 Pacotes")) {
						quantity = 2;
						price = 1900;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else if (n.equals("§9§l3 Pacotes")) {
						quantity = 3;
						price = 2900;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else if (n.equals("§9§lCombo 6 Pacotes")) {
						quantity = 6;
						price = 5000;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else if (n.equals("§9§lCombo 9 Pacotes")) {
						quantity = 9;
						price = 8000;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else if (n.equals("§9§lCombo 12 Pacotes")) {
						quantity = 12;
						price = 11000;
						if (bP.hasGroupPermission(Group.VIP)) {
							price = price / 2;
						}
					} else {
						return;
					}

					if (bP.getMoney() >= price) {
						p.closeInventory();
						bP.addDoubleXpMultiplier(quantity);
						bP.removeMoney(price);
						bP.save(DataCategory.BALANCE);
						p.sendMessage("§b§lDOUBLEXP§f Você comprou §9§l" + quantity
								+ " PACOTES§f, para usar digite §b§l/doublexp");
					} else {
						p.closeInventory();
						p.sendMessage("§b§lDOUBLEXP§f Você precisa de mais §6§l" + (price - bP.getMoney())
								+ " MOEDAS§f para comprar!");
					}
				}
			} else if (name.equals(storeBoxesMenu.getName()) && current != null) {
				event.setCancelled(true);
				if (current.getType() == Material.ARROW) {
					p.openInventory(categoryMenu);
				} else if (current.hasItemMeta()) {
					String n = current.getItemMeta().getDisplayName();
					if (n.equals("§7§lPRATA")) {
						if (bP.getMoney() >= 15000) {
							p.closeInventory();
							bP.addSilverCrates(1);
							bP.removeMoney(15000);
							bP.save(DataCategory.CRATES, DataCategory.BALANCE);
							p.sendMessage("§5§lCAIXAS§f Você comprou uma caixa §7§lPRATA!");
						} else {
							p.closeInventory();
							p.sendMessage("§5§lCAIXAS§f Você precisa de mais §6§l" + (15000 - bP.getMoney())
									+ " MOEDAS§f para comprar esta caixa!");
						}
					} else if (n.equals("§6§lGOLD")) {
						if (bP.getMoney() >= 30000) {
							p.closeInventory();
							bP.addGoldCrates(1);
							bP.removeMoney(30000);
							bP.save(DataCategory.CRATES, DataCategory.BALANCE);
							p.sendMessage("§5§lCAIXAS§f Você comprou uma caixa §6§lOURO!");
						} else {
							p.closeInventory();
							p.sendMessage("§5§lCAIXAS§f Você precisa de mais §6§l" + (30000 - bP.getMoney())
									+ " MOEDAS§f para comprar esta caixa!");
						}
					} else if (n.equals("§b§lDIAMANTE")) {
						if (bP.getMoney() >= 45000) {
							p.closeInventory();
							bP.addDiamondCrates(1);
							bP.removeMoney(45000);
							bP.save(DataCategory.CRATES, DataCategory.BALANCE);
							p.sendMessage("§5§lCAIXAS§f Você comprou uma caixa §b§lDIAMANTE!");
						} else {
							p.closeInventory();
							p.sendMessage("§5§lCAIXAS§f Você precisa de mais §6§l" + (45000 - bP.getMoney())
									+ " MOEDAS§f para comprar esta caixa!");
						}
					}
				}
			} else if (name.equals(storeVipMenu.getName()) && current != null) {
				event.setCancelled(true);
				if (current.getType() == Material.ARROW) {
					p.openInventory(categoryMenu);
				} else if (current.hasItemMeta()) {
					String n = current.getItemMeta().getDisplayName();
					if (n.equals("§a§lVip VIP (3 dias)")) {
						if (bP.getTickets() >= 60) {
							p.closeInventory();
							bP.removeTickets(60);
							bP.save(DataCategory.BALANCE);
							p.sendMessage("§6§lVIP§f Parabéns, você comprou um vip §a§lVIP§f com duração de 3 dias!");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"group " + bP.getName() + " add VIP 3d");
						} else {
							p.closeInventory();
							p.sendMessage("§6§lVIP§f Você precisa de mais §3§l" + (60 - bP.getTickets())
									+ " TICKETS§f para comprar este vip!");
						}
					} else if (n.equals("§6§lVip PRO (3 dias)")) {
						if (bP.getTickets() >= 120) {
							p.closeInventory();
							bP.removeTickets(120);
							bP.save(DataCategory.BALANCE);
							p.sendMessage("§6§lVIP§f Parabéns, você comprou um vip §6§lPRO§f com duração de 3 dias!");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"group " + bP.getName() + " add PRO 3d");
						} else {
							p.closeInventory();
							p.sendMessage("§6§lVIP§f Você precisa de mais §3§l" + (120 - bP.getTickets())
									+ " TICKETS§f para comprar este vip!");
						}
					} else if (n.equals("§d§lVip ULTRA (3 dias)")) {
						if (bP.getTickets() >= 180) {
							p.closeInventory();
							bP.removeTickets(180);
							bP.save(DataCategory.BALANCE);
							p.sendMessage("§6§lVIP§f Parabéns, você comprou um vip §d§lULTRA§f com duração de 3 dias!");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"group " + bP.getName() + " add ULTRA 3d");
						} else {
							p.closeInventory();
							p.sendMessage("§6§lVIP§f Você precisa de mais §3§l" + (180 - bP.getTickets())
									+ " TICKETS§f para comprar este vip!");
						}
					}
				}
			}
		}
	}

	@Override
	public void disable() {

	}
}
