package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Erepede extends RideableCreatureEntity {

    public Erepede(EntityType<? extends Erepede> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;
        this.attackCooldownMax = 10;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
    }

	@Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public float getAISpeedModifier() {
    	if(this.hasRiderTarget()) {
            BlockState blockState = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, -1, 0));
            if (blockState.getMaterial() == Material.SAND || (blockState.getMaterial() == Material.AIR && this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, -2, 0)).getMaterial() == Material.SAND))
                return 1.8F;
        }
    	return 1.0F;
    }
    
    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.9D;
    }

    public void mountAbility(Entity rider) {
    	if(this.getCommandSenderWorld().isClientSide)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	if(rider instanceof Player) {
    		Player player = (Player)rider;
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("mudshot");
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
    	return 5;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 0;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("mudshot", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }

	public boolean petControlsEnabled() { return true; }
}
