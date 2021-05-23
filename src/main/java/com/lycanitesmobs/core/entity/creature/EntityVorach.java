package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public class EntityVorach extends TameableCreatureEntity implements IMob {

	public EntityVorach(EntityType<? extends EntityBehemophet> entityType, World world) {
		super(entityType, world);
		this.attribute = CreatureAttribute.UNDEAD;
		this.hasAttackSound = true;
		this.spreadFire = true;
		this.setupMob();

		this.stepHeight = 1.0F;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
	}

	@Override
	public boolean attackMelee(Entity target, double damageScale) {
		if(!super.attackMelee(target, damageScale))
			return false;

		// Leech:
		float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
		this.heal(leeching);

		return true;
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
