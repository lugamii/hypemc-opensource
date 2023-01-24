package br.com.weavenmc.lobby.gamer;

import java.util.UUID;

import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.scoreboard.Sidebar;
import br.com.weavenmc.lobby.enums.Particles;
import br.com.weavenmc.lobby.enums.Wings;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Gamer {

	private String name;
	private UUID uniqueId;

	@Setter
	private Sidebar sidebar;
	@Setter
	private boolean flying = false;

	@Setter
	private boolean usingParticle = false, isCape = false;
	@Setter
	private Particles particle;
	@Setter
	private Wings wing;
	@Setter
	private double alpha = 0;

	public Gamer(BukkitPlayer bP) {
		this.name = bP.getName();
		this.uniqueId = bP.getUniqueId();
	}
}
