package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;

public class EntityNecrovore extends TameableCreatureEntity {
    public EntityNecrovore(EntityType<? extends EntityNecrovore> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEAD;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;

        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.HUSK, EntityType.ZOMBIE_VILLAGER));
        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityGeist.class));
    }

    @Override
    public boolean daylightBurns() {
        return !this.isBaby() && !this.isTamed();
    }

    @Override
    public boolean canBurn() {
        return true;
    }
}
