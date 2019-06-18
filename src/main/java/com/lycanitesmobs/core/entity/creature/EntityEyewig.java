package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityPoisonRay;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityEyewig extends EntityCreatureRideable {

	AttackRangedGoal rangedAttackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEyewig(EntityType<? extends EntityEyewig> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        //this.goalSelector.addGoal(2, new EntityAIPlayerControl(this));
        this.goalSelector.addGoal(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(5, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
        this.rangedAttackAI = new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false);
        this.goalSelector.addGoal(6, rangedAttackAI);
		this.goalSelector.addGoal(7, this.aiSit);
		this.goalSelector.addGoal(8, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(9, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RiderRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new RiderAttackTargetingGoal(this));
		this.targetSelector.addGoal(2, new OwnerRevengeTargetingGoal(this));
		this.targetSelector.addGoal(3, new OwnerAttackTargetingGoal(this));
		this.targetSelector.addGoal(4, new OwnerDefenseTargetingGoal(this));
        this.targetSelector.addGoal(5, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(ChickenEntity.class));
        }
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
	        this.getEntityWorld().addEntity(abilityProjectile);
    	}
    	
    	this.applyStaminaCost();
    }
    
    
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
	        this.getEntityWorld().addEntity(projectile);
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
