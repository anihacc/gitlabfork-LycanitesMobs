package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class AttackRangedGoal extends Goal {
    // Targets:
	private final EntityCreatureBase host;
    private LivingEntity attackTarget;

    // Properties
    private int attackTime;

    private int attackStamina = 0;
    private int attackStaminaMax = 0;
    public boolean attackOnCooldown = false;
    private int staminaRecoverRate = 1;
    private int staminaDrainRate = 1;
    
    private double speed = 1.0D;
    private int chaseTime;
    private int chaseTimeMax = -1; // Average of 20
    private float range = 6.0F;
    private float attackDistance = 5.0F;
    private float minChaseDistance = 3.0F;
    private float flyingHeight = 2.0F;
    private boolean longMemory = true;
    private boolean checkSight = true;
    private boolean mountedAttacking = true;
    public boolean enabled = true;

	// Pathing:
	private int repathTimeMax = 20;
	private int repathTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public AttackRangedGoal(EntityCreatureBase setHost) {
    	this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AttackRangedGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public AttackRangedGoal setLongMemory(boolean setLongMemory) {
    	this.longMemory = setLongMemory;
    	return this;
    }
    public AttackRangedGoal setCheckSight(boolean setCheckSight) {
    	this.checkSight = setCheckSight;
    	return this;
    }
    
    // ========== Stamina ==========
    public AttackRangedGoal setStaminaTime(int setInt) {
    	this.attackStaminaMax = setInt;
    	this.attackStamina = this.attackStaminaMax;
    	return this;
    }
    public AttackRangedGoal setStaminaRecoverRate(int rate) {
    	this.staminaRecoverRate = rate;
    	return this;
    }
    public AttackRangedGoal setStaminaDrainRate(int rate) {
    	this.staminaDrainRate = rate;
    	return this;
    }
    
    public AttackRangedGoal setRange(float setRange) {
    	this.range = setRange;
    	this.attackDistance = setRange;
    	return this;
    }
    public AttackRangedGoal setMinChaseDistance(float setMinDist) {
    	this.minChaseDistance = setMinDist;
    	return this;
    }
    public AttackRangedGoal setChaseTime(int setChaseTime) {
    	this.chaseTimeMax = setChaseTime;
    	return this;
    }
    public AttackRangedGoal setFlyingHeight(float setFlyingHeight) {
    	this.flyingHeight = setFlyingHeight;
    	return this;
    }
    public AttackRangedGoal setMountedAttacking(boolean bool) {
        this.mountedAttacking = bool;
        return this;
    }
    public AttackRangedGoal setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	// Attack Stamina/Cooldown Recovery:
        if(this.attackStaminaMax > 0) {
        	if(this.attackOnCooldown) {
        		this.attackStamina += this.staminaRecoverRate;
        		if(this.attackStamina >= this.attackStaminaMax)
        			this.attackOnCooldown = false;
        	}
        }
        
        // Should Execute:
    	if(!this.enabled)
    		return false;
        if(!this.mountedAttacking && this.host instanceof EntityCreatureRideable) {
            EntityCreatureRideable rideableHost = (EntityCreatureRideable)this.host;
            if(rideableHost.getControllingPassenger() instanceof PlayerEntity)
                return false;
        }
        LivingEntity possibleAttackTarget = this.host.getAttackTarget();
        if(possibleAttackTarget == null)
            return false;
        if(!possibleAttackTarget.isAlive())
            return false;
        this.attackTarget = possibleAttackTarget;
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean shouldContinueExecuting() {
    	if(!this.longMemory)
	    	if(!this.host.useDirectNavigator() && !this.host.getNavigator().noPath())
                return this.shouldExecute();
	    	else if(this.host.useDirectNavigator() && this.host.directNavigator.targetPosition == null)
                return this.shouldExecute();

    	// Should Execute:
    	if(!this.enabled)
    		return false;
        LivingEntity possibleAttackTarget = this.host.getAttackTarget();
        if(possibleAttackTarget == null)
            return false;
        if(!possibleAttackTarget.isAlive())
            return false;
        this.attackTarget = possibleAttackTarget;
        return true;
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
    public void resetTask() {
        this.attackTarget = null;
        this.chaseTime = 0;
        this.attackTime = -1;
    }
    
    
    // ==================================================
  	//                   Update Task
  	// ==================================================
    public void updateTask() {
    	boolean fixated = this.host.hasFixateTarget() && this.host.getFixateTarget() == this.attackTarget;
        double distance = this.host.getDistance(this.attackTarget);
        boolean hasSight = fixated || this.host.getEntitySenses().canSee(this.attackTarget);
        float flyingHeightOffset = this.flyingHeight;
        
        if(hasSight && this.chaseTimeMax >= 0 && !fixated)
            ++this.chaseTime;
        else
            this.chaseTime = 0;
        
        if(!hasSight)
        	flyingHeightOffset = 0;

        // If within min range or chase timed out:
        if(distance <= this.minChaseDistance || (this.chaseTimeMax >= 0 && distance <= (double)this.attackDistance && this.chaseTime >= this.chaseTimeMax)) {
            if(!this.host.useDirectNavigator())
                this.host.getNavigator().clearPath();
            else
                this.host.directNavigator.clearTargetPosition(1.0D);
        }
        else if(--this.repathTime <= 0) {
        	this.repathTime = this.repathTimeMax;
            BlockPos targetPosition = this.attackTarget.getPosition();
            if(this.host.isFlying())
                targetPosition = targetPosition.add(0, flyingHeightOffset, 0);
            if(!this.host.useDirectNavigator())
                this.host.getNavigator().tryMoveToXYZ(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), this.speed);
            else
                this.host.directNavigator.setTargetPosition(targetPosition, this.speed);
        }

        this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        float rangeFactor;
        
        // Attack Stamina/Cooldown:
        if(this.attackStaminaMax > 0) {
        	if(!this.attackOnCooldown) {
        		this.attackStamina -= this.staminaDrainRate;
        		if(this.attackStamina <= 0)
        			this.attackOnCooldown = true;
        	}
        	else {
        		this.attackStamina += this.staminaRecoverRate;
        		if(this.attackStamina >= this.attackStaminaMax)
        			this.attackOnCooldown = false;
        	}
        }
        else if(this.attackOnCooldown)
        	this.attackOnCooldown = false;
        
        // Fire Projectile:
        if(!this.attackOnCooldown) {
	        if(--this.attackTime == 0) {
	            if(distance > (double)this.attackDistance || (this.checkSight && !hasSight))
	                return;
	
	            rangeFactor = (float)distance / this.range;
	            float outerRangeFactor = rangeFactor; // Passed to the attack, clamps targets within 10% closeness.
	            if(rangeFactor < 0.1F)
	            	outerRangeFactor = 0.1F;
	            if(outerRangeFactor > 1.0F)
	            	outerRangeFactor = 1.0F;
	
	            this.host.attackRanged(this.attackTarget, outerRangeFactor);
	            this.attackTime = this.host.getRangedCooldown();
	        }
	        else if(this.attackTime < 0) {
				this.attackTime = this.host.getRangedCooldown();
	        }
        }
    }
}