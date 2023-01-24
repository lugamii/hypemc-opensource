package br.com.weavenmc.commons.core.permission;

import lombok.Getter;

@Getter
public class GroupMessage {

	private String userName;
	private String messageResponse;
	private String groupName;
	private int groupId;
	private long groupTime;
	private boolean findPlayer;
	private boolean add;
	
	public GroupMessage writeUser(String r) {
		this.userName = r;
		return this;
	}
	
	public GroupMessage writeResponse(String r) {
		this.messageResponse = r;
		return this;
	}
	
	public GroupMessage writeGroup(Group group) {
		this.groupName = group.name();
		return this;
	}
	
	public GroupMessage writeFindPlayer(boolean r) {
		this.findPlayer = r;
		return this;
	}
	
	public GroupMessage writeTime(long groupTime) {
		this.groupTime = groupTime;
		return this;
	}
	
	public GroupMessage writeAdd(boolean r) {
		this.add = r;
		return this;
	}
	
	public GroupMessage writeGroupId(int r) {
		this.groupId = r;
		return this;
	}
	
	public Group readGroup() {
		return Group.valueOf(groupName);
	}
}
