package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGrell extends RideableCreatureEntity {

    public EntityGrell(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(3.0F));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityGhast.class));
    }

    public void spawnAlly(double x, double y, double z) {
        EntityWraith minion = (EntityWraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getEntityWorld());
        minion.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		minion.setMinion(true);
		minion.setMasterTarget(this);
        this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null) {
            minion.setRevengeTarget(this.getAttackTarget());
        }
        minion.setSizeScale(this.sizeScale);
    }

    public boolean isFlying() { return true; }

    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }

    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if(target instanceof  EntityTrite || target instanceof  EntityAstaroth || target instanceof  EntityAsmodeus || target instanceof  EntityWraith)
            return false;
        return super.canAttackEntity(target);
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("acidglob", target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
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

        if(rider instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)rider;
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("acidglob");
            if(projectileInfo != null) {
                BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
                this.getEntityWorld().spawnEntity(projectile);
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
