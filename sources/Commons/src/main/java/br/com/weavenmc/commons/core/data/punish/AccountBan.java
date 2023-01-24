package br.com.weavenmc.commons.core.data.punish;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountBan {

	private UUID accountId;
	private String author;
	private String reason;	
	private long appliedTime = 0L;
	private long duration = -1;
	
	public boolean hasExpired() {
		return !isPermanent() && duration < System.currentTimeMillis();
	}
	
	public boolean isPermanent() {
		return duration == -1;
	}
	
	public String toDateApplied() {
		Date date = new Date(appliedTime);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        return df2.format(date);
	}
}
