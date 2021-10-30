package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class Spectre extends TameableCreatureEntity implements Enemy, IGroupHeavy {

	protected int pullRange = 6;
	protected int pullEnergy = 0;
	protected int pullEnergyMax = 2 * 20;
	protected int pullEnergyRecharge = 0;
	protected int pullEnergyRechargeMax = 4 * 20;
	protected boolean pullRecharging = true;

    public Spectre(EntityType<? extends Spectre> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
    public void aiStep() {
        super.aiStep();

		if(!this.getCommandSenderWorld().isClientSide) {
			if(this.pullRecharging) {
				if(++this.pullEnergyRecharge >= this.pullEnergyRechargeMax) {
					this.pullRecharging = false;
					this.pullEnergy = this.pullEnergyMax;
					this.pullEnergyRecharge = 0;
				}
			}
			this.pullEnergy = Math.min(this.pullEnergy, this.pullEnergyMax);
			if(this.canPull()) {
				for (LivingEntity entity : this.getNearbyEntities(LivingEntity.class, null, this.pullRange)) {
					if (entity == this || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy || entity.hasEffect(ObjectManager.getEffect("weight")) || !this.canAttack(entity))
						continue;
					ServerPlayer player = null;
					if (entity instanceof ServerPlayer) {
						player = (ServerPlayer) entity;
						if (player.getAbilities().instabuild)
							continue;
					}
					double xDist = this.position().x() - entity.position().x();
					double zDist = this.position().z() - entity.position().z();
					double xzDist = Mth.sqrt((float) (xDist * xDist + zDist * zDist));
					double factor = 0.1D;
					double motionCap = 10;
					if(entity.getDeltaMovement().x() < motionCap && entity.getDeltaMovement().x() > -motionCap && entity.getDeltaMovement().z() < motionCap && entity.getDeltaMovement().z() > -motionCap) {
						entity.push(
								xDist / xzDist * factor + entity.getDeltaMovement().x() * factor,
								0,
								zDist / xzDist * factor + entity.getDeltaMovement().z() * factor
						);
					}
					if (player != null)
						player.connection.send(new ClientboundSetEntityMotionPacket(entity));
				}
				if(--this.pullEnergy <= 0) {
					this.pullRecharging = true;
					this.pullEnergyRecharge = 0;
				}
			}
		}

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

	public boolean extraAnimation01() {
		if(this.getCommandSenderWorld().isClientSide) {
			return super.extraAnimation01();
		}
		return this.canPull();
	}

	public boolean canPull() {
		if(this.getCommandSenderWorld().isClientSide) {
			return this.extraAnimation01();
		}

		return !this.pullRecharging && this.hasAttackTarget() && this.distanceTo(this.getTarget()) <= (this.pullRange * 3);
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
}