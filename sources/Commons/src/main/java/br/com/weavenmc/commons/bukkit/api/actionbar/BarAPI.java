package br.com.weavenmc.commons.bukkit.api.actionbar;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import br.com.weavenmc.commons.bukkit.BukkitMain;
import br.com.weavenmc.commons.bukkit.protocol.ProtocolGetter;
import br.com.weavenmc.commons.bukkit.protocol.ProtocolVersion;

public class BarAPI {

    public static void send(Player player, String text) {
        ProtocolVersion version = ProtocolGetter.getVersion(player);
        if (version != ProtocolVersion.MINECRAFT_1_7_5 && version != ProtocolVersion.MINECRAFT_1_7_10) {
            PacketContainer chatPacket = new PacketContainer(PacketType.Play.Server.CHAT);
            chatPacket.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\"" + text + " \"}"));
            chatPacket.getBytes().write(0, (byte) 2);
            try {
                BukkitMain.getInstance().getProcotolManager().sendServerPacket(player, chatPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet " + chatPacket, e);
            }
        }
    }

    public static void broadcast(String text) {
        Bukkit.getOnlinePlayers().forEach(p -> send(p, text));
    }
}
