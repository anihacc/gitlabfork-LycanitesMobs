package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DungeonHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Contains default spawn conditions for a creature. **/
public class CreatureSpawn {

	// General:
	/** If false, spawning of this creature is disabled, but this wont despawn existing creatures. **/
	public boolean enabled = true;

	/** If true, this mob wont naturally spawn as a subspecies. **/
	public boolean disableSubspecies = false;


	// Spawners:
	/** A list of Spawners that this creature should use. **/
	public List<String> spawners = new ArrayList<>();

	/** A list of Vanilla Creature Types to use. **/
	public List<EntityClassification> vanillaSpawnerTypes = new ArrayList<>();


	// Dimensions:
	/** The dimension IDs that the world must or must not match depending on the list type. **/
	public int[] dimensionIds;

	/** How the dimension ID list works. Can be whitelist or blacklist. **/
	public String dimensionListType = "whitelist";


	// Biomes:
	/** The list of biome tags that this creature spawns in. Converts to a list of biomes on demand. **/
	public List<String> biomeTags = new ArrayList<>();

	/** The list of biomes generated from the list of biome tags. **/
	public List<Biome> biomesFromTags = null;

	/** The list of specific biome ids that this creature spawns in. **/
	public List<String> biomeIds = new ArrayList<>();

	/** The list of specific biomes that this creature spawns in. **/
	public List<Biome> biomes = null;

	/** If true, the biome check will be ignored completely by this creature. **/
	public boolean ignoreBiome = false;


	// Weights:
	/** The chance of this mob spawning over others. **/
	public int spawnWeight = 8;

	/** The chance of dungeons using this mob over others. **/
	public int dungeonWeight = 200;


	// Limits:
	/** The maximum arount of this mob allowed within the Spawn Area Search Limit. **/
	public int spawnAreaLimit = 5;

	/** The minimum number of this mob to group spawn at once. **/
	public int spawnGroupMin = 1;

	/** The maximum number of this mob to group spawn at once. **/
	public int spawnGroupMax = 3;


	// Area Conditions:
	/** Whether or not this mob can spawn in high light levels. **/
	public boolean spawnsInLight = false;

	/** Whether or not this mob can spawn in low light levels. **/
	public boolean spawnsInDark = true;

	/** The minimum world days that must have gone by, can accept fractions such as 5.25 for 5 and a quarter days. **/
	public double worldDayMin = -1;


	// Despawning:
	/** Whether this mob should despawn or not by default (some mobs can override persistence, such as once farmed). **/
	public boolean despawnNatural = true;

	/** Whether this mob should always despawn no matter what. **/
	public boolean despawnForced = false;


	/**
	 * Loads this element from a JSON object.
	 */
	public void loadFromJSON(CreatureInfo creatureInfo, JsonObject json) {
		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();
		if(json.has("disableSubspecies"))
			this.disableSubspecies = json.get("disableSubspecies").getAsBoolean();

		// Spawners:
		this.spawners.clear();
		this.vanillaSpawnerTypes.clear();
		if(json.has("spawners")) {
			this.spawners = JSONHelper.getJsonStrings(json.get("spawners").getAsJsonArray());
			for(String spawner : this.spawners) {
				LycanitesMobs.logDebug("Creature", "Adding " + creatureInfo.getName() + " to " + spawner + " global spawn list.");
				SpawnerMobRegistry.createSpawn(creatureInfo, spawner);

				if ("monster".equalsIgnoreCase(spawner))
					this.vanillaSpawnerTypes.add(EntityClassification.MONSTER);
				else if ("creature".equalsIgnoreCase(spawner))
					this.vanillaSpawnerTypes.add(EntityClassification.CREATURE);
				else if ("watercreature".equalsIgnoreCase(spawner))
					this.vanillaSpawnerTypes.add(EntityClassification.WATER_CREATURE);
				else if ("ambient".equalsIgnoreCase(spawner))
					this.vanillaSpawnerTypes.add(EntityClassification.AMBIENT);
			}
		}

		// Dimensions:
		if(json.has("dimensionIds")) {
			JsonArray jsonArray = json.get("dimensionIds").getAsJsonArray();
			this.dimensionIds = new int[jsonArray.size()];
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			int i = 0;
			while (jsonIterator.hasNext()) {
				this.dimensionIds[i] = jsonIterator.next().getAsInt();
				i++;
			}
		}
		if(json.has("dimensionListType"))
			this.dimensionListType = json.get("dimensionListType").getAsString();

		// Biomes:
		if(json.has("ignoreBiome"))
			this.ignoreBiome = json.get("ignoreBiome").getAsBoolean();
		if(json.has("biomes")) {
			this.biomeTags.clear();
			this.biomesFromTags = null;
			this.biomeTags = JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray());
		}
		if(json.has("biomeIds")) {
			this.biomeIds.clear();
			this.biomes = null;
			this.biomeIds = JSONHelper.getJsonStrings(json.get("biomeIds").getAsJsonArray());
		}

