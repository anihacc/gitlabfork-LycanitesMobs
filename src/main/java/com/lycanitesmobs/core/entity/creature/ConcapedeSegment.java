package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ConcapedeSegment extends AgeableCreatureEntity {

	UUID parentUUID = null;

	public BaseCreatureEntity backSegment;
	
    public ConcapedeSegment(EntityType<? extends ConcapedeSegment> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.hasStepSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
		this.goalSelector.addGoal(this.nextTravelGoalIndex++, new FollowParentGoal(this).setSpeed(1.0D).setStrayDistance(0));
        super.registerGoals();
    }

    @Override
    public boolean environmentSpawnCheck(Level world, BlockPos pos) {
    	if(this.getNearbyEntities(ConcapedeHead.class, null, CreatureManager.getInstance().spawnConfig.spawnLimitRange).size() <= 0)
    		return false;
    	return super.environmentSpawnCheck(world, pos);
    }

    @Override
    public void getRandomVariant() {
    	if(this.subspecies == null && !this.hasParent()) {
    		this.subspecies = this.creatureInfo.getRandomSubspecies(this);
    	}
    	
    	if(this.hasParent() && this.getParentTarget() instanceof BaseCreatureEntity) {
    		this.applyVariant(((BaseCreatureEntity)this.getParentTarget()).getSubspeciesIndex());
    	}
    }

    @Override
    protected boolean canDespawnNaturally() {
    	if(!super.canDespawnNaturally())
    		return false;
    	return !this.hasParent();
    }

	@Override
    public void aiStep() {
        if(!this.getCommandSenderWorld().isClientSide && !this.hasParent() && this.parentUUID != null && this.updateTick > 0 && this.updateTick % 40 == 0) {
	        double range = 64D;
	        List connections = this.getCommandSenderWorld().getEntitiesOfClass(AgeableCreatureEntity.class, this.getBoundingBox().inflate(range, range, range));
	        Iterator possibleConnections = connections.iterator();
	        while(possibleConnections.hasNext()) {
	        	AgeableCreatureEntity possibleConnection = (AgeableCreatureEntity)possibleConnections.next();
	            if(possibleConnection != this && possibleConnection.getUUID().equals(this.parentUUID)) {
	            	this.setParentTarget(possibleConnection);
	            	break;
	            }
	        }
	        this.parentUUID = null;
        }
        
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide) {
			if(this.backSegment != null) {
				if(!this.backSegment.isAlive())
					this.backSegment = null;
			}

        	if(this.hasParent()) {
        		if(!this.getParentTarget().isAlive())
        			this.setParentTarget(null);
        	}

        	if(this.hasParent()) {
				this.getLookControl().setLookAt(this.getParentTarget(), 360.0F, 360.0F);
				this.lookAt(this.getParentTarget(), 360, 360);

				Vec3 parentPos = this.getFacingPositionDouble(this.getParentTarget().getX(), this.getParentTarget().getY(), this.getParentTarget().getZ(), -0.65D, this.getParentTarget().getYRot());
				double segmentPullThreshold = 0.15D;
				double segmentDistance = Math.sqrt(this.distanceToSqr(parentPos));
				if (segmentDistance > segmentPullThreshold) {
					double dragAmount = segmentPullThreshold / 2;
					Vec3 posVector = new Vec3(this.getX(), this.getY(), this.getZ());
					Vec3 dragPos = this.getFacingPositionDouble(parentPos.x, parentPos.y, parentPos.z, dragAmount, posVector.dot(parentPos));
					double distY = (parentPos.y - this.getY());
					double dragY = this.getY() + (distY / 2);
					this.setPos(dragPos.x, dragY, dragPos.z);
				}
        	}

			if(!this.getCommandSenderWorld().isClientSide && this.getGrowingAge() <= 0)
				this.setGrowingAge(-this.growthTime);
        }
    }

	@Override
	public boolean rollLookChance() {
		if(this.hasParent())
			return false;
		return super.rollLookChance();
	}

	@Override
	public boolean rollWanderChance() {
		if(this.hasParent())
			return false;
		return super.rollWanderChance();
	}

	@Override
	public void setGrowingAge(int age) {
		if(this.hasParent())
			age = -this.growthTime;
        super.setGrowingAge(age);
		if(age == 0 && !this.getCommandSenderWorld().isClientSide) {
			ConcapedeHead concapedeHead = (ConcapedeHead)CreatureManager.getInstance().getCreature("concapede").createEntity(this.getCommandSenderWorld());
			concapedeHead.copyPosition(this);
			concapedeHead.firstSpawn = false;
			concapedeHead.setGrowingAge(-this.growthTime / 4);
			concapedeHead.setSizeScale(this.sizeScale);
			concapedeHead.applyVariant(this.getVariantIndex());
			this.getCommandSenderWorld().addFreshEntity(concapedeHead);
			if(this.backSegment != null)
				this.backSegment.setParentTarget(concapedeHead);
			this.discard();
		}
    }

    @Override
	public boolean shouldFollowParent() { return true; }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.DIRT)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
    

    @Override
    public boolean canBeLeashed(Player player) {
	    return !this.hasParent();
    }

    @Override
    public double getFallingMod() {
    	if(this.getCommandSenderWorld().isClientSide)
    		return 0.0D;
    	if(this.hasParent() && this.getParentTarget().position().y() > this.position().y())
    		return 0.0D;
    	return super.getFallingMod();
    }

    @Override
	public boolean useDirectNavigator() {
    	return this.hasParent();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void setParentTarget(LivingEntity setTarget) {
		if(setTarget != this) {
			if (setTarget instanceof ConcapedeSegment)
				((ConcapedeSegment) setTarget).backSegment = this;
			if (setTarget instanceof ConcapedeHead)
				((ConcapedeHead) setTarget).backSegment = this;
		}
		super.setParentTarget(setTarget);
	}

	@Override
    public boolean renderVariantNameTag() {
    	return !this.hasParent();
    }

    @Override
    public boolean canClimb() { return true; }
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

	@Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall"))
    		return false;
    	return super.isVulnerableTo(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }

    @Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return null;
	}

	public boolean breed() {
		if(!this.canBreed())
			return false;
        this.setGrowingAge(0);
        return true;
	}
	
	@Override
	public boolean canBreed() {
        return !this.hasParent();
    }

	@Override
	public boolean shouldFindParent() {
		return false;
	}

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
    	if(nbt.hasUUID("ParentUUID")) {
            this.parentUUID = nbt.getUUID("ParentUUID");
        }
        super.readAdditionalSaveData(nbt);
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
    	if(this.hasParent()) {
			nbt.putUUID("ParentUUID", this.getParentTarget().getUUID());
    	}
    }
}
