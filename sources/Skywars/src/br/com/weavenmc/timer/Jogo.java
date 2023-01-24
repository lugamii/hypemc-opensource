package br.com.weavenmc.timer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.weavenmc.commons.bukkit.api.title.TitleAPI;
import br.com.weavenmc.commons.core.server.ServerType;
import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.game.GameManager.EventsState;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;

public class Jogo {
	
	public static BukkitTask task;
	public static int timer = 0;
	
	public Jogo() {
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				startTime();
				
			}
		}.runTaskTimer(WeavenSkywars.getInstance(), 20L, 20L);
	}
	
	public void startTime() {
		if (WeavenSkywars.getGameManager().getEState() == EventsState.NONE) {
			timer = 180;
			WeavenSkywars.getGameManager().setEState(EventsState.REFIL1);
			Bukkit.getOnlinePlayers().forEach(players -> Scoreboarding.sendScoreboard(players));
		}
		if (WeavenSkywars.getGameManager().getEState() == EventsState.REFIL1) {
			timer--;
			if (timer == 0) {
				//TODO: REFIL
				WeavenSkywars.getGameManager().getOpened().clear();
				
				timer = 120;
				WeavenSkywars.getGameManager().setEState(EventsState.REFIL2);
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§eOs baús foram", "§crecarregados");
					players.playSound(players.getLocation(), Sound.CHEST_CLOSE, 1f, 1f);
					Scoreboarding.setScoreboard(players);
				});
			}
		}
		if (WeavenSkywars.getGameManager().getEState() == EventsState.REFIL2) {
			timer--;
			if (timer == 0) {
				//TODO: REFIL2
				WeavenSkywars.getGameManager().getOpened().clear();
				timer = 240;
				WeavenSkywars.getGameManager().setEState(EventsState.ENDING);
				Bukkit.getOnlinePlayers().forEach(players -> {
					TitleAPI.setTitle(players, "§eOs baús foram", "§crecarregados");
					players.playSound(players.getLocation(), Sound.CHEST_CLOSE, 1f, 1f);
					Scoreboarding.setScoreboard(players);
				});
			}
		}
		if (WeavenSkywars.getGameManager().getEState() == EventsState.ENDING) {
			timer--;
			if (timer % 60 == 0) {
				Bukkit.broadcastMessage("§a§lFINAL §fFinalizando jogo em " + getTimerChat(timer));
			}
			if (timer == 0) {
				//TODO: FINAL
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getOnlinePlayers().forEach(players -> {
							WeavenSkywars.getGameManager().findServer(players, ServerType.SKYWARS);
						});

					}
				}.runTaskLater(WeavenSkywars.getInstance(), 2L);
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.getOnlinePlayers().forEach(players -> {
							WeavenSkywars.getGameManager().sendLobby(players);
						});

					}
				}.runTaskLater(WeavenSkywars.getInstance(), 4L);
				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.shutdown();

					}
				}.runTaskLater(WeavenSkywars.getInstance(), 6L);
			}
		}
	}
	
	public String getTimerChat(int timer) {
		int minutos = timer / 60,
				segundos = timer % 60;
		String mensagem = "";
		String mMsg = "";
		String sMsg = "";
		if (minutos > 0 && segundos == 0) {
			mMsg = minutos == 1 ? " minuto" : " minutos";
			mensagem = minutos + mMsg;
		} else if (minutos == 0 && segundos > 0) {
			sMsg = segundos == 1 ? " segundo" : " segundos";
			mensagem = segundos + sMsg;
		} else if (minutos > 0 && segundos > 0) {
			mMsg = minutos == 1 ? " minuto" : " minutos";
			sMsg = segundos == 1 ? " segundo" : " segundos";
			mensagem = minutos + mMsg + " e " + segundos + sMsg;
		}
		return mensagem;
	}

}
