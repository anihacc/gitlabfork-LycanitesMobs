package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class RevengeOwnerGoal extends FindAttackTargetGoal {
	
	// Targets:
	private TameableCreatureEntity host;
	
	// Properties:
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public RevengeOwnerGoal(TameableCreatureEntity setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public RevengeOwnerGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public RevengeOwnerGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public RevengeOwnerGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public RevengeOwnerGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.getOwner() == null)
    		return false;
        if (!(this.host.getOwner() instanceof EntityLivingBase))
            return false;

		EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        int i = owner.getRevengeTimer();
        if(i == this.revengeTime)
        	return false;
        if(!this.isEntityTargetable(owner.getRevengeTarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
	@Override
    public void startExecuting() {
		EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        this.target = owner.getRevengeTarget();
        this.revengeTime = owner.getRevengeTimer();
        if(this.callForHelp) {
            this.callNearbyForHelp();
        }
        super.startExecuting();
    }
}
