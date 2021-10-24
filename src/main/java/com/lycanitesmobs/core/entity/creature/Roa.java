package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Roa extends RideableCreatureEntity implements Enemy {

    protected int whirlpoolRange = 6;

    protected int whirlpoolEnergy = 0;
    protected int whirlpoolEnergyMax = 5 * 20;
    protected boolean whirlpoolRecharging = true;
    protected int mountedWhirlpool = 0;

    public Roa(EntityType<? extends Roa> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

    @Override
    public void loadCreatureFlags() {
        this.whirlpoolRange = this.creatureInfo.getFlag("whirlpoolRange", this.whirlpoolRange);
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
            if(this.whirlpoolRecharging) {
                if(++this.whirlpoolEnergy >= this.whirlpoolEnergyMax)
                    this.whirlpoolRecharging = false;
            }
            this.whirlpoolEnergy = Math.min(this.whirlpoolEnergy, this.whirlpoolEnergyMax);
            if(this.canWhirlpool()) {
                for (Entity entity : this.getNearbyEntities(Entity.class, null, this.whirlpoolRange)) {
                    if (entity == this || entity.getClass() == this.getClass() || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy)
                        continue;
                    if(entity instanceof LivingEntity) {
                        LivingEntity entityLivingBase = (LivingEntity)entity;
                        if(entityLivingBase.hasEffect(ObjectManager.getEffect("weight")) || !this.canAttack(entityLivingBase))
                            continue;
                        if(!entity.isInWater() && !this.spawnEventType.equalsIgnoreCase("sharknado"))
                            continue;
                    }
                    ServerPlayer player = null;
                    if (entity instanceof ServerPlayer) {
                        player = (ServerPlayer) entity;
                        if (player.getAbilities().instabuild)
                            continue;
                    }
                    double xDist = this.position().x() - entity.position().x();
                    double zDist = this.position().z() - entity.position().z();
                    double xzDist = Math.max(Mth.sqrt((float) (xDist * xDist + zDist * zDist)), 0.01D);
                    double factor = 0.1D;
                    double motionCap = 10;
                    if(entity.getDeltaMovement().x() < motionCap && entity.getDeltaMovement().x() > -motionCap && entity.getDeltaMovement().z() < motionCap && entity.getDeltaMovement().z() > -motionCap) {
                        entity.push(
                                xDist / xzDist * factor + entity.getDeltaMovement().x() * factor,
                                0,
                                zDist / xzDist * factor + entity.getDeltaMovement().z() * factor
                        );
                    }
                    if (player != null)
                        player.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }
                if(--this.whirlpoolEnergy <= 0)
                    this.whirlpoolRecharging = true;
            }
        }

        if(this.mountedWhirlpool > 0)
            this.mountedWhirlpool--;
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        rider.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        super.riderEffects(rider);
    }

    public boolean extraAnimation01() {
        if(this.getCommandSenderWorld().isClientSide) {
            return super.extraAnimation01();
        }
        return this.canWhirlpool();
    }

    public boolean canWhirlpool() {
        if(this.getCommandSenderWorld().isClientSide) {
            return this.extraAnimation01();
        }

        if("sharknado".equals(this.spawnEventType)) {
            return true;
        }

        if(!this.isInWater()) {
            return false;
        }

        if(this.getControllingPassenger() != null && this.mountedWhirlpool > 0) {
            return true;
        }

        return !this.whirlpoolRecharging && this.hasAttackTarget() && this.distanceTo(this.getTarget()) <= (this.whirlpoolRange * 3);
    }

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getCommandSenderWorld().isRaining() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

	@Override
	public boolean isStrongSwimmer() {
		return true;
	}

	@Override
	public boolean canWalk() {
		return false;
	}

    @Override
    public boolean isFlying() {
        if("sharknado".equals(this.spawnEventType)) {
            return true;
        }
        return super.isFlying();
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.25D;
    }



    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return "sharknado".equals(this.spawnEventType);
    }

    @Override
    public void mountAbility(Entity rider) {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(this.getStamina() < this.getStaminaCost()) {
            return;
        }

        this.applyStaminaCost();
        this.mountedWhirlpool = 20;
    }

    @Override
    public float getStaminaCost() {
        return 2;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 4 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 2.0F;
    }

    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 5 * 20, 1));
        }
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean petControlsEnabled() { return true; }
}
