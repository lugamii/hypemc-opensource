package br.com.adlerlopes.bypiramid.hungergames.utilitaries.loader;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class Getter {

	public static ArrayList<Class<?>> getClassesForPackage(JavaPlugin plugin, String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		return classes;
	}

	public static ArrayList<Class<?>> getClassesForPackage(Plugin plugin, String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		return classes;
	}

	public static ArrayList<Class<?>> getClassesForPackageSimple(JavaPlugin plugin, String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
		if (src != null) {
			URL resource = src.getLocation();
			resource.getPath();
			processJarfile(resource, pkgname, classes);
		}
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Class<?>> classi = new ArrayList<Class<?>>();
		for (Class<?> classy : classes) {
			names.add(classy.getSimpleName());
			classi.add(classy);
		}
		classes.clear();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (String s : names)
			for (Class<?> classy : classi) {
				if (classy.getSimpleName().equals(s)) {
					classes.add(classy);
					break;
				}
			}
		return classes;
	}

	private static Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class " + className + "");
		}
	}

	@SuppressWarnings("resource")
	private static void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes) {
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath().replace("%20", " ");
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		JarFile jarFile;
		try {
			jarFile = new JarFile(jarPath);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File " + jarPath + "", e);
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String className = null;
			if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}
			if (className != null) {
				classes.add(loadClass(className));
			}
		}
	}

}
