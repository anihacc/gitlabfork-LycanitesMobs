package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.MathHelper;

public class FaceTargetGoal extends EntityAIBase {
	private BaseCreatureEntity host;

	private EntityLivingBase target;

	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public FaceTargetGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	@Override
    public boolean shouldExecute() {
		if(!this.host.isEntityAlive()) {
			return false;
		}
		this.target = this.host.getAttackTarget();
		return this.target != null;
    }

	@Override
    public void updateTask() {
		this.host.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, this.host.getVerticalFaceSpeed());
    }
}
