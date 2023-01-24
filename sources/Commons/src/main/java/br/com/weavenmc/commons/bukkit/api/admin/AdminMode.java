package br.com.weavenmc.commons.bukkit.api.admin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeEnterEvent;
import br.com.weavenmc.commons.bukkit.event.admin.PlayerAdminModeQuitEvent;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;;

public class AdminMode {
	private Set<UUID> admin;
	private static final AdminMode instance = new AdminMode();

	public AdminMode() {
		admin = new HashSet<UUID>();
	}

	public static AdminMode getInstance() {
		return instance;
	}

	public void setAdmin(Player p) {
		PlayerAdminModeEnterEvent event = new PlayerAdminModeEnterEvent(p);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if (!admin.contains(p.getUniqueId()))
			admin.add(p.getUniqueId());
		p.setGameMode(GameMode.CREATIVE);
		Group group = VanishAPI.getInstance().hidePlayer(p);
		p.sendMessage("§dModo Admin ATIVADO");
		p.sendMessage("§dVocê está INVISÍVEL para " + group.name() + " e abaixo!");
	}

	public void setPlayer(Player p) {
		PlayerAdminModeQuitEvent event = new PlayerAdminModeQuitEvent(p);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if (admin.contains(p.getUniqueId())) {
			p.sendMessage("§dModo Admin DESATIVADO");
			admin.remove(p.getUniqueId());
		}
		p.sendMessage("§dVocê está VISIVEL para TODOS OS PLAYERS");
		p.setGameMode(GameMode.SURVIVAL);
		VanishAPI.getInstance().showPlayer(p);
	}

	public boolean isAdmin(Player p) {
		return p != null && admin.contains(p.getUniqueId());
	}

	public int playersInAdmin() {
		return admin.size();
	}

	public void removeAdmin(Player p) {
		admin.remove(p.getUniqueId());
	}
}
