package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Conba extends TameableCreatureEntity implements Enemy {
	AttackMeleeGoal aiAttackMelee;
	public boolean vespidInfection = false;
	public int vespidInfectionTime = 0;
	
    public Conba(EntityType<? extends Conba> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
		this.aiAttackMelee = new AttackMeleeGoal(this).setLongMemory(true).setEnabled(false);
		this.goalSelector.addGoal(this.nextPriorityGoalIndex++, this.aiAttackMelee);

        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new FireProjectilesGoal(this).setProjectile("poop").setFireRate(20).setVelocity(1.2F));

		super.registerGoals();

        this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(Player.class).setTameTargetting(false));
        this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(Villager.class));
        this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(Pillager.class));
    }

	@Override
    public MutableComponent getSpeciesName() {
		BaseComponent infection = new TextComponent("");
		if(this.vespidInfection) {
			String entityName = this.creatureInfo.getName();
	    	if(entityName != null) {
				infection = new TranslatableComponent("entity." + this.creatureInfo.modInfo.modid + "." + entityName + ".infected");
				infection.append(" ");
			}
		}
    	return infection.append(super.getSpeciesName());
    }
    
    public String getTextureName() {
    	if(this.vespidInfection)
    		return super.getTextureName() + "_infected";
    	return super.getTextureName();
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(this.onGround && !this.getCommandSenderWorld().isClientSide) {
        	if(this.hasAvoidTarget()) {
        		if(this.random.nextInt(10) == 0)
        			this.leap(1.0F, 0.6D, this.getTarget());
        	}
        	else {
        		if(this.random.nextInt(50) == 0 && this.isMoving())
        			this.leap(1.0D, 0.6D);
        	}
        }

        if(!this.getCommandSenderWorld().isClientSide) {
			if(!this.vespidInfection && "theswarm".equals(this.spawnEventType)) {
				this.vespidInfection = true;
			}

            if (this.vespidInfection && !this.getCommandSenderWorld().isClientSide) {
                this.aiAttackMelee.setEnabled(true);
                if (this.vespidInfectionTime++ >= 60 * 20) {
                    this.spawnVespidSwarm();
                    this.discard();
                }
            } else {
                this.aiAttackMelee.setEnabled(false);
            }
        }

        if(this.getCommandSenderWorld().isClientSide) {
        	this.vespidInfection = this.extraAnimation01();
        	if(this.vespidInfection) {
    	        for(int i = 0; i < 2; ++i) {
    	            this.getCommandSenderWorld().addParticle(ParticleTypes.WITCH, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
    	        }
        	}
        }
    }

	@Override
	public boolean shouldCreatureGroupFlee(LivingEntity target) {
		if(this.isTamed())
			return false;
		return super.shouldCreatureGroupFlee(target);
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof Vespid || target instanceof VespidQueen)
			return false;
		return super.canAttack(target);
	}

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("poop", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}

    @Override
    public void die(DamageSource damageSource) {
		if(!this.getCommandSenderWorld().isClientSide && this.vespidInfection)
			this.spawnVespidSwarm();
        super.die(damageSource);
    }
    
    public void spawnVespidSwarm() {
    	int j = 2 + this.random.nextInt(5) + getCommandSenderWorld().getDifficulty().getId() - 1;
        for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * this.getDimensions(Pose.STANDING).width / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * this.getDimensions(Pose.STANDING).width / 4.0F;
            Vespid vespid = (Vespid)CreatureManager.getInstance().getCreature("vespid").createEntity(this.getCommandSenderWorld());
            vespid.moveTo(this.position().x() + (double)f, this.position().y() + 0.5D, this.position().z() + (double)f1, this.random.nextFloat() * 360.0F, 0.0F);
            vespid.applyVariant(this.getVariantIndex());
            vespid.setGrowingAge(vespid.growthTime);
            vespid.spawnEventType = this.spawnEventType;
            this.getCommandSenderWorld().addFreshEntity(vespid);
            if(this.getTarget() != null)
            	vespid.setLastHurtByMob(this.getTarget());
        }
    }

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    public boolean extraAnimation01() {
    	if(!this.getCommandSenderWorld().isClientSide)
    		return this.vespidInfection;
	    else
	    	return this.extraAnimation01;
    }

    @Override
    public float getFallResistance() {
    	return 100;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
    	super.readAdditionalSaveData(nbt);
        
        if(nbt.contains("VespidInfection")) {
        	this.vespidInfection = nbt.getBoolean("VespidInfection");
        }
        if(nbt.contains("VespidInfectionTime")) {
        	this.vespidInfectionTime = nbt.getInt("VespidInfectionTime");
        }
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
    	nbt.putBoolean("VespidInfection", this.vespidInfection);
    	if(this.vespidInfection)
        	nbt.putInt("VespidInfectionTime", this.vespidInfectionTime);
    }
}
