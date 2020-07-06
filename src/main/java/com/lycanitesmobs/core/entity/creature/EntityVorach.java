package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityVorach extends TameableCreatureEntity implements IMob {

	public EntityVorach(EntityType<? extends EntityBehemoth> entityType, World world) {
		super(entityType, world);
		this.attribute = CreatureAttribute.UNDEAD;
		this.setupMob();
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
	}

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("hellfireball", target, range, 0, new Vector3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}

	@Override
	public boolean canClimb() { return true; }

	@Override
	public boolean canBurn() { return false; }

	@Override
	public float getFallResistance() {
		return 10;
	}
}
