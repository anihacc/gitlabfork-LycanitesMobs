package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.AvoidGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Brucha extends TameableCreatureEntity implements Enemy {
	AvoidGoal aiAvoid;

    public Brucha(EntityType<? extends Brucha> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true).setEnabled(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1));
    }

    @Override
    public void attackRanged(Entity target, float range) {
        for(int i = -2; i < 12; i++) {
            this.fireProjectile("quill", target, range, 0, new Vec3(0, 0, 0), 0.75f, 1f, i * 2.0F * (this.getRandom().nextFloat() - 0.5F));
        }

        super.attackRanged(target, range);
    }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
}
