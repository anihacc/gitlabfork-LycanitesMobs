package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class ChaseGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    private LivingEntity target;
    
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
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
    public boolean canUse() {
		this.target = this.host.getTarget();
		if(this.target == null || !this.target.isAlive()) {
			return false;
		}
		float distance = this.host.distanceTo(this.target);
		if(distance > this.maxTargetDistance) {
			return false;
		}
		if(distance < this.minTargetDistance) {
			return false;
		}

		this.movePos = this.target.blockPosition();

        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean canContinueToUse() {
		if (!this.canUse()) {
			return false;
		}
		if (!this.host.useDirectNavigator() && this.host.getNavigation().isDone()) {
			return false;
		}
    	return true;
    }


	// ==================================================
	//                      Start
	// ==================================================
	@Override
	public void start() {
		if(!this.host.useDirectNavigator())
			this.host.getNavigation().moveTo(this.movePos.getX(), this.movePos.getY(), this.movePos.getZ(), this.speed);
		else
			this.host.directNavigator.setTargetPosition(this.movePos, this.speed);
	}


	// ==================================================
	//                       Reset
	// ==================================================
	@Override
	public void stop() {
		this.target = null;
		if(!this.host.useDirectNavigator())
			this.host.getNavigation().stop();
		else
			this.host.directNavigator.clearTargetPosition(this.speed);
	}
}
