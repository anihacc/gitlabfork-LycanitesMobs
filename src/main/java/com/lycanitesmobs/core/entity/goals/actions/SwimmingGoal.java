package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;

import java.util.EnumSet;

public class SwimmingGoal extends Goal {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private boolean sink = false;
    
    // ==================================================
   	//                    Constructor
   	// ==================================================
    public SwimmingGoal(EntityCreatureBase setEntity) {
        this.host = setEntity;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        if(setEntity.getNavigator() instanceof GroundPathNavigator || setEntity.getNavigator() instanceof CreaturePathNavigator)
            setEntity.getNavigator().setCanSwim(true);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public SwimmingGoal setSink(boolean setSink) {
    	this.sink = setSink;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        if(this.host.getControllingPassenger() != null && this.host.getControllingPassenger() instanceof PlayerEntity && this.host.canBeSteered())
            return false;
        return this.host.isInWater() || this.host.isInLava();
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
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

			if(!this.host.isStrongSwimmer()) {
				if (this.host.posY < targetY) {
					this.host.getJumpHelper().setJumping();
				}
				else {
					this.host.addVelocity(0, -(0.01F + this.host.getAIMoveSpeed() * 0.25F), 0);
				}
			}
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F && !this.host.isStrongSwimmer()) {
			this.host.getJumpHelper().setJumping();
		}
    }
}