package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Chupacabra extends TameableCreatureEntity {

    public Chupacabra(EntityType<? extends Chupacabra> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(1.5D));
        this.goalSelector.addGoal(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("chaosorb").setFireRate(40).setVelocity(1.0F).setScale(0.5F)
                .setConditions(new GoalConditions().setRareVariantOnly(true)));
    }

    @Override
    public boolean shouldCreatureGroupFlee(LivingEntity target) {
        return false;
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
        	return false;

        if((target instanceof Animal || (target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).creatureInfo.isFarmable())) && target.getDimensions(Pose.STANDING).height >= 1F)
    		this.breed();

        float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
        this.heal(leeching);
    	
        return true;
    }

    public boolean canBeTempted() {
    	return this.isBaby();
    }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
        return false;
    }
}
