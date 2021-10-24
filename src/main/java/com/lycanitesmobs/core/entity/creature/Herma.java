package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Herma extends TameableCreatureEntity implements Enemy {

	WanderGoal wanderAI;

    public Herma(EntityType<? extends Herma> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

    @Override
    public float getAISpeedModifier() {
    	if(this.isInWater())
    		return 1.25F;
    	else if(this.waterContact())
    		return 1.125F;
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


    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        BlockPos groundPos;
        for(groundPos = wanderPosition.below(); groundPos.getY() > 0 && !this.getCommandSenderWorld().getBlockState(groundPos).getMaterial().isSolid(); groundPos = groundPos.below()) {}
        return groundPos.above();
    }

    @Override
    public boolean isAggressive() {
    	if(this.getAirSupply() <= -100)
    		return false;
    	return super.isAggressive();
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
