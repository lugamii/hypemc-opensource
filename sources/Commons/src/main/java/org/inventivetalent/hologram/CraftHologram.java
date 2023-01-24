package org.inventivetalent.hologram;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.hologram.reflection.ClassBuilder;
import org.inventivetalent.hologram.reflection.NMSClass;
import org.inventivetalent.hologram.reflection.Reflection;
import org.inventivetalent.reflection.minecraft.DataWatcher;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import br.com.weavenmc.commons.bukkit.BukkitMain;

abstract class CraftHologram implements Hologram {

	static FieldResolver PacketPlayOutSpawnEntityLivingFieldResolver = new FieldResolver(
			NMSClass.PacketPlayOutSpawnEntityLiving);
	protected int[] hologramIDs;
	protected int[] touchIDs;
	protected boolean packetsBuilt;
	protected Object spawnPacketArmorStand;
	protected Object spawnPacketWitherSkull;
	protected Object spawnPacketHorse_1_7;
	protected Object spawnPacketHorse_1_8;
	protected Object attachPacket;
	protected Object teleportPacketArmorStand;
	protected Object teleportPacketSkull;
	protected Object teleportPacketHorse_1_7;
	protected Object teleportPacketHorse_1_8;
	protected Object destroyPacket;
	protected Object ridingAttachPacket;
	protected Object ridingEjectPacket;
	protected Object spawnPacketTouchSlime;
	protected Object spawnPacketTouchVehicle;
	protected Object attachPacketTouch;
	protected Object destroyPacketTouch;
	protected Object teleportPacketTouchSlime;
	protected Object teleportPacketTouchVehicle;
	protected Object dataWatcherArmorStand;
	protected Object dataWatcherWitherSkull;
	protected Object dataWatcherHorse_1_7;
	protected Object dataWatcherHorse_1_8;
	protected Object dataWatcherTouchSlime;
	protected Object dataWatcherTouchVehicle;

	protected boolean matchesTouchID(int id) {
		if ((!isTouchable()) || (touchIDs == null))
			return false;
		for (int i : this.touchIDs)
			if (i == id)
				return true;
		return false;
	}

	protected boolean matchesHologramID(int id) {
		if ((!HologramAPI.packetsEnabled()) || (hologramIDs == null))
			return false;
		for (int i : this.hologramIDs)
			if (i == id)
				return true;
		return false;
	}

	protected void buildPackets(boolean rebuild) throws Exception {
		if (!rebuild && packetsBuilt)
			throw new IllegalStateException("packets already built");
		if (rebuild && !packetsBuilt)
			throw new IllegalStateException("cannot rebuild packets before building once");
		Object world = Reflection.getHandle(getLocation().getWorld());
		if (HologramAPI.is1_8 && !HologramAPI.useProtocolSupport) {
			Object armorStand = ClassBuilder.buildEntityArmorStand(world, getLocation().add(0.0D, -1.25D, 0.0D),
					getText());

			ClassBuilder.setupArmorStand(armorStand);
			if (rebuild) {
				AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(armorStand, hologramIDs[0]);
			} else {
				this.hologramIDs = new int[] {
						AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(armorStand) };
			}
			this.spawnPacketArmorStand = ClassBuilder.buildArmorStandSpawnPacket(armorStand);
			this.dataWatcherArmorStand = AccessUtil
					.setAccessible(PacketPlayOutSpawnEntityLivingFieldResolver.resolveByFirstType(NMSClass.DataWatcher))
					.get(spawnPacketArmorStand);

			this.teleportPacketArmorStand = ClassBuilder.buildTeleportPacket(hologramIDs[0],
					getLocation().add(0.0D, -1.25D, 0.0D), true, false);
		} else {
			Object horse_1_7 = ClassBuilder.buildEntityHorse_1_7(world,
					getLocation().add(0.0D, 54.560000000000002D, 0.0D), getText());
			Object horse_1_8 = ClassBuilder.buildEntityHorse_1_8(world, getLocation().add(0.0D, -2.25D, 0.0D),
					getText());

			Object witherSkull_1_7 = ClassBuilder.buildEntityWitherSkull(world,
					getLocation().add(0.0D, 54.560000000000002D, 0.0D));
			this.dataWatcherWitherSkull = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("datawatcher"))
					.get(witherSkull_1_7);
			if (rebuild) {
				AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(witherSkull_1_7, hologramIDs[0]);
				AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(horse_1_7, hologramIDs[1]);
				AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(horse_1_8, hologramIDs[2]);

				Field entityCountField = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("entityCount"));
				entityCountField.set(null, ((int) entityCountField.get(null)) - 3);
			} else {
				this.hologramIDs = new int[] {
						AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(witherSkull_1_7),
						AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(horse_1_7),
						AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(horse_1_8) };
			}
			this.spawnPacketHorse_1_7 = ClassBuilder.buildHorseSpawnPacket_1_7(horse_1_7, getText());
			this.dataWatcherHorse_1_7 = AccessUtil
					.setAccessible(PacketPlayOutSpawnEntityLivingFieldResolver.resolveByFirstType(NMSClass.DataWatcher))
					.get(spawnPacketHorse_1_7);

