package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Wraith extends TameableCreatureEntity implements Enemy {

    protected int detonateTimer = -1;
    
    public Wraith(EntityType<? extends Wraith> entityType, Level world) {
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
    }
    
    @Override
    public void aiStep() {
        if(!this.getCommandSenderWorld().isClientSide) {
            if(this.detonateTimer == 0) {
                this.getCommandSenderWorld().explode(this, this.position().x(), this.position().y(), this.position().z(), 1, Explosion.BlockInteraction.BREAK);
                this.discard();
            }
            else if(this.detonateTimer > 0) {
                this.detonateTimer--;
                if(this.getCommandSenderWorld().getBlockState(this.blockPosition()).getMaterial().isSolid()) {
                    this.detonateTimer = 0;
                }
                else {
                    for (LivingEntity entity : this.getNearbyEntities(LivingEntity.class, null, 1)) {
                        if (this.getPlayerOwner() != null && entity == this.getPlayerOwner())
                            continue;
                        if (entity instanceof TameableCreatureEntity) {
                            TameableCreatureEntity entityCreature = (TameableCreatureEntity) entity;
                            if (entityCreature.getPlayerOwner() != null && entityCreature.getPlayerOwner() == this.getPlayerOwner())
                                continue;
                        }
                        this.detonateTimer = 0;
                        this.attackEntityAsMob(entity, 4);
                    }
                }
            }
        }

        if(this.getCommandSenderWorld().isClientSide && this.detonateTimer <= 5) {
			for (int i = 0; i < 2; ++i) {
				this.getCommandSenderWorld().addParticle(ParticleTypes.SMOKE, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
		}
        
        super.aiStep();
    }

	@Override
	public boolean rollWanderChance() {
		return this.getRandom().nextDouble() <= 0.25D;
	}

    public void chargeAttack() {
        this.leap(5, this.getXRot());
        this.detonateTimer = 10;
    }
	
    public boolean isFlying() { return true; }

    @Override
    public void die(DamageSource par1DamageSource) {
		if(!this.getCommandSenderWorld().isClientSide && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
			int explosionRadius = 1;
			if(this.subspecies != null)
				explosionRadius = 3;
			explosionRadius = Math.max(1, Math.round((float)explosionRadius * (float)this.sizeScale));
			this.getCommandSenderWorld().explode(this, this.position().x(), this.position().y(), this.position().z(), explosionRadius, Explosion.BlockInteraction.BREAK);
		}
        super.die(par1DamageSource);
    }
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }

    public float getBrightness() {
        return super.getBrightness();
    }

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains("DetonateTimer")) {
			this.detonateTimer = nbt.getInt("DetonateTimer");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		if(this.detonateTimer > -1) {
			nbt.putInt("DetonateTimer", this.detonateTimer);
		}
	}
}