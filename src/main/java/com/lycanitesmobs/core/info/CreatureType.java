package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.localisation.LanguageManager;
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

	/** A list of all creature of this type that are tameable. **/
	public List<CreatureInfo> tameableCreatures = new ArrayList<>();

	/** The treat item this type uses. **/
	public Item treat;

	/** The soulstone item this type uses. **/
	public ItemSoulstone soulstone;

	/** The spawn egg item this type uses. **/
	public Item spawnEgg;


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
	 * Generates a spawn egg item name from this type. Ex: beastspawn
	 * @return The spawn egg item name for this creature type.
	 */
	public String getSpawnEggName() {
		return this.getName() + "spawn";
	}


	/**
	 * Gets this creature type's treat item.
	 * @return The treat item for this creature type.
	 */
	public Item getTreatItem() {
		return this.treat;
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
	 * Creates items for this creature type such as the spawn egg item or treat item, must be called after creatures are loaded so that an egg for each creature can be added.
	 */
	public void createItems() {
		// Treat:
		this.treat = ObjectManager.getItem(this.getTreatName());
		if(this.treat != null) {
			return;
		}
		Item.Properties treatProperties = new Item.Properties();
		treatProperties.maxStackSize(16);
		this.treat = new ItemTreat(treatProperties, this);
		ObjectManager.addItem(this.getTreatName(), this.treat);

		// Spawn Eggs:
		String spawnEggName = this.getSpawnEggName();
		Item.Properties spawnEggProperties = new Item.Properties();
		spawnEggProperties.group(ItemManager.getInstance().creatures);

		this.spawnEgg = new ItemCustomSpawnEgg(spawnEggProperties, spawnEggName, this);
		ObjectManager.addItem(spawnEggName, this.spawnEgg);

		// Soulstone:
		// TODO Create soulstones for each group type (so long as there are tameable creatures).
	}
}
