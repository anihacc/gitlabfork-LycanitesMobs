package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCacodemon extends RideableCreatureEntity {
    public boolean griefing = true;
    
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

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.GHAST));
    }

    @Override
    public void loadCreatureFlags() {
        this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
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
            if (this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").getEntityType(), 64D) < 10) {
                float random = this.rand.nextFloat();
                if (random <= 0.1F) {
                    this.spawnAlly(this.getPositionVec().getX() - 2 + (random * 4), this.getPositionVec().getY(), this.getPositionVec().getZ() - 2 + (random * 4));
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
        this.fireProjectile("demonicblast", target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
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
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("demonicblast");
            if(projectileInfo != null) {
                BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
                this.getEntityWorld().addEntity(projectile);
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.triggerAttackCooldown();
            }
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
}
