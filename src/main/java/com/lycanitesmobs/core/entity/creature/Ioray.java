package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Ioray extends RideableCreatureEntity implements Enemy {

	WanderGoal wanderAI;

    public Ioray(EntityType<? extends Ioray> entityType, Level world) {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false));
    }

	@Override
    public void riderEffects(LivingEntity rider) {
        rider.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        super.riderEffects(rider);
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
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.6D;
    }
    
    

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return false;
    }

    @Override
    public boolean canBurn() { return false; }

    BaseProjectileEntity projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
        ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("waterjet");
        if(projectileInfo == null) {
            return;
        }

        if(this.projectile != null && this.projectile.isAlive()) {
            this.projectile.projectileLife = 20;
        }
        else {
            this.projectile = null;
        }

        if(this.projectile == null) {
    
            this.projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);

            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getCommandSenderWorld().addFreshEntity(projectile);
        }

        super.attackRanged(target, range);
    }

    BaseProjectileEntity abilityProjectile = null;
    public void mountAbility(Entity rider) {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(this.getStamina() < this.getStaminaRecoveryMax() * 2)
            return;

        if(this.hasAttackTarget())
            this.setTarget(null);

        if(this.abilityProjectile != null && this.abilityProjectile.isAlive()) {
            this.abilityProjectile.projectileLife = 20;
        }
        else {
            this.abilityProjectile = null;
        }

        if(this.abilityProjectile == null) {
    
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("waterjet");
            if(projectileInfo == null) {
                return;
            }
            if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof LivingEntity))
                return;

            this.abilityProjectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);

            this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getCommandSenderWorld().addFreshEntity(abilityProjectile);
        }

        this.applyStaminaCost();
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
