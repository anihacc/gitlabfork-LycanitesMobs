package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.LivingEntity;

public class EntityAITargetingOwnerRevenge extends AttackTargetingGoal {
	
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITargetingOwnerRevenge(EntityCreatureTameable setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetingOwnerRevenge setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public EntityAITargetingOwnerRevenge setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetingOwnerRevenge setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetingOwnerRevenge setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.getOwner() == null)
    		return false;
        if (!(this.host.getOwner() instanceof LivingEntity))
            return false;
        LivingEntity owner = (LivingEntity)this.host.getOwner();
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
    public void startExecuting() {
        LivingEntity owner = (LivingEntity)this.host.getOwner();
        this.target = owner.getRevengeTarget();
        this.revengeTime = owner.getRevengeTimer();
        if(this.callForHelp) {
            this.callNearbyForHelp();
        }
        super.startExecuting();
    }
}
