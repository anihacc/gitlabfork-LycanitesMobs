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
import com.lycanitesmobs.core.info.ElementInfo;
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
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityWarg extends EntityCreatureRideable implements IGroupPredator {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWarg(EntityType<? extends EntityWarg> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
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
        this.targetSelector.addGoal(3, new OwnerDefenseTargetingGoal(this));
        this.targetSelector.addGoal(4, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
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
        			this.leap(6.0F, 1D, this.getAttackTarget());
        	}
        }

        // Leap Landing Effect:
        if(this.leapedAbilityQueued && !this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityQueued = false;
            this.leapedAbilityReady = true;
        }
        if(this.leapedAbilityReady && this.onGround && !this.getEntityWorld().isRemote) {
            this.leapedAbilityReady = false;
            double distance = 4.0D;
            List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isAlive()
						|| possibleTarget == EntityWarg.this
						|| EntityWarg.this.isRidingOrBeingRiddenBy(possibleTarget)
						|| EntityWarg.this.isOnSameTeam(possibleTarget)
						|| !EntityWarg.this.canAttack(possibleTarget.getType())
						|| !EntityWarg.this.canAttack(possibleTarget))
					return false;

				return true;
			});
            if(!possibleTargets.isEmpty()) {
                for(LivingEntity possibleTarget : possibleTargets) {
                    boolean doDamage = true;
                    if(this.getRider() instanceof PlayerEntity) {
                        if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)this.getRider(), possibleTarget))) {
                            doDamage = false;
                        }
                    }
                    if(doDamage) {
                        for(ElementInfo element : this.creatureInfo.elements) {
							element.debuffEntity(possibleTarget, this.getEffectDuration(1), this.getEffectAmplifier(1));
						}
                    }
                }
            }
            this.playAttackSound();
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(!this.onGround)
    		return 2.0F;
    	return 1.0F;
    }

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.85D;
    }

    // ========== Leap ==========
    @Override
    public void leap(double distance, double leapHeight) {
        super.leap(distance, leapHeight);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    // ========== Leap to Target ==========
    @Override
    public void leap(float range, double leapHeight, Entity target) {
        super.leap(range, leapHeight, target);
        if(!this.getEntityWorld().isRemote)
            this.leapedAbilityQueued = true;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(!this.onGround)
            return;
        if(this.abilityToggled)
            return;
        if(this.getStamina() < this.getStaminaCost())
            return;

        this.playJumpSound();
        this.leap(4.0D, 0.5D);

        this.applyStaminaCost();
    }

    @Override
    public float getStaminaCost() {
        return 15;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }
	
	
	// ==================================================
    //                     Breeding
    // ==================================================
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
