package com.lycanitesmobs.core.info;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.config.ConfigCreatureSubspecies;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class Subspecies {
    // ========== Subspecies Global ==========
    /** The weight used by the default subspecies. **/
    public static int BASE_WEIGHT = 400;

    /** Common weights used by most subspecies. **/
    public static Map<String, Integer> COMMON_WEIGHTS = new HashMap<String, Integer>() {{
    	put("common", 100);
    	put("uncommon", 20);
    	put("rare", 2);
    	put("legendary", 1);
    }};

    public static String[] SUBSPECIES_NAMES = new String[] {"uncommon", "rare"};

	/** A static map containing all the global multipliers for each stat for each subspecies. **/
	public static Map<String, Double> statMultipliers = new HashMap<>();

    /** The drop amount scale of uncommon subspecies. **/
    public static int uncommonDropScale = 2;

    /** The drop amount scale of rare subspecies. **/
    public static int rareDropScale = 5;

    /** The scale of experience for uncommon subspecies. **/
    public static double uncommonExperienceScale = 2.0D;

    /** The scale of experience for uncommon subspecies. **/
    public static double rareExperienceScale = 10.0D;

	/** The minimum amount of days before uncommon species start to spawn. **/
	public static int uncommonSpawnDayMin = 0;

	/** The minimum amount of days before rare species start to spawn. **/
	public static int rareSpawnDayMin = 0;

    /** Whether rare subspecies should show boss health bars or not. **/
    public static boolean rareHealthBars = false;

    // ========== Subspecies General ==========
    /** The index of this subspecies in creature info. Set by creature info when added. Should never be 0 as that is used by the default and will result in this subspecies being ignored. **/
    public int index;

    /** The size scale of this subspecies. **/
    public double scale = 1.0D;

	/** The skin of this subspecies. Skins refer to different models and major texture changes. Ex: Void Astaroth. **/
	public String skin;

    /** The color of this subspecies. Colors refer to just color or minor texture variations. Ex: Verdant Grue. **/
    public String color;

    /** The rarity of this subspecies. **/
    public String rarity;

	/** The class name of the model this subspecies should use, loaded client side only. If null, the default Creature model is used instead. **/
	@Nullable
	public String modelClassName;

    /** The weight of this subspecies, used when randomly determining the subspecies of a mob. A base species uses the static baseSpeciesWeight value. **/
    public int weight;

    /** Higher priority Subspecies will always spawn in place of lower priority ones if they can spawn (conditions are met) regardless of weight. Only increase this above 0 if a subspecies has conditions otherwise they will stop standard/base Subspecies from showing up. **/
    public int priority = 0;

    /** A list of Spawn Conditions required for this subspecies to spawn. **/
    public List<SpawnCondition> spawnConditions = new ArrayList<>();


	/**
	 * Loads global Subspecies config values, etc.
	 */
	public static void loadGlobalSettings() {
        BASE_WEIGHT = ConfigCreatureSubspecies.INSTANCE.baseWeight.get();
		for(String subspeciesName : Subspecies.SUBSPECIES_NAMES) {
			COMMON_WEIGHTS.put(subspeciesName,  ConfigCreatureSubspecies.INSTANCE.commonWeights.get(subspeciesName).get());
		}

		statMultipliers = new HashMap<>();
        for(String subspeciesName : SUBSPECIES_NAMES) {
            for(String statName : CreatureStats.STAT_NAMES) {
                statMultipliers.put((subspeciesName + "-" + statName).toUpperCase(), ConfigCreatureSubspecies.INSTANCE.subspeciesMultipliers.get(subspeciesName).get(statName).get());
            }
        }

        uncommonDropScale = ConfigCreatureSubspecies.INSTANCE.uncommonDropScale.get();
        rareDropScale = ConfigCreatureSubspecies.INSTANCE.rareDropScale.get();

        uncommonExperienceScale = ConfigCreatureSubspecies.INSTANCE.uncommonExperienceScale.get();
        rareExperienceScale = ConfigCreatureSubspecies.INSTANCE.rareExperienceScale.get();

		uncommonSpawnDayMin = ConfigCreatureSubspecies.INSTANCE.uncommonSpawnDayMin.get();
		rareSpawnDayMin = ConfigCreatureSubspecies.INSTANCE.rareSpawnDayMin.get();

		rareHealthBars = ConfigCreatureSubspecies.INSTANCE.rareHealthBars.get();
    }


    public static Subspecies createFromJSON(CreatureInfo creatureInfo, JsonObject json) {
		// Rarity:
		String rarity = "uncommon";
		if(json.has("rarity")) {
			rarity = json.get("rarity").getAsString().toLowerCase();
		}
		else if(json.has("type")) {
			rarity = json.get("type").getAsString().toLowerCase();
		}

		// Skin:
		String skin = null;
		if(json.has("skin")) {
			skin = json.get("skin").getAsString().toLowerCase();
		}

		// Color:
		String color = null;
		if(json.has("color")) {
			color = json.get("color").getAsString().toLowerCase();
		}
		else if(json.has("name")) {
			color = json.get("name").getAsString().toLowerCase();
		}

		if(skin == null && color == null) {
			throw new RuntimeException("Invalid subspecies added with no Skin and/or Color defined! At least one value must be set.");
		}

		// Create Subspecies:
		Subspecies subspecies = new Subspecies(skin, color, rarity);
		subspecies.index = json.get("index").getAsInt();

		// Scale:
		if(json.has("scale")) {
			subspecies.scale = json.get("scale").getAsDouble();
		}

		// Model Class:
		if (json.has("modelClass")) {
			subspecies.modelClassName = json.get("modelClass").getAsString();
		}

		// Priority:
		if(json.has("priority")) {
			subspecies.priority = json.get("priority").getAsInt();
		}

		// Conditions:
		if(json.has("conditions")) {
			JsonArray jsonArray = json.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject conditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
				subspecies.spawnConditions.add(spawnCondition);
			}
		}

		return subspecies;
	}


	/**
	 * Constructor for creating a color/skin Subspecies based on a rarity.
	 * @param skin The skin of the Subspecies. Can be null for default skin.
	 * @param color The color of the Subspecies. Can be null for default color.
	 * @param rarity The rarity of the Subspecies ('common', 'uncommon' or 'rare').
	 */
	public Subspecies(@Nullable String skin, @Nullable String color, String rarity) {
        this.color = color;
        this.skin = skin;
        this.rarity = rarity;
        this.weight = COMMON_WEIGHTS.get(rarity);
    }


	/**
	 * Loads this subspecies, used for adding new sounds, etc. Can only be done during startup.
	 */
	public void load(CreatureInfo creatureInfo) {
		if(this.skin != null && !creatureInfo.loadedSubspeciesSkins.contains(this.skin)) {
			creatureInfo.addSounds("." + this.skin);
			creatureInfo.loadedSubspeciesSkins.add(this.skin);
		}
	}


	/**
	 * Gets the display name of this Subspecies.
	 * @return The Subspecies title.
	 */
	public ITextComponent getTitle() {
		ITextComponent subspeciesName = new StringTextComponent("");
		if(this.color != null) {
			subspeciesName.appendSibling(new TranslationTextComponent("subspecies." + this.color));
		}
		if(this.skin != null) {
			if(!subspeciesName.getFormattedText().equals("")) {
				subspeciesName.appendText(" ");
			}
			subspeciesName.appendSibling(new TranslationTextComponent("subspecies." + this.skin));
		}
        return subspeciesName;
    }


	/**
	 * Gets the size scale of this subspecies.
	 * @return The size scale.
	 */
	public double getScale() {
		return this.scale;
	}


	/**
	 * Returns if this Subspecies is allowed to be used on the spawned entity.
	 * @return True if this Subspecies is allowed.
	 */
	public boolean canSpawn(LivingEntity entityLiving) {
		if(entityLiving != null) {
			World world = entityLiving.getEntityWorld();

			// Spawn Day Limit:
			int day = (int)Math.floor(world.getGameTime() / 23999D);
			int spawnDayMin = 0;
			if("uncommon".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = uncommonSpawnDayMin;
			}
			else if("rare".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = rareSpawnDayMin;
			}
			if(day < spawnDayMin) {
				return false;
			}

			// Check Conditions:
			for(SpawnCondition condition : this.spawnConditions) {
				if(!condition.isMet(world, null, entityLiving.getPosition())) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public String toString() {
		String subspeciesName = this.color != null ? this.color : "normal";
		if(this.skin != null) {
			subspeciesName += " - " + this.skin;
		}
		subspeciesName += " - " + this.weight;
		return subspeciesName;
	}
}
