package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.*;

public class CreatureManager extends JSONLoader {
	public static CreatureManager INSTANCE;

	/** Handles all global creature general config settings. **/
	public CreatureConfig config;

	/** Handles all global creature spawning config settings. **/
	public CreatureSpawnConfig spawnConfig;

	/** A map of all creatures types by name. **/
	public Map<String, CreatureType> creatureTypes = new HashMap<>();

	/** A map of all creatures groups by name. **/
	public Map<String, CreatureGroup> creatureGroups = new HashMap<>();

	/** A map of all creatures by name. **/
	public Map<String, CreatureInfo> creatures = new HashMap<>();

	/** A map of all creatures by class. **/
	public Map<Class, CreatureInfo> creatureClassMap = new HashMap<>();

	/** The next available network id for creatures to register by. **/
	protected int nextCreatureNetworkId = 100;

	/** A list of mods that have loaded with this Creature Manager. **/
	public List<ModInfo> loadedMods = new ArrayList<>();

	/** A map containing all the global multipliers for each stat for each difficulty. **/
	public Map<String, Double> difficultyMultipliers = new HashMap<>();

	/** A map containing all the global multipliers for each stat for mob level scaling. **/
	public Map<String, Double> levelMultipliers = new HashMap<>();

	/** The global multiplier to use for the health of tamed creatures. **/
	public double tamedHealthMultiplier = 3;

	/** Set to true if Doomlike Dungeons is loaded allowing mobs to register their Dungeon themes. **/
	public boolean dlDungeonsLoaded = false;


