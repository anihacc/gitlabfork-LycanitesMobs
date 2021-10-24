package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class Aegis extends TameableCreatureEntity implements IFusable {

    public Aegis(EntityType<? extends Aegis> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
        this.maxUpStep = 1.0F;
	}

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, Villager.class));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
			if(!this.hasAttackTarget() && this.currentBlockingTime < 2) {
				this.setBlocking();
			}
		}
    }

	@Override
	public boolean canBeTargetedBy(LivingEntity entity) {
		if(entity instanceof IronGolem || entity instanceof Villager) {
			return false;
		}
		return super.canBeTargetedBy(entity);
	}

	@Override
	public boolean shouldCreatureGroupHunt(LivingEntity target) {
		if(target instanceof TameableCreatureEntity && ((TameableCreatureEntity)target).isTamed()) {
			return false;
		}
		return super.shouldCreatureGroupHunt(target);
	}

    @Override
    public boolean isFlying() { return true; }

    @Override
	public boolean canAttackWhileBlocking() {
		return false;
	}

    public boolean petControlsEnabled() { return true; }

	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isVulnerableTo(type, source, damage);
    }

	@Override
	public void onDamage(DamageSource damageSrc, float damage) {
		if(this.getRandom().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F)
			this.setBlocking();
		if(damageSrc.getEntity() != null) {
			if(damageSrc.getEntity() instanceof Monster)
				damage *= 0.5F;
		}
		super.onDamage(damageSrc, damage);
	}

	@Override
	public void setBlocking() {
		this.currentBlockingTime = this.blockingTime + this.getRandom().nextInt(this.blockingTime / 2);
	}

	@Override
	public float getDamageModifier(DamageSource damageSrc) {
		if (!this.isBlocking()) {
			return 2.0F;
		}
		return super.getDamageModifier(damageSrc);
	}

	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
		if(fusable instanceof Cinder) {
			return CreatureManager.getInstance().getEntityType("wisp");
		}
		if(fusable instanceof Jengu) {
			return CreatureManager.getInstance().getEntityType("nymph");
		}
		if(fusable instanceof Geonach) {
			return CreatureManager.getInstance().getEntityType("vapula");
		}
		if(fusable instanceof Zephyr) {
			return CreatureManager.getInstance().getEntityType("sylph");
		}
		if(fusable instanceof Argus) {
			return CreatureManager.getInstance().getEntityType("spectre");
		}
		return null;
	}
}
