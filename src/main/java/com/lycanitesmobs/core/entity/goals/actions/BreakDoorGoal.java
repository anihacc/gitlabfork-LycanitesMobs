package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class BreakDoorGoal extends DoorInteractGoal {
	//Properties:
    private int breakingTime;
    private int lastBreakTime = -1;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public BreakDoorGoal(MobEntity setHost) {
        super(setHost);
    }

	
	// ==================================================
 	//                  Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(!super.shouldExecute())
    		return false;
    	if(!this.host.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING))
    		return false;
    	return true;
    }

	
	// ==================================================
 	//                      Start
 	// ==================================================
	@Override
    public void startExecuting() {
        super.startExecuting();
        this.breakingTime = 0;
    }

	
	// ==================================================
 	//                Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
		return this.breakingTime <= 240 && !this.canDestroy() && this.doorPosition.withinDistance(this.host.getPositionVec(), 2.0D);
    }

	protected boolean canDestroy() {
		if (!this.doorInteract) {
			return false;
		} else {
			BlockState blockState = this.host.world.getBlockState(this.doorPosition);
			if (!(blockState.getBlock() instanceof DoorBlock)) {
				this.doorInteract = false;
				return false;
			} else {
				return blockState.get(DoorBlock.OPEN);
			}
		}
	}

	
	// ==================================================
 	//                      Reset
 	// ==================================================
	@Override
    public void resetTask() {
        super.resetTask();
        this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), -1);
    }

	
	// ==================================================
 	//                     Update
 	// ==================================================
	@Override
    public void tick() {
        super.tick();

        if(this.host.getRNG().nextInt(20) == 0)
            this.host.getEntityWorld().playEvent(1010, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);

        ++this.breakingTime;
        int breaking = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if(breaking != this.lastBreakTime) {
            this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), breaking);
            this.lastBreakTime = breaking;
        }

        if(this.breakingTime == 240 && this.host.getEntityWorld().getDifficulty() == Difficulty.HARD) {
            this.host.getEntityWorld().removeBlock(new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), true);
            this.host.getEntityWorld().playEvent(1012, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
            this.host.getEntityWorld().playEvent(2001, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
        }
    }
}
