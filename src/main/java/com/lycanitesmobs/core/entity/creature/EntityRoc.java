package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRoc extends RideableCreatureEntity implements IMob {
    public AttackMeleeGoal attackAI;

    public boolean creeperDropping = true;

    private int creeperDropCooldown = 0;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRoc(EntityType<? extends EntityRoc> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.entityGroup = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;

        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.CREEPER));

        super.registerGoals();

        this.attackAI = new AttackMeleeGoal(this).setLongMemory(false);
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, this.attackAI);
    }

    @Override
    public void loadCreatureFlags() {
        this.creeperDropping = this.creatureInfo.getFlag("creeperDropping", this.creeperDropping);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Entity Pickup Update:
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null) {
            // Attack AI and Creeper Carrying:
	    	this.attackAI.setEnabled(this.hasPickupEntity() ? this.getPickupEntity() instanceof CreeperEntity : this.creeperDropCooldown <= 0);
            if(this.creeperDropCooldown > 0) {
                this.creeperDropCooldown--;
            }

            // Pickup Update:
	    	if(this.hasPickupEntity()) {
	    		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
	    		if(extendedEntity != null)
	    			extendedEntity.setPickedUpByEntity(this);

                // Drop Creeper On Target:
                if(this.getPickupEntity() instanceof CreeperEntity && this.hasAttackTarget() && !(this.getAttackTarget() instanceof CreeperEntity)) {
                    double distance = new Vec3d(this.getPositionVec().getX(), 0, this.getPositionVec().getZ()).distanceTo(new Vec3d(this.getAttackTarget().getPositionVec().getX(), 0, this.getAttackTarget().getPositionVec().getZ()));
                    if(distance <= 2D && this.getPositionVec().getY() > this.getAttackTarget().getPositionVec().getY()) {
                        this.getPickupEntity().setRevengeTarget(this.getAttackTarget());
                        this.dropPickupEntity();
                        this.creeperDropCooldown = 6 * 20;
                    }
                }

                // Random Dropping:
                if(this.hasPickupEntity()) {
                    if (this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                        if (this.getPickupEntity() instanceof PlayerEntity) {
                            for (int distToGround = 0; distToGround < 8; distToGround++) {
                                Block searchBlock = this.getEntityWorld().getBlockState(new BlockPos((int) this.getPositionVec().getX(), (int) this.getPositionVec().getY() - distToGround, (int) this.getPositionVec().getZ())).getBlock();
                                if (searchBlock != null && searchBlock != Blocks.AIR) {
                                    this.dropPickupEntity();
                                    this.leap(1.0F, 2.0D);
                                    break;
                                }
                            }
                        } else if (!(this.getPickupEntity() instanceof CreeperEntity))
                            this.dropPickupEntity();
                    }
                }
	    	}
	    	
	    	/*/ Random Swooping:
	    	else if(this.hasAttackTarget() && !this.hasPickupEntity() && this.getDistance(this.getAttackTarget()) > 2 && this.getRNG().nextInt(20) == 0) {
	    		if(this.getPositionVec().getY() - 1 > this.getAttackTarget().getPositionVec().getY())
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.getPositionVec().getY() + 1 < this.getAttackTarget().getPositionVec().getY())
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    	}*/
        }

        // Mounted Creeper Carrying:
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null && this.getPickupEntity() instanceof CreeperEntity) {
            ((CreeperEntity) this.getPickupEntity()).setAttackTarget(null); // Prevent the carried Creeper from exploding on the riding player.
        }
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(StatusEffects.WEAKNESS))
            rider.removePotionEffect(StatusEffects.WEAKNESS);
        if(rider.isPotionActive(StatusEffects.MINING_FATIGUE))
            rider.removePotionEffect(StatusEffects.MINING_FATIGUE);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    /** Returns how high above attack targets this mob should fly when chasing. **/
    @Override
    public double getFlightOffset() {
        if(this.hasPickupEntity()) {
			return 5D;
		}
        return super.getFlightOffset();
    }

    @Override
    public boolean rollWanderChance() {
        if(this.isFlying())
            return this.getRNG().nextDouble() <= 0.25D;
        return this.getRNG().nextDouble() <= 0.008D;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        if(target instanceof LivingEntity && this.getControllingPassenger() == null) {
            LivingEntity entityLivingBase = (LivingEntity)target;
            // Pickup:
            if (this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
            if(entityLivingBase instanceof CreeperEntity) {
                entityLivingBase.setRevengeTarget(null);
                ((CreeperEntity) entityLivingBase).setAttackTarget(null);
                this.setAttackTarget(null);
            }
        }
        
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity targetEntity) {
        if(this.isTamed()) {
            return super.canAttack(targetEntity);
        }

        // Ignore Targets Picked Up By Another Mob:
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(targetEntity);
        if(extendedEntity != null && extendedEntity.pickedUpByEntity != null) {
            return false;
        }

        // Creeper Bombing:
        if(!this.creeperDropping && targetEntity instanceof CreeperEntity) {
            return false;
        }
        if(this.hasPickupEntity()) {
            if (targetEntity instanceof CreeperEntity)
                return false;
        }
        if(this.creeperDropCooldown > 0) {
            return false;
        }

        return super.canAttack(targetEntity);
    }

    @Override
    public boolean isAggressive() {
        if(this.isTamed()) {
            return super.isAggressive();
        }
        if ("".equals(this.spawnEventType) && this.getEntityWorld().isDaytime() && this.testLightLevel() >= 2) {
            return false;
        }
        return super.isAggressive();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    // ========== Pickup ==========
    @Override
    public void pickupEntity(LivingEntity entity) {
        super.pickupEntity(entity);
        if(this.getEntityWorld().getBlockState(this.getPosition()) != null && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
            this.leap(0.5F, 4.0D);
    }
    
    @Override
    public double[] getPickupOffset(Entity entity) {
        if(entity != null) {
            return new double[]{0, 1 - entity.getSize(Pose.STANDING).height, 0};
        }
    	return new double[]{0, -1, 0};
    }

    @Override
    public boolean canPickupEntity(LivingEntity entity) {
        if(this.creeperDropCooldown > 0 || this.hasPickupEntity())
            return false;
        return super.canPickupEntity(entity);
    }

    @Override
    public boolean canAttackWithPickup() {
        if(this.getPickupEntity() instanceof CreeperEntity) {
            return true;
        }
        return super.canAttackWithPickup();
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }


    // ==================================================
    //                     Positions
    // ==================================================
    // ========== Get Wander Position ==========
    /** Takes an initial chunk coordinate for a random wander position and ten allows the entity to make changes to the position or react to it. **/
    @Override
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.hasPickupEntity() && this.getPickupEntity() instanceof PlayerEntity)
            return new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
        return super.getWanderPosition(wanderPosition);
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
            if(this.getPickupEntity() instanceof CreeperEntity) {
                ((CreeperEntity)this.getPickupEntity()).ignite();
            }
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        LivingEntity nearestTarget = this.getNearestEntity(LivingEntity.class, null, 4, false);
        if(this.canPickupEntity(nearestTarget)) {
            this.pickupEntity(nearestTarget);
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 20;
    }

    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }
}
