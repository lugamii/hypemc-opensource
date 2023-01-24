package br.com.weavenmc.commons.core.data.player.category;

import br.com.weavenmc.commons.core.data.player.type.DataType;
import lombok.Getter;

@Getter
public enum DataCategory {

	ACCOUNT("category_account", DataType.GROUPS, DataType.CLAN_NAME, DataType.PASSWORD, DataType.CITY, DataType.COUNTRY,
			DataType.PLAYER_PERMISSIONS),
	BALANCE("category_balance", DataType.TICKETS, DataType.MONEY, DataType.TOTAL_XP, DataType.XP,
			DataType.CURRENT_LEAGUE, DataType.CASH, DataType.DOUBLEXPMULTIPLIER, DataType.LASTACTIVATEDMULTIPLIER),
	
	KITPVP("category_kitpvp", DataType.PVP_KILLS, DataType.PVP_DEATHS, DataType.PVP_KILLSTREAK,
			DataType.PVP_GREATER_KILLSTREAK, DataType.PVP_FISHERMAN_HOOKEDS),
	
	LAVA_CHALLENGE("category_lavachallenge", DataType.LAVA_CHALLENGE_EASY_COMPLETIONS,
			DataType.LAVA_CHALLENGE_MEDIUM_COMPLETIONS, DataType.LAVA_CHALLENGE_HARD_COMPLETIONS,
			DataType.LAVA_CHALLENGE_EXTREME_COMPLETIONS),
	BATTLE_1V1("category_battle1v1", DataType.PVP_1V1_KILLS, DataType.PVP_1V1_DEATHS, DataType.PVP_1V1_KILLSTREAK,
			DataType.PVP_1V1_GREATER_KILLSTREAK),
	GLADIATOR("category_gladiator", DataType.GLADIATOR_WINS, DataType.GLADIATOR_LOSES, DataType.GLADIATOR_WINSTREAK,
			DataType.GLADIATOR_GREATER_WINSTREAK),
	SKYWARS("category_skywars", DataType.SKYWARS_SOLO_WINS, DataType.SKYWARS_SOLO_KILLS, DataType.SKYWARS_DUO_WINS,
			DataType.SKYWARS_DUO_KILLS, DataType.SKYWARS_TEAM_WINS, DataType.SKYWARE_TEAM_KILLS),
	HUNGERGAMES("category_hungergames", DataType.HG_WINS, DataType.HG_DEATHS, DataType.HG_KILLS,
			DataType.HG_HAS_PENDENT_WINNER),
	TEAMHG("category_teamhg", DataType.TEAMHG_WINS, DataType.TEAMHG_DEATHS, DataType.TEAMHG_KILLS),
	CRATES("category_crates", DataType.SILVER_CRATES, DataType.GOLD_CRATES, DataType.DIAMOND_CRATES),
	ACHIEVEMENTS("category_achievements", DataType.PVP_100MORE_KILLS, DataType.PVP_50_KILLSTREAK,
			DataType.HG_50MORE_WINS, DataType.GLADIATOR_40_WINSTREAK, DataType.PVP_1V1_25_KILLSTREAK,
			DataType.SERVER_1HOURS_PLAYING_PER_DAY),
	CONNECTION("category_connection", DataType.COMPUTER_ADDRESS, DataType.LAST_COMPUTER_ADDRES, DataType.IP_ADDRESS,
			DataType.LAST_IP_ADDRESS, DataType.ONLINE_TIME, DataType.JOIN_TIME, DataType.FIRST_LOGGED_IN,
			DataType.LAST_LOGGED_IN),
	PERSONAL_DATA("category_personaldata", DataType.SKYPE, DataType.TWITTER, DataType.DISCORD, DataType.YOUTUBE_CHANNEL,
			DataType.STEAM, DataType.TWITCH),
	SERVER_SESSION("category_session", DataType.IS_ONLINE, DataType.IN_SCREENSHARE, DataType.SERVER_CONNECTED,
			DataType.SERVER_CONNECTED_TYPE, DataType.LAST_SERVER_CONNECTED, DataType.LAST_SERVR_CONNECTED_TYPE),
	REWARDS("category_rewards", DataType.CODE, DataType.GROUP_NAME, DataType.GROUP_EXPIRE, DataType.REDEEMED,
			DataType.REDEEMED_BY, DataType.REDEEMED_IN),
	PREFERENCES("category_preferences", DataType.TELL, DataType.CLAN),
	
	COPA("copa", DataType.PARTICIPANDO);

	private String tableName;
	private DataType[] dataTypes;

	private DataCategory(String tableName, DataType... dataTypes) {
		this.tableName = tableName;
		this.dataTypes = dataTypes;
	}
}
