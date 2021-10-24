package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class Zoataur extends RideableCreatureEntity implements Enemy {
    
    public Zoataur(EntityType<? extends Zoataur> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsUnderground = true;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

    @Override
    public void aiStep() {
        if(!this.getCommandSenderWorld().isClientSide && this.isBlocking() && this.hasAttackTarget()) {
            this.setTarget(null);
        }

        super.aiStep();
    }

    @Override
    public void onDamage(DamageSource damageSrc, float damage) {
    	if(!this.hasRiderTarget() && this.getRandom().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F)
    		this.setBlocking();
        super.onDamage(damageSrc, damage);
    }
    
    public void setBlocking() {
    	this.currentBlockingTime = this.blockingTime + this.getRandom().nextInt(this.blockingTime / 2);
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public float getFallResistance() {
    	return 100;
    }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("cactus")) return false;
        return super.isVulnerableTo(type, source, damage);
    }

    public boolean petControlsEnabled() { return true; }

    @Override
    public void mountAbility(Entity rider) {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(this.getStamina() < this.getStaminaCost())
            return;

        this.currentBlockingTime = 10;

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 0.5F;
    }

    public int getStaminaRecoveryWarmup() {
        return 2 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }
}
