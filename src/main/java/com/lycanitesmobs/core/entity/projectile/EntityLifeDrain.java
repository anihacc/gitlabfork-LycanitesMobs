package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLifeDrain extends EntityProjectileLaser {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityLifeDrain(World par1World) {
		super(par1World);
	}

	public EntityLifeDrain(World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
		super(par1World, par2, par4, par6, setTime, setDelay);
	}

	public EntityLifeDrain(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityLifeDrain(World par1World, LivingEntity par2LivingEntity, int setTime, int setDelay) {
		super(par1World, par2LivingEntity, setTime, setDelay);
	}

	public EntityLifeDrain(World par1World, LivingEntity par2LivingEntity, int setTime, int setDelay, Entity followEntity) {
		super(par1World, par2LivingEntity, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "lifedrain";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.waterProof = true;
    	this.setDamage(4);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(3.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityLifeDrainEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
            this.getThrower().heal(this.getDamage(target));
        }
        return damageDealt;
    }
    
    //========== On Damage ==========
    @Override
    public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {
    	if(this.getThrower() != null) {
            this.getThrower().heal(this.getEffectStrength(damage / 2));
        }
    	super.onDamage(target, damage, attackSuccess);
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
    	return AssetManager.getSound(entityName);
    }
	
	@Override
	public SoundEvent getBeamSound() {
    	return AssetManager.getSound(entityName);
	}
}