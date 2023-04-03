package br.com.adlerlopes.bypiramid.hungergames.manager.managers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerCommand;
import br.com.adlerlopes.bypiramid.hungergames.game.custom.HungerListener;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.manager.constructor.Management;
import br.com.adlerlopes.bypiramid.hungergames.player.inventories.CustomInventory;
import br.com.adlerlopes.bypiramid.hungergames.player.kit.Kit;
import br.com.adlerlopes.bypiramid.hungergames.utilitaries.loader.Getter;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClassManager extends Management {

	public ClassManager(Manager manager) {
		super(manager, "ClassManager");
	}

	public boolean initialize() {
		return load();
	}

	public boolean load() {
		getLogger().log("Starting trying to load all the classes of commands and listeners of the plugin.");

		for (Class<?> classes : Getter.getClassesForPackage(getManager().getPlugin(), "br.com.adlerlopes.bypiramid.hungergames")) {
			try {
				Listener listener = null;
				if (!Listener.class.isAssignableFrom(classes)) {
					continue;
				} else if (Kit.class.isAssignableFrom(classes)) {
					continue;
				} else if (classes.getSimpleName().equals("Timer")) {
					continue;
				} else if (classes.getSimpleName().equals("GladiatorFight")) {
					continue;
				} else if (classes.getSimpleName().startsWith("GladiatorFight")) {
					continue;
				} else if (classes.getSimpleName().equals("")) {
					continue;
				} else if (classes.getSimpleName().equals("CustomInventory")) {
					continue;
				} else if (HungerCommand.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else if (HungerListener.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor().newInstance();
				} else if (CustomInventory.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.getConstructor(Manager.class).newInstance(getManager());
				} else {
					listener = (Listener) classes.getConstructor(Manager.class).newInstance(getManager());
				}

				Bukkit.getPluginManager().registerEvents(listener, getManager().getPlugin());
				getLogger().debug("The listener " + listener.getClass().getSimpleName() + " was loaded correcly!");

			} catch (Exception exception) {
				getLogger().error("Error to load the listener " + classes.getSimpleName() + ", stopping the process!", exception);
				return false;
			}
		}
		return true;
	}
}
