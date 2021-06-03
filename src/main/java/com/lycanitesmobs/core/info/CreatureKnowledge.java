package com.lycanitesmobs.core.info;

public class CreatureKnowledge {
	public Beastiary beastiary;
	public String creatureName;
	public int rank;
	public int experience = 0;

	/**
	 * Constructor
	 * @param beastiary The Beastiary this knowledge is part of.
	 * @param creatureName The name of the creature that this is knowledge of.
	 * @param rank The rank of this knowledge.
	 * @param rank The amount of experience of this knowledge.
	 */
	public CreatureKnowledge(Beastiary beastiary, String creatureName, int rank, int experience) {
		this.beastiary = beastiary;
		this.creatureName = creatureName;
		this.rank = rank;
		this.experience = experience;
	}

	/**
	 * Returns the Creature Info of the Knowledge.
	 * @return The creature info.
	 */
	public CreatureInfo getCreatureInfo() {
		return CreatureManager.getInstance().getCreature(this.creatureName);
	}

	public void addExperience(int experience) {
		int maxExperience = this.getMaxExperience();
		if (maxExperience <= 0) {
			return;
		}
		this.experience += experience;

		// Rank Up:
		if (this.experience >= maxExperience) {
			this.rank++;
			int remainingExperience = this.experience - maxExperience;
			this.experience = 0;
			this.addExperience(remainingExperience);
			this.beastiary.sendAddedMessage(this);
		}
	}

	public int getMaxExperience() {
		if (this.rank == 1) {
			return 1000;
		}
		if (this.rank >= 2) {
			return 0;
		}
		return 1;
	}
}
