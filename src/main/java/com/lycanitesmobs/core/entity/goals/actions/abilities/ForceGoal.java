package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.MathHelper;

public class ForceGoal extends Goal {
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
		if(!this.host.isAlive()) {
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
    public void tick() {
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
			factor *= this.abilityTime / this.windUp;
		}
		for(Entity entity : this.host.getNearbyEntities(Entity.class, null, this.range)) {
			if(!(entity instanceof LivingEntity)) {
				continue;
			}
			double xDist = this.host.getPositionVec().getX() - entity.getPositionVec().getX();
			double zDist = this.host.getPositionVec().getZ() - entity.getPositionVec().getZ();
			double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
			ServerPlayerEntity player = null;
			if (entity instanceof ServerPlayerEntity) {
				player = (ServerPlayerEntity) entity;
				if (player.abilities.isCreativeMode) {
					continue;
				}
			}
			if (entity.getMotion().getX() < motionCap && entity.getMotion().getX() > -motionCap && entity.getMotion().getZ() < motionCap && entity.getMotion().getZ() > -motionCap) {
				entity.addVelocity(
						xDist / xzDist * factor + entity.getMotion().getX() * factor,
						0,
						zDist / xzDist * factor + entity.getMotion().getZ() * factor
				);
			}
			if (player != null) {
				player.connection.sendPacket(new SEntityVelocityPacket(entity));
			}
		}
    }
}
