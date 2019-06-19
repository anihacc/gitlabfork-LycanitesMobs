package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityProjectileModel extends EntityProjectileBase {


	// ==================================================
	//                   Constructors
	// ==================================================
	public EntityProjectileModel(EntityType<? extends EntityProjectileBase> entityType, World world) {
		super(entityType, world);
	}

	public EntityProjectileModel(EntityType<? extends EntityProjectileBase> entityType, World world, LivingEntity entityLiving) {
		super(entityType, world, entityLiving);
	}

	public EntityProjectileModel(EntityType<? extends EntityProjectileBase> entityType, World world, double x, double y, double z) {
		super(entityType, world, x, y, z);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	public String getTextureName() {
	return this.entityName.toLowerCase();
	}

	public ResourceLocation getTexture() {
		if(AssetManager.getTexture(this.getTextureName()) == null)
			AssetManager.addTexture(this.getTextureName(), this.modInfo, "textures/projectile/" + this.getTextureName() + ".png");
		return AssetManager.getTexture(this.getTextureName());
	}
}
