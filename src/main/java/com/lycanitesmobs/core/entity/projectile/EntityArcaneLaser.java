package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityArcaneLaser extends LaserProjectileEntity {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(entityType, world, par2, par4, par6, setTime, setDelay);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6) {
		this(entityType, world, par2, par4, par6, 25, 20);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving, int setTime, int setDelay) {
		super(entityType, world, entityLiving, setTime, setDelay);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving) {
		this(entityType, world, entityLiving, 25, 20);
	}

	public EntityArcaneLaser(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, entityLiving, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaser";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(4);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(2.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityArcaneLaserEnd.class;
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(TextureManager.getTexture(this.entityName + "beam") == null)
    		TextureManager.addTexture(this.entityName + "beam", this.modInfo, "textures/item/" + this.entityName.toLowerCase() + "beam.png");
    	return TextureManager.getTexture(this.entityName + "beam");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound(this.entityName);
    }
	
	@Override
	public SoundEvent getBeamSound() {
    	return null;
	}
}
