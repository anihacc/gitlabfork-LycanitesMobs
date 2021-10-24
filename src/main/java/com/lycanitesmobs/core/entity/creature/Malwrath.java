package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Malwrath extends RideableCreatureEntity {
    public boolean griefing = true;
    
    public Malwrath(EntityType<? extends Malwrath> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEAD;
        this.hasAttackSound = false;

        this.setupMob();

        this.maxUpStep = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.GHAST));
    }

    @Override
    public void loadCreatureFlags() {
        this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
    }

    @Override
    public void aiStep() {
        if(!this.getCommandSenderWorld().isClientSide && this.isRareVariant() && this.hasAttackTarget() && this.tickCount % 20 == 0) {
            this.allyUpdate();
        }

        super.aiStep();
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.hasEffect(MobEffects.WITHER))
            rider.removeEffect(MobEffects.WITHER);
        if(rider.isOnFire())
            rider.clearFire();
    }

    public void allyUpdate() {
        if(this.getCommandSenderWorld().isClientSide)
            return;

        if(CreatureManager.getInstance().getCreature("wraith").enabled) {
            if (this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").getEntityType(), 64D) < 10) {
                float random = this.random.nextFloat();
                if (random <= 0.1F) {
                    this.spawnAlly(this.position().x() - 2 + (random * 4), this.position().y(), this.position().z() - 2 + (random * 4));
                }
            }
        }
    }

    public void spawnAlly(double x, double y, double z) {
        Wraith minion = (Wraith)CreatureManager.getInstance().getCreature("wraith").createEntity(this.getCommandSenderWorld());
        minion.moveTo(x, y, z, this.getYRot(), this.getXRot());
		minion.setMinion(true);
		minion.setMasterTarget(this);
        this.getCommandSenderWorld().addFreshEntity(minion);
        if(this.getTarget() != null) {
            minion.setLastHurtByMob(this.getTarget());
        }
        minion.setSizeScale(this.sizeScale);
    }
	
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof Trite || target instanceof Astaroth || target instanceof Asmodeus || target instanceof Wraith)
            return false;
        return super.canAttack(target);
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("demonicblast", target, range, 0, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }

    @Override
    public boolean isVulnerableTo(Entity entity) {
    	if(entity instanceof Malwrath)
    		return false;
    	return super.isVulnerableTo(entity);
    }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if("explosion".equals(type))
            return false;
        return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.9D;
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

        if(rider instanceof Player) {
            Player player = (Player)rider;
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("demonicblast");
            if(projectileInfo != null) {
                BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), player);
                this.getCommandSenderWorld().addFreshEntity(projectile);
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
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

    @Override
    public float getBrightness() {
        if(isAttackOnCooldown())
            return 1.0F;
        else
            return super.getBrightness();
    }
}
