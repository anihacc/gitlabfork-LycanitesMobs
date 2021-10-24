package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Eyewig extends RideableCreatureEntity {
    
    public Eyewig(EntityType<? extends Eyewig> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

	BaseProjectileEntity projectile = null;
	@Override
	public void attackRanged(Entity target, float range) {
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("poisonray");
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
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("poisonray");
		if(projectileInfo == null) {
			return;
		}

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
			if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof LivingEntity))
    			return;

			this.abilityProjectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);

	        this.playSound(this.abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	        this.getCommandSenderWorld().addFreshEntity(this.abilityProjectile);
    	}
    	
    	this.applyStaminaCost();
    }

    @Override
    public boolean isAggressive() {
		if(this.isTamed()) {
			return super.isAggressive();
		}
    	if(this.getCommandSenderWorld() != null && this.getCommandSenderWorld().isDay())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }

    @Override
    public boolean canClimb() { return true; }

	@Override
	public boolean isStrongSwimmer() { return true; }


	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }
    
    

    @Override
    public float getFallResistance() {
    	return 10;
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	public boolean petControlsEnabled() { return true; }
}
