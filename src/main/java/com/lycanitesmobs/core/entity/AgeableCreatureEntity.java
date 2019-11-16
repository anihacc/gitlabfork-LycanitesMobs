package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.entity.goals.actions.MateGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;

public abstract class AgeableCreatureEntity extends BaseCreatureEntity {
	
	// Size:
    private float scaledWidth = -1.0F;
    private float scaledHeight;
    
    // Targets:
    private AgeableCreatureEntity breedingTarget;
    
    // Growth:
    public int growthTime = -24000;
    public boolean canGrow = true;
    public double babySpawnChance = 0D;
    
    // Breeding:
    public int loveTime;
    private int loveTimeMax = 600;
    public int breedingCooldown = 6000;
    
    public boolean hasBeenFarmed = false;

    // Datawatcher:
    protected static final DataParameter<Integer> AGE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LOVE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
    
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public AgeableCreatureEntity(World world) {
		super(world);
	}
    
    // ========== Setup ==========
    public void setupMob() {
        if(this.babySpawnChance > 0D && this.rand.nextDouble() < this.babySpawnChance)
        	this.setGrowingAge(growthTime);
        super.setupMob();
    }
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(AGE, 0);
        this.dataManager.register(LOVE, 0);
    }

	// ========== Init AI ==========
	@Override
	protected void initEntityAI() {
		// Greater Actions:
		this.tasks.addTask(this.nextDistractionGoalIndex++, new MateGoal(this).setMateDistance(5.0D));

		super.initEntityAI();

		// Lesser Targeting:
		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindParentGoal(this).setSightCheck(false).setDistance(32.0D));

		// Lesser Actions:
		this.tasks.addTask(this.nextTravelGoalIndex++, new FollowParentGoal(this).setSpeed(1.0D).setStrayDistance(3.0D));
	}
    
    // ========== Name ==========
    @Override
    public String getAgeName() {
    	if(this.isChild())
    		return "Baby";
    	else
    		return "";
    }
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasBeenFarmed)
    		return true;
    	return super.isPersistant();
    }
    
    public void setFarmed() {
    	this.hasBeenFarmed = true;
        if(this.timeUntilPortal > this.getPortalCooldown())
            this.timeUntilPortal = this.getPortalCooldown();
    }
    
    // ========== Get Random Subspecies ==========
    @Override
    public void getRandomSubspecies() {
    	if(this.isChild())
    		return;
    	super.getRandomSubspecies();
    }
	
	
	// ==================================================
  	//                       Update
  	// ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Growing:
        if(this.getEntityWorld().isRemote)
            this.setScaleForAge(this.isChild());
        else if(this.canGrow) {
            int age = this.getGrowingAge();
            if(age < 0) {
                ++age;
                this.setGrowingAge(age);
            }
            else if(age > 0) {
                --age;
                this.setGrowingAge(age);
            }
        }
        
        // Breeding:
        if(!this.canBreed())
            this.loveTime = 0;

        if(!this.getEntityWorld().isRemote)
        	this.dataManager.set(LOVE, this.loveTime);
        if(this.getEntityWorld().isRemote)
        	this.loveTime = this.getIntFromDataManager(LOVE);
        
        if(this.isInLove()) {
        	this.setFarmed();
            --this.loveTime;
            if(this.getEntityWorld().isRemote) {
	            EnumParticleTypes particle = EnumParticleTypes.HEART;
	            if(this.loveTime % 10 == 0) {
	                double d0 = this.rand.nextGaussian() * 0.02D;
	                double d1 = this.rand.nextGaussian() * 0.02D;
	                double d2 = this.rand.nextGaussian() * 0.02D;
	                this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }
            }
        }
    }
    
    // ========== AI Update ==========
    @Override
    protected void updateAITasks() {
        if(!this.canBreed())
            this.loveTime = 0;
        super.updateAITasks();
    }
    
	
	// ==================================================
  	//                      Interact
  	// ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Item Commands:
    	if(itemStack != null) {
    		
    		// Spawn Egg:
    		if(itemStack.getItem() == this.creatureInfo.creatureType.getSpawnEgg())
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Spawn Baby");
    		
    		// Breeding Item:
    		if(this.isBreedingItem(itemStack) && this.canBreed() && !this.isInLove())
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Breed");
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public boolean performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Spawn Baby:
    	if(command.equals("Spawn Baby") && !this.getEntityWorld().isRemote) {
            ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
			CreatureInfo spawnEggCreatureInfo = itemCustomSpawnEgg.getCreatureInfo(itemStack);
			if(spawnEggCreatureInfo != null) {
				if (spawnEggCreatureInfo.entityClass != null && spawnEggCreatureInfo.entityClass.isAssignableFrom(this.getClass())) {
					AgeableCreatureEntity baby = this.createChild(this);
					if (baby != null) {
						baby.setGrowingAge(baby.growthTime);
						baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
						baby.setFarmed();
						this.getEntityWorld().spawnEntity(baby);
						if (itemStack.hasDisplayName()) {
							baby.setCustomNameTag(itemStack.getDisplayName());
						}
						this.consumePlayersItem(player, itemStack);
					}
				}
			}
			return true;
    	}
    	
    	// Breed:
    	if(command.equals("Breed")) {
    		if(this.breed()) {
				this.consumePlayersItem(player, itemStack);
				return true;
			}
    	}

		return super.performCommand(command, player, itemStack);
    }
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	public int getGrowingAge() {
        return this.getIntFromDataManager(AGE);
    }
	
	public void setGrowingAge(int age) {
		this.dataManager.set(AGE, age);
        this.setScaleForAge(this.isChild());
    }
	
	public void addGrowth(int growth) {
        int age = this.getGrowingAge();
        age += growth * 20;
        if (age > 0)
        	age = 0;
        this.setGrowingAge(age);
    }
	
	@Override
	public boolean isChild() {
        return this.getGrowingAge() < 0;
    }

	/**
	 * Returns if this creature should follow and find parents. By default only returns true if this creature is a baby.
	 * @return Returns true if parents should be searched for and followed.
	 */
	public boolean shouldFollowParent() {
		return !this.isChild();
	}

	/**
	 * Returns if this creature should look for a parent to follow if it has none already.
	 * @return True if this creature should actively seek parents.
	 */
	public boolean shouldFindParent() {
		return true;
	}

	
	// ==================================================
  	//                        Size
  	// ==================================================
	public void setScaleForAge(boolean age) {
        this.setAgeScale(age ? 0.5F : 1.0F);
    }
	
	protected final void setAgeScale(float age) {
        super.setSize(this.scaledWidth * age, this.scaledHeight * age);
    }
	
	@Override
	protected void setSize(float width, float height) {
        boolean validWidth = this.scaledWidth > 0.0F;
        this.scaledWidth = width;
        this.scaledHeight = height;
        if(!validWidth)
            this.setAgeScale(1.0F);
        super.setSize(width, height);
    }

    /** When called, this reapplies the initial width and height this mob and then applies sizeScale. **/
    @Override
	public void updateSize() {
        this.setSize(this.setWidth, this.setHeight);
        this.setScaleForAge(this.isChild());
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    /** Can this entity by tempted (usually lured by an item) currently? **/
    public boolean canBeTempted() { return !this.isInLove(); }

	// ========== Targets ==========
	public AgeableCreatureEntity getBreedingTarget() { return this.breedingTarget; }
	public void setBreedingTarget(AgeableCreatureEntity target) { this.breedingTarget = target; }
	
    // ========== Create Child ==========
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partener) {
		return (AgeableCreatureEntity)this.creatureInfo.createEntity(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	public boolean isBreedingItem(ItemStack itemStack) {
		if(!this.creatureInfo.isFarmable() || this.getAir() <= -100) {
			return false;
		}
		return this.creatureInfo.canEat(itemStack);
    }
	
	// ========== Valid Partner ==========
	public boolean canBreedWith(AgeableCreatureEntity partner) {
		if(partner == this) return false;
		if(partner.getClass() != this.getClass()) return false;
		return this.isInLove() && partner.isInLove();
	}
	
	// ========== Love Check ==========
	public boolean isInLove() {
		return this.loveTime > 0;
	}

	// ========== Mate Check ==========
	public boolean canMate() {
		return this.isInLove();
	}
	
	// ========== Breed ==========
	public boolean breed() {
		if(!this.canBreed())
			return false;
        this.loveTime = this.loveTimeMax;
        return true;
	}
	
	public boolean canBreed() {
        return this.getGrowingAge() == 0;
    }
	
	// ========== Procreate ==========
	public void procreate(AgeableCreatureEntity partner) {
		AgeableCreatureEntity baby = this.createChild(partner);

        if(baby != null) {
            this.finishBreeding();
            partner.finishBreeding();
            baby.setGrowingAge(baby.growthTime);
            Subspecies babySubspecies = this.creatureInfo.getChildSubspecies(this, this.getSubspeciesIndex(), partner.getSubspecies());
            baby.applySubspecies(babySubspecies != null ? babySubspecies.index : 0);
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.getEntityWorld().spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }

            this.onCreateBaby(partner, baby);

            this.getEntityWorld().spawnEntity(baby);
        }
    }

	public void onCreateBaby(AgeableCreatureEntity partner, AgeableCreatureEntity baby) {

	}
	
	public void finishBreeding() {
        this.setGrowingAge(this.breedingCooldown);
        this.setBreedingTarget(null);
        this.loveTime = 0;
	}
	
	
	// ==================================================
  	//                       NBT
  	// ==================================================
	// ========== Read ==========
    @Override
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        super.readEntityFromNBT(nbtTagCompound);
        if(nbtTagCompound.hasKey("Age")) {
        	this.setGrowingAge(nbtTagCompound.getInteger("Age"));
        }
        else {
        	this.setGrowingAge(0);
        }
        
        if(nbtTagCompound.hasKey("InLove")) {
        	this.loveTime = nbtTagCompound.getInteger("InLove");
        }
        else {
        	this.loveTime = 0;
        }
        
        if(nbtTagCompound.hasKey("HasBeenFarmed")) {
        	if(nbtTagCompound.getBoolean("HasBeenFarmed")) {
        		this.setFarmed();
        	}
        }
    }
	
	// ========== Write ==========
    @Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("Age", this.getGrowingAge());
        nbtTagCompound.setInteger("InLove", this.loveTime);
        nbtTagCompound.setBoolean("HasBeenFarmed", this.hasBeenFarmed);
    }
}
