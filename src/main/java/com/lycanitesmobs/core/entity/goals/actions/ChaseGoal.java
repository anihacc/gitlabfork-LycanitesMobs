package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ChaseGoal extends Goal {
	// Targets:
    private EntityCreatureBase host;
    private LivingEntity target;
    
    // Properties:
    private double speed = 1.0D;
    private float maxTargetDistance = 8.0F;
    
    private BlockPos movePos;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public ChaseGoal(EntityCreatureBase setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
        this.target = this.host.getAttackTarget();
        if(this.target == null) {
			return false;
		}
        //else if(this.host.getDistance(this.target) > (double)(this.maxTargetDistance * this.maxTargetDistance))
            //return false;
        
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, new Vec3d(this.target.posX, this.target.posY, this.target.posZ));
        if(vec3 == null)
            return false;
        
        this.movePos = new BlockPos(vec3.x, vec3.y, vec3.z);
        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
		if (!this.host.isAlive()) {
			return false;
		}
		boolean fixated = this.host.hasFixateTarget() && this.host.getFixateTarget() == this.target;
		if(!fixated && this.target.getDistance(this.host) > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
			return false;
		}
		if (this.host.getNavigator().noPath()) {
			return this.shouldExecute();
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
    		this.host.directNavigator.setTargetPosition(this.movePos, speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.target = null;
        this.host.directNavigator.clearTargetPosition(1.0D);
    }
}
