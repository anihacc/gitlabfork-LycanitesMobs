package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.MathHelper;

public class ForceGoal extends EntityAIBase {
	BaseCreatureEntity host;

    // Properties:
	protected int duration = 10 * 20;
	protected int cooldownDuration = 15 * 20;
	protected int windUp = 3 * 20;
	protected float range = 15 * 20;
	protected float force = 1F;
	protected int phase = -1;

	public int abilityTime = 0;
	public int cooldownTime = this.cooldownDuration;


	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public ForceGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets the battle phase to restrict this goal to.
	 * @param phase The phase to restrict to, if below 0 phases are ignored.
	 * @return This goal for chaining.
	 */
	public ForceGoal setPhase(int phase) {
		this.phase = phase;
		return this;
	}

	/**
	 * Sets the duration of firing (in ticks).
	 * @param duration The firing duration.
	 * @return This goal for chaining.
	 */
	public ForceGoal setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	/**
	 * Sets the cooldown after firing (in ticks).
	 * @param cooldown The cooldown.
	 * @return This goal for chaining.
	 */
	public ForceGoal setCooldown(int cooldown) {
		this.cooldownDuration = cooldown;
		this.cooldownTime = cooldown;
		return this;
	}

	/**
	 * Sets how long it takes for the pull/push to wind up (in ticks).
	 * @param windUp The wind up duration.
	 * @return This goal for chaining.
	 */
	public ForceGoal setWindUp(int windUp) {
		this.windUp = windUp;
		return this;
	}

	/**
	 * Sets the range of force.
	 * @param range The range.
	 * @return This goal for chaining.
	 */
	public ForceGoal setRange(float range) {
		this.range = range;
		return this;
	}

	/**
	 * Sets the pull/push force.
	 * @param range The force, positive pushes, negative pulls.
	 * @return This goal for chaining.
	 */
	public ForceGoal setForce(float force) {
		this.force = force;
		return this;
	}

	@Override
    public boolean shouldExecute() {
		if(!this.host.isEntityAlive()) {
			return false;
		}

		if(this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
			return false;
		}

		return true;
    }

	@Override
	public void startExecuting() {
		this.cooldownTime = this.cooldownDuration;
	}

	@Override
    public void updateTask() {
		if(this.cooldownTime-- > 0) {
			this.abilityTime = 0;
			return;
		}

		if(this.abilityTime == this.windUp) {
			this.host.playAttackSound();
		}

		if(this.abilityTime++ >= this.duration && this.cooldownDuration > 0) {
			this.cooldownTime = this.cooldownDuration;
			return;
		}

		double motionCap = -this.force;
		double factor = -this.force * 0.1D;
		if(this.abilityTime < this.windUp) {
			factor *= (double)this.abilityTime / this.windUp;
		}
		for(Entity entity : this.host.getNearbyEntities(Entity.class, this::isValidTarget, this.range)) {
			if(!(entity instanceof EntityLivingBase)) {
				continue;
			}
			double xDist = this.host.posX - entity.posX;
			double zDist = this.host.posZ - entity.posZ;
			double xzDist = Math.max(MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
			EntityPlayerMP player = null;
			if (entity instanceof EntityPlayerMP) {
				player = (EntityPlayerMP) entity;
			}
			if (entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
				entity.addVelocity(
						xDist / xzDist * factor + entity.motionX * factor,
						0,
						zDist / xzDist * factor + entity.motionZ * factor
				);
			}
			if (player != null) {
				player.connection.sendPacket(new SPacketEntityVelocity(entity));
			}
		}
    }

    public boolean isValidTarget(Entity entity) {
		if(entity == this.host) {
			return false;
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.capabilities.isCreativeMode) {
				return false;
			}
		}
		return true;
	}
}
