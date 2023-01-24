package br.com.weavenmc.timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.weavenmc.commons.bukkit.api.actionbar.BarAPI;
import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameController;
import br.com.weavenmc.skywars.game.GameManager.GameState;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;

public class Iniciando {
	
	public static BukkitTask task;
	public static int timer;
	public static int min = 5;
	
	public Iniciando() {
		timer = 45;
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (WeavenSkywars.getGameManager().getState() == GameState.LOBBY) {
					startTimer();
				} else {
					startJail();
				}
				
			}
		}.runTaskTimer(WeavenSkywars.getInstance(), 20L, 20L);
	}
	
	public void startJail() {
		--timer;
		if (timer == 0) {
			task.cancel();
		}
	}
	
	public void startTimer() {
		if (GameController.player.size() < min) {
			int i = min - GameController.player.size();
			String jogador = i == 1 ? "jogador" : "jogadores";
			Bukkit.getOnlinePlayers().forEach(players -> BarAPI.send(players, "§eAguardando §c" + i + " §e"+jogador+"..."));
		}
		if (GameController.player.size() >= min) {
			timer--;
	
			if (timer == 30) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c30 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c30 §esegundos.");
			}
			if (timer == 15) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c15 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c15 §esegundos.");
			}
			if (timer == 10) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c10 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c10 §esegundos.");
			}
			if (timer == 5) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c5 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c5 §esegundos.");
			}
			if (timer == 4) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c4 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c4 §esegundos.");
			}
			if (timer == 3) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§c3 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §c3 §esegundos.");
			}
			if (timer == 2) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§e2 segundos", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §62 §esegundos.");
			}
			if (timer == 1) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§a1 segundo", "§epara iniciar o jogo");
					players.playSound(players.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
				});
				Bukkit.broadcastMessage("§eO jogo irá iniciar em §a1 §esegundo.");
			}
			if (timer == 0) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					Scoreboarding.sendScoreboard(players);
				});
				WeavenSkywars.getGameManager().startPartida();
				timer = 6;
			}
		}
	}

}
