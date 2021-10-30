package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public class Trite extends TameableCreatureEntity implements Enemy {
    
    public Trite(EntityType<? extends Trite> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.hasAttackTarget() && this.onGround && !this.getCommandSenderWorld().isClientSide && this.random.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getTarget());
    }
    
    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof Astaroth ||  target instanceof Asmodeus)
            return false;
        return super.canAttack(target);
    }

    @Override
    public boolean canClimb() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canBeAffected(MobEffectInstance potionEffect) {
		if(potionEffect.getEffect() == MobEffects.WITHER)
			return false;
		if(ObjectManager.getEffect("decay") != null)
			if(potionEffect.getEffect() == ObjectManager.getEffect("decay")) return false;
        super.canBeAffected(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public float getFallResistance() {
        return 10;
    }

    @Override
    public boolean webProof() { return true; }
}