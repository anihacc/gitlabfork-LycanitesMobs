package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Reiver extends TameableCreatureEntity implements Enemy {
    
    public Reiver(EntityType<? extends Reiver> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(8.0F));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

    @Override
    public void attackRanged(Entity target, float range) {
    	this.fireProjectile("frostbolt", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
        super.attackRanged(target, range);
    }

    @Override
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("ooze")) return false;
        return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
