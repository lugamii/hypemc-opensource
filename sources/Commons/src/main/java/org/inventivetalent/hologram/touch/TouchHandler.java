package org.inventivetalent.hologram.touch;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.inventivetalent.hologram.Hologram;

public abstract interface TouchHandler {
	
	public abstract void onTouch(@Nonnull Hologram hologram, @Nonnull Player player, @Nonnull TouchAction touchAction);
}
