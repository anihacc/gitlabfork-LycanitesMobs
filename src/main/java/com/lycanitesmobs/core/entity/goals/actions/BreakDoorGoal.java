package com.lycanitesmobs.core.entity.goals.actions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;

public class BreakDoorGoal extends DoorInteractGoal {
	//Properties:
    private int breakingTime;
    private int lastBreakTime = -1;


	// ==================================================
 	//                    Constructor
 	// ==================================================
    public BreakDoorGoal(EntityLiving setHost) {
        super(setHost);
    }

	
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!super.shouldExecute())
    		return false;
    	if(!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing"))
    		return false;
    	
        return !this.targetDoor.isOpen(this.host.getEntityWorld(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));
    }

	
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        super.startExecuting();
        this.breakingTime = 0;
    }

	
	// ==================================================
 	//                Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
        double distance = this.host.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
        return this.breakingTime <= 240 && !this.targetDoor.isOpen(this.host.getEntityWorld(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ)) && distance < 4.0D;
    }

	
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
        super.resetTask();
        this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), -1);
    }

	
	// ==================================================
 	//                     Update
 	// ==================================================
    public void updateTask() {
        super.updateTask();

        if(this.host.getRNG().nextInt(20) == 0)
            this.host.getEntityWorld().playEvent(1010, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);

        ++this.breakingTime;
        int breaking = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if(breaking != this.lastBreakTime) {
            this.host.getEntityWorld().sendBlockBreakProgress(this.host.getEntityId(), new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), breaking);
            this.lastBreakTime = breaking;
        }

        if(this.breakingTime == 240 && this.host.getEntityWorld().getDifficulty() == EnumDifficulty.HARD) {
            this.host.getEntityWorld().setBlockToAir(new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ));
            this.host.getEntityWorld().playEvent(1012, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
            this.host.getEntityWorld().playEvent(2001, new BlockPos(this.entityPosX, this.entityPosY, this.entityPosZ), 0);
        }
    }
}
