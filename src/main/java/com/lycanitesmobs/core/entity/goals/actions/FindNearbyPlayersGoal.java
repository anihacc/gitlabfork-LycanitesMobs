package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class FindNearbyPlayersGoal extends EntityAIBase {
	BaseCreatureEntity host;

    // Properties:
	private double searchRange = 64D;

	private int searchTime = 0;
	private int searchRate = 20;


	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public FindNearbyPlayersGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets the player search range (in blocks).
	 * @param searchRange The range to find blocks.
	 * @return This goal for chaining.
	 */
	public FindNearbyPlayersGoal setSearchRange(double searchRange) {
    	this.searchRange = searchRange;
    	return this;
    }

	@Override
    public boolean shouldExecute() {
		return this.host.isEntityAlive();
    }

	@Override
    public void updateTask() {
		if(this.searchTime++ % this.searchRate != 0) {
			return;
		}

		EntityLivingBase newTarget = null;
		try {
			this.host.playerTargets = this.host.getEntityWorld().getPlayers(EntityPlayer.class, input -> input.getDistance(this.host) <= this.searchRange);
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
			e.printStackTrace();
		}
    }
}
