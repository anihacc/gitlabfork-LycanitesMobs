package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.LivingEntity;

import java.util.EnumSet;

public class AvoidTargetingGoal extends TargetingGoal {
	// Targets:
    private Class targetClass = LivingEntity.class;
    
    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public AvoidTargetingGoal(EntityCreatureBase setHost) {
        super(setHost);
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AvoidTargetingGoal setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public AvoidTargetingGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }

    public AvoidTargetingGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public AvoidTargetingGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public AvoidTargetingGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public AvoidTargetingGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public AvoidTargetingGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() { return this.host.getAvoidTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAvoidTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        // Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;

		// Tamed Check:
		if(target instanceof EntityCreatureTameable && ((EntityCreatureTameable)target).isTamed())
			return false;
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if (this.host.updateTick % 20 != 0) {
			return false;
		}
		if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}

		// Check for other avoid target AIs:
        LivingEntity avoidTarget = this.getTarget();
        if(avoidTarget != null && !this.isValidTarget(avoidTarget)) {
            return false;
        }

    	this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
