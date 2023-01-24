package br.com.weavenmc.ypvp.ability.list;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.gamer.Gamer;

public class HotpotatoAbility {

	private HashMap<UUID, Player> u = new HashMap<>();
	private HashMap<UUID, ItemStack> last = new HashMap<>();

	public HotpotatoAbility() {
		//setName("Hotpotato");
		//setHasItem(true);
		//setGroupToUse(Group.PREMIUM);
		//setIcon(Material.TNT);
		//setDescription(new String[] { "§7Coloque TNT na cabeça de seus", "§7inimigos para eles explodirem." });
		//setPrice(50000);
		//setTempPrice(3500);
	}

//	@Override
	public void eject(Player p) {
		if (u.containsKey(p.getUniqueId())) {
			u.remove(p.getUniqueId());
		}
		if (last.containsKey(p.getUniqueId())) {
			last.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		eject(e.getPlayer());
	}

	@EventHandler
	public void onHotpotato(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		if (true) {
			if (false) {
				if (event.getRightClicked() instanceof Player) {
					event.setCancelled(true);
					final Player target = (Player) event.getRightClicked();
					Gamer gamer = yPvP.getPlugin().getGamerManager().getGamer(target.getUniqueId());
					if (!gamer.getWarp().isProtected(target)) {
						if (false) {
							if (!u.containsKey(target.getUniqueId())) {
								//addCooldown(player, 35);
								u.put(target.getUniqueId(), player);
								last.put(target.getUniqueId(), target.getInventory().getHelmet());

								target.getInventory().setHelmet(new ItemStack(Material.TNT));
								target.updateInventory();

								player.sendMessage("§5§lHOTPOTATO§f Você §9§lARMOU§f a §9§lHOTPOTATO§f no jogador §9§l"
										+ target.getName());
								for (int i = 0; i < 6; i++) {
									final int current = i;

									new BukkitRunnable() {

										@Override
										public void run() {
											if (u.containsKey(target.getUniqueId())
													&& u.get(target.getUniqueId()) == player) {
												if (target.getInventory().getHelmet() != null) {
													if (target.getInventory().getHelmet().getType() == Material.TNT) {
														if (current == 5) {
															target.damage(20.0D, player);
															target.getWorld().playEffect(target.getLocation(), Effect.EXPLOSION	, 2);
															u.remove(target.getUniqueId());
														} else {
															target.sendMessage(
																	"§5§lHOTPOTATO§f Você está com uma §9§lHOTPOTATO§f que irá explodir em §9§l"
																			+ convert(current)
																			+ " SEGUNDOS§f! Clique para removê-la!");
														}
													}
												}
											}
										}
									}.runTaskLater(yPvP.getPlugin(), i * 20);
								}
							} else {
								player.sendMessage("§5§lHOTPOTATO§f Este jogador já está com uma §9§lHOTPOTATO!");
							}
						} else {
							//sendCooldown(player);
						}
					} else {
						player.sendMessage("§5§lHOTPOTATO§f Este jogador está com proteção de spawn.");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onHotpotatoClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (u.containsKey(player.getUniqueId())) {
				if (event.getSlotType() == SlotType.ARMOR) {
					if (event.getSlot() == 103) {
						event.setCancelled(true);

						event.getInventory().setItem(103, null);

						player.sendMessage("§5§lHOTPOTATO§f Você removeu a §9§lTNT");

						Player target = u.get(player.getUniqueId());
						if (target != null && target.isOnline()) {
							target.sendMessage(
									"§5§lHOTPOTATO§f O jogador §9§l" + player.getName() + "§f desarmou a §9§lHOTPOTATO§f!");
						}

						target.updateInventory();
						
						u.remove(target.getUniqueId());
						target.getInventory().setHelmet(last.get(player.getUniqueId()));
						last.remove(target.getUniqueId());
					}
				}
			}
		}
	}

	public int convert(int a) {
		if (a == 0) {
			return 5;
		} else if (a == 1) {
			return 4;
		} else if (a == 2) {
			return 3;
		} else if (a == 3) {
			return 2;
		} else if (a == 4) {
			return 1;
		} else {
			return a;
		}
	}
}
