package br.com.weavenmc.commons.bukkit.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.event.account.PlayerChangeLeagueEvent;
import br.com.weavenmc.commons.core.account.League;
import br.com.weavenmc.commons.core.account.Tag;
import br.com.weavenmc.commons.core.account.WeavenPlayer;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BukkitPlayer extends WeavenPlayer {
	@Setter
	private String fakeName = "";
	private List<Tag> tags = new ArrayList<>();
	@Setter
	private UUID lastTell;

	public BukkitPlayer(UUID uniqueId, String name) {
		super(uniqueId, name);
		WeavenMC.getAsynchronousExecutor().runAsync(() -> {
		load(DataCategory.HUNGERGAMES, DataCategory.GLADIATOR, DataCategory.KITPVP);
		});
	}

	public void setFake(String fakeName) {
		this.fakeName = fakeName;
	}

	public void activateDoubleXp() {
		getData(DataType.LASTACTIVATEDMULTIPLIER).setValue(System.currentTimeMillis() + WeavenMC.MULTIPLIER_DURATION);
		removeDoubleXpMultiplier(1);
	}

	public void resetFake() {
		fakeName = "";
	}

	public boolean isUsingFake() {
		return !fakeName.isEmpty();
	}

	@Override
	public void addXp(int i) {
		super.addXp(i);
		checkLeague();
	}

	public void updateTags() {
		tags.clear();
		for (Tag tag : Tag.values()) {
			if (hasGroupPermission(tag.getGroupToUse())) {
				tags.add(tag);
			}
		}
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void checkLeague() {
		League currentLeague = getLeague();
		League futureLeague = League.fromLeagueAndExp(currentLeague, getXp());
		if (currentLeague != futureLeague) {
			if (Bukkit.getPlayer(getUniqueId()) == null)
				return;
			PlayerChangeLeagueEvent event = new PlayerChangeLeagueEvent(Bukkit.getPlayer(getUniqueId()), currentLeague,
					futureLeague);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				getData(DataType.CURRENT_LEAGUE).setValue(event.getFutureLeague().name());
				getData(DataType.XP).setValue(0);
				save(DataCategory.BALANCE);
			}
		}
	}

	public static BukkitPlayer getPlayer(UUID uuid) {
		return (BukkitPlayer) WeavenMC.getAccountCommon().getWeavenPlayer(uuid);
	}
}
