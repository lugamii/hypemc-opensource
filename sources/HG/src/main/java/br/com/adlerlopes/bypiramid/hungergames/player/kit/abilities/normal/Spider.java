package br.com.adlerlopes.bypiramid.hungergames.player.kit.abilities.normal;

import br.com.adlerlopes.bypiramid.hungergames.player.kit.*;
import br.com.adlerlopes.bypiramid.hungergames.manager.*;
import org.bukkit.inventory.*;
import org.bukkit.event.entity.*;
import org.bukkit.block.*;
import org.bukkit.scheduler.*;
import java.util.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.*;
import org.bukkit.entity.*;

public class Spider extends Kit
{
    public Spider(final Manager manager) {
        super(manager);
        this.setPrice(21000);
        this.setCooldownTime(30.0);
        this.setIcon(new ItemStack(Material.SNOW_BALL));
        this.setFree(false);
        this.setDescription("Lan\u00e7e sua teia e prenda seus inimigos nela.");
        this.setRecent(false);
        this.setItems(this.createItemStack("§aSpider", Material.SNOW_BALL, 64));
    }
    
    @EventHandler
    public void onSpiderCatch(final ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("Spiderball")) {
            final List<Block> webs = new ArrayList<Block>();
            final Location loc = event.getEntity().getLocation();
            final int x = new Random().nextInt(2) - 1;
            final int z = new Random().nextInt(2) - 1;
            for (int y = 0; y < 2; ++y) {
                for (int xx = 0; xx < 2; ++xx) {
                    for (int zz = 0; zz < 2; ++zz) {
                        final Block b = loc.clone().add((double)(x + xx), (double)y, (double)(z + zz)).getBlock();
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.WEB);
                            webs.add(b);
                        }
                    }
                }
            }
            event.getEntity().remove();
            new BukkitRunnable() {
                public void run() {
                    for (final Block web : webs) {
                        web.setType(Material.AIR);
                    }
                }
            }.runTaskLater((Plugin)this.getManager().getPlugin(), 200L);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (this.hasKit(p)) {
            final ItemStack itemInHand = p.getItemInHand();
            if (itemInHand.getType() == this.getIcon().getType() && itemInHand.hasItemMeta() && itemInHand.getItemMeta().getDisplayName().equals("§aSpider")) {
                event.setCancelled(true);
                if (!this.inCooldown(p)) {
                    this.addCooldown(p, 25.0);
                    p.throwSnowball().setMetadata("Spiderball", (MetadataValue)new FixedMetadataValue((Plugin)this.getManager().getPlugin(), (Object)"Spiderball"));
                }
                else {
                    this.sendCooldown(p);
                }
                p.updateInventory();
            }
        }
    }
}
