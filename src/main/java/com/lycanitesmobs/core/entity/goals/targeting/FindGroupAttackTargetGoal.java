package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class FindGroupAttackTargetGoal extends FindAttackTargetGoal {

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public FindGroupAttackTargetGoal(BaseCreatureEntity setHost) {
        super(setHost);
    }


    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FindGroupAttackTargetGoal setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }
    
    public FindGroupAttackTargetGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public FindGroupAttackTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public FindGroupAttackTargetGoal setRange(double range) {
        this.targetingRange = range;
        return this;
    }

    public FindGroupAttackTargetGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    public FindGroupAttackTargetGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
		// Tamed Targeting Check:
		if(!this.tameTargeting && this.host.isTamed()) {
			return false;
		}

		// Group Check:
		if(!this.shouldCreatureGroupHunt(this.host, target)) {
			return false;
		}
    	
    	// Type Check:
    	if(!this.host.canAttack(target.getType()))
            return false;

        // Entity Check:
		if(!this.host.canAttack(target)) {
			return false;
		}

		// Random Chance:
		if(!this.host.rollAttackTargetChance(target)) {
			return false;
		}
        
    	return true;
    }


	// ==================================================
	//                  Get New Target
	// ==================================================
	@Override
	public LivingEntity getNewTarget(double rangeX, double rangeY, double rangeZ) {
		return super.getNewTarget(rangeX, rangeY, rangeZ);
	}
}
