package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.projectile.EntityFaeBolt;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityNymph extends EntityCreatureTameable implements IGroupPlant {

	public int healingRate = 20;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityNymph(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

		this.fleeHealthPercent = 1.0F;
		this.isAggressiveByDefault = false;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.healingRate = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getInt("Features", "Nymph Healing Rate", this.healingRate, "Sets the rate in ticks (20 ticks = 1 second) that a Nymph heals surrounding entities.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
		this.field_70714_bg.addTask(5, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.field_70714_bg.addTask(8, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
							target.addPotionEffect(new EffectInstance(MobEffects.REGENERATION, 3 * 20, 0));
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
                this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        0.0D, 0.0D, 0.0D,
                        Blocks.RED_FLOWER.getStateId(Blocks.RED_FLOWER.getStateFromMeta(2)));
				this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						this.posY + this.rand.nextDouble() * (double) this.height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
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
