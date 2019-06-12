package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAcidSplash extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityAcidSplash(World world) {
        super(world);
    }

    public EntityAcidSplash(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityAcidSplash(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "acidsplash";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
    	this.setProjectileScale(0.5F);
        this.knockbackChance = 0D;
		this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
		Potion penetration = ObjectManager.getEffect("penetration");
		if(penetration != null)
			entityLiving.addPotionEffect(new PotionEffect(penetration, this.getEffectDuration(10), 1));
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.CLOUD, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
