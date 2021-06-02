package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public class EntityVorach extends TameableCreatureEntity implements IMob {

    public EntityVorach(World world) {
        super(world);
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.setupMob();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
            return false;

        // Leech:
        float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
        this.heal(leeching);

        return true;
    }

    @Override
    public float getAISpeedModifier() {
        if (this.hasAttackTarget() && this.getDistanceSq(this.getAttackTarget()) >= 60) {
            if (this.isLookingAtMe(this.getAttackTarget())) {
                return 0;
            }
            return super.getAISpeedModifier() * 5;
        }
        return super.getAISpeedModifier();
    }

    @Override
    public boolean canClimb() { return true; }

    @Override
    public boolean canBurn() { return false; }

    @Override
    public float getFallResistance() {
        return 10;
    }
}
