package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Quetzodracl extends RideableCreatureEntity implements Enemy, IGroupHeavy {

    protected AttackMeleeGoal attackAI;
    protected int waterTime = 0;
    protected boolean wantsToLand;
    protected boolean  isLanded;

    public Quetzodracl(EntityType<? extends Quetzodracl> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.attackAI = new AttackMeleeGoal(this).setLongMemory(false);
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, this.attackAI);
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.isLeashed() || this.isInWater() || (!this.isTamed() && this.updateTick % (5 * 20) == 0 && this.getRandom().nextBoolean())) {
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
                    if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % (5 * 20) == 0 && this.getRandom().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
            if(this.hasPickupEntity() || this.getControllingPassenger() != null || this.hasAttackTarget() || this.isInWater()) {
                this.wantsToLand = false;
            }
            else if(this.isTamed() && !this.isLeashed()) {
                this.wantsToLand = true;
            }
        }
        
        if(!this.getCommandSenderWorld().isClientSide && this.getControllingPassenger() == null) {
	    	this.attackAI.setEnabled(!this.hasPickupEntity());
            if(!this.isInWater()) {
                this.waterTime = 0;

                if(this.hasPickupEntity()) {
                    if(this.updateTick % this.getMeleeCooldown() == 0) {
                        this.attackMelee(this.getPickupEntity(), 1);
                    }
                    ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                    if(extendedEntity != null)
                        extendedEntity.setPickedUpByEntity(this);
                    if(this.tickCount % 100 == 0 && this.getRandom().nextBoolean()) {
                        this.dropPickupEntity();
                    }
                }
    	    	
    	    	/*/ Random Swooping:
    	    	else if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 2 && this.getRNG().nextInt(20) == 0) {
    	    		if(this.getPositionVec().getY() - 1 > this.getAttackTarget().getPositionVec().getY())
    	    			this.leap(1.0F, -1.0D, this.getAttackTarget());
    	    		else if(this.getPositionVec().getY() + 1 < this.getAttackTarget().getPositionVec().getY())
    	    			this.leap(1.0F, 1.0D, this.getAttackTarget());
    	    	}*/
            }

            else {
                this.waterTime++;
                if(this.hasPickupEntity() || this.getAirSupply() <= 40) {
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

    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.below(); groundPos.getY() > 0 && this.getCommandSenderWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.below()) {}
            if(this.getCommandSenderWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.above();
            }
        }
        if(this.hasPickupEntity() && this.getPickupEntity() instanceof Player)
            wanderPosition = new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
        return wanderPosition;
    }

    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }

    @Override
    public boolean rollWanderChance() {
        if(this.isFlying())
            return this.getRandom().nextDouble() <= 0.25D;
        return this.getRandom().nextDouble() <= 0.008D;
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        if(target instanceof LivingEntity && this.getControllingPassenger() == null) {
            LivingEntity entityLivingBase = (LivingEntity)target;
            if (this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
        }
        
        return true;
    }

    @Override
    public boolean isFlying() {
        return !this.isLanded || this.hasPickupEntity();
    }

    @Override
    public boolean isStrongSwimmer() { return false; }
    
    @Override
    public void pickupEntity(LivingEntity entity) {
    	super.pickupEntity(entity);
        if(this.getCommandSenderWorld().getBlockState(this.blockPosition()) != null && this.getCommandSenderWorld().canSeeSkyFromBelowWater(this.blockPosition()))
    	    this.leap(1.0F, 2.0D);
    }

    @Override
    public void dropPickupEntity() {
        if(this.hasPickupEntity()) {
            if(ObjectManager.getEffect("weight") != null)
                this.getPickupEntity().addEffect(new MobEffectInstance(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
        }
    	super.dropPickupEntity();
    }
    
    @Override
    public double[] getPickupOffset(Entity entity) {
        if(entity != null) {
            return new double[]{0, 1 - entity.getDimensions(Pose.STANDING).height, 0};
        }
        return new double[]{0, -1, 0};
    }

    public boolean petControlsEnabled() { return true; }
    
    

    @Override
    public float getFallResistance() {
        return 100;
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.8D;
    }

    @Override
    public void mountAbility(Entity rider) {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        LivingEntity nearestTarget = this.getNearestEntity(LivingEntity.class, null, 4, false);
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
