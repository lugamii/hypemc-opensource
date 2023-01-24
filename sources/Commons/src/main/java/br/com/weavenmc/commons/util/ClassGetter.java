package br.com.weavenmc.commons.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassGetter {

	private static Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
		} catch (NoClassDefFoundError e) {
			return null;
		}
	}
	
	public static List<Class<?>> getClassesForPackageByPlugin(Object plugin, String pkgname) {
		try {
			Method method = plugin.getClass().getMethod("getFile");
			method.setAccessible(true);
			File file = (File) method.invoke(plugin);
			return getClassesForPackageByFile(file, pkgname);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static List<Class<?>> getClassesForPackageByFile(File file, String pkgname) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		try {
			String relPath = pkgname.replace('.', '/');		
			try (JarFile jarFile = new JarFile(file)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if (entryName.endsWith(".class") 
							&& entryName.startsWith(relPath)
							&& entryName.length() > (relPath.length() + "/".length())) {
						String className = entryName.replace('/', '.').replace('\\', '.');
						if (className.endsWith(".class"))
							className = className.substring(0, className.length()-6);
						Class<?> c = loadClass(className);
						if (c != null)
							classes.add(c);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException reading JAR File '" + file.getAbsolutePath() + "'", e);
		}

		return classes;
	}
}