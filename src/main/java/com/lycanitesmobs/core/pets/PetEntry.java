package com.lycanitesmobs.core.pets;


import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.UUID;

public class PetEntry {
    /** The unique name for this entry, used when reading and writing NBT Data. If it is an empty string "" or null then NBT Data will not be stored. **/
    public String name;
    /** The Pet Manager that this entry is added to, this can also be null. **/
    public PetManager petManager;
    /** The ID given to this entry by the PetManager. **/
    public int petEntryID;
    /** The type of entry this is. This should really always be set if this entry is to be added to a manager. This should only be set when the entry is instantiated and then kept the same. **/
    private String type;
    /** This is set to false if this entry has been removed. Used by PetManagers to auto-remove finished temporary entries. **/
    public boolean active = true;

    /** A timer used to count down to 0 for respawning. **/
    public int respawnTime = 0;
    /** The amount of time until respawn. **/
    public int respawnTimeMax;
    /** True if the entity has died and must wait to respawn. **/
    public boolean isRespawning = false;
    /** Counts how many times this entry has summoned its entity. **/
    public int spawnCount = 0;
    /** If true, this entry and it's entity will be marked as temporary where once the entity is gone, it will not respawn. The minion type sets this to true. **/
    public boolean temporary = false;
    /** For temporary entities, this will set how the long the entity will last before it despawns. **/
    public int temporaryDuration = 5 * 20;
    /** True if this entry should keep its entity spawned/respawned. False if the entity should be removed and not spawned. This can be turned on and off (such as for familiars). **/
    public boolean spawningActive = true;

    /** The entity that this entry belongs to. **/
    public LivingEntity host;
    /** The summon set to use when spawning, etc. **/
    public SummonSet summonSet;
    /** The current entity instance that this entry is using. **/
    public Entity entity;
    /** The current entity NBT data. **/
    public CompoundNBT entityNBT;
    /** Entity update tick, this counts up each tick as the entity is spawned and active and is paused when the entity is inactive. **/
    public int entityTick = 0;
    /** Entity Health **/
    public float entityHealth = 1;
    /** Entity Max Health **/
    public float entityMaxHealth = 1;

    /** The name to use for the entity. Leave empty/null "" for no name. **/
    public String entityName = "";
    /** The Subspecies ID to use for the entity. **/
    public int subspeciesID = 0;
    /** The size scale to use for the entity. **/
    public double entitySize = 1.0D;
    /** Coloring for this entity such as collar coloring. **/
    public String color = "000000";

    /** If true, a teleport has been requested to teleport the entity (if active) to the host entity. **/
    public boolean teleportEntity = false;

    /** If true, a release is pending where the player must confirm the release. This will not release the entity, instead the player must confirm that. Only used client side. **/
    public boolean releaseEntity = false;

    // ==================================================
    //                 Create from Entity
    // ==================================================
    /** Returns a new PetEntry based off the provided entity for the provided player. **/
    public static PetEntry createFromEntity(PlayerEntity player, BaseCreatureEntity entity, String petType) {
        CreatureInfo creatureInfo = entity.creatureInfo;
        String entryName = petType + "-" + player.getName() + "-" + creatureInfo.getName() + "-" + UUID.randomUUID().toString();
        PetEntry petEntry = new PetEntry(entryName, petType, player, creatureInfo.getName());
        if(entity.hasCustomName()) {
			petEntry.setEntityName(entity.getCustomName().toString());
		}
        petEntry.setEntitySubspeciesID(entity.getSubspeciesIndex());
        petEntry.setEntitySize(entity.sizeScale);
        petEntry.setColor("000000");
        return petEntry;
    }

	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntry(String name, String type, LivingEntity host, String summonType) {
        this.name = name;
        this.type = type;
        this.host = host;

        ExtendedPlayer playerExt = null;
        if(host != null && host instanceof PlayerEntity)
            playerExt = ExtendedPlayer.getForPlayer((PlayerEntity)host);
        this.summonSet = new SummonSet(playerExt);
        this.summonSet.summonableOnly = false;
        this.summonSet.setSummonType(summonType);

        this.respawnTimeMax = 5 * 60 * 20;
        if("minion".equalsIgnoreCase(this.type))
            this.temporary = true;
	}


    // ==================================================
    //                     Set Values
    // ==================================================
    public PetEntry setEntityName(String name) {
        this.entityName = name;
        return this;
    }

