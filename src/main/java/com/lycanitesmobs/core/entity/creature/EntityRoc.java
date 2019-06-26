package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRoc extends RideableCreatureEntity implements IMob, IGroupHunter {
    public AttackMeleeGoal attackAI;

    public boolean creeperDropping = true; // TODO Creature Flags.
    private int creeperDropCooldown = 0;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRoc(EntityType<? extends EntityRoc> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;

        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(2, new PlayerControlGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.attackAI = new AttackMeleeGoal(this).setLongMemory(false);
        this.goalSelector.addGoal(5, this.attackAI);
        this.goalSelector.addGoal(6, this.aiSit);
        this.goalSelector.addGoal(7, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this).setPauseRate(0));
        this.goalSelector.addGoal(9, new BegGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RiderRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new RiderAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(3, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(4, new OwnerDefenseTargetingGoal(this));
        this.targetSelector.addGoal(5, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(6, new AttackTargetingGoal(this).setTargetClass(CreeperEntity.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(7, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
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
                    double distance = new Vec3d(this.posX, 0, this.posZ).distanceTo(new Vec3d(this.getAttackTarget().posX, 0, this.getAttackTarget().posZ));
                    if(distance <= 2D && this.posY > this.getAttackTarget().posY) {
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
                                Block searchBlock = this.getEntityWorld().getBlockState(new BlockPos((int) this.posX, (int) this.posY - distToGround, (int) this.posZ)).getBlock();
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
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
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
        if(rider.isPotionActive(Effects.WEAKNESS))
            rider.removePotionEffect(Effects.WEAKNESS);
        if(rider.isPotionActive(Effects.MINING_FATIGUE))
            rider.removePotionEffect(Effects.MINING_FATIGUE);
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
    public boolean canAttack(LivingEntity target) {
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(target);
        if(extendedEntity != null && extendedEntity.pickedUpByEntity != null)
            return false;

        if(!this.creeperDropping && target instanceof CreeperEntity)
            return false;
        if(this.hasPickupEntity()) {
            if (target instanceof CreeperEntity)
                return false;
        }
        if (this.creeperDropCooldown > 0)
            return false;

        return super.canAttack(target);
    }


    // ==================================================
    //                      Targets
    // ==================================================
    @Override
    public boolean isAggressive() {
        if(this.isTamed()) {
            return super.isAggressive();
        }
        if(this.getEntityWorld() != null && this.getEntityWorld().isDaytime())
            return this.testLightLevel() < 2;
        else
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
    	return new double[]{0, -1.0D, 0};
    }

    @Override
    public boolean canPickupEntity(LivingEntity entity) {
        if(this.creeperDropCooldown > 0 || this.hasPickupEntity())
            return false;
        return super.canPickupEntity(entity);
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
