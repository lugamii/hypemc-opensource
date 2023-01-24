package br.com.weavenmc.commons.core.data.punish;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Mute {

	private UUID accountId;
	
	private String mutedBy;
	
	private long muteTime;
	private String reason;
	
	private long appliedTime = 0L;
	
	public boolean isPermanent() {
		return muteTime == -1;
	}
	
	public boolean hasExpired() {
		return muteTime != -1 && muteTime < System.currentTimeMillis();
	}
	
	public String toDateApplied() {
		Date date = new Date(appliedTime);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        return df2.format(date);
	}
}
