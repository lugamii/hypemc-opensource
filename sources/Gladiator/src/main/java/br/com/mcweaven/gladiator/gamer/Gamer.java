package br.com.mcweaven.gladiator.gamer;

import java.util.UUID;

import br.com.mcweaven.gladiator.fight.Fight;
import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Gamer {

	private UUID playerUuid;

	@Setter
	UUID gladiatorEnemy;

	private int wins, deaths, winStreak;
	@Setter
	private Fight fight;
	@Setter
	private Sidebar sidebar;

	public Gamer(UUID playerUuid) {
		this.playerUuid = playerUuid;

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(getPlayerUuid());
		bP.load(DataCategory.GLADIATOR);
		this.wins = bP.getData(DataType.GLADIATOR_WINS).asInt();
		this.deaths = bP.getData(DataType.GLADIATOR_LOSES).asInt();
		this.winStreak = bP.getData(DataType.GLADIATOR_WINSTREAK).asInt();
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

	public void addLose(int lose) {

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(getPlayerUuid());
		int loses = bP.getData(DataType.GLADIATOR_LOSES).asInt();
		bP.getData(DataType.GLADIATOR_LOSES).setValue(loses += lose);
		bP.save(DataCategory.BALANCE, DataCategory.GLADIATOR);
		this.deaths = lose +  this.deaths; 
	}

	public void removeWinStreak() {

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(getPlayerUuid());

		int bestWinStreak = bP.getData(DataType.GLADIATOR_GREATER_WINSTREAK).asInt();

		if (winStreak > bestWinStreak)
			bP.getData(DataType.GLADIATOR_GREATER_WINSTREAK).setValue(winStreak);

		bP.getData(DataType.GLADIATOR_WINSTREAK).setValue(0);
		this.winStreak =0;
		bP.save(DataCategory.BALANCE, DataCategory.GLADIATOR);

	}

	public void addWinStreak(int winStreak) {

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(getPlayerUuid());
		int winStreakA = bP.getData(DataType.GLADIATOR_WINSTREAK).asInt();
		int bestWinStreak = bP.getData(DataType.GLADIATOR_GREATER_WINSTREAK).asInt();

		if (winStreak > bestWinStreak)
			bP.getData(DataType.GLADIATOR_GREATER_WINSTREAK).setValue(winStreak);
		
		bP.getData(DataType.GLADIATOR_WINSTREAK).setValue(winStreakA += winStreak);
		bP.save(DataCategory.BALANCE, DataCategory.GLADIATOR);
		this.winStreak = this.winStreak + winStreak;

	}

	public void addWin(int win) {

		BukkitPlayer bP = (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(getPlayerUuid());
		int kills = bP.getData(DataType.GLADIATOR_WINS).asInt();
		bP.getData(DataType.GLADIATOR_WINS).setValue(kills += win);
		bP.save(DataCategory.BALANCE, DataCategory.GLADIATOR);
		this.wins = this.wins + win;

	}

}