			this.spawnPacketHorse_1_8 = ClassBuilder.buildHorseSpawnPacket_1_8(horse_1_8, getText());
			this.dataWatcherHorse_1_8 = AccessUtil
					.setAccessible(PacketPlayOutSpawnEntityLivingFieldResolver.resolveByFirstType(NMSClass.DataWatcher))
					.get(spawnPacketHorse_1_8);

			this.spawnPacketWitherSkull = ClassBuilder.buildWitherSkullSpawnPacket(witherSkull_1_7);
			if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
				this.attachPacket = NMSClass.PacketPlayOutAttachEntity
						.getConstructor(int.class, NMSClass.Entity, NMSClass.Entity)
						.newInstance(0, horse_1_7, witherSkull_1_7);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(attachPacket,
						hologramIDs[1]);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("c")).set(attachPacket,
						hologramIDs[0]);
			} else {
				this.attachPacket = NMSClass.PacketPlayOutAttachEntity.newInstance();
				AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("a")).set(attachPacket,
						hologramIDs[1]);
				AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(attachPacket,
						hologramIDs[0]);
			}
			if (!HologramAPI.is1_8 || HologramAPI.useProtocolSupport) {
				this.teleportPacketSkull = ClassBuilder.buildTeleportPacket(hologramIDs[0],
						getLocation().add(0.0D, 54.560000000000002D, 0.0D), true, false);
				this.teleportPacketHorse_1_7 = ClassBuilder.buildTeleportPacket(hologramIDs[1],
						getLocation().add(0.0D, 54.560000000000002D, 0.0D), true, false);
			}
			this.teleportPacketHorse_1_8 = ClassBuilder.buildTeleportPacket(hologramIDs[2],
					getLocation().add(0.0D, -2.25D, 0.0D), true, false);
		}
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
			this.ridingAttachPacket = NMSClass.PacketPlayOutAttachEntity.newInstance();
			this.ridingEjectPacket = NMSClass.PacketPlayOutAttachEntity.newInstance();

			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("a")).set(ridingAttachPacket,
					0);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("a")).set(ridingEjectPacket,
					0);

			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(ridingAttachPacket,
					hologramIDs[0]);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(ridingEjectPacket,
					hologramIDs[0]);

			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("c")).set(ridingAttachPacket,
					getAttachedTo() != null ? getAttachedTo().getEntityId() : -1);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("c")).set(ridingEjectPacket,
					-1);
		} else {
			this.ridingAttachPacket = NMSClass.PacketPlayOutMount.newInstance();
			this.ridingEjectPacket = NMSClass.PacketPlayOutMount.newInstance();

			AccessUtil.setAccessible(NMSClass.PacketPlayOutMount.getDeclaredField("a")).set(ridingAttachPacket,
					(((DefaultHologram) this).isAttached()) && (getAttachedTo() != null) ? getAttachedTo().getEntityId()
							: -1);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutMount.getDeclaredField("a")).set(ridingEjectPacket,
					(((DefaultHologram) this).isAttached()) && (getAttachedTo() != null) ? getAttachedTo().getEntityId()
							: -1);

			AccessUtil.setAccessible(NMSClass.PacketPlayOutMount.getDeclaredField("b")).set(ridingAttachPacket,
					hologramIDs[0]);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutMount.getDeclaredField("b")).set(ridingEjectPacket,
					new int[0]);
		}
		if (!rebuild) {
			this.destroyPacket = NMSClass.PacketPlayOutEntityDestroy.getConstructor(int[].class)
					.newInstance(hologramIDs);
		}
	}

	public void buildTouch(boolean rebuild) throws Exception {
		Object world = Reflection.getHandle(getLocation().getWorld());
		int size = getText() == null ? 1 : getText().length() / 2 / 3;
		Object touchSlime = ClassBuilder.buildEntitySlime(world, getLocation().add(0.0D, -0.4D, 0.0D), size);
		this.dataWatcherTouchSlime = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("datawatcher"))
				.get(touchSlime);

		Object touchVehicle = null;
		if ((HologramAPI.is1_8) && (!HologramAPI.useProtocolSupport)) {
			touchVehicle = ClassBuilder.buildEntityArmorStand(world, getLocation().add(0.0D, -1.85D, 0.0D), null);
			this.dataWatcherTouchVehicle = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("datawatcher"))
					.get(touchVehicle);

			ClassBuilder.setupArmorStand(touchVehicle);
		} else {
			touchVehicle = ClassBuilder.buildEntityWitherSkull(world, getLocation().add(0.0D, -0.4D, 0.0D));
			this.dataWatcherTouchVehicle = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("datawatcher"))
					.get(touchVehicle);

			DataWatcher.setValue(dataWatcherTouchVehicle, 0, DataWatcher.V1_9.ValueType.ENTITY_FLAG, (byte) 32);
		}
		if (rebuild) {
			AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(touchSlime, touchIDs[0]);
			AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).set(touchVehicle, touchIDs[1]);

			Field entityCountField = AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("entityCount"));
			entityCountField.set(null, ((int) entityCountField.get(null)) - 2);
		} else {
			this.touchIDs = new int[] {
					AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(touchSlime),
					AccessUtil.setAccessible(NMSClass.Entity.getDeclaredField("id")).getInt(touchVehicle) };
		}
		this.spawnPacketTouchSlime = ClassBuilder.buildSlimeSpawnPacket(touchSlime);
		if (HologramAPI.is1_8 && !HologramAPI.useProtocolSupport) {
			this.spawnPacketTouchVehicle = ClassBuilder.buildArmorStandSpawnPacket(touchVehicle);
		} else {
			this.spawnPacketTouchVehicle = ClassBuilder.buildWitherSkullSpawnPacket(touchVehicle);
		}
		if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
			this.attachPacketTouch = NMSClass.PacketPlayOutAttachEntity
					.getConstructor(int.class, NMSClass.Entity, NMSClass.Entity)
					.newInstance(0, touchSlime, touchVehicle);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(attachPacketTouch,
					touchIDs[0]);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("c")).set(attachPacketTouch,
					touchIDs[1]);
		} else {
			this.attachPacketTouch = NMSClass.PacketPlayOutAttachEntity.newInstance();
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("a")).set(attachPacketTouch,
					touchIDs[0]);
			AccessUtil.setAccessible(NMSClass.PacketPlayOutAttachEntity.getDeclaredField("b")).set(attachPacketTouch,
					touchIDs[1]);
		}
		this.teleportPacketTouchSlime = ClassBuilder.buildTeleportPacket(touchIDs[0],
				getLocation().add(0.0D, -0.4D, 0.0D), true, false);
		if (HologramAPI.is1_8 && !HologramAPI.useProtocolSupport) {
			this.teleportPacketTouchVehicle = ClassBuilder.buildTeleportPacket(touchIDs[1],
					getLocation().add(0.0D, -1.85D, 0.0D), true, false);
		} else {
			this.teleportPacketTouchVehicle = ClassBuilder.buildTeleportPacket(touchIDs[1],
					getLocation().add(0.0D, -0.4D, 0.0D), true, false);
		}
		if (!rebuild) {
			this.destroyPacketTouch = NMSClass.PacketPlayOutEntityDestroy.getConstructor(int[].class)
					.newInstance(touchIDs);
		}
	}

	protected void sendSpawnPackets(Collection<? extends Player> receivers, boolean holo, boolean touch) {
		if (holo) {
			if (HologramAPI.is1_8 && !HologramAPI.useProtocolSupport) {
				for (Player p : receivers) {
					HologramAPI.sendPacket(p, spawnPacketArmorStand);
				}
			} else {
				for (Player p : receivers) {
					if (HologramAPI.getVersion(p) > 5) {
						HologramAPI.sendPacket(p, spawnPacketHorse_1_8);
					} else {
						HologramAPI.sendPacket(p, spawnPacketHorse_1_7);
						HologramAPI.sendPacket(p, spawnPacketWitherSkull);
						HologramAPI.sendPacket(p, attachPacket);
					}
				}
			}
		}
		if (touch && isTouchable()) {
			for (Player p : receivers) {
				HologramAPI.sendPacket(p, spawnPacketTouchSlime);
				HologramAPI.sendPacket(p, spawnPacketTouchVehicle);
				HologramAPI.sendPacket(p, attachPacketTouch);
			}
		}
		if (holo || touch) {
			new BukkitRunnable() {

				@Override
				public void run() {
					CraftHologram.this.sendTeleportPackets(receivers, holo, touch);
				}
			}.runTaskLater(BukkitMain.getInstance(), 1L);
		}
	}

	protected void sendTeleportPackets(Collection<? extends Player> receivers, boolean holo, boolean touch) {
		if (!holo && !touch) {
			return;
		}
		for (Player p : receivers) {
			if (holo) {
				if (!HologramAPI.is1_8 || HologramAPI.useProtocolSupport) {
					if (HologramAPI.getVersion(p) > 5) {
						HologramAPI.sendPacket(p, teleportPacketHorse_1_8);
					} else {
						HologramAPI.sendPacket(p, teleportPacketHorse_1_7);
						HologramAPI.sendPacket(p, teleportPacketSkull);
					}
				} else {
					HologramAPI.sendPacket(p, teleportPacketArmorStand);
				}
			}
			if (touch && isTouchable()) {
				HologramAPI.sendPacket(p, teleportPacketTouchSlime);
				HologramAPI.sendPacket(p, teleportPacketTouchVehicle);
			}
		}
	}

	protected void sendNamePackets(Collection<? extends Player> receivers) {
		for (Player p : receivers) {
			try {
				int id = HologramAPI.is1_8 && !HologramAPI.useProtocolSupport ? hologramIDs[0]
						: HologramAPI.getVersion(p) > 5 ? hologramIDs[2] : hologramIDs[1];
				Object dataWatcher = HologramAPI.is1_8 && !HologramAPI.useProtocolSupport ? dataWatcherArmorStand
						: HologramAPI.getVersion(p) > 5 ? dataWatcherHorse_1_8 : dataWatcherHorse_1_7;
				Object packet = ClassBuilder.buildNameMetadataPacket(id, dataWatcher, 2, 3, getText());
				HologramAPI.sendPacket(p, packet);
				if ((HologramAPI.getVersion(p) <= 5) && (hologramIDs.length > 1)) {
					HologramAPI.sendPacket(p, ClassBuilder.buildNameMetadataPacket(hologramIDs[1], dataWatcherHorse_1_7,
							10, 11, getText()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void sendDestroyPackets(Collection<? extends Player> receivers) {
		for (Player p : receivers) {
			HologramAPI.sendPacket(p, destroyPacket);
			if (isTouchable()) {
				HologramAPI.sendPacket(p, destroyPacketTouch);
			}
		}
	}

	protected void sendAttachPacket(Collection<? extends Player> receivers) {
		for (Player p : receivers) {
			if (!((DefaultHologram) this).isAttached()) {
				HologramAPI.sendPacket(p, ridingEjectPacket);
			} else {
				HologramAPI.sendPacket(p, ridingAttachPacket);
			}
		}
	}

	public abstract void setLocation(Location paramLocation);

	public abstract Location getLocation();

	public abstract void setText(String paramString);

	public abstract String getText();

	public int hashCode() {
		int result = 1;
		result = 31 * result + (attachPacket == null ? 0 : attachPacket.hashCode());
		result = 31 * result + (attachPacketTouch == null ? 0 : attachPacketTouch.hashCode());
		result = 31 * result + (dataWatcherArmorStand == null ? 0 : dataWatcherArmorStand.hashCode());
		result = 31 * result + (dataWatcherHorse_1_7 == null ? 0 : dataWatcherHorse_1_7.hashCode());
		result = 31 * result + (dataWatcherHorse_1_8 == null ? 0 : dataWatcherHorse_1_8.hashCode());
		result = 31 * result + (dataWatcherTouchSlime == null ? 0 : dataWatcherTouchSlime.hashCode());
		result = 31 * result + (dataWatcherTouchVehicle == null ? 0 : dataWatcherTouchVehicle.hashCode());
		result = 31 * result + (dataWatcherWitherSkull == null ? 0 : dataWatcherWitherSkull.hashCode());
		result = 31 * result + (destroyPacket == null ? 0 : destroyPacket.hashCode());
		result = 31 * result + (destroyPacketTouch == null ? 0 : destroyPacketTouch.hashCode());
		result = 31 * result + Arrays.hashCode(hologramIDs);
		result = 31 * result + (packetsBuilt ? 1231 : 1237);
		result = 31 * result + (spawnPacketArmorStand == null ? 0 : spawnPacketArmorStand.hashCode());
		result = 31 * result + (spawnPacketHorse_1_7 == null ? 0 : spawnPacketHorse_1_7.hashCode());
		result = 31 * result + (spawnPacketHorse_1_8 == null ? 0 : spawnPacketHorse_1_8.hashCode());
		result = 31 * result + (spawnPacketTouchSlime == null ? 0 : spawnPacketTouchSlime.hashCode());
		result = 31 * result + (spawnPacketTouchVehicle == null ? 0 : spawnPacketTouchVehicle.hashCode());
		result = 31 * result + (spawnPacketWitherSkull == null ? 0 : spawnPacketWitherSkull.hashCode());
		result = 31 * result + (teleportPacketArmorStand == null ? 0 : teleportPacketArmorStand.hashCode());
		result = 31 * result + (teleportPacketHorse_1_7 == null ? 0 : teleportPacketHorse_1_7.hashCode());
		result = 31 * result + (teleportPacketHorse_1_8 == null ? 0 : teleportPacketHorse_1_8.hashCode());
		result = 31 * result + (teleportPacketSkull == null ? 0 : teleportPacketSkull.hashCode());
		result = 31 * result + (teleportPacketTouchSlime == null ? 0 : teleportPacketTouchSlime.hashCode());
		result = 31 * result + (teleportPacketTouchVehicle == null ? 0 : teleportPacketTouchVehicle.hashCode());
		result = 31 * result + Arrays.hashCode(touchIDs);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CraftHologram other = (CraftHologram) obj;
		if (attachPacket == null) {
			if (other.attachPacket != null) {
				return false;
			}
		} else if (!attachPacket.equals(other.attachPacket)) {
			return false;
		}
		if (attachPacketTouch == null) {
			if (other.attachPacketTouch != null) {
				return false;
			}
		} else if (!attachPacketTouch.equals(other.attachPacketTouch)) {
			return false;
		}
		if (dataWatcherArmorStand == null) {
			if (other.dataWatcherArmorStand != null) {
				return false;
			}
		} else if (!dataWatcherArmorStand.equals(other.dataWatcherArmorStand)) {
			return false;
		}
		if (dataWatcherHorse_1_7 == null) {
			if (other.dataWatcherHorse_1_7 != null) {
				return false;
			}
		} else if (!dataWatcherHorse_1_7.equals(other.dataWatcherHorse_1_7)) {
			return false;
		}
		if (dataWatcherHorse_1_8 == null) {
			if (other.dataWatcherHorse_1_8 != null) {
				return false;
			}
		} else if (!dataWatcherHorse_1_8.equals(other.dataWatcherHorse_1_8)) {
			return false;
		}
		if (dataWatcherTouchSlime == null) {
			if (other.dataWatcherTouchSlime != null) {
				return false;
			}
		} else if (!dataWatcherTouchSlime.equals(other.dataWatcherTouchSlime)) {
			return false;
		}
		if (dataWatcherTouchVehicle == null) {
			if (other.dataWatcherTouchVehicle != null) {
				return false;
			}
		} else if (!dataWatcherTouchVehicle.equals(other.dataWatcherTouchVehicle)) {
			return false;
		}
		if (dataWatcherWitherSkull == null) {
			if (other.dataWatcherWitherSkull != null) {
				return false;
			}
		} else if (!dataWatcherWitherSkull.equals(other.dataWatcherWitherSkull)) {
			return false;
		}
		if (destroyPacket == null) {
			if (other.destroyPacket != null) {
				return false;
			}
		} else if (!destroyPacket.equals(other.destroyPacket)) {
			return false;
		}
		if (destroyPacketTouch == null) {
			if (other.destroyPacketTouch != null) {
				return false;
			}
		} else if (!destroyPacketTouch.equals(other.destroyPacketTouch)) {
			return false;
		}
		if (!Arrays.equals(hologramIDs, other.hologramIDs)) {
			return false;
		}
		if (packetsBuilt != other.packetsBuilt) {
			return false;
		}
		if (spawnPacketArmorStand == null) {
			if (other.spawnPacketArmorStand != null) {
				return false;
			}
		} else if (!spawnPacketArmorStand.equals(other.spawnPacketArmorStand)) {
			return false;
		}
		if (spawnPacketHorse_1_7 == null) {
			if (other.spawnPacketHorse_1_7 != null) {
				return false;
			}
		} else if (!spawnPacketHorse_1_7.equals(other.spawnPacketHorse_1_7)) {
			return false;
		}
		if (spawnPacketHorse_1_8 == null) {
			if (other.spawnPacketHorse_1_8 != null) {
				return false;
			}
		} else if (!spawnPacketHorse_1_8.equals(other.spawnPacketHorse_1_8)) {
			return false;
		}
		if (spawnPacketTouchSlime == null) {
			if (other.spawnPacketTouchSlime != null) {
				return false;
			}
		} else if (!spawnPacketTouchSlime.equals(other.spawnPacketTouchSlime)) {
			return false;
		}
		if (spawnPacketTouchVehicle == null) {
			if (other.spawnPacketTouchVehicle != null) {
				return false;
			}
		} else if (!spawnPacketTouchVehicle.equals(other.spawnPacketTouchVehicle)) {
			return false;
		}
		if (spawnPacketWitherSkull == null) {
			if (other.spawnPacketWitherSkull != null) {
				return false;
			}
		} else if (!spawnPacketWitherSkull.equals(other.spawnPacketWitherSkull)) {
			return false;
		}
		if (teleportPacketArmorStand == null) {
			if (other.teleportPacketArmorStand != null) {
				return false;
			}
		} else if (!teleportPacketArmorStand.equals(other.teleportPacketArmorStand)) {
			return false;
		}
		if (teleportPacketHorse_1_7 == null) {
			if (other.teleportPacketHorse_1_7 != null) {
				return false;
			}
		} else if (!teleportPacketHorse_1_7.equals(other.teleportPacketHorse_1_7)) {
			return false;
		}
		if (teleportPacketHorse_1_8 == null) {
			if (other.teleportPacketHorse_1_8 != null) {
				return false;
			}
		} else if (!teleportPacketHorse_1_8.equals(other.teleportPacketHorse_1_8)) {
			return false;
		}
		if (teleportPacketSkull == null) {
			if (other.teleportPacketSkull != null) {
				return false;
			}
		} else if (!teleportPacketSkull.equals(other.teleportPacketSkull)) {
			return false;
		}
		if (teleportPacketTouchSlime == null) {
			if (other.teleportPacketTouchSlime != null) {
				return false;
			}
		} else if (!teleportPacketTouchSlime.equals(other.teleportPacketTouchSlime)) {
			return false;
		}
		if (teleportPacketTouchVehicle == null) {
			if (other.teleportPacketTouchVehicle != null) {
				return false;
			}
		} else if (!teleportPacketTouchVehicle.equals(other.teleportPacketTouchVehicle)) {
			return false;
		}
		if (!Arrays.equals(touchIDs, other.touchIDs)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "{\"hologramIDs\":\"" + Arrays.toString(hologramIDs) + "\",\"touchIDs\":\"" + Arrays.toString(touchIDs)
				+ "\",\"packetsBuilt\":\"" + packetsBuilt + "\",\"spawnPacketArmorStand\":\"" + spawnPacketArmorStand
				+ "\",\"spawnPacketWitherSkull\":\"" + spawnPacketWitherSkull + "\",\"spawnPacketHorse_1_7\":\""
				+ spawnPacketHorse_1_7 + "\",\"spawnPacketHorse_1_8\":\"" + spawnPacketHorse_1_8
				+ "\",\"attachPacket\":\"" + attachPacket + "\",\"teleportPacketArmorStand\":\""
				+ teleportPacketArmorStand + "\",\"teleportPacketSkull\":\"" + teleportPacketSkull
				+ "\",\"teleportPacketHorse_1_7\":\"" + teleportPacketHorse_1_7 + "\",\"teleportPacketHorse_1_8\":\""
				+ teleportPacketHorse_1_8 + "\",\"destroyPacket\":\"" + destroyPacket
				+ "\",\"spawnPacketTouchSlime\":\"" + spawnPacketTouchSlime + "\",\"spawnPacketTouchWitherSkull\":\""
				+ spawnPacketTouchVehicle + "\",\"attachPacketTouch\":\"" + attachPacketTouch
				+ "\",\"destroyPacketTouch\":\"" + destroyPacketTouch + "\",\"teleportPacketTouchSlime\":\""
				+ teleportPacketTouchSlime + "\",\"teleportPacketTouchWitherSkull\":\"" + teleportPacketTouchVehicle
				+ "\",\"dataWatcherArmorStand\":\"" + dataWatcherArmorStand + "\",\"dataWatcherWitherSkull\":\""
				+ dataWatcherWitherSkull + "\",\"dataWatcherHorse_1_7\":\"" + dataWatcherHorse_1_7
				+ "\",\"dataWatcherHorse_1_8\":\"" + dataWatcherHorse_1_8 + "\",\"dataWatcherTouchSlime\":\""
				+ dataWatcherTouchSlime + "\",\"dataWatcherTouchWitherSkull\":\"" + dataWatcherTouchVehicle + "\"}";
	}
}
