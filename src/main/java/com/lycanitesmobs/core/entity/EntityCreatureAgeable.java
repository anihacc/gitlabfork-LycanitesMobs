package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;

public abstract class EntityCreatureAgeable extends EntityCreatureBase {
	
	// Size:
    private float scaledWidth = -1.0F;
    private float scaledHeight;
    
    // Targets:
    private EntityCreatureAgeable breedingTarget;
    
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
    protected static final DataParameter<Integer> AGE = EntityDataManager.createKey(EntityCreatureAgeable.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LOVE = EntityDataManager.createKey(EntityCreatureAgeable.class, DataSerializers.VARINT);
    
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureAgeable(EntityType<? extends EntityCreatureAgeable> entityType, World world) {
		super(entityType, world);
	}
    
    // ========== Setup ==========
    public void setupMob() {
        if(this.babySpawnChance > 0D && this.rand.nextDouble() < this.babySpawnChance)
        	this.setGrowingAge(growthTime);
        super.setupMob();
    }
	
	// ========== Init ==========
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(AGE, 0);
        this.dataManager.register(LOVE, 0);
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
    public void livingTick() {
        super.livingTick();

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
	            IParticleData particle = ParticleTypes.HEART;
	            if(this.loveTime % 10 == 0) {
	                double d0 = this.rand.nextGaussian() * 0.02D;
	                double d1 = this.rand.nextGaussian() * 0.02D;
	                double d2 = this.rand.nextGaussian() * 0.02D;
	                this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
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
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Item Commands:
    	if(itemStack != null) {
    		
    		// Spawn Egg:
    		if(itemStack.getItem() == this.creatureInfo.getSpawnEgg())
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Spawn Baby");
    		
    		// Breeding Item:
    		if(this.isBreedingItem(itemStack) && this.canBreed() && !this.isInLove())
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Breed");
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Spawn Baby:
    	if(command.equals("Spawn Baby") && !this.getEntityWorld().isRemote) {
            ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
			CreatureInfo spawnEggCreatureInfo = itemCustomSpawnEgg.creatureInfo;
			if(spawnEggCreatureInfo != null) {
				if (spawnEggCreatureInfo.entityClass != null && spawnEggCreatureInfo.entityClass.isAssignableFrom(this.getClass())) {
					EntityCreatureAgeable baby = this.createChild(this);
					if (baby != null) {
						baby.setGrowingAge(baby.growthTime);
						baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
						baby.setFarmed();
						this.getEntityWorld().addEntity(baby);
						if (itemStack.hasDisplayName()) {
							baby.setCustomName(itemStack.getDisplayName());
						}
						this.consumePlayersItem(player, itemStack);
					}
				}
			}
    	}
    	
    	// Breed:
    	if(command.equals("Breed")) {
    		if(this.breed())
    			this.consumePlayersItem(player, itemStack);
    	}
    	
    	super.performCommand(command, player, itemStack);
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

	
	// ==================================================
  	//                        Size
  	// ==================================================
	public double setScaleForAge(boolean adult) {
        return adult ? 0.5F : 1.0F;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    /** Can this entity by tempted (usually lured by an item) currently? **/
    public boolean canBeTempted() { return !this.isInLove(); }

	// ========== Targets ==========
	public EntityCreatureAgeable getBreedingTarget() { return this.breedingTarget; }
	public void setBreedingTarget(EntityCreatureAgeable target) { this.breedingTarget = target; }
	
    // ========== Create Child ==========
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
    	if(!this.creatureInfo.getClass().isAssignableFrom(EntityCreatureAgeable.class))
			return null;
    	return (EntityCreatureAgeable)this.creatureInfo.createEntity(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
    }
	
	// ========== Valid Partner ==========
	public boolean canBreedWith(EntityCreatureAgeable partner) {
		if(partner == this) return false;
		if(partner.getClass() != this.getClass()) return false;
		return this.isInLove() && partner.isInLove();
	}
	
	// ========== Love Check ==========
	public boolean isInLove() {
		return this.loveTime > 0;
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
	public void procreate(EntityCreatureAgeable partner) {
		EntityCreatureAgeable baby = this.createChild(partner);

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
                this.getEntityWorld().addParticle(ParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
            }

            this.onCreateBaby(partner, baby);

            this.getEntityWorld().addEntity(baby);
        }
    }

	public void onCreateBaby(EntityCreatureAgeable partner, EntityCreatureAgeable baby) {

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
	public void read(CompoundNBT nbtTagCompound) {
        super.read(nbtTagCompound);
        if(nbtTagCompound.contains("Age")) {
        	this.setGrowingAge(nbtTagCompound.getInt("Age"));
        }
        else {
        	this.setGrowingAge(0);
        }
        
        if(nbtTagCompound.contains("InLove")) {
        	this.loveTime = nbtTagCompound.getInt("InLove");
        }
        else {
        	this.loveTime = 0;
        }
        
        if(nbtTagCompound.contains("HasBeenFarmed")) {
        	if(nbtTagCompound.getBoolean("HasBeenFarmed")) {
        		this.setFarmed();
        	}
        }
    }
	
	// ========== Write ==========
    @Override
	public void writeAdditional(CompoundNBT nbtTagCompound) {
        super.writeAdditional(nbtTagCompound);
        nbtTagCompound.putInt("Age", this.getGrowingAge());
        nbtTagCompound.putInt("InLove", this.loveTime);
        nbtTagCompound.putBoolean("HasBeenFarmed", this.hasBeenFarmed);
    }
}
