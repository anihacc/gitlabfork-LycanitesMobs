package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class StayByHomeGoal extends EntityAIBase {
	// Targets:
    protected BaseCreatureEntity host;
    
    // Properties:
	protected boolean enabled = true;
	protected double speed = 1.0D;
	protected double farSpeed = 1.5D;

    public StayByHomeGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexBits(1);
    }

    public StayByHomeGoal setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    public StayByHomeGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    public StayByHomeGoal setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }

	@Override
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
		if(!this.host.hasHome() || this.host.getDistanceFromHome() <= 1.0F)
    		return false;
        if(!this.host.canBreatheUnderwater() && this.host.isInWater())
            return false;
        
        return true;
    }

	@Override
	public boolean shouldContinueExecuting() {
		if (!this.shouldExecute()) {
			return false;
		}
		if (!this.host.useDirectNavigator() && this.host.getNavigator().noPath()) {
			return false;
		}
		return true;
	}

	@Override
    public void startExecuting() {
		BlockPos homePos = this.host.getHomePosition();
		double speed = this.speed;
		if(this.host.getDistanceFromHome() > this.host.getHomeDistanceMax())
			speed = this.farSpeed;
		if(!host.useDirectNavigator())
			this.host.getNavigator().tryMoveToXYZ(homePos.getX(), homePos.getY(), homePos.getZ(), this.speed);
		else
			host.directNavigator.setTargetPosition(new BlockPos((int)homePos.getX(), (int)homePos.getY(), (int)homePos.getZ()), speed);
    }

	@Override
	public void resetTask() {
		if(!this.host.useDirectNavigator())
			this.host.getNavigator().clearPath();
		else
			this.host.directNavigator.clearTargetPosition(this.speed);
	}
}
