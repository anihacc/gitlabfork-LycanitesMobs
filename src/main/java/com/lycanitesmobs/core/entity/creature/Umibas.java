package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class Umibas extends TameableCreatureEntity implements IGroupHeavy {

    public Umibas(EntityType<? extends Umibas> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(60));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
    }

    @Override
    public boolean rollWanderChance() {
        return this.getRandom().nextDouble() <= 0.001D;
    }

    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 2.0F;
        return 1.0F;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void attackRanged(Entity target, float range) {
        ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("magma");
        if(projectileInfo == null) {
            return;
        }
        BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);
        projectile.setProjectileScale(2f);

        projectile.setPos(
                projectile.position().x(),
                projectile.position().y() - this.getDimensions(Pose.STANDING).height / 4,
                projectile.position().z()
        );

        float accuracy = 1.0F * (this.getRandom().nextFloat() - 0.5F);

        double d0 = target.position().x() - this.position().x() + accuracy;
        double d1 = target.position().y() + (double)target.getEyeHeight() - 1.100000023841858D - projectile.position().y() + accuracy;
        double d2 = target.position().z() - this.position().z() + accuracy;
        float f1 = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
        float velocity = 1.2F;
        projectile.shoot(d0, d1 + (double) f1, d2, velocity, 6.0F);

        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getCommandSenderWorld().addFreshEntity(projectile);

        for(int i = 0; i < 10; i++) {
            projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);
            projectile.setProjectileScale(2f);
            projectile.shoot((this.getRandom().nextFloat()) - 0.5F, this.getRandom().nextFloat(), (this.getRandom().nextFloat()) - 0.5F, 0.5F, 3.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.getCommandSenderWorld().addFreshEntity(projectile);
        }

        super.attackRanged(target, range);
    }

    @Override
    public boolean canStealth() {
        if(this.isTamed() && this.isSitting())
            return false;
        BlockState blockState = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, -1, 0));
        if(blockState.getBlock() != Blocks.AIR) {
            if(blockState.getMaterial() == Material.DIRT) return true;
            if(blockState.getMaterial() == Material.GRASS) return true;
            if(blockState.getMaterial() == Material.LEAVES) return true;
            if(blockState.getMaterial() == Material.SAND) return true;
            if(blockState.getMaterial() == Material.CLAY) return true;
            if(blockState.getMaterial() == Material.TOP_SNOW) return true;
            if(blockState.getMaterial() == Material.SNOW) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
        return false;
    }

    public boolean canBeTempted() {
    	return this.isBaby();
    }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean waterDamage() { return true; }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBreatheAir() {
        return true;
    }


    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFire())
            return 0F;
        else return super.getDamageModifier(damageSrc);
    }
}