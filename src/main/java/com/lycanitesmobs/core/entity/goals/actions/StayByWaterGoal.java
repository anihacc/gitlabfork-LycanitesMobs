package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class StayByWaterGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private double speed = 1.0D;
    private double strayDistance = 64.0D;
    
    private BlockPos waterPos;
    private boolean hasWaterPos = false;

    private int waterSearchRate = 0;
    private int updateRate = 0;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public StayByWaterGoal(BaseCreatureEntity setHost) {
    	this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public StayByWaterGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }

    public StayByWaterGoal setStrayDistance(double strayDistance) {
    	this.strayDistance = strayDistance;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(this.host.canBreatheAir())
    		return false;

    	// Set home when in water or lava (for lava creatures).
    	if(this.host.isInWater()) {
    		FluidState fluidState = this.host.getEntityWorld().getFluidState(this.host.getPosition());
    		if((!this.host.isLavaCreature && fluidState.isTagged(FluidTags.WATER)) ||
    			(this.host.isLavaCreature && fluidState.isTagged(FluidTags.LAVA))) {
	    		this.waterPos = this.host.getPosition();
	    		this.hasWaterPos = true;
	    		return false;
    		}
    	}
    	
    	// If we're not in water:
    	if(!this.host.isInWater()) {
    		// If we have a water position but it is no longer water/lava, clear the water position. It is up to the water searcher, wander AI and path weights for find a new water position.
    		if(this.hasWaterPos && !this.isValidWaterPosition(this.waterPos)) {
	    		this.hasWaterPos = false;
    		}
    		
    		// If we don't have a water position, search for one every 2 seconds, if we do, check if there is a close one every 5 seconds:
    		if(this.waterSearchRate-- <= 0) {
	    		if(!this.hasWaterPos)
	    			this.waterSearchRate = 40;
	    		else
	    			this.waterSearchRate = 100;
	    		
	    		// Search within a 64x8x64 block area and find the closest water/lava block:
	    		double closestDistance = 99999;
	    		if(this.hasWaterPos)
	    			closestDistance = this.getDistanceFromWater();
	    		int searchRangeX = 32;
	    		int searchRangeY = 8;
	    		int searchRangeZ = 32;
                BlockPos hostPos = this.host.getPosition();
	    		for(int searchX = hostPos.getX() - searchRangeX; searchX <= hostPos.getX() + searchRangeX; ++searchX) {
	    			for(int searchY = hostPos.getY() - searchRangeY; searchY <= hostPos.getY() + searchRangeY; ++searchY) {
	    				for(int searchZ = hostPos.getZ() - searchRangeZ; searchZ <= hostPos.getZ() + searchRangeZ; ++searchZ) {
	    					BlockPos searchPos = new BlockPos(searchX, searchY, searchZ);

	    					// If the block is closer than the last valid location...
	    					double searchDistance = this.host.getDistanceSq(Vector3d.copy(searchPos));
    		    			if(!this.hasWaterPos || searchDistance < closestDistance) {
		    					
    		    				// And it is a valid water position...
    		    				if(this.isValidWaterPosition(searchPos)) {
		    						
    		    					// If the host has a rounded width larger than 1 then check if it can fit in the target block...
    		    					boolean enoughSpace = Math.round(this.host.getSize(Pose.STANDING).width) <= 1;
		    						if(!enoughSpace) {
		    							enoughSpace = true;
			    						int neededSpace = Math.round(this.host.getSize(Pose.STANDING).width + 0.5F);
			    						for(int adjX = searchX - neededSpace; adjX <= searchX + neededSpace; ++adjX) {
			    							for(int adjZ = searchZ - neededSpace; adjZ <= searchZ + neededSpace; ++adjZ) {
			    								if(this.host.getEntityWorld().getBlockState(new BlockPos(adjX, searchY, adjZ)).getMaterial().isSolid()) {
			    									enoughSpace = false;
			    									break;
			    								}
				    						}
			    						}
		    						}
		    						
		    						// If all is good, then set it as the new water position!
		    						if(enoughSpace) {
		    							closestDistance = searchDistance;
		    		    				this.waterPos = searchPos;
                                        this.hasWaterPos = true;
		    						}
		    					}
		    		    	}
	    	    		}
		    		}
	    		}
    		}
    	}
    	
    	// If it's raining and the host isn't a lava creature, then there's no need to return to the water position, this shouldn't be set as a new water position though.
    	if(!this.host.isLavaCreature && this.host.waterContact() && this.getDistanceFromWater() <= this.strayDistance)
    		return false;
    	
    	// If the host has an attack target and plenty of air, then it is allowed to stray from the water position within the limit.
        if(this.host.hasAttackTarget() && this.host.getAir() > -100 && this.getDistanceFromWater() <= this.strayDistance)
        	return false;
    	
        // At this point the host should return to the water position, if there is one.
        return this.hasWaterPos;
    }

    public boolean isValidWaterPosition(BlockPos pos) {
        if(!this.host.canBreatheUnderwater())
            return false;
        if(!this.host.waterDamage()) {
            if(this.host.getEntityWorld().getBlockState(pos).getMaterial() == Material.WATER)
                return true;
        }
        if(this.host.isInvulnerableTo(DamageSource.ON_FIRE) || this.host.isLavaCreature) {
            if(this.host.getEntityWorld().getBlockState(pos).getMaterial() == Material.LAVA)
                return true;
        }
        return false;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
	@Override
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
	@Override
    public void tick() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 20;
	    	if(!host.useDirectNavigator()) {
	    		this.host.getNavigator().tryMoveToXYZ(this.waterPos.getX() + 0.5D, this.waterPos.getY(), this.waterPos.getZ() + 0.5D, this.speed);
	    	}
	    	else
	    		host.directNavigator.setTargetPosition(this.waterPos, this.speed);
    	}
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.host.clearMovement();
    }

	
	// ==================================================
 	//              Get Distance From Water
 	// ==================================================
    public double getDistanceFromWater() {
        if(!this.hasWaterPos)
            return 0;
    	return this.host.getDistanceSq(Vector3d.copy(this.waterPos));
    }
}
