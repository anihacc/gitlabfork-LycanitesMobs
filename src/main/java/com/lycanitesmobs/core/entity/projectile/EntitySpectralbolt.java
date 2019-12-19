package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntitySpectralbolt extends BaseProjectileEntity {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntitySpectralbolt(World world) {
        super(world);
    }

    public EntitySpectralbolt(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntitySpectralbolt(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "spectralbolt";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
    	if(ObjectManager.getEffect("fear") != null) {
			entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getEffect("fear"), this.getEffectDuration(2), 0));
		}
		if(ObjectManager.getEffect("insomnia") != null) {
			entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getEffect("insomnia"), this.getEffectDuration(60), 0));
		}
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("Spectralbolt");
    }
}
