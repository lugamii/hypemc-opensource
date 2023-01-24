package br.com.weavenmc.lobby.npcs;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import br.com.weavenmc.lobby.Lobby;
import br.com.weavenmc.lobby.managers.NpcManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;

public class PacketReader {

	Player player;
	Channel channel;
	Map<Player, Long> buff;

	public PacketReader(Player player) {
		this.buff = new HashMap<>();
		this.player = player;
	}

	public void inject() {
		CraftPlayer cPlayer = (CraftPlayer) this.player;
		this.channel = (cPlayer.getHandle()).playerConnection.networkManager.channel;
		this.channel.pipeline().addAfter("decoder", "PacketInjector",
				(ChannelHandler) new MessageToMessageDecoder<Packet<?>>() {
					protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2)
							throws Exception {
						arg2.add(packet);
						PacketReader.this.readPacket(packet);
					}
				});
	}

	public void uninject() {
		if (this.channel.pipeline().get("PacketInjector") != null)
			this.channel.pipeline().remove("PacketInjector");
	}

	public void readPacket(Packet<?> packet) {

		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			NpcManager npcManager = Lobby.getPlugin().getNpcManager();

			if (buff.containsKey(player)) {
				if (!new Date().after(new Date(buff.get(player)))) {
					return;
				}
			}

			int id = ((Integer) getValue(packet, "a")).intValue();
			if (id == NpcManager.getPvpNpc().getEntityID()) {
				npcManager.connectMessage(player, "pvp");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
			} else
			if (id == NpcManager.getGladiatorNpc().getEntityID()) {
				npcManager.connectMessage(player, "gladiator");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
			} else if (id == NpcManager.getHgNpc().getEntityID()) {
				npcManager.connectMessage(player, "lobby-hg");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
			}  else if (id == NpcManager.getSkywarsNpc().getEntityID()) {
				npcManager.connectMessage(player, "lobby-sw");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND, 3);
				buff.put(player, c.getTimeInMillis());
			} 


		}
	}

	public void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception exception) {
		}
	}

	public Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception exception) {
			return null;
		}
	}
}