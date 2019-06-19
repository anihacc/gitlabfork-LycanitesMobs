package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHellLaser extends EntityProjectileLaser {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityHellLaser(EntityType<? extends EntityProjectileBase> entityType, World world) {
		super(entityType, world);
	}

	public EntityHellLaser(EntityType<? extends EntityProjectileBase> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(entityType, world, par2, par4, par6, setTime, setDelay);
	}

	public EntityHellLaser(EntityType<? extends EntityProjectileBase> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityHellLaser(EntityType<? extends EntityProjectileBase> entityType, World world, LivingEntity entityShooter, int setTime, int setDelay) {
		super(entityType, world, entityShooter, setTime, setDelay);
	}

	public EntityHellLaser(EntityType<? extends EntityProjectileBase> entityType, World world, LivingEntity entityShooter, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, entityShooter, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "helllaser";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(1);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(4.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityHellLaserEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
        	if(target instanceof LivingEntity)
    			((LivingEntity)target).addPotionEffect(new EffectInstance(Effects.WITHER, this.getEffectDuration(5), 0));
        }
        return damageDealt;
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "Beam") == null)
    		AssetManager.addTexture(this.entityName + "Beam", this.modInfo, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
    	return AssetManager.getTexture(this.entityName + "Beam");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound(entityName);
    }
	
	@Override
	public SoundEvent getBeamSound() {
    	return ObjectManager.getSound(entityName);
	}
}
