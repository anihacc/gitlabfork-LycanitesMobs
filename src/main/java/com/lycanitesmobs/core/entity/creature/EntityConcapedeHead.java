package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityConcapedeHead extends EntityCreatureAgeable implements IGroupAnimal, IGroupAlpha {
	
	public static int CONCAPEDE_SIZE_MAX = 10; // TODO Creature flags.
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeHead(EntityType<? extends EntityConcapedeHead> entityType, World world) {
        super(entityType, world);

        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(4, new AttackMeleeGoal(this).setLongMemory(false));
        this.goalSelector.addGoal(5, new TemptGoal(this).setItemList("vegetables"));
        this.goalSelector.addGoal(6, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));
        this.targetSelector.addGoal(0, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(1, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(2, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
    }
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onFirstSpawn() {
		// Create Starting Segments:
        if(!this.getEntityWorld().isRemote && !this.hasMaster()) {
        	this.setGrowingAge(-this.growthTime / 4);
        	int segmentCount = this.getRNG().nextInt(CONCAPEDE_SIZE_MAX);
    		EntityCreatureAgeable parentSegment = this;
        	for(int segment = 0; segment < segmentCount; segment++) {
        		EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(parentSegment.getEntityWorld());
        		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
				segmentEntity.setParentTarget(parentSegment);
        		parentSegment.getEntityWorld().addEntity(segmentEntity);
				parentSegment = segmentEntity;
        	}
        }
        super.onFirstSpawn();
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
    }
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	@Override
	public void setGrowingAge(int age) {
		// Spawn Additional Segments:
		if(!this.firstSpawn && age == 0 && CreatureManager.getInstance().getCreature("ConcapedeSegment") != null && !this.getEntityWorld().isRemote) {
			age = -(this.growthTime / 4);
			EntityCreatureBase parentSegment = this;
			boolean lastSegment = false;
			int size = 0;
			while(!lastSegment) {
				size++;
				if(parentSegment.hasMaster() && parentSegment.getMasterTarget() instanceof EntityCreatureBase)
					parentSegment = (EntityCreatureBase)(parentSegment.getMasterTarget());
				else
					lastSegment = true;
			}
			if(size < CONCAPEDE_SIZE_MAX) {
				EntityConcapedeSegment segmentEntity = (EntityConcapedeSegment)CreatureManager.getInstance().getCreature("concapedesegment").createEntity(parentSegment.getEntityWorld());
	    		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
	    		parentSegment.getEntityWorld().addEntity(segmentEntity);
				segmentEntity.setParentTarget(parentSegment);
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
		if(this.getEntityWorld().isDaytime())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }
    
    @Override
    public boolean isProtective(Entity entity) {
    	if(this.isInLove())
    		return false;
    	if(entity instanceof EntityConcapedeSegment) {
    		EntityCreatureBase checkSegment = this;
    		while(checkSegment != null) {
    			if(checkSegment == entity)
    				return true;
    			if(!checkSegment.hasMaster())
    				break;
    			checkSegment = (EntityCreatureBase)checkSegment.getMasterTarget();
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
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
		return null;
	}
    
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
	
	@Override
	public boolean canBreed() {
        return this.getGrowingAge() >= 0;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }
}
