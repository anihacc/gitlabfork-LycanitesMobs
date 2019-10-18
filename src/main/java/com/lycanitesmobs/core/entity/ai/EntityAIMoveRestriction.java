package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIMoveRestriction extends EntityAIBase {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private double speed = 1.0D;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIMoveRestriction(BaseCreatureEntity setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIMoveRestriction setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
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
    public boolean shouldContinueExecuting() {
        return !this.host.getNavigator().noPath();
    }
    
    
    // ==================================================
  	//                     Start
  	// ==================================================
    public void startExecuting() {
        this.host.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
