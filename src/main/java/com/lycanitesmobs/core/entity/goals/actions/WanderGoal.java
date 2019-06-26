package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class WanderGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private double speed = 1.0D;
    private int pauseRate = 120;
    
    private double xPosition;
    private double yPosition;
    private double zPosition;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public WanderGoal(BaseCreatureEntity setHost) {
    	this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public WanderGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public WanderGoal setPauseRate(int setPause) {
    	this.pauseRate = setPause;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
	@Override
    public boolean shouldExecute() {
        if(this.host.getAge() >= 100)
            return false;
        else if(this.pauseRate != 0 && this.host.getRNG().nextInt(this.pauseRate) != 0)
            return false;
        else {
            Vec3d newTarget = RandomPositionGenerator.findRandomTarget(this.host, 10, 7, this.host.getFlyingHeight());
            if(newTarget == null)
                return false;
            else {
                // Random Position:
                BlockPos wanderPosition = this.host.getWanderPosition(new BlockPos((int)newTarget.x, (int)newTarget.y, (int)newTarget.z));
                this.xPosition = wanderPosition.getX();
                this.yPosition = wanderPosition.getY();
                this.zPosition = wanderPosition.getZ();

                return true;
            }
        }
    }
    
    
    // ==================================================
   	//                Continue Executing
   	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	if(!this.host.useDirectNavigator()) {
    	    if(this.host.getNavigator().noPath()) {
                return false;
            }
    	    else if(this.host.getDistanceSq(new Vec3d(this.xPosition, this.yPosition, this.zPosition)) < 4) {
                this.host.getNavigator().clearPath();
                return false;
            }
            else {
    	        return true;
            }
        }
    	else {
            return !this.host.directNavigator.atTargetPosition() && this.host.directNavigator.isTargetPositionValid();
        }
        	//return this.host.getRNG().nextInt(100) != 0 && !this.host.directNavigator.atTargetPosition() && this.host.directNavigator.isTargetPositionValid();
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
	@Override
    public void startExecuting() {
    	if(!host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
        }
    	else
    		this.host.directNavigator.setTargetPosition(new BlockPos((int)this.xPosition, (int)this.yPosition, (int)this.zPosition), this.speed);
    }
}
