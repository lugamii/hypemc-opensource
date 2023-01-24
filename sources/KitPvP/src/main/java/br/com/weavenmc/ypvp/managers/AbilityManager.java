package br.com.weavenmc.ypvp.managers;

import java.util.ArrayList;
import java.util.List;

import br.com.weavenmc.commons.util.ClassGetter;
import br.com.weavenmc.ypvp.Management;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.ability.Ability;
import br.com.weavenmc.ypvp.ability.NoneAbility;
import lombok.Getter;

@Getter
public class AbilityManager extends Management {

	private List<Ability> abilities = new ArrayList<>();
	private final Ability none = new NoneAbility();

	public AbilityManager(yPvP plugin) {
		super(plugin);
	}

	@Override
	public void enable() {
		for (Class<?> clazz : ClassGetter.getClassesForPackageByFile(getPlugin().getFile(),
				"br.com.weavenmc.ypvp.ability.list")) {
			if (Ability.class.isAssignableFrom(clazz)) {
				try {
					Ability ability = (Ability) clazz.newInstance();
					registerListener(ability);
					abilities.add(ability);
					getPlugin().getLogger().info("Habilidade Carregada: " + ability.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Ability getAbility(String name) {
		for (Ability ability : abilities) {
			if (!ability.getName().equalsIgnoreCase(name))
				continue;
			return ability;
		}
		return null;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}
}