    public PetEntry setEntitySubspeciesID(int id) {
        this.subspeciesID = id;
        return this;
    }

    public PetEntry setEntitySize(double size) {
        this.entitySize = size;
        return this;
    }

    public PetEntry setColor(String color) {
        this.color = color;
        return this;
    }

    public PetEntry setOwner(LivingEntity owner) {
        this.host = owner;
        if(host != null && host instanceof PlayerEntity) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((PlayerEntity) host);
            this.summonSet.playerExt = playerExt;
        }
        return this;
    }

    /** Used to set whether this PetEntry spawns mobs, this will also take a Spirit cost if Spirit is used (server side). use a direct spawningActive = true/false to avoid the Spirit cost. **/
    public PetEntry setSpawningActive(boolean spawningActive) {
        if(this.spawningActive == spawningActive)
            return this;
        if(!this.host.getEntityWorld().isRemote) {
            if(!spawningActive)
                this.despawnEntity();
            else if(this.usesSpirit() && this.summonSet.playerExt != null) {
                if(this.summonSet.playerExt.spirit < this.getSpiritCost()) {
                    this.spawningActive = false;
                    return this;
                }
                this.summonSet.playerExt.spirit -= this.getSpiritCost();
                this.summonSet.playerExt.spiritReserved += this.getSpiritCost();
            }
        }
        this.spawningActive = spawningActive;
        return this;
    }


    // ==================================================
    //                     Get Values
    // ==================================================
    public CreatureInfo getCreatureInfo() {
        if(this.summonSet == null || "".equals(this.summonSet.summonType))
            return null;
        return CreatureManager.getInstance().getCreature(this.summonSet.summonType);
    }

    public float getHealth() {
        if(this.entity != null && this.entity instanceof LivingEntity)
            this.entityHealth = ((LivingEntity)this.entity).getHealth();
        return this.entityHealth;
    }

    public float getMaxHealth() {
        if(this.entity != null && this.entity instanceof LivingEntity)
            this.entityMaxHealth = ((LivingEntity)this.entity).getMaxHealth();
        return this.entityMaxHealth;
    }


    // ==================================================
    //                     Copy Entry
    // ==================================================
    /** Makes this entry copy all information from another entry, useful for updating entries. Does not copy over the owner, ID or entry name and will only copy the SummonSet's summon type. **/
    public void copy(PetEntry copyEntry) {
        this.setEntityName(copyEntry.entityName);
        this.setEntitySubspeciesID(copyEntry.subspeciesID);
        this.setEntitySize(copyEntry.entitySize);
        this.setColor(copyEntry.color);
        if(copyEntry.summonSet != null)
            this.summonSet.setSummonType(copyEntry.summonSet.summonType);
    }


    // ==================================================
    //                       Name
    // ==================================================
    public String getName() {
        return this.name;
    }

    public ITextComponent getDisplayName() {
        ITextComponent displayName = this.summonSet.getCreatureInfo().getTitle();
        if(this.entityName != null && !"".equals(this.entityName)) {
            displayName = new StringTextComponent(this.entityName + " (")
                    .appendSibling(displayName)
                    .appendText(")");
        }
        return displayName;
    }


    // ==================================================
    //                       On Add
    // ==================================================
    /** Called when this entry is first added. A Pet Manager is passed if added to one, otherwise null. **/
    public void onAdd(PetManager petManager, int petEntryID) {
        this.petManager = petManager;
        this.petEntryID = petEntryID;
    }


    // ==================================================
    //                       Remove
    // ==================================================
    /** Called when this entry is finished and should be removed. Note: The PetManager will auto remove any inactive entries it might have. **/
    public void remove() {
        this.setSpawningActive(false);
        this.active = false;
    }
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the PetManager, runs any logic for this entry. This is normally called from an entity update. **/
	public void onUpdate(World world) {
        // Client Side Update:
        if(world.isRemote) {
            if(this.isRespawning && this.respawnTime > 0) {
                this.respawnTime--;
            }
            return;
        }

        // Active Checks:
		if(!this.active)
            return;
        if(!this.isActive()) {
            this.remove();
            return;
        }

        // Active Spawning:
        if(this.spawningActive) {
            // Dead Check:
            if(this.entity != null && !this.entity.isAlive()) {
                this.saveEntityNBT();
                this.entity = null;
                this.isRespawning = true;
                this.respawnTime = this.respawnTimeMax;
                if(this.summonSet.playerExt != null)
                    this.summonSet.playerExt.sendPetEntryToPlayer(this);
            }

            // No Entity:
            if(this.entity == null) {
                // Respawn:
                if(!this.isRespawning)
                    this.respawnTime = 0;
                if(this.respawnTime-- <= 0) {
                    this.spawnEntity();
                    this.isRespawning = false;
                }
            }

            // Entity Update:
            if(this.entity != null) {
                this.entityTick++;

                // Teleport Entity:
                try {
                    if (this.teleportEntity) {
                        if (this.entity.getEntityWorld() != this.host.getEntityWorld())
                            this.entity.changeDimension(this.host.getEntityWorld().getDimension().getType());
                        this.entity.setPosition(this.host.posX, this.host.posY, this.host.posZ);
                    }
                }
                catch(Exception e) {
                    LycanitesMobs.logDebug("Pet", "Unable to teleport a pet.");
                }

                if(entity instanceof LivingEntity) {
                    LivingEntity entityLiving = (LivingEntity)this.entity;
                    if(this.entityTick % 20 == 0 && entityLiving.getHealth() < entityLiving.getMaxHealth())
                        entityLiving.setHealth(Math.min(entityLiving.getHealth() + 1, entityLiving.getMaxHealth()));
                    this.entityHealth = entityLiving.getHealth();
                    this.entityMaxHealth = entityLiving.getMaxHealth();
                }

                if(entity.hasCustomName()) {
                	this.entityName = this.entity.getCustomName().toString();
				}
            }
        }

        // Inactive Spawning:
        else {
            // Remove Entity If Spawned:
            if(this.entity != null) {
                this.saveEntityNBT();
                this.entity.remove();
                this.entity = null;
            }

            // Count Down Respawn Timer If Active:
            if(this.respawnTime > 0)
                this.respawnTime--;
        }

        this.teleportEntity = false;
	}


    // ==================================================
    //                 On Behaviour Update
    // ==================================================
    /** Called when this entry's entity behaviour has been changed by the client. **/
    public void onBehaviourUpdate() {
        if(this.entity != null && this.entity instanceof TameableCreatureEntity)
            this.summonSet.applyBehaviour((TameableCreatureEntity)this.entity);
    }


    // ==================================================
    //                    Active Check
    // ==================================================
    /** Called every update, if this returns false this entry will call onRemove(). **/
    public boolean isActive() {
        if(this.entity == null && this.temporary && this.spawnCount > 0)
            return false;
        return true;
    }


    // ==================================================
    //                    Spawn Entity
    // ==================================================
    /** Spawns and sets this entry's entity if it isn't active already. **/
    public void spawnEntity() {
        if(this.entity != null || this.host == null)
            return;
        try {
            this.entity = (Entity)this.summonSet.getCreatureClass().getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.host.getEntityWorld()});
        }
        catch (Exception e) {
            LycanitesMobs.logWarning("Pets", "[Pet Entry] Unable to find an entity class for pet entry. " + " Type: " + this.summonSet.summonType + " Class: " + this.summonSet.getCreatureClass() + " Name: " + this.name);
            //e.printStackTrace();
        }

        if(this.entity == null)
            return;

        // Load NBT Data:
        this.loadEntityNBT();

        // Spawn Location:
        this.entity.setLocationAndAngles(this.host.posX, this.host.posY, this.host.posZ, this.host.rotationYaw, 0.0F);

        if(this.entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity entityCreature = (BaseCreatureEntity)this.entity;
            entityCreature.setMinion(true);
            entityCreature.setPetEntry(this);

            if(entityCreature instanceof TameableCreatureEntity) {
                TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                this.summonSet.applyBehaviour(entityTameable);
            }

            // Better Spawn Location and Angle:
            float randomAngle = 45F + (45F * this.host.getRNG().nextFloat());
            if(this.host.getRNG().nextBoolean())
                randomAngle = -randomAngle;
            BlockPos spawnPos = entityCreature.getFacingPosition(this.host, -1, randomAngle);
            if(this.entity.getEntityWorld().getBlockState(spawnPos).canEntitySpawn(this.entity.getEntityWorld(), spawnPos, this.entity.getType()))
                this.entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), this.host.rotationYaw, 0.0F);
            else {
                spawnPos = entityCreature.getFacingPosition(this.host, -1, -randomAngle);
                if(this.entity.getEntityWorld().getBlockState(spawnPos).canEntitySpawn(this.entity.getEntityWorld(), spawnPos, this.entity.getType()))
                    this.entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), this.host.rotationYaw, 0.0F);
            }

            // Temporary:
            if(this.temporary)
                entityCreature.setTemporary(this.temporaryDuration);

            // Entity Name and Appearance:
            if(this.entityName != null && !"".equals(this.entityName)) {
				entityCreature.setCustomName(new TranslationTextComponent(this.entityName));
			}
            entityCreature.setSizeScale(this.entitySize);
            entityCreature.applySubspecies(this.subspeciesID);

            // Tamed Behaviour:
            if(entityCreature instanceof TameableCreatureEntity && this.host instanceof PlayerEntity) {
                TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                entityTameable.setPlayerOwner((PlayerEntity)this.host);
                this.summonSet.applyBehaviour(entityTameable);
            }
        }

        this.spawnCount++;

        // Respawn with half health:
        if(this.entity instanceof LivingEntity && this.isRespawning) {
            LivingEntity entityLiving = (LivingEntity)this.entity;
            entityLiving.setHealth(entityLiving.getMaxHealth() / 2);
        }

        this.onSpawnEntity(this.entity);
        this.host.getEntityWorld().addEntity(this.entity);
    }

    /** Called when the entity for this entry is spawned just before it is added to the world. **/
    public void onSpawnEntity(Entity entity) {
        // This can be used on extensions of this class for NBT data, etc.
    }


    // ==================================================
    //                    Despawn Entity
    // ==================================================
    /** Despawns this entry's entity if it isn't already. This entry will still be active even if the entity is despawned so that it may be spawned again in the future. **/
    public void despawnEntity() {
        if(this.entity == null)
            return;
        this.onDespawnEntity(this.entity);
        this.saveEntityNBT();
        this.entity.remove();
        this.entity = null;
    }

    /** Called when the entity for this entry is despawned. **/
    public void onDespawnEntity(Entity entity) {
        // This can be used on extensions of this class for NBT data, etc.
    }


    // ==================================================
    //                    Assign Entity
    // ==================================================
    /** Connects this PetEntry to the provided entity. If there is already an entity attached then the attached entity will be despawned. **/
    public void assignEntity(Entity entity) {
        if(this.entity != null)
            this.despawnEntity();
        this.setSpawningActive(true);
        this.entity = entity;

        if(this.entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity entityCreature = (BaseCreatureEntity) this.entity;
            entityCreature.setMinion(true);
            entityCreature.setPetEntry(this);

            if(entityCreature instanceof TameableCreatureEntity) {
                TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                this.summonSet.updateBehaviour(entityTameable);
            }

            if(this.temporary)
                entityCreature.setTemporary(this.temporaryDuration);

            if(this.entityName != null && !"".equals(this.entityName))
                entityCreature.setCustomName(new TranslationTextComponent(this.entityName));
            entityCreature.setSizeScale(this.entitySize);
            entityCreature.applySubspecies(this.subspeciesID);
        }

        this.spawnCount++;
        this.saveEntityNBT();
        this.onSpawnEntity(this.entity);
    }


    // ==================================================
    //                    Get Type
    // ==================================================
    /** Returns the type of this entry. This should always be accurate else PetManagers could have inactive entries stuck in their lists! **/
    public String getType() {
        return this.type;
    }


    // ==================================================
    //                    Spirit Cost
    // ==================================================
    /** Returns true if this PetEntry uses spirit to summon. **/
    public boolean usesSpirit() {
        return "pet".equals(this.getType()) || "mount".equals(this.getType());
    }

    /** Returns the spirit cost of this entity. **/
    public int getSpiritCost() {
        if(this.summonSet.playerExt == null)
            return 0;
        return this.summonSet.playerExt.spiritCharge * this.getCreatureInfo().summonCost;
    }


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads pet entry from NBTTag. Should be called by PetManagers or other classes that store PetEntries and NBT Data for them. **/
    public void readFromNBT(CompoundNBT nbtTagCompound) {
        if(nbtTagCompound.contains("Active"))
            this.active = nbtTagCompound.getBoolean("Active");
        if(nbtTagCompound.contains("RespawnTime"))
            this.respawnTime = nbtTagCompound.getInt("RespawnTime");
        if(nbtTagCompound.contains("Respawning"))
            this.isRespawning = nbtTagCompound.getBoolean("Respawning");
        if(nbtTagCompound.contains("SpawningActive"))
            this.spawningActive = nbtTagCompound.getBoolean("SpawningActive");

        this.summonSet.read(nbtTagCompound);

        // Load Entity:
        if(nbtTagCompound.contains("EntityName"))
            this.setEntityName(nbtTagCompound.getString("EntityName"));
        if(nbtTagCompound.contains("SubspeciesID"))
            this.setEntitySubspeciesID(nbtTagCompound.getInt("SubspeciesID"));
        if(nbtTagCompound.contains("EntitySize"))
            this.setEntitySize(nbtTagCompound.getDouble("EntitySize"));
        if(nbtTagCompound.contains("Color"))
            this.setColor(nbtTagCompound.getString("Color"));
        if(nbtTagCompound.contains("EntityNBT"))
            this.entityNBT = nbtTagCompound.getCompound("EntityNBT");
    }

    // ========== Write ==========
    /** Writes pet entry to NBTTag. **/
    public void writeToNBT(CompoundNBT nbtTagCompound) {
        if(this.name == null || "".equals(this.name))
            return;
        nbtTagCompound.putBoolean("Active", this.active);
        nbtTagCompound.putInt("RespawnTime", this.respawnTime);
        nbtTagCompound.putBoolean("Respawning", this.isRespawning);
        nbtTagCompound.putBoolean("SpawningActive", this.spawningActive);

        nbtTagCompound.putInt("ID", this.petEntryID);
        nbtTagCompound.putString("EntryName", this.name);
        nbtTagCompound.putString("Type", this.getType());
        this.summonSet.write(nbtTagCompound);

        // Save Entity:
        if (this.usesSpirit()) {
            this.saveEntityNBT();
            nbtTagCompound.putString("EntityName", this.entityName);
            nbtTagCompound.putInt("SubspeciesID", this.subspeciesID);
            nbtTagCompound.putDouble("EntitySize", this.entitySize);
            nbtTagCompound.putString("Color", this.color);
            nbtTagCompound.put("EntityNBT", this.entityNBT);
        }
    }

    // ========== Save Entity NBT ==========
    /** If this PetEntry currently has an active entity, this will save that entity's NBT data to this PetEntry's record of it. **/
    public void saveEntityNBT() {
    	if(this.entity == null) {
    		return;
		}

        if(this.entityNBT == null) {
			this.entityNBT = new CompoundNBT();
		}

		// Creature Base:
        if(this.entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)this.entity;

            baseCreatureEntity.inventory.write(this.entityNBT);

            CompoundNBT extTagCompound = new CompoundNBT();
            baseCreatureEntity.extraMobBehaviour.write(extTagCompound);
            this.entityNBT.put("ExtraBehaviour", extTagCompound);

            if(this.entity instanceof AgeableCreatureEntity) {
                AgeableCreatureEntity ageableCreatureEntity = (AgeableCreatureEntity)this.entity;
                this.entityNBT.putInt("Age", ageableCreatureEntity.getGrowingAge());
            }
        }

        // Update Pet Name:
		if(this.entity instanceof BaseCreatureEntity && this.entity.hasCustomName()) {
			this.entityName = this.entity.getCustomName().toString();
		}
		this.entity.writeWithoutTypeId(this.entityNBT);
    }

    // ========== Load Entity NBT ==========
    /** If this PetEntry is spawning a new entity, this will load any saved entity NBT data onto it. **/
    public void loadEntityNBT() {
        if(this.entity == null || this.entityNBT == null)
            return;
        if(this.entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)this.entity;

            baseCreatureEntity.inventory.read(this.entityNBT);

            if(this.entityNBT.contains("ExtraBehaviour"))
                baseCreatureEntity.extraMobBehaviour.read(this.entityNBT.getCompound("ExtraBehaviour"));

            if(this.entity instanceof AgeableCreatureEntity) {
                AgeableCreatureEntity ageableCreatureEntity = (AgeableCreatureEntity)this.entity;
                if(this.entityNBT.contains("Age"))
                    ageableCreatureEntity.setGrowingAge(this.entityNBT.getInt("Age"));
                else
                    ageableCreatureEntity.setGrowingAge(0);
            }
        }
//        this.entity.readFromNBT(this.entityNBT);
    }
}
