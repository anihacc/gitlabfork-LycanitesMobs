package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class BuildAroundTargetGoal extends EntityAIBase {
	// Targets:
    private TameableCreatureEntity host;

    // Properties:
    private Block block = null;
    private int tickRate = 40;
    private int currentTick = 0;
    private int range = 2;
    private boolean enclose = false;
    private BaseCreatureEntity.TARGET_BITS targetBit = BaseCreatureEntity.TARGET_BITS.ATTACK;

	public BuildAroundTargetGoal(TameableCreatureEntity setHost) {
        this.host = setHost;
    }

    /**
     * Sets the block to build around the target.
     * @param block The block to build.
     * @return This goal for chaining.
     */
    public BuildAroundTargetGoal setBlock(Block block) {
        this.block = block;
        return this;
    }

    /**
     * Sets the tick rate for building.
     * @param tickRate The tick rate (20 ticks = 1 second).
     * @return This goal for chaining.
     */
    public BuildAroundTargetGoal setTickRate(int tickRate) {
        this.tickRate = tickRate;
        return this;
    }

    /**
     * Sets the range from the target to build (where 0 would be on the target directly).
     * @param range The range in blocks.
     * @return This goal for chaining.
     */
    public BuildAroundTargetGoal setRange(int range) {
        this.range = range;
        return this;
    }

    /**
     * If true, the range will be reduced if any of the blocks to build are found within the initial range to build in.
     * @param enclose True if the block building range should decrease and engulf the target.
     * @return This goal for chaining.
     */
    public BuildAroundTargetGoal setEnclose(boolean enclose) {
        this.enclose = enclose;
        return this;
    }

    /**
     * Sets the target bit to determine which target should be used.
     * @param targetBit The target to use's bit (defaults to ATTACK).
     * @return This goal for chaining.
     */
    public BuildAroundTargetGoal setTargetBit(BaseCreatureEntity.TARGET_BITS targetBit) {
        this.targetBit = targetBit;
        return this;
    }
    
    @Override
    public boolean shouldExecute() {
        return this.block == null || this.getTarget() != null;
    }

    public EntityLivingBase getTarget() {
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.ATTACK) {
            return this.host.getAttackTarget();
        }
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.AVOID) {
            return this.host.getAvoidTarget();
        }
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.MASTER) {
            return this.host.getMasterTarget();
        }
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.PARENT) {
            return this.host.getParentTarget();
        }
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.RIDER) {
            return this.host.getRider();
        }
        if(this.targetBit == BaseCreatureEntity.TARGET_BITS.PERCH) {
            return this.host.getPerchTarget();
        }
        return null;
    }
    
    @Override
    public void startExecuting() {
        this.currentTick = 0;
    }

    @Override
    public void updateTask() {
        if(this.currentTick++ < this.tickRate) {
            return;
        }
        this.currentTick = 0;

        int range = this.range + 1;
        BlockPos targetPos = this.getTarget().getPosition();

        // Enclose:
        if(this.enclose) {
            int smallestRange = range;
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos blockPos = targetPos.add(x, 0, z);
                    IBlockState blockState = this.host.getEntityWorld().getBlockState(blockPos);
                    if (blockState.getBlock() == this.block) {
                        smallestRange = Math.min(Math.max(Math.abs(x), Math.abs(z)), smallestRange);
                    }
                }
            }
            range -= (range + 1) - smallestRange;
        }

        // Place Blocks:
        for(int x = -range; x <= range; x++) {
            for(int z = -range; z <= range; z++) {
                if(Math.abs(x) != range && Math.abs(z) != range) {
                    continue;
                }
                BlockPos blockPos = targetPos.add(x, 0, z);
                IBlockState blockState = this.host.getEntityWorld().getBlockState(blockPos);
                if(blockState.getBlock() == Blocks.AIR || blockState.getBlock() == this.block) {
                    this.host.getEntityWorld().setBlockState(blockPos, this.block.getDefaultState());
                }
            }
        }
    }
}
