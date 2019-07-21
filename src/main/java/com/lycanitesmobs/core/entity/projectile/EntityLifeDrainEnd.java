package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLifeDrainEnd extends LaserEndProjectileEntity {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityLifeDrainEnd(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityLifeDrainEnd(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, LaserProjectileEntity laser) {
        super(entityType, world, par2, par4, par6, laser);
    }

    public EntityLifeDrainEnd(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity shooter, LaserProjectileEntity laser) {
        super(entityType, world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "lifedrainend";
    	this.modInfo = LycanitesMobs.modInfo;
		this.waterProof = true;
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setSpeed(1.0D);
    }
    
	
	// ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(TextureManager.getTexture(this.entityName + "End") == null)
    		TextureManager.addTexture(this.entityName + "End", this.modInfo, "textures/item/" + this.entityName.toLowerCase() + ".png");
    	return TextureManager.getTexture(this.entityName + "End");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound(entityName);
	}
}
