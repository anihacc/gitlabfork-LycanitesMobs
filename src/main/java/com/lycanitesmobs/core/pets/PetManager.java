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
    public Map<UUID, PetEntry> entries = new HashMap<>();
    /** Newly added PetEntries that need to be synced to the client player. **/
    public List<PetEntry> newEntries = new ArrayList<>();
    /** PetEntries that need to be removed. **/
    public List<PetEntry> removedEntries = new ArrayList<>();
    /** A map containing NBT Tag Compounds mapped to Pet Entry UUIDs. **/
    public Map<UUID, NBTTagCompound> entryNBTs = new HashMap<>();


	public PetManager(EntityLivingBase host) {
		this.host = host;
	}

    /** Returns true if the provided entry is in this manager. **/
    public boolean hasEntry(PetEntry petEntry) {
        return this.entries.containsKey(petEntry.petEntryID);
    }

    /** Adds a new PetEntry and executes onAdd() methods. The provided entry should have set whether it's a pet, mount, minion, etc. **/
    public void addEntry(PetEntry petEntry) {
        if(this.entries.containsKey(petEntry.petEntryID)) {
            LycanitesMobs.logWarning("", "[Pet Manager] Tried to add a Pet Entry that is already added!");
            return;
        }

        // Load From NBT:
        if(this.entryNBTs.containsKey(petEntry.petEntryID)) {
            petEntry.readFromNBT(this.entryNBTs.get(petEntry.petEntryID));
        }

        this.entries.put(petEntry.petEntryID, petEntry);
        petEntry.onAdd(this);
        this.newEntries.add(petEntry);
    }

    /** Removes an entry from this manager. This is called automatically if the entry itself is no longer active.
     * This will not cause the entry itself to become inactive if it is still active.
     * If an entry is finished, it is best to call onRemove() on the entry itself, this method will then be called automatically. **/
    public void removeEntry(PetEntry petEntry) {
        if(!this.entries.containsValue(petEntry)) {
            LycanitesMobs.logWarning("", "[Pet Manager] Tried to remove a pet entry that isn't added!");
            return;
        }

        this.entries.remove(petEntry.petEntryID);
    }

    /** Returns the requested pet entry from its specific id. **/
    public PetEntry getEntry(UUID id) {
        return this.entries.get(id);
    }

    /** Returns the requested entry list. **/
    public List<PetEntry> createEntryListByType(String type) {
        List<PetEntry> filteredEntries = new ArrayList<>();
        for(PetEntry petEntry : this.entries.values()) {
            if(type.equalsIgnoreCase(petEntry.getType())) {
                filteredEntries.add(petEntry);
            }
        }
        return filteredEntries;
    }

	/** Called by the host's entity update, runs any logic to manage pet entries. **/
	public void onUpdate(World world) {
        // Load NBT Entries:
	    if(!this.entryNBTs.isEmpty()) {
            // Have Currently Loaded Entries Read From The NBT Map:
            for (PetEntry petEntry : this.entries.values()) {
                if (this.entryNBTs.containsKey(petEntry.petEntryID)) {
                    petEntry.readFromNBT(this.entryNBTs.get(petEntry.petEntryID));
                    this.entryNBTs.remove(petEntry.petEntryID);
                }
            }

            // Create New Entries For Non-Loaded Entries From The NBT Map:
            for (NBTTagCompound nbtEntry : this.entryNBTs.values()) {
                if(this.host instanceof EntityPlayer && nbtEntry.getString("Type").equalsIgnoreCase("familiar")) { // Only load active familiars.
                    if(!PlayerFamiliars.INSTANCE.playerFamiliars.containsKey(this.host.getUniqueID()) ||
                            !PlayerFamiliars.INSTANCE.playerFamiliars.get(this.host.getUniqueID()).containsKey(nbtEntry.getUniqueId("UUID"))) {
                        continue;
                    }
                }
                PetEntry petEntry = new PetEntry(nbtEntry.getUniqueId("UUID"), nbtEntry.getString("Type"), this.host, nbtEntry.getString("SummonType"));
                petEntry.readFromNBT(nbtEntry);
                if (petEntry.active)
                    this.addEntry(petEntry);
            }

            this.entryNBTs.clear();
        }

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
		for(PetEntry petEntry : this.entries.values()) {

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
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Pet Entries from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if(!nbtTagCompound.hasKey("PetManager"))
            return;

        // Load All NBT Data Into The NBT Map:
        NBTTagList entryList = nbtTagCompound.getTagList("PetManager", 10);
        for(int i = 0; i < entryList.tagCount(); ++i) {
            NBTTagCompound nbtEntry = entryList.getCompoundTagAt(i);
            if(!nbtEntry.hasUniqueId("UUID") && nbtEntry.hasKey("EntryName")) { // Convert Pet Entries from older mod versions.
                LycanitesMobs.logInfo("", "[Pets] Converting Pet Entry NBT from older mod version: " + nbtEntry.getString("EntryName") + "...");
                nbtEntry.setUniqueId("UUID", UUID.randomUUID());
            }
            if(nbtEntry.hasUniqueId("UUID")) {
                this.entryNBTs.put(nbtEntry.getUniqueId("UUID"), nbtEntry);
            }
            else {
                LycanitesMobs.logWarning("", "[Pets] A Pet Entry was missing a UUID and EntryName, this is either a bug or NBT data has been tampered with, please report this!");
            }
        }
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        NBTTagList entryList = new NBTTagList();
        for(PetEntry petEntry : this.entries.values()) {
            NBTTagCompound nbtEntry = new NBTTagCompound();
            petEntry.writeToNBT(nbtEntry);
            entryList.appendTag(nbtEntry);
        }
        nbtTagCompound.setTag("PetManager", entryList);
    }
}
