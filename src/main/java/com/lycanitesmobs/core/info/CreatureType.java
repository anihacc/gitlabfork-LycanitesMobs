package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.item.special.ItemSoulstoneFilled;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureType {

	// Core Info:
	/** The name of this creature type. Lowercase, no space, used for language entries and for generating the entity id, etc. Required. **/
	protected String name;

	/** The mod info of the mod this creature type belongs to. **/
	public ModInfo modInfo;

	/** A map of all creatures of this type by name. **/
	public Map<String, CreatureInfo> creatures = new HashMap<>();

	/** A list of all creatures of this type that can be tamed. **/
	public List<CreatureInfo> tameableCreatures = new ArrayList<>();

	/** The treat item this type uses. **/
	protected Item treat;

	/** The soulstone item this type uses. **/
	protected ItemSoulstone soulstone;

	/** The spawn egg item this type uses. **/
	protected Item spawnEgg;

	/** The diet type this Creature Type provides as food for predators. "none" by default. **/
	public String dietProvided = "none";


	/**
	 * Constructor
	 * @param group The group that this creature definition will belong to.
	 */
	public CreatureType(ModInfo group) {
		this.modInfo = group;
	}


	/** Loads this creature type from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();

		if(json.has("dietProvided"))
			this.dietProvided = json.get("dietProvided").getAsString();
	}

	/**
	 * Loads this creature type (should only be called during startup), generates spawn egg, etc.
	 */
	public void load() {
		this.createItems();

		LycanitesMobs.logDebug("Creature Type", "Loaded Creature Type: " + this.getTitle());
	}


	/**
	 * Returns the name of this creature type. Ex: beast
	 * @return The name of this creature type.
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Returns a translated title for this creature type. Ex: Beast
	 * @return The display name of this creature type.
	 */
	public String getTitle() {
		return LanguageManager.translate("creaturetype." + this.getName() + ".name");
	}


	/**
	 * Generates a treat item name from this type. Ex: beasttreat
	 * @return The treat item name for this creature type.
	 */
	public String getTreatName() {
		return this.getName() + "treat";
	}


	/**
	 * Gets this creature type's treat item.
	 * @return The treat item for this creature type.
	 */
	public Item getTreatItem() {
		return this.treat;
	}


	/**
	 * Generates a soulstone item name from this type. Ex: imp_soulstone
	 * @return The soulstone item name for this creature type.
	 */
	public String getSoulstoneName() {
		return "soulstone_" + this.getName();
	}


	/**
	 * Gets this creature type's soulstone item.
	 * @return The soulstone item for this creature type.
	 */
	public ItemSoulstone getSoulstoneItem() {
		return this.soulstone;
	}


	/**
	 * Generates a spawn egg item name from this type. Ex: beastspawn
	 * @return The spawn egg item name for this creature type.
	 */
	public String getSpawnEggName() {
		return this.getName() + "spawn";
	}


	/**
	 * Adds a creature to this Creature Type.
	 * @param creatureInfo The creature to add.
	 * @return
	 */
	public void addCreature(CreatureInfo creatureInfo) {
		if(this.creatures.containsKey(creatureInfo.getName())) {
			return;
		}
		this.creatures.put(creatureInfo.getName(), creatureInfo);
		if(creatureInfo.isTameable()) {
			this.tameableCreatures.add(creatureInfo);
		}
	}


	/**
	 * Returns the the spawn egg this creature type uses.
	 * @return Spawn egg item.
	 */
	public Item getSpawnEgg() {
		return this.spawnEgg;
	}


	/**
	 * Creates items for this creature type such as the spawn egg item or treat item, must be called after creatures are loaded so that an egg for each creature can be added.
	 */
	public void createItems() {
		// Spawn Egg:
		this.spawnEgg = ObjectManager.getItem(this.getSpawnEggName());
		if(this.spawnEgg != null) {
			if(this.spawnEgg instanceof ItemCustomSpawnEgg)
				((ItemCustomSpawnEgg)this.spawnEgg).creatureType = this;
			return;
		}
		this.spawnEgg = new ItemCustomSpawnEgg(this.getSpawnEggName(), this);
		ObjectManager.addItem(this.getSpawnEggName(), this.spawnEgg);

		// Treat:
		this.treat = ObjectManager.getItem(this.getTreatName());
		if(this.treat != null) {
			return;
		}
		this.treat = new ItemTreat(this);
		ObjectManager.addItem(this.getTreatName(), this.treat);

		// Soulstone:
		this.soulstone = (ItemSoulstone)ObjectManager.getItem(this.getSoulstoneName());
		if(this.soulstone != null) {
			return;
		}
		this.soulstone = new ItemSoulstoneFilled(this.modInfo, this);
		ObjectManager.addItem(this.getSoulstoneName(), this.soulstone);
	}
}
