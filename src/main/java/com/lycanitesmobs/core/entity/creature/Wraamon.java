package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

public class Wraamon extends TameableCreatureEntity implements Enemy {

    public Wraamon(EntityType<? extends Wraamon> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide && this.hasAttackTarget() && this.onGround && !this.getCommandSenderWorld().isClientSide && this.random.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getTarget());
    }
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        double targetKnockbackResistance = 0;
        if(target instanceof LivingEntity) {
            targetKnockbackResistance = ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
            ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }

    	if(!super.attackMelee(target, damageScale))
    		return false;

        if(target instanceof LivingEntity)
            ((LivingEntity)target).getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
        
        return true;
    }

    @Override
    public boolean canClimb() { return false; }

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

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
