package br.com.weavenmc.commons.core.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.weavenmc.commons.core.permission.Group;

public interface CommandFramework {

	Object getPlugin();

    void registerCommands(CommandClass commandClass);

    @Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command {

		String name();

		Group groupToUse() default Group.MEMBRO;

		String permission() default "";

		String[] aliases() default {};

		String description() default "";

		String usage() default "";

		boolean runAsync() default false;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Completer {

		String name();

		String[] aliases() default {};
	}
}
