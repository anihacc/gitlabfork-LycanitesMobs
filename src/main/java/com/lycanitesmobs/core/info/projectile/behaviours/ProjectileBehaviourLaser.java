package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
		projectile.movement = false;
		((CustomProjectileEntity)projectile).syncThrower();
		((CustomProjectileEntity)projectile).laserWidth = this.width;

		// Follow Thrower/Parent Projectile:
		if(projectile.getThrower() != null) {
			Entity entityToFollow = projectile.getThrower();
			if(((CustomProjectileEntity)projectile).getParent() != null) {
				entityToFollow = ((CustomProjectileEntity)projectile).getParent();
			}
			double xPos = entityToFollow.getPositionVector().x;
			double yPos = entityToFollow.getEntityBoundingBox().minY + (entityToFollow.height * 0.5D);
			double zPos = entityToFollow.getPositionVector().z;
			/*if(entityToFollow instanceof BaseCreatureEntity) {
				BaseCreatureEntity creatureToFollow = (BaseCreatureEntity)entityToFollow;
				xPos = creatureToFollow.getFacingPosition(creatureToFollow, 0, creatureToFollow.rotationYaw + 90F).getX();
				zPos = creatureToFollow.getFacingPosition(creatureToFollow, 0, creatureToFollow.rotationYaw).getZ();
			}*/
			projectile.setPosition(xPos, yPos, zPos);
			projectile.motionX = entityToFollow.motionX;
			projectile.motionY = entityToFollow.motionY;
			projectile.motionZ = entityToFollow.motionZ;
		}

		// Update Laser End:
		this.updateEnd(projectile);
	}

	/**
	 * Updates the end of the laser.
	 * @param projectile The projectile firing its laser!
	 */
	public void updateEnd(BaseProjectileEntity projectile) {
		// Laser Aiming:
		double targetX = projectile.getPositionVector().x;
		double targetY = projectile.getPositionVector().y;
		double targetZ = projectile.getPositionVector().z;

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
				((CustomProjectileEntity)projectile).setTarget(((BaseCreatureEntity) projectile.getThrower()).getAttackTarget());
			}
			Entity attackTarget = ((CustomProjectileEntity)projectile).getTarget();
			if(attackTarget != null) {
				targetX = attackTarget.getPositionVector().x;
				targetY = attackTarget.getPositionVector().y + (attackTarget.height / 2);
				targetZ = attackTarget.getPositionVector().z;
				lockedLaser = true;
			}
			else {
				Vec3d lookDirection = projectile.getThrower().getLookVec();
				targetX = projectile.getThrower().getPositionVector().x + (lookDirection.x * this.range);
				targetY = projectile.getThrower().getPositionVector().y + projectile.getThrower().getEyeHeight() + (lookDirection.y * this.range);
				targetZ = projectile.getThrower().getPositionVector().z + (lookDirection.z * this.range);
			}
		}

		// Raytracing:
		HashSet<Entity> excludedEntities = new HashSet<>();
		excludedEntities.add(projectile);
		if(projectile.getThrower() != null)
			excludedEntities.add(projectile.getThrower());
		RayTraceResult rayTraceResult = Utilities.raytrace(projectile.getEntityWorld(), projectile.getPositionVector().x, projectile.getPositionVector().y, projectile.getPositionVector().z, targetX, targetY, targetZ, this.width, excludedEntities);

		// Update Laser End Position:
		Entity entityHit = null;
		if(rayTraceResult != null) {
			if(!lockedLaser) {
				targetX = rayTraceResult.hitVec.x;
				targetY = rayTraceResult.hitVec.y;
				targetZ = rayTraceResult.hitVec.z;
			}
			if(rayTraceResult.entityHit != null) {
				entityHit = rayTraceResult.entityHit;
				targetY += entityHit.height / 2;
			}
		}

		((CustomProjectileEntity)projectile).setLaserEnd(new Vec3d(targetX, targetY, targetZ));

		// Laser Damage:
		if(projectile.projectileLife % 10 == 0 && projectile.isEntityAlive() && entityHit != null) {
			if(((CustomProjectileEntity)projectile).getLaserEnd().distanceTo(entityHit.getPositionVector()) <= (this.width * 10)) {
				boolean doDamage = true;
				if (entityHit instanceof EntityLivingBase) {
					doDamage = projectile.canDamage((EntityLivingBase)entityHit);
				}
				if (doDamage) {
					this.updateDamage(projectile, entityHit);
				}
			}
		}

		projectile.playSound(projectile.getBeamSound(), 1.0F, 1.0F / (projectile.getEntityWorld().rand.nextFloat() * 0.4F + 0.8F));
	}


	public boolean updateDamage(BaseProjectileEntity projectile, Entity target) {
		boolean attackSuccess;
		float damage = projectile.getDamage(target);
		float damageInit = damage;

		// Prevent Knockback:
		double targetKnockbackResistance = 0;
		if(projectile.knockbackChance < 1) {
			if(projectile.knockbackChance <= 0 || projectile.getEntityWorld().rand.nextDouble() <= projectile.knockbackChance) {
				if(target instanceof EntityLivingBase) {
					targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
					((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
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

		if(target instanceof EntityLivingBase) {
			projectile.onDamage((EntityLivingBase) target, damageInit, attackSuccess);
		}

		// Restore Knockback:
		if(projectile.knockbackChance < 1) {
			if(projectile.knockbackChance <= 0 || projectile.getEntityWorld().rand.nextDouble() <= projectile.knockbackChance) {
				if(target instanceof EntityLivingBase)
					((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
			}
		}

		return attackSuccess;
	}

	@Override
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {}
}
