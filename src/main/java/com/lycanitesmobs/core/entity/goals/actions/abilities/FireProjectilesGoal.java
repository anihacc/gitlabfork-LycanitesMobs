package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class FireProjectilesGoal extends EntityAIBase {
	BaseCreatureEntity host;

    // Properties:
	private String projectileName;
	private Class<? extends EntityProjectileBase> projectileClass;
	float velocity = 1.6F;
	float inaccuracy = 0F;
	float scale = 1F;
	float angle = 0F;
	Vec3d offset = Vec3d.ZERO;
	private int fireRate = 60;
	private boolean allPlayers = false;
	protected int phase = -1;

	private int fireTime = 60;
	private Entity attackTarget;


	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public FireProjectilesGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	/**
	 * Sets the battle phase to restrict this goal to.
	 * @param phase The phase to restrict to, if below 0 phases are ignored.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setPhase(int phase) {
		this.phase = phase;
		return this;
	}

	/**
	 * Sets the projectile via info to fire.
	 * @param projectileName The projectile via name to fire.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setProjectile(String projectileName) {
		this.projectileName = projectileName;
		return this;
	}

	/**
	 * Sets the projectile class to fire.
	 * @param projectileClass The projectile class to fire.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setProjectile(Class<? extends EntityProjectileBase> projectileClass) {
		this.projectileClass = projectileClass;
		return this;
	}

	/**
	 * Sets the rate of firing (in ticks).
	 * @param fireRate The firing tick rate.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setFireRate(int fireRate) {
		this.fireRate = fireRate;
		return this;
	}

	/**
	 * Sets the velocity of firing.
	 * @param velocity The firing velocity.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setVelocity(float velocity) {
		this.velocity = velocity;
		return this;
	}

	/**
	 * Sets the scale of the projectile.
	 * @param scale The projectile scale.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setScale(float scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * Sets the inaccuracy of firing.
	 * @param inaccuracy The firing inaccuracy.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setInaccuracy(int inaccuracy) {
		this.inaccuracy = inaccuracy;
		return this;
	}

	/**
	 * Sets the angle offset of firing.
	 * @param angle The firing angle offset.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setAngle(int angle) {
		this.angle = angle;
		return this;
	}

	/**
	 * Sets the xyz offset to fire from.
	 * @param offset The firing angle offset.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setOffset(Vec3d offset) {
		this.offset = offset;
		return this;
	}

	/**
	 * Sets anti flight summoning where minions are summoned at any player targets that are flying.
	 * @param allPlayers True to target all players (requires FindNearbyPlayers goal) otherwise the current attack target is used.
	 * @return This goal for chaining.
	 */
	public FireProjectilesGoal setAllPlayers(boolean allPlayers) {
		this.allPlayers = allPlayers;
		return this;
	}

	@Override
    public boolean shouldExecute() {
		if(!this.host.isEntityAlive() || (this.projectileName == null && this.projectileClass == null)) {
			return false;
		}

		this.attackTarget = this.host.getAttackTarget();
		if(!this.allPlayers && this.attackTarget == null) {
			return false;
		}

		return this.phase < 0 || this.phase == this.host.getBattlePhase();
    }

	@Override
	public void startExecuting() {
		this.fireTime = 1;
	}

	@Override
    public void resetTask() {
		this.attackTarget = null;
	}

	@Override
    public void updateTask() {
		if(this.fireTime++ % this.fireRate != 0) {
			return;
		}

		// All Players Mode:
		if(this.allPlayers) {
			for (EntityPlayer target : this.host.playerTargets) {
				if(target.capabilities.disableDamage || target.isSpectator())
					continue;
				this.fireProjectile(target);
			}
			return;
		}

		// Single Target Mode:
		this.fireProjectile(this.attackTarget);
    }

	/**
	 * Calls the hosts fire projectile from this goal.
	 * @param target The target to fire at.
	 */
	public void fireProjectile(Entity target) {
		if(this.projectileName != null) {
			this.host.fireProjectile(this.projectileName, target, this.host.getDistance(target), this.angle, this.offset, this.velocity, this.scale, this.inaccuracy);
		}
		if(this.projectileClass != null) {
			this.host.fireProjectile(this.projectileClass, target, this.host.getDistance(target), this.angle, this.offset, this.velocity, this.scale, this.inaccuracy);
		}
	}
}
