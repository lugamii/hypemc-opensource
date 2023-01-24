package br.com.weavenmc.skywars.hability.cooldown;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class ItemCooldown extends Cooldown {

    @Getter
    private ItemStack item;

    @Getter
    @Setter
    private boolean selected;

    public ItemCooldown(ItemStack item, String name, Long duration) {
        super(name, duration);
        this.item = item;
    }

}
