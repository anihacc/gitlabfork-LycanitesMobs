package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class PlaceBlockGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private double speed = 1.0D;
    private double range = 2.0D;
    private double maxDistance = 64.0D;
    private boolean replaceSolid = false;
    private boolean replaceLiquid = true;
    
    private BlockPos pos;
    public BlockState blockState;
    public int metadata = 0;
    
    private int repathTime = 0;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public PlaceBlockGoal(BaseCreatureEntity setHost) {
    	this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public PlaceBlockGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public PlaceBlockGoal setRange(double setRange) {
    	this.range = setRange;
    	return this;
    }
    public PlaceBlockGoal setMaxDistance(double setMax) {
    	this.maxDistance = setMax;
    	return this;
    }
    public PlaceBlockGoal setBlockPlacement(BlockState blockState, BlockPos pos) {
        this.blockState = blockState;
    	this.pos = pos;
    	return this;
    }
    public PlaceBlockGoal setMetadata(int setMetadata) {
    	this.metadata = setMetadata;
    	return this;
    }
    public PlaceBlockGoal setReplaceSolid(boolean bool) {
    	this.replaceSolid = bool;
    	return this;
    }
    public PlaceBlockGoal setReplaceLiquid(boolean bool) {
    	this.replaceLiquid = bool;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
	@Override
    public boolean shouldExecute() {
        if(this.blockState == null)
            return false;
        
    	if(!this.canPlaceBlock(this.pos)) {
            this.blockState = null;
    		return false;
    	}
    	
        return true;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
	@Override
    public void startExecuting() {
    	if(!host.useDirectNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.speed);
    	else
    		host.directNavigator.setTargetPosition(this.pos, this.speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.host.getNavigator().clearPath();
        this.host.directNavigator.clearTargetPosition(1.0D);
        this.blockState = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
	@Override
    public void tick() {
    	if(this.repathTime-- <= 0) {
    		this.repathTime = 20;
    		if(!host.useDirectNavigator())
        		this.host.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.speed);
        	else
        		host.directNavigator.setTargetPosition(this.pos, this.speed);
    	}
    	
        this.host.getLookController().setLookPosition(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 30.0F, 30.0F);
        
        // Place Block:
        if(this.host.getDistanceSq(new Vec3d(this.pos)) <= this.range * this.range) {
        	this.host.getEntityWorld().setBlockState(this.pos, this.blockState, 3);
            this.blockState = null;
            this.host.clearMovement();
        }
        
        // Cancel If Too Far:
        if(this.host.getDistanceSq(new Vec3d(this.pos)) >= this.maxDistance * this.maxDistance) {
            this.blockState = null;
            this.host.clearMovement();
        }
    }
    
    
    // ==================================================
   	//                  Can Place Block
   	// ==================================================
    public boolean canPlaceBlock(BlockPos pos) {
    	BlockState targetState = this.host.getEntityWorld().getBlockState(pos);
		if(targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA) {
			if(!this.replaceLiquid)
				return false;
		}
		else if(targetState.getMaterial() != Material.AIR && !this.replaceSolid)
			return false;
		if(!this.host.useDirectNavigator() && this.host.getNavigator() != null) {
			if(this.host.getNavigator().getPathToPos(pos) == null)
				return false;
		}
		return true;
    }
}
