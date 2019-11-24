package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;

public class AvoidIfHitGoal extends FindAvoidTargetGoal {

	// Properties:
    Class[] helpClasses = null;
    private int revengeTime;

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public AvoidIfHitGoal(BaseCreatureEntity setHost) {
        super(setHost);
    }


    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AvoidIfHitGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }

    public AvoidIfHitGoal setHelpClasses(Class... setHelpClasses) {
    	this.helpClasses = setHelpClasses;
    	this.callForHelp = true;
    	return this;
    }

    public AvoidIfHitGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public AvoidIfHitGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public AvoidIfHitGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }


	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(this.host.getRevengeTarget() == null)
    		return false;

    	// Group Check:
		if(this.shouldCreatureGroupRevenge(this.host, this.host.getRevengeTarget())) {
			return false;
		}

        return this.host.getRevengeTimer() != this.revengeTime;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getRevengeTarget();
        this.revengeTime = this.host.getRevengeTimer();
		this.callNearbyForHelp();
        super.startExecuting();
    }
}
