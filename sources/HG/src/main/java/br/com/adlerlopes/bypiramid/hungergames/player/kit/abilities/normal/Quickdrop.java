package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import br.com.adlerlopes.bypiramid.hungergames.player.kit.*;
import br.com.adlerlopes.bypiramid.hungergames.manager.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public class Quickdrop extends Kit
{
    public Quickdrop(final Manager manager) {
        super(manager);
        this.setPrice(31000);
        this.setCooldownTime(30.0);
        this.setIcon(new ItemStack(Material.BOWL));
        this.setFree(true);
        this.setDescription("Drope seus potes automaticamente ao tomar sopa.");
        this.setRecent(false);
    }
}
