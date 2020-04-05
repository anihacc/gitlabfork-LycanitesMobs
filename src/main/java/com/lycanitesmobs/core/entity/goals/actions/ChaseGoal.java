package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class ChaseGoal extends EntityAIBase {
	// Targets:
    private BaseCreatureEntity host;
    private EntityLivingBase target;
    
    // Properties:
    private double speed = 1.0D;
    private float maxTargetDistance = 8.0F;
    private float minTargetDistance = 0F;

    private BlockPos movePos;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public ChaseGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public ChaseGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public ChaseGoal setMaxDistance(float setDist) {
    	this.maxTargetDistance = setDist;
    	return this;
    }
	public ChaseGoal setMinDistance(float setDist) {
		this.minTargetDistance = setDist;
		return this;
	}
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
        this.target = this.host.getAttackTarget();
        if(this.target == null || !this.target.isEntityAlive()) {
			return false;
		}
        float distance = this.host.getDistance(this.target);
        if(distance > this.maxTargetDistance) {
			return false;
		}
		if(distance < this.minTargetDistance) {
			return false;
		}

		this.movePos = this.target.getPosition();

        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	if (!this.shouldExecute()) {
    		return false;
		}
		if (!this.host.useDirectNavigator() && this.host.getNavigator().noPath()) {
			return false;
		}
    	return true;
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
	@Override
    public void startExecuting() {
    	if(!this.host.useDirectNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.movePos.getX(), this.movePos.getY(), this.movePos.getZ(), this.speed);
    	else
    		this.host.directNavigator.setTargetPosition(this.movePos, this.speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.target = null;
		if(!this.host.useDirectNavigator())
			this.host.getNavigator().clearPath();
		else
			this.host.directNavigator.clearTargetPosition(this.speed);
    }
}
