package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class DefendEntitiesGoal extends TargetingGoal {
	/** The entity class to defend. **/
	protected Class<? extends EntityLiving> defendClass;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public DefendEntitiesGoal(BaseCreatureEntity setHost, Class<? extends EntityLiving> defendClass) {
        super(setHost);
        this.defendClass = defendClass;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public DefendEntitiesGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public DefendEntitiesGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public DefendEntitiesGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() {
    	return this.host.getAttackTarget();
    }

    @Override
    protected void setTarget(EntityLivingBase newTarget) {
    	this.host.setAttackTarget(newTarget);
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {

		// Owner Check:
		if(this.host.getOwner() != null) {
			return false;
		}

		// Has Target Check:
		EntityLivingBase targetTarget = target.getRevengeTarget();
		if(target instanceof EntityLiving) {
			targetTarget = ((EntityLiving)target).getAttackTarget();
		}
		if(targetTarget == null) {
			return false;
		}

		// Ownable Checks:
		if(this.host.getOwner() != null) {
			if(target instanceof EntityTameable && this.host.getOwner() == ((EntityTameable)target).getOwner()) {
				return false;
			}
			if(target instanceof TameableCreatureEntity && this.host.getOwner() == ((TameableCreatureEntity)target).getOwner()) {
				return false;
			}
			if(target == this.host.getOwner()) {
				return false;
			}
		}

		// Threat Check:
		if(this.defendClass.isAssignableFrom(targetTarget.getClass())) {
            return true;
        }
        
    	return false;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
    	// Owner Check:
    	if(this.host.getOwner() != null)
    		return false;
        
        double distance = this.getTargetDistance() - this.host.width;
        double heightDistance = 4.0D - this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = this.getTargetDistance() - this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
