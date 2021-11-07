package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.inventory.CreatureInventory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FearEntity extends BaseCreatureEntity {
    public LivingEntity fearedEntity;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public FearEntity(EntityType<? extends FearEntity> entityType, Level world) {
        super(entityType, world);
        
        // Setup:
        this.hasStepSound = false;
        this.hasAttackSound = false;
        this.spreadFire = false;
        //this.setSize(0.8f, 1.8f);

        this.setupMob();
    }

    public FearEntity(EntityType<? extends FearEntity> entityType, Level world, LivingEntity feared) {
        this(entityType, world);
        this.setFearedEntity(feared);
    }

    // ========== Setup ==========
    /** This should be called by the specific mob entity and set the default starting values. **/
    @Override
    public void setupMob() {
        // Size:
        //Set by feared entity instead.

        // Stats:
        this.xpReward = 0;
        this.inventory = new CreatureInventory(this.getName().toString(), this);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {}
	
	
	// ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawnNaturally() {
    	return false;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void aiStep() {
        super.aiStep();
        
        // Server Side Only:
        if(this.getCommandSenderWorld().isClientSide) {
			return;
		}
        
        // Clean Up:
        if(this.fearedEntity == null || !this.fearedEntity.isAlive() || !(this.fearedEntity instanceof LivingEntity)) {
        	this.discard();
        	return;
        }

        LivingEntity fearedLivingEntity = (LivingEntity)this.fearedEntity;
        
        // Pickup Entity For Fear Movement Override:
        if(this.canPickupEntity(this.fearedEntity)) {
        	this.pickupEntity(this.fearedEntity);
        }
        
        // Set Rotation:
        if(this.hasPickupEntity() && !(this.getPickupEntity() instanceof Player)) {
        	this.getPickupEntity().setYRot(this.getYRot());
        	this.getPickupEntity().setXRot(this.getXRot());
        }
        
        // Follow Fear Target If Not Picked Up:
        if(this.getPickupEntity() == null) {
        	this.setPos(this.fearedEntity.position().x(), this.fearedEntity.position().y(), this.fearedEntity.position().z());
			this.setDeltaMovement(this.fearedEntity.getDeltaMovement());
			this.fallDistance = 0;
        }

        // Remove When Fear is Over:
        if(ObjectManager.getEffect("fear") == null || !fearedEntity.hasEffect(ObjectManager.getEffect("fear"))) {
            this.discard();
            return;
        }

        // Copy Movement Debuffs:
		if(this.fearedEntity != null) {
			if (fearedEntity.hasEffect(MobEffects.LEVITATION)) {
				MobEffectInstance activeDebuff = fearedEntity.getEffect(MobEffects.LEVITATION);
				this.addEffect(new MobEffectInstance(MobEffects.LEVITATION, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
			}

			MobEffect instability = ObjectManager.getEffect("instability");
			if (instability != null && fearedEntity.hasEffect(instability)) {
				MobEffectInstance activeDebuff = fearedEntity.getEffect(instability);
				this.addEffect(new MobEffectInstance(instability, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
			}
		}
    }

    @Override
	public boolean rollWanderChance() {
		return true;
	}
    
    
    // ==================================================
  	//                        Fear
  	// ==================================================
    public void setFearedEntity(LivingEntity feared) {
    	this.fearedEntity = feared;
        //this.setSize(feared.width, feared.height); TODO Entity Type Size
        this.noPhysics = feared.noPhysics;
        this.maxUpStep = feared.maxUpStep;
		this.moveTo(feared.position().x(), feared.position().y(), feared.position().z(), feared.getYRot(), feared.getXRot());
		
        if(!(feared instanceof Player)) {
	        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(feared.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
        }
    }
	
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	return false;
    }
    
    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
    	if(this.pickupEntity != null) {
    		if(this.pickupEntity instanceof BaseCreatureEntity)
    			return ((BaseCreatureEntity)this.pickupEntity).isFlying();
    		if(this.pickupEntity instanceof FlyingMob)
    			return true;
    		if(this.pickupEntity instanceof Player)
    			return ((Player)this.pickupEntity).getAbilities().instabuild;
    	}
    	return false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInvisibleTo(Player player) {
        return true;
    }

    public boolean isPickable()
    {
        return false;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected SoundEvent getAmbientSound() { return ObjectManager.getSound("effect_fear"); }

    // ========== Hurt ==========
    /** Returns the sound to play when this creature is damaged. **/
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { return ObjectManager.getSound("effect_fear"); }

    // ========== Death ==========
    /** Returns the sound to play when this creature dies. **/
    @Override
    protected SoundEvent getDeathSound() { return ObjectManager.getSound("effect_fear"); }
     
    // ========== Fly ==========
    /** Plays a flying sound, usually a wing flap, called randomly when flying. **/
    public void playFlySound() {}
}
