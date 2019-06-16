package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class DefenseTargetingGoal extends TargetingGoal {
	/** The entity class to defend. **/
	protected Class<? extends LivingEntity> defendClass;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public DefenseTargetingGoal(EntityCreatureBase setHost, Class<? extends LivingEntity> defendClass) {
        super(setHost);
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.defendClass = defendClass;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public DefenseTargetingGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public DefenseTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public DefenseTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
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
		if(target instanceof CreatureEntity) {
			targetTarget = ((CreatureEntity)target).getAttackTarget();
		}
		if(targetTarget == null) {
			return false;
		}

		// Ownable Checks:
		if(this.host.getOwner() != null) {
			if(target instanceof TameableEntity && this.host.getOwner() == ((TameableEntity)target).getOwner()) {
				return false;
			}
			if(target instanceof EntityCreatureTameable && this.host.getOwner() == ((EntityCreatureTameable)target).getOwner()) {
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
        
        double distance = this.getTargetDistance() - this.host.getSize(Pose.STANDING).width;
        double heightDistance = 4.0D - this.host.getSize(Pose.STANDING).height;
        if(this.host.useDirectNavigator())
            heightDistance = this.getTargetDistance() - this.host.getSize(Pose.STANDING).height;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
