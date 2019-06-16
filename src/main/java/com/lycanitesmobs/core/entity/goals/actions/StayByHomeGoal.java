package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class StayByHomeGoal extends Goal {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private boolean enabled = true;
    private double speed = 1.0D;
    private double farSpeed = 1.5D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public StayByHomeGoal(EntityCreatureBase setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public StayByHomeGoal setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    public StayByHomeGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    public StayByHomeGoal setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.hasHome())
    		return false;
        if(this.host.isInWater() && !this.host.canBreatheUnderwater())
            return false;
        if(!this.host.onGround && !this.host.useDirectNavigator())
            return false;
        
        return true;
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.clearMovement();
        if(this.host.hasHome() && this.host.getDistanceFromHome() > 1.0F) {
            BlockPos homePos = this.host.getHomePosition();
        	double speed = this.speed;
        	if(this.host.getDistanceFromHome() > this.host.getHomeDistanceMax())
        		speed = this.farSpeed;
	    	if(!host.useDirectNavigator())
	    		this.host.getNavigator().tryMoveToXYZ(homePos.getX(), homePos.getY(), homePos.getZ(), this.speed);
	    	else
	    		host.directNavigator.setTargetPosition(new BlockPos((int)homePos.getX(), (int)homePos.getY(), (int)homePos.getZ()), speed);
        }
    }
}
