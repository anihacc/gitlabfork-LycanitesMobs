package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public class EntityGrigori extends TameableCreatureEntity implements IMob {
    public EntityGrigori(EntityType<? extends EntityGrigori> entityType, World world) {
        super(entityType, world);
        
        // Setup:
		this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(2.0D).setLongMemory(false));

		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(EntityGrell.class).setSightCheck(false));
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(this));
    }

	@Override
	public boolean rollWanderChance() {
		return this.getRNG().nextDouble() <= 0.25D;
	}
	
	public boolean isFlying() { return true; }

    @Override
    public boolean canBurn() { return false; }
}
