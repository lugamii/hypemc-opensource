package br.com.mcweaven.gladiator.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.mcweaven.gladiator.Gladiator;
import br.com.mcweaven.gladiator.fight.Fight;
import br.com.mcweaven.gladiator.gamer.Gamer;

public class FightListener implements Listener {

	Map<Player, Player> challengeMap = new HashMap<>();
	List<Player> queueChallengesList;

	List<Block> returnToOldBlock;
	Map<Block, Material> oldBlock;

	public FightListener() {
		returnToOldBlock = new ArrayList<>();
		oldBlock = new HashMap<>();
		challengeMap = new HashMap<>();
		queueChallengesList = new ArrayList<>();
		new BukkitRunnable() {

			@Override
			public void run() {
				if (returnToOldBlock.isEmpty() || oldBlock.isEmpty()) {
					return;
				}
				for (Block block : returnToOldBlock) {
					if (block != null && oldBlock.containsKey(block) && block.getType() != Material.AIR) {
						block.setType(oldBlock.get(block));
					}
				}
				returnToOldBlock.clear();
				oldBlock.clear();
			}
		}.runTaskTimer(Gladiator.getInstance(), 0, 12 * 20l);

	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getFight() != null)
			gamer.getFight().drop(event);

	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();
			Player target = (Player) event.getRightClicked();

			Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

			if (gamer.getFight() == null) {
				event.setCancelled(true);
				if (player.getItemInHand().getType() == Material.IRON_FENCE) {
					if (challengeMap.containsKey(target) && challengeMap.get(target) == player) {

						Gamer targetGamer = Gladiator.getInstance().getGamerManager().getGamer(target.getUniqueId());

						challengeMap.remove(target);

						Fight fight = new Fight(player.getUniqueId(), target.getUniqueId());

						targetGamer.setFight(fight);
						gamer.setFight(fight);

						gamer.setGladiatorEnemy(target.getUniqueId());

						targetGamer.setGladiatorEnemy(player.getUniqueId());

						player.getInventory().clear();
						target.getInventory().clear();

						if (queueChallengesList.contains(event.getPlayer()))
							queueChallengesList.remove(event.getPlayer());
						if (queueChallengesList.contains(target))
							queueChallengesList.remove(target);

						Gladiator.getInstance().getGladiatorManager().giveBattleItens(player);
						Gladiator.getInstance().getGladiatorManager().giveBattleItens(target);

						Gladiator.getInstance().getFightManager().newFight(fight);

						player.sendMessage("§6§lDESAFIO §fVocê aceitou o desafio de §3§l" + target.getName() + "§f!");
						target.sendMessage("§6§lDESAFIO §3§l" + player.getName() + " §Faceitou o seu desafio§f!");

						gamer.getFight().startBattle();

					} else {

						if (!challengeMap.containsKey(player)) {

							challengeMap.put(player, target);

							player.sendMessage("§6§lDESAFIO §fVocê desafiou §9§l" + target.getName()
									+ " §fpara um duelo Gladiator normal!");

							target.sendMessage("§6§lDESAFIO §fVocê foi desafiado pelo §9§l" + player.getName()
									+ " §fpara um duelo Gladiator normal!");

							new BukkitRunnable() {

								@Override
								public void run() {
									if (challengeMap.containsKey(player))
										challengeMap.remove(player);

								}
							}.runTaskLaterAsynchronously(Gladiator.getInstance(), 15 * 20l);

						} else {
							player.sendMessage("§6§lDESAFIO§f Você deve §3§lAGUARDAR§f para desafiar!");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer.getGladiatorEnemy() == null && player.getItemInHand().hasItemMeta())
			if (player.getItemInHand().getItemMeta().getDisplayName().startsWith("§fDesafio Rápido")) {
				if (!queueChallengesList.contains(player)) {
					queueChallengesList.add(player);
					ItemStack itemInHandStack = player.getItemInHand();

					itemInHandStack.setDurability((short) 10);
					ItemMeta itemInHandMeta = itemInHandStack.getItemMeta();
					itemInHandMeta.setDisplayName("§fDesafio Rápido §7(§aProcurando§7)");
					itemInHandStack.setItemMeta(itemInHandMeta);

					player.updateInventory();

					if (queueChallengesList.size() > 1) {

						Player duel;

						/*
						 * Find battle
						 */

						if (queueChallengesList.get(0) == player) {
							duel = queueChallengesList.get(1);
						} else {
							duel = queueChallengesList.get(0);
						}

						/*
						 * Start battle
						 */

						if (duel == null) {
							player.sendMessage(
									"§6§lDESAFIO§f O desafio rápido §c§lNÃO ENCONTROU§f ninguém, tente novamente!");

							player.getInventory().clear();

							Gladiator.getInstance().getGladiatorManager().giveJoinItens(player);
							queueChallengesList.remove(player);
							return;
						}

						queueChallengesList.remove(player);
						queueChallengesList.remove(duel);

						player.sendMessage("§6§lDESAFIO§f O desafio rápido encontrou §9§l" + duel.getName() + "§f!");
						duel.sendMessage("§6§lDESAFIO§f O desafio rápido encontrou §9§l" + player.getName() + "§f!");

						Fight fight = new Fight(player.getUniqueId(), duel.getUniqueId());

						Gamer duelGamer = Gladiator.getInstance().getGamerManager().getGamer(duel.getUniqueId());

						gamer.setFight(fight);
						duelGamer.setFight(fight);

						gamer.setGladiatorEnemy(duel.getUniqueId());
						duelGamer.setGladiatorEnemy(player.getUniqueId());

						player.getInventory().clear();
						duel.getInventory().clear();

						Gladiator.getInstance().getGladiatorManager().giveBattleItens(player);
						Gladiator.getInstance().getGladiatorManager().giveBattleItens(duel);

						fight.startBattle();

						Gladiator.getInstance().getFightManager().newFight(fight);

					}
				} else {

					Gladiator.getInstance().getGladiatorManager().giveSearch1v1Item(player);
					queueChallengesList.remove(player);

				}
			}
	}

	@EventHandler
	public void onBreakingBlock(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = Gladiator.getInstance().getGamerManager().getGamer(player.getUniqueId());
		if (gamer.getGladiatorEnemy() != null) {
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getClickedBlock().getType().toString().contains("GLASS")) {
					oldBlock.put(event.getClickedBlock(), event.getClickedBlock().getType());
					returnToOldBlock.add(event.getClickedBlock());
					event.getClickedBlock().setType(Material.BEDROCK);
					event.setCancelled(true);
				} else if (event.getClickedBlock().getType() == Material.BEDROCK) {
					event.setCancelled(true);
					
				}
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		
		
		if (queueChallengesList.contains(event.getPlayer()))
			queueChallengesList.remove(event.getPlayer());
		if (challengeMap.containsKey(event.getPlayer()))
			challengeMap.remove(event.getPlayer());
		
		
	}

}
