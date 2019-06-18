package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class EntityVentoraptor extends EntityCreatureRideable implements IGroupPredator {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVentoraptor(EntityType<? extends EntityVentoraptor> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.hasJumpSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        //this.goalSelector.addGoal(2, new EntityAIPlayerControl(this));
        this.goalSelector.addGoal(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(5, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(6, new AttackMeleeGoal(this));
		this.goalSelector.addGoal(7, this.aiSit);
		this.goalSelector.addGoal(8, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(9, new FollowParentGoal(this).setSpeed(1.0D));
        this.goalSelector.addGoal(10, new WanderGoal(this));
        this.goalSelector.addGoal(11, new BegGoal(this));
        this.goalSelector.addGoal(12, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(13, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RiderRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new RiderAttackTargetingGoal(this));
		this.targetSelector.addGoal(2, new OwnerRevengeTargetingGoal(this));
		this.targetSelector.addGoal(3, new OwnerAttackTargetingGoal(this));
		this.targetSelector.addGoal(4, new OwnerDefenseTargetingGoal(this));
        this.targetSelector.addGoal(5, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(8, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(8, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.targetSelector.addGoal(8, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(1, 3));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Leaping:
        if(!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.5D, this.getAttackTarget());
        	}
        	else {
        		if(this.rand.nextInt(50) == 0 && this.isMoving())
        			this.leap(2.0D, 0.5D);
        	}
        }
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(Effects.WEAKNESS))
            rider.removePotionEffect(Effects.WEAKNESS);
        if(rider.isPotionActive(Effects.SLOWNESS))
            rider.removePotionEffect(Effects.SLOWNESS);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(!this.onGround)
    		return 5.0F;
    	return 1.0F;
    }

    // ========== Falling Speed Modifier ==========
    @Override
    public double getFallingMod() {
    	return 0.98D;
    }

    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.8D;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;

    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	this.playJumpSound();
    	this.leap(4.0D, 0.5D);
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 20;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
    
    
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
    	return 100;
    }


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
	
	
	// ==================================================
    //                     Breeding
    // ==================================================
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
