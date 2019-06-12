package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.ai.EntityAISwimming;
import com.lycanitesmobs.core.entity.ai.EntityAIWander;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFear extends EntityCreatureBase {
    public Entity fearedEntity;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityFear(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasStepSound = false;
        this.hasAttackSound = false;
        this.spreadFire = false;
        this.setSize(0.8f, 1.8f);

        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this).setPauseRate(0));
    }

    public EntityFear(World world, Entity feared) {
        this(world);
        this.setFearedEntity(feared);
    }

    // ========== Setup ==========
    /** This should be called by the specific mob entity and set the default starting values. **/
    @Override
    public void setupMob() {
        // Size:
        //Set by feared entity instead.

        // Stats:
        this.experienceValue = 0;
        this.inventory = new InventoryCreature(this.getName(), this);

        // Fire Immunity:
        this.isImmuneToFire = true;
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
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Server Side Only:
        if(this.getEntityWorld().isRemote) {
			return;
		}
        
        // Clean Up:
        if(this.fearedEntity == null || !this.fearedEntity.isAlive() || !(this.fearedEntity instanceof LivingEntity)) {
        	this.remove();
        	return;
        }

        LivingEntity fearedEntityLiving = (LivingEntity)this.fearedEntity;
        
        // Pickup Entity For Fear Movement Override:
        if(this.canPickupEntity(fearedEntityLiving)) {
        	this.pickupEntity(fearedEntityLiving);
        }
        
        // Set Rotation:
        if(this.hasPickupEntity() && !(this.getPickupEntity() instanceof PlayerEntity)) {
        	this.getPickupEntity().rotationYaw = this.rotationYaw;
        	this.getPickupEntity().rotationPitch = this.rotationPitch;
        }
        
        // Follow Fear Target If Not Picked Up:
        if(this.getPickupEntity() == null) {
        	this.setPosition(this.fearedEntity.posX, this.fearedEntity.posY, this.fearedEntity.posZ);
			this.motionX = this.fearedEntity.motionX;
			this.motionY = this.fearedEntity.motionY;
			this.motionZ = this.fearedEntity.motionZ;
			this.fallDistance = 0;
        }

        // Remove When Fear is Over:
        if(ObjectManager.getEffect("fear") == null || !fearedEntityLiving.isPotionActive(ObjectManager.getEffect("fear"))) {
            this.remove();
            return;
        }

        // Copy Movement Debuffs:
		if(this.fearedEntity instanceof LivingEntity) {
			if (fearedEntityLiving.isPotionActive(MobEffects.LEVITATION)) {
				PotionEffect activeDebuff = fearedEntityLiving.getActivePotionEffect(MobEffects.LEVITATION);
				this.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
			}

			Potion instability = ObjectManager.getEffect("instability");
			if (instability != null && fearedEntityLiving.isPotionActive(instability)) {
				PotionEffect activeDebuff = fearedEntityLiving.getActivePotionEffect(instability);
				this.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
			}
		}
    }
    
    
    // ==================================================
  	//                        Fear
  	// ==================================================
    public void setFearedEntity(Entity feared) {
    	this.fearedEntity = feared;
        this.setSize(feared.width, feared.height);
        this.noClip = feared.noClip;
        this.stepHeight = feared.stepHeight;
		this.setLocationAndAngles(feared.posX, feared.posY, feared.posZ, feared.rotationYaw, feared.rotationPitch);
		
        if(feared instanceof LivingEntity && !(feared instanceof PlayerEntity)) {
	        LivingEntity fearedEntityLiving = (LivingEntity)feared;
	        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(fearedEntityLiving.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
        }
    }
	
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	return false;
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        return false;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
    	if(this.pickupEntity != null) {
    		if(this.pickupEntity instanceof EntityCreatureBase)
    			return ((EntityCreatureBase)this.pickupEntity).isFlying();
    		if(this.pickupEntity instanceof EntityFlying)
    			return true;
    		if(this.pickupEntity instanceof PlayerEntity)
    			return ((PlayerEntity)this.pickupEntity).capabilities.isFlying;
    	}
    	return false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInvisibleToPlayer(PlayerEntity player) {
        return true;
    }

    public boolean canBeCollidedWith()
    {
        return false;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected SoundEvent getAmbientSound() { return AssetManager.getSound("effect_fear"); }

    // ========== Hurt ==========
    /** Returns the sound to play when this creature is damaged. **/
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { return AssetManager.getSound("effect_fear"); }

    // ========== Death ==========
    /** Returns the sound to play when this creature dies. **/
    @Override
    protected SoundEvent getDeathSound() { return AssetManager.getSound("effect_fear"); }
     
    // ========== Fly ==========
    /** Plays a flying sound, usually a wing flap, called randomly when flying. **/
    public void playFlySound() {
    	return;
    }
}
