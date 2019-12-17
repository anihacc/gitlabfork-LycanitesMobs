package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityProjectileModelCustom extends EntityProjectileCustom {

	// ==================================================
	//                   Constructors
	// ==================================================
	public EntityProjectileModelCustom(World world) {
		super(world);
	}

	public EntityProjectileModelCustom(World world, ProjectileInfo projectileInfo) {
		super(world, projectileInfo);
	}

	public EntityProjectileModelCustom(World world, EntityLivingBase entityLiving, ProjectileInfo projectileInfo) {
		super(world, entityLiving, projectileInfo);
	}

	public EntityProjectileModelCustom(World world, double x, double y, double z, ProjectileInfo projectileInfo) {
		super(world, x, y, z, projectileInfo);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public String getTextureName() {
		return this.entityName.toLowerCase();
	}

	@Override
	public ResourceLocation getTexture() {
		if("projectile".equals(this.getTextureName()))
			return null;
		if(AssetManager.getTexture(this.getTextureName()) == null)
			AssetManager.addTexture(this.getTextureName(), this.modInfo, "textures/projectile/" + this.getTextureName() + ".png");
		return AssetManager.getTexture(this.getTextureName());
	}
}
