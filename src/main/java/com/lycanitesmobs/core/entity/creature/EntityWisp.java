package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupLight;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import com.lycanitesmobs.core.entity.projectile.EntityLightBall;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class EntityWisp extends EntityCreatureTameable implements IGroupLight {
	EntityWisp playPartner = null;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWisp(EntityType<? extends EntityWisp> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
		this.setAttackCooldownMax(this.getRangedCooldown());
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
		this.goalSelector.addGoal(2, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
		this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this).setPauseRate(600));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
		this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
		this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(IGroupShadow.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
	@Override
	public void tick() {
    	super.tick();
    	if(this.playPartner != null && !this.hasAttackTarget()) {
			this.faceEntity(this.playPartner, 360, 360);
		}
	}

    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

		if(!this.getEntityWorld().isRemote) {

			// Light Aura:
			if(!this.isPetType("familiar")) {
				if (this.updateTick % 10 == 0) {
					List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 2);
					for (Object entityObj : aoeTargets) {
						LivingEntity target = (LivingEntity) entityObj;
						if(target == this) {
							continue;
						}
						if(target instanceof EntityWisp) {
							if (this.playPartner == null) {
								this.playPartner = (EntityWisp) target;
								if (((EntityWisp) target).playPartner == null) {
									((EntityWisp) target).playPartner = this;
								}
							}
							continue;
						}
						this.applyBuffs(target, 1, 1);
						this.applyDebuffs(target, 1, 1);
						if(target instanceof ZombieEntity || target instanceof SkeletonEntity) {
							target.setFire(1);
						}
						if(target instanceof EntityCreatureBase) {
							EntityCreatureBase targetCreature = (EntityCreatureBase)target;
							if(targetCreature.daylightBurns()) {
								targetCreature.setFire(1);
							}
						}
					}
				}
			}

			// Playing:
			if(this.playPartner == null) {
				this.setAttackCooldownMax(this.getRangedCooldown());
				if (this.updateTick % 40 == 0) {
					List aoeTargets = this.getNearbyEntities(EntityWisp.class, null, 30);
					for (Object entityObj : aoeTargets) {
						EntityWisp target = (EntityWisp)entityObj;
						if(target != this && this.getAttackTarget() != target) {
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
				if(!this.playPartner.isAlive() || this.getAttackTarget() == this.playPartner || this.getDistance(this.playPartner) >= 100) {
					this.playPartner = null;
				}
				else if(this.hasAttackTarget()) {
					this.setAttackCooldownMax(this.getRangedCooldown());
					if(this.getPlayerOwner() == this.playPartner.getPlayerOwner()) {
						this.playPartner.setAttackTarget(this.getAttackTarget());
					}
				}
				else {
					if(!this.isAttackOnCooldown() && !this.hasAttackTarget()) {
						if(this.updateTick % this.getRangedCooldown() == 0) {
							this.setAttackCooldownMax(160);
							this.playPartner.setAttackCooldownMax(160);
							this.playPartner.triggerAttackCooldown();
							this.attackRanged(this.playPartner, this.getDistance(this.playPartner));
						}
					}
				}
			}
		}
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityLightBall.class, target, range, 0, new Vec3d(0, 0, 0), 0.5f, 1f, 1F);
		super.attackRanged(target, range);
    }

    @Override
	public boolean canAttack(LivingEntity targetEntity) {
    	if(targetEntity == this.playPartner) {
    		return false;
		}
		if(targetEntity instanceof EntityWisp && this.getPlayerOwner() == ((EntityWisp)targetEntity).getPlayerOwner()) {
    		return false;
		}
    	return super.canAttack(targetEntity);
	}


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean isFlying() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }


	// ==================================================
	//                   Brightness
	// ==================================================
	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}
}
