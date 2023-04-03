package br.com.adlerlopes.bypiramid.hungergames.logger;

import java.util.logging.Level;

/**
* Copyright (C) LittleMC, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public final class Logger {

	private static final String LOG_PREFIX_FORMAT = "[%s]";

	private final java.util.logging.Logger handle;
	private final Logger parent;
	private final boolean debugMode;
	private String prefix;
	private String logPrefix;

	public Logger(java.util.logging.Logger handle, String prefix, boolean debugMode) {
		this.handle = handle;
		this.parent = null;
		this.prefix = prefix;
		this.debugMode = debugMode;

		this.logPrefix = buildPrefix(new StringBuilder());
	}

	public Logger(Logger parent, String prefix) {
		this.handle = parent.handle;
		this.parent = parent;
		this.prefix = prefix;
		this.debugMode = parent.hasDebugMode();

		this.logPrefix = buildPrefix(new StringBuilder());
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		this.logPrefix = buildPrefix(new StringBuilder());
	}

	private String buildPrefix(StringBuilder builder) {
		if (prefix != null) {
			builder.insert(0, String.format(LOG_PREFIX_FORMAT, prefix));
		}
		return parent == null ? builder.length() < 1 ? "" : builder + " " : parent.buildPrefix(builder);
	}

	public void log(String format, Object... args) {
		log(Level.INFO, null, String.format(format, args));
	}

	public void log(String message) {
		log(Level.INFO, null, message);
	}

	public void log(long time, String message) {
		log(Level.INFO, time, message);
	}

	public void log(Level level, String format, Object... args) {
		log(level, null, String.format(format, args));
	}

	public void log(Level level, String message) {
		log(level, null, message);
	}

	public void log(Level level, long time, String message) {
		log(level, null, "[" + (System.currentTimeMillis() - time) + "ms] " + message);
	}

	public void log(Level level, Throwable ex, String format, Object... args) {
		log(level, ex, String.format(format, args));
	}

	public void log(Level level, Throwable ex, String message) {
		handle.log(level, logPrefix + message, ex);
	}

	public void debug(String message) {
		if (hasDebugMode())
			log(Level.WARNING, null, message);
	}

	public void error(String message) {
		log(Level.SEVERE, null, message);
	}

	public void error(String message, Throwable throwable) {
		log(Level.SEVERE, throwable, message);
	}

	public boolean hasDebugMode() {
		return debugMode;
	}
}