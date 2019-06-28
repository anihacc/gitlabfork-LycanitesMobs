package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class FindAttackTargetGoal extends TargetingGoal {
	// Targets:
    private List<EntityType> targetTypes = new ArrayList<>();

    // Properties:
	protected boolean targetPlayers;
	private boolean requirePack = false;
    protected boolean tameTargeting = false;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public FindAttackTargetGoal(BaseCreatureEntity setHost) {
        super(setHost);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }


    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FindAttackTargetGoal setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }

	public FindAttackTargetGoal addTargets(EntityType... targets) {
		this.targetTypes.addAll(Arrays.asList(targets));
		for(EntityType targetType : targets) {
			this.host.setHostileTo(targetType);
			if(targetType == EntityType.PLAYER)
				this.targetPlayers = true;
		}
    	return this;
    }
    
    public FindAttackTargetGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public FindAttackTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public FindAttackTargetGoal setRange(double range) {
        this.targetingRange = range;
        return this;
    }

    public FindAttackTargetGoal setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    public FindAttackTargetGoal setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    /** Makes the creature require a pack for this targeting to. **/
    public FindAttackTargetGoal requiresPack() {
    	this.requirePack = true;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAttackTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
    	// Target Type Check:
		if(this.targetTypes.size() > 0 && !this.targetTypes.contains(target.getType())) {
			return false;
		}

		// Tamed Targeting Check:
		if(!this.tameTargeting && this.host.isTamed()) {
			return false;
		}

		// Random Chance:
		if(!this.host.rollAttackTargetChance(target)) {
			return false;
		}
    	
    	// Type Check:
    	if(!this.host.canAttack(target.getType()))
            return false;

        // Entity Check:
		if(!this.host.canAttack(target)) {
			return false;
		}
        
        // Pack Check:
        if(this.requirePack && !this.host.isInPack()) {
            return false;
        }
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if(!this.host.isAggressive() || this.host.hasFixateTarget()) {
			return false;
		}

		if(this.targetPlayers) {
			if (this.host.updateTick % 10 != 0) {
				return false;
			}
		}
		else {
			if (this.host.updateTick % 40 != 0) {
				return false;
			}
		}

		this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D + this.host.getHeight();
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();

        return this.target != null;
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
			if(newTarget != null || this.targetTypes.size() == 1) {
				return newTarget;
			}
		}

		if(this.host.updateTick % 40 == 0) {
			return super.getNewTarget(rangeX, rangeY, rangeZ);
		}

		return null;
	}
}