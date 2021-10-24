package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Sutiramu extends TameableCreatureEntity implements Enemy {

    public Sutiramu(EntityType<? extends Sutiramu> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
    
    

    @Override
    public boolean canBurn() { return false; }

    public boolean petControlsEnabled() { return true; }
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
}
