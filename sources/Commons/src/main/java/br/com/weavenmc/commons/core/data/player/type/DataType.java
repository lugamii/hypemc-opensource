package br.com.weavenmc.commons.core.data.player.type;

import java.util.*;

public enum DataType
{
    PARTICIPANDO("PARTICIPANDO", 0, "participando", 1, (Object)null, "String", "participando", "VARCHAR(17)"), 
    ACCOUNT_UUID("ACCOUNT_UUID", 1, "ACCOUNT_UUID", 0, (Object)null, "String", "uniqueId", "VARCHAR(50)"), 
    USERNAME("USERNAME", 2, "USERNAME", 1, (Object)null, "String", "username", "VARCHAR(17)"), 
    GROUPS("GROUPS", 3, "GROUPS", 2, (Object)new ArrayList(), "String", "groups", "VARCHAR(1000)"), 
    CLAN_NAME("CLAN_NAME", 4, "CLAN_NAME", 3, (Object)"Nenhum", "String", "clan", "VARCHAR(16)"), 
    PASSWORD("PASSWORD", 5, "PASSWORD", 4, (Object)"*", "String", "password", "VARCHAR(30)"), 
    CITY("CITY", 6, "CITY", 5, (Object)"", "String", "city", "VARCHAR(30)"), 
    COUNTRY("COUNTRY", 7, "COUNTRY", 6, (Object)"", "String", "country", "VARCHAR(30)"), 
    PLAYER_PERMISSIONS("PLAYER_PERMISSIONS", 8, "PLAYER_PERMISSIONS", 7, (Object)new ArrayList(), "String", "permissions", "VARCHAR(1000)"), 
    TICKETS("TICKETS", 9, "TICKETS", 8, (Object)0, "Int", "tickets", "INT(100)"), 
    MONEY("MONEY", 10, "MONEY", 9, (Object)0, "Int", "money", "INT(100)"), 
    TOTAL_XP("TOTAL_XP", 11, "TOTAL_XP", 10, (Object)0, "Int", "totalXp", "INT(100)"), 
    XP("XP", 12, "XP", 11, (Object)0, "Int", "xp", "INT(100)"), 
    CURRENT_LEAGUE("CURRENT_LEAGUE", 13, "CURRENT_LEAGUE", 12, (Object)"UNRANKED", "String", "league", "VARCHAR(20)"), 
    CASH("CASH", 14, "CASH", 13, (Object)0, "Int", "cash", "INT(100)"), 
    DOUBLEXPMULTIPLIER("DOUBLEXPMULTIPLIER", 15, "DOUBLEXPMULTIPLIER", 14, (Object)0, "Int", "doubleXpMultiplier", "INT(100)"), 
    LASTACTIVATEDMULTIPLIER("LASTACTIVATEDMULTIPLIER", 16, "LASTACTIVATEDMULTIPLIER", 15, (Object)(-1L), "Long", "lastActivatedMultiplier", "VARCHAR(100)"), 
    PVP_KILLS("PVP_KILLS", 17, "PVP_KILLS", 16, (Object)0, "Int", "kills", "INT(100)"), 
    PVP_DEATHS("PVP_DEATHS", 18, "PVP_DEATHS", 17, (Object)0, "Int", "deaths", "INT(100)"), 
    PVP_KILLSTREAK("PVP_KILLSTREAK", 19, "PVP_KILLSTREAK", 18, (Object)0, "Int", "streak", "INT(100)"), 
    PVP_GREATER_KILLSTREAK("PVP_GREATER_KILLSTREAK", 20, "PVP_GREATER_KILLSTREAK", 19, (Object)0, "Int", "greater_streak", "INT(100)"), 
    PVP_MLG_HIT("PVP_MLG_HIT", 21, "PVP_MLG_HIT", 20, (Object)0, "Int", "mlg_hit", "INT(100)"), 
    PVP_MLG_FAIL("PVP_MLG_FAIL", 22, "PVP_MLG_FAIL", 21, (Object)0, "Int", "mlg_fail", "INT(100)"), 
    LAVA_CHALLENGE_EASY_COMPLETIONS("LAVA_CHALLENGE_EASY_COMPLETIONS", 23, "LAVA_CHALLENGE_EASY_COMPLETIONS", 22, (Object)0, "Int", "easy", "INT(100)"), 
    LAVA_CHALLENGE_MEDIUM_COMPLETIONS("LAVA_CHALLENGE_MEDIUM_COMPLETIONS", 24, "LAVA_CHALLENGE_MEDIUM_COMPLETIONS", 23, (Object)0, "Int", "medium", "INT(100)"), 
    LAVA_CHALLENGE_HARD_COMPLETIONS("LAVA_CHALLENGE_HARD_COMPLETIONS", 25, "LAVA_CHALLENGE_HARD_COMPLETIONS", 24, (Object)0, "Int", "hard", "INT(100)"), 
    LAVA_CHALLENGE_EXTREME_COMPLETIONS("LAVA_CHALLENGE_EXTREME_COMPLETIONS", 26, "LAVA_CHALLENGE_EXTREME_COMPLETIONS", 25, (Object)0, "Int", "extreme", "INT(100)"), 
    PVP_1V1_KILLS("PVP_1V1_KILLS", 27, "PVP_1V1_KILLS", 26, (Object)0, "Int", "kills", "INT(100)"), 
    PVP_1V1_DEATHS("PVP_1V1_DEATHS", 28, "PVP_1V1_DEATHS", 27, (Object)0, "Int", "deaths", "INT(100)"), 
    PVP_1V1_KILLSTREAK("PVP_1V1_KILLSTREAK", 29, "PVP_1V1_KILLSTREAK", 28, (Object)0, "Int", "streak", "INT(100)"), 
    PVP_1V1_GREATER_KILLSTREAK("PVP_1V1_GREATER_KILLSTREAK", 30, "PVP_1V1_GREATER_KILLSTREAK", 29, (Object)0, "Int", "greater_streak", "INT(100)"), 
    PVP_FISHERMAN_HOOKEDS("PVP_FISHERMAN_HOOKEDS", 31, (Object)0, "Int", "fisherman_hookeds", "INT(100)"), 
    GLADIATOR_WINS("GLADIATOR_WINS", 32, "GLADIATOR_WINS", 30, (Object)0, "Int", "wins", "INT(100)"), 
    GLADIATOR_LOSES("GLADIATOR_LOSES", 33, "GLADIATOR_LOSES", 31, (Object)0, "Int", "loses", "INT(100)"), 
    GLADIATOR_WINSTREAK("GLADIATOR_WINSTREAK", 34, "GLADIATOR_WINSTREAK", 32, (Object)0, "Int", "streak", "INT(100)"), 
    GLADIATOR_GREATER_WINSTREAK("GLADIATOR_GREATER_WINSTREAK", 35, "GLADIATOR_GREATER_WINSTREAK", 33, (Object)0, "Int", "greater_streak", "INT(100)"), 
    SKYWARS_SOLO_WINS("SKYWARS_SOLO_WINS", 36, "SKYWARS_SOLO_WINS", 34, (Object)0, "Int", "solo_wins", "INT(100)"), 
    SKYWARS_SOLO_KILLS("SKYWARS_SOLO_KILLS", 37, "SKYWARS_SOLO_KILLS", 35, (Object)0, "Int", "solo_kills", "INT(100)"), 
    SKYWARS_DUO_WINS("SKYWARS_DUO_WINS", 38, "SKYWARS_DUO_WINS", 36, (Object)0, "Int", "duo_wins", "INT(100)"), 
    SKYWARS_DUO_KILLS("SKYWARS_DUO_KILLS", 39, "SKYWARS_DUO_KILLS", 37, (Object)0, "Int", "duo_kills", "INT(100)"), 
    SKYWARS_TEAM_WINS("SKYWARS_TEAM_WINS", 40, "SKYWARS_TEAM_WINS", 38, (Object)0, "Int", "team_wins", "INT(100)"), 
    SKYWARE_TEAM_KILLS("SKYWARE_TEAM_KILLS", 41, "SKYWARE_TEAM_KILLS", 39, (Object)0, "Int", "team_kills", "INT(100)"), 
    HG_WINS("HG_WINS", 42, "HG_WINS", 40, (Object)0, "Int", "wins", "INT(100)"), 
    HG_DEATHS("HG_DEATHS", 43, "HG_DEATHS", 41, (Object)0, "Int", "deaths", "INT(100)"), 
    HG_KILLS("HG_KILLS", 44, "HG_KILLS", 42, (Object)0, "Int", "kills", "INT(100)"), 
    HG_HAS_PENDENT_WINNER("HG_HAS_PENDENT_WINNER", 45, "HG_HAS_PENDENT_WINNER", 43, (Object)false, "Boolean", "winner", "VARCHAR(20)"), 
    TEAMHG_WINS("TEAMHG_WINS", 46, "TEAMHG_WINS", 44, (Object)0, "Int", "wins", "INT(100)"), 
    TEAMHG_DEATHS("TEAMHG_DEATHS", 47, "TEAMHG_DEATHS", 45, (Object)0, "Int", "deaths", "INT(100)"), 
    TEAMHG_KILLS("TEAMHG_KILLS", 48, "TEAMHG_KILLS", 46, (Object)0, "Int", "kills", "INT(100)"), 
    SILVER_CRATES("SILVER_CRATES", 49, "SILVER_CRATES", 47, (Object)0, "Int", "silver", "INT(100)"), 
    GOLD_CRATES("GOLD_CRATES", 50, "GOLD_CRATES", 48, (Object)0, "Int", "gold", "INT(100)"), 
    DIAMOND_CRATES("DIAMOND_CRATES", 51, "DIAMOND_CRATES", 49, (Object)0, "Int", "diamond", "INT(100)"), 
    PVP_100MORE_KILLS("PVP_100MORE_KILLS", 52, "PVP_100MORE_KILLS", 50, (Object)false, "Boolean", "pvpmore100kills", "VARCHAR(10)"), 
    PVP_50_KILLSTREAK("PVP_50_KILLSTREAK", 53, "PVP_50_KILLSTREAK", 51, (Object)false, "Boolean", "pvp50streak", "VARCHAR(10)"), 
    HG_50MORE_WINS("HG_50MORE_WINS", 54, "HG_50MORE_WINS", 52, (Object)false, "Boolean", "hgmore50wins", "VARCHAR(10)"), 
    GLADIATOR_40_WINSTREAK("GLADIATOR_40_WINSTREAK", 55, "GLADIATOR_40_WINSTREAK", 53, (Object)false, "Boolean", "gladmore40streak", "VARCHAR(10)"), 
    PVP_1V1_25_KILLSTREAK("PVP_1V1_25_KILLSTREAK", 56, "PVP_1V1_25_KILLSTREAK", 54, (Object)false, "Boolean", "pvp1v125streak", "VARCHAR(10)"), 
    SERVER_1HOURS_PLAYING_PER_DAY("SERVER_1HOURS_PLAYING_PER_DAY", 57, "SERVER_1HOURS_PLAYING_PER_DAY", 55, (Object)false, "Boolean", "server1hourplayed", "VARCHAR(10)"), 
    COMPUTER_ADDRESS("COMPUTER_ADDRESS", 58, "COMPUTER_ADDRESS", 56, (Object)"", "String", "macAddress", "VARCHAR(50)"), 
    LAST_COMPUTER_ADDRES("LAST_COMPUTER_ADDRES", 59, "LAST_COMPUTER_ADDRES", 57, (Object)"", "String", "lastMacAddress", "VARCHAR(50)"), 
    IP_ADDRESS("IP_ADDRESS", 60, "IP_ADDRESS", 58, (Object)"", "String", "ipAddress", "VARCHAR(50)"), 
    LAST_IP_ADDRESS("LAST_IP_ADDRESS", 61, "LAST_IP_ADDRESS", 59, (Object)"", "String", "lastIpAddress", "VARCHAR(50)"), 
    ONLINE_TIME("ONLINE_TIME", 62, "ONLINE_TIME", 60, (Object)0L, "Long", "onlineTime", "VARCHAR(100)"), 
    JOIN_TIME("JOIN_TIME", 63, "JOIN_TIME", 61, (Object)0L, "Long", "joinTime", "VARCHAR(100)"), 
    FIRST_LOGGED_IN("FIRST_LOGGED_IN", 64, "FIRST_LOGGED_IN", 62, (Object)0L, "Long", "firstLoggedIn", "VARCHAR(100)"), 
    LAST_LOGGED_IN("LAST_LOGGED_IN", 65, "LAST_LOGGED_IN", 63, (Object)0L, "Long", "lastLoggedIn", "VARCHAR(100)"), 
    SKYPE("SKYPE", 66, "SKYPE", 64, (Object)"Nenhum", "String", "skype", "VARCHAR(1000)"), 
    TWITTER("TWITTER", 67, "TWITTER", 65, (Object)"Nenhum", "String", "twitter", "VARCHAR(1000)"), 
    DISCORD("DISCORD", 68, "DISCORD", 66, (Object)"Nenhum", "String", "discord", "VARCHAR(1000)"), 
    YOUTUBE_CHANNEL("YOUTUBE_CHANNEL", 69, "YOUTUBE_CHANNEL", 67, (Object)"Nenhum", "String", "ytChannel", "VARCHAR(1000)"), 
    STEAM("STEAM", 70, "STEAM", 68, (Object)"Nenhum", "String", "steam", "VARCHAR(1000)"), 
    TWITCH("TWITCH", 71, "TWITCH", 69, (Object)"Nenhum", "String", "twitch", "VARCHAR(1000)"), 
    IS_ONLINE("IS_ONLINE", 72, "IS_ONLINE", 70, (Object)false, "Boolean", "online", "VARCHAR(10)"), 
    IN_SCREENSHARE("IN_SCREENSHARE", 73, "IN_SCREENSHARE", 71, (Object)false, "Boolean", "screensharing", "VARCHAR(10)"), 
    SERVER_CONNECTED("SERVER_CONNECTED", 74, "SERVER_CONNECTED", 72, (Object)"none", "String", "server", "VARCHAR(100)"), 
    SERVER_CONNECTED_TYPE("SERVER_CONNECTED_TYPE", 75, "SERVER_CONNECTED_TYPE", 73, (Object)"NONE", "String", "serverType", "VARCHAR(100)"), 
    LAST_SERVER_CONNECTED("LAST_SERVER_CONNECTED", 76, "LAST_SERVER_CONNECTED", 74, (Object)"none", "String", "lastServer", "VARCHAR(100)"), 
    LAST_SERVR_CONNECTED_TYPE("LAST_SERVR_CONNECTED_TYPE", 77, "LAST_SERVR_CONNECTED_TYPE", 75, (Object)"NONE", "String", "lastServerType", "VARCHAR(100)"), 
    CODE("CODE", 78, "CODE", 75, (Object)"NONE", "String", "code", "VARCHAR(100)"), 
    GROUP_NAME("GROUP_NAME", 79, "GROUP_NAME", 76, (Object)"NONE", "String", "groupName", "VARCHAR(100)"), 
    GROUP_EXPIRE("GROUP_EXPIRE", 80, "GROUP_EXPIRE", 77, (Object)"NONE", "String", "groupTime", "VARCHAR(100)"), 
    REDEEMED("REDEEMED", 81, "REDEEMED", 78, (Object)"NONE", "Boolean", "redeemed", "BOOLEAN"), 
    REDEEMED_BY("REDEEMED_BY", 82, "REDEEMED_BY", 79, (Object)"NONE", "String", "redeemedBy", "VARCHAR(100)"), 
    REDEEMED_IN("REDEEMED_IN", 83, "REDEEMED_IN", 80, (Object)"NONE", "Long", "redeemedIn", "LONG"), 
    TELL("TELL", 84, "TELL", 78, (Object)"NONE", "Boolean", "tell", "BOOLEAN"), 
    CLAN("CLAN", 85, "CLAN", 78, (Object)"NONE", "Boolean", "clan", "BOOLEAN");
    
    private Object defaultValue;
    private String classExpected;
    private String field;
    private String tableType;
    
    private DataType(final String s, final int n, final Object defaultValue, final String classExpected, final String field, final String tableType) {
        this.defaultValue = defaultValue;
        this.field = field;
        this.classExpected = classExpected;
        this.tableType = tableType;
    }
    
    private DataType(final String s2, final int n2, final String s, final int n, final Object defaultValue, final String classExpected, final String field, final String tableType) {
        this.defaultValue = defaultValue;
        this.field = field;
        this.classExpected = classExpected;
        this.tableType = tableType;
    }
    
    public boolean isUUID() {
        return this == DataType.ACCOUNT_UUID;
    }
    
    public boolean isUsername() {
        return this == DataType.USERNAME;
    }
    
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    public String getClassExpected() {
        return this.classExpected;
    }
    
    public String getField() {
        return this.field;
    }
    
    public String getTableType() {
        return this.tableType;
    }
}