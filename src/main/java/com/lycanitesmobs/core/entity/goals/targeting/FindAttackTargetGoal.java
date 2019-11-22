package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityArgus;
import com.lycanitesmobs.core.entity.creature.EntityBanshee;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FindAttackTargetGoal extends TargetingGoal {
	// Targets:
    private List<Class<? extends Entity>> targetClasses = new ArrayList<>();

    // Properties:
	protected boolean targetPlayers;
	private boolean requirePack = false;
    protected boolean tameTargeting = false;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public FindAttackTargetGoal(BaseCreatureEntity setHost) {
        super(setHost);
        this.setMutexBits(1);
    }


    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FindAttackTargetGoal setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }

	public FindAttackTargetGoal addTargets(Class<? extends Entity>... targets) {
		this.targetClasses.addAll(Arrays.asList(targets));
		for(Class<? extends Entity> targetType : targets) {
			this.host.setHostileTo(targetType);
			if(targetType.isAssignableFrom(EntityPlayer.class))
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
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
    	// Target Class Check:
		if(!this.targetClasses.isEmpty()) {
			boolean isTargetClass = false;
			for (Class<? extends Entity> targetClass : this.targetClasses) {
				if (targetClass.isAssignableFrom(target.getClass())) {
					isTargetClass = true;
					break;
				}
			}
			if(!isTargetClass) {
				return false;
			}
		}

		// Tamed Targeting Check:
		if(!this.tameTargeting && this.host.isTamed()) {
			return false;
		}
    	
    	// Type Check:
    	if(!this.host.canAttackClass(target.getClass())) {
			return false;
		}

        // Entity Check:
		if(!this.host.canAttackEntity(target)) {
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
			if (this.host.updateTick % 5 != 0) {
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
        double heightDistance = 4.0D + this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();

        return this.target != null && this.host.rollAttackTargetChance(this.target);
    }

    @Override
	public boolean shouldStopTargeting(EntityLivingBase target) {
		return !this.isValidTarget(target);
	}


	// ==================================================
	//                  Get New Target
	// ==================================================
	@Override
	public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
		// Faster Player Targeting:
		if(this.targetPlayers) {
			EntityLivingBase newTarget = null;
			try {
				List<? extends EntityPlayer> players = this.host.getEntityWorld().getPlayers(EntityPlayer.class, this.targetSelector);
				if (players.isEmpty())
					return null;
				List<EntityPlayer> possibleTargets = new ArrayList<>();
				for(EntityPlayer player : players) {
					if(this.isValidTarget(player)) {
						possibleTargets.add(player);
					}
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

			// Return player target first always.
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
