package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;

import java.util.EnumSet;

public class DefenceTargetingGoal extends TargetingGoal {
	/** The entity class to defend. **/
	protected Class<? extends LivingEntity> defendClass;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public DefenceTargetingGoal(EntityCreatureBase setHost, Class<? extends LivingEntity> defendClass) {
        super(setHost);
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.defendClass = defendClass;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public DefenceTargetingGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public DefenceTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public DefenceTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() {
    	return this.host.getAttackTarget();
    }

    @Override
    protected void setTarget(LivingEntity newTarget) {
    	this.host.setAttackTarget(newTarget);
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {

		// Owner Check:
		if(this.host.getOwner() != null) {
			return false;
		}

		// Has Target Check:
		LivingEntity targetTarget = target.getRevengeTarget();
		if(target instanceof EntityCreature) {
			targetTarget = ((EntityCreature)target).getAttackTarget();
		}
		else if(target instanceof EntityCreatureBase) {
			targetTarget = ((EntityCreatureBase)target).getAttackTarget();
		}
		if(targetTarget == null) {
			return false;
		}

		// Ownable Checks:
		if(this.host.getOwner() != null) {
			if(target instanceof IEntityOwnable && this.host.getOwner() == ((IEntityOwnable)target).getOwner()) {
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
