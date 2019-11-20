package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
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

	/** A list of groups that this group will ignore completely. **/
	public List<CreatureGroup> ignoreGroups = new ArrayList<>();
	public List<String> ignoreGroupNames = new ArrayList<>();

	enum Interaction {
		HUNT("hunt"), PACKHUNT("pack"), WARY("wary"), FLEE("flee"), IGNORE("ignore"), RETALIATE("retaliate");
		public final String name;
		Interaction(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}

	/** The default interaction towards a group not in any interaction lists. **/
	public Interaction defaultInteraction = Interaction.HUNT;

	/** If true, this group includes animals like Sheep, Cows and Pigs. **/
	public boolean animals = false;

	/** If true, this group includes humanoids like Players, Villagers or Pillagers. **/
	public boolean humanoids = false;

	/** If true, this group includes raiders like Pillagers and Ravagers. **/
	public boolean raiders = false;

	/** If true, this group includes Snow Golems. **/
	public boolean frozen = false;

	/** If true, this group includes Blazes and Magma Cubes. **/
	public boolean inferno = false;

	/** A list of additional entity ids in this group. **/
	public List<String> entityIds = new ArrayList<>();

	/**
	 * Loads this creature group from json.
	 */
	public void loadFromJson(JsonObject json) {
		this.name = json.get("name").getAsString();

		// Interaction Lists:
		if(json.has("hunt")) {
			this.huntGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("hunt"));
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
		if(json.has("ignore")) {
			this.ignoreGroupNames = JSONHelper.getJsonStrings(json.getAsJsonArray("ignore"));
		}

		// Interactions:
		if(json.has("default")) {
			this.defaultInteraction = Interaction.valueOf(json.get("default").getAsString().toUpperCase());
		}

		// Special Entities:
		if(json.has("animals")) {
			this.animals = json.get("animals").getAsBoolean();
		}
		if(json.has("humanoids")) {
			this.humanoids = json.get("humanoids").getAsBoolean();
		}
		if(json.has("raiders")) {
			this.raiders = json.get("raiders").getAsBoolean();
		}
		if(json.has("frozen")) {
			this.frozen = json.get("frozen").getAsBoolean();
		}
		if(json.has("inferno")) {
			this.inferno = json.get("inferno").getAsBoolean();
		}
		if(json.has("entityIds")) {
			this.entityIds = JSONHelper.getJsonStrings(json.get("entityIds").getAsJsonArray());
		}
	}

	/**
	 * Initialises this creature group (should only be called during startup, after all groups are loaded), generates interactions with other groups, etc.
	 */
	public void init() {
		for(String groupName : this.huntGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(groupName.equals(this.name))
				group = this;
			if(group != null)
				this.huntGroups.add(group);
		}

		for(String groupName : this.packGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(groupName.equals(this.name))
				group = this;
			if(group != null)
				this.packGroups.add(group);
		}

		for(String groupName : this.waryGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(groupName.equals(this.name))
				group = this;
			if(group != null)
				this.waryGroups.add(group);
		}

		for(String groupName : this.fleeGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(groupName.equals(this.name))
				group = this;
			if(group != null)
				this.fleeGroups.add(group);
		}

		for(String groupName : this.ignoreGroupNames) {
			CreatureGroup group = CreatureManager.getInstance().getCreatureGroup(groupName);
			if(groupName.equals(this.name))
				group = this;
			if(group != null)
				this.ignoreGroups.add(group);
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
	public boolean hasEntity(@Nonnull Entity entity) {
		if(entity instanceof BaseCreatureEntity) {
			return ((BaseCreatureEntity)entity).creatureInfo.groups.contains(this);
		}
		if(!(entity instanceof EntityLivingBase)) {
			return false;
		}

		if(this.animals && entity instanceof EntityAnimal) {
			return true;
		}
		if(this.humanoids && (entity instanceof EntityPlayer || entity instanceof EntityVillager || entity instanceof AbstractIllager || entity instanceof EntityWitch)) {
			return true;
		}
		if(this.raiders && (entity instanceof AbstractIllager || entity instanceof EntityWitch)) {
			return true;
		}
		if(this.frozen && entity instanceof EntitySnowman) {
			return true;
		}
		if(this.inferno && (entity instanceof EntityBlaze || entity instanceof EntityMagmaCube)) {
			return true;
		}

		ResourceLocation entityResourceLocation = EntityList.getKey(entity);
		if(entityResourceLocation == null) {
			return false;
		}
		String entityId = entityResourceLocation.toString();
		if(this.entityIds.contains(entityId))
			return true;

		return false;
	}

	/**
	 * Returns if this group should fight back if hit by the entity.
	 * @param entity The target entity.
	 * @return True if this group should retaliate.
	 */
	public boolean shouldRevenge(Entity entity) {
		for(CreatureGroup group : this.ignoreGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		for(CreatureGroup group : this.waryGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		for(CreatureGroup group : this.fleeGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		return this.defaultInteraction == Interaction.HUNT || this.defaultInteraction == Interaction.RETALIATE;
	}

	/**
	 * Returns if this group should hunt the entity.
	 * @param entity The target entity.
	 * @return True if this group should hunt.
	 */
	public boolean shouldHunt(Entity entity) {
		for(CreatureGroup group : this.ignoreGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		for(CreatureGroup group : this.huntGroups) {
			if(group.hasEntity(entity))
				return true;
		}
		return this.defaultInteraction == Interaction.HUNT;
	}

	/**
	 * Returns if this group should hunt the entity when in a pack, this overrides everything.
	 * @param entity The target entity.
	 * @return True if this group should hunt when in a pack.
	 */
	public boolean shouldPackHunt(Entity entity) {
		for(CreatureGroup group : this.ignoreGroups) {
			if(group.hasEntity(entity))
				return false;
		}
		for(CreatureGroup group : this.packGroups) {
			if(group.hasEntity(entity))
				return true;
		}
		return this.defaultInteraction == Interaction.PACKHUNT;
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
		return this.defaultInteraction == Interaction.FLEE;
	}
}
