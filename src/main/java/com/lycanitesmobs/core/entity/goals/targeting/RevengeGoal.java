package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;

import java.util.Iterator;
import java.util.List;

public class RevengeGoal extends FindAttackTargetGoal {
	
	// Properties:
    Class[] helpClasses = null;
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

    public RevengeGoal setHelpClasses(Class... setHelpClasses) {
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
    	if(this.host.getRevengeTarget() == null)
    		return false;

    	// Group Check:
		boolean shouldRevenge = this.host.creatureInfo.getGroups().isEmpty();
		boolean shouldPackHunt = false;
		for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
			if (group.shouldRevenge(this.host.getRevengeTarget())) {
				shouldRevenge = true;
			}
			if (group.shouldPackHunt(this.host.getRevengeTarget())) {
				shouldPackHunt = true;
			}
		}
		if(!shouldRevenge && (!shouldPackHunt || !this.host.isInPack())) {
			return false;
		}

		return this.host.getRevengeTimer() != this.revengeTime && this.isEntityTargetable(this.host.getRevengeTarget(), false);
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getRevengeTarget();
        this.revengeTime = this.host.getRevengeTimer();

		if (this.callForHelp && !this.host.isTamed()) {
        	try {
                double d0 = this.getTargetDistance();
                List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().grow(d0, 4.0D, d0), this.targetSelector);
                if (this.helpClasses != null)
                    for (Class helpClass : this.helpClasses) {
                        if (helpClass != null && BaseCreatureEntity.class.isAssignableFrom(helpClass) && !this.target.getClass().isAssignableFrom(helpClass)) {
                            allies.addAll(this.host.getEntityWorld().getEntitiesWithinAABB(helpClass, this.host.getEntityBoundingBox().grow(d0, 4.0D, d0), this.targetSelector));
                        }
                    }
                Iterator possibleAllies = allies.iterator();

                while(possibleAllies.hasNext()) {
                    BaseCreatureEntity possibleAlly = (BaseCreatureEntity) possibleAllies.next();
                    if (possibleAlly != this.host && !possibleAlly.hasAttackTarget() && !possibleAlly.isOnSameTeam(this.target) && possibleAlly.isProtective(this.host))
                        if (!possibleAlly.isTamed())
                            possibleAlly.setAttackTarget(this.target);
                }
            }
			catch (Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when selecting help targets in revenge, this has been skipped to prevent a crash.");
				e.printStackTrace();
			}
        }

        super.startExecuting();
    }
}
