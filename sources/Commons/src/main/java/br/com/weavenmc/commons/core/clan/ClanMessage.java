package br.com.weavenmc.commons.core.clan;

import lombok.Getter;

@Getter
public class ClanMessage {
	
	private String userMessageResponser = "-";
	private String clanName = "-";
	private String oldAdmin = "-";
	private String oldMember = "-";
	private String userToLeave = "-";
	private int moneyUsed = 0;
	private String userKicked = "-";
	private String userJoined = "-";
	private String clanTag = "-";
	private String messageTypeString = "-";
	
	public ClanMessage writeResponse(String r) {
		this.userMessageResponser = r;
		return this;
	}
	
	public ClanMessage writeOldAdmin(String r) {
		this.oldAdmin = r;
		return this;
	}
	
	public ClanMessage writeOldMember(String r) {
		this.oldMember = r;
		return this;
	}
	
	public ClanMessage writeClanName(String r) {
		this.clanName = r;
		return this;
	}
	
	public ClanMessage writeUserToLeave(String r) {
		this.userToLeave = r;
		return this;
	}
	
	public ClanMessage writeMoneyUsed(int r) {
		this.moneyUsed = r;
		return this;
	}
	
	public ClanMessage writeUserKicked(String r) {
		this.userKicked = r;
		return this;
	}
	
	public ClanMessage writeUserJoined(String r) {
		this.userJoined = r;
		return this;
	}
	
	public ClanMessage writeClanTag(String r) {
		this.clanTag = r;
		return this;
	}
	
	public ClanMessage writeType(MessageType r) {
		this.messageTypeString = r.name();
		return this;
	}
 	
	public MessageType readType() {
		return MessageType.valueOf(messageTypeString);
	}

	public enum MessageType {
		LEAVE, KICK, JOIN, CREATE, DISBAND, CHANGEABB, RECOVERY_OWNER, RECOVERY_ADMIN, RECOVERY_MEMBER;
	}
}
