package br.com.mcweaven.gladiator.fight.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.mcweaven.gladiator.fight.Fight;

public class FightManager {

	List<Fight> fights;

	public FightManager() {
		fights = new ArrayList<>();
	}

	public boolean fightExists(Fight fight) {
		return fights.contains(fight);
	}

	public Collection<Fight> getFights() {
		return fights;
	}

	public void newFight(Fight fight) {
		fights.add(fight);
	}

	public void endFight(Fight fight) {
		fights.remove(fight);
	}

}
