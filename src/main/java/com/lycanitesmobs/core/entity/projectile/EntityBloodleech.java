package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.AssetManager;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBloodleech extends BaseProjectileEntity {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBloodleech(World world) {
        super(world);
    }

    public EntityBloodleech(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityBloodleech(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "bloodleech";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
        this.knockbackChance = 0.5D;
        this.weight = 0.1D;
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Damage ==========
    @Override
    public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
    	if(this.getThrower() != null) {
			this.getThrower().heal(damage);
			if(this.getThrower().getRidingEntity() instanceof EntityLiving) {
				((EntityLiving)this.getThrower().getRidingEntity()).heal(damage * 0.1F);
			}
		}
    	super.onDamage(target, damage, attackSuccess);
    }
    
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("bloodleech");
    }
}
