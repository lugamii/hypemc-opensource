package br.com.adlerlopes.bypiramid.hungergames.player.gamer.listener;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.game.event.GamerDeathEvent;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class GamerDeath extends HungerListener {
	private boolean firstKill = false;

	private String getItem(Material type) {
		String cause = "";
		if (type.equals(Material.BOWL)) {
			cause = "tigela";
		} else if (type.equals(Material.MUSHROOM_SOUP)) {
			cause = "sopa";
		} else if (type.equals(Material.COMPASS)) {
			cause = "bússola";
		} else if (type.equals(Material.STICK)) {
			cause = "madeira";
		} else if (type.equals(Material.IRON_INGOT)) {
			cause = "barra de ferro";
		} else if (type.equals(Material.GOLD_INGOT)) {
			cause = "barra de ouro";
		} else if (type.equals(Material.BOW)) {
			cause = "arco";
		} else if (type.equals(Material.WOOD_SWORD)) {
			cause = "espada de madeira";
		} else if (type.equals(Material.STONE_SWORD)) {
			cause = "espada de pedra";
		} else if (type.equals(Material.IRON_SWORD)) {
			cause = "espada de ferro";
		} else if (type.equals(Material.DIAMOND_SWORD)) {
			cause = "espada de diamante";
		} else if (type.equals(Material.WOOD_AXE)) {
			cause = "machado de madeira";
		} else if (type.equals(Material.STONE_AXE)) {
			cause = "machado de pedra";
		} else if (type.equals(Material.IRON_AXE)) {
			cause = "machado de ferro";
		} else if (type.equals(Material.DIAMOND_AXE)) {
			cause = "machado de diamante";
		} else {
			cause = "mão";
		}
		return cause;
	}

	private String getCause(EntityDamageEvent.DamageCause deathCause) {
		String cause = "";
		if (deathCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
			cause = "atacado por um monstro";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.CUSTOM)) {
			cause = "de uma forma não conhecida";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
			cause = "explodido em mil pedaços";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
			cause = "explodido por um monstro";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.CONTACT)) {
			cause = "abra§ando um cacto";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.FALL)) {
			cause = "esquecendo de abrir os paraquedas";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)) {
			cause = "stompado por um bloco";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
			cause = "pegando fogo";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.LAVA)) {
			cause = "nadando na lava";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
			cause = "atingido por um raio";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.MAGIC)) {
			cause = "atingido por uma magia";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.MELTING)) {
			cause = "atingido por um boneco de neve";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.POISON)) {
			cause = "envenenado";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			cause = "atingido por um projetil";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.STARVATION)) {
			cause = "de fome";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
			cause = "sufocado";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.SUICIDE)) {
			cause = "se suicidando";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.THORNS)) {
			cause = "encostando em alguns espinhos";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.VOID)) {
			cause = "pela press§o do void";
		} else if (deathCause.equals(EntityDamageEvent.DamageCause.WITHER)) {
			cause = "pelo efeito do whiter";
		} else {
			cause = "por uma causa desconhecida";
		}
		return cause;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!getManager().getGameManager().isPreGame()) {
			event.setDeathMessage(null);

			Gamer player = getManager().getGamerManager().getGamer(event.getEntity());
			StringBuilder message = new StringBuilder();

			List<ItemStack> drops = event.getDrops();
			Location location = event.getEntity().getLocation();
			for (ItemStack items : drops) {
				if ((items != null) && (items.getType() != Material.AIR)) {
					if (!getManager().getKitManager().isItemKit(items)) {
						if ((event.getEntity().getKiller() instanceof Player)) {
							event.getEntity().getWorld().dropItemNaturally(event.getEntity().getKiller().getLocation(),
									items);
						} else {
							event.getEntity().getWorld().dropItemNaturally(location, items);
						}
					}
				}
			}
			GamerDeathEvent eventGamer;
			if ((event.getEntity().getKiller() instanceof Player)) {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller().getPlayer(), event.getEntity(),
						event.getEntity().getLocation().clone().add(0.0D, 0.5D, 0.0D), event.getDrops());
			} else {
				eventGamer = new GamerDeathEvent(event.getEntity().getKiller(), event.getEntity().getPlayer(),
						event.getEntity().getPlayer().getLocation().clone().add(0.0D, 0.5D, 0.0D), event.getDrops());
			}
			Bukkit.getPluginManager().callEvent(eventGamer);

			event.getDrops().clear();

			String kitDeath = player.getKit().getName();
			String kitDeath2 = player.getKit2().getName();
			if (player.getPlayer().getWorld() == Bukkit.getWorld("world")) {
				Bukkit.getWorld("world").strikeLightning(
						location.clone().add(new Location(Bukkit.getWorld("world"), 0.0D, 100.0D, 0.0D)));
			}
			BukkitPlayer bPLoser = BukkitPlayer.getPlayer(player.getUUID());
			Gamer playerKiller = null;
			if (!(event.getEntity().getKiller() instanceof Player)) {
				message.append("§e" + player.getPlayer().getName());

				message.append("[" + kitDeath + "]");
				message.append(" morreu " + getCause(player.getPlayer().getLastDamageCause().getCause()));
				getManager().getGamerManager().hideSpecs(player.getPlayer());
			} else {
				playerKiller = getManager().getGamerManager().getGamer(event.getEntity().getKiller());
				BukkitPlayer bPKiller = BukkitPlayer.getPlayer(playerKiller.getPlayer().getUniqueId());

				String kitKill = playerKiller.getKit().getName();
				String kitKill2 = playerKiller.getKit2().getName();

				message.append("§b" + playerKiller.getPlayer().getName());
				message.append("(" + kitKill + ", " + kitKill2 + ")");
				message.append(" matou " + player.getPlayer().getName());
				message.append("(" + kitDeath + ", " + kitDeath2 + ")");
				message.append(" usando sua ");
				message.append(getItem(playerKiller.getPlayer().getItemInHand().getType()));

				if (!this.firstKill) {
					this.firstKill = true;
					if (playerKiller.getPlayer().getInventory().getChestplate() == null)
						playerKiller.getPlayer().getInventory().setChestplate(new ItemBuilder(Material.AIR)
								.setColor(Material.LEATHER_CHESTPLATE, Color.GREEN, "§aPeitoral"));
					playerKiller.sendMessage("§a§lFIRSTBLOOD§f Você foi o §2§lPRIMEIRO§f a §a§lMATAR!");
					playerKiller.sendMessage("§a§lFIRSTBLOOD§f Você recebeu §a§l1 DOUBLEXP");
					bPKiller.addDoubleXpMultiplier(1);
				}

				int xp = calculateXp(bPKiller, bPLoser);
				bPKiller.addXp(xp);
				bPKiller.addMoney(100);
				playerKiller.getPlayer().sendMessage("§9§lXP§f Você recebeu §9§l" + xp + " XPs"
						+ (bPKiller.isMultiplierInUse() ? " §7(doublexp)" : ""));
				playerKiller.getPlayer().sendMessage("§6§lMONEY§f Você recebeu §6§l100 MOEDAS");
				
				int hgDeaths = bPLoser.getData(DataType.HG_DEATHS).asInt();
				bPLoser.getData(DataType.HG_DEATHS).setValue(hgDeaths += 1);
				
				bPKiller.save(DataCategory.BALANCE);
				bPLoser.save(DataCategory.HUNGERGAMES);
				
				getManager().getGamerManager().hideSpecs(player.getPlayer());

			}
			if ((event.getEntity().getKiller() instanceof Player)) {
				getManager().getGamerManager().hideSpecs(playerKiller.getPlayer());
			}
			boolean hasRespawn = false;
			String name = player.getPlayer().getName();
			if ((player.isWinner() || bPLoser.getGroup().getId() >= Group.VIP.getId())
					&& (getManager().getGameManager().getGameTime().intValue() <= 300)) {
				hasRespawn = true;
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setRespawn(player);
			} else if (player.isWinner() || bPLoser.getGroup().getId() >= Group.VIP.getId()) {
				getManager().getGamerManager().respawnPlayer(player);
				getManager().getGamerManager().setSpectator(player);
			} else {
				getManager().getGamerManager().setDied(player);
				connectMessage(player.getPlayer(), "lobby");
			}
			getManager().getGamerManager().checkWinner();
			if (!hasRespawn) {
				Bukkit.broadcastMessage(message.toString());
				Bukkit.broadcastMessage(
						"§c" + getManager().getGamerManager().getAlivePlayers().size() + " jogadores restantes");
				Bukkit.broadcastMessage("§e" + name + " saiu do servidor");
				
			}
		}
	}
	public void connectMessage(Player p, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}
	public void findFastHGToPlayer(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("FastHG");
		player.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
	}

	public int calculateXp(WeavenPlayer receiver, WeavenPlayer wP) {
		double result = 5;
		// pvp calculating
		int kills = wP.getData(DataType.PVP_KILLS).asInt();
		int deaths = wP.getData(DataType.PVP_DEATHS).asInt();
		if (kills != 0 && deaths != 0)
			result += Double.valueOf(kills / deaths);
		int battleWins = wP.getData(DataType.PVP_1V1_KILLS).asInt();
		int battleLoses = wP.getData(DataType.PVP_1V1_DEATHS).asInt();
		if (battleWins != 0 && battleLoses != 0)
			result += battleWins / battleLoses;
		// league calculating
		result += Double.valueOf(wP.getLeague().ordinal() / 2);
		// hg calculating
		int hgWins = wP.getData(DataType.HG_WINS).asInt();
		int hgDeaths = wP.getData(DataType.HG_DEATHS).asInt();
		if (hgWins != 0 && hgDeaths != 0)
			result += hgWins / hgDeaths;
		int gladWins = wP.getData(DataType.GLADIATOR_WINS).asInt();
		int gladDeaths = wP.getData(DataType.GLADIATOR_LOSES).asInt();
		if (gladWins != 0 && gladDeaths != 0)
			result += gladWins / gladDeaths;
		if ((int) result <= 0)
			result = 5;
		if (receiver.isDoubleXPActived())
			result = result * 2;
		return (int) result;
	}
}
