package com.lycanitesmobs.core.info;


import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Variant {
    // ========== Global ==========
    /** The weight used by the default variants. **/
    public static int BASE_WEIGHT = 400;

    /** Common weights used by most variants. **/
    public static Map<String, Integer> COMMON_WEIGHTS = new HashMap<String, Integer>() {{
    	put("common", 100);
    	put("uncommon", 20);
    	put("rare", 2);
    	put("legendary", 1);
    }};

	/** A static map containing all the global multipliers for each stat for each variant. **/
	public static Map<String, Double> STAT_MULTIPLIERS = new HashMap<>();

    /** The drop amount scale of uncommon variants. **/
    public static int UNCOMMON_DROP_SCALE = 2;

    /** The drop amount scale of rare variants. **/
    public static int RARE_DROP_SCALE = 5;

    /** The scale of experience for uncommon variants. **/
    public static double UNCOMMON_EXPERIENCE_SCALE = 2.0D;

    /** The scale of experience for uncommon variants. **/
    public static double RARE_EXPERIENCE_SCALE = 10.0D;

	/** The minimum amount of days before uncommon variants start to spawn. **/
	public static int UNCOMMON_SPAWN_DAY_MIN = 0;

	/** The minimum amount of days before rare variants start to spawn. **/
	public static int RARE_SPAWN_DAY_MIN = 0;

	/** If true, rare variants will despawn naturally over time. **/
	public static boolean RARE_DESPAWNING = false;

    /** Whether rare variants should show boss health bars or not. **/
    public static boolean RARE_HEALTH_BARS = false;

    // ========== General ==========
    /** The index of this variant in MobInfo. Set by MobInfo when added. Should never be 0 as that is used by the default variant and will result in this variant being ignored. **/
    public int index;

	/** The size scale of this variant. **/
	public double scale = 1.0D;

    /** The color of this variant. **/
    public String color;

    /** The rarity of this variant. **/
    public String rarity = "uncommon";

    /** The weight of this variant, used when randomly determining the variant of a mob. **/
    public int weight = BASE_WEIGHT;


	/**
	 * Loads global Subspecies config values, etc.
	 * @param config The config instance to load values from.
	 */
	public static void loadGlobalSettings(ConfigBase config) {
        BASE_WEIGHT = config.getInt("Mob Variations", "Variant Base Weight", BASE_WEIGHT, "The weight of base variants (regular mob colors).");
        COMMON_WEIGHTS.put("uncommon", config.getInt("Mob Variations", "Variant Uncommon Weight", COMMON_WEIGHTS.get("uncommon"), "The weight of uncommon variants (such as Azure, Verdant, Scarlet, etc)."));
		COMMON_WEIGHTS.put("rare", config.getInt("Mob Variations", "Variant Rare Weight", COMMON_WEIGHTS.get("rare"), "The weight of rare variants (such as Lunar or Celestial)."));

        // Difficulty:
        String[] subspeciesNames = new String[] {"uncommon", "rare"};
		STAT_MULTIPLIERS = new HashMap<>();
		boolean foundOldStatsCategory = config.setCategoryReplacedComment("Subspecies Multipliers", "Variant Multipliers");
        config.setCategoryComment("Variant Multipliers", "Here you can scale the stats of every mob on a per variant basis.");
        for(String subspeciesName : subspeciesNames) {
            for(String statName : CreatureStats.STAT_NAMES) {
                double defaultValue = 1.0;
				if("uncommon".equals(subspeciesName)) {
					if("health".equals(statName)) {
						defaultValue = 2;
					}
				}
                if("rare".equals(subspeciesName)) {
					if("health".equals(statName)) {
						defaultValue = 20;
					}
					else if("attackSpeed".equals(statName)) {
						defaultValue = 2;
					}
					else if("rangedSpeed".equals(statName)) {
						defaultValue = 2;
					}
					else if("effect".equals(statName)) {
						defaultValue = 2;
					}
				}
                if(foundOldStatsCategory) {
					defaultValue = config.getDouble("Subspecies Multipliers", subspeciesName + " " + statName, defaultValue);
				}
                STAT_MULTIPLIERS.put((subspeciesName + "-" + statName).toUpperCase(Locale.ENGLISH), config.getDouble("Variant Multipliers", subspeciesName + " " + statName, defaultValue));
            }
        }

        UNCOMMON_DROP_SCALE = config.getInt("Mob Variations", "Variant Uncommon Item Drops Scale", UNCOMMON_DROP_SCALE, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its item drops amount is multiplied by this value.");
        RARE_DROP_SCALE = config.getInt("Mob Variations", "Variant Rare Item Drops Scale", RARE_DROP_SCALE, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its item drops amount is multiplied by this value.");

        UNCOMMON_EXPERIENCE_SCALE = config.getDouble("Mob Variations", "Variant Uncommon Experience Scale", UNCOMMON_EXPERIENCE_SCALE, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its experience amount is multiplied by this value.");
        RARE_EXPERIENCE_SCALE = config.getDouble("Mob Variations", "Variant Rare Experience Scale", RARE_EXPERIENCE_SCALE, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its experience amount is multiplied by this value.");

		UNCOMMON_SPAWN_DAY_MIN = config.getInt("Mob Variations", "Variant Uncommon Spawn Day Min", UNCOMMON_SPAWN_DAY_MIN, "The minimum amount of days before uncommon species start to spawn.");
		RARE_SPAWN_DAY_MIN = config.getInt("Mob Variations", "Variant Rare Spawn Day Min", RARE_SPAWN_DAY_MIN, "The minimum amount of days before rare species start to spawn.");

		RARE_DESPAWNING = config.getBool("Mob Variations", "Variant Rare Despawning", RARE_DESPAWNING, "If set to true, rare subspecies such as the Lunar Grue will despawn naturally over time.");

		RARE_HEALTH_BARS = config.getBool("Mob Variations", "Variant Rare Health Bars", RARE_HEALTH_BARS, "If set to true, rare subspecies such as the Lunar Grue or Celestial Geonach will display boss health bars.");
    }


    public static Variant createFromJSON(CreatureInfo creatureInfo, JsonObject json) {
		// Rarity:
		String rarity = "uncommon";
		if(json.has("rarity")) {
			rarity = json.get("rarity").getAsString().toLowerCase();
		}
		else if(json.has("type")) {
			rarity = json.get("type").getAsString().toLowerCase();
		}

		// Color:
		String color = null;
		if(json.has("color")) {
			color = json.get("color").getAsString().toLowerCase();
		}
		else if(json.has("name")) {
			color = json.get("name").getAsString().toLowerCase();
		}
		if(color == null) {
			throw new RuntimeException("Invalid subspecies variant added with no color defined!");
		}

		// Create Variant:
		Variant variant = new Variant(color, rarity, json.get("index").getAsInt());

		// Scale:
		if(json.has("scale")) {
			variant.scale = json.get("scale").getAsDouble();
		}

		return variant;
	}


	/**
	 * Constructor for creating a Creature Variant based on a rarity.
	 * @param color The color of the Variant.
	 * @param rarity The rarity of the Variant ('common', 'uncommon' or 'rare').
	 * @param index The index of the Variant, 0 is reserved for no color variation.
	 */
	public Variant(String color, String rarity, int index) {
        this.color = color;
        this.rarity = rarity;
        this.index = index;
        this.weight = COMMON_WEIGHTS.get(rarity);
    }


	/**
	 * Gets the display name of this Variant.
	 * @return The Subspecies title.
	 */
	public String getTitle() {
		return LanguageManager.translate("subspecies." + this.color + ".name");
    }


	/**
	 * Gets the size scale of this Variant.
	 * @return The size scale.
	 */
	public double getScale() {
		return this.scale;
	}


	/**
	 * Returns if this Variant is allowed to be used on the spawned entity.
	 * @return True if this Variant is allowed.
	 */
	public boolean canSpawn(EntityLivingBase entityLiving) {
		if(entityLiving != null) {
			World world = entityLiving.getEntityWorld();

			// Spawn Day Limit:
			int day = (int)Math.floor(world.getTotalWorldTime() / 23999D);
			int spawnDayMin = 0;
			if("uncommon".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = UNCOMMON_SPAWN_DAY_MIN;
			}
			else if("rare".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = RARE_SPAWN_DAY_MIN;
			}
			if(day < spawnDayMin) {
				return false;
			}
		}
		return true;
	}


	@Override
	public String toString() {
		return this.color + " - " + this.weight;
	}


	public static int getIndexFromOld(int oldIndex) {
		return oldIndex % 4;
	}
}
