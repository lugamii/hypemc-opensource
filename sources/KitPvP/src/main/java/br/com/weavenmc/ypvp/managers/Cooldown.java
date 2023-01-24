package br.com.weavenmc.ypvp.managers;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.entity.Player;

import br.com.weavenmc.commons.util.string.StringTimeUtils;
import br.com.weavenmc.ypvp.ability.Ability;
import lombok.Getter;

@Getter
public class Cooldown {

	private Player p;
	private Ability ability;
	private int seconds;
	private Calendar c;

	public Cooldown(Player p, Ability ability, int seconds) {
		this.p = p;
		this.ability = ability;
		this.seconds = seconds;
		c = Calendar.getInstance();
		c.add(Calendar.SECOND, seconds);
	}

	public void nullable() {
		p = null;
		ability = null;
		seconds = 0;
		c = null;
	}
	
	public String time() {
		return StringTimeUtils.formatDifference(c.getTimeInMillis());
	}

	public boolean has() {
		if (new Date().after(c.getTime()))
			return false;
		return true;
	}

	public void sendCooldown() {
		p.sendMessage("§e§lCOOLDOWN §fVocê está em §e§lCOOLDOWN§f, aguarde §b§l" + time() + "§f!");
	}

}
