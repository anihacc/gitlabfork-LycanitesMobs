package com.lycanitesmobs.core.pets;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.*;

public class PetManager {
	public EntityLivingBase host;
    /** A list of all pet entries, useful for looking up everything summoned by an entity as well as ensuring that no entries are added as multiple types. **/
    public Map<UUID, PetEntry> allEntries = new HashMap<>();
    /** Pets are mobs that the player has tamed and then bound. They can be summoned and dismissed at will. Eg: Pet Lurker. **/
    public List<PetEntry> pets = new ArrayList<>();
    /** Mounts are mobs that the player has tamed and then bound. One can be summoned for riding at will, they will despawn if unmounted after a short while. Eg: Pet Ventoraptor. **/
	public List<PetEntry> mounts = new ArrayList<>();
    /** Minions are mobs that the player has summoned. These are temporary. Eg: Summoned Cinder. **/
	public List<PetEntry> minions = new ArrayList<>();
    /** Guardians are mobs that are bound to equipment/effects that the player has. They are passively summoned and dismissed based on various conditions. Eg: Cyclone Armor Reiver. **/
	public List<PetEntry> guardians = new ArrayList<>();
    /** Familiars are mobs that are bound to the player, they are similar to guardians but aren't dependant on any equipment/effects, etc. Eg: Donation Familiars. **/
	public List<PetEntry> familiars = new ArrayList<>();
    // I might also add slaves for mobs that are temporarily under the host's control who can break free, etc instead of despawning.

    /** Newly added PetEntries that need to be synced to the client player. **/
    public List<PetEntry> newEntries = new ArrayList<>();
    /** PetEntries that need to be removed. **/
    public List<PetEntry> removedEntries = new ArrayList<>();
    /** A map containing NBT Tag Compunds mapped to Unique Pet Entry Names. **/
    public Map<String, NBTTagCompound> entryNBTs = new HashMap<>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetManager(EntityLivingBase host) {
		this.host = host;
	}


    // ==================================================
    //                       Check
    // ==================================================
    /** Returns true if the provided entry is in this manager. **/
    public boolean hasEntry(PetEntry petEntry) {
        return this.allEntries.containsValue(petEntry);
    }


    // ==================================================
    //                        Add
    // ==================================================
    /** Adds a new PetEntry and executes onAdd() methods. The provided entry should have set whether it's a pet, mount, minion, etc. **/
    public void addEntry(PetEntry petEntry) {
        if(this.allEntries.containsValue(petEntry)) {
            LycanitesMobs.logWarning("", "[Pet Manager] Tried to add a Pet Entry that is already added!");
            return;
        }

        // Load From NBT:
        if(this.entryNBTs.containsKey(petEntry.name)) {
            petEntry.readFromNBT(this.entryNBTs.get(petEntry.name));
        }

        this.allEntries.put(petEntry.petEntryID, petEntry);

        this.pets.remove(petEntry);
        this.mounts.remove(petEntry);
        this.minions.remove(petEntry);
        this.guardians.remove(petEntry);
        this.familiars.remove(petEntry);

        if("pet".equalsIgnoreCase(petEntry.getType()))
            this.pets.add(petEntry);
        else if("mount".equalsIgnoreCase(petEntry.getType()))
            this.mounts.add(petEntry);
        else if("minion".equalsIgnoreCase(petEntry.getType()))
            this.minions.add(petEntry);
        else if("guardian".equalsIgnoreCase(petEntry.getType()))
            this.guardians.add(petEntry);
        else if("familiar".equalsIgnoreCase(petEntry.getType()))
            this.familiars.add(petEntry);

        petEntry.onAdd(this);
        this.newEntries.add(petEntry);
    }


    // ==================================================
    //                      Remove
    // ==================================================
    /** Removes an entry from this manager. This is called automatically if the entry itself is no longer active.
     * This will not cause the entry itself to become inactive if it is still active.
     * If an entry is finished, it is best to call onRemove() on the entry itself, this method will then be called automatically. **/
    public void removeEntry(PetEntry petEntry) {
        if(!this.allEntries.containsValue(petEntry)) {
            LycanitesMobs.logWarning("", "[Pet Manager] Tried to remove a pet entry that isn't added!");
            return;
        }

        this.allEntries.remove(petEntry.petEntryID);
        this.pets.remove(petEntry);
        this.mounts.remove(petEntry);
        this.minions.remove(petEntry);
        this.guardians.remove(petEntry);
        this.familiars.remove(petEntry);
    }

