package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityDemonicBlast;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityCacodemon extends EntityCreatureRideable implements IGroupDemon {
    public boolean cacodemonGreifing = true; // TODO Creature flags.
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCacodemon(EntityType<? extends EntityCacodemon> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
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
        this.goalSelector.addGoal(1, new MateGoal(this));
        this.goalSelector.addGoal(2, new PlayerControlGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(4, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.goalSelector.addGoal(5, this.aiSit);
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(7, new FollowParentGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(9, new BegGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(3, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(4, new OwnerDefenseTargetingGoal(this));
        this.targetSelector.addGoal(4, new RevengeTargetingGoal(this));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(GhastEntity.class));
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
            this.allyUpdate();
        }

        super.livingTick();
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(Effects.WITHER))
            rider.removePotionEffect(Effects.WITHER);
        if(rider.isBurning())
            rider.extinguish();
    }

    // ========== Spawn Minions ==========
    public void allyUpdate() {
        if(this.getEntityWorld().isRemote)
            return;

        // Spawn Minions:
        if(CreatureManager.getInstance().getCreature("wraith").enabled) {
            if (this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").entityClass, 64D) < 10) {
                float random = this.rand.nextFloat();
                if (random <= 0.1F) {
                    this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
                }
            }
        }
    }

    public void spawnAlly(double x, double y, double z) {
        EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
        minion.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		minion.setMinion(true);
		minion.setMasterTarget(this);
        this.getEntityWorld().addEntity(minion);
        if(this.getAttackTarget() != null) {
            minion.setRevengeTarget(this.getAttackTarget());
        }
        minion.setSizeScale(this.sizeScale);
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
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
   	//                      Attacks
   	// ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof  EntityTrite || target instanceof  EntityAstaroth || target instanceof  EntityAsmodeus || target instanceof  EntityWraith)
            return false;
        return super.canAttack(target);
    }

	// ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityDemonicBlast.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isInvulnerableTo(Entity entity) {
    	if(entity instanceof EntityCacodemon)
    		return false;
    	return super.isInvulnerableTo(entity);
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    
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
        return (double)this.getSize(Pose.STANDING).height * 0.9D;
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
            EntityDemonicBlast projectile = new EntityDemonicBlast(ProjectileManager.getInstance().oldProjectileTypes.get(EntityDemonicBlast.class), this.getEntityWorld(), player);
            this.getEntityWorld().addEntity(projectile);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.triggerAttackCooldown();
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 10;
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
