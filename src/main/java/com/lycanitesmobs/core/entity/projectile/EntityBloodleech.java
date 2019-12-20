package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBloodleech extends BaseProjectileEntity {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBloodleech(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityBloodleech(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLivingBase) {
        super(entityType, world, entityLivingBase);
    }

    public EntityBloodleech(EntityType<? extends BaseProjectileEntity> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "bloodleech";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
		this.setProjectileScale(1F);
        this.knockbackChance = 0.5D;
		this.weight = 0.1D;

    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Damage ==========
    @Override
    public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {
		if(this.getThrower() != null) {
			this.getThrower().heal(damage);
			if(this.getThrower().getRidingEntity() instanceof LivingEntity) {
				((LivingEntity)this.getThrower().getRidingEntity()).heal(damage * 0.1F);
			}
		}
    	super.onDamage(target, damage, attackSuccess);
    }
    
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().addParticle(RedstoneParticleData.REDSTONE_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("bloodleech");
    }
}
