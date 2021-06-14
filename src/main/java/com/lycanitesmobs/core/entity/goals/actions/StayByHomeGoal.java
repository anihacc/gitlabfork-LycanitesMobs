package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class StayByHomeGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private boolean enabled = true;
    private double speed = 1.0D;
    private double farSpeed = 1.5D;

    public StayByHomeGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
		this.setFlags(EnumSet.of(Flag.MOVE));
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
    public boolean canUse() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.hasHome() || this.host.getDistanceFromHome() <= 1.0F)
    		return false;
        if(this.host.isInWater() && !this.host.canBreatheUnderwater())
            return false;
        
        return true;
    }

	@Override
    public void start() {
        this.host.clearMovement();
		BlockPos homePos = this.host.getRestrictCenter();
		double speed = this.speed;
		if(this.host.getDistanceFromHome() > this.host.getHomeDistanceMax())
			speed = this.farSpeed;
		if(!host.useDirectNavigator())
			this.host.getNavigation().moveTo(homePos.getX(), homePos.getY(), homePos.getZ(), this.speed);
		else
			host.directNavigator.setTargetPosition(new BlockPos(homePos.getX(), homePos.getY(), homePos.getZ()), speed);
    }
}
