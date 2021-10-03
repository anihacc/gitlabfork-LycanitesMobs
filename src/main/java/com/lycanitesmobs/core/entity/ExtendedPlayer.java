package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.temp.ItemStaffSummoning;
import com.lycanitesmobs.core.network.*;
import com.lycanitesmobs.core.pets.PlayerFamiliars;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import com.lycanitesmobs.client.localisation.LanguageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtendedPlayer implements IExtendedPlayer {
    public static Map<EntityPlayer, ExtendedPlayer> clientExtendedPlayers = new HashMap<>();
	public static Map<UUID, NBTTagCompound> backupNBTTags = new HashMap<>();
	
	// Player Info and Containers:
	public EntityPlayer player;
	public Beastiary beastiary;
	public PetManager petManager;
	public long timePlayed = 0;

	// Beastiary Menu:
	public CreatureType selectedCreatureType;
	public CreatureInfo selectedCreature;
	public int selectedSubspecies = 0;
	public int selectedVariant = 0;
	public int selectedPetType = 0;
	public PetEntry selectedPet;

	public long currentTick = 0;
	public boolean needsFullSync = true;
	/** Set for a few seconds after a player breaks a block. **/
	public IBlockState justBrokenBlock;
	/** How many ticks left until the justBrokenBlock should be cleared. **/
	protected int justBrokenClearTime;
	
	// Action Controls:
	public byte controlStates = 0;
	public static enum CONTROL_ID {
		JUMP((byte)1), MOUNT_DISMOUNT((byte)2), MOUNT_ABILITY((byte)4), MOUNT_INVENTORY((byte)8), ATTACK((byte)16), DESCEND((byte)32), RIGHT_MOUSE((byte)64);
		public byte id;
		CONTROL_ID(byte i) { id = i; }
	}
    public boolean hasAttacked = false; // If true, this entity has attacked this tick.

	// Spirit:
	public int spiritCharge = 100;
	public int spiritMax = (this.spiritCharge * 10);
	public int spirit = this.spiritMax;
	public int spiritReserved = 0;
	public int spiritRecharge = 10;
	
	// Summoning:
	public int selectedSummonSet = 1;
	public int summonFocusCharge = 600;
	public int summonFocusMax = (this.summonFocusCharge * 10);
	public int summonFocus = this.summonFocusMax;
	public int summonFocusRecharge = 10;
	public Map<Integer, SummonSet> summonSets = new HashMap<>();
	public int summonSetMax = 5;
	public PortalEntity staffPortal;

	// Creature Studying:
	public int creatureStudyCooldown = 0;
	public int creatureStudyCooldownMax = 200;

    // Initial Setup:
    private boolean initialSetup = false;
	
	// ==================================================
    //                   Get for Player
    // ==================================================
	public static ExtendedPlayer getForPlayer(EntityPlayer player) {
		if(player == null) {
			//LycanitesMobs.logWarning("", "Tried to access an ExtendedPlayer from a null EntityPlayer.");
			return null;
		}

        // Client Side:
        if(player.getEntityWorld() != null && player.getEntityWorld().isRemote) {
            if(clientExtendedPlayers.containsKey(player)) {
                ExtendedPlayer extendedPlayer = clientExtendedPlayers.get(player);
                extendedPlayer.setPlayer(player);
                return extendedPlayer;
            }
            ExtendedPlayer extendedPlayer = new ExtendedPlayer();
            extendedPlayer.setPlayer(player);
            clientExtendedPlayers.put(player, extendedPlayer);
            LycanitesMobs.packetHandler.sendToServer(new MessageSyncRequest());
        }

        // Server Side:
        IExtendedPlayer iExtendedPlayer = player.getCapability(LycanitesMobs.EXTENDED_PLAYER, null);
        if(!(iExtendedPlayer instanceof ExtendedPlayer))
            return null;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)iExtendedPlayer;
        if(extendedPlayer.getPlayer() != player)
            extendedPlayer.setPlayer(player);
        return extendedPlayer;
	}


    // ==================================================
    //                    Constructor
    // ==================================================
    public ExtendedPlayer() {
        this.beastiary = new Beastiary(this);
        this.petManager = new PetManager(this.player);

        this.spiritRecharge = LycanitesMobs.config.getInt("Player", "Spirit Recharge", this.spiritRecharge, "How much spirit a player regains per second. Default is 10, was 1 in earlier versions.");
		this.summonFocusRecharge = LycanitesMobs.config.getInt("Player", "Summoning Focus Recharge", this.summonFocusRecharge, "How much summoning focus a player regains per second. Default is 10, was 1 in earlier versions.");
		this.creatureStudyCooldownMax = CreatureManager.getInstance().config.creatureStudyCooldown;
    }
	
	
	// ==================================================
    //                    Player Entity
    // ==================================================
    /** Called when the player entity is being cloned, backups all data so that it can be loaded into a new ExtendedPlayer for the clone. **/
    public void backupPlayer() {
        if(this.player != null && !this.player.getEntityWorld().isRemote) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            this.writeNBT(nbtTagCompound);
            backupNBTTags.put(this.player.getUniqueID(), nbtTagCompound);
        }
    }

    /** Initially sets the player entity and loads any backup data, if the entity is being cloned from another call backupPlayer() instead so that the clone's ExtendedPlayer can load it. **/
	public void setPlayer(EntityPlayer player) {
        this.player = player;
        this.petManager.host = player;
        if(this.player.getEntityWorld().isRemote)
            return;
        if(backupNBTTags.containsKey(this.player.getUniqueID())) {
            this.readNBT(backupNBTTags.get(this.player.getUniqueID()));
            backupNBTTags.remove(this.player.getUniqueID());
        }
	}

    public EntityPlayer getPlayer() {
        return this.player;
    }

    /** Returns true if the provided entity is within melee attack range and is considered large. This is used for when the vanilla attack range fails on big entities. **/
    public boolean canMeleeBigEntity(Entity targetEntity) {
        if(!(targetEntity instanceof EntityLivingBase))
            return false;
        float targetWidth = targetEntity.width;
        float targetHeight = targetEntity.height;
        if(targetEntity instanceof BaseCreatureEntity) {
        	BaseCreatureEntity targetCreature = (BaseCreatureEntity)targetEntity;
        	targetWidth *= targetCreature.hitAreaWidthScale;
			targetHeight *= targetCreature.hitAreaHeightScale;
		}
        if(targetWidth <= 4 && targetHeight <= 4)
            return false;
        double heightOffset = this.player.posY - targetEntity.posY;
        double heightCompensation = 0;
        if(heightOffset > 0)
            heightCompensation = Math.min(heightOffset, targetHeight);
        double distance = Math.sqrt(this.player.getDistance(targetEntity));
        double range = 6 + heightCompensation + (targetWidth / 2);
        return distance <= range;
    }

    /** Makes this player attempt to melee attack. This is typically used for when the vanilla attack range fails on big entities. **/
    public void meleeAttack(Entity targetEntity) {
        if(!this.hasAttacked && !this.player.getHeldItemMainhand().isEmpty() && this.canMeleeBigEntity(targetEntity)) {
            this.player.attackTargetEntityWithCurrentItem(targetEntity);
            this.player.resetCooldown();
            this.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the EventListener, runs any logic on the main player entity's main update loop. **/
	public void onUpdate() {
		this.timePlayed++;
        this.hasAttacked = false;
		boolean creative = this.player.capabilities.isCreativeMode;

		// Stats:
		boolean sync = false;
		if(this.justBrokenClearTime > 0) {
			if(--this.justBrokenClearTime <= 0) {
				this.justBrokenBlock = null;
			}
		}

		// Spirit Stat Update:
		this.spirit = Math.min(Math.max(this.spirit, 0), this.spiritMax - this.spiritReserved);
		if(this.spirit < this.spiritMax - this.spiritReserved) {
			this.spirit += this.spiritRecharge;
			if(!this.player.getEntityWorld().isRemote && this.currentTick % 20 == 0 || this.spirit == this.spiritMax - this.spiritReserved) {
				sync = true;
			}
		}

		// Summoning Focus Stat Update:
		this.summonFocus = Math.min(Math.max(this.summonFocus, 0), this.summonFocusMax);
		if(this.summonFocus < this.summonFocusMax) {
			this.summonFocus += this.summonFocusRecharge;
			if(!this.player.getEntityWorld().isRemote && !creative && this.currentTick % 20 == 0
					|| this.summonFocus < this.summonFocusMax
					|| (!this.player.getHeldItemMainhand().isEmpty() && this.player.getHeldItemMainhand().getItem() instanceof ItemStaffSummoning)
                    || (!this.player.getHeldItemOffhand().isEmpty() && this.player.getHeldItemOffhand().getItem() instanceof ItemStaffSummoning)) {
				sync = true;
			}
		}

		// Creature Study Cooldown Update:
		if (this.creatureStudyCooldown > 0) {
			this.creatureStudyCooldown--;
			sync = true;
		}

		// Sync Stats To Client:
		if(!this.player.getEntityWorld().isRemote) {
			if(sync) {
				MessagePlayerStats message = new MessagePlayerStats(this);
				LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP) this.player);
			}
		}

		// Initial Setup:
		if(!this.initialSetup) {

			// Pet Manager Setup:
			if (!this.player.getEntityWorld().isRemote) {
				this.loadFamiliars();
			}

			// Mod Version Check:
			if (this.player.getEntityWorld().isRemote) {
				VersionChecker.VersionInfo latestVersion = VersionChecker.INSTANCE.getLatestVersion();
				if (latestVersion != null && latestVersion.isNewer && VersionChecker.INSTANCE.enabled) {
					this.player.sendMessage(new TextComponentString(LanguageManager.translate("lyc.version.newer").replace("{current}", LycanitesMobs.versionNumber).replace("{latest}", latestVersion.versionNumber)));
				}
			}

			this.initialSetup = true;
		}
		
		// Initial Network Sync:
		if(!this.player.getEntityWorld().isRemote && this.needsFullSync) {
			this.beastiary.sendAllToClient();
			this.sendAllSummonSetsToPlayer();
			MessageSummonSetSelection message = new MessageSummonSetSelection(this);
			LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
			this.sendPetEntriesToPlayer("");
		}

        // Pet Manager:
        this.petManager.onUpdate(this.player.getEntityWorld());
		
		this.currentTick++;
		this.needsFullSync = false;

		// Right Click Fix:
		if (!this.isControlActive(CONTROL_ID.RIGHT_MOUSE)) {
			if (this.player.getHeldItemMainhand().getItem() instanceof ItemEquipment || this.player.getHeldItemOffhand().getItem() instanceof ItemEquipment) {
				this.player.stopActiveHand();
			}
		}
	}

	/**
	 * Sets the block that the player has just broken and resets the clear timer.
	 * @param blockState The block state that the player has just broken.
	 */
	public void setJustBrokenBlock(IBlockState blockState) {
		this.justBrokenBlock = blockState;
		this.justBrokenClearTime = 60;
	}
	
	
	// ==================================================
    //                      Summoning
    // ==================================================
	public SummonSet getSummonSet(int setID) {
		if(setID <= 0) {
			LycanitesMobs.logWarning("", "Attempted to access set " + setID + " but the minimum ID is 1. Player: " + this.player);
			return this.getSummonSet(1);
		}
		else if(setID > this.summonSetMax) {
			LycanitesMobs.logWarning("", "Attempted to access set " + setID + " but the maximum set ID is " + this.summonSetMax + ". Player: " + this.player);
			return this.getSummonSet(this.summonSetMax);
		}
		if(!this.summonSets.containsKey(setID)) {
			this.summonSets.put(setID, new SummonSet(this));
		}
		return this.summonSets.get(setID);
	}

	public SummonSet getSelectedSummonSet() {
		//if(this.selectedSummonSet != this.validateSummonSetID(this.selectedSummonSet))
			//this.setSelectedSummonSet(this.selectedSummonSet); // This is a fail safe and shouldn't really happen, it will fix the current set ID if it is invalid, resending packets too.
		return this.getSummonSet(this.selectedSummonSet);
	}

	public void setSelectedSummonSet(int targetSetID) {
		//targetSetID = validateSummonSetID(targetSetID);
		this.selectedSummonSet = targetSetID;
	}
	
	/** Use to make sure that the target summoning set ID is valid, it will return it if it is or the best next set ID if it isn't. **/
	public int validateSummonSetID(int targetSetID) {
		targetSetID = Math.max(Math.min(targetSetID, this.summonSetMax), 1);
		while(!this.getSummonSet(targetSetID).isUseable() && targetSetID > 1 && !"".equals(this.getSummonSet(targetSetID).summonType)) {
			targetSetID--;
		}
		return targetSetID;
	}
	
	
	// ==================================================
    //                    Beastiary
    // ==================================================
	/** Returns the player's beastiary, will also update the client, access the beastiary variable directly when loading NBT data as the network player is null at first. **/
	public Beastiary getBeastiary() {
		return this.beastiary;
	}


	public void loadFamiliars() {
		Map<UUID, PetEntry> playerFamiliars = PlayerFamiliars.INSTANCE.getFamiliarsForPlayer(this.player);
		if (!playerFamiliars.isEmpty()) {
			for (PetEntry petEntry : playerFamiliars.values()) {
				if (this.petManager.hasEntry(petEntry)) {
					PetEntry currentFamiliarEntry = this.petManager.getEntry(petEntry.petEntryID);
					currentFamiliarEntry.copy(petEntry);
				}
				else {
					this.petManager.addEntry(petEntry);
					petEntry.entity = null;
				}
			}
			this.sendPetEntriesToPlayer("familiar");
		}
	}

	public boolean studyCreature(Entity entity, int experience) {
		if(this.creatureStudyCooldown > 0 || !(entity instanceof BaseCreatureEntity)) {
			return false;
		}
		BaseCreatureEntity creature = (BaseCreatureEntity)entity;
		if (this.beastiary.addCreatureKnowledge(creature, creature.scaleKnowledgeExperience(experience))) {
			this.creatureStudyCooldown = this.creatureStudyCooldownMax;
			return true;
		}
		return false;
	}
	
	
	// ==================================================
    //                      Death
    // ==================================================
	public void onDeath() {

	}
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	public void sendPetEntriesToPlayer(String entryType) {
		if(this.player.getEntityWorld().isRemote) return;
		LycanitesMobs.logDebug("Packets", "Sending all pet entries to client.");
		for(PetEntry petEntry : this.petManager.entries.values()) {
            if(entryType.equals(petEntry.getType()) || "".equals(entryType)) {
                MessagePetEntry message = new MessagePetEntry(this, petEntry);
                LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
            }
		}
	}

    public void sendPetEntryToPlayer(PetEntry petEntry) {
        if(this.player.getEntityWorld().isRemote) return;
        MessagePetEntry message = new MessagePetEntry(this, petEntry);
        LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
    }

	public void sendPetEntryRemoveToPlayer(PetEntry petEntry) {
		if(this.player.getEntityWorld().isRemote) return;
		MessagePetEntryRemove message = new MessagePetEntryRemove(this, petEntry);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
	}

	public void sendPetEntryToServer(PetEntry petEntry) {
		if(!this.player.getEntityWorld().isRemote) return;
        MessagePetEntry message = new MessagePetEntry(this, petEntry);
		LycanitesMobs.packetHandler.sendToServer(message);
	}

	public void sendPetEntryRemoveRequest(PetEntry petEntry) {
		if(!this.player.getEntityWorld().isRemote) return;
		petEntry.remove();
		MessagePetEntryRemove message = new MessagePetEntryRemove(this, petEntry);
		LycanitesMobs.packetHandler.sendToServer(message);
	}

    public void sendAllSummonSetsToPlayer() {
        if(this.player.getEntityWorld().isRemote) return;
		LycanitesMobs.logDebug("Packets", "Sending all summoning sets to client.");
        for(byte setID = 1; setID <= this.summonSetMax; setID++) {
            MessageSummonSet message = new MessageSummonSet(this, setID);
            LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.player);
        }
    }

    public void sendSummonSetToServer(byte setID) {
        if(!this.player.getEntityWorld().isRemote) return;
        MessageSummonSet message = new MessageSummonSet(this, setID);
        LycanitesMobs.packetHandler.sendToServer(message);
    }
	
	
	// ==================================================
    //                     Controls
    // ==================================================
	public void updateControlStates(byte controlStates) {
		this.controlStates = controlStates;
	}
	
	public boolean isControlActive(CONTROL_ID controlID) {
		return (this.controlStates & controlID.id) > 0;
	}
	
	
	// ==================================================
    //                 Request GUI Data
    // ==================================================
	public void requestGUI(byte guiID) {
		if(guiID == GuiHandler.Beastiary.PETS.id)
			this.sendPetEntriesToPlayer("pet");
		if(guiID == GuiHandler.Beastiary.PETS.id)
			this.sendPetEntriesToPlayer("mount");
        if(guiID == GuiHandler.Beastiary.PETS.id)
            this.sendPetEntriesToPlayer("familiar");
	}


	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag("LycanitesMobsPlayer");

    	this.beastiary.readFromNBT(extTagCompound);
        this.petManager.readFromNBT(extTagCompound);

		if(extTagCompound.hasKey("SummonFocus"))
			this.summonFocus = extTagCompound.getInteger("SummonFocus");

		if(extTagCompound.hasKey("Spirit"))
			this.spirit = extTagCompound.getInteger("Spirit");

		if(extTagCompound.hasKey("CreatureStudyCooldown"))
			this.creatureStudyCooldown = extTagCompound.getInteger("CreatureStudyCooldown");

		if(extTagCompound.hasKey("SelectedSummonSet"))
			this.selectedSummonSet = extTagCompound.getInteger("SelectedSummonSet");

		if(extTagCompound.hasKey("SummonSets")) {
			NBTTagList nbtSummonSets = extTagCompound.getTagList("SummonSets", 10);
			for(int setID = 0; setID < this.summonSetMax; setID++) {
				NBTTagCompound nbtSummonSet = (NBTTagCompound)nbtSummonSets.getCompoundTagAt(setID);
				SummonSet summonSet = new SummonSet(this);
				summonSet.readFromNBT(nbtSummonSet);
				this.summonSets.put(setID + 1, summonSet);
			}
		}

		if(extTagCompound.hasKey("TimePlayed"))
			this.timePlayed = extTagCompound.getLong("TimePlayed");
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = new NBTTagCompound();

    	this.beastiary.writeToNBT(extTagCompound);
		this.petManager.writeToNBT(extTagCompound);

		extTagCompound.setInteger("SummonFocus", this.summonFocus);
		extTagCompound.setInteger("Spirit", this.spirit);
		extTagCompound.setInteger("CreatureStudyCooldown", this.creatureStudyCooldown);
		extTagCompound.setInteger("SelectedSummonSet", this.selectedSummonSet);
		extTagCompound.setLong("TimePlayed", this.timePlayed);

		NBTTagList nbtSummonSets = new NBTTagList();
		for(int setID = 0; setID < this.summonSetMax; setID++) {
			NBTTagCompound nbtSummonSet = new NBTTagCompound();
			SummonSet summonSet = this.getSummonSet(setID + 1);
			summonSet.writeToNBT(nbtSummonSet);
			nbtSummonSets.appendTag(nbtSummonSet);
		}
		extTagCompound.setTag("SummonSets", nbtSummonSets);

    	nbtTagCompound.setTag("LycanitesMobsPlayer", extTagCompound);
    }
}