	/** Returns the main Creature Manager instance or creates it and returns it. **/
	public static CreatureManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CreatureManager();
		}
		return INSTANCE;
	}


	/**
	 * Constructor
	 */
	public CreatureManager() {
		this.config = new CreatureConfig();
		this.spawnConfig = new CreatureSpawnConfig();
	}

	/**
	 * Called during startup and initially loads everything in this manager.
	 * @param modInfo The mod loading this manager.
	 */
	public void startup(ModInfo modInfo) {
		this.loadConfig();

		// Load From JSON:
		this.loadCreatureTypesFromJSON(modInfo);
		this.loadCreatureGroupsFromJSON(modInfo);
		this.loadCreaturesFromJSON(modInfo);

		// Initialise:
		for(CreatureType creatureType : this.creatureTypes.values()) {
			creatureType.load();
		}
		for(CreatureGroup creatureGroup : this.creatureGroups.values()) {
			creatureGroup.init();
		}
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			creatureInfo.load();
		}
	}

	/**
	 * Called during post init, loads vanilla spawns.
	 * @param modInfo The mod loading this manager.
	 */
	public void lateStartup(ModInfo modInfo) {
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			creatureInfo.lateLoad();
		}
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
		this.config.loadConfig(config);
		this.spawnConfig.loadConfig(ConfigBase.getConfig(LycanitesMobs.modInfo, "spawning"));

		// Difficulty:
		String[] difficultyNames = new String[] {"easy", "normal", "hard"};
		double[] difficultyDefaults = new double[] {0.8D, 1.0D, 1.1D};
		difficultyMultipliers = new HashMap<>();
		config.setCategoryComment("Difficulty Multipliers", "Here you can scale the stats of every mob on a per difficulty basis. Note that on easy, speed is kept at 1.0 by default as 0.5 makes them stupidly slow.");
		int difficultyIndex = 0;
		for(String difficultyName : difficultyNames) {
			for(String statName : CreatureStats.STAT_NAMES) {
				double defaultValue = difficultyDefaults[difficultyIndex];
				if("easy".equalsIgnoreCase(difficultyName) && "speed".equalsIgnoreCase(statName))
					defaultValue = 1.0D;
				if("hard".equalsIgnoreCase(difficultyName) && ("attackSpeed".equalsIgnoreCase(statName) || "rangedSpeed".equalsIgnoreCase(statName)))
					defaultValue = 1.5D;
				if("armor".equalsIgnoreCase(statName))
					defaultValue = 1.0D;
				if("sight".equalsIgnoreCase(statName))
					defaultValue = 1.0D;
				difficultyMultipliers.put((difficultyName + "-" + statName).toUpperCase(Locale.ENGLISH), config.getDouble("Difficulty Multipliers", difficultyName + " " + statName, defaultValue));
			}
			difficultyIndex++;
		}

		// Level:
		config.setCategoryComment("Mob Level Multipliers", "Normally mobs are level 1, but Spawners can increase their level. Here you can adjust the percentage of each stat that is added per extra level. So by default at level 2 a mobs health is increased by 10%, at level 3 20% and so on.");
		for(String statName : CreatureStats.STAT_NAMES) {
			double levelValue = 0.01D;
			if("health".equalsIgnoreCase(statName))
				levelValue = 0.1D;
			if("defense".equalsIgnoreCase(statName))
				levelValue = 0.01D;
			if("armor".equalsIgnoreCase(statName))
				levelValue = 0D;
			if("speed".equalsIgnoreCase(statName))
				levelValue = 0.01D;
			if("damage".equalsIgnoreCase(statName))
				levelValue = 0.02D;
			if("attackSpeed".equalsIgnoreCase(statName))
				levelValue = 0.01D;
			if("rangedSpeed".equalsIgnoreCase(statName))
				levelValue = 0.01D;
			if("effect".equalsIgnoreCase(statName))
				levelValue = 0.02D;
			if("pierce".equalsIgnoreCase(statName))
				levelValue = 0.02D;
			if("sight".equalsIgnoreCase(statName))
				levelValue = 0D;
			levelMultipliers.put(statName.toUpperCase(Locale.ENGLISH), config.getDouble("Mob Level Multipliers", statName, levelValue));
		}
	}


	/** Loads all JSON Creature Types. Should be done before creatures are loaded so that they can find their type on load. **/
	public void loadCreatureTypesFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Creature Type", "creaturetypes", "name", true);
			LycanitesMobs.logDebug("Creature", "Complete! " + this.creatureTypes.size() + " JSON Creature Types Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Creature Types loaded for: " + groupInfo.name);
		}
	}


	/** Loads all JSON Creature groups. Should be done before creatures are loaded so that they can find their groups on load. **/
	public void loadCreatureGroupsFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Creature Group", "creaturegroups", "name", true);
			LycanitesMobs.logDebug("Creature", "Complete! " + this.creatureGroups.size() + " JSON Creature Groups Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Creature Groups loaded for: " + groupInfo.name);
		}
	}


	/** Loads all JSON Creatures. Should only initially be done on pre-init and before Creature Info is loaded and can then be done in game on reload. **/
	public void loadCreaturesFromJSON(ModInfo groupInfo) {
		try {
			if(!this.loadedMods.contains(groupInfo)) {
				this.loadedMods.add(groupInfo);
			}
			this.loadAllJson(groupInfo, "Creature", "creatures", "name", false);
			LycanitesMobs.logDebug("Creature", "Complete! " + this.creatures.size() + " JSON Creature Info Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Creatures loaded for: " + groupInfo.name);
		}
	}


	@Override
	public void parseJson(ModInfo groupInfo, String loadGroup, JsonObject json) {
		// Parse Creature Type JSON:
		if("Creature Type".equals(loadGroup)) {
			CreatureType creatureType = new CreatureType(groupInfo);
			creatureType.loadFromJSON(json);
			if (creatureType.name == null) {
				LycanitesMobs.logWarning("", "[Creature] Unable to load " + loadGroup + " json due to missing name.");
				return;
			}

			// Already Exists:
			if (this.creatureTypes.containsKey(creatureType.name)) {
				creatureType = this.creatureTypes.get(creatureType.name);
				creatureType.loadFromJSON(json);
			}

			this.creatureTypes.put(creatureType.name, creatureType);
			return;
		}

		// Parse Creature Group JSON:
		if("Creature Group".equals(loadGroup)) {
			CreatureGroup creatureGroup = new CreatureGroup();
			creatureGroup.loadFromJson(json);

			// Already Exists:
			if (this.creatureGroups.containsKey(creatureGroup.name)) {
				creatureGroup = this.creatureGroups.get(creatureGroup.name);
				creatureGroup.loadFromJson(json);
			}

			this.creatureGroups.put(creatureGroup.name, creatureGroup);
			return;
		}

		// Parse Creature JSON:
		if("Creature".equals(loadGroup)) {
			CreatureInfo creatureInfo = new CreatureInfo(groupInfo);
			creatureInfo.loadFromJSON(json);
			if (creatureInfo.name == null) {
				LycanitesMobs.logWarning("", "[Creature] Unable to load " + loadGroup + " json due to missing name.");
				return;
			}

			// Already Exists:
			if (this.creatures.containsKey(creatureInfo.name)) {
				creatureInfo = this.creatures.get(creatureInfo.name);
				creatureInfo.loadFromJSON(json);
			}

			this.creatures.put(creatureInfo.name, creatureInfo);
			this.creatureClassMap.put(creatureInfo.entityClass, creatureInfo);
		}
	}


	/**
	 * Generates the next available creature network id to register with.
	 * @return The next creature network id.
	 */
	public int getNextCreatureNetworkId() {
		return this.nextCreatureNetworkId++;
	}


	/**
	 * Registers all creatures added to this creature manager, called from the registry event.
	 * @param event The enity register event.
	 * @param modInfo The mod to register entities from.
	 */
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ModInfo modInfo = LycanitesMobs.modInfo;
		LycanitesMobs.logDebug("Creature", "Forge registering all " + this.creatures.size() + " creatures from the mod: " + modInfo.name + "...");
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			if(creatureInfo.modInfo != modInfo) {
				continue;
			}
			try {
				EntityEntry entityEntry = EntityEntryBuilder.create()
						.entity(creatureInfo.entityClass)
						.id(creatureInfo.getEntityId(), this.getNextCreatureNetworkId())
						.name(creatureInfo.getName())
						.tracker(creatureInfo.isBoss() ? 160 : 80, 3, false)
						.build();
				event.getRegistry().register(entityEntry);
			}
			catch (Exception e) {
				LycanitesMobs.logWarning("", "Unable to find entity class for: " + creatureInfo.getName() + ".");
				throw e;
			}
		}
	}


	/**
	 * Reloads all Creature JSON.
	 */
	public void reload() {
		this.loadConfig();
		SpawnerMobRegistry.SPAWNER_MOB_REGISTRIES.clear();
		for(ModInfo group : this.loadedMods) {
			this.loadCreaturesFromJSON(group);
		}
	}


	/**
	 * Gets a creature type by name.
	 * @param creatureTypeName The name of the creature type to get.
	 * @return The Creature Type.
	 */
	public CreatureType getCreatureType(String creatureTypeName) {
		if(!this.creatureTypes.containsKey(creatureTypeName))
			return null;
		return this.creatureTypes.get(creatureTypeName);
	}


	/**
	 * Gets a creature group by name.
	 * @param creatureGroupName The name of the creature group to get.
	 * @return The Creature Group.
	 */
	public CreatureGroup getCreatureGroup(String creatureGroupName) {
		if(!this.creatureGroups.containsKey(creatureGroupName))
			return null;
		return this.creatureGroups.get(creatureGroupName);
	}


	/**
	 * Gets a creature by name.
	 * @param creatureName The name of the creature to get.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreature(String creatureName) {
		if(!this.creatures.containsKey(creatureName))
			return null;
		return this.creatures.get(creatureName);
	}


	/**
	 * Gets a creature by class.
	 * @param creatureClass The class of the creature to get.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreature(Class creatureClass) {
		if(!this.creatureClassMap.containsKey(creatureClass))
			return null;
		return this.creatureClassMap.get(creatureClass);
	}


	/**
	 * Gets a creature by entity id.
	 * @param entityId The the entity id of the creature to get. Periods will be replaced with semicolons.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreatureFromId(String entityId) {
		entityId = entityId.replace(".", ":");
		String[] mobIdParts = entityId.toLowerCase().split(":");
		return this.getCreature(mobIdParts[mobIdParts.length - 1]);
	}


	/**
	 * Gets a creature's entity class by name.
	 * @param creatureName The name of the creature to get.
	 * @return The Creature's Entity Class.
	 */
	public Class<? extends EntityLiving> getEntityClass(String creatureName) {
		CreatureInfo creatureInfo = this.getCreature(creatureName);
		if(creatureInfo == null) {
			return null;
		}
		return creatureInfo.entityClass;
	}


	/**
	 * Returns a global difficulty multiplier for a stat.
	 * @param difficultyName The difficulty name.
	 * @param statName The stat name.
	 * @return The multiplier.
	 */
	public double getDifficultyMultiplier(String difficultyName, String statName) {
		String key = difficultyName.toUpperCase(Locale.ENGLISH) + "-" + statName.toUpperCase(Locale.ENGLISH);
		if(!this.difficultyMultipliers.containsKey(key)) {
			return 1;
		}
		return this.difficultyMultipliers.get(key);
	}


	/**
	 * Returns a global level multiplier for a stat.
	 * @param statName The stat name.
	 * @return The multiplier.
	 */
	public double getLevelMultiplier(String statName) {
		if(!this.levelMultipliers.containsKey(statName.toUpperCase(Locale.ENGLISH))) {
			return 1;
		}
		return this.levelMultipliers.get(statName.toUpperCase(Locale.ENGLISH));
	}
}
