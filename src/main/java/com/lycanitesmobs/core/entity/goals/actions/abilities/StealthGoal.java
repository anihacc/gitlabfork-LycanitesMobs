package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class StealthGoal extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;
	
	// Properties:
	private int stealthTimeMax = 20;
	private int stealthTimeMaxPrev = 20;
	private int stealthTime = 0;
	private int unstealthRate = 4;
	private boolean stealthMove = false;
	private boolean stealthAttack = false;
	
	private boolean unstealth = false;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
	public StealthGoal(BaseCreatureEntity setHost) {
		this.host = setHost;
		this.setMutexBits(0);
	}
	
	
    // ==================================================
 	//                    Properties
 	// ==================================================
	public StealthGoal setStealthTime(int time) {
		this.stealthTimeMax = time;
		return this;
	}
	public StealthGoal setUnstealthRate(int rate) {
		this.unstealthRate = rate;
		return this;
	}
	public StealthGoal setStealthMove(boolean flag) {
		this.stealthMove = flag;
		return this;
	}
	public StealthGoal setStealthAttack(boolean flag) {
		this.stealthAttack = flag;
		return this;
	}

	
    // ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
	public boolean shouldExecute() {
		this.unstealth = false;
		if(this.host.getLeashed()) this.unstealth = true;
		
		if(!this.stealthMove) {
			if(!this.host.useDirectNavigator() && !this.host.getNavigator().noPath())
				this.unstealth = true;
			if(this.host.useDirectNavigator() && !this.host.directNavigator.atTargetPosition())
				this.unstealth = true;
		}
		
		if(!this.stealthAttack && this.host.getAttackTarget() != null)
			this.unstealth = true;
		if(!this.host.canStealth())
			this.unstealth = true;
		
		return !this.unstealth;
	}

	
    // ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
	public boolean shouldContinueExecuting() {
		if(this.host.getLeashed()) this.unstealth = true;
		
		if(!this.stealthMove) {
			if(!this.host.useDirectNavigator() && !this.host.getNavigator().noPath())
				this.unstealth = true;
			if(this.host.useDirectNavigator() && !this.host.directNavigator.atTargetPosition())
				this.unstealth = true;
		}
		
		if(!this.stealthAttack && this.host.getAttackTarget() != null)
			this.unstealth = true;
		if(!this.host.canStealth())
			this.unstealth = true;
		
		if(this.unstealth && this.host.getStealth() <= 0)
			return false;
		
		if(this.stealthTimeMaxPrev != this.stealthTimeMax)
			return false;
		
		return true;
	}

	
    // ==================================================
 	//                 Start Executing
 	// ==================================================
	@Override
	public void startExecuting() {
		this.host.setStealth(0F);
		this.stealthTime = 0;
		this.stealthTimeMaxPrev = this.stealthTimeMax;
	}

	
    // ==================================================
 	//                  Reset Task
 	// ==================================================
	@Override
	public void resetTask() {
		this.host.setStealth(0F);
		this.stealthTime = 0;
		this.stealthTimeMaxPrev = this.stealthTimeMax;
	}

	
    // ==================================================
 	//                  Update Task
 	// ==================================================
	public void updateTask() {
		float nextStealth = (float)this.stealthTime / (float)this.stealthTimeMax;
		this.host.setStealth(nextStealth);
		
		if(!this.unstealth && this.stealthTime < this.stealthTimeMax)
			this.stealthTime++;
		else if(this.unstealth && this.stealthTime > 0)
			this.stealthTime -= this.unstealthRate;
		//this.stealthTime = Math.min(this.stealthTime, 1);
		//this.stealthTime = Math.max(this.stealthTime, 0);
	}
}
