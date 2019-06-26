package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.Iterator;
import java.util.List;

public class RiderRevengeTargetingGoal extends AttackTargetingGoal {
	
	// Targets:
	private TameableCreatureEntity host;
	
	// Properties:
    boolean callForHelp = false;
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public RiderRevengeTargetingGoal(TameableCreatureEntity setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public RiderRevengeTargetingGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public RiderRevengeTargetingGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public RiderRevengeTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public RiderRevengeTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(this.host.getRider() == null)
    		return false;
        int i = this.host.getRider().getRevengeTimer();
        if(i == this.revengeTime)
        	return false;
        if(!this.isEntityTargetable(this.host.getRider().getRevengeTarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getRider().getRevengeTarget();
        this.revengeTime = this.host.getRider().getRevengeTimer();

        try {
            if (this.callForHelp) {
                double d0 = this.getTargetDistance();
                List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getBoundingBox().grow(d0, 4.0D, d0), (Predicate<Entity>) input -> input instanceof LivingEntity);
                Iterator possibleAllies = allies.iterator();

                while (possibleAllies.hasNext()) {
                    BaseCreatureEntity possibleAlly = (BaseCreatureEntity) possibleAllies.next();
                    if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target))
                        possibleAlly.setAttackTarget(this.target);
                }
            }
        }
        catch(Exception e) {
            LycanitesMobs.logWarning("", "An exception occurred when selecting help targets in rider revenge, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }

        super.startExecuting();
    }
}
