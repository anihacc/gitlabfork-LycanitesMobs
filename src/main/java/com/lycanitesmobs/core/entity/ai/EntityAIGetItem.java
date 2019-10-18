package com.lycanitesmobs.core.entity.ai;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public class EntityAIGetItem extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;
	private EntityItem target;
	
	// Properties:
    private Predicate<EntityItem> targetSelector;
    private TargetSorterNearest targetSorter;
    private double distanceMax = 32.0D * 32.0D;
    double speed = 1.0D;
    private boolean checkSight = true;
    private int cantSeeTime = 0;
    protected int cantSeeTimeMax = 60;
    private int updateRate = 0;
    private int recheckTime = 0;
    public boolean tamedLooting = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAIGetItem(BaseCreatureEntity setHost) {
        super();
        this.setMutexBits(1);
        this.host = setHost;
        this.targetSelector = input -> true;
        this.targetSorter = new TargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIGetItem setDistanceMax(double setDouble) {
    	this.distanceMax = setDouble * setDouble;
    	return this;
    }

    public EntityAIGetItem setSpeed(double setDouble) {
    	this.speed = setDouble;
    	return this;
    }
    
    public EntityAIGetItem setCheckSight(boolean setBool) {
    	this.checkSight = setBool;
    	return this;
    }
    
    public EntityAIGetItem setTamedLooting(boolean bool) {
    	this.tamedLooting = bool;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.canPickupItems())
    		return false;

    	if(this.recheckTime++ < 40) {
    		return false;
		}
		this.recheckTime = 0;

    	if(!this.tamedLooting) {
    		if(this.host instanceof TameableCreatureEntity)
    			if(((TameableCreatureEntity)this.host).isTamed())
    				return false;
    	}
    	
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
        	heightDistance = this.distanceMax;
        List<EntityItem> possibleTargets = this.host.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, this.host.getEntityBoundingBox().grow(this.distanceMax, heightDistance, this.distanceMax), this.targetSelector);
        
        if(possibleTargets.isEmpty())
            return false;
        Collections.sort(possibleTargets, this.targetSorter);
        this.target = possibleTargets.get(0);
        
        return this.shouldContinueExecuting();
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
    	if(this.target == null)
            return false;
        if(!this.target.isEntityAlive())
            return false;
        
        double distance = this.host.getDistance(target);
        if(distance > this.distanceMax)
        	return false;
        
        if(this.checkSight)
            if(this.host.getEntitySenses().canSee(this.target))
                this.cantSeeTime = 0;
            else if(++this.cantSeeTime > this.cantSeeTimeMax)
                return false;
        
        return true;
    }
    
    
    // ==================================================
 	//                      Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.target = null;
        this.host.clearMovement();
    }
    
    
    // ==================================================
  	//                       Start
  	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 20;
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        	else
        		this.host.directNavigator.setTargetPosition(new BlockPos((int)this.target.posX, (int)this.target.posY, (int)this.target.posZ), this.speed);
        }
    }
}
