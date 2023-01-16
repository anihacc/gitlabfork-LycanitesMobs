package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.*;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityVespidQueen extends TameableCreatureEntity implements IMob {
	public final CreatureStructure creatureStructure;
	protected int swarmLimit = 10;

    public EntityVespidQueen(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.solidCollision = true;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;

        this.stepHeight = 1.0F;
        this.setAttackCooldownMax(10);

		this.creatureStructure = new CreatureStructure(this, DungeonManager.getInstance().getTheme("vespid_hive"));
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
        this.tasks.addTask(this.nextTravelGoalIndex++, new StayByHomeGoal(this));

		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getClass()));
		Class<? extends Entity> conbaType = CreatureManager.getInstance().getEntityClass("conba");
		if(conbaType != null)
			this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(conbaType));
    }

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
	}

    @Override
    public boolean isPersistant() {
    	if(this.hasHome() && this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL)
    		return true;
    	return super.isPersistant();
    }

	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getEntityWorld().isRemote) {
        	return;
		}

		if (this.updateTick > 0 && this.updateTick % 20 == 0) {
			// Hive Structure:
			if (!this.hasHome()) {
				this.creatureStructure.setOrigin(this.getPosition());
			}
			if (!this.creatureStructure.isPhaseComplete(0) || this.updateTick % 200 == 0) {
				this.creatureStructure.refreshBuildTasks();
			}

			// Spawn Babies:
			if(this.hasHome() && this.creatureStructure.getFinalPhaseBuildTaskSize() <= 10 && this.updateTick % 60 == 0) {
				this.allyUpdate();
			}
		}
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }

	@Override
	public void onBuildTaskComplete(CreatureBuildTask buildTask) {
		super.onBuildTaskComplete(buildTask);
		if (!this.hasHome()) {
			this.setHome(this.creatureStructure.getOrigin().getX(), this.creatureStructure.getOrigin().getY(), this.creatureStructure.getOrigin().getZ(), 8F);
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
		return this.getRNG().nextDouble() <= 0.0008D;
	}

	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Babies:
		if(this.swarmLimit > 0 && this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("vespid").getEntityClass(), 32D) < this.swarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.05F) {
				EntityLivingBase minion = this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
				if(minion instanceof AgeableCreatureEntity) {
		    		((AgeableCreatureEntity)minion).setGrowingAge(((AgeableCreatureEntity) minion).growthTime);
		    	}
			}
		}
	}
	
    public EntityLivingBase spawnAlly(double x, double y, double z) {
		EntityLivingBase minion = CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof BaseCreatureEntity) {
    		((BaseCreatureEntity)minion).applyVariant(this.getVariantIndex());
    	}
    	this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
        return minion;
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
		if(!super.attackMelee(target, damageScale))
			return false;

		if(target instanceof EntityConba) {
			((EntityConba)target).vespidInfection = true;
			return true;
		}

        return true;
    }

    @Override
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity instanceof EntityConba)
        	if(((EntityConba)targetEntity).vespidInfection)
        		return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this)
    			return false;
    	}
    	return super.canAttackEntity(targetEntity);
    }

	@Override
	public boolean canAttackOwnSpecies() {
		return true;
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
	public boolean canBePushed() {
		return false;
	}

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	return 1.0F;
    }

    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }

	@Override
	public void setDead() {
		if (this.creatureStructure != null) {
			this.creatureStructure.removePitBLocks();
		}
		super.setDead();
	}
}
