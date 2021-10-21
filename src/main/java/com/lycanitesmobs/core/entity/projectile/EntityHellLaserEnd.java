package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public class EntityHellLaserEnd extends LaserEndProjectileEntity {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityHellLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, Level world) {
        super(entityType, world);
    }

    public EntityHellLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, Level world, double par2, double par4, double par6, LaserProjectileEntity laser) {
        super(entityType, world, par2, par4, par6, laser);
    }
    
    public EntityHellLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, Level world, LivingEntity shooter, LaserProjectileEntity laser) {
        super(entityType, world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "helllaserend";
    	this.modInfo = LycanitesMobs.modInfo;
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
