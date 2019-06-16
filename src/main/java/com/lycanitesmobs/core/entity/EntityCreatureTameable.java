package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.goals.actions.SitGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EntityCreatureTameable extends EntityCreatureAgeable {
	
	// Stats:
	public float hunger = this.getCreatureHungerMax();
	public float stamina = this.getStaminaMax();
	public float staminaRecovery = 0.5F;
	public float sittingGuardRange = 16F;

    // Owner:
	public UUID ownerUUID;

	// AI:
	public SitGoal aiSit;

    // Datawatcher:
    protected static final DataParameter<Byte> TAMED = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.field_187191_a);
    protected static final DataParameter<Optional<UUID>> OWNER_ID = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.field_187203_m);
    protected static final DataParameter<Float> HUNGER = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.field_187193_c);
    protected static final DataParameter<Float> STAMINA = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.field_187193_c);

    /** Used for the TAMED WATCHER_ID, this holds a series of booleans that describe the tamed status as well as instructed behaviour. **/
	public static enum TAMED_ID {
		IS_TAMED((byte)1), MOVE_SIT((byte)2), MOVE_FOLLOW((byte)4),
		STANCE_PASSIVE((byte)8), STANCE_AGGRESSIVE((byte)16), STANCE_ASSIST((byte)32), PVP((byte)64);
		public final byte id;
	    TAMED_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureTameable(World world) {
		super(world);
	}
	
	// ========== Init ==========
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TAMED, (byte)0);
        this.dataManager.register(OWNER_ID, Optional.empty());
        this.dataManager.register(HUNGER, this.getCreatureHungerMax());
        this.dataManager.register(STAMINA, this.getStaminaMax());
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.aiSit = new SitGoal(this);
    }
    
    // ========== Name ==========
    @Override
    public ITextComponent getName() {
    	if(!this.isTamed() || !CreatureManager.getInstance().config.ownerTags)
    		return super.getName();
    	
    	String ownerName = this.getOwnerName();
    	String ownerSuffix = "'s ";
        if(ownerName != null && ownerName.length() > 0) {
            if ("s".equals(ownerName.substring(ownerName.length() - 1)) || "S".equals(ownerName.substring(ownerName.length() - 1)))
                ownerSuffix = "' ";
        }
    	String ownedName = ownerName + ownerSuffix + this.getFullName();
    	
    	if(this.hasCustomName())
    		return super.getName();// + " (" + ownedName + ")";
    	else
    		return new TranslationTextComponent(ownedName);
    }

	/**
	 * Returns true if this thing is named
	 */
	@Override
	public boolean hasCustomName() {
		if(!CreatureManager.getInstance().config.ownerTags) {
			return false;
		}
		return super.hasCustomName();
	}
    
    
    // ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawnNaturally() {
    	if(this.isTamed())
    		return false;
        return super.canDespawnNaturally();
    }
    
    @Override
    public boolean despawnCheck() {
        if(this.getEntityWorld().isRemote)
        	return false;

        // Bound Pet:
        if(this.getPetEntry() != null) {
			if(this.getPetEntry().entity != this && this.getPetEntry().entity != null)
				return true;
			if(this.getPetEntry().host == null || !this.getPetEntry().host.isAlive())
				return true;
		}

    	if(this.isTamed() && !this.isTemporary)
    		return false;
        return super.despawnCheck();
    }
    
    @Override
    public boolean isPersistant() {
    	return this.isTamed() || super.isPersistant();
    }
    
    
    // ==================================================
  	//                      Movement
  	// ==================================================
    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
	    if(this.isTamed() && player == this.getPlayerOwner())
	        return true;
	    return super.canBeLeashedTo(player);
    }
    
    // ========== Test Leash ==========
    @Override
    public void testLeash(float distance) {
    	if(this.isSitting() && distance > 10.0F)
    		this.clearLeashed(true, true);
    	else
    		super.testLeash(distance);
    }
    
    
    // ==================================================
    //                       Update
    // ==================================================
    @Override
    public void livingTick() {
    	super.livingTick();
    	this.staminaUpdate();
	}
    
    public void staminaUpdate() {
    	if(this.getEntityWorld().isRemote)
    		return;
    	if(this.stamina < this.getStaminaMax() && this.staminaRecovery >= this.getStaminaRecoveryMax() / 2)
    		this.setStamina(Math.min(this.stamina + this.staminaRecovery, this.getStaminaMax()));
    	if(this.staminaRecovery < this.getStaminaRecoveryMax())
    		this.staminaRecovery = Math.min(this.staminaRecovery + (this.getStaminaRecoveryMax() / this.getStaminaRecoveryWarmup()), this.getStaminaRecoveryMax());
    }


    // ==================================================
    //                       Interact
    // ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
		
		// Open GUI:
		if(!this.getEntityWorld().isRemote && this.isTamed() && (itemStack == null || player.isSneaking()) && player == this.getPlayerOwner())
			commands.put(COMMAND_PIORITIES.MAIN.id, "GUI");
    	
    	// Server Item Commands:
    	if(!this.getEntityWorld().isRemote && itemStack != null && !player.isSneaking()) {
    		
    		// Taming:
    		if(!this.isTamed() && isTamingItem(itemStack) && CreatureManager.getInstance().config.tamingEnabled)
    			commands.put(COMMAND_PIORITIES.IMPORTANT.id, "Tame");
    		
    		// Feeding:
    		if(this.isTamed() && this.isHealingItem(itemStack) && this.getHealth() < this.getMaxHealth())
                commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Feed");
    		
    		// Equipment:
    		if(this.isTamed() && !this.isChild() && this.canEquip() && player == this.getPlayerOwner()) {
	    		String equipSlot = this.inventory.getSlotForEquipment(itemStack);
	    		if(equipSlot != null && (this.inventory.getEquipmentStack(equipSlot) == null || this.inventory.getEquipmentStack(equipSlot).getItem() != itemStack.getItem()))
	    			commands.put(COMMAND_PIORITIES.EQUIPPING.id, "Equip Item");
    		}
    	}
		
		// Sit:
		//if(this.isTamed() && this.canSit() && player == this.getPlayerOwner() && !this.getEntityWorld().isRemote)
			//commands.put(CMD_PRIOR.MAIN.id, "Sit");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Open GUI:
    	if(command.equals("GUI")) {
    		this.playTameSound();
    		this.openGUI(player);
    	}
    	
    	// Tame:
    	if(command.equals("Tame")) {
    		this.tame(player);
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Feed:
    	if(command.equals("Feed")) {
    		int healAmount = 4;
    		if(itemStack.getItem().func_219971_r()) { // If item has food data
    			healAmount = itemStack.getItem().func_219967_s().func_221466_a(); // Get food data of item and get food heal amount.
    		}
    		this.heal((float)healAmount);
            this.playEatSound();
            if(this.getEntityWorld().isRemote) {
                IParticleData particle = ParticleTypes.HEART;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                for(int i = 0; i < 25; i++)
                	this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
            }
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Equip Armor:
    	if(command.equals("Equip Item")) {
    		ItemStack equippedItem = this.inventory.getEquipmentStack(this.inventory.getSlotForEquipment(itemStack));
    		if(equippedItem != null)
    			this.dropItem(equippedItem);
    		ItemStack equipStack = itemStack.copy();
    		equipStack.setCount(1);
    		this.inventory.setEquipmentStack(equipStack.copy());
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Sit:
    	if(command.equals("Sit")) {
    		this.playTameSound();
            this.setAttackTarget((LivingEntity)null);
            this.clearMovement();
        	this.setSitting(!this.isSitting());
            this.isJumping = false;
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
    
    // ========== Can Name Tag ==========
    @Override
    public boolean canNameTag(PlayerEntity player) {
    	if(!this.isTamed())
    		return super.canNameTag(player);
    	else if(this.isTamed() && player == this.getPlayerOwner())
    		return super.canNameTag(player);
    	return false;
    }
    
    // ========== Perform GUI Command ==========
    @Override
    public void performGUICommand(PlayerEntity player, byte guiCommandID) {
    	if(!this.petControlsEnabled())
    		return;
    	if(player != this.getOwner())
    		return;

    	// Pet Commands:
    	if(guiCommandID == PET_COMMAND_ID.PVP.id) {
			this.setPVP(!this.isPVP());
		}
		else if(guiCommandID == PET_COMMAND_ID.PASSIVE.id) {
			this.setPassive(true);
		}
		else if(guiCommandID == PET_COMMAND_ID.DEFENSIVE.id) {
			this.setPassive(false);
			this.setAssist(false);
			this.setAggressive(false);
		}
		else if(guiCommandID == PET_COMMAND_ID.ASSIST.id) {
			this.setPassive(false);
			this.setAssist(true);
			this.setAggressive(false);
		}
		else if(guiCommandID == PET_COMMAND_ID.AGGRESSIVE.id) {
			this.setPassive(false);
			this.setAssist(true);
			this.setAggressive(true);
		}
		else if(guiCommandID == PET_COMMAND_ID.FOLLOW.id) {
			this.setSitting(false);
			this.setFollowing(true);
		}
		else if(guiCommandID == PET_COMMAND_ID.WANDER.id) {
			this.setSitting(false);
			this.setFollowing(false);
		}
		else if(guiCommandID == PET_COMMAND_ID.SIT.id) {
			this.setSitting(true);
			this.setFollowing(false);
		}

		this.playTameSound();

        // Update Pet Entry Summon Set:
        if(this.petEntry != null && this.petEntry.summonSet != null) {
            this.petEntry.summonSet.updateBehaviour(this);
        }

    	super.performGUICommand(player, guiCommandID);
    }
    
    
    // ==================================================
    //                       Targets
    // ==================================================
    // ========== Teams ==========
    @Override
    public Team getTeam() {
        if(this.isTamed()) {
            Entity owner = this.getOwner();
            if(owner != null)
                return owner.getTeam();
        }
        return super.getTeam();
    }

	/**
	 * Returns if this creature is on the same team as the target entity. If PvP is disabled and this creature is tamed then it is considered on the same team as all players and their tames.
	 * @param target The entity to check teams with.
	 * @return True when on the same team.
	 */
	@Override
    public boolean isOnSameTeam(Entity target) {
		if(this.getEntityWorld().isRemote) {
			return super.isOnSameTeam(target);
		}

        if(this.isTamed()) {
        	// Check If Owner:
            if(target == this.getPlayerOwner() || target == this.getOwner())
                return true;

            // Check Player PvP:
			if(target instanceof PlayerEntity && (!this.getEntityWorld().getServer().isPVPEnabled() || !this.isPVP())) {
				return true;
			}

			// Check If Tameable:
            if(target instanceof EntityCreatureTameable) {
            	EntityCreatureTameable tamedTarget = (EntityCreatureTameable)target;
            	if(tamedTarget.isTamed()) {
            		if(!this.getEntityWorld().getServer().isPVPEnabled() || !this.isPVP() || tamedTarget.getPlayerOwner() == this.getPlayerOwner()) {
						return true;
					}
				}
            }
			else if(target instanceof TameableEntity) {
				TameableEntity tamedTarget = (TameableEntity)target;
				if(tamedTarget.getOwner() != null) {
					if(!this.getEntityWorld().getServer().isPVPEnabled() || !this.isPVP() || tamedTarget.getOwner() == this.getOwner()) {
						return true;
					}
				}
			}

			// Check Owner Teams:
			if(this.getPlayerOwner() != null) {
				if(this.getPlayerOwner().getRidingEntity() == target) {
					return true;
				}
				return this.getPlayerOwner().isOnSameTeam(target);
			}
            else if(this.getOwner() != null) {
				if(this.getOwner().getRidingEntity() == target) {
					return true;
				}
				return this.getOwner().isOnSameTeam(target);
			}

			return false;
        }
        return super.isOnSameTeam(target);
    }
    
    
    // ==================================================
    //                       Attacks
    // ==================================================
    // ========== Can Attack ==========
	@Override
	public boolean canAttack(EntityType targetType) {
		if(this.isPassive())
			return false;
		return super.canAttack(targetType);
	}
	
	@Override
	public boolean canAttack(LivingEntity targetEntity) {
		if(this.isPassive())
			return false;
		if(this.isTamed()) {
            if(this.getOwner() == targetEntity)
                return false;
            if(!this.getEntityWorld().isRemote) {
                boolean canPVP = this.getEntityWorld().getServer().isPVPEnabled() && this.isPVP();
                if(targetEntity instanceof PlayerEntity && !canPVP)
                    return false;
                if(targetEntity instanceof EntityCreatureTameable) {
                    EntityCreatureTameable targetTameable = (EntityCreatureTameable)targetEntity;
                    if(targetTameable.isTamed()) {
                        if(!canPVP)
                            return false;
                        if(targetTameable.getOwner() == this.getOwner())
                            return false;
                    }
                }
            }
        }
		return super.canAttack(targetEntity);
	}

    // ========= Get Damage Source ==========
    /**
     * Returns the damage source to be used by this mob when dealing damage.
     * @param nestedDamageSource This can be null or can be a passed entity damage source for all kinds of use, mainly for minion damage sources.
     * @return
     */
    @Override
    public DamageSource getDamageSource(EntityDamageSource nestedDamageSource) {
        if(this.isTamed() && this.getOwner() != null) {
            if(nestedDamageSource == null)
                nestedDamageSource = new EntityDamageSource("mob", this);
            return new MinionEntityDamageSource(nestedDamageSource, this.getOwner());
        }
        return super.getDamageSource(nestedDamageSource);
    }
	
    // ========== Attacked From ==========
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
        if (this.isInvulnerableTo(damageSrc))
            return false;
        else {
            if(!this.isPassive())
            	this.setSitting(false);

            Entity entity = damageSrc.getImmediateSource();
            if(entity instanceof ThrowableEntity)
            	entity = ((ThrowableEntity)entity).getThrower();
            
            if(this.isTamed() && this.getOwner() == entity)
            	return false;

            return super.attackEntityFrom(damageSrc, damage);
        }
    }


	// ==================================================
	//                     Immunities
	// ==================================================
	// ========== Damage ==========
	@Override
	public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
		if("inWall".equals(type) && this.isTamed())
			return false;
		return super.isInvulnerableTo(type, source, damage);
	}


	// ==================================================
	//                       Owner
	// ==================================================
	/**
	 * Sets the owner of this entity via the unique id of the owner entity. Also updates the tamed status of this entity.
	 * @param ownerUUID The owner entity UUID.
	 */
	public void setOwnerId(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
		this.dataManager.set(OWNER_ID, Optional.ofNullable(ownerUUID));
		this.setTamed(ownerUUID != null);
	}

	@Override
	public Entity getOwner() {
		UUID uuid = this.getOwnerId();
		if(uuid == null) {
			return super.getOwner();
		}
		return this.getEntityWorld().getPlayerByUuid(uuid);
	}

	/**
	 * Sets the owner of this entity to the provided player entity. Also updates the tamed status of this entity.
	 * @param player The player to become the owner.
	 */
	public void setPlayerOwner(PlayerEntity player) {
		this.setOwnerId(player.getUniqueID());
	}

	@Override
	public UUID getOwnerId() {
		if(this.getEntityWorld().isRemote) {
			try {
				return this.getUUIDFromDataManager(OWNER_ID).orElse(null);
			} catch (Exception ignored) {}
		}
		return this.ownerUUID;
	}

	/**
	 * Returns the owner of this entity as a player or null if there is no player owner. This is separated from getOwner and getOwnerId as they behave inconsistently when built.
	 * @return The player owner.
	 */
	public Entity getPlayerOwner() {
		if(this.getEntityWorld().isRemote) {
			Entity owner = this.getOwner();
			if(owner instanceof PlayerEntity) {
				return owner;
			}
			return null;
		}
		if(this.ownerUUID == null) {
			return null;
		}
		return this.getEntityWorld().getPlayerByUuid(this.ownerUUID);
	}

	/**
	 * Gets the display name of the entity that owns this entity or an empty string if none. TODO Maybe this hack is no longer needed now.
	 * @return The owner display name.
	 */
	public String getOwnerName() {
		Entity owner = this.getOwner();
		if(owner != null) {
			if(owner instanceof PlayerEntity) {
				return owner.getDisplayName().toString();
			}
			else {
				return owner.getDisplayName().toString();
			}
		}
		return "";
	}
    
    
    // ==================================================
    //                       Taming
    // ==================================================
	@Override
    public boolean isTamed() {
        try {
            return (this.getByteFromDataManager(TAMED) & TAMED_ID.IS_TAMED.id) != 0;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public void setTamed(boolean isTamed) {
        byte tamed = this.getByteFromDataManager(TAMED);
        if(isTamed) {
            this.dataManager.set(TAMED, (byte) (tamed | TAMED_ID.IS_TAMED.id));
            this.spawnEventType = "";
        }
        else {
            this.dataManager.set(TAMED, (byte) (tamed - (tamed & TAMED_ID.IS_TAMED.id)));
        }
        this.setCustomNameVisible(isTamed);
    }
    
    public boolean isTamingItem(ItemStack itemstack) {
		if(itemstack.isEmpty() || this.creatureInfo.creatureType == null) {
			return false;
		}

		if(itemstack.getItem() instanceof ItemTreat) {
			ItemTreat itemTreat = (ItemTreat)itemstack.getItem();
			if(itemTreat.getCreatureType() == this.creatureInfo.creatureType) {
				return this.creatureInfo.isTameable();
			}
		}

		return false;
    }
    
    // ========== Tame Entity ==========
	/**
	 * Attempts to tame this entity to the provided player.
	 * @param player The player taming this entity.
	 * @return True if the entity is tamed, false on failure.
	 */
    public boolean tame(PlayerEntity player) {
    	if(!this.getEntityWorld().isRemote && !this.isRareSubspecies()) {
			if (this.rand.nextInt(3) == 0) {
				this.setPlayerOwner(player);
				this.onTamedByPlayer();
				this.unsetTemporary();
				String tameMessage = LanguageManager.translate("message.pet.tamed");
				tameMessage = tameMessage.replace("%creature%", this.getSpeciesName());
				player.sendMessage(new TranslationTextComponent(tameMessage));
				this.playTameEffect(this.isTamed());
				player.addStat(ObjectManager.getStat(this.creatureInfo.getName() + ".tame"), 1);
				if (this.timeUntilPortal > this.getPortalCooldown()) {
					this.timeUntilPortal = this.getPortalCooldown();
				}
				ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
				if(extendedPlayer != null) {
					extendedPlayer.getBeastiary().discoverCreature(this, 2, false);
				}
			}
			else {
				String tameFailedMessage = LanguageManager.translate("message.pet.tamefail");
				tameFailedMessage = tameFailedMessage.replace("%creature%", this.getSpeciesName());
				player.sendMessage(new TranslationTextComponent(tameFailedMessage));
				this.playTameEffect(this.isTamed());
			}
		}
    	return this.isTamed();
    }

	/**
	 * Called when this creature is first tamed by a player, this clears movement, targets, etc and sets default the pet behaviour.
	 */
	public void onTamedByPlayer() {
		this.refreshStats();
		this.clearMovement();
		this.setAttackTarget(null);
		this.setSitting(false);
		this.setFollowing(true);
		this.setPassive(false);
		this.setAggressive(false);
		this.setPVP(true);
		this.playTameSound();
	}

	@Override
	public void onCreateBaby(EntityCreatureAgeable partner, EntityCreatureAgeable baby) {
		if(this.isTamed() && this.getOwner() instanceof PlayerEntity && partner instanceof EntityCreatureTameable && baby instanceof EntityCreatureTameable) {
			EntityCreatureTameable partnerTameable = (EntityCreatureTameable)partner;
			EntityCreatureTameable babyTameable = (EntityCreatureTameable)baby;
			if(partnerTameable.getPlayerOwner() == this.getPlayerOwner()) {
				babyTameable.setPlayerOwner((PlayerEntity)this.getOwner());
			}
		}
		super.onCreateBaby(partner, baby);
	}
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte status) {
        if(status == 7)
            this.playTameEffect(true);
        else if(status == 6)
            this.playTameEffect(false);
        else
            super.handleStatusUpdate(status);
    }
    
    // ========== Feeding Food ==========
    public boolean isHealingItem(ItemStack testStack) {
    	return false;
    }
    
    
    // ==================================================
    //                    Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    public byte behaviourBitMask() { return this.getByteFromDataManager(TAMED); }
    
    // ========== Sitting ==========
    public boolean isSitting() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.MOVE_SIT.id) != 0;
    }

    public void setSitting(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set) {
            this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.MOVE_SIT.id));
            this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, this.sittingGuardRange);
        }
        else {
            this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.MOVE_SIT.id)));
            this.detachHome();
        }
    }
    
    // ========== Following ==========
    public boolean isFollowing() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.MOVE_FOLLOW.id) != 0;
    }

    public void setFollowing(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.MOVE_FOLLOW.id));
        else
            this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.MOVE_FOLLOW.id)));
    }
    
    // ========== Passiveness ==========
    public boolean isPassive() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.STANCE_PASSIVE.id) != 0;
    }

    public void setPassive(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set) {
            this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.STANCE_PASSIVE.id));
            this.setAttackTarget(null);
            this.setStealth(0);
        }
        else
            this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.STANCE_PASSIVE.id)));
    }
    
    // ========== Agressiveness ==========
	@Override
    public boolean isAggressive() {
    	if(!this.isTamed())
    		return super.isAggressive();
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.STANCE_AGGRESSIVE.id) != 0;
    }

    public void setAggressive(boolean set) {
    	if(!this.petControlsEnabled()) {
			set = false;
		}
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.STANCE_AGGRESSIVE.id));
        else
            this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.STANCE_AGGRESSIVE.id)));
    }

	// ========== Assist ==========
	public boolean isAssisting() {
		if(!this.isTamed()) {
			return false;
		}
		return (this.getByteFromDataManager(TAMED) & TAMED_ID.STANCE_ASSIST.id) != 0;
	}

	public void setAssist(boolean set) {
		if(!this.petControlsEnabled()) {
			set = true;
		}
		byte tamedStatus = this.getByteFromDataManager(TAMED);
		if(set)
			this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.STANCE_ASSIST.id));
		else
			this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.STANCE_ASSIST.id)));
	}
    
    // ========== PvP ==========
    public boolean isPVP() {
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.PVP.id) != 0;
    }

    public void setPVP(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, (byte) (tamedStatus | TAMED_ID.PVP.id));
        else
            this.dataManager.set(TAMED, (byte) (tamedStatus - (tamedStatus & TAMED_ID.PVP.id)));
    }
    
    
    // ==================================================
    //                       Hunger
    // ==================================================
    public float getCreatureHunger() {
    	if(this.getEntityWorld() == null)
    		return this.getCreatureHungerMax();
    	if(!this.getEntityWorld().isRemote)
    		return this.hunger;
    	else {
            try {
                return this.getFloatFromDataManager(HUNGER);
            } catch (Exception e) {
                return 0;
            }
        }
    }
    
    public void setCreatureHunger(float setHunger) {
    	this.hunger = setHunger;
    }
    
    public float getCreatureHungerMax() {
    	return 20;
    }
    
    
    // ==================================================
    //                       Stamina
    // ==================================================
    public float getStamina() {
    	if(this.getEntityWorld() != null && this.getEntityWorld().isRemote) {
            try {
                this.stamina = this.getFloatFromDataManager(STAMINA);
            } catch (Exception e) {}
        }
    	return this.stamina;
    }
    
    public void setStamina(float setStamina) {
    	this.stamina = setStamina;
    	if(this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
    		this.dataManager.set(STAMINA, setStamina);
    	}
    }
    
    public float getStaminaMax() {
    	return 100;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1F;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 10 * 20;
    }
    
    public float getStaminaCost() {
    	return 1;
    }
    
    public void applyStaminaCost() {
    	float newStamina = this.getStamina() - this.getStaminaCost();
    	if(newStamina < 0)
    		newStamina = 0;
    	this.setStamina(newStamina);
    	this.staminaRecovery = 0;
    }
    
    // ========== GUI Feedback ==========
    public float getStaminaPercent() {
    	return this.getStamina() / this.getStaminaMax();
    }
    
    // "energy" = Usual blue-orange bar. "toggle" = Solid purple bar for on and off.
    public String getStaminaType() {
    	return "energy";
    }
    
    
    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
 	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
    	EntityCreatureAgeable spawnedBaby = super.createChild(baby);
    	UUID ownerId = this.getOwnerId();
    	if(ownerId != null && spawnedBaby != null && spawnedBaby instanceof EntityCreatureTameable) {
    		EntityCreatureTameable tamedBaby = (EntityCreatureTameable)spawnedBaby;
    		tamedBaby.setOwnerId(ownerId);
    	}
    	return spawnedBaby;
 	}
    
    
    // ==================================================
    //                     Abilities
    // ==================================================
    // =========== Movement ==========
    public boolean canBeTempted() { return !this.isTamed(); }

    /*@Override
    public boolean shouldDismountInWater(Entity rider) { TODO Possibly in EntityType as some obfuscated method?
        return false;
    }*/

	@Override
	public boolean canBeSteered() {
		return this.isTamed();
	}
    
    
    // ==================================================
    //                       Client
    // ==================================================
    protected void playTameEffect(boolean success) {
        IParticleData particle = ParticleTypes.HEART;
        if(!success)
        	particle = ParticleTypes.SMOKE;

        for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
        }
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Coloring ==========
    /**
     * Returns true if this mob can be dyed different colors. Usually for wool and collars.
     * @param player The player to check for when coloring, this is to stop players from dying other players pets. If provided with null it should return if this creature can be dyed in general.
     */
    @Override
    public boolean canBeColored(PlayerEntity player) {
    	if(player == null) return true;
    	return this.isTamed() && player == this.getPlayerOwner();
    }


    // ========== Boss Health Bar ==========
    public boolean showBossInfo() {
        if(this.isTamed())
            return false;
        return super.showBossInfo();
    }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    @Override
    public void read(CompoundNBT nbtTagCompound) {
        super.read(nbtTagCompound);

		// UUID NBT:
        if(nbtTagCompound.hasUniqueId("OwnerId")) {
            this.setOwnerId(nbtTagCompound.getUniqueId("OwnerId"));
        }
        else {
			this.setOwnerId(null);
        }
        
        if(nbtTagCompound.contains("Sitting")) {
	        this.setSitting(nbtTagCompound.getBoolean("Sitting"));
        }
        else {
        	this.setSitting(false);
        }
        
        if(nbtTagCompound.contains("Following")) {
	        this.setFollowing(nbtTagCompound.getBoolean("Following"));
        }
        else {
        	this.setFollowing(true);
        }
        
        if(nbtTagCompound.contains("Passive")) {
	        this.setPassive(nbtTagCompound.getBoolean("Passive"));
        }
        else {
        	this.setPassive(false);
        }
        
        if(nbtTagCompound.contains("Aggressive")) {
	        this.setAggressive(nbtTagCompound.getBoolean("Aggressive"));
        }
        else {
        	this.setAggressive(false);
        }
        
        if(nbtTagCompound.contains("PVP")) {
	        this.setPVP(nbtTagCompound.getBoolean("PVP"));
        }
        else {
        	this.setPVP(true);
        }
        
        if(nbtTagCompound.contains("Hunger")) {
        	this.setCreatureHunger(nbtTagCompound.getFloat("Hunger"));
        }
        else {
        	this.setCreatureHunger(this.getCreatureHungerMax());
        }
        
        if(nbtTagCompound.contains("Stamina")) {
        	this.setStamina(nbtTagCompound.getFloat("Stamina"));
        }
    }
    
    // ========== Write ==========
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
        super.writeAdditional(nbtTagCompound);
        if(this.getOwnerId() != null) {
            nbtTagCompound.putUniqueId("OwnerId", this.getOwnerId());
        }
        nbtTagCompound.putBoolean("Sitting", this.isSitting());
        nbtTagCompound.putBoolean("Following", this.isFollowing());
        nbtTagCompound.putBoolean("Passive", this.isPassive());
        nbtTagCompound.putBoolean("Aggressive", this.isAggressive());
        nbtTagCompound.putBoolean("PVP", this.isPVP());
        nbtTagCompound.putFloat("Hunger", this.getCreatureHunger());
        nbtTagCompound.putFloat("Stamina", this.getStamina());
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Get number of ticks, at least during which the living entity will be silent. **/
    @Override
    public int getTalkInterval() {
        if(this.isTamed())
            return 600;
        return super.getTalkInterval();
    }
    @Override
    protected SoundEvent getAmbientSound() {
    	String sound = "_say";
    	if(this.isTamed() && this.getHealth() < this.getMaxHealth())
    		sound = "_beg";
    	return AssetManager.getSound(this.getSoundName() + sound);
    }
    
    // ========== Tame ==========
    public void playTameSound() {
    	this.playSound(AssetManager.getSound(this.getSoundName() + "_tame"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
    
    // ========== Eat ==========
    public void playEatSound() {
    	this.playSound(AssetManager.getSound(this.getSoundName() + "_eat"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
