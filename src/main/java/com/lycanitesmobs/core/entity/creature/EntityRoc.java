package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRoc extends EntityCreatureRideable implements IMob, IGroupHunter {
    public EntityAIAttackMelee attackAI;

    public boolean creeperDropping = true;
    private int creeperDropCooldown = 0;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRoc(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;

        this.creeperDropping = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Roc Creeper Dropping", this.creeperDropping, "Set to false to prevent Rocs from picking up Creepers to drop on their victims!");
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
        this.field_70714_bg.addTask(5, this.attackAI);
        this.field_70714_bg.addTask(6, this.aiSit);
        this.field_70714_bg.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new EntityAIWander(this).setPauseRate(0));
        this.field_70714_bg.addTask(9, new EntityAIBeg(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityCreeper.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
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
	    	this.attackAI.setEnabled(this.hasPickupEntity() ? this.getPickupEntity() instanceof EntityCreeper : this.creeperDropCooldown <= 0);
            if(this.creeperDropCooldown > 0) {
                this.creeperDropCooldown--;
            }

            // Pickup Update:
	    	if(this.hasPickupEntity()) {
	    		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
	    		if(extendedEntity != null)
	    			extendedEntity.setPickedUpByEntity(this);

                // Drop Creeper On Target:
                if(this.getPickupEntity() instanceof EntityCreeper && this.hasAttackTarget() && !(this.getAttackTarget() instanceof EntityCreeper)) {
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
                        } else if (!(this.getPickupEntity() instanceof EntityCreeper))
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
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null && this.getPickupEntity() instanceof EntityCreeper) {
            ((EntityCreeper) this.getPickupEntity()).setAttackTarget(null); // Prevent the carried Creeper from exploding on the riding player.
        }
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(MobEffects.WEAKNESS))
            rider.removeEffectInstance(MobEffects.WEAKNESS);
        if(rider.isPotionActive(MobEffects.MINING_FATIGUE))
            rider.removeEffectInstance(MobEffects.MINING_FATIGUE);
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
            if(entityLivingBase instanceof EntityCreeper) {
                entityLivingBase.setRevengeTarget(null);
                ((EntityCreeper) entityLivingBase).setAttackTarget(null);
                this.setAttackTarget(null);
            }
        }
        
        return true;
    }

    @Override
    public boolean canAttackEntity(LivingEntity targetEntity) {
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(targetEntity);
        if(extendedEntity != null && extendedEntity.pickedUpByEntity != null)
            return false;
        return super.canAttackEntity(targetEntity);
    }
    
    @Override
	public boolean canAttackClass(Class targetClass) {
        if(!this.creeperDropping && targetClass == EntityCreeper.class)
            return false;
        if(this.hasPickupEntity()) {
            if (targetClass == EntityCreeper.class)
                return false;
        }
        if (this.creeperDropCooldown > 0)
            return false;
		return super.canAttackClass(targetClass);
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
        return (double)this.height * 0.9D;
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
            if(this.getPickupEntity() instanceof EntityCreeper) {
                ((EntityCreeper)this.getPickupEntity()).ignite();
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
