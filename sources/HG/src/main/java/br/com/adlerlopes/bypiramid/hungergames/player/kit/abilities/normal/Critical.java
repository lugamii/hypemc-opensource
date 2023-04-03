package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import br.com.adlerlopes.bypiramid.hungergames.player.kit.*;
import br.com.adlerlopes.bypiramid.hungergames.manager.*;
import org.bukkit.inventory.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class Critical extends Kit
{
    public Critical(final Manager manager) {
        super(manager);
        this.setPrice(31000);
        this.setCooldownTime(30.0);
        this.setIcon(new ItemStack(Material.REDSTONE_BLOCK));
        this.setFree(false);
        this.setDescription("Tenha 30% de chance de dar um golpe critico (+1.5 de dano)");
        this.setRecent(false);
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player)event.getEntity();
            Player d = (Player)event.getDamager();
            if (this.hasKit(d) && !event.isCancelled()) {
                Integer chance = new Random().nextInt(100);
                if (chance > 0 && chance <= 30) {
                    event.setDamage(event.getDamage() + 1.5);
                    d.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, (Object)Material.REDSTONE_BLOCK, 10);
                    e.sendMessage("§5§lCRITICAL§f Voc\u00ea recebeu um §9§lGOLPE CRITICO§f de §9§l" + d.getName());
                    d.sendMessage("§5§lCRITICAL§f Voc\u00ea deu um §9§lGOLPE CRITICO§f no §9§l" + e.getName());
                }
                chance = null;
            }
            e = null;
            d = null;
        }
    }
}
