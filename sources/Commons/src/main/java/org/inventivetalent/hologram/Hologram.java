package org.inventivetalent.hologram;

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.inventivetalent.hologram.touch.TouchHandler;
import org.inventivetalent.hologram.view.ViewHandler;

public abstract interface Hologram {
	
	public abstract boolean isSpawned();

	public abstract void spawn(@Nonnull @Nonnegative long paramLong);

	public abstract boolean spawn();

	public abstract boolean despawn();

	public abstract void setText(@Nullable String paramString);

	public abstract String getText();

	public abstract void update();

	public abstract void update(long paramLong);

	public abstract void setLocation(@Nonnull Location paramLocation);

	public abstract Location getLocation();

	public abstract void move(@Nonnull Location paramLocation);

	public abstract void setTouchable(boolean paramBoolean);

	public abstract boolean isTouchable();

	public abstract void addTouchHandler(@Nonnull TouchHandler paramTouchHandler);

	public abstract void removeTouchHandler(@Nonnull TouchHandler paramTouchHandler);

	public abstract Collection<TouchHandler> getTouchHandlers();

	public abstract void clearTouchHandlers();

	public abstract void addViewHandler(@Nonnull ViewHandler paramViewHandler);

	public abstract void removeViewHandler(@Nonnull ViewHandler paramViewHandler);

	@Nonnull
	public abstract Collection<ViewHandler> getViewHandlers();

	public abstract void clearViewHandlers();

	@Nonnull
	public abstract Hologram addLineBelow(String paramString);

	@Nullable
	public abstract Hologram getLineBelow();

	public abstract boolean removeLineBelow();

	@Nonnull
	public abstract Collection<Hologram> getLinesBelow();

	@Nonnull
	public abstract Hologram addLineAbove(String paramString);

	@Nullable
	public abstract Hologram getLineAbove();

	public abstract boolean removeLineAbove();

	@Nonnull
	public abstract Collection<Hologram> getLinesAbove();

	@Nonnull
	public abstract Collection<Hologram> getLines();

	public abstract void setAttachedTo(@Nullable Entity paramEntity);

	@Nullable
	public abstract Entity getAttachedTo();
}
