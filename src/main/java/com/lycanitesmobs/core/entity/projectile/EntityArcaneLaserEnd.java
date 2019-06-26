package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityArcaneLaserEnd extends LaserEndProjectileEntity {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityArcaneLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityArcaneLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, LaserProjectileEntity laser) {
        super(entityType, world, par2, par4, par6, laser);
    }
    
    public EntityArcaneLaserEnd(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity shooter, LaserProjectileEntity laser) {
        super(entityType, world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaser";
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
    	if(AssetManager.getTexture(this.entityName + "end") == null)
    		AssetManager.addTexture(this.entityName + "end", this.modInfo, "textures/items/" + this.entityName.toLowerCase() + "_end.png");
    	return AssetManager.getTexture(this.entityName + "end");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public SoundEvent getLaunchSound() {
    	return null;
	}
}