		if(json.has("spawnWeight"))
			this.spawnWeight = json.get("spawnWeight").getAsInt();
		if(json.has("dungeonWeight"))
			this.dungeonWeight = json.get("dungeonWeight").getAsInt();

		if(json.has("spawnAreaLimit"))
			this.spawnAreaLimit = json.get("spawnAreaLimit").getAsInt();
		if(json.has("spawnGroupMin"))
			this.spawnGroupMin = json.get("spawnGroupMin").getAsInt();
		if(json.has("spawnGroupMax"))
			this.spawnGroupMax = json.get("spawnGroupMax").getAsInt();

		if (json.has("spawnsInLight"))
			this.spawnsInLight = json.get("spawnsInLight").getAsBoolean();
		if (json.has("spawnsInDark"))
			this.spawnsInDark = json.get("spawnsInDark").getAsBoolean();
		if(json.has("worldDayMin"))
			this.worldDayMin = json.get("worldDayMin").getAsInt();

		if (json.has("despawnNatural"))
			this.despawnNatural = json.get("despawnNatural").getAsBoolean();
		if (json.has("despawnForced"))
			this.despawnForced = json.get("despawnForced").getAsBoolean();
	}


	/**
	 * Registers this mob to vanilla spawners and dungeons. Can only be done during startup.
	 */
	public void registerVanillaSpawns(CreatureInfo creatureInfo) {
		// Load Biomes:
		if(this.biomesFromTags == null) {
			this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
		}

		/*/ Add Vanilla Spawns: TODO Not possible anymore? Using custom spawner for everthing now anyway.
		if(!CreatureManager.getInstance().spawnConfig.disableAllSpawning) {
			if(creatureInfo.enabled && this.enabled && this.spawnWeight > 0 && this.spawnGroupMax > 0) {
				for(EntityClassification creatureType : this.vanillaSpawnerTypes) {
					EntityRegistry.addSpawn(creatureInfo.entityClass, this.spawnWeight, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, this.biomesFromTags.toArray(new Biome[this.biomesFromTags.size()]));
					for(Biome biome : this.biomesFromTags) {
						if(biome == Biomes.NETHER) {
							EntityRegistry.addSpawn(creatureInfo.entityClass, this.spawnWeight * 10, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, biome);
							break;
						}
					}
				}
			}
		}*/

		// Dungeon Spawn:
		if(!CreatureManager.getInstance().spawnConfig.disableDungeonSpawners) {
			if(this.dungeonWeight > 0) {
				DungeonHooks.addDungeonMob(creatureInfo.getEntityType(), this.dungeonWeight);
				LycanitesMobs.logDebug("MobSetup", "Dungeon Spawn Added - Weight: " + this.dungeonWeight);
			}
		}
	}


	/**
	 * Returns if this creature is allowed to spawn in the provided world dimension.
	 * @param world The world to check.
	 * @return True if allowed, false if disallowed.
	 */
	public boolean isAllowedDimension(World world) {
		if(world == null) {
			LycanitesMobs.logDebug("MobSpawns", "No world or dimension spawn settings were found, defaulting to valid.");
			return true;
		}

		// Global Check:
		if(!CreatureManager.getInstance().spawnConfig.isAllowedGlobal(world)) {
			return false;
		}

		// Default:
		if(this.dimensionIds.length == 0) {
			return true;
		}

		// Check IDs:
		for(int dimensionId : this.dimensionIds) {
			if(world.getDimension().getType().getId() == dimensionId) {
				LycanitesMobs.logDebug("MobSpawns", "Dimension is in " + this.dimensionListType + ".");
				return this.dimensionListType.equalsIgnoreCase("whitelist");
			}
		}
		LycanitesMobs.logDebug("MobSpawns", "Dimension was not in " + this.dimensionListType + ".");
		return this.dimensionListType.equalsIgnoreCase("blacklist");
	}


	/**
	 * Returns if the provided biome is valid for this creature to spawn in.
	 * @param biome The biome to check.
	 * @return True if a valid biome.
	 */
	public boolean isValidBiome(Biome biome) {
		if(this.ignoreBiome) {
			return true;
		}

		// Biome Tags:
		if(!this.biomeTags.isEmpty()) {
			if (this.biomesFromTags == null) {
				this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
			}
			if (this.biomesFromTags.contains(biome)) {
				return true;
			}
		}

		// Biome IDs:
		if(!this.biomeIds.isEmpty()) {
			if (this.biomes == null) {
				this.biomes = JSONHelper.getBiomes(this.biomeIds);
			}
			if (this.biomes.contains(biome)) {
				return true;
			}
		}

		return false;
	}
}
