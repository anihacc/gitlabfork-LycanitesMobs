package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityQuetzodracl extends RideableCreatureEntity implements IMob {

    protected AttackMeleeGoal attackAI;
    protected int waterTime = 0;
    protected boolean wantsToLand;
    protected boolean  isLanded;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityQuetzodracl(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.attackAI = new AttackMeleeGoal(this).setLongMemory(false);
        this.tasks.addTask(this.nextCombatGoalIndex++, this.attackAI);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

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
        
        // Entity Pickup Update:
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null) {
	    	this.attackAI.setEnabled(!this.hasPickupEntity());
            if(!this.isInWater()) {
                this.waterTime = 0;

                // Random Dropping:
                if(this.hasPickupEntity()) {
                    if(this.updateTick % this.getMeleeCooldown() == 0) {
                        this.attackMelee(this.getPickupEntity(), 1);
                    }
                    ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                    if(extendedEntity != null)
                        extendedEntity.setPickedUpByEntity(this);
                    if(this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                        this.dropPickupEntity();
                    }
                }
    	    	
    	    	/*/ Random Swooping:
    	    	else if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 2 && this.getRNG().nextInt(20) == 0) {
    	    		if(this.posY - 1 > this.getAttackTarget().posY)
    	    			this.leap(1.0F, -1.0D, this.getAttackTarget());
    	    		else if(this.posY + 1 < this.getAttackTarget().posY)
    	    			this.leap(1.0F, 1.0D, this.getAttackTarget());
    	    	}*/
            }

            // Burst Out of Water:
            else {
                this.waterTime++;
                if(this.hasPickupEntity() || this.getAir() <= 40) {
	                if(this.waterTime >= (2 * 20)) {
	                    this.waterTime = 0;
	                    this.leap(0.5F, 2.0D);
	                }
                }
                else if(this.hasAttackTarget()) {
                	if(this.waterTime >= (16 * 20)) {
	                    this.waterTime = 4 * 20;
	                    this.leap(0.5F, 2.0D);
                	}
                }
                else if(this.waterTime >= (8 * 20)) {
                    this.waterTime = 4 * 20;
                    this.leap(0.5F, 2.0D);
            	}
            }
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        if(this.hasPickupEntity() && this.getPickupEntity() instanceof EntityPlayer)
            wanderPosition = new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
        return wanderPosition;
    }

    // ========== Get Flight Offset ==========
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
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

        // Pickup:
        if(target instanceof EntityLivingBase && this.getControllingPassenger() == null) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            if (this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
        return !this.isLanded || this.hasPickupEntity();
    }

    @Override
    public boolean isStrongSwimmer() { return false; }
    
    @Override
    public void pickupEntity(EntityLivingBase entity) {
    	super.pickupEntity(entity);
        if(this.getEntityWorld().getBlockState(this.getPosition()) != null && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
    	    this.leap(1.0F, 2.0D);
    }

    @Override
    public void dropPickupEntity() {
    	// Drop Weight Effect:
        if(this.hasPickupEntity()) {
            if(ObjectManager.getEffect("weight") != null)
                this.getPickupEntity().addPotionEffect(new PotionEffect(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
        }
    	super.dropPickupEntity();
    }
    
    @Override
    public double[] getPickupOffset(Entity entity) {
    	return new double[]{0, -1.0D, 0};
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
        return 100;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.8D;
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

        EntityLivingBase nearestTarget = this.getNearestEntity(EntityLivingBase.class, null, 4, false);
        if(this.canPickupEntity(nearestTarget))
            this.pickupEntity(nearestTarget);

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
