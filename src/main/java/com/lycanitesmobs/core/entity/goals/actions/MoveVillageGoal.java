package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.Heightmap;

import java.util.EnumSet;
import java.util.Random;

public class MoveVillageGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private int frequency = 200;
    private boolean isNocturnal = true;
    private BlockPos blockPos;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public MoveVillageGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public MoveVillageGoal setFrequency(int frequency) {
    	this.frequency = frequency;
    	return this;
    }
    public MoveVillageGoal setNocturnal(boolean flag) {
    	this.isNocturnal = flag;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
        if (this.host.isBeingRidden()) {
            return false;
        } else if (this.isNocturnal && this.host.world.isDaytime()) {
            return false;
        } else if (this.host.getRNG().nextInt(this.frequency) != 0) {
            return false;
        } else {
            ServerWorld serverWorld = (ServerWorld)this.host.world;
            BlockPos blockPos = new BlockPos(this.host);
            if (!serverWorld.func_217471_a(blockPos, 6)) {
                return false;
            } else {
                Vec3d lvt_3_1_ = RandomPositionGenerator.func_221024_a(this.host, 15, 7, (p_220755_1_) -> {
                    return (double)(-serverWorld.func_217486_a(SectionPos.from(p_220755_1_)));
                });
                this.blockPos = lvt_3_1_ == null ? null : new BlockPos(lvt_3_1_);
                return this.blockPos != null;
            }
        }
    }
	
    
	// ==================================================
 	//                Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
        return this.blockPos != null && !this.host.getNavigator().noPath() && this.host.getNavigator().getTargetPos().equals(this.blockPos);
    }

    
    // ==================================================
    //                     Update
    // ==================================================
    @Override
    public void tick() {
        if (this.blockPos != null) {
            PathNavigator lvt_1_1_ = this.host.getNavigator();
            if (lvt_1_1_.noPath() && !this.blockPos.withinDistance(this.host.getPositionVec(), 10.0D)) {
                Vec3d lvt_2_1_ = new Vec3d(this.blockPos);
                Vec3d lvt_3_1_ = new Vec3d(this.host.getPositionVec().getX(), this.host.getPositionVec().getY(), this.host.getPositionVec().getZ());
                Vec3d lvt_4_1_ = lvt_3_1_.subtract(lvt_2_1_);
                lvt_2_1_ = lvt_4_1_.scale(0.4D).add(lvt_2_1_);
                Vec3d lvt_5_1_ = lvt_2_1_.subtract(lvt_3_1_).normalize().scale(10.0D).add(lvt_3_1_);
                BlockPos lvt_6_1_ = new BlockPos(lvt_5_1_);
                lvt_6_1_ = this.host.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_6_1_);
                if (!lvt_1_1_.tryMoveToXYZ((double) lvt_6_1_.getX(), (double) lvt_6_1_.getY(), (double) lvt_6_1_.getZ(), 1.0D)) {
                    this.func_220754_g();
                }
            }

        }
    }

    private void func_220754_g() {
        Random lvt_1_1_ = this.host.getRNG();
        BlockPos lvt_2_1_ = this.host.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.host)).add(-8 + lvt_1_1_.nextInt(16), 0, -8 + lvt_1_1_.nextInt(16)));
        this.host.getNavigator().tryMoveToXYZ((double)lvt_2_1_.getX(), (double)lvt_2_1_.getY(), (double)lvt_2_1_.getZ(), 1.0D);
    }
}
