package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;
import net.minecraft.entity.LivingEntity;

import java.util.EnumSet;

public class FindGroupAvoidTargetGoal extends FindAvoidTargetGoal {

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public FindGroupAvoidTargetGoal(BaseCreatureEntity host) {
        super(host);
    }
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FindGroupAvoidTargetGoal setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public FindGroupAvoidTargetGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public FindGroupAvoidTargetGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public FindGroupAvoidTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public FindGroupAvoidTargetGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public FindGroupAvoidTargetGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
		// Tamed Check:
		if(target instanceof TameableCreatureEntity && ((TameableCreatureEntity)target).isTamed())
			return false;

		// Group Check:
		boolean shouldFlee = false;
		boolean shouldPackHunt = false;
		for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
			if(group.shouldFlee(target)) {
				shouldFlee = true;
			}
			if(group.shouldPackHunt(target)) {
				shouldPackHunt = true;
			}
		}
    	return shouldFlee && (!shouldPackHunt || !this.host.isInPack());
    }
}