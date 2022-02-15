package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAegis extends TameableCreatureEntity implements IFusable {

    public EntityAegis(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
        this.stepHeight = 1.0F;
	}

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityVillager.class));
		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityIronGolem.class));
    }

	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(!this.getEntityWorld().isRemote) {
			if(!this.hasAttackTarget() && this.currentBlockingTime < 2) {
				this.setBlocking();
			}
		}
    }

	@Override
	public boolean canBeTargetedBy(EntityLivingBase entity) {
		if(entity instanceof EntityIronGolem || entity instanceof EntityVillager) {
			return false;
		}
		return super.canBeTargetedBy(entity);
	}

	@Override
	public boolean shouldCreatureGroupHunt(EntityLivingBase target) {
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
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isDamageTypeApplicable(type, source, damage);
    }

	/** Called when this mob has received damage. Here a random blocking chance is applied. **/
	@Override
	public void onDamage(DamageSource damageSrc, float damage) {
		if(this.getRNG().nextDouble() > 0.5D && this.getHealth() / this.getMaxHealth() > 0.25F)
			this.setBlocking();
		if(damageSrc.getTrueSource() != null) {
			if(damageSrc.getTrueSource() instanceof EntityMob)
				damage *= 0.5F;
		}
		super.onDamage(damageSrc, damage);
	}

	@Override
	public void setBlocking() {
		this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
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
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityClass("wisp");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityClass("nymph");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityClass("vapula");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityClass("sylph");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityClass("spectre");
		}
		return null;
	}
}
