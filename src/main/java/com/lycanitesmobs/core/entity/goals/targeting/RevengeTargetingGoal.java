package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;

import java.util.Iterator;
import java.util.List;

public class RevengeTargetingGoal extends AttackTargetingGoal {
	
	// Properties:
    Class[] helpClasses = null;
    private int revengeTime;
    private boolean tameTargeting = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public RevengeTargetingGoal(BaseCreatureEntity setHost) {
        super(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public RevengeTargetingGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public RevengeTargetingGoal setHelpClasses(Class... setHelpClasses) {
    	this.helpClasses = setHelpClasses;
    	this.callForHelp = true;
    	return this;
    }
    public RevengeTargetingGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public RevengeTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public RevengeTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public RevengeTargetingGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        int i = this.host.getRevengeTimer();
        /*if(!this.host.isAggressive() && !(this.host instanceof EntityCreatureTameable))
        	return false;
        if(!this.host.isAggressive() && this.host instanceof EntityCreatureTameable && !((EntityCreatureTameable)this.host).isTamed())
        	return false;*/
        return i != this.revengeTime && this.isEntityTargetable(this.host.getRevengeTarget(), false);
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getRevengeTarget();
        this.revengeTime = this.host.getRevengeTimer();

        try {
            if (this.callForHelp && this.host.getOwner() == null) {
                double d0 = this.getTargetDistance();
                List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getBoundingBox().grow(d0, 4.0D, d0), this.targetSelector);
                if (this.helpClasses != null)
                    for (Class helpClass : this.helpClasses) {
                        if (helpClass != null && BaseCreatureEntity.class.isAssignableFrom(helpClass) && !this.target.getClass().isAssignableFrom(helpClass)) {
                            allies.addAll(this.host.getEntityWorld().getEntitiesWithinAABB(helpClass, this.host.getBoundingBox().grow(d0, 4.0D, d0), this.targetSelector));
                        }
                    }
                Iterator possibleAllies = allies.iterator();

                while (possibleAllies.hasNext()) {
                    BaseCreatureEntity possibleAlly = (BaseCreatureEntity) possibleAllies.next();
                    if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target) && possibleAlly.isProtective(this.host))
                        if (!(possibleAlly instanceof TameableCreatureEntity) || (possibleAlly instanceof TameableCreatureEntity && !((TameableCreatureEntity) possibleAlly).isTamed()))
                            possibleAlly.setAttackTarget(this.target);
                }
            }
        }
        catch (Exception e) {
            LycanitesMobs.logWarning("", "An exception occurred when selecting help targets in revenge, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }

        super.startExecuting();
    }
}
