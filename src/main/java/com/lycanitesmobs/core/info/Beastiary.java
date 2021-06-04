package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.FearEntity;
import com.lycanitesmobs.core.network.MessageBeastiary;
import com.lycanitesmobs.core.network.MessageCreatureKnowledge;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import com.lycanitesmobs.client.localisation.LanguageManager;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Beastiary {
	public ExtendedPlayer extendedPlayer;
	public Map<String, CreatureKnowledge> creatureKnowledgeList = new HashMap<>();

	/**
	 * Constructor
	 * @param extendedPlayer The Extended Player this Beastiary belongs to.
	 */
	public Beastiary(ExtendedPlayer extendedPlayer) {
		this.extendedPlayer = extendedPlayer;
	}
	
	
    // ==================================================
    //                     Knowledge
    // ==================================================
	/**
	 * Adds Creature Knowledge to this Beastiary after checking rank, etc.
	 * @param newKnowledge The new knowledge to add.
	 * @return The increase of knowledge rank or 0 on failure.
	 */
	public boolean addCreatureKnowledge(CreatureKnowledge newKnowledge, boolean sendToClient) {
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(newKnowledge.creatureName);
		if(creatureInfo == null)
			return false;
		if(creatureInfo.dummy)
			return false;

		CreatureKnowledge currentKnowledge = this.getCreatureKnowledge(creatureInfo.getName());
		if(currentKnowledge != null) {
			currentKnowledge.rank = newKnowledge.rank;
			currentKnowledge.experience = newKnowledge.experience;
			if (sendToClient) {
				this.sendToClient(currentKnowledge);
			}
			return true;
		}

		this.creatureKnowledgeList.put(newKnowledge.creatureName, newKnowledge);
		if (sendToClient) {
			this.sendAddedMessage(newKnowledge);
			this.sendToClient(newKnowledge);
		}
		return true;
	}


	/**
	 * Attempt to add Creature Knowledge to this Beastiary based on the provided entity and sends feedback to the player.
	 * @param entity The entity being discovered.
	 * @param experience The Knowledge experience being gained.
	 * @return True if new knowledge is gained and false if not.
	 */
	public boolean addCreatureKnowledge(Entity entity, int experience) {
		// Invalid Entity:
		if(!(entity instanceof BaseCreatureEntity)) {
			if (!this.extendedPlayer.player.getEntityWorld().isRemote) {
				this.extendedPlayer.player.sendMessage(new TextComponentString(LanguageManager.translate("message.beastiary.unknown")));
			}
			return false;
		}
		if(entity instanceof FearEntity) {
			return false;
		}

		CreatureInfo creatureInfo = ((BaseCreatureEntity)entity).creatureInfo;
		CreatureKnowledge newKnowledge = this.getCreatureKnowledge(creatureInfo.getName());
		if (newKnowledge == null) {
			newKnowledge = new CreatureKnowledge(this.extendedPlayer.getBeastiary(), creatureInfo.getName(), 1, experience);
		}
		else {
			if (newKnowledge.getMaxExperience() <= 0) {
				return false;
			}
			newKnowledge.addExperience(experience);
		}
		this.addCreatureKnowledge(newKnowledge, true);

		return true;
	}


	/**
	 * Sends a message to the player on gaining Creature Knowledge.
	 * @param creatureKnowledge The creature knowledge that was added.
	 */
	public void sendAddedMessage(CreatureKnowledge creatureKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote || !CreatureManager.getInstance().config.beastiaryKnowledgeMessages) {
			return;
		}
		CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
		String message = LanguageManager.translate("message.beastiary.new");
		message = message.replace("%creature%", creatureInfo.getTitle());
		message = message.replace("%rank%", "" + creatureKnowledge.rank);
		this.extendedPlayer.player.sendMessage(new TextComponentString(message));

		if(creatureInfo.isSummonable()) {
			String summonMessage = LanguageManager.translate("message.beastiary.summonable");
			if(creatureKnowledge.rank >= 3) {
				summonMessage = LanguageManager.translate("message.beastiary.summonable.skins");
			}
			else if(creatureKnowledge.rank == 2) {
				summonMessage = LanguageManager.translate("message.beastiary.summonable.colors");
			}
			summonMessage = summonMessage.replace("%creature%", creatureInfo.getTitle());
			this.extendedPlayer.player.sendMessage(new TextComponentString(summonMessage));
		}

		if(creatureInfo.isTameable() && creatureKnowledge.rank == 2) {
			String tameMessage = LanguageManager.translate("message.beastiary.tameable");
			tameMessage = tameMessage.replace("%creature%", creatureInfo.getTitle());
			this.extendedPlayer.player.sendMessage(new TextComponentString(tameMessage));
		}
	}


	/**
	 * Sends a message to the player if they attempt to add a creature that they already know.
	 * @param creatureKnowledge The creature knowledge that was trying to be added.
	 */
	public void sendKnownMessage(CreatureKnowledge creatureKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			return;
		}
		CreatureInfo creatureInfo = creatureKnowledge.getCreatureInfo();
		CreatureKnowledge currentKnowledge = this.extendedPlayer.getBeastiary().getCreatureKnowledge(creatureInfo.getName());
		String message = LanguageManager.translate("message.beastiary.known");
		message = message.replace("%creature%", creatureInfo.getTitle());
		message = message.replace("%rank%", "" + currentKnowledge.rank);
		this.extendedPlayer.player.sendMessage(new TextComponentString(message));
	}


	/**
	 * Returns the current knowledge of the provided creature. Use CreatureKnowledge.rank to get the current rank of knowledge the player has.
	 * @param creatureName The name of the creature to get the knowledge of.
	 * @return The creature knowledge or knowledge if there is no knowledge.
	 */
	
	public CreatureKnowledge getCreatureKnowledge(String creatureName) {
		if(!this.creatureKnowledgeList.containsKey(creatureName)) {
			return null;
		}
		return this.creatureKnowledgeList.get(creatureName);
	}


	/**
	 * Returns if this Beastiary has the provided knowledge rank or higher.
	 * @param creatureName The name of the creature to check the knowledge rank of.
	 * @param rank The minimum knowledge rank required.
	 * @return True if the knowledge rank is met or exceeded.
	 */
	public boolean hasKnowledgeRank(String creatureName, int rank) {
		CreatureKnowledge creatureKnowledge = this.getCreatureKnowledge(creatureName);
		if(creatureKnowledge == null) {
			return false;
		}
		return creatureKnowledge.rank >= rank;
	}

	
	/**
	 * Returns how many creatures of the specified creature type the player has descovered.
	 * @param creatureType Creature Type to check with.
	 * @return True if the player has at least one creature form the specific creature type.
	 */
	public int getCreaturesDiscovered(CreatureType creatureType) {
		if(this.creatureKnowledgeList.size() == 0) {
			return 0;
		}

		int creaturesDescovered = 0;
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : this.creatureKnowledgeList.entrySet()) {
			if(creatureKnowledgeEntry.getValue() != null) {
				if (creatureKnowledgeEntry.getValue().getCreatureInfo().creatureType == creatureType) {
					creaturesDescovered++;
				}
			}
		}
		return creaturesDescovered;
	}
	
	
    // ==================================================
    //                     Summoning
    // ==================================================
	public Map<Integer, String> getSummonableList() {
		Map<Integer, String> minionList = new HashMap<>();
		int minionIndex = 0;
		for(String minionName : this.creatureKnowledgeList.keySet()) {
			if(SummonSet.isSummonableCreature(minionName)) {
				minionList.put(minionIndex++, minionName);
			}
		}
		return minionList;
	}
	
	
	// ==================================================
    //                    Network Sync
    // ==================================================
	/** Sends CreatureKnowledge to the client. For when it's added or changed server side but needs updated client side. **/
	public void sendToClient(CreatureKnowledge newKnowledge) {
		if(this.extendedPlayer.player.getEntityWorld().isRemote) {
			return;
		}
		MessageCreatureKnowledge message = new MessageCreatureKnowledge(newKnowledge);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
	}
	
	/** Sends the whole Beastiary progress to the client, use sparingly! **/
	public void sendAllToClient() {
		LycanitesMobs.logDebug("Packets", "Sending all beastiary to client.");
		MessageBeastiary message = new MessageBeastiary(this);
		LycanitesMobs.packetHandler.sendToPlayer(message, (EntityPlayerMP)this.extendedPlayer.getPlayer());
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	if(!nbtTagCompound.hasKey("CreatureKnowledge"))
    		return;
    	this.creatureKnowledgeList.clear();
    	NBTTagList knowledgeList = nbtTagCompound.getTagList("CreatureKnowledge", 10);
    	for(int i = 0; i < knowledgeList.tagCount(); ++i) {
	    	NBTTagCompound nbtKnowledge = knowledgeList.getCompoundTagAt(i);
    		if(nbtKnowledge.hasKey("CreatureName")) {
    			String creatureName = nbtKnowledge.getString("CreatureName");
				int rank = 0;
				if(nbtKnowledge.hasKey("Rank")) {
					rank = nbtKnowledge.getInteger("Rank");
				}
				int experience = 0;
				if(nbtKnowledge.hasKey("Experience")) {
					experience = nbtKnowledge.getInteger("Experience");
				}
	    		CreatureKnowledge creatureKnowledge = new CreatureKnowledge(
                        this,
	    				creatureName,
	    				rank,
						experience
	    			);
	    		this.addCreatureKnowledge(creatureKnowledge, false);
    		}
    	}
    }

    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	NBTTagList knowledgeList = new NBTTagList();
		for(Entry<String, CreatureKnowledge> creatureKnowledgeEntry : creatureKnowledgeList.entrySet()) {
			CreatureKnowledge creatureKnowledge = creatureKnowledgeEntry.getValue();
			NBTTagCompound nbtKnowledge = new NBTTagCompound();
			nbtKnowledge.setString("CreatureName", creatureKnowledge.creatureName);
			nbtKnowledge.setInteger("Rank", creatureKnowledge.rank);
			nbtKnowledge.setInteger("Experience", creatureKnowledge.experience);
			knowledgeList.appendTag(nbtKnowledge);
		}
		nbtTagCompound.setTag("CreatureKnowledge", knowledgeList);
    }
}
