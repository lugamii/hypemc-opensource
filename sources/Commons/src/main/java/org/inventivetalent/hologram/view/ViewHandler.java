package org.inventivetalent.hologram.view;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.inventivetalent.hologram.Hologram;

public abstract interface ViewHandler {
	
	public abstract String onView(@Nonnull Hologram hologram, @Nonnull Player player, @Nonnull String str);
}
