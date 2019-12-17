package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class SummonMinionsGoal extends EntityAIBase {
	BaseCreatureEntity host;

    // Properties:
	private int summonTime = 0;
	private int summonRate = 60;
	private CreatureInfo minionInfo;
	private boolean antiFlight = false;


	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public SummonMinionsGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets the rate of summoning (in ticks).
	 * @param summonRate The summoning tick rate.
	 * @return This goal for chaining.
	 */
	public SummonMinionsGoal setSummonRate(int summonRate) {
    	this.summonRate = summonRate;
    	return this;
    }

	/**
	 * Sets anti flight summoning where minions are summoned at any player targets that are flying.
	 * @param antiFlight True to enable.
	 * @return This goal for chaining.
	 */
	public SummonMinionsGoal setAntiFlight(boolean antiFlight) {
    	this.antiFlight = antiFlight;
    	return this;
    }

	/**
	 * Sets the creature to summon.
	 * @param creatureName The creature name to summon.
	 * @return This goal for chaining.
	 */
	public SummonMinionsGoal setMinionInfo(String creatureName) {
    	this.minionInfo = CreatureManager.getInstance().getCreature(creatureName);
    	return this;
    }

	@Override
    public boolean shouldExecute() {
		return this.host.isEntityAlive() && this.minionInfo != null;
    }

	@Override
    public void startExecuting() {
		this.summonTime = 1;
	}

	@Override
    public void updateTask() {
		if(this.summonTime++ % this.summonRate != 0) {
			return;
		}

		// Anti Flight Mode:
		if(this.antiFlight) {
			for (EntityPlayer target : this.host.playerTargets) {
				if(target.capabilities.disableDamage || target.isSpectator())
					continue;
				if (CreatureManager.getInstance().config.bossAntiFlight > 0 && target.posY > this.host.posY + CreatureManager.getInstance().config.bossAntiFlight + 1) {
					EntityLivingBase minion = this.minionInfo.createEntity(this.host.getEntityWorld());
					this.host.summonMinion(minion, this.host.getRNG().nextDouble() * 360, this.host.width + 1);
					if(minion instanceof BaseCreatureEntity) {
						((BaseCreatureEntity)minion).setAttackTarget(target);
					}
				}
			}
		}

		// TODO Standard mode.
    }
}
