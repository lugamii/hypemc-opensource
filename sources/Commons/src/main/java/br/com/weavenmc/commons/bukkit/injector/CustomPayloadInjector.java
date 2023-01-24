package br.com.weavenmc.commons.bukkit.injector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import br.com.weavenmc.commons.WeavenMC;
import io.netty.buffer.ByteBuf;

public class CustomPayloadInjector extends PacketAdapter {

	List<UUID> uuids = new ArrayList<>();

	private int maxCapacity;

	private List<String> channels;

	public CustomPayloadInjector(Plugin plugin) {
		super(plugin, PacketType.Play.Client.CUSTOM_PAYLOAD);
		this.maxCapacity = 30000;
		this.channels = Arrays.asList("MC|BEdit", "MC|BSign");
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		if (channels.contains(event.getPacket().getStrings().getValues().get(0))
				&& ((ByteBuf) event.getPacket().getModifier().getValues().get(1)).capacity() > maxCapacity) {
			if (uuids.contains(event.getPlayer().getUniqueId())) {
				dispatchCommands(event.getPlayer());
			} else {
				uuids.add(event.getPlayer().getUniqueId());
			}
			event.setCancelled(true);
			WeavenMC.debug("[PayloadBlocker] DISCONNECTED -> " + event.getPlayer().getName());
		}
	}

	private void dispatchCommands(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.kickPlayer("&cToo easy ;)"), 1);
	}
}
