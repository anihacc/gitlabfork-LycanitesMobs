package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.World;

public class EntityTpumpkyn extends TameableCreatureEntity {

    public EntityTpumpkyn(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
    }

    @Override
    public boolean rollLookChance() {
        return false;
    }

    @Override
    public boolean rollWanderChance() {
       return this.getRNG().nextDouble() <= 0.0005D;
    }
}
