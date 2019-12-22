package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityConcapedeHead extends AgeableCreatureEntity {
	
	public static int CONCAPEDE_SIZE_MAX = 10;
	public BaseCreatureEntity backSegment;
	public boolean isHungry = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeHead(World world) {
        super(world);

        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.setupMob();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.tasks.addTask(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
		this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityConcapedeSegment.class));
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
        		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
				segmentEntity.setParentTarget(parentSegment);
				segmentEntity.applySubspecies(this.getSubspeciesIndex());
				segmentEntity.setSizeScale(this.sizeScale);
				segmentEntity.spawnEventType = this.spawnEventType;
				segmentEntity.firstSpawn = false;
        		parentSegment.getEntityWorld().spawnEntity(segmentEntity);
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
	    		segmentEntity.setLocationAndAngles(lastSegment.posX, lastSegment.posY, lastSegment.posZ, 0.0F, 0.0F);
				segmentEntity.setParentTarget(lastSegment);
				segmentEntity.applySubspecies(this.getSubspeciesIndex());
				segmentEntity.setSizeScale(this.sizeScale);
				segmentEntity.spawnEventType = this.spawnEventType;
				segmentEntity.firstSpawn = false;
	    		lastSegment.getEntityWorld().spawnEntity(segmentEntity);
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
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return true;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
	@Override
	public boolean canAttackEntity(EntityLivingBase target) {
		if(target instanceof EntityConcapedeSegment)
			return false;
		return super.canAttackEntity(target);
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
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("IsHungry")) {
			this.isHungry = nbtTagCompound.getBoolean("IsHungry");
		}
		super.readEntityFromNBT(nbtTagCompound);
	}

	// ========== Write ==========
	/** Used when saving this mob to a chunk. **/
	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("IsHungry", this.isHungry);
	}
}
