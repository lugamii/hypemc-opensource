package br.com.weavenmc.commons.bukkit.api.bossbar.entity;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarEntity;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.boss.ViaBossBar;

public class GenericBoss extends BossBarEntity {

    private ViaBossBar bossBar;

    public GenericBoss(Player player) {
        super(player);
        spawn();
    }

    @Override
    public boolean isAlive() {
        return bossBar != null;
    }

    @Override
    public void spawn() {
        if (!isAlive()) {
            bossBar = new ViaBossBar("", 1F, BossColor.PINK, BossStyle.SOLID);
            bossBar.addPlayer(getPlayer());
        }
    }

    @Override
    public void remove() {
        if (isAlive()) {
            bossBar.removePlayer(getPlayer());
            bossBar = null;
        }
    }

    @Override
    public boolean setTitle(String title) {
        if (isAlive()) {
            if (!Objects.equals(this.title, title)) {
                this.bossBar.setTitle(title);
                this.title = title;
            }
        }
        return false;
    }

    @Override
    public boolean setHealth(float percent) {
        if (isAlive()) {
            float health = percent / 100F;
            if (!Objects.equals(this.health, health)) {
                this.bossBar.setHealth(health);
                this.health = health;
            }
        }
        return false;
    }

    @Override
    public void update() {
        
    }

    @Override
    public void move(PlayerMoveEvent event) {
        
    }
}
