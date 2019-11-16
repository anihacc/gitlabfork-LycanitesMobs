package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;
import net.minecraft.entity.EntityLivingBase;

public class RevengeGoal extends FindAttackTargetGoal {
	
	// Properties:
    Class<? extends EntityLivingBase>[] helpClasses = null;
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public RevengeGoal(BaseCreatureEntity setHost) {
        super(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public RevengeGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }

    public RevengeGoal setHelpClasses(Class<? extends EntityLivingBase>... setHelpClasses) {
    	this.helpClasses = setHelpClasses;
    	this.callForHelp = true;
    	return this;
    }

    public RevengeGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public RevengeGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public RevengeGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(this.host.getRevengeTarget() == null || !this.isEntityTargetable(this.host.getRevengeTarget(), false))
    		return false;

		this.target = this.host.getRevengeTarget();
    	if(this.host.getRevengeTimer() != this.revengeTime) {
			this.revengeTime = this.host.getRevengeTimer();
			this.callNearbyForHelp();
		}

    	// Group Check:
		boolean shouldRevenge = this.host.creatureInfo.getGroups().isEmpty();
		boolean shouldPackHunt = false;
		for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
			if (group.shouldRevenge(this.target)) {
				shouldRevenge = true;
			}
			if (group.shouldPackHunt(this.target)) {
				shouldPackHunt = true;
			}
		}
		if(!shouldRevenge && (!shouldPackHunt || !this.host.isInPack())) {
			return false;
		}

		return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        super.startExecuting();
    }
}
