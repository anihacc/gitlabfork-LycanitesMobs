package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import java.util.Iterator;
import java.util.List;

public class RevengeRiderGoal extends FindAttackTargetGoal {
	
	// Targets:
	private TameableCreatureEntity host;
	
	// Properties:
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public RevengeRiderGoal(TameableCreatureEntity setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public RevengeRiderGoal setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public RevengeRiderGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public RevengeRiderGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public RevengeRiderGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.hasRiderTarget())
			return false;
    	if(this.host.getRider() == null)
    		return false;
        int i = this.getRiderRevengeTime();
		if(i == this.revengeTime)
        	return false;
        if(!this.isEntityTargetable(this.getRiderRevengeTarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
		this.target = this.getRiderRevengeTarget();
		this.revengeTime = this.getRiderRevengeTime();

        try {
            if (this.callForHelp) {
                double d0 = this.getTargetDistance();
                List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().grow(d0, 4.0D, d0), (Predicate<Entity>) input -> input instanceof EntityLiving);
                Iterator possibleAllies = allies.iterator();

                while (possibleAllies.hasNext()) {
                    BaseCreatureEntity possibleAlly = (BaseCreatureEntity) possibleAllies.next();
                    if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target))
                        possibleAlly.setAttackTarget(this.target);
                }
            }
        }
        catch(Exception e) {
            LycanitesMobs.logWarning("", "An exception occurred when selecting help targets in rider revenge, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }

        super.startExecuting();
    }


	// ==================================================
	//                    Rider Revenge
	// ==================================================
    public EntityLivingBase getRiderRevengeTarget() {
		EntityLivingBase revengeTarget = this.host.getRider().getRevengeTarget();
		if(revengeTarget == null) {
			ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.host.getRider());
			if(extendedEntity != null) {
				revengeTarget = extendedEntity.lastAttackedEntity;
			}
		}
		return revengeTarget;
	}

	public int getRiderRevengeTime() {
		int revengeTime = this.host.getRider().getRevengeTimer();
		if(revengeTime == 0) {
			ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.host.getRider());
			if(extendedEntity != null) {
				revengeTime = extendedEntity.lastAttackedTime;
			}
		}
		return revengeTime;
	}
}
