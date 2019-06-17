package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBloodleech extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBloodleech(World world) {
        super(world);
    }

    public EntityBloodleech(World world, LivingEntity entityLivingBase) {
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
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Damage ==========
    @Override
    public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {
    	if(this.getThrower() != null)
            this.getThrower().heal(damage);
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
