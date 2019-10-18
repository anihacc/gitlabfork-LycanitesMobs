package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderGoal extends EntityAIBase {
	// Targets:
	private BaseCreatureEntity host;

	// Properties:
	private double speed = 1.0D;

	private double xPosition;
	private double yPosition;
	private double zPosition;

	// ==================================================
	//                     Constructor
	// ==================================================
	public WanderGoal(BaseCreatureEntity setHost) {
		this.host = setHost;
		this.setMutexBits(1);
	}


	// ==================================================
	//                  Set Properties
	// ==================================================
	public WanderGoal setSpeed(double setSpeed) {
		this.speed = setSpeed;
		return this;
	}


	// ==================================================
	//                  Should Execute
	// ==================================================
	@Override
	public boolean shouldExecute() {
		if (this.host.hasAttackTarget())
			return false;
		if (this.host.getAge() >= 100)
			return false;
		else if (!this.host.rollWanderChance())
			return false;
		else {
			Vec3d newTarget = RandomPositionGenerator.findRandomTarget(this.host, 10, 7, this.host.getFlyingHeight());
			if (newTarget == null)
				return false;
			else {
				// Random Position:
				BlockPos wanderPosition = this.host.getWanderPosition(new BlockPos((int) newTarget.x, (int) newTarget.y, (int) newTarget.z));
				this.xPosition = wanderPosition.getX();
				this.yPosition = wanderPosition.getY();
				this.zPosition = wanderPosition.getZ();
				return true;
			}
		}
	}


	// ==================================================
	//                Continue Executing
	// ==================================================
	@Override
	public boolean shouldContinueExecuting() {
		if (!this.host.useDirectNavigator()) {
			if (this.host.getNavigator().noPath()) {
				return false;
			}
			else if (this.host.getDistanceSq(this.xPosition, this.yPosition, this.zPosition) < 4) {
				this.host.getNavigator().clearPath();
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return !this.host.directNavigator.atTargetPosition() && this.host.directNavigator.isTargetPositionValid();
		}
		//return this.host.getRNG().nextInt(100) != 0 && !this.host.directNavigator.atTargetPosition() && this.host.directNavigator.isTargetPositionValid();
	}


	// ==================================================
	//                     Start
	// ==================================================
	@Override
	public void startExecuting() {
		if (!host.useDirectNavigator()) {
			this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
		}
		else
			this.host.directNavigator.setTargetPosition(new BlockPos((int) this.xPosition, (int) this.yPosition, (int) this.zPosition), this.speed);
	}
}
