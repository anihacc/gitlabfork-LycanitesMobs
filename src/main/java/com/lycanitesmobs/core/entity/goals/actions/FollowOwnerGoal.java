package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FollowOwnerGoal extends FollowGoal {
	// Targets:
	EntityCreatureTameable host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public FollowOwnerGoal(EntityCreatureTameable setHost) {
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
    	if(!this.host.isFollowing())
    		return null;
    	return this.host.getOwner();
    }

	@Override
	public void setTarget(Entity entity) {
    	// Do nothing here.
	}
    
	
	// ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void updateTask() {
    	if(!this.host.isSitting()) {
    		if(this.host.getDistance(this.getTarget()) >= this.lostDistance) {
				this.teleportToOwner();
			}
    	}
    	super.updateTask();
    }
    
    // ========== Teleport to Owner ==========
    public void teleportToOwner() {
    	if(this.getTarget() != null) {
			if(!this.host.canBreatheAboveWater() && ((!this.host.isLavaCreature && !this.getTarget().isInWater()) || (this.host.isLavaCreature && !this.getTarget().isInLava()))) {
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
	
	        for(int x = 0; x <= 4; ++x) {
	            for(int z = 0; z <= 4; ++z) {
	                if(this.canTeleportTo(new BlockPos(x, this.getTarget().getPosition().getY(), z))) {
                        this.host.setLocationAndAngles((double)((float)(i + x) + 0.5F), (double)j, (double)((float)(k + z) + 0.5F), this.host.rotationYaw, this.host.rotationPitch);
	                    this.host.clearMovement();
	                    return;
	                }
	            }
	        }
    	}
    }

	protected boolean canTeleportTo(BlockPos p_220707_1_) {
		BlockState lvt_2_1_ = this.host.getEntityWorld().getBlockState(p_220707_1_);
		return lvt_2_1_.canEntitySpawn(this.host.getEntityWorld(), p_220707_1_, this.host.getType()) && this.host.getEntityWorld().isAirBlock(p_220707_1_.up()) && this.host.getEntityWorld().isAirBlock(p_220707_1_.up(2));
	}
    
    //TODO Wait on the ChunkUnload Chunk event, if this mob is not sitting and the unloading chunk is what it's in, then teleport this mob to it's owner away from the unloaded chunk, unless it's player has disconnected.
}
