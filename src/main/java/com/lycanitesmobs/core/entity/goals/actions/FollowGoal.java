package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public abstract class FollowGoal extends Goal {
	// Targets:
	EntityCreatureBase host;
    
    // Properties:
    double speed = 1.0D;
    Class targetClass;
    private int updateRate;
    double strayDistance = 1.0D;
    double lostDistance = 64.0D;
    double behindDistance = 0;
    
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public FollowGoal(EntityCreatureBase setHost) {
        this.host = setHost;
        this.targetClass = this.host.getClass();
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
	
	// ==================================================
 	//                      Target
 	// ==================================================
    public abstract Entity getTarget();
	public abstract void setTarget(Entity entity);
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FollowGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public FollowGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public FollowGoal setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public FollowGoal setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
    public FollowGoal setFollowBehind(double setDist) {
    	this.behindDistance = setDist;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	Entity target = this.getTarget();
	    if(target == null)
	        return false;
        if(!target.isAlive())
        	return false;

        double distance = this.host.getDistance(target);
	    if(distance > this.lostDistance && this.lostDistance != 0)
	        return false;
	    if(distance <= this.strayDistance && this.strayDistance != 0)
	        return false;
	    
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean shouldContinueExecuting() {
    	Entity target = this.getTarget();
    	if(target == null)
    		return false;
        if(!target.isAlive())
        	return false;
        
        double distance = this.host.getDistance(target);
        if(distance > this.lostDistance && this.lostDistance != 0)
        	this.setTarget(null);
        // Start straying when we reach halfway between the stray radius and the target
        if(distance <= this.strayDistance / 2.0 && this.strayDistance != 0)
        	return false;
		this.onTargetDistance(distance, target);
        
        return this.getTarget() != null;
    }
    
    
    // ==================================================
  	//                       Start
  	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 10;
            Entity target = this.getTarget();
        	if(!this.host.useDirectNavigator()) {
        		if(this.behindDistance == 0 || !(target instanceof EntityCreatureBase)) {
                    this.host.getNavigator().tryMoveToEntityLiving(target, this.speed);
                }
        		else {
        			BlockPos pos = ((EntityCreatureBase)target).getFacingPosition(-this.behindDistance);
        			this.host.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.speed);
        		}
        	}
        	else {
        		if(this.behindDistance == 0 || !(target instanceof EntityCreatureBase))
        			this.host.directNavigator.setTargetPosition(new BlockPos((int)target.posX, (int)target.posY, (int)target.posZ), this.speed);
        		else {
                    BlockPos pos = ((EntityCreatureBase)target).getFacingPosition(-this.behindDistance);
        			this.host.directNavigator.setTargetPosition(pos, this.speed);
        		}
        	}
        }
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.clearMovement();
    }


	// ==================================================
	//                  Target Distance
	// ==================================================
	public void onTargetDistance(double distance, Entity followTarget) {

	}
}
