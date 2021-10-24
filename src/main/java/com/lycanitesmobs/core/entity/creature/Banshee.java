package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class Banshee extends TameableCreatureEntity implements Enemy {

	private int strafeTime = 60;

    public Banshee(EntityType<? extends Banshee> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

		this.noPhysics = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide && this.hasAttackTarget()) {
	        if(this.strafeTime-- <= 0) {
	        	this.strafeTime = 60 + this.getRandom().nextInt(40);
				this.strafe(this.getRandom().nextBoolean() ? -1F : 1F, 0D);
	        }
        }

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.WITCH, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

    @Override
    public boolean isFlying() { return true; }

	@Override
	public boolean useDirectNavigator() {
		return true;
	}

	@Override
	public boolean hasLineOfSight(Entity target) {
		return true;
	}

    public boolean petControlsEnabled() { return true; }
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isVulnerableTo(type, source, damage);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
