package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityConcapedeSegment extends AgeableCreatureEntity {
    
	// Parent UUID:
	/** Used to identify the parent segment when loading this saved entity, set to null when found or lost for good. **/
	UUID parentUUID = null;

	public BaseCreatureEntity backSegment;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeSegment(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.hasStepSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		this.tasks.addTask(this.nextTravelGoalIndex++, new FollowParentGoal(this).setSpeed(4.0D).setStrayDistance(4.0D));
        super.initEntityAI();
    }

    // ==================================================
    //                      Spawning
    // ==================================================
    // ========== Natural Spawn Check ==========
    /** Second stage checks for spawning, this check is ignored if there is a valid monster spawner nearby. **/
    @Override
    public boolean environmentSpawnCheck(World world, BlockPos pos) {
    	if(this.getNearbyEntities(EntityConcapedeHead.class, null, CreatureManager.getInstance().spawnConfig.spawnLimitRange).size() <= 0)
    		return false;
    	return super.environmentSpawnCheck(world, pos);
    }
    
    // ========== Get Random Subspecies ==========
    @Override
    public void getRandomVariant() {
    	if(this.subspecies == null && !this.hasParent()) {
    		this.subspecies = this.creatureInfo.getRandomSubspecies(this);
    	}
    	
    	if(this.hasParent() && this.getParentTarget() instanceof BaseCreatureEntity) {
    		this.applyVariant(((BaseCreatureEntity)this.getParentTarget()).getSubspeciesIndex());
    	}
    }
    
    // ========== Despawning ==========
    /** Returns whether this mob should despawn overtime or not. Config defined forced despawns override everything except tamed creatures and tagged creatures. **/
    @Override
    protected boolean canDespawn() {
    	if(!super.canDespawn())
    		return false;
    	return !this.hasParent();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onUpdate() {
        // Try to Load Parent from UUID:
        if(!this.getEntityWorld().isRemote && !this.hasParent() && this.parentUUID != null && this.updateTick > 0 && this.updateTick % 40 == 0) {
	        double range = 64D;
	        List connections = this.getEntityWorld().getEntitiesWithinAABB(AgeableCreatureEntity.class, this.getEntityBoundingBox().grow(range, range, range));
	        Iterator possibleConnections = connections.iterator();
	        while(possibleConnections.hasNext()) {
	        	AgeableCreatureEntity possibleConnection = (AgeableCreatureEntity)possibleConnections.next();
	            if(possibleConnection != this && possibleConnection.getUniqueID().equals(this.parentUUID)) {
	            	this.setParentTarget(possibleConnection);
	            	break;
	            }
	        }
	        this.parentUUID = null;
        }
        
        super.onUpdate();
        
        // Concapede Connections:
        if(!this.getEntityWorld().isRemote) {
        	// Check if back segment is alive:
        	if(this.backSegment != null) {
        		if(!this.backSegment.isEntityAlive())
					this.backSegment = null;
        	}

        	// Check if front segment is alive:
        	if(this.hasParent()) {
        		if(!this.getParentTarget().isEntityAlive())
        			this.setParentTarget(null);
        	}

        	// Force position to front with offset:
        	if(this.hasParent()) {
				this.getLookHelper().setLookPositionWithEntity(this.getParentTarget(), 360.0F, 360.0F);
				this.faceEntity(this.getParentTarget(), 360, 360);

				Vec3d parentPos = this.getFacingPositionDouble(this.getParentTarget().posX, this.getParentTarget().posY, this.getParentTarget().posZ, -0.65D, this.getParentTarget().rotationYaw);//this.parentRotationLerp);
				double segmentPullThreshold = 0.15D;
				double segmentDistance = Math.sqrt(this.getDistanceSq(parentPos.x, parentPos.y, parentPos.z));
				if (segmentDistance > segmentPullThreshold) {
					double dragAmount = segmentPullThreshold / 2;
					Vec3d dragPos = this.getFacingPositionDouble(parentPos.x, parentPos.y, parentPos.z, dragAmount, this.getPositionVector().dotProduct(parentPos));
					double distY = (parentPos.y - this.posY);
					double dragY = this.posY + (distY / 2);
					this.setPosition(dragPos.x, dragY, dragPos.z);
				}
        	}

			// Growth Into Head:
			if(!this.getEntityWorld().isRemote && this.getGrowingAge() <= 0) {
				this.setGrowingAge(-this.growthTime);
			}
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
	public boolean isFlying() {
    	return false;
    }

	@Override
	public boolean canEntityBeSeen(Entity target) {
		return this.hasParent() && target == this.getParentTarget();
	}

	@Override
	public boolean canBePushed() {
		return !this.hasParent();
	}
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	@Override
	public void setGrowingAge(int age) {
		if(this.hasParent())
			age = -this.growthTime;
        super.setGrowingAge(age);
		if(age == 0 && !this.getEntityWorld().isRemote) {
			EntityConcapedeHead concapedeHead = (EntityConcapedeHead)CreatureManager.getInstance().getCreature("concapede").createEntity(this.getEntityWorld());
			concapedeHead.copyLocationAndAnglesFrom(this);
			concapedeHead.firstSpawn = false;
			concapedeHead.setGrowingAge(-this.growthTime / 4);
			concapedeHead.setSizeScale(this.sizeScale);
			concapedeHead.applyVariant(this.getVariantIndex());
			this.getEntityWorld().spawnEntity(concapedeHead);
			if(this.backSegment != null)
				this.backSegment.setParentTarget(concapedeHead);
			this.setDead();
		}
    }

    @Override
	public boolean shouldFollowParent() {
    	return true; // Follow as an adult.
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
	    return !this.hasParent();
    }
    
    // ========== Falling Speed Modifier ==========
    @Override
    public double getFallingMod() {
    	if(this.getEntityWorld().isRemote)
    		return 0.0D;
    	if(this.hasParent() && this.getParentTarget().posY > this.posY)
    		return 0.0D;
    	return super.getFallingMod();
    }
    
    
    // ==================================================
   	//                     Targets
   	// ==================================================
	@Override
	public void setParentTarget(EntityLivingBase setTarget) {
    	if(setTarget != this) {
			if (setTarget instanceof EntityConcapedeSegment)
				((EntityConcapedeSegment) setTarget).backSegment = this;
			if (setTarget instanceof EntityConcapedeHead)
				((EntityConcapedeHead) setTarget).backSegment = this;
		}
		super.setParentTarget(setTarget);
	}
    
	
	// ==================================================
   	//                     Interact
   	// ==================================================
    // ========== Render Subspecies Name Tag ==========
    /** Gets whether this mob should always display its nametag if it's a subspecies. **/
	@Override
    public boolean renderVariantNameTag() {
    	return !this.hasParent();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }

	// ==================================================
	//                     Equipment
	// ==================================================
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }


	// ==================================================
   	//                     Immunities
   	// ==================================================
	@Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("inWall"))
    		return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return null;
	}
	
	// ========== Breed ==========
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
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	if(nbtTagCompound.hasUniqueId("ParentUUID")) {
            this.parentUUID = nbtTagCompound.getUniqueId("ParentUUID");
		}
        super.readEntityFromNBT(nbtTagCompound);
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
    	if(this.hasParent()) {
    		nbtTagCompound.setUniqueId("ParentUUID", this.getParentTarget().getUniqueID());
    	}
    }
}
