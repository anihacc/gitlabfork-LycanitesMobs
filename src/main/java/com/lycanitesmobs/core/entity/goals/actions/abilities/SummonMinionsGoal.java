package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class SummonMinionsGoal extends Goal {
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
		this.setMutexFlags(EnumSet.noneOf(Flag.class));
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
		return this.host.isAlive() && this.minionInfo != null;
    }

	@Override
    public void startExecuting() {
		this.summonTime = 1;
	}

	@Override
    public void tick() {
		if(this.summonTime++ % this.summonRate != 0) {
			return;
		}

		// Anti Flight Mode:
		if(this.antiFlight) {
			for (PlayerEntity target : this.host.playerTargets) {
				if(target.abilities.disableDamage || target.isSpectator())
					continue;
				if (CreatureManager.getInstance().config.bossAntiFlight > 0 && target.posY > this.host.posY + CreatureManager.getInstance().config.bossAntiFlight + 1) {
					LivingEntity minion = this.minionInfo.createEntity(this.host.getEntityWorld());
					this.host.summonMinion(minion, this.host.getRNG().nextDouble() * 360, this.host.getSize(this.host.getPose()).width + 1);
					if(minion instanceof BaseCreatureEntity) {
						((BaseCreatureEntity)minion).setAttackTarget(target);
					}
				}
			}
		}

		// TODO Standard mode.
    }
}