    /** Clears an entire list of entries, best used for clean loads. **/
    public void clearEntries(String type) {
        if("pet".equalsIgnoreCase(type)) {
            for(PetEntry petEntry : this.pets)
                this.allEntries.remove(petEntry.petEntryID);
            this.pets = new ArrayList<>();
        }
        else if("mount".equalsIgnoreCase(type)) {
            for(PetEntry petEntry : this.mounts)
                this.allEntries.remove(petEntry.petEntryID);
            this.mounts = new ArrayList<>();
        }
        else if("minion".equalsIgnoreCase(type)) {
            for(PetEntry petEntry : this.minions)
                this.allEntries.remove(petEntry.petEntryID);
            this.minions = new ArrayList<>();
        }
        else if("guardian".equalsIgnoreCase(type)) {
            for(PetEntry petEntry : this.guardians)
                this.allEntries.remove(petEntry.petEntryID);
            this.guardians = new ArrayList<>();
        }
        else if("familiar".equalsIgnoreCase(type)) {
            for(PetEntry petEntry : this.familiars)
                this.allEntries.remove(petEntry.petEntryID);
            this.familiars = new ArrayList<>();
        }
    }
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the host's entity update, runs any logic to manage pet entries. **/
	public void onUpdate(World world) {

        // New Entries:
        if(this.newEntries.size() > 0) {
            if (!world.isRemote && this.host instanceof EntityPlayer) {
                for (PetEntry petEntry : this.newEntries) {
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer) this.host);
                    if (playerExt != null)
                        playerExt.sendPetEntryToPlayer(petEntry);
                }
            }
            this.newEntries = new ArrayList<>();
        }

        // Entry Updates:
        int newSpiritReserved = 0;
		for(PetEntry petEntry : this.allEntries.values()) {

            // Owner Check:
            if(petEntry.host != this.host)
                petEntry.setOwner(this.host);

            // Pet and Mount Spirit Check:
            if(this.host instanceof EntityPlayer) {
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
                if(playerExt != null && petEntry.usesSpirit()) {
                    int spiritCost = petEntry.getSpiritCost();
                    if(petEntry.spawningActive && petEntry.active) {
                        newSpiritReserved += spiritCost;
                        if((playerExt.spirit + playerExt.spiritReserved < newSpiritReserved) || newSpiritReserved > playerExt.spiritMax) {
                            petEntry.spawningActive = false;
                            newSpiritReserved -= spiritCost;
                        }
                    }
                }
            }

            if(petEntry.active)
			    petEntry.onUpdate(world);
            else
                this.removedEntries.add(petEntry);
		}

        // Remove Inactive Entries:
        if(this.removedEntries.size() > 0) {
            for(PetEntry petEntry : this.removedEntries) {
                this.removeEntry(petEntry);
            }
            this.removedEntries = new ArrayList<>();
        }

        // Spirit Reserved:
        if(this.host instanceof EntityPlayer) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
            if(playerExt != null)
                playerExt.spiritReserved = newSpiritReserved;
        }
	}


    // ==================================================
    //                        Get
    // ==================================================
    /** Returns the requested pet entry from its specific id. **/
    public PetEntry getEntry(UUID id) {
        return this.allEntries.get(id);
    }

    /** Returns the requested pet entry from the specified type by id. **/
    public PetEntry getEntry(String type, int id) {
        return this.getEntryList(type).get(id);
    }

    /** Returns the requested entry list. **/
    public List<PetEntry> getEntryList(String type) {
        if("pet".equalsIgnoreCase(type))
            return this.pets;
        else if("mount".equalsIgnoreCase(type))
            return this.mounts;
        else if("minion".equalsIgnoreCase(type))
            return this.minions;
        else if("guardian".equalsIgnoreCase(type))
            return this.guardians;
        else if("familiar".equalsIgnoreCase(type))
            return this.familiars;
        return null;
    }


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Pet Entries from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if(!nbtTagCompound.hasKey("PetManager"))
            return;
        this.entryNBTs = new HashMap<String, NBTTagCompound>();

        // Load All NBT Data Into The Map:
        NBTTagList entryList = nbtTagCompound.getTagList("PetManager", 10);
        for(int i = 0; i < entryList.tagCount(); ++i) {
            NBTTagCompound nbtEntry = (NBTTagCompound)entryList.getCompoundTagAt(i);
            if(nbtEntry.hasKey("EntryName"))
                this.entryNBTs.put(nbtEntry.getString("EntryName"), nbtEntry);
        }

        // Have All Entries In Use Read From The Map:
        for(PetEntry petEntry : this.allEntries.values()) {
            if(this.entryNBTs.containsKey(petEntry.name)) {
                petEntry.readFromNBT(this.entryNBTs.get(petEntry.name));
                this.entryNBTs.remove(petEntry.name);
            }
        }

        // Create New Entries For Pets and Mounts:
        for(NBTTagCompound nbtEntry : this.entryNBTs.values()) {
            if(nbtEntry.hasKey("Type") && ("pet".equalsIgnoreCase(nbtEntry.getString("Type")) || "mount".equalsIgnoreCase(nbtEntry.getString("Type")))) {
                UUID petEntryID;
                if(nbtEntry.hasKey("UUID"))
                    petEntryID = nbtEntry.getUniqueId("UUID");
                else
                    petEntryID = UUID.randomUUID();
                PetEntry petEntry = new PetEntry(petEntryID, nbtEntry.getString("EntryName"), nbtEntry.getString("Type"), this.host, nbtEntry.getString("SummonType"));
                petEntry.readFromNBT(nbtEntry);
                if(petEntry.active)
                    this.addEntry(petEntry);
            }
        }
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        List<String> writtenEntries = new ArrayList<String>();
        NBTTagList entryList = new NBTTagList();

        // Save Entries In Use:
        for(PetEntry petEntry : this.allEntries.values()) {
            if(!petEntry.active)
                continue;
            NBTTagCompound nbtEntry = new NBTTagCompound();
            petEntry.writeToNBT(nbtEntry);
            entryList.appendTag(nbtEntry);
            writtenEntries.add(petEntry.name);
        }

        // Update Saved Entries Not In Use:
//        for(Map.Entry<String, NBTTagCompound> entryNBTSet : this.entryNBTs.entrySet()) {
//            if(!writtenEntries.contains(entryNBTSet.getKey())) {
//                entryList.appendTag(entryNBTSet.getValue());
//            }
//        }

        nbtTagCompound.setTag("PetManager", entryList);
    }
}
