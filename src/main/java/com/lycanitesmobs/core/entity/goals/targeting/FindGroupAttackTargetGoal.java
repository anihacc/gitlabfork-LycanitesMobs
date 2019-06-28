package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;
import net.minecraft.entity.EntityType;
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
    public FindGroupAttackTargetGoal setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    
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
    	// Group Check:
		boolean shouldFlee = false;
		boolean shouldAttack = false;
		boolean shouldPackAttack = false;
		for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
			if(group.shouldFlee(target)) {
				shouldFlee = false;
			}
			if(group.shouldHunt(target)) {
				shouldAttack = true;
			}
			if(group.shouldPackHunt(target)) {
				shouldPackAttack = true;
			}
		}
		if(shouldFlee) {
			if(!shouldPackAttack || !this.host.isInPack()) {
				return false;
			}
		}
		else {
			if(!shouldAttack && (!shouldPackAttack || !this.host.isInPack())) {
				return false;
			}
		}

		// Tamed Targeting Check:
		if(!this.tameTargeting && this.host.isTamed())
			return false;
    	
    	// Type Check:
    	if(!this.host.canAttack(target.getType()))
            return false;

        // Entity Check:
		if(!this.host.canAttack(target)) {
			return false;
		}
        
    	return true;
    }


	// ==================================================
	//                  Get New Target
	// ==================================================
	@Override
	public LivingEntity getNewTarget(double rangeX, double rangeY, double rangeZ) {
		// Faster Player Targeting:
		if(this.targetPlayers) {
			LivingEntity newTarget = null;
			try {
				List<? extends PlayerEntity> players = this.host.getEntityWorld().getPlayers();
				if (players.isEmpty())
					return null;
				List<PlayerEntity> possibleTargets = new ArrayList<>();
				for(PlayerEntity player : players) {
					if(this.isValidTarget(player))
						possibleTargets.add(player);
				}
				if (possibleTargets.isEmpty())
					return null;
				Collections.sort(possibleTargets, this.nearestSorter);
				newTarget = possibleTargets.get(0);
			}
			catch (Exception e) {
				LycanitesMobs.logWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
				e.printStackTrace();
			}

			// Return player target unless other entities should also be targeted.
			if(newTarget != null) {
				return newTarget;
			}
		}

		if(this.host.updateTick % 40 == 0) {
			return super.getNewTarget(rangeX, rangeY, rangeZ);
		}

		return null;
	}
}
