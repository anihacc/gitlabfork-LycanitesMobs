package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityDawon extends EntityCreatureTameable implements IGroupAnimal, IGroupPredator, IGroupHunter {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDawon(EntityType<? extends EntityDawon> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
        
        this.attackCooldownMax = 15;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this));
        this.goalSelector.addGoal(2, this.aiSit);
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(5, new AttackMeleeGoal(this).setTargetClass(ZombiePigmanEntity.class).setSpeed(1.5D).setDamage(8.0D).setRange(2.5D));
        this.goalSelector.addGoal(6, new AttackMeleeGoal(this).setSpeed(1.5D));
        this.goalSelector.addGoal(7, new WanderGoal(this).setSpeed(1.0D));
        this.goalSelector.addGoal(9, new BegGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(CowEntity.class).setTameTargetting(true));
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(PigEntity.class).setTameTargetting(true));
            this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(SheepEntity.class).setTameTargetting(true));
        }
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(ZombiePigmanEntity.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(7, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

        // Random Leaping:
        if(this.onGround && !this.getEntityWorld().isRemote) {
            if(this.hasAttackTarget()) {
                if(this.rand.nextInt(10) == 0)
                    this.leap(16.0F, 0.2D, this.getAttackTarget());
            }
            else {
                if(this.isMoving() && this.rand.nextInt(50) == 0)
                    this.leap(2.0D, 0.5D);
            }
        }
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public float getFallResistance() {
        return 100;
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
