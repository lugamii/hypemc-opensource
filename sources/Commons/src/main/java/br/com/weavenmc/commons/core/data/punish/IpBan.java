package br.com.weavenmc.commons.core.data.punish;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IpBan {

	private String address;	
	private String author;
	private String reason;	
	private long appliedTime = 0L;
	
	public String toDateApplied() {
		Date date = new Date(appliedTime);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        return df2.format(date);
	}
}
