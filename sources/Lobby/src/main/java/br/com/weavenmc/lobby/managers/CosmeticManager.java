package br.com.weavenmc.lobby.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.Management;
import br.com.weavenmc.lobby.enums.Heads;
import br.com.weavenmc.lobby.enums.Particles;
import br.com.weavenmc.lobby.enums.Point3D;
import br.com.weavenmc.lobby.enums.Wings;
import br.com.weavenmc.lobby.gamer.Gamer;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

@Getter
public class CosmeticManager extends Management implements Listener {

	private Inventory cosmeticMenu;

	private List<UUID> cantCloseInv;
	private List<ItemStack> crateSilverItems, crateGoldItems, crateDiamondItems;
	private static Point3D[] outline = { new Point3D(0, 0, -0.5f), new Point3D(0.1f, 0.01f, -0.5f),
			new Point3D(0.3f, 0.03f, -0.5f), new Point3D(0.4f, 0.04f, -0.5f), new Point3D(0.6f, 0.1f, -0.5f),
			new Point3D(0.61f, 0.2f, -0.5f), new Point3D(0.62f, 0.4f, -0.5f), new Point3D(0.63f, 0.6f, -0.5f),
			new Point3D(0.635f, 0.7f, -0.5f), new Point3D(0.7f, 0.7f, -0.5f), new Point3D(0.9f, 0.75f, -0.5f),
			new Point3D(1.2f, 0.8f, -0.5f), new Point3D(1.4f, 0.9f, -0.5f), new Point3D(1.6f, 1f, -0.5f),
			new Point3D(1.8f, 1.1f, -0.5f), new Point3D(1.85f, 0.9f, -0.5f), new Point3D(1.9f, 0.7f, -0.5f),
			new Point3D(1.85f, 0.5f, -0.5f), new Point3D(1.8f, 0.3f, -0.5f), new Point3D(1.75f, 0.1f, -0.5f),
			new Point3D(1.7f, -0.1f, -0.5f), new Point3D(1.65f, -0.3f, -0.5f), new Point3D(1.55f, -0.5f, -0.5f),
			new Point3D(1.45f, -0.7f, -0.5f), new Point3D(1.30f, -0.75f, -0.5f), new Point3D(1.15f, -0.8f, -0.5f),
			new Point3D(1.0f, -0.85f, -0.5f), new Point3D(0.8f, -0.87f, -0.5f), new Point3D(0.6f, -0.7f, -0.5f),
			new Point3D(0.5f, -0.5f, -0.5f), new Point3D(0.4f, -0.3f, -0.5f), new Point3D(0.3f, -0.3f, -0.5f),
			new Point3D(0.15f, -0.3f, -0.5f), new Point3D(0f, -0.3f, -0.5f),

			//
			new Point3D(0.9f, 0.55f, -0.5f), new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f),
			new Point3D(1.6f, 0.9f, -0.5f),
			//
			new Point3D(0.9f, 0.35f, -0.5f), new Point3D(1.2f, 0.4f, -0.5f), new Point3D(1.4f, 0.5f, -0.5f),
			new Point3D(1.6f, 0.7f, -0.5f),
			//
			new Point3D(0.9f, 0.15f, -0.5f), new Point3D(1.2f, 0.2f, -0.5f), new Point3D(1.4f, 0.3f, -0.5f),
			new Point3D(1.6f, 0.5f, -0.5f),
			//
			new Point3D(0.9f, -0.05f, -0.5f), new Point3D(1.2f, 0f, -0.5f), new Point3D(1.4f, 0.1f, -0.5f),
			new Point3D(1.6f, 0.3f, -0.5f),
			//
			new Point3D(0.7f, -0.25f, -0.5f), new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f),
			new Point3D(1.4f, 0.1f, -0.5f),
			//
			new Point3D(0.7f, -0.45f, -0.5f), new Point3D(1.0f, -0.4f, -0.5f), new Point3D(1.2f, -0.3f, -0.5f),
			new Point3D(1.4f, -0.1f, -0.5f),
			//
			new Point3D(1.30f, -0.55f, -0.5f), new Point3D(1.15f, -0.6f, -0.5f), new Point3D(1.0f, -0.65f, -0.5f) };

	private static Point3D[] fill = { new Point3D(1.2f, 0.6f, -0.5f), new Point3D(1.4f, 0.7f, -0.5f),

			new Point3D(1.1f, 0.2f, -0.5f), new Point3D(1.3f, 0.3f, -0.5f),

			new Point3D(1.0f, -0.2f, -0.5f), new Point3D(1.2f, -0.1f, -0.5f), };
	static int x = 0;
	static int B = 0;
	static {

		new BukkitRunnable() {

			@Override
			public void run() {

				if (Lobby.getPlugin().getGamerManager().getGamers() == null)
					return;

				for (Gamer g : Lobby.getPlugin().getGamerManager().getGamers()) {

					if (g.isUsingParticle() && !g.isCape()) {
						playParticles(Bukkit.getPlayer(g.getUniqueId()));
					}
					if (g.isCape()) {

						if (x > 500)
							x = 0;
						if (x % 5 == 0) {
							B++;
							if (B % 25 == 0)
								return;

							Player player = Bukkit.getPlayer(g.getUniqueId());
							EnumParticle particle = g.getWing().getParticle();

							Location playerLocation = player.getEyeLocation();
							float x = (float) playerLocation.getX();
							float y = (float) playerLocation.getY() - 0.2f;
							float z = (float) playerLocation.getZ();
							float rot = -playerLocation.getYaw() * 0.017453292F;

							Point3D rotated = null;
							for (Point3D point : outline) {
								rotated = point.rotate(rot);

								PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
										rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);

								point.z *= -1;
								rotated = point.rotate(rot + 3.1415f);
								point.z *= -1;

								PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(particle, true,
										rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (online.canSee(player)) {
										((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
										((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
									}
								}
							}

							for (Point3D point : fill) {
								rotated = point.rotate(rot);

								PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
										rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);

								point.z *= -1;
								rotated = point.rotate(rot + 3.1415f);
								point.z *= -1;

								PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(particle, true,
										rotated.x + x, rotated.y + y, rotated.z + z, 0, 0, 0, 0, 1);
								for (Player online : Bukkit.getOnlinePlayers()) {
									if (online.canSee(player)) {
										((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
										((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
									}
								}
							}

						}

						x++;
					}
				}

			}
		}.runTaskTimerAsynchronously(Lobby.getPlugin(), 0, 3);
	}

	public CosmeticManager(Lobby plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		cantCloseInv = new ArrayList<>();
		cosmeticMenu = Bukkit.createInventory(null, 9 * 4, "§aCosmeticos");
		ItemBuilder builder = new ItemBuilder().name("§aCaixas §7(Clique para abrir)").type(Material.CHEST);
		cosmeticMenu.setItem(10, builder.build());

		builder = new ItemBuilder().name("§aPartículas §7(Clique para abrir)").lore("§7Exclusivo para o vip §6PRO")
				.type(Material.POTION);
		cosmeticMenu.setItem(12, builder.build());

		builder = new ItemBuilder().name("§aApostas §7(Clique para abrir)").type(Material.STAINED_CLAY).durability(5);
		cosmeticMenu.setItem(14, builder.build());
		builder = new ItemBuilder().name("§aHeads §7(Clique para abrir)").lore("§7Exclusivo para o vip §AVIP")
				.type(Material.SKULL_ITEM).skin("HoonReich").durability(3);
		cosmeticMenu.setItem(14, builder.build());
		builder = new ItemBuilder().name("§aCapas §7(Clique para abrir)").lore("§7Exclusivo para o vip §DULTRA")
				.type(Material.BLAZE_POWDER);
		cosmeticMenu.setItem(16, builder.build());
		builder = new ItemBuilder().name("§aLoja §7(Clique para abrir)").type(Material.DOUBLE_PLANT);
		cosmeticMenu.setItem(31, builder.build());

		registerListener(this);
		crateSilverItems = new ArrayList<>();
		int i = 1;
		while (i <= 4) {
			builder = new ItemBuilder().name("§6§l3000 MOEDAS").type(Material.GOLD_INGOT);
			crateSilverItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§6§l5000 MOEDAS").type(Material.GOLD_INGOT);
			crateSilverItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§9§lX3 DOUBLEXPs").type(Material.EXP_BOTTLE);
			crateSilverItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 2) {
			builder = new ItemBuilder().name("§3§lX1 TICKETs").type(Material.DIAMOND);
			crateSilverItems.add(builder.build());
			++i;
		}
		builder = new ItemBuilder().name("§6§l1 CAIXA DE OURO").type(Material.ENDER_CHEST);
		crateSilverItems.add(builder.build());
		builder = new ItemBuilder().name("§a§l1 VIP VIP POR 3 HORAS").type(Material.INK_SACK).durability(10);
		crateSilverItems.add(builder.build());
		builder = new ItemBuilder().name("§6§l3000 MOEDAS").type(Material.GOLD_INGOT);
		crateSilverItems.add(builder.build());
		//
		crateGoldItems = new ArrayList<>();
		i = 1;
		while (i <= 4) {
			builder = new ItemBuilder().name("§6§l7000 MOEDAS").type(Material.GOLD_INGOT);
			crateGoldItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§6§l9000 MOEDAS").type(Material.GOLD_INGOT);
			crateGoldItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§9§lX6 DOUBLEXPs").type(Material.EXP_BOTTLE);
			crateGoldItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 2) {
			builder = new ItemBuilder().name("§3§lX2 TICKETs").type(Material.DIAMOND);
			crateGoldItems.add(builder.build());
			++i;
		}
		builder = new ItemBuilder().name("§b§l1 CAIXA DE DIAMANTE").type(Material.ENDER_CHEST);
		crateGoldItems.add(builder.build());
		builder = new ItemBuilder().name("§6§l1 VIP PRO POR 3 HORAS").type(Material.INK_SACK).durability(1);
		crateGoldItems.add(builder.build());
		builder = new ItemBuilder().name("§6§l7000 MOEDAS").type(Material.GOLD_INGOT);
		crateGoldItems.add(builder.build());
		//
		crateDiamondItems = new ArrayList<>();
		i = 1;
		while (i <= 4) {
			builder = new ItemBuilder().name("§6§l10000 MOEDAS").type(Material.GOLD_INGOT);
			crateDiamondItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§6§l15000 MOEDAS").type(Material.GOLD_INGOT);
			crateDiamondItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 3) {
			builder = new ItemBuilder().name("§9§lX10 DOUBLEXPs").type(Material.EXP_BOTTLE);
			crateDiamondItems.add(builder.build());
			++i;
		}
		i = 1;
		while (i <= 2) {
			builder = new ItemBuilder().name("§3§lX4 TICKETs").type(Material.DIAMOND);
			crateDiamondItems.add(builder.build());
			++i;
		}
		builder = new ItemBuilder().name("§b§l+1 CAIXA DE DIAMANTE").type(Material.ENDER_CHEST);
		crateDiamondItems.add(builder.build());
		builder = new ItemBuilder().name("§1§l1 VIP BETA POR 3 HORAS").type(Material.INK_SACK).durability(4);
		crateDiamondItems.add(builder.build());
		builder = new ItemBuilder().name("§d§l1 VIP ULTRA POR 3 HORAS").type(Material.INK_SACK).durability(13);
		crateDiamondItems.add(builder.build());
		builder = new ItemBuilder().name("§6§l10000 MOEDAS").type(Material.GOLD_INGOT);
		crateDiamondItems.add(builder.build());

		builder = null;
		i = 0;
	}

	@Override
	public void disable() {
		cantCloseInv.clear();
		cantCloseInv = null;
		cosmeticMenu.clear();
		cosmeticMenu = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLeaveListener(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (cantCloseInv.contains(p.getUniqueId())) {
			cantCloseInv.remove(p.getUniqueId());
			p.closeInventory();
		}
		if (luckyTask.containsKey(p.getUniqueId())) {
			luckyTask.get(p.getUniqueId()).cancel();
			luckyTask.remove(p.getUniqueId());
		}
		if (luckyType.containsKey(p.getUniqueId())) {
			luckyType.remove(p.getUniqueId());
		}
	}

	static void playParticles(Player player) {

		Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(player.getUniqueId());
		gamer.setAlpha(gamer.getAlpha() + Math.PI / 16);
		double alpha = gamer.getAlpha();

		Location loc = player.getLocation();
		Location firstLocation = loc.clone().add(Math.cos(alpha), Math.sin(alpha) + 1, Math.sin(alpha));
		Location secondLocation = loc.clone().add(Math.cos(alpha + Math.PI), Math.sin(alpha) + 1,
				Math.sin(alpha + Math.PI));

		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(gamer.getParticle().getParticle(), true,
				(float) firstLocation.getX(), (float) firstLocation.getY(), (float) firstLocation.getZ(), 0, 0, 0, 0,
				1);
		PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(gamer.getParticle().getParticle(), true,
				(float) secondLocation.getX(), (float) secondLocation.getY(), (float) secondLocation.getZ(), 0, 0, 0, 0,
				1);

		for (Player online : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
			((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet2);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();
			Inventory menu = event.getInventory();
			if (cantCloseInv.contains(p.getUniqueId())) {
				new BukkitRunnable() {
					@Override
					public void run() {
						p.openInventory(menu);
					}
				}.runTaskLater(getPlugin(), 1L);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	// TODO
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
			if (event.getInventory().getName().contains("Sorteando")) {
				event.setCancelled(true);
			} else if (event.getInventory().getName().equals("§aCosmeticos")) {
				event.setCancelled(true);
				if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST) {
					openBoxesMenuToPlayer(p);
				} else if (event.getCurrentItem() != null
						&& event.getCurrentItem().getType() == Material.STAINED_CLAY) {
					openLuckyMenuToPlayer(p);
				} else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.POTION) {

					if (!bP.hasGroupPermission(Group.PRO)) {
						p.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando§f!");
						return;
					}

					openParticlesMenuToPlayer(p);
				} else if (event.getCurrentItem() != null
						&& event.getCurrentItem().getType() == Material.BLAZE_POWDER) {

					if (!bP.hasGroupPermission(Group.ULTRA)) {
						p.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando§f!");
						return;
					}

					openWingsMenuToPlayer(p);
				} else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SKULL_ITEM) {

					if (!bP.hasGroupPermission(Group.VIP)) {
						p.sendMessage("§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando§f!");
						return;
					}

					openHeadsMenuToPlayer(p);
				} else if (event.getCurrentItem() != null
						&& event.getCurrentItem().getType() == Material.DOUBLE_PLANT) {
					p.closeInventory();
					p.openInventory(Lobby.getPlugin().getStoreManager().getCategoryMenu());
				}
			} else if (event.getInventory().getName().equals("§eCaixas")) {
				event.setCancelled(true);
				if (event.getCurrentItem() == null)
					return;
				if (event.getCurrentItem().getType() == Material.ARROW) {
					p.openInventory(cosmeticMenu);
					return;
				}
				if (!event.getCurrentItem().hasItemMeta())
					return;
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				if (name.equals("§7Caixa de §7§lPRATA")) {
					int amount = bP.getSilverCrates();
					if (amount > 0) {
						bP.removeSilverCrates(1);
						bP.save(DataCategory.CRATES);
						useCrateSilver(p);
					} else {
						p.sendMessage("§5§lCAIXAS§f Você não possui nenhuma caixa de prata.");
					}
				} else if (name.equals("§7Caixa de §6§lOURO")) {
					int amount = bP.getGoldCrates();
					if (amount > 0) {
						bP.removeGoldCrates(1);
						bP.save(DataCategory.CRATES);
						useCrateGold(p);
					} else {
						p.sendMessage("§5§lCAIXAS§f Você não possui nenhuma caixa de ouro.");
					}
				} else if (name.equals("§7Caixa de §b§lDIAMANTE")) {
					int amount = bP.getDiamondCrates();
					if (amount > 0) {
						bP.removeDiamondCrates(1);
						bP.save(DataCategory.CRATES);
						useCrateDiamond(p);
					} else {
						p.sendMessage("§5§lCAIXAS§f Você não possui nenhuma caixa de diamante.");
					}
				}
			} else if (event.getInventory().getName().equals("§ePartículas")) {
				event.setCancelled(true);
				if (event.getCurrentItem() == null)
					return;
				if (event.getCurrentItem().getType() == Material.ARROW) {
					p.openInventory(cosmeticMenu);
					return;
				}
				if (!event.getCurrentItem().hasItemMeta())
					return;

				Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(p.getUniqueId());

				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cRemover partículas")
						|| gamer.isUsingParticle()) {

					gamer.setUsingParticle(false);
					gamer.setParticle(null);

					gamer.setCape(false);
					if (event.getCurrentItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase("§cRemover partículas")) {
						p.closeInventory();
						return;

					}

				}

				for (Particles particles : Particles.values()) {
					if (event.getCurrentItem().getType() == particles.getItem().getType()) {
						gamer.setUsingParticle(true);
						gamer.setCape(false);
						gamer.setParticle(particles);
						p.closeInventory();
						p.sendMessage("§e§lPARTICLES §fVocê selecionou a particula §e§l"
								+ particles.getName().toUpperCase() + "§f!");
					}
				}
			} else if (event.getInventory().getName().equals("§eCapas")) {
				event.setCancelled(true);
				if (event.getCurrentItem() == null)
					return;
				if (event.getCurrentItem().getType() == Material.ARROW) {
					p.openInventory(cosmeticMenu);
					return;
				}
				if (!event.getCurrentItem().hasItemMeta())
					return;

				Gamer gamer = Lobby.getPlugin().getGamerManager().getGamer(p.getUniqueId());

				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cRemover capa")
						|| gamer.isUsingParticle()) {

					gamer.setUsingParticle(false);
					gamer.setParticle(null);

					if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cRemover capa")) {
						p.closeInventory();
						return;

					}

				}

				for (Wings particles : Wings.values()) {
					if (event.getCurrentItem().getType() == particles.getItem().getType()) {
						gamer.setUsingParticle(true);
						gamer.setWing(particles);
						gamer.setCape(true);
						p.closeInventory();
						p.sendMessage("§e§lPARTICLES §fVocê selecionou a capa §e§l" + particles.getName().toUpperCase()
								+ "§f!");
					}
				}
			} else if (event.getInventory().getName().equals("§eCabeças")) {
				event.setCancelled(true);

				if (event.getCurrentItem() == null)
					return;
				if (event.getCurrentItem().getType() == Material.ARROW) {
					p.openInventory(cosmeticMenu);
					return;
				}
				if (!event.getCurrentItem().hasItemMeta())
					return;

				if (event.getCurrentItem().getItemMeta().getDisplayName()
						.equalsIgnoreCase("§cVolte sua cabeça ao normal")) {
					p.getInventory().setHelmet(null);
					p.closeInventory();
				}

				for (Heads head : Heads.values()) {
					if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase()
							.contains(head.getHeadName().toLowerCase())) {
						p.closeInventory();
						p.getInventory().setHelmet(event.getCurrentItem());
						p.sendMessage("§aVocê selecionou a cabeça: " + head.getHeadName());
					}
				}

			} else if (event.getInventory().getName().equals("§eCapas")) {

			} else if (event.getInventory().getName().equals("§7Escolha uma aposta")) {

				event.setCancelled(true);
				if (event.getCurrentItem() == null)
					return;
				if (event.getCurrentItem().getType() == Material.ARROW) {
					p.openInventory(cosmeticMenu);
					return;
				}
				if (!event.getCurrentItem().hasItemMeta())
					return;
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				if (name.equals("§2§lX3 GREEN BLOCKS")) {
					int amount = bP.getMoney();
					if (amount >= 10000) {
						bP.removeMoney(10000);
						bP.save(DataCategory.BALANCE);
						play3Green(p);
					} else {
						p.sendMessage("§2§lAPOSTAS§f Você precisa de mais §6§l" + (10000 - amount)
								+ " MOEDAS§f para esta aposta.");
					}
				} else if (name.equals("§2§lX6 GREEN BLOCKS")) {
					int amount = bP.getMoney();
					if (amount >= 20000) {
						bP.removeMoney(20000);
						bP.save(DataCategory.BALANCE);
						play6Green(p);
					} else {
						p.sendMessage("§2§lAPOSTAS§f Você precisa de mais §6§l" + (20000 - amount)
								+ " MOEDAS§f para esta aposta.");
					}
				} else if (name.equals("§2§lX9 GREEN BLOCKS")) {
					int amount = bP.getMoney();
					if (amount >= 30000) {
						bP.removeMoney(30000);
						bP.save(DataCategory.BALANCE);
						play9Green(p);
					} else {
						p.sendMessage("§2§lAPOSTAS§f Você precisa de mais §6§l" + (30000 - amount)
								+ " MOEDAS§f para esta aposta.");
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private List<ItemStack> luckys = Arrays.asList(
			new ItemBuilder().name("§2§lVERDE").type(Material.getMaterial(159)).durability(5).build(),
			new ItemBuilder().name("§2§lVERDE").type(Material.getMaterial(159)).durability(5).build(),
			new ItemBuilder().name("§c§lVERMELHO").type(Material.getMaterial(159)).durability(14).build(),
			new ItemBuilder().name("§c§lVERMELHO").type(Material.getMaterial(159)).durability(14).build(),
			new ItemBuilder().name("§c§lVERMELHO").type(Material.getMaterial(159)).durability(14).build(),
			new ItemBuilder().name("§c§lVERMELHO").type(Material.getMaterial(159)).durability(14).build(),
			new ItemBuilder().name("§C§lVERMELHO").type(Material.getMaterial(159)).durability(14).build());

	public void play3Green(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		luckyType.put(p.getUniqueId(), 3);

		AtomicInteger current = new AtomicInteger();
		current.set(1);
		AtomicInteger played = new AtomicInteger();

		luckyTask.put(p.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if (current.get() == 1) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(0, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(2);
						played.set(0);
					}
				} else if (current.get() == 2) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(1, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(3);
						played.set(0);
					}
				} else if (current.get() == 3) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(2, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(4);
						played.set(0);
					}
				} else if (current.get() >= 4) {
					getResult(p, menu);
					cancel();
					return;
				}
				p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
			}
		}.runTaskTimer(getPlugin(), 2L, 2L));
	}

	public void play6Green(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		luckyType.put(p.getUniqueId(), 6);

		AtomicInteger current = new AtomicInteger();
		current.set(1);
		AtomicInteger played = new AtomicInteger();

		luckyTask.put(p.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if (current.get() == 1) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(0, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(2);
						played.set(0);
					}
				} else if (current.get() == 2) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(1, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(3);
						played.set(0);
					}
				} else if (current.get() == 3) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(2, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(4);
						played.set(0);
					}
				} else if (current.get() == 4) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(3, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(5);
						played.set(0);
					}
				} else if (current.get() == 5) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(4, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(6);
						played.set(0);
					}
				} else if (current.get() == 6) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(5, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(7);
						played.set(0);
					}

				} else if (current.get() >= 7) {
					getResult(p, menu);
					cancel();
					return;
				}
				p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
			}
		}.runTaskTimer(getPlugin(), 2L, 2L));
	}

	public void play9Green(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		luckyType.put(p.getUniqueId(), 9);

		AtomicInteger current = new AtomicInteger();
		current.set(1);
		AtomicInteger played = new AtomicInteger();

		luckyTask.put(p.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if (current.get() == 1) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(0, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(2);
						played.set(0);
					}
				} else if (current.get() == 2) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(1, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(3);
						played.set(0);
					}
				} else if (current.get() == 3) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(2, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(4);
						played.set(0);
					}
				} else if (current.get() == 4) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(3, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(5);
						played.set(0);
					}
				} else if (current.get() == 5) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(4, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(6);
						played.set(0);
					}
				} else if (current.get() == 6) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(5, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(7);
						played.set(0);
					}
				} else if (current.get() == 7) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(6, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(8);
						played.set(0);
					}
				} else if (current.get() == 8) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(7, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(9);
						played.set(0);
					}
				} else if (current.get() == 9) {
					if (played.incrementAndGet() <= 50) {
						menu.setItem(8, luckys.get(new Random().nextInt(luckys.size() - 1)));
					} else {
						current.set(10);
						played.set(0);
					}
				} else if (current.get() >= 10) {
					getResult(p, menu);
					cancel();
					return;
				}
				p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
			}
		}.runTaskTimer(getPlugin(), 2L, 2L));
	}

	public void getResult(Player p, Inventory menu) {
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				cantCloseInv.remove(p.getUniqueId());
				p.closeInventory();
				if (luckyType.get(p.getUniqueId()) == 3) {
					int result = 0;
					for (int i = 0; i < 3; i++) {
						String name = menu.getItem(i).getItemMeta().getDisplayName();
						if (name.equals("§2§lVERDE")) {
							++result;
						}
					}
					if (result > 0) {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1.5F, 1.5F);
						int toAdd = 8000 * result;
						bP.addMoney(toAdd);
						bP.save(DataCategory.BALANCE);
						p.sendMessage("§2§lAPOSTAS§f Você acertou §a§l" + result + " VEZES§f e recebeu §6§l" + toAdd
								+ " MOEDAS");
					} else {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.5F, 1.5F);
						p.sendMessage("§2§lAPOSTAS§f Você §c§lNAO ACERTOU NENHUMA VEZ D:");
					}
				} else if (luckyType.get(p.getUniqueId()) == 6) {
					int result = 0;
					for (int i = 0; i < 6; i++) {
						String name = menu.getItem(i).getItemMeta().getDisplayName();
						if (name.equals("§2§lVERDE")) {
							++result;
						}
					}
					if (result > 0) {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1.5F, 1.5F);
						int toAdd = 8000 * result;
						bP.addMoney(toAdd);
						bP.save(DataCategory.BALANCE);
						p.sendMessage("§2§lAPOSTAS§f Você acertou §a§l" + result + " VEZES§f e recebeu §6§l" + toAdd
								+ " MOEDAS");
					} else {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.5F, 1.5F);
						p.sendMessage("§2§lAPOSTAS§f Você §c§lNAO ACERTOU NENHUMA VEZ D:");
					}
				} else if (luckyType.get(p.getUniqueId()) == 9) {
					int result = 0;
					for (int i = 0; i < 9; i++) {
						String name = menu.getItem(i).getItemMeta().getDisplayName();
						if (name.equals("§2§lVERDE")) {
							++result;
						}
					}
					if (result > 0) {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1.5F, 1.5F);
						int toAdd = 8000 * result;
						bP.addMoney(toAdd);
						bP.save(DataCategory.BALANCE);
						p.sendMessage("§2§lAPOSTAS§f Você acertou §a§l" + result + " VEZES§f e recebeu §6§l" + toAdd
								+ " MOEDAS");
					} else {
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.5F, 1.5F);
						p.sendMessage("§2§lAPOSTAS§f Você §c§lNAO ACERTOU NENHUMA VEZ D:");
					}
				}
				luckyType.remove(p.getUniqueId());
				luckyTask.remove(p.getUniqueId());
			}
		}.runTaskLater(getPlugin(), 10L);
	}

	public HashMap<UUID, BukkitTask> luckyTask = new HashMap<>();

	private HashMap<UUID, Integer> luckyType = new HashMap<>();
	private HashMap<UUID, Integer> blocks = new HashMap<>();

	public void useCrateDiamond(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando caixa de Diamante...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		//
		for (int i = 1; i < 57; i++) {
			final int current = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (current != 56) {
						ItemStack next = crateDiamondItems.get(new Random().nextInt(crateDiamondItems.size() - 1));
						for (int i = 1; i < 9; i++) {
							ItemStack item = menu.getItem(i);
							if (item != null) {
								menu.setItem(i - 1, item);
							}
						}
						menu.setItem(8, next);
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
					} else {
						int i = 0;
						while (i <= 3) {
							menu.setItem(i, null);
							i++;
						}
						i = 5;
						while (i <= 8) {
							menu.setItem(i, null);
							i++;
						}
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
						new BukkitRunnable() {
							@Override
							public void run() {
								getCrateDiamondResult(p, menu.getItem(4));
							}
						}.runTaskLater(getPlugin(), 20L);
					}
				}
			}.runTaskLater(getPlugin(), i * 3L);
		}
	}

	@SuppressWarnings("deprecation")
	public void openLuckyMenuToPlayer(Player p) {
		Inventory menu = Bukkit.createInventory(p, 18, "§7Escolha uma aposta");
		ItemBuilder builder = new ItemBuilder().name("§2§lX3 GREEN BLOCKS")
				.lore("§7Apostar 3x blocos verdes", "", "§aPode acertar até 3 blocos verdes.", "",
						"§f- §68000 moedas x quantidade de acertos", "", "§cVocê paga 10000 moedas para jogar.")
				.type(Material.getMaterial(159)).durability(5).amount(3);
		menu.setItem(10, builder.build());
		builder = new ItemBuilder().name("§2§lX6 GREEN BLOCKS")
				.lore("§7Apostar 6x blocos verdes", "", "§aPode acertar até 6 blocos verdes.", "",
						"§f- §68000 moedas x quantidade de acertos", "", "§cVocê paga 20000 moedas para jogar.")
				.type(Material.getMaterial(159)).durability(5).amount(6);
		menu.setItem(13, builder.build());
		builder = new ItemBuilder().name("§2§lX9 GREEN BLOCKS")
				.lore("§7Apostar 9x blocos verdes", "", "§aPode acertar até 9 blocos verdes.", "",
						"§f- §68000 moedas x quantidade de acertos", "", "§cVocê paga 30000 para jogar.")
				.type(Material.getMaterial(159)).durability(5).amount(9);
		menu.setItem(16, builder.build());
		builder = new ItemBuilder().name("§5§lVoltar").lore("§7Clique para voltar ao menu de Cosmeticos")
				.type(Material.ARROW);
		menu.setItem(4, builder.build());
		p.openInventory(menu);
		builder = null;
	}

	public void useCrateGold(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando caixa de Ouro...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		//
		for (int i = 1; i < 57; i++) {
			final int current = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (current != 56) {
						ItemStack next = crateGoldItems.get(new Random().nextInt(crateGoldItems.size() - 1));
						for (int i = 1; i < 9; i++) {
							ItemStack item = menu.getItem(i);
							if (item != null) {
								menu.setItem(i - 1, item);
							}
						}
						menu.setItem(8, next);
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
					} else {
						int i = 0;
						while (i <= 3) {
							menu.setItem(i, null);
							i++;
						}
						i = 5;
						while (i <= 8) {
							menu.setItem(i, null);
							i++;
						}
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
						new BukkitRunnable() {
							@Override
							public void run() {
								getCrateGoldResult(p, menu.getItem(4));
							}
						}.runTaskLater(getPlugin(), 20L);
					}
				}
			}.runTaskLater(getPlugin(), i * 3L);
		}
	}

	public void useCrateSilver(Player p) {
		Inventory menu = Bukkit.createInventory(p, 9, "§7Sorteando caixa de Prata...");
		p.closeInventory();
		cantCloseInv.add(p.getUniqueId());
		p.openInventory(menu);
		//
		for (int i = 1; i < 57; i++) {
			final int current = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (current != 56) {
						ItemStack next = crateSilverItems.get(new Random().nextInt(crateSilverItems.size() - 1));
						for (int i = 1; i < 9; i++) {
							ItemStack item = menu.getItem(i);
							if (item != null) {
								menu.setItem(i - 1, item);
							}
						}
						menu.setItem(8, next);
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
					} else {
						int i = 0;
						while (i <= 3) {
							menu.setItem(i, null);
							i++;
						}
						i = 5;
						while (i <= 8) {
							menu.setItem(i, null);
							i++;
						}
						p.playSound(p.getLocation(), Sound.CLICK, 1.5F, 1.5F);
						new BukkitRunnable() {
							@Override
							public void run() {
								getCrateSilverResult(p, menu.getItem(4));
							}
						}.runTaskLater(getPlugin(), 20L);
					}
				}
			}.runTaskLater(getPlugin(), i * 3L);
		}
	}

	private void getCrateDiamondResult(Player p, ItemStack result) {
		if (p == null || result == null)
			return;
		cantCloseInv.remove(p.getUniqueId());
		p.closeInventory();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		String name = result.getItemMeta().getDisplayName();
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.5F, 1.5F);
		if (name.equals("§6§l10000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l10000 MOEDAS");
			bP.addMoney(10000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§6§l15000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l15000 MOEDAS");
			bP.addMoney(15000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§9§lX10 DOUBLEXPs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §9§l10 DOUBLEXPs");
			bP.addDoubleXpMultiplier(10);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§3§lX4 TICKETs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §3§l4 TICKET");
			bP.addTickets(4);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§b§l+1 CAIXA DE DIAMANTE")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §b§l+1 CAIXA DE DIAMANTE");
			bP.addDiamondCrates(1);
			bP.save(DataCategory.CRATES);
		} else if (name.equals("§1§l1 VIP BETA POR 3 HORAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §1§l1 VIP BETA COM DURAÇAO DE 3 HORAS");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add beta 3h");
		} else if (name.equals("§d§l1 VIP ULTRA POR 3 HORAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §d§l1 VIP ULTRA COM DURAÇAO DE 3 HORAS");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add ULTRA 3h");
		} else {
			p.sendMessage(
					"§5§lCAIXAS§f O resultado da prêmiação não foi identificado! Você receberá sua caixa de volta.");
			bP.addDiamondCrates(1);
			bP.save(DataCategory.CRATES);
		}
	}

	private void getCrateGoldResult(Player p, ItemStack result) {
		if (p == null || result == null)
			return;
		cantCloseInv.remove(p.getUniqueId());
		p.closeInventory();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		String name = result.getItemMeta().getDisplayName();
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.5F, 1.5F);
		if (name.equals("§6§l7000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l7000 MOEDAS");
			bP.addMoney(7000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§6§l9000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l9000 MOEDAS");
			bP.addMoney(9000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§9§lX6 DOUBLEXPs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §9§l6 DOUBLEXPs");
			bP.addDoubleXpMultiplier(6);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§3§lX2 TICKETs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §3§l2 TICKET");
			bP.addTickets(2);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§b§l1 CAIXA DE DIAMANTE")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §b§l1 CAIXA DE DIAMANTE");
			bP.addDiamondCrates(1);
			bP.save(DataCategory.CRATES);
		} else if (name.equals("§6§l1 VIP PRO POR 3 HORAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l1 VIP PRO COM DURAÇAO DE 3 HORAS");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add PRO 3h");
		} else {
			p.sendMessage(
					"§5§lCAIXAS§f O resultado da prêmiação não foi identificado! Você receberá sua caixa de volta.");
			bP.addGoldCrates(1);
			bP.save(DataCategory.CRATES);
		}
	}

	private void getCrateSilverResult(Player p, ItemStack result) {
		if (p == null || result == null)
			return;
		cantCloseInv.remove(p.getUniqueId());
		p.closeInventory();
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		String name = result.getItemMeta().getDisplayName();
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.5F, 1.5F);
		if (name.equals("§6§l3000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l3000 MOEDAS");
			bP.addMoney(3000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§6§l5000 MOEDAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l3000 MOEDAS");
			bP.addMoney(5000);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§9§lX3 DOUBLEXPs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §9§l3 DOUBLEXPs");
			bP.addDoubleXpMultiplier(3);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§3§lX1 TICKETs")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §3§l1 TICKET");
			bP.addTickets(1);
			bP.save(DataCategory.BALANCE);
		} else if (name.equals("§6§l1 CAIXA DE OURO")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §6§l1 CAIXA DE OURO");
			bP.addGoldCrates(1);
			bP.save(DataCategory.CRATES);
		} else if (name.equals("§a§l1 VIP POR 3 HORAS")) {
			p.sendMessage("§5§lCAIXAS§f Você ganhou §a§lVIP COM DURAÇAO DE 3 HORAS");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "group " + bP.getName() + " add VIP 3h");
		} else {
			p.sendMessage(
					"§5§lCAIXAS§f O resultado da prêmiação não foi identificado! Você receberá sua caixa de volta.");
			bP.addSilverCrates(1);
			bP.save(DataCategory.CRATES);
		}
	}

	public void openHeadsMenuToPlayer(Player p) {
		Inventory menu = Bukkit.createInventory(p, 6 * 9, "§eCabeças");

		menu.setItem(49,
				new ItemBuilder().type(Material.INK_SACK).durability(1).name("§cVolte sua cabeça ao normal!").build());
		menu.setItem(48, new ItemBuilder().type(Material.ARROW).name("§cVoltar").build());

		int i = 9;

		for (Heads heads : Heads.values()) {

			i++;

			if (i % 8 == 0)
				i++;
			if (i % 8 == 8)
				i++;

			menu.setItem(i,
					new ItemBuilder().type(Material.SKULL_ITEM).durability(3).name("§eCabeça §a" + heads.getHeadName())
							.lore("", "§7Exclusivo para §aVIP", "", "§eClique para selecionar")
							.skin(heads.getSkinHeadName()).build());
		}

		p.openInventory(menu);
	}

	// TODOO
	public void openWingsMenuToPlayer(Player p) {
		Inventory menu = Bukkit.createInventory(p, 6 * 9, "§eCapas");

		menu.setItem(49, new ItemBuilder().type(Material.INK_SACK).durability(1).name("§cRemova sua capa!").build());
		menu.setItem(48, new ItemBuilder().type(Material.ARROW).name("§cVoltar").build());

		int i = 9;

		for (Wings particles : Wings.values()) {
			i++;

			if (i % 8 == 0)
				i++;
			if (i % 8 == 8)
				i++;

			menu.setItem(i, particles.getItem());
		}

		p.openInventory(menu);
	}

	public void openParticlesMenuToPlayer(Player p) {
		Inventory menu = Bukkit.createInventory(p, 6 * 9, "§ePartículas");

		menu.setItem(49,
				new ItemBuilder().type(Material.INK_SACK).durability(1).name("§cRemova suas partículas!").build());
		menu.setItem(48, new ItemBuilder().type(Material.ARROW).name("§cVoltar").build());

		int i = 9;

		for (Particles particles : Particles.values()) {
			i++;

			if (i % 8 == 0)
				i++;
			if (i % 8 == 8)
				i++;

			menu.setItem(i, particles.getItem());
		}

		p.openInventory(menu);
	}

	public void openBoxesMenuToPlayer(Player p) {
		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(p.getUniqueId());
		int boxes = bP.getSilverCrates();
		Inventory menu = Bukkit.createInventory(p, 27, "§eCaixas");
		ItemBuilder builder = new ItemBuilder().name("§7Caixa de §7§lPRATA").amount(boxes)
				.lore("§7Premiações desta caixa:", "", "§f- §6§l3000 MOEDAS", "§f- §6§l5000 MOEDAS",
						"§f- §9§lX3 DOUBLEXPs", "§f- §3§lX1 TICKETs", "§f- §6§l1 CAIXA DE OURO",
						"§f- §a§l1 VIP PRO POR 3 HORAS", "", "§7Você possui " + boxes + " desta caixa.")
				.type(Material.CHEST);
		menu.setItem(11, builder.build());
		boxes = bP.getGoldCrates();
		builder = new ItemBuilder().name("§7Caixa de §6§lOURO").amount(boxes)
				.lore("§7Premiações desta caixa:", "", "§f- §6§l7000 MOEDAS", "§f- §6§l9000 MOEDAS",
						"§f- §9§lX6 DOUBLEXPs", "§f- §3§lX2 TICKETs", "§f- §b§l1 CAIXA DE DIAMANTE",
						"§f- §6§l1 VIP PRO POR 3 HORAS", "", "§7Você possui " + boxes + " desta caixa.")
				.type(Material.CHEST);
		menu.setItem(13, builder.build());
		boxes = bP.getDiamondCrates();
		builder = new ItemBuilder().name("§7Caixa de §b§lDIAMANTE").amount(boxes)
				.lore("§7Premiações desta caixa:", "", "§f- §6§l10000 MOEDAS", "§f- §6§l15000 MOEDAS",
						"§f- §9§lX10 DOUBLEXPs", "§f- §3§lX4 TICKETs", "§f- §b§l+1 CAIXA DE DIAMANTE",
						"§f- §1§l1 VIP BETA POR 3 HORAS", "§f- §d§l1 VIP ULTRA POR 3 HORAS", "",
						"§7Você possui " + boxes + " desta caixa.")
				.type(Material.CHEST);
		menu.setItem(15, builder.build());
		builder = new ItemBuilder().name("§CVoltar").lore("§7Clique para voltar ao menu de Cosmeticos")
				.type(Material.ARROW);
		menu.setItem(26, builder.build());
		builder = new ItemBuilder().name("§aCaixa §7(O que são?)")
				.lore("§7São pacotes que sorteiam premios aleatorios", "§7de acordo com seu tipo e preço.",
						"§7Aqui em nosso servidor possuimos 3 tipos de caixas:",
						"§7prata, diamante e ouro. Para saber mais", "§7Passe o mouse em cima delas.")
				.type(Material.PAPER);
		menu.setItem(18, builder.build());
		builder = null;
		bP = null;
		p.openInventory(menu);
	}
}
