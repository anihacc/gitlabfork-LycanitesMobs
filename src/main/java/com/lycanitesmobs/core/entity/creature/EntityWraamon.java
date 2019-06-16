package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerAttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerRevengeTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityWraamon extends EntityCreatureTameable implements IMob, IGroupShadow {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraamon(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.field_70714_bg.addTask(2, new AttackMeleeGoal(this));
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(6, new WanderGoal(this).setSpeed(1.0D).setPauseRate(30));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(ChickenEntity.class));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(3, 1));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Leap:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        // Disable Knockback:
        double targetKnockbackResistance = 0;
        if(target instanceof LivingEntity) {
            targetKnockbackResistance = ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue();
            ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

        // Melee Attack:
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Restore Knockback:
        if(target instanceof LivingEntity)
            ((LivingEntity)target).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
        
        return true;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return false; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall"))
            return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
