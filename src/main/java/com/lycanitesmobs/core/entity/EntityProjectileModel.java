package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityProjectileModel extends EntityProjectileBase {


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
	public String getTextureName() {
	return this.entityName.toLowerCase();
	}

	public ResourceLocation getTexture() {
		if(AssetManager.getTexture(this.getTextureName()) == null)
			AssetManager.addTexture(this.getTextureName(), this.group, "textures/projectile/" + this.getTextureName() + ".png");
		return AssetManager.getTexture(this.getTextureName());
	}
}
