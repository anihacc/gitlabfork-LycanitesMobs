package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class EntityWraamon extends TameableCreatureEntity implements IMob {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraamon(EntityType<? extends EntityWraamon> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }
    
    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
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
            targetKnockbackResistance = ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
            ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

        // Melee Attack:
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Restore Knockback:
        if(target instanceof LivingEntity)
            ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
        
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
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall"))
            return false;
        return super.isVulnerableTo(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
