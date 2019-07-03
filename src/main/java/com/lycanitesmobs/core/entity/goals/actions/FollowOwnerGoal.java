package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FollowOwnerGoal extends FollowGoal {
	// Targets:
	TameableCreatureEntity host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public FollowOwnerGoal(TameableCreatureEntity setHost) {
    	super(setHost);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public FollowOwnerGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public FollowOwnerGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public FollowOwnerGoal setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public FollowOwnerGoal setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public Entity getTarget() {
    	return this.host.getOwner();
    }

	@Override
	public void setTarget(Entity entity) {
    	// Do nothing here.
	}


	// ==================================================
	//                  Should Execute
	// ==================================================
	@Override
	public boolean shouldExecute() {
		Entity target = this.getTarget();
		if(target == null)
			return false;
		if(!target.isAlive())
			return false;
		if(this.host.isSitting() || !this.host.isFollowing())
			return false;

		// Start straying when within the stray radius and the target.
		double distance = this.host.getDistance(target);
		if(distance <= this.strayDistance && this.strayDistance != 0) {
			this.host.clearMovement();
			return false;
		}

		return true;
	}


	// ==================================================
	//                Continue Executing
	// ==================================================
	@Override
	public boolean shouldContinueExecuting() {
    	return this.shouldExecute();
	}
    
	
	// ==================================================
 	//                      Update
 	// ==================================================
	@Override
    public void tick() {
		if(this.host.getDistance(this.getTarget()) >= this.lostDistance) {
			this.teleportToOwner();
		}
    	super.tick();
    }
    
    // ========== Teleport to Owner ==========
    public void teleportToOwner() {
    	if(this.getTarget() != null) {
			if(!this.host.canBreatheAir() && ((!this.host.isLavaCreature && !this.getTarget().isInWater()) || (this.host.isLavaCreature && !this.getTarget().isInLava()))) {
				return;
			}
			if(!this.host.canBreatheUnderwater() && this.getTarget().isInWater()) {
				return;
			}

    		World world = this.getTarget().getEntityWorld();
	    	int i = MathHelper.floor(this.getTarget().posX) - 2;
	        int j = MathHelper.floor(this.getTarget().getBoundingBox().minY);
	        int k = MathHelper.floor(this.getTarget().posZ) - 2;

            if(this.host.isFlying() || this.getTarget().isInWater()) {
                this.host.setLocationAndAngles(i, j + 1, k, this.host.rotationYaw, this.host.rotationPitch);
                this.host.clearMovement();
                return;
            }
	
	        for(int x = -2; x <= 2; ++x) {
	            for(int z = -2; z <= 2; ++z) {
	                if(this.canTeleportTo(this.getTarget().getPosition().add(x, 0, z))) {
                        this.host.setLocationAndAngles((double)((float)(i + x) + 0.5F), (double)j, (double)((float)(k + z) + 0.5F), this.host.rotationYaw, this.host.rotationPitch);
	                    this.host.clearMovement();
	                    return;
	                }
	            }
	        }
    	}
    }

	protected boolean canTeleportTo(BlockPos blockPos) {
		BlockState blockState = this.host.getEntityWorld().getBlockState(blockPos.down());
		return blockState.canEntitySpawn(this.host.getEntityWorld(), blockPos.down(), this.host.getType()) && this.host.getEntityWorld().isAirBlock(blockPos) && this.host.getEntityWorld().isAirBlock(blockPos.up());
	}
    
    //TODO Wait on the ChunkUnload Chunk event, if this mob is not sitting and the unloading chunk is what it's in, then teleport this mob to it's owner away from the unloaded chunk, unless it's player has disconnected.
}
