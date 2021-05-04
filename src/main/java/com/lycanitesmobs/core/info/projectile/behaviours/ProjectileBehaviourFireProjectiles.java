package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ProjectileBehaviourFireProjectiles extends ProjectileBehaviour {
	/** The name of the projectile to fire. **/
	public String projectileName;

	/** How many ticks per projectile fired on update, if less than 0 no projectiles are fired on update. 20 ticks = 1 second. **/
	public int tickRate = 10;

	/** If above 0, all spawned projectiles are remembered and additional ones are only spawned if the total spawned amount is too low. **/
	public int persistentCount = 0;

	/** How many projectiles fired on impact. **/
	public int impactCount = 5;

	@Override
	public void loadFromJSON(JsonObject json) {
		this.projectileName = json.get("projectileName").getAsString();

		if(json.has("tickRate"))
			this.tickRate = json.get("tickRate").getAsInt();

		if(json.has("impactCount"))
			this.impactCount = json.get("impactCount").getAsInt();

		if(json.has("persistentCount"))
			this.persistentCount = json.get("persistentCount").getAsInt();
	}

	@Override
	public void onProjectileUpdate(BaseProjectileEntity projectile) {
		if(this.tickRate < 0 || projectile.updateTick % this.tickRate != 0 || projectile.getEntityWorld().isRemote) {
			return;
		}

		if(this.persistentCount <= 0 || ((CustomProjectileEntity)projectile).spawnedProjectiles.size() < this.persistentCount) {
			this.createProjectile(projectile);
		}
	}

	@Override
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
		if(projectile.getEntityWorld().isRemote) {
			return;
		}

		for(int i = 0; i < this.impactCount; i++) {
			this.createProjectile(projectile);
		}

		for(BaseProjectileEntity spawnedProjectile : ((CustomProjectileEntity)projectile).spawnedProjectiles) {
			spawnedProjectile.remove();
		}
	}

	@Override
	protected LivingEntity getShooter() {
		return null;
	}

	/**
	 * Fires a new projectile from the given projectile.
	 * @param projectile The projectile to fire a new projectile from.
	 * @return The new projectile that was fired.
	 */
	public BaseProjectileEntity createProjectile(BaseProjectileEntity projectile) {
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
		if(projectileInfo == null) {
			return null;
		}
		BaseProjectileEntity childProjectile;

		if(projectile.func_234616_v_() != null) {
			childProjectile = projectileInfo.createProjectile(projectile.getEntityWorld(), this.getShooter());
			childProjectile.setPosition(
					projectile.getPositionVec().getX(),
					projectile.getPositionVec().getY(),
					projectile.getPositionVec().getZ()
			);
		}
		else {
			childProjectile = projectileInfo.createProjectile(
					projectile.getEntityWorld(),
					projectile.getPositionVec().getX(),
					projectile.getPositionVec().getY(),
					projectile.getPositionVec().getZ()
			);
		}

		if(childProjectile instanceof CustomProjectileEntity) {
			((CustomProjectileEntity)childProjectile).setParent(projectile);
		}
		if(this.persistentCount > 0 && childProjectile instanceof CustomProjectileEntity) {
			((CustomProjectileEntity)childProjectile).laserAngle = (360F / this.persistentCount) * ((CustomProjectileEntity)projectile).spawnedProjectiles.size();
			((CustomProjectileEntity)projectile).spawnedProjectiles.add(childProjectile);
		}

		float velocity = 1.2F;
		double motionT = projectile.getMotion().getX() + projectile.getMotion().getY() + projectile.getMotion().getZ();
		if(projectile.getMotion().getX() < 0)
			motionT -= projectile.getMotion().getX() * 2;
		if(projectile.getMotion().getY() < 0)
			motionT -= projectile.getMotion().getY() * 2;
		if(projectile.getMotion().getZ() < 0)
			motionT -= projectile.getMotion().getZ() * 2;
		childProjectile.shoot(
				projectile.getMotion().getX() / motionT + (projectile.getEntityWorld().getRandom().nextGaussian() - 0.5D),
				projectile.getMotion().getY() / motionT + (projectile.getEntityWorld().getRandom().nextGaussian() - 0.5D),
				projectile.getMotion().getZ() / motionT + (projectile.getEntityWorld().getRandom().nextGaussian() - 0.5D),
				velocity,
				0
		);

		projectile.playSound(childProjectile.getLaunchSound(), 1.0F, 1.0F / (projectile.getEntityWorld().getRandom().nextFloat() * 0.4F + 0.8F));
		projectile.getEntityWorld().addEntity(childProjectile);

		return childProjectile;
	}
}
