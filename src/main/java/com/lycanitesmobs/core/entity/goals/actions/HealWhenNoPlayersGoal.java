package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class HealWhenNoPlayersGoal extends EntityAIBase {
	BaseCreatureEntity host;

	// Targets:
	public List<EntityPlayer> playerTargets = new ArrayList<>();
	public boolean firstPlayerTargetCheck = false;

    // Properties:
    private float healAmount = 50;

	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public HealWhenNoPlayersGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets how much this creature heals by.
	 * @param healAmount The amount to heal by.
	 * @return This goal for chaining.
	 */
	public HealWhenNoPlayersGoal setHealAmount(float healAmount) {
    	this.healAmount = healAmount;
    	return this;
    }

	@Override
    public boolean shouldExecute() {
		return this.host.isEntityAlive();
    }

	@Override
    public boolean shouldContinueExecuting() {
        return this.host.isEntityAlive();
    }

	@Override
    public void startExecuting() {}

	@Override
    public void resetTask() {
		this.firstPlayerTargetCheck = false;
	}

	@Override
    public void updateTask() {
		if(this.host.updateTick % 200 != 0 || !this.firstPlayerTargetCheck) {
			return;
		}
		this.firstPlayerTargetCheck = true;
		this.playerTargets = this.host.getNearbyEntities(EntityPlayer.class, null, 64);
		if (this.host.updateTick % 20 == 0 && this.playerTargets.isEmpty()) {
			this.host.heal(this.healAmount);
		}
    }
}
