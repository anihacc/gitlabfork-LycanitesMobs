package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class FaceTargetGoal extends Goal {
	private BaseCreatureEntity host;

	private LivingEntity target;

	/**
	 * Constrcutor
	 * @param setHost The creature using this goal.
	 */
	public FaceTargetGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
    }

	@Override
    public boolean shouldExecute() {
		if(!this.host.isAlive()) {
			return false;
		}
		this.target = this.host.getAttackTarget();
		return this.target != null;
    }

	@Override
    public void tick() {
		this.host.getLookController().setLookPositionWithEntity(this.target, 10.0F, this.host.getVerticalFaceSpeed());
    }
}
