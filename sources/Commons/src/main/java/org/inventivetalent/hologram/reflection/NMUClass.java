package org.inventivetalent.hologram.reflection;

import java.lang.reflect.Field;

public abstract class NMUClass {

	private static boolean initialized = false;

	public static Class<?> gnu_trove_map_TIntObjectMap;
	public static Class<?> gnu_trove_map_hash_TIntObjectHashMap;
	public static Class<?> gnu_trove_impl_hash_THash;
	public static Class<?> io_netty_channel_Channel;

	static {
		if (!initialized) {
			for (Field f : NMUClass.class.getDeclaredFields()) {
				if (f.getType().equals(Class.class)) {
					try {
						String name = f.getName().replace("_", ".");
						f.set(null, Class.forName(name));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			initialized = true;
		}
	}

}
