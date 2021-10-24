package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Naxiris extends RideableCreatureEntity {
	public boolean griefing = true;
    
    public Naxiris(EntityType<? extends Naxiris> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;
        
        this.setAttackCooldownMax(20);
		this.solidCollision = true;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.hasEffect(MobEffects.DIG_SLOWDOWN))
            rider.removeEffect(MobEffects.DIG_SLOWDOWN);
        if(rider.hasEffect(ObjectManager.getEffect("weight")))
            rider.removeEffect(ObjectManager.getEffect("weight"));
    }

    public void onDamage(DamageSource damageSrc, float damage) {
    	super.onDamage(damageSrc, damage);
    	
    	Entity damageEntity = damageSrc.getEntity();
    	if(damageEntity != null && ("mob".equals(damageSrc.msgId) || "player".equals(damageSrc.msgId))) {
        	if(damageEntity instanceof LivingEntity) {
        		LivingEntity targetLiving = (LivingEntity)damageEntity;
        		List<MobEffect> goodEffects = new ArrayList<>();
        		for(MobEffectInstance effect : targetLiving.getActiveEffects()) {
					if(ObjectLists.inEffectList("buffs", effect.getEffect()))
						goodEffects.add(effect.getEffect());
        		}
        		if(goodEffects.size() > 0 && this.getRandom().nextBoolean()) {
        			if(goodEffects.size() > 1)
        				targetLiving.removeEffect(goodEffects.get(this.getRandom().nextInt(goodEffects.size())));
        			else
        				targetLiving.removeEffect(goodEffects.get(0));
    		    	float leeching = damage * 1.1F;
    		    	this.heal(leeching);
        		}
        	}
    	}
    }
	
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("arcanelaserstorm", target, range, 0, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }

    @Override
    public boolean isVulnerableTo(Entity entity) {
    	if(entity instanceof Naxiris)
    		return false;
    	return super.isVulnerableTo(entity);
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.9D;
    }

	@Override
	public double getMountedZOffset() {
		return (double)this.getDimensions(Pose.STANDING).width * -0.2D;
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
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("arcanelaserstorm");
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
