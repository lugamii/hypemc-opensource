package br.com.weavenmc.skywars.hability.cooldown;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CooldownEvent extends Event {

    @Getter
    @NonNull
    private Player player;

    @Getter
    @NonNull
    private Cooldown cooldown;

}
