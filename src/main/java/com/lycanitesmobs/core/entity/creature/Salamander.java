package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Salamander extends RideableCreatureEntity implements Enemy {
    public Salamander(EntityType<? extends Salamander> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = true;
        this.hasJumpSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.solidCollision = false;
        this.setupMob();

        this.maxUpStep = 1.0F;

        this.setPathfindingMalus(BlockPathTypes.LAVA, 0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        rider.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, (5 * 20) + 5, 1));
        if(rider.hasEffect(ObjectManager.getEffect("penetration")))
            rider.removeEffect(ObjectManager.getEffect("penetration"));
        if(rider.isOnFire())
            rider.clearFire();
    }

    @Override
    public float getAISpeedModifier() {
        if (!this.lavaContact())
            return 0.5F;
        return 2.0F;
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(this.getCommandSenderWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canStandOnFluid(Fluid fluid) {
        if (this.getControllingPassenger() instanceof Player) {
            Player player = (Player) this.getControllingPassenger();
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
            if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.DESCEND)) {
                return false;
            }
        }
        return fluid.is(FluidTags.LAVA);
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.85D;
    }

    public void specialAttack() {
        double distance = 5.0D;
        List<LivingEntity> possibleTargets = this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == Salamander.this
                        || Salamander.this.isEntityPassenger(possibleTarget, Salamander.this)
                        || Salamander.this.isAlliedTo(possibleTarget)
                        || !Salamander.this.canAttackType(possibleTarget.getType())
                        || !Salamander.this.canAttack(possibleTarget))
                    return false;
                return true;
            }
        });
        if(!possibleTargets.isEmpty()) {
            for(LivingEntity possibleTarget : possibleTargets) {
                boolean doDamage = true;
                if(this.getRider() instanceof Player) {
                    if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((Player)this.getRider(), possibleTarget))) {
                        doDamage = false;
                    }
                }
                if(doDamage) {
                    possibleTarget.setSecondsOnFire(5);
                }
            }
        }
        this.playAttackSound();
        this.triggerAttackCooldown();
    }

    @Override
    public void mountAbility(Entity rider) {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(this.abilityToggled)
            return;
        if(this.getStamina() < this.getStaminaCost())
            return;

        this.specialAttack();
        this.applyStaminaCost();
    }

    @Override
    public float getStaminaCost() {
        return 15;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 1.0F;
    }

    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5 * 20, 1));
        }
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return false; }
    
    @Override
    public boolean canBreatheUnderlava() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return true;
    }

    @Override
    public float getFallResistance() {
        return 100;
    }

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }

    @Override
    public void applyDropEffects(CustomItemEntity entityItem) {
        entityItem.setCanBurn(false);
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean petControlsEnabled() { return true; }
}
