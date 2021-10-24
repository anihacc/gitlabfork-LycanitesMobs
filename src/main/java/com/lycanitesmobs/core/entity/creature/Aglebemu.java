package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Aglebemu extends TameableCreatureEntity implements Enemy {

    public Aglebemu(EntityType<? extends Aglebemu> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();

        this.setPathfindingMalus(BlockPathTypes.WATER, 0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setRange(3));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.onGround && !this.getCommandSenderWorld().isClientSide && this.isMoving()) {
            if(this.hasAttackTarget()) {
                if(this.random.nextInt(5) == 0)
                    this.leap(6.0F, 0.6D, this.getTarget());
            }
            else {
                if(this.random.nextInt(25) == 0)
                    this.leap(1.0D, 1.0D);
            }
        }
    }

    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 2.0F;
        else if(this.waterContact())
            return 1.5F;
        return super.getAISpeedModifier();
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = this.getCommandSenderWorld().getBlockState(pos);
        if(blockState.getBlock() == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getCommandSenderWorld().isRaining() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(pos))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public float getFallResistance() {
    	return 100;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBreatheAir() {
        return true;
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    public boolean petControlsEnabled() { return true; }
}
