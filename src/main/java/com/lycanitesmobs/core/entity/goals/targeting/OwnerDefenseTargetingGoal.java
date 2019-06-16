package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.Targeting;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;

public class EntityAITargetingOwnerThreats extends TargetingGoal {
	// Properties:
	private EntityCreatureTameable tamedHost;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetingOwnerThreats(EntityCreatureTameable setHost) {
        super(setHost);
    	this.tamedHost = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetingOwnerThreats setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public EntityAITargetingOwnerThreats setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public EntityAITargetingOwnerThreats setCantSeeTimeMax(int setCantSeeTimeMax) {
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
		if(target instanceof EntityLiving) {
			LivingEntity targetLiving = (EntityLiving)target;
			if(!targetLiving.canAttackClass(PlayerEntity.class)) {
				return false;
			}
		}

		// Mod Interaction Check:
		if(!Targeting.isValidTarget(this.host, target)) {
        	return false;
		}

        // Threat Check:
        if(target instanceof IMob && !(target instanceof IEntityOwnable) && !(target instanceof EntityCreatureBase)) {
            return true;
        }
        else if(target instanceof EntityCreatureBase && ((EntityCreatureBase)target).isHostile() && !(target instanceof IGroupAnimal)) {
            return true;
        }
        else if(target instanceof LivingEntity && ((EntityLiving)target).getAttackTarget() == this.getOwner()) {
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
