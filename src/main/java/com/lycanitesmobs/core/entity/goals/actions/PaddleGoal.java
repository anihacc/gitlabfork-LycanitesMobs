package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;

public class PaddleGoal extends EntityAIBase {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private boolean sink = false;
    
    // ==================================================
   	//                    Constructor
   	// ==================================================
    public PaddleGoal(BaseCreatureEntity setEntity) {
        this.host = setEntity;
		this.setMutexBits(4);
		if(setEntity.getNavigator() instanceof PathNavigateGround)
			((PathNavigateGround)setEntity.getNavigator()).setCanSwim(true);
		this.sink = this.host.canBreatheUnderwater() || (this.host.canBreatheUnderlava() && this.host.isLavaCreature);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public PaddleGoal setSink(boolean setSink) {
    	this.sink = setSink;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(this.host.isStrongSwimmer())
    		return false;
        if(this.host.getControllingPassenger() != null && this.host.getControllingPassenger() instanceof EntityPlayer && this.host.canBeSteered())
            return false;
        return this.host.isInWater() || this.host.isInLava();
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
	@Override
    public void updateTask() {
    	if(this.sink) {
	    	double targetY = this.host.posY;
	    	if(!this.host.useDirectNavigator()) {
	    		if(!this.host.getNavigator().noPath()) {
                    targetY = this.host.getNavigator().getPath().getFinalPathPoint().y;
                    if(this.host.hasAttackTarget())
                        targetY = this.host.getAttackTarget().posY;
                    else if(this.host.hasParent())
                        targetY = this.host.getParentTarget().posY;
                    else if(this.host.hasMaster())
                        targetY = this.host.getMasterTarget().posY;
                }
	    	}
	    	else {
	    		if(!this.host.directNavigator.atTargetPosition()) {
                    targetY = this.host.directNavigator.targetPosition.getY();
                }
	    	}

			if (this.host.posY < targetY) {
				this.host.getJumpHelper().setJumping();
			}
			else {
				this.host.addVelocity(0, -(0.01F + this.host.getAIMoveSpeed() * 0.25F), 0);
			}
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F) {
			this.host.getJumpHelper().setJumping();
		}
    }
}
