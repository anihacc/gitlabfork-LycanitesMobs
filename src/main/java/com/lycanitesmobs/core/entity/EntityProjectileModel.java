package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityProjectileModel extends BaseProjectileEntity {


	// ==================================================
	//                   Constructors
	// ==================================================
	public EntityProjectileModel(World world) {
		super(world);
	}

	public EntityProjectileModel(World world, EntityLivingBase entityLiving) {
		super(world, entityLiving);
	}

	public EntityProjectileModel(World world, double x, double y, double z) {
		super(world, x, y, z);
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
		if(AssetManager.getTexture(this.getTextureName()) == null)
			AssetManager.addTexture(this.getTextureName(), this.modInfo, "textures/projectile/" + this.getTextureName() + ".png");
		return AssetManager.getTexture(this.getTextureName());
	}
}
