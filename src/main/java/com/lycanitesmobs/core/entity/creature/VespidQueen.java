package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureStructure;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

public class VespidQueen extends TameableCreatureEntity implements Enemy {
	public final CreatureStructure creatureStructure;
	protected int swarmLimit = 10;

    public VespidQueen(EntityType<? extends VespidQueen> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.solidCollision = true;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;

        this.maxUpStep = 1.0F;
        this.setAttackCooldownMax(10);

        this.creatureStructure = new CreatureStructure(this, DungeonManager.getInstance().getTheme("vespid_hive"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
        this.goalSelector.addGoal(this.nextTravelGoalIndex++, new StayByHomeGoal(this));

		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getType()));
		EntityType conbaType = CreatureManager.getInstance().getEntityType("conba");
		if(conbaType != null)
			this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(conbaType));
    }

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
	}

    @Override
    public boolean isPersistant() {
    	if(this.hasHome() && this.getCommandSenderWorld().getDifficulty() != Difficulty.PEACEFUL)
    		return true;
    	return super.isPersistant();
    }

	@Override
    public void aiStep() {
        super.aiStep();
        if (this.getCommandSenderWorld().isClientSide()) {
        	return;
		}

		if (this.updateTick % 20 == 0) {

			if (!this.hasHome()) {
				this.creatureStructure.setOrigin(this.blockPosition());
			}
			boolean structureStarted = this.creatureStructure.isPhaseComplete(0);
			if (!structureStarted || this.updateTick % 200 == 0) {
				this.creatureStructure.refreshBuildTasks();
			}
			if (structureStarted && !this.hasHome()) {
				this.setHome(this.creatureStructure.getOrigin().getX(), this.creatureStructure.getOrigin().getY(), this.creatureStructure.getOrigin().getZ(), 8F);
			}

			if(structureStarted && this.creatureStructure.getFinalPhaseBuildTaskSize() <= 10 && this.updateTick % 60 == 0) {
				this.allyUpdate();
			}
		}
        
        if(this.getTarget() instanceof Conba) {
        	if(((Conba)this.getTarget()).vespidInfection) {
        		this.setTarget(null);
        	}
        }
    }

    @Override
	public void setHomePosition(int x, int y, int z) {
    	super.setHomePosition(x, y, z);
    	this.creatureStructure.setOrigin(new BlockPos(x, y, z));
	}

	@Override
	public boolean rollWanderChance() {
    	if (this.hasHome()) {
    		return false;
		}
		return this.getRandom().nextDouble() <= 0.0008D;
	}

	public void allyUpdate() {
		if(this.getCommandSenderWorld().isClientSide)
			return;

		if(this.swarmLimit > 0 && this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("vespid").getEntityType(), 32D) < this.swarmLimit) {
			float random = this.random.nextFloat();
			if(random <= 0.05F) {
				LivingEntity minion = this.spawnAlly(this.position().x() - 2 + (random * 4), this.position().y(), this.position().z() - 2 + (random * 4));
				if(minion instanceof AgeableCreatureEntity) {
		    		((AgeableCreatureEntity)minion).setGrowingAge(((AgeableCreatureEntity) minion).growthTime);
		    	}
			}
		}
	}
	
    public LivingEntity spawnAlly(double x, double y, double z) {
		LivingEntity minion = CreatureManager.getInstance().getCreature("vespid").createEntity(this.getCommandSenderWorld());
    	minion.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof BaseCreatureEntity) {
    		((BaseCreatureEntity)minion).applyVariant(this.getVariantIndex());
    	}
    	this.getCommandSenderWorld().addFreshEntity(minion);
        if(this.getTarget() != null)
        	minion.setLastHurtByMob(this.getTarget());
        return minion;
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
		if(!super.attackMelee(target, damageScale))
			return false;

		if(target instanceof Conba) {
			((Conba)target).vespidInfection = true;
			return true;
		}

        return true;
    }

	@Override
	public boolean canAttackType(EntityType targetType) {
		if(targetType == this.getType())
			return true;
		return super.canAttackType(targetType);
	}

    @Override
    public boolean canAttack(LivingEntity targetEntity) {
    	if(targetEntity instanceof Conba)
        	if(((Conba)targetEntity).vespidInfection)
        		return false;
    	if(targetEntity instanceof Vespid) {
    		if(!((Vespid)targetEntity).hasMaster() || ((Vespid)targetEntity).getMasterTarget() == this)
    			return false;
    	}
		return super.canAttack(targetEntity);
    }

    @Override
	public boolean canBeTempted() {
    	return true;
	}

    @Override
	public boolean isTamingItem(ItemStack itemStack) {
		if(itemStack.isEmpty() || this.creatureInfo.creatureType == null) {
			return false;
		}

		if(itemStack.getItem() instanceof CreatureTreatItem) {
			CreatureTreatItem itemTreat = (CreatureTreatItem)itemStack.getItem();
			if(itemTreat.getCreatureType() == this.creatureInfo.creatureType) {
				return true;
			}
		}

		return super.isTamingItem(itemStack);
	}

    @Override
    public boolean isFlying() { return true; }

	@Override
	public boolean isPushable() {
		return false;
	}

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 2.0F;
    	return super.getDamageModifier(damageSrc);
    }

	@Override
	public int getNoBagSize() { return 0; }

	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }
}
