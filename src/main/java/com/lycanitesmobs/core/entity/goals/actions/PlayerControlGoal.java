package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PlayerControlGoal extends Goal {
    // Targets:
    private EntityCreatureRideable host;
    
    // Properties:
    private double speed = 1.0D;
    private double sprintSpeed = 1.5D;
    private double flightSpeed = 1.0D;
    public boolean enabled = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public PlayerControlGoal(EntityCreatureRideable setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
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
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.isTamed())
    		return false;
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(!(this.host.getControllingPassenger() instanceof LivingEntity))
    		return false;
    	return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
    	return this.shouldExecute();
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {

    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
    	
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {

    }
}
