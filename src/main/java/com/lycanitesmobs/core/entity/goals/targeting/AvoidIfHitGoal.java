package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;

import java.util.Iterator;
import java.util.List;

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
    	// Group Check:
		boolean shouldFlee = true;
		boolean shouldPackHunt = false;
		for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
			if (group.shouldRevenge(this.host.getRevengeTarget())) {
				shouldFlee = false;
			}
			if (group.shouldPackHunt(this.host.getRevengeTarget())) {
				shouldPackHunt = true;
			}
		}
		if(!shouldFlee && (!shouldPackHunt || !this.host.isInPack())) {
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

		if (this.callForHelp && !this.host.isTamed()) {
        	try {
                double d0 = this.getTargetDistance();
                List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getBoundingBox().grow(d0, 4.0D, d0), this.targetSelector);
                if (this.helpClasses != null)
                    for (Class helpClass : this.helpClasses) {
                        if (helpClass != null && BaseCreatureEntity.class.isAssignableFrom(helpClass) && !this.target.getClass().isAssignableFrom(helpClass)) {
                            allies.addAll(this.host.getEntityWorld().getEntitiesWithinAABB(helpClass, this.host.getBoundingBox().grow(d0, 4.0D, d0), this.targetSelector));
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
