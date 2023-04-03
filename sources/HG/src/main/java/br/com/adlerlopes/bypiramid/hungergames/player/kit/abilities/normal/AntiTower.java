package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import br.com.adlerlopes.bypiramid.hungergames.player.kit.*;
import br.com.adlerlopes.bypiramid.hungergames.manager.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public class AntiTower extends Kit {
	public AntiTower(final Manager manager) {
		super(manager);
		this.setPrice(42500);
		this.setCooldownTime(0.0);
		this.setIcon(new ItemStack(Material.LEATHER_CHESTPLATE));
		this.setFree(true);
		this.setDescription("N\u00e3o seja puxado por endermages e nao morra para stompers.");
		this.setRecent(false);
	}
}
