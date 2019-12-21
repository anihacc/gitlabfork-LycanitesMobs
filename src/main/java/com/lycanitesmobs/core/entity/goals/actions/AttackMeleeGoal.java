package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GrowGoal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AttackMeleeGoal extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;
    private EntityLivingBase attackTarget;
    private Path pathToTarget;
    
    // Properties:
    private double speed = 1.0D;
    private Class targetClass;
    private boolean longMemory = true;
    private int attackTime;
    private double attackRange = 0.5D;
    private float maxChaseDistance = 1024F;
    private double damageScale = 1.0D;
    public boolean enabled = true;
	protected int phase = -1;

    // Pathing:
	private int failedPathFindingPenalty;
	private int failedPathFindingPenaltyMax = 0;
    private int repathTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public AttackMeleeGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public AttackMeleeGoal setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public AttackMeleeGoal setDamageScale(double scale) {
    	this.damageScale = scale;
    	return this;
    }
    public AttackMeleeGoal setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public AttackMeleeGoal setLongMemory(boolean setMemory) {
    	this.longMemory = setMemory;
    	return this;
    }
    public AttackMeleeGoal setRange(double range) {
    	this.attackRange = range;
    	return this;
    }
    public AttackMeleeGoal setMaxChaseDistanceSq(float distance) {
    	this.maxChaseDistance = distance * distance;
    	return this;
    }
	public AttackMeleeGoal setMaxChaseDistance(float distance) {
		this.maxChaseDistance = distance;
		return this;
	}
    public AttackMeleeGoal setMissRate(int rate) {
    	this.failedPathFindingPenaltyMax = rate;
    	return this;
    }
    public AttackMeleeGoal setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }

	/**
	 * Sets the battle phase to restrict this goal to.
	 * @param phase The phase to restrict to, if below 0 phases are ignored.
	 * @return This goal for chaining.
	 */
	public AttackMeleeGoal setPhase(int phase) {
		this.phase = phase;
		return this;
	}
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;

    	if(this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
    		return false;
		}

		// With Pickup:
		if(this.host.hasPickupEntity() && !this.host.canAttackWithPickup()) {
			return false;
		}

		this.attackTarget = this.host.getAttackTarget();
        if(this.attackTarget == null)
            return false;
        if(!this.attackTarget.isEntityAlive())
            return false;
        if(this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(this.attackTarget.getClass()))
            return false;

        if(--this.repathTime <= 0) {
            // Set Path:
        	if(!this.host.useDirectNavigator()) {
				if(this.host.isCurrentlyFlying()) {
					this.pathToTarget = this.host.getNavigator().getPathToXYZ(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset(), this.attackTarget.posZ);
				}
				else {
					this.pathToTarget = this.host.getNavigator().getPathToEntityLiving(this.attackTarget);
				}
	            this.repathTime = 4 + this.host.getRNG().nextInt(7);

	            return this.pathToTarget != null;
        	}

            // Set Direct Target:
        	else {
				return this.host.directNavigator.setTargetPosition(new BlockPos((int) attackTarget.posX, (int) attackTarget.posY + this.host.getFlightOffset(), (int) attackTarget.posZ), this.speed);
			}
        }
        return true;
    }
	
    
	// ==================================================
 	//                  Continue Executing
 	// ==================================================
	@Override
    public boolean shouldContinueExecuting() {
    	if(!this.enabled)
    		return false;
		if(this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
			return false;
		}
        this.attackTarget = this.host.getAttackTarget();
        if(this.attackTarget == null)
        	return false;
		if(!this.host.isEntityAlive() || !this.attackTarget.isEntityAlive()) {
			return false;
		}
        if(this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(!this.longMemory)
        	if(!this.host.useDirectNavigator() && this.host.getNavigator().noPath())
        		return false;
        	else if(this.host.useDirectNavigator() && (this.host.directNavigator.atTargetPosition() || !this.host.directNavigator.isTargetPositionValid()))
        		return false;
        return this.host.positionNearHome(MathHelper.floor(attackTarget.posX), MathHelper.floor(attackTarget.posY), MathHelper.floor(attackTarget.posZ));
    }
	
    
	// ==================================================
 	//                   Start Executing
 	// ==================================================
	@Override
    public void startExecuting() {
    	if(!this.host.useDirectNavigator()) {
			this.host.getNavigator().setPath(this.pathToTarget, this.speed);
		}
    	else if(attackTarget != null) {
			this.host.directNavigator.setTargetPosition(new BlockPos((int) attackTarget.posX, (int) (attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset()), (int) attackTarget.posZ), speed);
		}
        this.repathTime = 0;
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
	@Override
    public void resetTask() {
        this.host.getNavigator().clearPath();
        this.host.directNavigator.clearTargetPosition(1.0D);
        this.attackTarget = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
	@Override
    public void updateTask() {
        this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);

		// Path To Target:
		if(this.longMemory || this.host.getEntitySenses().canSee(this.attackTarget)) {
			if(!this.host.useDirectNavigator() && --this.repathTime <= 0) {
				this.repathTime = this.failedPathFindingPenalty + 4 + this.host.getRNG().nextInt(7);
				if(this.host.isCurrentlyFlying()) {
					this.host.getNavigator().tryMoveToXYZ(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset(), this.attackTarget.posZ, this.speed);
				}
				else {
					this.host.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.speed);
				}
				if(this.host.getNavigator().getPath() != null) {
	                PathPoint finalPathPoint = this.host.getNavigator().getPath().getFinalPathPoint();
	                if(finalPathPoint != null && this.attackTarget.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
						this.failedPathFindingPenalty = 0;
					}
	                else {
						this.failedPathFindingPenalty += this.failedPathFindingPenaltyMax;
					}
	            }
	            else {
					this.failedPathFindingPenalty += this.failedPathFindingPenaltyMax;
				}
        	}
        	else if(this.host.useDirectNavigator()) {
        		this.host.directNavigator.setTargetPosition(new BlockPos((int) this.attackTarget.posX, (int) (this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset()), (int) this.attackTarget.posZ), speed);
        	}
        }
        
        // Damage Target:
		LycanitesMobs.logDebug("Attack", "Attack range: " + this.host.getMeleeAttackRange(this.attackTarget, this.attackRange) + "/" + this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ));
        if(this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) <= this.host.getMeleeAttackRange(this.attackTarget, this.attackRange)) {
            if(--this.attackTime <= 0) {
                this.attackTime = this.host.getMeleeCooldown();
                if(!this.host.getHeldItemMainhand().isEmpty())
                    this.host.swingArm(EnumHand.MAIN_HAND);
                this.host.attackMelee(this.attackTarget, this.damageScale);
            }

            // Move helper won't change the Yaw if the target is already close by
            double d0 = this.host.posX - this.attackTarget.posX;
            double d1 = this.host.posZ - this.attackTarget.posZ;
            float f = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) + 90.0F;
            f = MathHelper.wrapDegrees(f - this.host.rotationYaw);
            if(f < -30f) f = -30f;
            if(f > 30f) f = 30f;
            this.host.rotationYaw = this.host.rotationYaw + f;
        }
    }
}
