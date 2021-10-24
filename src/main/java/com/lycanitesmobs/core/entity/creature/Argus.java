package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Argus extends TameableCreatureEntity implements Enemy, IFusable {

	private int teleportTime = 60;

    public Argus(EntityType<? extends Argus> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.maxUpStep = 1.0F;

        this.setAttackCooldownMax(40);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true).setMaxChaseDistanceSq(5.0F));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        /*/ Random Target Teleporting:
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
			if(this.teleportTime-- <= 0) {
				this.teleportTime = 20 + this.getRNG().nextInt(20);
				BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().getSize(Pose.STANDING).width - 1D, 0);
				if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
			}
		}*/

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("chaosorb", target, range, 0, new Vec3(0, 0, 0), 0.6f, 1f, 1F);
		super.attackRanged(target, range);
	}

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }

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

	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof Cinder) {
			return CreatureManager.getInstance().getEntityType("grue");
		}
		if(fusable instanceof Jengu) {
			return CreatureManager.getInstance().getEntityType("eechetik");
		}
		if(fusable instanceof Geonach) {
			return CreatureManager.getInstance().getEntityType("tremor");
		}
		if(fusable instanceof Zephyr) {
			return CreatureManager.getInstance().getEntityType("wraith");
		}
		if(fusable instanceof Aegis) {
			return CreatureManager.getInstance().getEntityType("spectre");
		}
		return null;
	}
}
