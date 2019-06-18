package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerAttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerDefenseTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.OwnerRevengeTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.entity.projectile.EntityFaeBolt;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityNymph extends EntityCreatureTameable implements IGroupPlant {

	public int healingRate = 20; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityNymph(EntityType<? extends EntityNymph> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

		this.fleeHealthPercent = 1.0F;
		this.isAggressiveByDefault = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
		this.goalSelector.addGoal(5, new AvoidGoal(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Updates
    // ==================================================
	private int farmingTick = 0;
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

		if(!this.getEntityWorld().isRemote) {
			// Healing Aura:
			if(this.healingRate > 0 && !this.isPetType("familiar")) {
				if (this.updateTick % this.healingRate == 0) {
					List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
					for (Object entityObj : aoeTargets) {
						LivingEntity target = (LivingEntity) entityObj;
						if (target != this && !(target instanceof EntityNymph) && target != this.getAttackTarget() && target != this.getAvoidTarget()) {
							target.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 0));
						}
					}
				}
			}

			// Ranged Attack:
			if(this.hasAvoidTarget()) {
				if(this.updateTick % this.getRangedCooldown() == 0) {
					this.attackRanged(this.getAvoidTarget(), this.getDistance(this.getAvoidTarget()));
				}
			}
			else if(this.hasAttackTarget()) {
				if(this.updateTick % this.getRangedCooldown() == 0) {
					this.attackRanged(this.getAttackTarget(), this.getDistance(this.getAttackTarget()));
				}
			}
		}

        /*/ Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 1; ++i) {
                this.getEntityWorld().addParticle(ParticleTypes.BLOCK_CRACK,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
                        0.0D, 0.0D, 0.0D,
                        Blocks.RED_FLOWER.getStateId(Blocks.RED_FLOWER.getStateFromMeta(2)));
				this.getEntityWorld().addParticle(ParticleTypes.BLOCK_CRACK,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						0.0D, 0.0D, 0.0D,
						Blocks.RED_FLOWER.getStateId(Blocks.RED_FLOWER.getStateFromMeta(8)));
            }*/
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityFaeBolt.class, target, range, 0, new Vec3d(0, 0, 0), 0.75f, 1f, 1F);
		super.attackRanged(target, range);
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
}
