package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class SitGoal extends Goal {
	// Targets:
    private EntityCreatureTameable host;
    
    // Properties:
    private boolean enabled = true;
    private double speed = 1.0D;
    private double farSpeed = 1.5D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public SitGoal(EntityCreatureTameable setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public SitGoal setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    public SitGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    public SitGoal setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
        if(!this.host.isTamed())
            return false;
        if(this.host.isInWater()) {
        	if(!this.host.canBreatheUnderwater())
        		return false;
		}
        else if(!this.host.onGround && !this.host.isCurrentlyFlying())
            return false;

        if (!(this.host.getOwner() instanceof LivingEntity))
            return false;
        LivingEntity owner = (LivingEntity)this.host.getOwner();
        if(owner != null && this.host.getDistance(owner) < 144.0D && owner.getRevengeTarget() != null && !this.host.isPassive())
        	return false;
        
        return this.host.isSitting();
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
	    		host.directNavigator.setTargetPosition(new BlockPos(homePos), speed);
        }
    }
}
