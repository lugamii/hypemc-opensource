package br.com.weavenmc.ypvp.tournament;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tournament {

	private boolean running;
	private boolean joinEnabled;

	private boolean isLastOfDay = false;

	private int timeToStart = 300;

	private Set<UUID> players = new HashSet<>(), spectators = new HashSet<>();
	private Queue<UUID> waitingForBattle = new ConcurrentLinkedQueue<>();
	private UUID lastWinner, nextBattlePlayer;

	private TournamentBattle battle;

	public Tournament() {
		this.running = false;
		this.joinEnabled = false;
	}

	public boolean isParticipating(Player p) {
		return players.contains(p.getUniqueId());
	}

	public boolean isLastWinner(Player p) {
		return lastWinner.equals(p.getUniqueId());
	}

	public boolean isNextBattlePlayer(Player p) {
		return nextBattlePlayer.equals(p.getUniqueId());
	}
	
	public void joinPlayer(Player p) {
		if (!players.contains(p.getUniqueId())) {
			players.add(p.getUniqueId());
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.setGameMode(GameMode.SPECTATOR);
		} 
	}
	
	public void quitPlayer(Player p) {
		if (waitingForBattle.contains(p.getUniqueId())) {
			waitingForBattle.remove(p.getUniqueId());
		}
		if (players.contains(p.getUniqueId())) {
			players.remove(p.getUniqueId());
		} 
		if (lastWinner == p.getUniqueId()) {
			lastWinner = null;
		}
		if (nextBattlePlayer == p.getUniqueId()) {
			nextBattlePlayer = null;
		}
	}

	public void begin() {
		joinEnabled = false;
		timeToStart = 300;
		running = true;
	}

	public void getNextPlayer() {
		if (lastWinner == null)
			lastWinner = waitingForBattle.poll();
		nextBattlePlayer = waitingForBattle.poll();
	}

	public void checkWinner() {
		if (players.size() <= 1) {
			running = false;
			if (players.size() == 1) {
				Player winner = Bukkit.getPlayer(players.toArray(new UUID[] {})[0]);
				if (winner != null) {
					Bukkit.broadcastMessage(
							"§3§lTOURNAMENT§f O jogador §a§l" + winner.getName() + "§f foi o §2§lVENCEDOR§f!");
					// send To Spawn
					BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(winner.getUniqueId());
					bP.addMoney(7000);
					bP.addXp(25);
					bP.addDoubleXpMultiplier(4);
					bP.addTickets(1);
					bP.save(DataCategory.BALANCE);
					winner.sendMessage("§3§lTOURNAMENT§f Parabés! Você §a§lVENCEU§f o §1§lTORNEIO!");
					winner.sendMessage("§6§lMONEY§f Você recebeu §6§l7000");
					winner.sendMessage("§9§lXP§f Você recebeu §9§l25");
					winner.sendMessage("§3§lDOUBLEXP§f Você recebeu §3§l4");
					winner.sendMessage("§b§lTICKET§f Você recebeu §b§l1");
					//
					String tournamentTime = "4h";
					if (isLastOfDay)
						tournamentTime = "16h";
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							"group " + winner.getName() + " add torneio " + tournamentTime);
					
				} else {
					stop();
				}
			} else {
				stop();
			}
		}
	}

	public void stop() {

	}

	public TournamentBattle nextBattle() {
		if (players.size() <= 1)
			return null;
		if (lastWinner == null)
			lastWinner = waitingForBattle.poll();
		while (!validate(lastWinner)) {
			players.remove(lastWinner);
			if (players.size() <= 1)
				return null;
			lastWinner = waitingForBattle.poll();
		}
		nextBattlePlayer = waitingForBattle.poll();
		while (!validate(nextBattlePlayer)) {
			players.remove(nextBattlePlayer);
			if (players.size() <= 1)
				return null;
			nextBattlePlayer = waitingForBattle.poll();
		}
		Player player1 = Bukkit.getPlayer(lastWinner);
		Player player2 = Bukkit.getPlayer(nextBattlePlayer);
		return battle = new TournamentBattle(player1, player2);
	}

	public boolean validate(UUID uuid) {
		return Bukkit.getPlayer(uuid) != null;
	}
}
