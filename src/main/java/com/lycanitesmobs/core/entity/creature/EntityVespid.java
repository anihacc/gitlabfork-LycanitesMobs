package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureBuildTask;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.PlaceBlockGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityVespid extends AgeableCreatureEntity implements IMob {
    public PlaceBlockGoal aiPlaceBlock;
	public CreatureBuildTask creatureBuildTask;

	private boolean hiveBuilding = true;

    public EntityVespid(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();

        this.canGrow = true;
        this.babySpawnChance = 0.1D;

        this.stepHeight = 1.0F;
        this.setAttackCooldownMax(10);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
		this.aiPlaceBlock = new PlaceBlockGoal(this).setMaxDistance(128D).setSpeed(3D).setReplaceLiquid(true).setReplaceSolid(true);
		this.tasks.addTask(this.nextIdleGoalIndex++, this.aiPlaceBlock);

		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(EntityVespidQueen.class).setRange(64.0D));
    }

	@Override
	public void loadCreatureFlags() {
		this.hiveBuilding = this.creatureInfo.getFlag("hiveBuilding", this.hiveBuilding);
	}

    @Override
    public boolean isPersistant() {
		if(this.getMasterTarget() instanceof BaseCreatureEntity)
			return ((BaseCreatureEntity)this.getMasterTarget()).isPersistant();
    	return super.isPersistant();
    }

	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Building AI:
		if(!this.getEntityWorld().isRemote && this.hiveBuilding && this.getMasterTarget() instanceof EntityVespidQueen && this.aiPlaceBlock.blockState == null) {
			EntityVespidQueen queen = (EntityVespidQueen)this.getMasterTarget();
			this.creatureBuildTask = queen.creatureStructure.takeBuildTask(this);
			if (this.creatureBuildTask != null) {
				this.aiPlaceBlock.setBlockPlacement(this.creatureBuildTask.blockState, this.creatureBuildTask.pos);
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
	public void onBlockPlaced(BlockPos blockPos, IBlockState blockState) {
		if (this.getMasterTarget() instanceof EntityVespidQueen) {
			EntityVespidQueen queen = (EntityVespidQueen)this.getMasterTarget();
			if (this.creatureBuildTask != null) {
				queen.creatureStructure.completeBuildTask(this.creatureBuildTask);
				this.creatureBuildTask = null;
			}
		}
	}

    @Override
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity == this.getMasterTarget())
    		return false;
    	if(targetEntity instanceof EntityConba)
        	return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this.getMasterTarget())
    			return false;
    	}
    	if(targetEntity instanceof EntityVespidQueen) {
    		if(!this.hasMaster() || this.getMasterTarget() == targetEntity)
    			return false;
    	}
		if (this.hasMaster() && this.getMasterTarget() instanceof EntityVespidQueen) {
			EntityVespidQueen entityVespidQueen = (EntityVespidQueen)this.getMasterTarget();
			CreatureRelationshipEntry creatureRelationshipEntry = entityVespidQueen.relationships.getEntry(targetEntity);
			if (creatureRelationshipEntry != null && !creatureRelationshipEntry.canAttack()) {
				return false;
			}
		}
    	return super.canAttackEntity(targetEntity);
    }

    @Override
    public boolean isFlying() { return true; }

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
	public boolean canEntityBeSeen(Entity entity) {
		if (entity instanceof EntityVespidQueen) {
			return true;
		}
		return super.canEntityBeSeen(entity);
	}
}
