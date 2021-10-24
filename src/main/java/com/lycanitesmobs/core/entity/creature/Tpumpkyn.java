package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;

public class Tpumpkyn extends TameableCreatureEntity {

    public Tpumpkyn(EntityType<? extends Tpumpkyn> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
    }

    @Override
    public boolean rollLookChance() {
        return false;
    }

    @Override
    public boolean rollWanderChance() {
        return this.getRandom().nextDouble() <= 0.0005D;
    }
}
