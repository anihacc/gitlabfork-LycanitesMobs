package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public abstract class DoorInteractGoal extends Goal {
	// Targets:
    protected MobEntity host;
    protected BlockPos doorPosition;
    protected boolean doorInteract;
    private boolean hasStoppedDoorInteraction;

    // Properties:
    protected int entityPosX;
    protected int entityPosY;
    protected int entityPosZ;
    float entityPositionX;
    float entityPositionZ;

	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public DoorInteractGoal(MobEntity setHost) {
        this.doorPosition = BlockPos.ZERO;
        this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

	
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        if(!this.host.collidedHorizontally)
            return false;

        Path pathEntity = this.host.getNavigator().getPath();

        if(pathEntity != null && !pathEntity.isFinished()) {
            for(int i = 0; i < Math.min(pathEntity.getCurrentPathIndex() + 2, pathEntity.getCurrentPathLength()); ++i) {
                PathPoint pathpoint = pathEntity.getPathPointFromIndex(i);
                this.entityPosX = pathpoint.x;
                this.entityPosY = pathpoint.y + 1;
                this.entityPosZ = pathpoint.z;

                if(this.host.getDistanceSq((double)this.entityPosX, this.host.posY, (double)this.entityPosZ) <= 2.25D) {
                    BlockPos possibleDoorPos = new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ);
                    if(this.isDoor(possibleDoorPos)) {
                        this.doorPosition = possibleDoorPos;
                        this.doorInteract = true;
                        return true;
                    }
                }
            }

            this.entityPosX = MathHelper.floor(this.host.posX);
            this.entityPosY = MathHelper.floor(this.host.posY + 1.0D);
            this.entityPosZ = MathHelper.floor(this.host.posZ);
            BlockPos possibleDoorPos = new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ);
            if(this.isDoor(possibleDoorPos)) {
                this.doorPosition = possibleDoorPos;
                this.doorInteract = true;
                return true;
            }
        }

        this.doorInteract = false;
        return false;
    }

	
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float)((double)((float)this.entityPosX + 0.5F) - this.host.posX);
        this.entityPositionZ = (float)((double)((float)this.entityPosZ + 0.5F) - this.host.posZ);
    }

	
	// ==================================================
 	//                Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
        return !this.hasStoppedDoorInteraction;
    }

	
	// ==================================================
 	//                     Update
 	// ==================================================
    public void updateTask() {
        float f = (float)((double)((float)this.entityPosX + 0.5F) - this.host.posX);
        float f1 = (float)((double)((float)this.entityPosZ + 0.5F) - this.host.posZ);
        float f2 = this.entityPositionX * f + this.entityPositionZ * f1;

        if(f2 < 0.0F)
            this.hasStoppedDoorInteraction = true;
    }

	
	// ==================================================
 	//                   Is Door
 	// ==================================================
    private boolean isDoor(BlockPos pos) {
        BlockState iblockstate = this.host.getEntityWorld().getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block instanceof DoorBlock && iblockstate.getMaterial() == Material.WOOD;
    }
}
