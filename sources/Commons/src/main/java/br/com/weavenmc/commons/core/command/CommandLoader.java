package br.com.weavenmc.commons.core.command;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.util.ClassGetter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLoader {

	@NonNull
	private CommandFramework framework;
	
	public int loadCommandsFromPackage(String pkgname) {
		int i = 0;
		for (Class<?> clazz : ClassGetter.getClassesForPackageByPlugin(framework.getPlugin(), pkgname)) {
			if (CommandClass.class.isAssignableFrom(clazz)) {
				try {
					CommandClass command = (CommandClass) clazz.newInstance();
					framework.registerCommands(command);
					command = null;
				} catch (Exception e) {
					WeavenMC.getLogger().warning("Erro ao carregar comando da classe " + clazz.getSimpleName() + "!");
				}
				++i;
			}
		}
		return i;
	}
}
