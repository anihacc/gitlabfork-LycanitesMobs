package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class Wisp extends TameableCreatureEntity {
	Wisp playPartner = null;

    public Wisp(EntityType<? extends Wisp> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;

        this.setupMob();

        this.maxUpStep = 1.0F;
		this.setAttackCooldownMax(this.getRangedCooldown());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
    }

	@Override
	public void tick() {
    	super.tick();
    	if(this.playPartner != null && !this.hasAttackTarget()) {
			this.lookAt(this.playPartner, 360, 360);
		}
	}

    @Override
    public void aiStep() {
        super.aiStep();

		if(!this.getCommandSenderWorld().isClientSide) {

			if(!this.isPetType("familiar")) {
				if (this.updateTick % 10 == 0) {
					List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 2);
					for (Object entityObj : aoeTargets) {
						LivingEntity target = (LivingEntity) entityObj;
						if(target == this) {
							continue;
						}
						if(target instanceof Wisp) {
							if (this.playPartner == null) {
								this.playPartner = (Wisp) target;
								if (((Wisp) target).playPartner == null) {
									((Wisp) target).playPartner = this;
								}
							}
							continue;
						}
						this.applyBuffs(target, 1, 1);
						this.applyDebuffs(target, 1, 1);
						if(target instanceof Zombie || target instanceof Skeleton) {
							target.setSecondsOnFire(1);
						}
						if(target instanceof BaseCreatureEntity) {
							BaseCreatureEntity targetCreature = (BaseCreatureEntity)target;
							if(targetCreature.daylightBurns()) {
								targetCreature.setSecondsOnFire(1);
							}
						}
					}
				}
			}

			if(this.playPartner == null) {
				this.setAttackCooldownMax(this.getRangedCooldown());
				if (this.updateTick % 40 == 0) {
					List aoeTargets = this.getNearbyEntities(Wisp.class, null, 30);
					for (Object entityObj : aoeTargets) {
						Wisp target = (Wisp)entityObj;
						if(target != this && this.getTarget() != target) {
							this.playPartner = target;
							if(target.playPartner == null) {
								target.playPartner = this;
							}
							break;
						}
					}
				}
			}
			else {
				if(!this.playPartner.isAlive() || this.getTarget() == this.playPartner || this.distanceTo(this.playPartner) >= 100) {
					this.playPartner = null;
				}
				else if(this.hasAttackTarget()) {
					this.setAttackCooldownMax(this.getRangedCooldown());
					if(this.getPlayerOwner() == this.playPartner.getPlayerOwner()) {
						this.playPartner.setTarget(this.getTarget());
					}
				}
				else {
					if(!this.isAttackOnCooldown() && !this.hasAttackTarget()) {
						if(this.updateTick % this.getRangedCooldown() == 0) {
							this.setAttackCooldownMax(160);
							this.playPartner.setAttackCooldownMax(160);
							this.playPartner.triggerAttackCooldown();
							this.attackRanged(this.playPartner, this.distanceTo(this.playPartner));
						}
					}
				}
			}
		}
    }

    @Override
	public boolean rollWanderChance() {
		if(this.playPartner == null) {
			return super.rollWanderChance();
		}
		if(this.isAttackOnCooldown()) {
			return false;
		}
		return this.getRandom().nextDouble() <= 0.002D;
	}

    @Override
    public void attackRanged(Entity target, float range) {
		this.fireProjectile("lightball", target, range, 0, new Vec3(0, 0, 0), 0.5f, 1f, 1F);
		super.attackRanged(target, range);
    }

    @Override
	public boolean canAttack(LivingEntity targetEntity) {
    	if(targetEntity == this.playPartner) {
    		return false;
		}
		if(targetEntity instanceof Wisp && this.getPlayerOwner() == ((Wisp)targetEntity).getPlayerOwner()) {
    		return false;
		}
    	return super.canAttack(targetEntity);
	}

    @Override
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }


	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}
}
