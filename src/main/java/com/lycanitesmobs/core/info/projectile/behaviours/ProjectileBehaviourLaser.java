package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.HashSet;

public class ProjectileBehaviourLaser extends ProjectileBehaviour {
	/** The aiming speed of the laser. **/
	public double speed = 1;
	
	/** The width of the laser. **/
	public float width = 1;

	/** The total range of the laser. **/
	public double range = 16;

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("speed"))
			this.speed = json.get("speed").getAsDouble();
		if(json.has("width"))
			this.width = json.get("width").getAsFloat();
		if(json.has("range"))
			this.range = json.get("range").getAsDouble();
	}

	@Override
	public void onProjectileUpdate(BaseProjectileEntity projectile) {
		if(!(projectile instanceof CustomProjectileEntity)) {
			return;
		}
		((CustomProjectileEntity)projectile).syncThrower();
		((CustomProjectileEntity)projectile).laserWidth = this.width;

		// Follow Thrower/Parent Projectile:
		if(projectile.getThrower() != null) {
			Entity entityToFollow = projectile.getThrower();
			if(((CustomProjectileEntity)projectile).getParent() != null) {
				entityToFollow = ((CustomProjectileEntity)projectile).getParent();
			}
			double xPos = entityToFollow.getPositionVec().getX();
			double yPos = entityToFollow.getPositionVec().getY();
			double zPos = entityToFollow.getPositionVec().getZ();
			if(entityToFollow instanceof BaseCreatureEntity) {
				BaseCreatureEntity creatureToFollow = (BaseCreatureEntity)entityToFollow;
				xPos = creatureToFollow.getFacingPosition(creatureToFollow, 0, creatureToFollow.rotationYaw + 90F).getX();
				yPos += entityToFollow.getSize(entityToFollow.getPose()).height * 0.75D;
				zPos = creatureToFollow.getFacingPosition(creatureToFollow, 0, creatureToFollow.rotationYaw).getZ();
			}
			projectile.setPosition(xPos, yPos, zPos);
			projectile.setVelocity(entityToFollow.getMotion().getX(), entityToFollow.getMotion().getY(), entityToFollow.getMotion().getZ());
		}

		// Update Laser End:
		this.updateEnd(projectile);
	}

	/**
	 * Updates the end of the laser.
	 * @param projectile The projectile firing its laser!
	 * @return The laser end used for stopping the laser at.
	 */
	public LaserEndProjectileEntity updateEnd(BaseProjectileEntity projectile) {
		// Get or Create Laser End:
		LaserEndProjectileEntity laserEnd = ((CustomProjectileEntity)projectile).getLaserEnd();
		if(laserEnd == null && !projectile.getEntityWorld().isRemote) {
			laserEnd = new LaserEndProjectileEntity(ProjectileManager.getInstance().oldProjectileTypes.get(LaserEndProjectileEntity.class), projectile.getEntityWorld());
			projectile.getEntityWorld().addEntity(laserEnd);
			((CustomProjectileEntity)projectile).setLaserEnd(laserEnd);
		}
		if(laserEnd == null) {
			return null;
		}

		// Laser Aiming:
		double targetX = projectile.getPositionVec().getX();
		double targetY = projectile.getPositionVec().getY();
		double targetZ = projectile.getPositionVec().getZ();

		// Entity Laser Aiming:
		boolean lockedLaser = false;
		if(((CustomProjectileEntity)projectile).getParent() != null) {
			double[] target = projectile.getFacingPosition(projectile, this.range, ((CustomProjectileEntity)projectile).laserAngle);
			targetX = target[0] + ((MathHelper.cos((projectile.updateTick + ((CustomProjectileEntity)projectile).laserAngle) * 0.25F) * 1.0F) - 0.5F);
			targetY = target[1] + (((MathHelper.cos((projectile.updateTick + ((CustomProjectileEntity)projectile).laserAngle) * 0.25F) * 1.0F) - 0.5F) * 10);
			targetZ = target[2] + ((MathHelper.cos((projectile.updateTick + ((CustomProjectileEntity)projectile).laserAngle) * 0.25F) * 1.0F) - 0.5F);
		}
		else if(projectile.getThrower() != null) {
			if(projectile.getThrower() instanceof BaseCreatureEntity && ((BaseCreatureEntity)projectile.getThrower()).getAttackTarget() != null) {
				LivingEntity attackTarget = ((BaseCreatureEntity)projectile.getThrower()).getAttackTarget();
				targetX = attackTarget.getPositionVec().getX();
				targetY = attackTarget.getPositionVec().getY() + (attackTarget.getSize(attackTarget.getPose()).height / 2);
				targetZ = attackTarget.getPositionVec().getZ();
				lockedLaser = true;
			}
			else {
				Vec3d lookDirection = projectile.getThrower().getLookVec();
				targetX = projectile.getThrower().getPositionVec().getX() + (lookDirection.x * this.range);
				targetY = projectile.getThrower().getPositionVec().getY() + projectile.getThrower().getEyeHeight() + (lookDirection.y * this.range);
				targetZ = projectile.getThrower().getPositionVec().getZ() + (lookDirection.z * this.range);
			}
		}

		// Client End:
		if(projectile.getEntityWorld().isRemote) {
			laserEnd.setPosition(targetX, targetY, targetZ);
			return laserEnd;
		}

		// Raytracing:
		HashSet<Entity> excludedEntities = new HashSet<>();
		excludedEntities.add(projectile);
		if(projectile.getThrower() != null)
			excludedEntities.add(projectile.getThrower());
		RayTraceResult rayTraceResult = Utilities.raytrace(projectile.getEntityWorld(), projectile.getPositionVec().getX(), projectile.getPositionVec().getY(), projectile.getPositionVec().getZ(), targetX, targetY, targetZ, this.width, projectile, excludedEntities);

		// Update Laser End Position:
		if(rayTraceResult != null && !lockedLaser) {
			targetX = rayTraceResult.getHitVec().x;
			targetY = rayTraceResult.getHitVec().y;
			targetZ = rayTraceResult.getHitVec().z;
			if(rayTraceResult instanceof EntityRayTraceResult) {
				Entity entityHit = ((EntityRayTraceResult)rayTraceResult).getEntity();
				targetY += entityHit.getSize(entityHit.getPose()).height / 2;
			}
		}
		laserEnd.setPosition(targetX, targetY, targetZ);

		// Laser Damage:
		if(projectile.projectileLife % 20 == 0 && projectile.isAlive() && rayTraceResult instanceof EntityRayTraceResult) {
			EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult)rayTraceResult;
			if(laserEnd.getDistance(entityRayTraceResult.getEntity()) <= (this.width * 10)) {
				boolean doDamage = true;
				if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
					doDamage = projectile.canDamage((LivingEntity) entityRayTraceResult.getEntity());
				}
				if (doDamage) {
					this.updateDamage(projectile, entityRayTraceResult.getEntity());
				}
			}
		}

		projectile.playSound(projectile.getBeamSound(), 1.0F, 1.0F / (projectile.getEntityWorld().getRandom().nextFloat() * 0.4F + 0.8F));
		return laserEnd;
	}


	public boolean updateDamage(BaseProjectileEntity projectile, Entity target) {
		boolean attackSuccess;
		float damage = projectile.getDamage(target);
		float damageInit = damage;

		// Prevent Knockback:
		double targetKnockbackResistance = 0;
		if(projectile.knockbackChance < 1) {
			if(projectile.knockbackChance <= 0 || projectile.getEntityWorld().getRandom().nextDouble() <= projectile.knockbackChance) {
				if(target instanceof LivingEntity) {
					targetKnockbackResistance = ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue();
					((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
				}
			}
		}

		// Deal Damage:
		if(projectile.getThrower() instanceof BaseCreatureEntity) {
			BaseCreatureEntity creatureThrower = (BaseCreatureEntity)projectile.getThrower();
			attackSuccess = creatureThrower.doRangedDamage(target, projectile, damage);
		}
		else {
			double pierceDamage = 1;
			if(damage <= pierceDamage)
				attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(projectile, projectile.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
			else {
				int hurtResistantTimeBefore = target.hurtResistantTime;
				target.attackEntityFrom(DamageSource.causeThrownDamage(projectile, projectile.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
				target.hurtResistantTime = hurtResistantTimeBefore;
				damage -= pierceDamage;
				attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(projectile, projectile.getThrower()), damage);
			}
		}

		if(target instanceof LivingEntity) {
			projectile.onDamage((LivingEntity) target, damageInit, attackSuccess);
		}

		// Restore Knockback:
		if(projectile.knockbackChance < 1) {
			if(projectile.knockbackChance <= 0 || projectile.getEntityWorld().getRandom().nextDouble() <= projectile.knockbackChance) {
				if(target instanceof LivingEntity)
					((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
			}
		}

		return attackSuccess;
	}

	@Override
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {}
}
