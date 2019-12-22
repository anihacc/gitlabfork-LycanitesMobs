package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.entity.goals.actions.MateGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
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
    protected static final DataParameter<Integer> AGE = EntityDataManager.createKey(AgeableCreatureEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LOVE = EntityDataManager.createKey(AgeableCreatureEntity.class, DataSerializers.VARINT);
    
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public AgeableCreatureEntity(EntityType<? extends AgeableCreatureEntity> entityType, World world) {
		super(entityType, world);
	}

	// ========== Init ==========
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(AGE, 0);
		this.dataManager.register(LOVE, 0);
	}

	// ========== Init AI ==========
	@Override
	protected void registerGoals() {
		// Greater Actions:
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new MateGoal(this).setMateDistance(5.0D));

		super.registerGoals();

		// Lesser Targeting:
		this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindParentGoal(this).setSightCheck(false).setDistance(32.0D));

		// Lesser Actions:
		this.goalSelector.addGoal(this.nextTravelGoalIndex++, new FollowParentGoal(this).setSpeed(1.0D).setStrayDistance(3.0D));
	}
    
    // ========== Setup ==========
	@Override
    public void setupMob() {
        if(this.babySpawnChance > 0D && this.rand.nextDouble() < this.babySpawnChance)
        	this.setGrowingAge(growthTime);
        super.setupMob();
    }
    
    // ========== Name ==========
    @Override
    public ITextComponent getAgeName() {
    	if(this.isChild())
    		return new TranslationTextComponent("entity.baby");
    	else
    		return super.getAgeName();
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
	                this.getEntityWorld().addParticle(particle, this.getPositionVec().getX() + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.getPositionVec().getZ() + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
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
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, @Nonnull ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Item Commands:
    	if(!itemStack.isEmpty()) {
    		
    		// Spawn Egg:
    		if(itemStack.getItem() instanceof ItemCustomSpawnEgg) {
    			CreatureInfo creatureInfo = ((ItemCustomSpawnEgg)itemStack.getItem()).getCreatureInfo(itemStack);
    			if(creatureInfo == this.creatureInfo)
					commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Spawn Baby");
			}
    		
    		// Breeding Item:
    		if(this.isBreedingItem(itemStack) && this.canBreed() && !this.isInLove())
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Breed");
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public boolean performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Spawn Baby:
    	if(command.equals("Spawn Baby") && !this.getEntityWorld().isRemote && itemStack.getItem() instanceof ItemCustomSpawnEgg) {
            ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
			CreatureInfo spawnEggCreatureInfo = itemCustomSpawnEgg.getCreatureInfo(itemStack);
			if(spawnEggCreatureInfo != null) {
				if (spawnEggCreatureInfo.entityClass != null && spawnEggCreatureInfo.entityClass.isAssignableFrom(this.getClass())) {
					AgeableCreatureEntity baby = this.createChild(this);
					if (baby != null) {
						baby.setGrowingAge(baby.growthTime);
						baby.setLocationAndAngles(this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), 0.0F, 0.0F);
						baby.setFarmed();
						this.getEntityWorld().addEntity(baby);
						if (itemStack.hasDisplayName()) {
							baby.setCustomName(itemStack.getDisplayName());
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
	 * Returns if this creature should follow parents. By default only returns true if this creature is a baby.
	 * @return Returns true if parents should be searched for and followed.
	 */
	public boolean shouldFollowParent() {
		return this.isChild();
	}

	/**
	 * Returns if this creature should look for a parent to follow if it has none already.
	 * @return True if this creature should actively seek parents.
	 */
	public boolean shouldFindParent() {
		return this.isChild();
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
    @Override
    public boolean canBeTempted() {
    	return !this.isInLove() && super.canBeTempted();
    }

	// ========== Targets ==========
	public AgeableCreatureEntity getBreedingTarget() { return this.breedingTarget; }
	public void setBreedingTarget(AgeableCreatureEntity target) { this.breedingTarget = target; }
	
    // ========== Create Child ==========
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
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
		if(partner == this)
			return false;
		if(partner.getClass() != this.getClass())
			return false;
		if(this.getSubspecies() == null) {
			if(partner.getSubspecies() != null && partner.getSubspecies().skin != null) {
				return false;
			}
		}
		else if(this.getSubspecies().skin != null) {
			if(partner.getSubspecies() == null) {
				return false;
			}
			if(!this.getSubspecies().skin.equals(partner.getSubspecies().skin)) {
				return false;
			}
		}
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
            baby.setLocationAndAngles(this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), this.rotationYaw, this.rotationPitch);

            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.getEntityWorld().addParticle(ParticleTypes.HEART, this.getPositionVec().getX() + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.getPositionVec().getZ() + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
            }

            this.onCreateBaby(partner, baby);

            this.getEntityWorld().addEntity(baby);
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
	public void readAdditional(CompoundNBT nbtTagCompound) {
        super.readAdditional(nbtTagCompound);
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
