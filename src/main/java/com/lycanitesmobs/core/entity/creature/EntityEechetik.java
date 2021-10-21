package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.EffectBase;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class EntityEechetik extends TameableCreatureEntity implements Enemy {

	public int myceliumRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEechetik(EntityType<? extends EntityEechetik> entityType, Level world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public void loadCreatureFlags() {
		this.myceliumRadius = this.creatureInfo.getFlag("myceliumRadius", this.myceliumRadius);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void aiStep() {
        super.aiStep();

		// Plague Aura Attack:
		if(!this.getCommandSenderWorld().isClientSide && this.updateTick % 40 == 0 && this.hasAttackTarget()) {
			EffectBase plague = ObjectManager.getEffect("plague");
			if(plague != null) {
				MobEffectInstance potionEffect = new MobEffectInstance(plague, this.getEffectDuration(2), 1);
				List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 2);
				for(Object entityObj : aoeTargets) {
					LivingEntity target = (LivingEntity) entityObj;
					if (target != this && this.canAttackType(target.getType()) && this.canAttack(target) && this.getSensing().canSee(target) && target.canBeAffected(potionEffect)) {
						target.addEffect(potionEffect);
					}
				}
			}
		}

		// Grow Mycelium:
		if(!this.getCommandSenderWorld().isClientSide && this.updateTick % 100 == 0 && this.myceliumRadius > 0 && !this.isTamed() && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
			int range = this.myceliumRadius;
			for (int w = -((int) Math.ceil(this.getDimensions(Pose.STANDING).width) + range); w <= (Math.ceil(this.getDimensions(Pose.STANDING).width) + range); w++) {
				for (int d = -((int) Math.ceil(this.getDimensions(Pose.STANDING).width) + range); d <= (Math.ceil(this.getDimensions(Pose.STANDING).width) + range); d++) {
					for (int h = -((int) Math.ceil(this.getDimensions(Pose.STANDING).height) + range); h <= Math.ceil(this.getDimensions(Pose.STANDING).height); h++) {
						BlockPos blockPos = this.blockPosition().offset(w, h, d);
						BlockState blockState = this.getCommandSenderWorld().getBlockState(blockPos);
						BlockState upperBlockState = this.getCommandSenderWorld().getBlockState(blockPos.above());
						if (upperBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT) {
							this.getCommandSenderWorld().setBlockAndUpdate(blockPos, Blocks.MYCELIUM.defaultBlockState());
						}
					}
				}
			}
		}

		// Particles:
		if(this.getCommandSenderWorld().isClientSide) {
			for(int i = 0; i < 2; ++i) {
				this.getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width * 2, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width * 2, 0.0D, 0.0D, 0.0D);
				this.getCommandSenderWorld().addParticle(ParticleTypes.MYCELIUM, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width * 2, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width * 2, 0.0D, 0.0D, 0.0D);
			}
		}
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
	@Override
	public boolean isFlying() { return true; }

	@Override
	public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

	// ==================================================
	//                     Equipment
	// ==================================================
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
	// ========== Damage Modifier ==========
	public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.isFire())
			return 0F;
		else return super.getDamageModifier(damageSrc);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
}
