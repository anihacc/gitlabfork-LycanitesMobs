package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityGrell extends RideableCreatureEntity {

    public EntityGrell(EntityType<? extends EntityGrell> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(3.0F));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(GhastEntity.class));
    }

    public boolean isFlying() { return true; }

    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("acidglob", target, range, 0, new Vector3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.9D;
    }

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
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("acidglob");
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
}
