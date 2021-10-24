package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Barghest extends RideableCreatureEntity {

    protected boolean leapedAbilityQueued = false;
    protected boolean leapedAbilityReady = false;

    public Barghest(EntityType<? extends Barghest> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(!this.isTamed() && this.onGround && !this.getCommandSenderWorld().isClientSide) {
        	if(this.hasAttackTarget()) {
        		if(this.random.nextInt(10) == 0)
        			this.leap(4.0F, 0.5D, this.getTarget());
        	}
        }

        if(this.leapedAbilityQueued && !this.onGround && !this.getCommandSenderWorld().isClientSide) {
            this.leapedAbilityQueued = false;
            this.leapedAbilityReady = true;
        }
        if(this.leapedAbilityReady && this.onGround && !this.getCommandSenderWorld().isClientSide) {
            this.leapedAbilityReady = false;
            double distance = 4.0D;
            List<LivingEntity> possibleTargets = this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(distance, distance, distance), possibleTarget -> {
				if (!possibleTarget.isAlive()
						|| possibleTarget == Barghest.this
						|| Barghest.this.isEntityPassenger(possibleTarget, Barghest.this)
						|| Barghest.this.isAlliedTo(possibleTarget)
						|| !Barghest.this.canAttackType(possibleTarget.getType())
						|| !Barghest.this.canAttack(possibleTarget))
					return false;

				return true;
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
                        if (ObjectManager.getEffect("weight") != null)
                            possibleTarget.addEffect(new MobEffectInstance(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
                        else
                            possibleTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 0));
                    }
                }
            }
            this.playAttackSound();
        }
    }
    
    public void riderEffects(LivingEntity rider) {
    	if(rider.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
    		rider.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    	if(rider.hasEffect(ObjectManager.getEffect("weight")))
    		rider.removeEffect(ObjectManager.getEffect("weight"));
    }

    @Override
    public float getAISpeedModifier() {
    	if(!this.onGround)
    		return 2.0F;
    	return 1.0F;
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getDimensions(Pose.STANDING).height * 0.8D;
    }

    @Override
    public void leap(double distance, double leapHeight) {
        super.leap(distance, leapHeight);
        if(!this.getCommandSenderWorld().isClientSide)
            this.leapedAbilityQueued = true;
    }

    @Override
    public void leap(float range, double leapHeight, Entity target) {
        super.leap(range, leapHeight, target);
        if(!this.getCommandSenderWorld().isClientSide)
            this.leapedAbilityQueued = true;
    }

    public void mountAbility(Entity rider) {
    	if(this.getCommandSenderWorld().isClientSide)
    		return;

        if(!this.onGround)
            return;
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	this.playJumpSound();
    	this.leap(2.0D, 1.5D);
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 15;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canClimb() { return true; }

	public boolean petControlsEnabled() { return true; }
    
    

    @Override
    public float getFallResistance() {
    	return 20;
    }
}
