package org.inventivetalent.hologram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.hologram.touch.TouchHandler;
import org.inventivetalent.hologram.view.ViewHandler;

import br.com.weavenmc.commons.bukkit.BukkitMain;

public class DefaultHologram extends CraftHologram {

	private Location location;
	private String text;
	private boolean touchable;
	private boolean spawned;
	private boolean isAttached;
	private Entity attachedTo;
	private List<TouchHandler> touchHandlers = new ArrayList<>();
	private List<ViewHandler> viewHandlers = new ArrayList<>();
	private Hologram lineBelow;
	private Hologram lineAbove;
	private BukkitRunnable updater;

	protected DefaultHologram(@Nonnull Location loc, String text) {
		if (loc == null) {
			throw new IllegalArgumentException("location cannot be null");
		}
		this.location = loc;
		this.text = text;
	}

	public boolean isSpawned() {
		return this.spawned;
	}

	public void spawn(long ticks) {
		if (ticks < 1L) {
			throw new IllegalArgumentException("ticks must be at least 1");
		}
		spawn();
		new BukkitRunnable() {
			public void run() {
				DefaultHologram.this.despawn();
			}
		}.runTaskLater(BukkitMain.getInstance(), ticks);
	}

	public boolean spawn() {
		validateDespawned();
		if (!packetsBuilt) {
			try {
				buildPackets(false);
				this.packetsBuilt = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			this.spawned = HologramAPI.spawn(this, getLocation().getWorld().getPlayers());
			if(lineBelow != null)
				HologramAPI.spawn(((DefaultHologram) this).lineBelow, getLocation().getWorld().getPlayers());
			if(lineAbove != null)
				HologramAPI.spawn(((DefaultHologram) this).lineAbove, getLocation().getWorld().getPlayers());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spawned;
	}

	public boolean despawn() {
		validateSpawned();
		try {
			this.spawned = (!HologramAPI.despawn(this, getLocation().getWorld().getPlayers()));
			if(lineBelow != null)
				HologramAPI.despawn(((DefaultHologram) this).lineBelow, getLocation().getWorld().getPlayers());
			if(lineAbove != null)
				HologramAPI.despawn(((DefaultHologram) this).lineAbove, getLocation().getWorld().getPlayers());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return !spawned;
	}

	public void setLocation(Location loc) {
		move(loc);
	}

	public Location getLocation() {
		return this.location.clone();
	}

	public void setText(String text) {
		this.text = text;
		if (isSpawned()) {
			try {
				buildPackets(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendNamePackets(getLocation().getWorld().getPlayers());
		}
	}

	public String getText() {
		return text;
	}

	public void update() {
		setText(getText());
	}

	public void update(long interval) {
		if (interval == -1L) {
			if (updater == null) {
				throw new IllegalStateException("Not updating");
			}
			updater.cancel();
			updater = null;
			return;
		}
		if (updater != null) {
			throw new IllegalStateException("Already updating");
		}
		if (interval < 1L) {
			throw new IllegalArgumentException("Interval must be at least 1");
		}
		updater = new BukkitRunnable() {
			public void run() {
				DefaultHologram.this.update();
			}
		};
		updater.runTaskTimer(BukkitMain.getInstance(), interval, interval);
	}

	public void move(@Nonnull Location loc) {
		if (loc == null) {
			throw new IllegalArgumentException("location cannot be null");
		}
		if (location.equals(loc)) {
			return;
		}
		if (!location.getWorld().equals(loc.getWorld())) {
			throw new IllegalArgumentException("cannot move to different world");
		}
		this.location = loc;
		if (isSpawned()) {
			try {
				buildPackets(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendTeleportPackets(getLocation().getWorld().getPlayers(), true, true);
		}
	}

	public void setTouchable(boolean flag) {
		validateTouchEnabled();
		if (flag == isTouchable()) {
			return;
		}
		this.touchable = flag;
		if (isSpawned()) {
			try {
				buildTouch(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendSpawnPackets(getLocation().getWorld().getPlayers(), false, true);
		}
	}

	public boolean isTouchable() {
		return touchable;
	}

	public void addTouchHandler(TouchHandler handler) {
		validateTouchEnabled();
		if (!isTouchable()) {
			throw new IllegalStateException("Hologram is not touchable");
		}
		touchHandlers.add(handler);
	}

	public void removeTouchHandler(TouchHandler handler) {
		validateTouchEnabled();
		if (!isTouchable()) {
			throw new IllegalStateException("Hologram is not touchable");
		}
		touchHandlers.remove(handler);
	}

	public Collection<TouchHandler> getTouchHandlers() {
		return new ArrayList<>(touchHandlers);
	}

	public void clearTouchHandlers() {
		for (TouchHandler handler : getTouchHandlers()) {
			removeTouchHandler(handler);
		}
	}

	public void addViewHandler(ViewHandler handler) {
		validateViewsEnabled();
		viewHandlers.add(handler);
	}

	public void removeViewHandler(ViewHandler handler) {
		validateViewsEnabled();
		viewHandlers.remove(handler);
	}

	public Collection<ViewHandler> getViewHandlers() {
		return new ArrayList<>(viewHandlers);
	}

	public void clearViewHandlers() {
		for (ViewHandler handler : getViewHandlers()) {
			removeViewHandler(handler);
		}
	}

	public Hologram addLineBelow(String text) {
		validateSpawned();
		Hologram hologram = HologramAPI.createHologram(getLocation().subtract(0.0D, 0.25D, 0.0D), text);
		this.lineBelow = hologram;
		((DefaultHologram) hologram).lineAbove = this;
		hologram.spawn();
		return hologram;
	}

	public Hologram getLineBelow() {
		validateSpawned();
		return lineBelow;
	}

	public boolean removeLineBelow() {
		if (getLineBelow() != null) {
			if (getLineBelow().isSpawned()) {
				getLineBelow().despawn();
			}
			this.lineBelow = null;
			return true;
		}
		return false;
	}

	public Collection<Hologram> getLinesBelow() {
		List<Hologram> list = new ArrayList<>();

		Hologram current = this;
		while ((current = ((DefaultHologram) current).lineBelow) != null) {
			list.add(current);
		}
		return list;
	}

	public Hologram addLineAbove(String text) {
		validateSpawned();
		Hologram hologram = HologramAPI.createHologram(getLocation().add(0.0D, 0.25D, 0.0D), text);
		this.lineAbove = hologram;
		((DefaultHologram) hologram).lineBelow = this;
		hologram.spawn();
		return hologram;
	}

	public Hologram getLineAbove() {
		validateSpawned();
		return lineAbove;
	}

	public boolean removeLineAbove() {
		if (getLineAbove() != null) {
			if (getLineAbove().isSpawned()) {
				getLineAbove().despawn();
			}
			this.lineAbove = null;
			return true;
		}
		return false;
	}

	public Collection<Hologram> getLinesAbove() {
		List<Hologram> list = new ArrayList<>();

		Hologram current = this;
		while ((current = ((DefaultHologram) current).lineAbove) != null) {
			list.add(current);
		}
		return list;
	}

	public Collection<Hologram> getLines() {
		List<Hologram> list = new ArrayList<>();
		list.addAll(getLinesAbove());
		list.add(this);
		list.addAll(getLinesBelow());
		return list;
	}

	public void setAttachedTo(Entity attachedTo) {
		if (this.attachedTo == attachedTo) {
			return;
		}
		setAttached(attachedTo != null);
		if (attachedTo != null) {
			this.attachedTo = attachedTo;
		}
		if (isSpawned()) {
			try {
				buildPackets(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendAttachPacket(getLocation().getWorld().getPlayers());
		}
		this.attachedTo = attachedTo;
	}

	public Entity getAttachedTo() {
		return attachedTo;
	}

	public boolean isAttached() {
		return isAttached;
	}

	public void setAttached(boolean isAttached) {
		this.isAttached = isAttached;
	}

	private void validateTouchEnabled() {
		if (!HologramAPI.packetsEnabled()) {
			throw new IllegalStateException("Touch-holograms are not enabled");
		}
	}

	private void validateViewsEnabled() {
		if (!HologramAPI.packetsEnabled()) {
			throw new IllegalStateException("ViewHandlers are not enabled");
		}
	}

	private void validateSpawned() {
		if (!spawned) {
			throw new IllegalStateException("Not spawned");
		}
	}

	private void validateDespawned() {
		if (spawned) {
			throw new IllegalStateException("Already spawned");
		}
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (location == null ? 0 : location.hashCode());
		result = 31 * result + (spawned ? 1231 : 1237);
		result = 31 * result + (text == null ? 0 : text.hashCode());
		result = 31 * result + (touchHandlers == null ? 0 : touchHandlers.hashCode());
		result = 31 * result + (touchable ? 1231 : 1237);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultHologram other = (DefaultHologram) obj;
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (spawned != other.spawned) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (touchHandlers == null) {
			if (other.touchHandlers != null) {
				return false;
			}
		} else if (!touchHandlers.equals(other.touchHandlers)) {
			return false;
		}
		if (touchable != other.touchable) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "{\"location\":\"" + location + "\",\"text\":\"" + text + "\",\"touchable\":\"" + touchable
				+ "\",\"spawned\":\"" + spawned + "\",\"touchHandlers\":\"" + touchHandlers + "\"}";
	}
}
