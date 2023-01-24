package br.com.weavenmc.commons.core;

public interface AsynchronousExecutor {
	public abstract void runAsync(Runnable r);
}
