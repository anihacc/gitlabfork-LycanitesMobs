package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityScorchfireball;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityIgnibus extends EntityCreatureRideable implements IGroupFire, IGroupHeavy {

    protected boolean wantsToLand;
    protected boolean  isLanded;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityIgnibus(EntityType<? extends EntityIgnibus> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.flySoundSpeed = 20;
        this.hasAttackSound = false;
        
        this.setAttackCooldownMax(20);
        this.setupMob();

        this.stepHeight = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this));
        this.goalSelector.addGoal(2, new PlayerControlGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(4, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(20.0F).setMinChaseDistance(10.0F));
        this.goalSelector.addGoal(5, this.aiSit);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(7, new FollowParentGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(9, new BegGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(IGroupIce.class));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(IGroupWater.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(IGroupPlant.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAlpha.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(IGroupAnimal.class));
            this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(AnimalEntity.class));
        }
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSizeScale(double scale) {
        if(this.isRareSubspecies()) {
			super.setSizeScale(scale * 1.5D);
            return;
        }
        super.setSizeScale(scale);
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

        // Land/Fly:
        if(!this.getEntityWorld().isRemote) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.getLeashed() || this.isInWater() || (!this.isTamed() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean())) {
                    this.leap(1.0D, 1.0D);
                    this.isLanded = false;
                }
            }
            else {
                if(this.wantsToLand) {
                    if(!this.isLanded && this.isSafeToLand()) {
                        this.isLanded = true;
                    }
                }
                else {
                    if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
            if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.hasAttackTarget() || this.isInWater()) {
                this.wantsToLand = false;
            }
            else if(this.isTamed() && !this.getLeashed()) {
                this.wantsToLand = true;
            }
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().addParticle(ParticleTypes.SMOKE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
            }
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        rider.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, (5 * 20) + 5, 1));
        super.riderEffects(rider);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    @Override
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        return super.getWanderPosition(wanderPosition);
    }

    // ========== Get Flight Offset ==========
    @Override
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return !this.isLanded; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
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
   	//                      Attacks
   	// ==================================================

    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        // Type:
        List<EntityProjectileRapidFire> projectiles = new ArrayList<>();

        EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>) ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectiles.add(projectileEntry);

        EntityProjectileRapidFire projectileEntry2 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry2.offsetX += 1.0D;
        projectileEntry2.setProjectileScale(0.25f);
        projectiles.add(projectileEntry2);

        EntityProjectileRapidFire projectileEntry3 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry3.offsetX -= 1.0D;
        projectileEntry3.setProjectileScale(0.25f);
        projectiles.add(projectileEntry3);

        EntityProjectileRapidFire projectileEntry4 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry4.offsetZ += 1.0D;
        projectileEntry4.setProjectileScale(0.25f);
        projectiles.add(projectileEntry4);

        EntityProjectileRapidFire projectileEntry5 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry5.offsetZ -= 1.0D;
        projectileEntry5.setProjectileScale(0.25f);
        projectiles.add(projectileEntry5);

        EntityProjectileRapidFire projectileEntry6 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry6.offsetY += 1.0D;
        projectileEntry6.setProjectileScale(0.25f);
        projectiles.add(projectileEntry6);

        EntityProjectileRapidFire projectileEntry7 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
        projectileEntry7.offsetY -= 1.0D;
        projectileEntry7.setProjectileScale(0.25f);
        projectiles.add(projectileEntry7);

        for(EntityProjectileRapidFire projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= this.getSize(Pose.STANDING).height / 4;

            // Accuracy:
            float accuracy = 4.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.posX - this.posX + accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
            double d2 = target.posZ - this.posZ + accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
            projectile.setProjectileScale(4);

            // Launch:
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().addEntity(projectile);
        }

        super.attackRanged(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean waterDamage() { return false; }

    @Override
    public float getFallResistance() {
        return 100;
    }


    // ==================================================
    //                    Taking Damage
    // ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 0F;
        else return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        if(this.onGround) {
            return (double)this.getSize(Pose.STANDING).height * 0.52D;
        }
        return (double)this.getSize(Pose.STANDING).height * 0.54D;
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        if(rider instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)rider;
            // Type:
            List<EntityProjectileRapidFire> projectiles = new ArrayList<>();

            EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), player, 15, 3);
            projectiles.add(projectileEntry);

			EntityProjectileRapidFire projectileEntry2 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry2.offsetX += 1.0D;
			projectileEntry2.setProjectileScale(0.25f);
			projectiles.add(projectileEntry2);

			EntityProjectileRapidFire projectileEntry3 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry3.offsetX -= 1.0D;
			projectileEntry3.setProjectileScale(0.25f);
			projectiles.add(projectileEntry3);

			EntityProjectileRapidFire projectileEntry4 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry4.offsetZ += 1.0D;
			projectileEntry4.setProjectileScale(0.25f);
			projectiles.add(projectileEntry4);

			EntityProjectileRapidFire projectileEntry5 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry5.offsetZ -= 1.0D;
			projectileEntry5.setProjectileScale(0.25f);
			projectiles.add(projectileEntry5);

			EntityProjectileRapidFire projectileEntry6 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry6.offsetY += 1.0D;
			projectileEntry6.setProjectileScale(0.25f);
			projectiles.add(projectileEntry6);

			EntityProjectileRapidFire projectileEntry7 = new EntityProjectileRapidFire((EntityType<? extends EntityProjectileBase>)ObjectManager.specialEntityTypes.get(EntityProjectileRapidFire.class), EntityScorchfireball.class, this.getEntityWorld(), this, 15, 3);
			projectileEntry7.offsetY -= 10D;
			projectileEntry7.setProjectileScale(0.25f);
			projectiles.add(projectileEntry7);

            for(EntityProjectileRapidFire projectile : projectiles) {
                projectile.setProjectileScale(1f);

                // Y Offset:
                projectile.posY -= this.getSize(Pose.STANDING).height / 4;

                // Launch:
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.getEntityWorld().addEntity(projectile);
            }
            this.triggerAttackCooldown();
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 2;
    }

    public int getStaminaRecoveryWarmup() {
        return 2 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        if(isAttackOnCooldown())
            return 1.0F;
        else
            return super.getBrightness();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        if(isAttackOnCooldown())
            return 15728880;
        else
            return super.getBrightnessForRender();
    }
}
