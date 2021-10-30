package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class Grigori extends TameableCreatureEntity implements Enemy {
    public Grigori(EntityType<? extends Grigori> entityType, Level world) {
        super(entityType, world);

		this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(2.0D).setLongMemory(false));

		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(Grell.class).setSightCheck(false));
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(this));
    }

	@Override
	public boolean canAttack(LivingEntity target) {
		if(target.getVehicle() instanceof Grell)
			return false;
		return super.canAttack(target);
	}

	@Override
	public boolean rollWanderChance() {
		return this.getRandom().nextDouble() <= 0.25D;
	}
	
	public boolean isFlying() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    @Override
    public boolean canBurn() { return false; }
}