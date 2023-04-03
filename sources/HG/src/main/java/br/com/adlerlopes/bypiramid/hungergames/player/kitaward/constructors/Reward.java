package br.com.adlerlopes.bypiramid.hungergames.player.kitaward.constructors;

public class Reward {

	public int ammount;
	public String reward;
	public RewardType type;

	public Reward(int ammount, RewardType type) {
		this.ammount = ammount;
		this.type = type;
		this.reward = "";
	}

	public Reward(String reward, int ammount,RewardType type) {
		this.ammount = ammount;
		this.type = type;
		this.reward = reward;
	}

	public int getAmmount() {
		return ammount;
	}

	public String getReward() {
		return reward;
	}

	public RewardType getType() {
		return type;
	}

	public static enum RewardType {
		COINS("Coins"),
		VIP("Vips"),
		KIT("Kit"),
		DOUBLE("Double XP");

		private String name;

		private RewardType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
