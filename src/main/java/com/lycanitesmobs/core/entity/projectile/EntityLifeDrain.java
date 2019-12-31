package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.LaserProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLifeDrain extends LaserProjectileEntity {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(entityType, world, par2, par4, par6, setTime, setDelay);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6) {
		this(entityType, world, par2, par4, par6, 25, 20);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity, int setTime, int setDelay) {
		super(entityType, world, par2LivingEntity, setTime, setDelay);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLiving) {
		this(entityType, world, entityLiving, 25, 20);
	}

	public EntityLifeDrain(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity, int setTime, int setDelay, Entity followEntity) {
		super(entityType, world, par2LivingEntity, setTime, setDelay, followEntity);
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
    	if(TextureManager.getTexture(this.entityName + "Beam") == null)
    		TextureManager.addTexture(this.entityName + "Beam", this.modInfo, "textures/item/" + this.entityName.toLowerCase() + "beam.png");
    	return TextureManager.getTexture(this.entityName + "Beam");
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
