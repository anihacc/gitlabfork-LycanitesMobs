package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class AvoidGoal extends Goal {
    // Targets:
    private EntityCreatureBase host;
    private LivingEntity avoidTarget;
    
    // Properties:
    private double farSpeed = 1.0D;
    private double nearSpeed = 1.2D;
    private double farDistance = 4096.0D;
    private double nearDistance = 49.0D;
    private Class targetClass;
    private float distanceFromEntity = 6.0F;
    private Path pathEntity;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public AvoidGoal(EntityCreatureBase setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AvoidGoal setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    public AvoidGoal setNearSpeed(double setSpeed) {
    	this.nearSpeed = setSpeed;
    	return this;
    }
    public AvoidGoal setFarDistance(double dist) {
    	this.farDistance = dist * dist;
    	return this;
    }
    public AvoidGoal setNearDistance(double dist) {
    	this.nearDistance = dist * dist;
    	return this;
    }
    public AvoidGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.avoidTarget = this.host.getAvoidTarget();
        if(this.avoidTarget == null) {
        	return false;
        }
    	
        if(!this.avoidTarget.isAlive())
            return false;
    	
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(this.avoidTarget.getClass()))
            return false;

        /*if(this.host.getDistance(this.avoidTarget) >= this.farDistance) {
        	return false;
        }*/
        
        Vec3d avoidVector = RandomPositionGenerator.findRandomTargetAwayFrom(this.host, 16, 7, new Vec3d(this.avoidTarget.posX, this.avoidTarget.posY, this.avoidTarget.posZ));
        if(avoidVector == null)
            return false;
        
        if(this.avoidTarget.getDistanceSq(avoidVector.x, avoidVector.y, avoidVector.z) < this.avoidTarget.getDistance(this.host))
            return false;

        if(!this.host.useDirectNavigator()) {
            this.pathEntity = this.host.getNavigator().getPathToXYZ(avoidVector.x, avoidVector.y, avoidVector.z);
            if(this.pathEntity == null)// || !this.pathEntity.isDestinationSame(avoidVector))
                return false;
        }
        
        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
        if(!this.host.useDirectNavigator() && this.host.getNavigator().noPath())
        	return false;
		if(this.host.useDirectNavigator() && this.host.directNavigator.atTargetPosition())
			return false;

        /*if(this.host.getDistance(this.avoidTarget) >= this.farDistance)
        	return false;*/
    	return true;
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useDirectNavigator())
    		this.host.getNavigator().setPath(this.pathEntity, this.farSpeed);
    	else
    		this.host.directNavigator.setTargetPosition(this.avoidTarget, this.farSpeed);
    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
        this.avoidTarget = null;
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {
        if(this.host.getDistance(this.avoidTarget) < this.nearDistance)
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().setSpeed(this.nearSpeed);
        	else
        		this.host.directNavigator.speedModifier = this.nearSpeed;
        else
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().setSpeed(this.farSpeed);
        	else
        		this.host.directNavigator.speedModifier = this.farSpeed;
    }
}