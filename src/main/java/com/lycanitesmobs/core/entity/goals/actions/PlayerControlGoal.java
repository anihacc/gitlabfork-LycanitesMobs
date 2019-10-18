package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class PlayerControlGoal extends EntityAIBase {
    // Targets:
    private RideableCreatureEntity host;
    
    // Properties:
    private double speed = 1.0D;
    private double sprintSpeed = 1.5D;
    private double flightSpeed = 1.0D;
    public boolean enabled = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public PlayerControlGoal(RideableCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public PlayerControlGoal setSpeed(double set) {
    	this.speed = set;
    	return this;
    }

    public PlayerControlGoal setSprintSpeed(double set) {
    	this.sprintSpeed = set;
    	return this;
    }

    public PlayerControlGoal setFlightSpeed(double set) {
    	this.flightSpeed = set;
    	return this;
    }
    
    public PlayerControlGoal setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.isTamed())
    		return false;
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(!(this.host.getControllingPassenger() instanceof EntityLivingBase))
    		return false;
    	return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	return this.shouldExecute();
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
	@Override
    public void startExecuting() {

    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
	@Override
    public void resetTask() {
    	
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
	@Override
    public void updateTask() {

    }
}
