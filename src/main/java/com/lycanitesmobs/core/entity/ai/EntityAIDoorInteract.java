package com.lycanitesmobs.core.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class EntityAIDoorInteract extends EntityAIBase {
	// Targets:
    protected EntityLiving host;
    protected BlockDoor targetDoor;

    // Properties:
    boolean hasStoppedDoorInteraction;
    protected int entityPosX;
    protected int entityPosY;
    protected int entityPosZ;
    float entityPositionX;
    float entityPositionZ;

	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIDoorInteract(EntityLiving par1EntityLiving)
    {
        this.host = par1EntityLiving;
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
                    this.targetDoor = this.findUsableDoor(new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));

                    if(this.targetDoor != null)
                        return true;
                }
            }

            this.entityPosX = MathHelper.floor(this.host.posX);
            this.entityPosY = MathHelper.floor(this.host.posY + 1.0D);
            this.entityPosZ = MathHelper.floor(this.host.posZ);
            this.targetDoor = this.findUsableDoor(new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));
            return this.targetDoor != null;
        }
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
 	//                    Find Door
 	// ==================================================
    private BlockDoor findUsableDoor(BlockPos pos) {
        BlockState iblockstate = this.host.getEntityWorld().getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block instanceof BlockDoor && iblockstate.getMaterial() == Material.WOOD ? (BlockDoor)block : null;
    }
}
