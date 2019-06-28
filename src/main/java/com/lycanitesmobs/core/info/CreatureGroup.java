package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CreatureGroup {
	/** A list of all creatures in this group. **/
	public List<CreatureInfo> creatures = new ArrayList<>();

	/** The name of this creature group. **/
	public String name;

	/** A list of groups that this group will actively hunt. **/
	public List<CreatureGroup> huntGroups = new ArrayList<>();
	public List<String> huntGroupNames = new ArrayList<>();

	/** A list of groups that this group will actively hunt if in a pack. **/
	public List<CreatureGroup> packGroups = new ArrayList<>();
	public List<String> packGroupNames = new ArrayList<>();

	/** A list of groups that this group will ignore but run from if hit by. **/
	public List<CreatureGroup> waryGroups = new ArrayList<>();
	public List<String> waryGroupNames = new ArrayList<>();

	/** A list of groups that this group will actively flee from. **/
	public List<CreatureGroup> fleeGroups = new ArrayList<>();
	public List<String> fleeGroupNames = new ArrayList<>();

	/** If true, this group includes humanoids like Player, Villagers or Illagers. **/
	public boolean humanoids = false;

	/**
	 * Loads this creature group from json.
	 */
	public void loadFromJson(JsonObject json) {
		this.name = json.get("name").getAsString();

		if(json.has("hostile")) {
			this.huntGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("hostile"));
		}

		if(json.has("pack")) {
			this.packGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("pack"));
		}

		if(json.has("wary")) {
			this.waryGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("wary"));
		}

		if(json.has("flee")) {
			this.fleeGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("flee"));
		}

		if(json.has("humanoids")) {
			this.humanoids = json.get("humanoids").getAsBoolean();
		}
	}

	/**
	 * Initialises this creature group (should only be called during startup, after all groups are loaded), generates interactions with other groups, etc.
	 */
	public void init() {
		for(String groupName : this.huntGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(group != null)
				this.huntGroups.add(group);
		}

		for(String groupName : this.packGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(group != null)
				this.packGroups.add(group);
		}

		for(String groupName : this.waryGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(group != null)
				this.waryGroups.add(group);
		}

		for(String groupName : this.fleeGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(group != null)
				this.fleeGroups.add(group);
		}

		LycanitesMobs.logDebug("Creature Group", "Loaded Creature Group: " + this.getName());
	}

	/**
	 * Gets the name of this group.
	 * @return The group name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Adds a creature to this group.
	 * @param creature The creature to add.
	 */
	public void addCreature(CreatureInfo creature) {
		if(this.creatures.contains(creature))
			return;
		this.creatures.add(creature);
	}

	/**
	 * Returns if this group has the provided entity in it.
	 * @param entity The entity to check for.
	 * @return True if the entity is in this group, otherwise false.
	 */
	public boolean hasEntity(Entity entity) {
		if(!(entity instanceof BaseCreatureEntity))
			return false;
		return ((BaseCreatureEntity)entity).creatureInfo.groups.contains(this);
	}

	/**
	 * Returns if this group should fight back if hit by the entity.
	 * @param entity The target entity.
	 * @return True if this group should retaliate.
	 */
	public boolean shouldRevenge(Entity entity) {
		for(CreatureGroup group : this.waryGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		for(CreatureGroup group : this.fleeGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		return true;
	}

	/**
	 * Returns if this group should hunt the entity.
	 * @param entity The target entity.
	 * @return True if this group should hunt.
	 */
	public boolean shouldHunt(Entity entity) {
		for(CreatureGroup group : this.huntGroups) {
			if(group.hasEntity(entity))
				return true;
		}
		return false;
	}

	/**
	 * Returns if this group should hunt the entity when in a pack, this overrides everything.
	 * @param entity The target entity.
	 * @return True if this group should hunt when in a pack.
	 */
	public boolean shouldPackHunt(Entity entity) {
		for(CreatureGroup group : this.packGroups) {
			if(group.hasEntity(entity))
				return true;
		}
		return false;
	}

	/**
	 * Returns if this group should flee from the entity on sight.
	 * @param entity The target entity.
	 * @return True if this group should flee on sight.
	 */
	public boolean shouldFlee(Entity entity) {
		for(CreatureGroup group : this.fleeGroups) {
			if(group.hasEntity(entity))
				return true;
		}
		return false;
	}
}
