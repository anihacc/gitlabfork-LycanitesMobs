package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.FileLoader;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigCreatures;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureManager extends JSONLoader {
	public static CreatureManager INSTANCE;
	public static String[] DIFFICULTY_NAMES = new String[] {"easy", "normal", "hard"};
	public static double[] DIFFICULTY_DEFAULTS = new double[] {0.8D, 1.0D, 1.1D};

	/** Handles all global creature general config settings. **/
	public CreatureConfig config;

	/** Handles all global creature spawning config settings. **/
	public CreatureSpawnConfig spawnConfig;

	/** A map of all creatures types by name. **/
	public Map<String, CreatureType> creatureTypes = new HashMap<>();

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
		this.loadCreatureTypesFromJSON(modInfo);
		this.loadCreaturesFromJSON(modInfo);
		for(CreatureType creatureType : this.creatureTypes.values()) {
			creatureType.load();
		}
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			creatureInfo.load();
		}
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		this.config.loadConfig();
		this.spawnConfig.loadConfig();

		// Difficulty:
		this.difficultyMultipliers = new HashMap<>();
		for(String difficultyName : DIFFICULTY_NAMES) {
			for(String statName : CreatureStats.STAT_NAMES) {
				this.difficultyMultipliers.put((difficultyName + "-" + statName).toUpperCase(), ConfigCreatures.INSTANCE.difficultyMultipliers.get(difficultyName).get(statName).get());
			}
		}

		// Level:
		for(String statName : CreatureStats.STAT_NAMES) {
			this.levelMultipliers.put(statName.toUpperCase(), ConfigCreatures.INSTANCE.levelMultipliers.get(statName).get());
		}
	}


	/** Loads all JSON Creature Types. Should be done before creatures are loaded so that they can find their type on load. **/
	public void loadCreatureTypesFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Creature Type", "creaturetypes", "name", true, FileLoader.PathType.COMMON);
			LycanitesMobs.logDebug("Creature", "Complete! " + this.creatures.size() + " JSON Creature Info Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Creature Types loaded for: " + groupInfo.name);
		}
	}


	/** Loads all JSON Creatures. Should only initially be done on pre-init and before Creature Info is loaded and can then be done in game on reload. **/
	public void loadCreaturesFromJSON(ModInfo groupInfo) {
		try {
			if(!this.loadedMods.contains(groupInfo)) {
				this.loadedMods.add(groupInfo);
			}
			this.loadAllJson(groupInfo, "Creature", "creatures", "name", true, FileLoader.PathType.COMMON);
			LycanitesMobs.logDebug("Creature", "Complete! " + this.creatures.size() + " JSON Creature Info Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Creatures loaded for: " + groupInfo.name);
		}
	}


	@Override
	public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
		// Parse Creature Type JSON:
		if("Creature Type".equals(name)) {
			CreatureType creatureType = new CreatureType(groupInfo);
			creatureType.loadFromJSON(json);
			if (creatureType.name == null) {
				LycanitesMobs.logWarning("", "[Creature] Unable to load " + name + " json due to missing name.");
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

		// Parse Creature JSON:
		if("Creature".equals(name)) {
			CreatureInfo creatureInfo = new CreatureInfo(groupInfo);
			creatureInfo.loadFromJSON(json);
			if (creatureInfo.name == null) {
				LycanitesMobs.logWarning("", "[Creature] Unable to load " + name + " json due to missing name.");
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
	 * @param event The entity register event.
	 */
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		LycanitesMobs.logDebug("Creature", "Forge registering all " + this.creatures.size() + " creatures...");
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			event.getRegistry().register(creatureInfo.getEntityType());
		}
	}


	/**
	 * Registers all stats for creatures added to this creature manager, called from the registry event.
	 * @param event The entity register event.
	 */
	/*@SubscribeEvent
	public void registerStats(RegistryEvent.Register<StatType<?>> event) {
		LycanitesMobs.logDebug("Creature", "Forge registering all " + this.creatures.size() + " creatures...");
		for(CreatureInfo creatureInfo : this.creatures.values()) {
			event.getRegistry().register(creatureInfo.getStatTypeKill());
			event.getRegistry().register(creatureInfo.getStatTypeKilledBy());
		}


		ObjectManager.addStat(this.getName() + ".kill", new Stat(this.getName() + ".kill", new TranslationTextComponent(this.getName() + ".kill")));
		ObjectManager.addStat(this.getName() + ".learn", new Stat(this.getName() + ".learn", new TranslationTextComponent(this.getName() + ".learn")));
		if(this.isSummonable()) {
			ObjectManager.addStat(this.getName() + ".summon", new Stat(this.getName() + ".summon", new TranslationTextComponent(this.getName() + ".summon")));
		}
		if(this.isTameable()) {
			ObjectManager.addStat(this.getName() + ".tame", new Stat(this.getName() + ".tame", new TranslationTextComponent(this.getName() + ".tame")));
		}
	}*/


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
	 * Gets a creature by name.
	 * @param creatureName The name of the creature to get.
	 * @return The Creature Info or null.
	 */
	@Nullable
	public CreatureInfo getCreature(String creatureName) {
		if(!this.creatures.containsKey(creatureName))
			return null;
		return this.creatures.get(creatureName);
	}


	/**
	 * Gets a Creature Entity Type by name.
	 * @param creatureName The name of the creature to get.
	 * @return The Entity Type or null.
	 */
	@Nullable
	public EntityType<? extends LivingEntity> getEntityType(String creatureName) {
		CreatureInfo creatureInfo = this.getCreature(creatureName);
		if(creatureInfo == null)
			return null;
		return creatureInfo.getEntityType();
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
	 * Returns a global difficulty multiplier for a stat.
	 * @param difficultyName The difficulty name.
	 * @param statName The stat name.
	 * @return The multiplier.
	 */
	public double getDifficultyMultiplier(String difficultyName, String statName) {
		String key = difficultyName.toUpperCase() + "-" + statName.toUpperCase();
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
		if(!this.levelMultipliers.containsKey(statName.toUpperCase())) {
			return 1;
		}
		return this.levelMultipliers.get(statName.toUpperCase());
	}
}
