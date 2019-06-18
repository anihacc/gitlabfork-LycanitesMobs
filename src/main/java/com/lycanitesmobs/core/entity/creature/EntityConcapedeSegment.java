package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.entity.goals.actions.SwimmingGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityConcapedeSegment extends EntityCreatureAgeable implements IGroupAnimal {
    
	// Parent UUID:
	/** Used to identify the parent segment when loading this saved entity, set to null when found or lost for good. **/
	UUID parentUUID = null;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeSegment(EntityType<? extends EntityConcapedeSegment> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.hasStepSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(5, new FollowParentGoal(this).setSpeed(1.1D).setStrayDistance(0).setLostDistance(0).setAdultFollowing(true).setFollowBehind(0.25D));
        this.goalSelector.addGoal(6, new WanderGoal(this).setPauseRate(30));
        this.targetSelector.addGoal(0, new RevengeTargetingGoal(this).setHelpClasses(EntityConcapedeHead.class));
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
    public void getRandomSubspecies() {
    	if(this.subspecies == null && !this.hasParent()) {
    		this.subspecies = this.creatureInfo.getRandomSubspecies(this);
    		if(this.subspecies != null)
    			LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to " + this.subspecies.getTitle());
    		else
    			LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to base species.");
    	}
    	
    	if(this.hasParent() && this.getParentTarget() instanceof EntityCreatureBase) {
    		this.applySubspecies(((EntityCreatureBase)this.getParentTarget()).getSubspeciesIndex());
    	}
    }
    
    // ========== Despawning ==========
    /** Returns whether this mob should despawn overtime or not. Config defined forced despawns override everything except tamed creatures and tagged creatures. **/
    @Override
    protected boolean canDespawnNaturally() {
    	if(!super.canDespawnNaturally())
    		return false;
    	return !this.hasParent();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        // Try to Load Parent from UUID:
        if(!this.getEntityWorld().isRemote && !this.hasParent() && this.parentUUID != null) {
	        double range = 64D;
	        List connections = this.getEntityWorld().getEntitiesWithinAABB(EntityCreatureAgeable.class, this.getBoundingBox().grow(range, range, range));
	        Iterator possibleConnections = connections.iterator();
	        while(possibleConnections.hasNext()) {
	        	EntityCreatureAgeable possibleConnection = (EntityCreatureAgeable)possibleConnections.next();
	            if(possibleConnection != this && possibleConnection.getUniqueID().equals(this.parentUUID)) {
	            	this.setParentTarget(possibleConnection);
	            	break;
	            }
	        }
	        this.parentUUID = null;
        }
        
        super.livingTick();
        
        // Concapede Connections:
        if(!this.getEntityWorld().isRemote) {
        	// Check if back segment is alive:
        	if(this.hasMaster()) {
        		if(!this.getMasterTarget().isAlive())
        			this.setMasterTarget(null);
        	}

        	// Check if front segment is alive:
        	if(this.hasParent()) {
        		if(!this.getParentTarget().isAlive())
        			this.setParentTarget(null);
        	}

        	// Force position to front with offset:
        	if(this.hasParent()) {
        		this.faceEntity(this.getParentTarget(), 360, 360);
        		
        		double segmentDistance = 0.5D;
        		Vec3d pos;
        		if(this.getParentTarget() instanceof EntityCreatureBase)
        			pos = ((EntityCreatureBase)this.getParentTarget()).getFacingPositionDouble(this.getParentTarget().posX, this.getParentTarget().posY, this.getParentTarget().posZ, -0.25D, 0);
        		else
					pos = new Vec3d(this.getParentTarget().posX, this.getParentTarget().posY, this.getParentTarget().posZ);

        		if(this.posX - pos.x > segmentDistance)
        			this.posX = pos.x + segmentDistance;
        		else if(this.posX - pos.x < -segmentDistance)
        			this.posX = pos.x - segmentDistance;
        		
        		if(this.posY - pos.y > segmentDistance)
        			this.posY = pos.y + segmentDistance;
        		else if(this.posY - pos.y < -(segmentDistance / 2))
        			this.posY = pos.y;
        		
        		if(this.posZ - pos.z > segmentDistance)
        			this.posZ = pos.z + segmentDistance;
        		else if(this.posZ - pos.z < -segmentDistance)
        			this.posZ = pos.z - segmentDistance;
        	}
        }
        
        // Growth Into Head:
        if(!this.getEntityWorld().isRemote && this.getGrowingAge() <= 0)
        	this.setGrowingAge(-this.growthTime);
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
			this.getEntityWorld().addEntity(concapedeHead);
			if(this.hasMaster() && this.getMasterTarget() instanceof EntityConcapedeSegment)
				((EntityConcapedeSegment)this.getMasterTarget()).setParentTarget(concapedeHead);
			this.remove();
		}
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
	public void setParentTarget(LivingEntity setTarget) {
		if(setTarget instanceof EntityConcapedeSegment || setTarget instanceof EntityConcapedeHead)
			((EntityCreatureBase)setTarget).setMasterTarget(this);
		super.setParentTarget(setTarget);
	}
    
	
	// ==================================================
   	//                     Interact
   	// ==================================================
    // ========== Render Subspecies Name Tag ==========
    /** Gets whether this mob should always display its nametag if it's a subspecies. **/
	@Override
    public boolean renderSubspeciesNameTag() {
    	return !this.hasParent();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
	@Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
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
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
		return null;
	}
    
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
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
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void read(CompoundNBT nbtTagCompound) {
    	if(nbtTagCompound.contains("ParentUUIDMost") && nbtTagCompound.contains("ParentUUIDLeast")) {
            this.parentUUID = new UUID(nbtTagCompound.getLong("ParentUUIDMost"), nbtTagCompound.getLong("ParentUUIDLeast"));
        }
        super.read(nbtTagCompound);
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
    	if(this.getParentTarget() != null) {
    		nbtTagCompound.putLong("ParentUUIDMost", this.getParentTarget().getUniqueID().getMostSignificantBits());
    		nbtTagCompound.putLong("ParentUUIDLeast", this.getParentTarget().getUniqueID().getLeastSignificantBits());
    	}
        super.writeAdditional(nbtTagCompound);
    }
}
