package br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors;

import java.util.Random;

import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;

public class Surprise {

	private Manager manager;
	private String name;
	private Reward reward;
	private ItemStack rewardIcon;
	private Random random;

	public Surprise(Manager manager,Reward reward, ItemStack rewardIcon) {
		this.manager = manager;
		this.reward = reward;
		this.rewardIcon = rewardIcon;
		this.random = new Random();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Reward getReward() {
		return reward;
	}

	public void setReward(Reward reward) {
		this.reward = reward;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public ItemStack getRewardIcon() {
		return rewardIcon;
	}

	public void setRewardIcon(ItemStack rewardIcon) {
		this.rewardIcon = rewardIcon;
	}

	public Random getRandom() {
		return random;
	}

}
