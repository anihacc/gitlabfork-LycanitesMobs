package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAegis extends TameableCreatureEntity implements IFusable {
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAegis(EntityType<? extends EntityAegis> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
        this.stepHeight = 1.0F;
	}

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
		this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, VillagerEntity.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        if(!this.getEntityWorld().isRemote) {
			if(!this.hasAttackTarget() && this.currentBlockingTime < 2) {
				this.setBlocking();
			}
		}
    }

	@Override
	public boolean canBeTargetedBy(LivingEntity entity) {
		if(entity instanceof IronGolemEntity || entity instanceof VillagerEntity) {
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
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
	public boolean canAttackWhileBlocking() {
		return false;
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
		return super.isVulnerableTo(type, source, damage);
    }


	// ==================================================
	//                   Taking Damage
	// ==================================================
	// ========== On Damage ==========
	/** Called when this mob has received damage. Here a random blocking chance is applied. **/
	@Override
	public void onDamage(DamageSource damageSrc, float damage) {
		if(this.getRNG().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F)
			this.setBlocking();
		if(damageSrc.getTrueSource() != null) {
			if(damageSrc.getTrueSource() instanceof MonsterEntity)
				damage *= 0.5F;
		}
		super.onDamage(damageSrc, damage);
	}

	// ========== Blocking ==========
	public void setBlocking() {
		this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
	}


	// ==================================================
	//                      Fusion
	// ==================================================
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
		if(fusable instanceof EntityCinder) {
			return CreatureManager.getInstance().getEntityType("wisp");
		}
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityType("nymph");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityType("vapula");
		}
		if(fusable instanceof EntityZephyr) {
			return CreatureManager.getInstance().getEntityType("sylph");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityType("spectre");
		}
		return null;
	}
}
