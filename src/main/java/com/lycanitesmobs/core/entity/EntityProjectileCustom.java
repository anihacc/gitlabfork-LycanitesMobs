package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityProjectileCustom extends EntityProjectileBase {

	/** Used to sync the Projectile Info's name to use. **/
	protected static final DataParameter<String> PROJECTILE_NAME = EntityDataManager.createKey(EntityProjectileCustom.class, DataSerializers.STRING);

	/** The Projectile Info to base this projectile from. **/
	public ProjectileInfo projectileInfo;


	// ==================================================
	//                   Constructors
	// ==================================================
	public EntityProjectileCustom(World world) {
		super(world);
		this.dataManager.register(PROJECTILE_NAME, "");
	}

	public EntityProjectileCustom(World world, ProjectileInfo projectileInfo) {
		super(world);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.setProjectileInfo(projectileInfo);
	}

	public EntityProjectileCustom(World world, EntityLivingBase entityLivingBase, ProjectileInfo projectileInfo) {
		super(world, entityLivingBase);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.setProjectileInfo(projectileInfo);
	}

	public EntityProjectileCustom(World world, double x, double y, double z, ProjectileInfo projectileInfo) {
		super(world, x, y, z);
		this.dataManager.register(PROJECTILE_NAME, "");
		this.setProjectileInfo(projectileInfo);
	}


	// ==================================================
	//                Projectile Custom
	// ==================================================
	/**
	 * Loads the projectile info for this projectile to use.
	 * @param projectileName The name of the projectile info to use.
	 */
	public void loadProjectileInfo(String projectileName) {
		this.setProjectileInfo(ProjectileManager.getInstance().getProjectile(projectileName));
	}

	/**
	 * Sets the projectile info for this projectile to use.
	 * @param projectileInfo The projectile info to use.
	 */
	public void setProjectileInfo(ProjectileInfo projectileInfo) {
		this.projectileInfo = projectileInfo;
		if(this.projectileInfo != null) {
			this.dataManager.set(PROJECTILE_NAME, this.projectileInfo.getName());
		}
	}
}
