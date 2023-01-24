package br.com.weavenmc.skywars.automatic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.weavenmc.skywars.WeavenSkywars;
import br.com.weavenmc.skywars.commands.BaseCommand;

public class ClassGetter {
	
	public static ArrayList<Class<?>> getClassesForPackage(Class<?> clas, String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		CodeSource src = clas.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		return classes;
	}
	
	public static void loadCommandBukkit() {
		for (Class<?> classes : getClassesForPackage(WeavenSkywars.getPlugin(WeavenSkywars.class), "br.com.weavenmc.skywars.commands")) {
			try {
				if (BaseCommand.class.isAssignableFrom(classes) && classes != BaseCommand.class) {
					BaseCommand utilsCommand = (BaseCommand) classes.newInstance();
					((CraftServer) Bukkit.getServer()).getCommandMap().register(utilsCommand.getName(), utilsCommand);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static void loadListenerBukkit() {
		for (Class<?> classes : getClassesForPackage(WeavenSkywars.getPlugin(WeavenSkywars.class), "br.com.weavenmc.skywars")) {
			try {
				if (Listener.class.isAssignableFrom(classes)) {
					Listener listener = (Listener) classes.newInstance();
					Bukkit.getPluginManager().registerEvents(listener, WeavenSkywars.getPlugin(WeavenSkywars.class));
				}
			} catch (Exception exception) {
			}
		}
	}
	
	public static List<Class<?>> getClassesForPackageByFile(File file, String pkgname) {
		List<Class<?>> classes = new ArrayList<>();
		try {
			String relPath = pkgname.replace('.', '/');
			JarFile jarFile = new JarFile(file);
			Throwable localThrowable3 = null;
			try {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = (JarEntry) entries.nextElement();
					String entryName = entry.getName();
					if ((entryName.endsWith(".class")) && (entryName.startsWith(relPath)) && (entryName.length() > relPath.length() + "/".length())) {
						String className = entryName.replace('/', '.').replace('\\', '.');
						if (className.endsWith(".class")) {
							className = className.substring(0, className.length() - 6);
						}
						Class<?> c = loadClass(className);
						if (c != null) {
							classes.add(c);
						}
					}
				}
			} catch (Throwable localThrowable1) {
				localThrowable3 = localThrowable1;
				throw localThrowable1;
			} finally {
				if (jarFile != null) {
					if (localThrowable3 != null) {
						try {
							jarFile.close();
						} catch (Throwable localThrowable2) {
							localThrowable3.addSuppressed(localThrowable2);
						}
					} else {
						jarFile.close();
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + file.getAbsolutePath() + "'", e);
		}
		return classes;
	}

	public static ArrayList<Class<?>> getClassesForPackage(final JavaPlugin plugin, final String pkgname) {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		final CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			final URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		return classes;
	}
	
	public static ArrayList<Class<?>> getClassesForPackage(Plugin plugin, final String pkgname) {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		final CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			final URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		return classes;
	}

	private static Class<?> loadClass(final String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		} catch (NoClassDefFoundError e2) {
			return null;
		}
	}

	private static void processJarfile(final URL resource, final String pkgname, final ArrayList<Class<?>> classes) {
		final String relPath = pkgname.replace('.', '/');
		final String resPath = resource.getPath().replace("%20", " ");
		final String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		JarFile jarFile;
		try {
			jarFile = new JarFile(jarPath);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
		}
		final Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			final JarEntry entry = entries.nextElement();
			final String entryName = entry.getName();
			String className = null;
			if (entryName.endsWith(".class") && entryName.startsWith(relPath)
					&& entryName.length() > relPath.length() + "/".length()) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}
			if (className != null) {
				final Class<?> c = loadClass(className);
				if (c == null) {
					continue;
				}
				classes.add(c);
			}
		}
		try {
			jarFile.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

}
