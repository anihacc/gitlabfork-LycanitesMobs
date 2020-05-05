package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityConcapedeHead extends AgeableCreatureEntity {
	
	public static int CONCAPEDE_SIZE_MAX = 10;
	public BaseCreatureEntity backSegment;
	public boolean isHungry = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeHead(EntityType<? extends EntityConcapedeHead> entityType, World world) {
        super(entityType, world);

        // Setup:
        this.entityGroup = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

	@Override
	public void loadCreatureFlags() {
		CONCAPEDE_SIZE_MAX = this.creatureInfo.getFlag("sizeMax", CONCAPEDE_SIZE_MAX);
	}
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onFirstSpawn() {
		// Create Starting Segments:
        if(!this.getEntityWorld().isRemote && this.backSegment == null) {
        	this.setGrowingAge(-this.growthTime / 4);
        	int segmentCount = this.getRNG().nextInt(CONCAPEDE_SIZE_MAX);
    		AgeableCreatureEntity parentSegment = this;
        	for(int segment = 0; segment < segmentCount; segment++) {
        		EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(parentSegment.getEntityWorld());
        		segmentEntity.setLocationAndAngles(parentSegment.getPositionVec().getX(), parentSegment.getPositionVec().getY(), parentSegment.getPositionVec().getZ(), 0.0F, 0.0F);
				segmentEntity.setParentTarget(parentSegment);
				segmentEntity.applyVariant(this.getVariantIndex());
				segmentEntity.setSizeScale(this.sizeScale);
				segmentEntity.spawnEventType = this.spawnEventType;
				segmentEntity.firstSpawn = false;
        		parentSegment.getEntityWorld().addEntity(segmentEntity);
				parentSegment = segmentEntity;
        	}
        }
        super.onFirstSpawn();
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	@Override
	public boolean shouldFollowParent() {
		return false; // Never follow parents.
	}
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	@Override
	public void setGrowingAge(int age) {
		// Spawn Additional Segments:
		if(!this.getEntityWorld().isRemote && !this.firstSpawn && age == 0 && !this.isHungry && CreatureManager.getInstance().getCreature("concapedesegment") != null) {
			age = -(this.growthTime / 4);
			this.isHungry = true;

			int size = 0;
			BaseCreatureEntity lastSegment = this;
			while(size <= CONCAPEDE_SIZE_MAX) {
				size++;

				BaseCreatureEntity trailingSegment = null;
				if(lastSegment instanceof EntityConcapedeHead)
					trailingSegment = ((EntityConcapedeHead)lastSegment).backSegment;
				else if(lastSegment instanceof EntityConcapedeSegment)
					trailingSegment = ((EntityConcapedeSegment)lastSegment).backSegment;

				if(trailingSegment == null || trailingSegment == lastSegment) {
					break;
				}
				lastSegment = trailingSegment;
			}

			if(size < CONCAPEDE_SIZE_MAX) {
				EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(lastSegment.getEntityWorld());
	    		segmentEntity.setLocationAndAngles(lastSegment.getPositionVec().getX(), lastSegment.getPositionVec().getY(), lastSegment.getPositionVec().getZ(), 0.0F, 0.0F);
				segmentEntity.setParentTarget(lastSegment);
				segmentEntity.applyVariant(this.getVariantIndex());
				segmentEntity.setSizeScale(this.sizeScale);
				segmentEntity.spawnEventType = this.spawnEventType;
				segmentEntity.firstSpawn = false;
				lastSegment.getEntityWorld().addEntity(segmentEntity);
			}
		}
        super.setGrowingAge(age);
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.ORGANIC)
                return 10F;
            if(blockState.getMaterial() == Material.EARTH)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return true;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof EntityConcapedeSegment)
			return false;
		return super.canAttack(target);
	}
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    @Override
    public boolean isAggressive() {
    	if(this.isInLove())
    		return false;
		this.getEntityWorld();
		if(this.isDaytime())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }
    
    @Override
    public boolean isProtective(Entity entity) {
    	if(this.isInLove())
    		return false;
		if(entity instanceof EntityConcapedeSegment) {
			BaseCreatureEntity checkCreature = (BaseCreatureEntity)entity;
			while(true) {
				if(!checkCreature.hasParent()) {
					break;
				}
				if(checkCreature.getParentTarget() == this) {
					return true;
				}
				if(checkCreature.getParentTarget() instanceof BaseCreatureEntity) {
					checkCreature = (BaseCreatureEntity)checkCreature.getParentTarget();
					continue;
				}
				break;
			}
		}
    	return false;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return null;
	}
	
	@Override
	public boolean canBreed() {
        return this.getGrowingAge() >= 0;
    }

    @Override
	public boolean breed() {
		if(super.breed()) {
			this.isHungry = false;
			if(this.getAge() == 0) {
				this.setGrowingAge(0);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canMate() {
		return false;
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }


	// ==================================================
	//                        NBT
	// ==================================================
	// ========== Read ===========
	/** Used when loading this mob from a saved chunk. **/
	@Override
	public void readAdditional(CompoundNBT nbtTagCompound) {
		if(nbtTagCompound.contains("IsHungry")) {
			this.isHungry = nbtTagCompound.getBoolean("IsHungry");
		}
		super.readAdditional(nbtTagCompound);
	}

	// ========== Write ==========
	/** Used when saving this mob to a chunk. **/
	@Override
	public void writeAdditional(CompoundNBT nbtTagCompound) {
		super.writeAdditional(nbtTagCompound);
		nbtTagCompound.putBoolean("IsHungry", this.isHungry);
	}
}
