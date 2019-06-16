package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MoveRestrictionGoal extends Goal {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public MoveRestrictionGoal(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public MoveRestrictionGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
        if(this.host.hasHome())
            return false;
        BlockPos chunkcoordinates = this.host.getHomePosition();
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, new Vec3d((double)chunkcoordinates.getX(), (double)chunkcoordinates.getY(), (double)chunkcoordinates.getZ()));
        if(vec3 == null)
            return false;
        
        this.movePosX = vec3.x;
        this.movePosY = vec3.y;
        this.movePosZ = vec3.z;
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    @Override
    public boolean shouldContinueExecuting() {
        return !this.host.getNavigator().noPath();
    }
    
    
    // ==================================================
  	//                     Start
  	// ==================================================
    @Override
    public void startExecuting() {
        this.host.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
