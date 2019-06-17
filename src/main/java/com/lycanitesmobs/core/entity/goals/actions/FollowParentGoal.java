package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class FollowParentGoal extends FollowGoal {
	public boolean followAsAdult = false;
	
	// Targets:
	EntityCreatureAgeable host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public FollowParentGoal(EntityCreatureAgeable setHost) {
    	super(setHost);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FollowParentGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public FollowParentGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public FollowParentGoal setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public FollowParentGoal setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
    public FollowParentGoal setAdultFollowing(boolean setBool) {
    	this.followAsAdult = setBool;
    	return this;
    }
    public FollowParentGoal setFollowBehind(double setDist) {
    	this.behindDistance = setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public Entity getTarget() {
    	return this.host.getParentTarget();
    }

	@Override
	public void setTarget(Entity entity) {
    	if(entity instanceof LivingEntity)
			this.host.setParentTarget((LivingEntity) entity);
	}
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(!this.followAsAdult && !this.host.isChild())
    		return false;
    	return super.shouldExecute();
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	if(!this.followAsAdult && !this.host.isChild())
    		return false;
    	return super.shouldContinueExecuting();
    }
}
