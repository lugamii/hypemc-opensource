package br.com.weavenmc.commons.bukkit.protocol;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ProtocolVersion {

    MINECRAFT_1_11_1(316), 
    MINECRAFT_1_11(315), 
    MINECRAFT_1_10(210), 
    MINECRAFT_1_9_4(110), 
    MINECRAFT_1_9_2(109), 
    MINECRAFT_1_9_1(108), 
    MINECRAFT_1_9(107), 
    MINECRAFT_1_8(47), 
    MINECRAFT_1_7_10(5), 
    MINECRAFT_1_7_5(4), 
    UNKNOWN(-1); 

    @Getter
    @NonNull
    private Integer id;

    public static ProtocolVersion getById(int id) {
        return Stream.of(values()).filter(e -> e.getId() == id).findFirst().orElse(UNKNOWN);
    }
}
