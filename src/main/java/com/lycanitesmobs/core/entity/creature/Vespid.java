package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureBuildTask;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.PlaceBlockGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

public class Vespid extends AgeableCreatureEntity implements Enemy {
    public PlaceBlockGoal aiPlaceBlock;
    public CreatureBuildTask creatureBuildTask;

	private boolean hiveBuilding = true;

    public Vespid(EntityType<? extends Vespid> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();

        this.canGrow = true;
        this.babySpawnChance = 0.1D;

        this.maxUpStep = 1.0F;
        this.setAttackCooldownMax(10);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
		this.aiPlaceBlock = new PlaceBlockGoal(this).setMaxDistance(128D).setSpeed(3D).setReplaceLiquid(true).setReplaceSolid(true);
		this.goalSelector.addGoal(this.nextIdleGoalIndex++, this.aiPlaceBlock);

		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(VespidQueen.class).setRange(64.0D));
    }

	@Override
	public void loadCreatureFlags() {
		this.hiveBuilding = this.creatureInfo.getFlag("hiveBuilding", this.hiveBuilding);
	}

    @Override
    public boolean isPersistant() {
		if(this.getMasterTarget() instanceof BaseCreatureEntity masterCreature) {
			return masterCreature.isPersistant();
		}
    	return super.isPersistant();
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(!this.getCommandSenderWorld().isClientSide && this.hiveBuilding && this.getMasterTarget() instanceof VespidQueen && this.aiPlaceBlock.blockState == null) {
        	VespidQueen queen = (VespidQueen)this.getMasterTarget();
        	this.creatureBuildTask = queen.creatureStructure.getBuildTask(this);
        	if (this.creatureBuildTask != null) {
				this.aiPlaceBlock.setBlockPlacement(this.creatureBuildTask.blockState, this.creatureBuildTask.pos);
			}
        }

        if(!this.getCommandSenderWorld().isClientSide && this.getTarget() instanceof Conba) {
        	if(((Conba)this.getTarget()).vespidInfection) {
        		this.setTarget(null);
        	}
        }
    }

    @Override
	public void onBlockPlaced(BlockPos blockPos, BlockState blockState) {
    	if (this.getMasterTarget() instanceof VespidQueen) {
			VespidQueen queen = (VespidQueen)this.getMasterTarget();
			if (this.creatureBuildTask != null) {
				queen.creatureStructure.completeBuildTask(this.creatureBuildTask);
				this.creatureBuildTask = null;
			}
		}
	}

    @Override
    public boolean canAttack(LivingEntity targetEntity) {
    	if(targetEntity == this.getMasterTarget())
    		return false;
		if(targetEntity instanceof Conba)
        	return false;
    	if(targetEntity instanceof Vespid) {
    		if(!((Vespid)targetEntity).hasMaster() || ((Vespid)targetEntity).getMasterTarget() == this.getMasterTarget())
    			return false;
    	}
    	if(targetEntity instanceof VespidQueen) {
    		if(!this.hasMaster() || this.getMasterTarget() == targetEntity)
    			return false;
    	}
    	if (this.hasMaster() && this.getMasterTarget() instanceof VespidQueen) {
			VespidQueen entityVespidQueen = (VespidQueen)this.getMasterTarget();
			CreatureRelationshipEntry creatureRelationshipEntry = entityVespidQueen.relationships.getEntry(targetEntity);
			if (creatureRelationshipEntry != null && !creatureRelationshipEntry.canAttack()) {
				return false;
			}
		}
    	return super.canAttack(targetEntity);
    }

    @Override
    public boolean isFlying() { return true; }

	@Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 2.0F;
    	return super.getDamageModifier(damageSrc);
    }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

    @Override
	public boolean hasLineOfSight(Entity entity) {
    	if (entity instanceof VespidQueen) {
    		return true;
		}
    	return super.hasLineOfSight(entity);
	}
}
