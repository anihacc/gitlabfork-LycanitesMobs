package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.Targeting;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class DefendOwnerGoal extends TargetingGoal {
	// Properties:
	private TameableCreatureEntity tamedHost;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public DefendOwnerGoal(TameableCreatureEntity setHost) {
        super(setHost);
    	this.tamedHost = setHost;
		this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public DefendOwnerGoal setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public DefendOwnerGoal setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public DefendOwnerGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected LivingEntity getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(LivingEntity newTarget) { this.host.setAttackTarget(newTarget); }
    protected Entity getOwner() { return this.host.getOwner(); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(LivingEntity target) {
    	// Owner Check:
    	if(!this.tamedHost.isTamed())
    		return false;
    	
    	// Passive Check:
    	if(this.tamedHost.isPassive())
			return false;
    	
    	// Aggressive Check:
    	if(!this.host.isAggressive())
            return false;
    	
    	// Team Checks:
        if(this.host.isOnSameTeam(target)) {
            return false;
        }

        // LivingEntity Check:
		if(target instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)target;
			if(!mobEntity.canAttack(EntityType.PLAYER)) {
				return false;
			}
		}

		// Mod Interaction Check:
		if(!Targeting.isValidTarget(this.host, target)) {
        	return false;
		}

        // Threat Check:
        if(target instanceof IMob && !(target instanceof TameableEntity) && !(target instanceof BaseCreatureEntity)) {
            return true;
        }
        else if(target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).isHostileTo(this.tamedHost.getOwner()) && !(target instanceof IGroupAnimal)) {
            return true;
        }
        else if(target instanceof MobEntity && ((MobEntity)target).getAttackTarget() == this.getOwner()) {
            return true;
        }
        else if(target.getRevengeTarget() == this.getOwner()) {
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
    	if(!this.tamedHost.isTamed())
    		return false;
    	
    	// Passive Check:
    	if(this.tamedHost.isPassive())
			return false;
    	
    	// Aggressive Check:
    	if(!this.host.isAggressive())
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
