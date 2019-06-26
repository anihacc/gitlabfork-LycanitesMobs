package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityPoisonRay extends LaserProjectileEntity {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityPoisonRay(EntityType<? extends BaseProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public EntityPoisonRay(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(entityType, world, par2, par4, par6, setTime, setDelay);
	}

	public EntityPoisonRay(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityPoisonRay(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity, int setTime, int setDelay) {
		super(entityType, world, par2LivingEntity, setTime, setDelay);
	}

	public EntityPoisonRay(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2LivingEntity, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "poisonray";
    	this.modInfo = LycanitesMobs.modInfo;
		this.waterProof = true;
    	this.setDamage(3);
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
        return EntityPoisonRayEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
        	if(target instanceof LivingEntity)
    			((LivingEntity)target).addPotionEffect(new EffectInstance(Effects.POISON, this.getEffectDuration(5), 0));
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
