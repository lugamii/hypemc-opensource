package br.com.weavenmc.commons.bukkit.api.player;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;

public class ProfileAPI {
	
	public static final LoadingCache<WrappedGameProfile, WrappedSignedProperty> Textures = CacheBuilder.newBuilder()
			.expireAfterWrite(30L, TimeUnit.MINUTES)
			.build(new CacheLoader<WrappedGameProfile, WrappedSignedProperty>() {
				@Override
				public WrappedSignedProperty load(WrappedGameProfile key) throws Exception {
					return loadTextures(key);
				}
			});

	private static final WrappedSignedProperty loadTextures(WrappedGameProfile profile) {
		try {
			Object minecraftServer = MinecraftReflection.getMinecraftServerClass().getMethod("getServer").invoke(null);
			((MinecraftSessionService) minecraftServer.getClass().getMethod("aD").invoke(minecraftServer))
					.fillProfileProperties((GameProfile) profile.getHandle(), true);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return Iterables.getFirst(profile.getProperties().get("textures"), null);
	}
}
