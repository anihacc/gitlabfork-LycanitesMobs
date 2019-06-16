package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookIdleGoal extends Goal {
    // Targets:
    private EntityCreatureBase host;

    // Properties:
    private int idleTime;
    private int idleTimeMin = 20;
    private int idleTimeRange = 20;
    private double lookX;
    private double lookZ;
    
    // ==================================================
   	//                    Constructor
   	// ==================================================
    public LookIdleGoal(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public LookIdleGoal setTimeMin(int setTimeMin) {
    	this.idleTimeMin = setTimeMin;
    	return this;
    }
    public LookIdleGoal setTimeRange(int setTimeRange) {
    	this.idleTimeRange = setTimeRange;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        return this.host.getRNG().nextFloat() < 0.02F;
    }
    
    
    // ==================================================
   	//                Continue Executing
   	// ==================================================
    public boolean shouldContinueExecuting() {
        return this.idleTime >= 0;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    public void startExecuting() {
        double d0 = (Math.PI * 2D) * this.host.getRNG().nextDouble();
        this.lookX = Math.cos(d0);
        this.lookZ = Math.sin(d0);
        this.idleTime = idleTimeMin + this.host.getRNG().nextInt(idleTimeRange);
    }
    
    
    // ==================================================
   	//                     Update
   	// ==================================================
    public void updateTask() {
        this.idleTime--;
        this.host.getLookHelper().setLookPosition(
        		this.host.posX + this.lookX,
        		this.host.posY + (double)this.host.getEyeHeight(),
        		this.host.posZ + this.lookZ, 10.0F,
        		(float)this.host.getVerticalFaceSpeed()
        		);
    }
}