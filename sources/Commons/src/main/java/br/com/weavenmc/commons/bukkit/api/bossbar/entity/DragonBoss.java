package br.com.weavenmc.commons.bukkit.api.bossbar.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import br.com.weavenmc.commons.bukkit.api.bossbar.BossBarEntity;

public class DragonBoss extends BossBarEntity {

    public DragonBoss(Player player) {
        super(player);
        spawn();
    }

    @Override
    public void spawn() {
        if (!isAlive()) {
            setAlive(true);
            Location dragon = getPlayer().getLocation().clone();
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            packet.getDataWatcherModifier().write(0, new WrappedDataWatcher());
            packet.getIntegers().write(0, getId());
            packet.getIntegers().write(1, 63);
            packet.getIntegers().write(2, dragon.getBlockX() * 32);
            packet.getIntegers().write(3, -9600);
            packet.getIntegers().write(4, dragon.getBlockZ() * 32);
            sendPacket(getPlayer(), packet);
        }
    }

    @Override
    public void remove() {
        if (isAlive()) {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getIntegerArrays().write(0, new int[]{getId()});
            sendPacket(getPlayer(), packet);
            setAlive(false);
        }
    }

    @Override
    public void update() {
        if (isAlive()) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, (byte) 0x20);
            watcher.setObject(2, this.title);
            watcher.setObject(3, (byte) 1);
            watcher.setObject(6, this.health);
            watcher.setObject(10, this.title);
            watcher.setObject(11, (byte) 1);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
            packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
            packet.getIntegers().write(0, getId());
            sendPacket(getPlayer(), packet);
        }
    }

    @Override
    public void move(PlayerMoveEvent event) {
        if (isAlive()) {
            Location to = event.getTo();
            Location from = event.getFrom();
            if ((to.getBlockX() != from.getBlockX()) && (to.getBlockY() != from.getBlockY()) && (to.getBlockZ() != from.getBlockZ())) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
                packet.getIntegers().write(0, getId());
                packet.getIntegers().write(1, to.getBlockX() * 32);
                packet.getIntegers().write(2, -9600);
                packet.getIntegers().write(3, to.getBlockZ() * 32);
                sendPacket(getPlayer(), packet);
            }
        }
    }

}
