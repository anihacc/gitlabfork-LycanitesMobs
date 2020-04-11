package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.BaseGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class SummonMinionsGoal extends BaseGoal {
    // Properties:
	protected int summonTime = 0;
	protected int summonRate = 60;
	protected int summonCap = 5;
	protected CreatureInfo minionInfo;
	protected boolean perPlayer = false;
	protected boolean antiFlight = false;

	/**
	 * Constructor
	 * @param setHost The creature using this goal.
	 */
	public SummonMinionsGoal(BaseCreatureEntity setHost) {
		super(setHost);
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
	 * Sets the minion count cap for summoning.
	 * @param summonCap The summoning cap.
	 * @return This goal for chaining.
	 */
	public SummonMinionsGoal setSummonCap(int summonCap) {
		this.summonCap = summonCap;
		return this;
	}

	/**
	 * If true, the cap is scaled per players detected.
	 * @param perPlayer True to enable.
	 * @return This goal for chaining.
	 */
	public SummonMinionsGoal setPerPlayer(boolean perPlayer) {
		this.perPlayer = perPlayer;
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
		return super.shouldExecute() && this.minionInfo != null;
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

		if(this.host.getMinions(this.minionInfo.getEntityClass()).size() >= this.summonCap) {
			return;
		}

		// Anti Flight Mode:
		if(this.antiFlight) {
			for (EntityPlayer target : this.host.playerTargets) {
				if(target.isCreative() || target.isSpectator())
					continue;
				if (CreatureManager.getInstance().config.bossAntiFlight > 0 && target.posY > this.host.posY + CreatureManager.getInstance().config.bossAntiFlight + 1) {
					this.summonMinion(target);
				}
			}
			return;
		}

		this.summonMinion(this.host.getAttackTarget());
    }

    protected void summonMinion(EntityLivingBase target) {
		EntityLivingBase minion = this.minionInfo.createEntity(this.host.getEntityWorld());
		this.host.summonMinion(minion, this.host.getRNG().nextDouble() * 360, this.host.width + 1);
		if(minion instanceof BaseCreatureEntity) {
			((BaseCreatureEntity)minion).setAttackTarget(target);
		}
	}
}
