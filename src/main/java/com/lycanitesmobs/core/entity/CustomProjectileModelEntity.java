package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CustomProjectileModelEntity extends CustomProjectileEntity {

	// ==================================================
	//                   Constructors
	// ==================================================
	public CustomProjectileModelEntity(World world) {
		super(world);
	}

	public CustomProjectileModelEntity(World world, ProjectileInfo projectileInfo) {
		super(world, projectileInfo);
	}

	public CustomProjectileModelEntity(World world, EntityLivingBase entityLiving, ProjectileInfo projectileInfo) {
		super(world, entityLiving, projectileInfo);
	}

	public CustomProjectileModelEntity(World world, double x, double y, double z, ProjectileInfo projectileInfo) {
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
