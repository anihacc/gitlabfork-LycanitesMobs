package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityPoisonRay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityEyewig extends EntityCreatureRideable {

	EntityAIAttackRanged rangedAttackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEyewig(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        //this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new EntityAIAttackMelee(this).setLongMemory(false).setMaxChaseDistance(4.0F));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false);
        this.field_70714_bg.addTask(6, rangedAttackAI);
		this.field_70714_bg.addTask(7, this.aiSit);
		this.field_70714_bg.addTask(8, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(9, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
		this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
		this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
		this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Rider Effects ==========
	public void riderEffects(LivingEntity rider) {
    	if(rider.isPotionActive(MobEffects.POISON))
    		rider.removeEffectInstance(MobEffects.POISON);
    	if(rider.isPotionActive(MobEffects.BLINDNESS))
    		rider.removeEffectInstance(MobEffects.BLINDNESS);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    EntityPoisonRay abilityProjectile = null;
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;
    	
    	if(this.getStamina() < this.getStaminaRecoveryMax() * 2)
    		return;

        if(this.hasAttackTarget())
            this.setAttackTarget(null);

    	// Update Laser:
    	if(this.abilityProjectile != null && this.abilityProjectile.isAlive()) {
    		this.abilityProjectile.setTime(20);
    	}
    	else {
    		this.abilityProjectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.abilityProjectile == null) {
	    	// Type:
    		if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof LivingEntity))
    			return;
    		
    		this.abilityProjectile = new EntityPoisonRay(this.getEntityWorld(), (LivingEntity)this.getControllingPassenger(), 25, 20, this);
    		this.abilityProjectile.setOffset(0, 0.5F, 0);
	    	
	    	// Launch:
	        this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().spawnEntity(abilityProjectile);
    	}
    	
    	this.applyStaminaCost();
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) { return false; }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityPoisonRay projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
    	// Update Laser:
    	if(this.projectile != null && this.projectile.isAlive()) {
    		this.projectile.setTime(20);
    	}
    	else {
    		this.projectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.projectile == null) {
	    	// Type:
	    	this.projectile = new EntityPoisonRay(this.getEntityWorld(), this, 20, 10);
	    	
	    	// Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().spawnEntity(projectile);
    	}

    	super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    @Override
    public boolean isAggressive() {
		if(this.isTamed()) {
			return super.isAggressive();
		}
    	if(this.getEntityWorld() != null && this.getEntityWorld().isDaytime())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }

	@Override
	public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 10;
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
