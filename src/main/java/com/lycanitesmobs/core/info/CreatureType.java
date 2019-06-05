package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class CreatureType {

	// Core Info:
	/** The name of this creature type. Lowercase, no space, used for language entries and for generating the entity id, etc. Required. **/
	protected String name;

	/** The mod info of the mod this creature type belongs to. **/
	public ModInfo modInfo;

	/** A map of all creatures of this type by name. **/
	public Map<String, CreatureInfo> creatures = new HashMap<>();

	/** The name of the spawn egg item this type uses. **/
	protected String spawnEggName = "beastspawn";

	/** The name of the spawn egg item this type uses. **/
	protected Item spawnEgg;


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

		if(json.has("spawnEggName")) {
			this.spawnEggName = json.get("spawnEggName").getAsString();
		}

		LycanitesMobs.printDebug("Creature Type", "Added Creature Type: " + this.getTitle());
	}


	/**
	 * Returns a translated title for this creature type. Ex: Beast
	 * @return The display name of this creature type.
	 */
	public String getTitle() {
		return LanguageManager.translate("creaturetype." + this.name + ".name");
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
	}


	/**
	 * Returns the the spawn egg this creature type uses.
	 * @return Spawn egg item.
	 */
	public Item getSpawnEgg() {
		return this.spawnEgg;
	}


	/**
	 * Creates the spawn egg item, must be called after creatures are loaded so that an egg for each creature can be added.
	 */
	public void createSpawnEggItem() {
		this.spawnEgg = ObjectManager.getItem(this.spawnEggName);
		if(this.spawnEgg != null) {
			return;
		}
		this.spawnEgg = new ItemCustomSpawnEgg(this.spawnEggName, this);
		ObjectManager.addItem(this.spawnEggName, this.spawnEgg);
	}
}
