package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

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
        this.setFlags(EnumSet.of(Flag.MOVE));
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
    public boolean canUse() {
        if (this.host.isVehicle()) {
            return false;
        } else if (this.isNocturnal && this.host.level.isDay()) {
            return false;
        } else if (this.host.getRandom().nextInt(this.frequency) != 0) {
            return false;
        } else {
            ServerLevel serverWorld = (ServerLevel)this.host.level;
            BlockPos blockPos = new BlockPos(this.host.blockPosition());
            if (!serverWorld.isCloseToVillage(blockPos, 6)) {
                return false;
            } else {
                Vec3 lvt_3_1_ = LandRandomPos.getPos(this.host, 15, 7, (p_220755_1_) -> {
                    return (double)(-serverWorld.getBlockFloorHeight(p_220755_1_));
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
    public boolean canContinueToUse() {
        return this.blockPos != null && !this.host.getNavigation().isDone() && this.host.getNavigation().getTargetPos().equals(this.blockPos);
    }

    
    // ==================================================
    //                     Update
    // ==================================================
    @Override
    public void tick() {
        if (this.blockPos != null) {
            PathNavigation lvt_1_1_ = this.host.getNavigation();
            if (lvt_1_1_.isDone() && !this.blockPos.closerThan(this.host.position(), 10.0D)) {
                Vec3 lvt_2_1_ = new Vec3(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
                Vec3 lvt_3_1_ = new Vec3(this.host.position().x(), this.host.position().y(), this.host.position().z());
                Vec3 lvt_4_1_ = lvt_3_1_.subtract(lvt_2_1_);
                lvt_2_1_ = lvt_4_1_.scale(0.4D).add(lvt_2_1_);
                Vec3 lvt_5_1_ = lvt_2_1_.subtract(lvt_3_1_).normalize().scale(10.0D).add(lvt_3_1_);
                BlockPos lvt_6_1_ = new BlockPos(lvt_5_1_);
                lvt_6_1_ = this.host.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, lvt_6_1_);
                if (!lvt_1_1_.moveTo(lvt_6_1_.getX(), lvt_6_1_.getY(), lvt_6_1_.getZ(), 1.0D)) {
                    this.moveRandomly();
                }
            }

        }
    }

    private void moveRandomly() {
        Random lvt_1_1_ = this.host.getRandom();
        BlockPos lvt_2_1_ = this.host.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.host.blockPosition().offset(-8 + lvt_1_1_.nextInt(16), 0, -8 + lvt_1_1_.nextInt(16)));
        this.host.getNavigation().moveTo(lvt_2_1_.getX(), lvt_2_1_.getY(), lvt_2_1_.getZ(), 1.0D);
    }
}
