package com.lycanitesmobs.core.config;

import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ConfigCreatures {
	public static ConfigCreatures INSTANCE;

	public Map<String, Map<String, ForgeConfigSpec.ConfigValue<Double>>> difficultyMultipliers = new HashMap<>();
	public Map<String, ForgeConfigSpec.ConfigValue<Double>> levelMultipliers = new HashMap<>();

	public final ForgeConfigSpec.ConfigValue<Boolean> subspeciesTags;
	public final ForgeConfigSpec.ConfigValue<Integer> idleSoundTicks;
	public final ForgeConfigSpec.ConfigValue<Boolean> disableModelAlpha;
	
	public final ForgeConfigSpec.ConfigValue<Integer> startingLevelMin;
	public final ForgeConfigSpec.ConfigValue<Integer> startingLevelMax;
	public final ForgeConfigSpec.ConfigValue<Double> levelPerDay;
	public final ForgeConfigSpec.ConfigValue<Integer> levelPerDayMax;
	public final ForgeConfigSpec.ConfigValue<Double> levelPerLocalDifficulty;
	
	public final ForgeConfigSpec.ConfigValue<Boolean> ownerTags;
	public final ForgeConfigSpec.ConfigValue<Boolean> tamingEnabled;
	public final ForgeConfigSpec.ConfigValue<Boolean> mountingEnabled;
	public final ForgeConfigSpec.ConfigValue<Boolean> mountingFlightEnabled;
	public final ForgeConfigSpec.ConfigValue<Boolean> friendlyFire;
	public final ForgeConfigSpec.ConfigValue<Integer> petRespawnTime;
	
	public final ForgeConfigSpec.ConfigValue<Double> beastiaryAddOnDeathChance;
	public final ForgeConfigSpec.ConfigValue<Boolean> beastiaryKnowledgeMessages;
	
	public final ForgeConfigSpec.ConfigValue<Double> bossAntiFlight;

	public final ForgeConfigSpec.ConfigValue<Boolean> mobsAttackVillagers;
	public final ForgeConfigSpec.ConfigValue<Boolean> animalsFightBack;
	public final ForgeConfigSpec.ConfigValue<Boolean> elementalFusion;
	public final ForgeConfigSpec.ConfigValue<String> elementalFusionLevelMix;
	public final ForgeConfigSpec.ConfigValue<Double> elementalFusionLevelMultiplier;
	public final ForgeConfigSpec.ConfigValue<Boolean> disablePickupOffsets;
	public final ForgeConfigSpec.ConfigValue<Boolean> suffocationImmunity;
	public final ForgeConfigSpec.ConfigValue<Boolean> drownImmunity;
	
	public final ForgeConfigSpec.ConfigValue<Boolean> subspeciesSpawn;
	public final ForgeConfigSpec.ConfigValue<Boolean> randomSizes;
	public final ForgeConfigSpec.ConfigValue<Double> randomSizeMin;
	public final ForgeConfigSpec.ConfigValue<Double> randomSizeMax;
	
	//public final ForgeConfigSpec.ConfigValue<String> globalDropsString;

	public ConfigCreatures(ForgeConfigSpec.Builder builder) {
		builder.push("Creatures");
		builder.comment("Global creature settings.");

		subspeciesTags = builder.comment("If true, all mobs that are a subspecies will always show their nametag.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.subspeciesTags")
				.define("subspeciesTags", true);
		idleSoundTicks = builder.comment("The minimum interval in ticks between random idle sounds.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.idleSoundTicks")
				.define("idleSoundTicks", 100);
		disableModelAlpha = builder.comment("If true, alpha is disabled on mob textures, this can make them look undesirable but can increase performance on low end systems.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.disableModelAlpha")
				.define("disableModelAlpha", false);

		startingLevelMin = builder.comment("The minimum base starting level of every mob. Cannot be less than 1.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.startingLevelMin")
				.define("startingLevelMin", 1);
		startingLevelMax = builder.comment("The maximum base starting level of every mob. Ignored when not greater than the min level.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.startingLevelMax")
				.define("startingLevelMax", 1);
		levelPerDay = builder.comment("Increases the base start level by this amount of every world day that has gone by, use this to slowly level up mobs as the world gets older. Fractions can be used such as 0.05 levels per day. The levels are rounded down so +0.9 would be +0 levels.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.levelPerDay")
				.define("levelPerDay", 0D);
		levelPerDayMax = builder.comment("The maximum level to be able gain from levels per day.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.levelPerDayMax")
				.define("levelPerDayMax", 100);
		levelPerLocalDifficulty = builder.comment("How many levels a mob gains multiplied by the local area difficulty level. Staying in an area for a while slowly increases the difficulty of that area ranging from 0.00 to 6.75. So 1.5 means level 10 at full local area difficulty.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.levelPerLocalDifficulty")
				.define("levelPerLocalDifficulty", 1.5D);

		ownerTags = builder.comment("If true, tamed mobs will display their owner's name in their name tag.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.ownerTags")
				.define("ownerTags", true);
		tamingEnabled = builder.comment("Set to false to disable pet/mount taming.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.tamingEnabled")
				.define("tamingEnabled", true);
		mountingEnabled = builder.comment("Set to false to disable mounts.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.mountingEnabled")
				.define("mountingEnabled", true);
		mountingFlightEnabled = builder.comment("Set to false to disable flying mounts, if all mounts are disable this option doesn't matter.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.mountingFlightEnabled")
				.define("mountingFlightEnabled", true);
		friendlyFire = builder.comment("If true, pets, minions, etc can't harm their owners (with ranged attacks, etc).")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.friendlyFire")
				.define("friendlyFire", true);
		petRespawnTime = builder.comment("The time in tics that it takes for a pet to respawn.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.petRespawnTime")
				.define("petRespawnTime", 3 * 60 * 20);

		beastiaryAddOnDeathChance = builder.comment("The chance that creatures are added to the player's Beastiary when killed, the Soulgazer can also be used to add creatures. Bosses are always a 100% chance.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.beastiaryAddOnDeathChance")
				.define("beastiaryAddOnDeathChance", 0.15D);
		beastiaryKnowledgeMessages = builder.comment("If true, a chat message will be displayed when gaining Beastiary Knowledge.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.beastiaryKnowledgeMessages")
				.define("beastiaryKnowledgeMessages", true);

		bossAntiFlight = builder.comment("How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.bossAntiFlight")
				.define("bossAntiFlight", 3D);

		mobsAttackVillagers = builder.comment("Set to false to prevent mobs that attack players from also attacking villagers.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.mobsAttackVillagers")
				.define("mobsAttackVillagers", true);
		animalsFightBack = builder.comment("If true, passive mobs will fight back when hit instead of running away.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.animalsFightBack")
				.define("animalsFightBack", false);
		elementalFusion = builder.comment("If true, some elemental mobs will fuse with each other on sight into a stronger different elemental.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.elementalFusion")
				.define("elementalFusion", true);
		elementalFusionLevelMix = builder.comment("Controls how fused mobs combine their levels. Can be 'both' (default) where both levels are added together, 'highest' where the higher level is used and 'lowest' where the lowest level is used.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.elementalFusionLevelMix")
				.define("elementalFusionLevelMix", "both");
		elementalFusionLevelMultiplier = builder.comment("The level of a mob created via fusion is multiplied by this value, set to 1 for no changes. Tamed fusions aren't multiplied by this value.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.elementalFusionLevelMultiplier")
				.define("elementalFusionLevelMultiplier", 10D);
		disablePickupOffsets = builder.comment("If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.disablePickupOffsets")
				.define("disablePickupOffsets", false);
		suffocationImmunity = builder.comment("If true, all mobs will be immune to suffocation (inWall) damage.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.suffocationImmunity")
				.define("suffocationImmunity", false);
		drownImmunity = builder.comment("If true, all mobs will be immune to damage from running out of air (drown damage).")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.drownImmunity")
				.define("drownImmunity", false);

		subspeciesSpawn = builder.comment("Set to false to prevent subspecies from spawning, this will not affect mobs that have already spawned as subspecies.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.subspeciesSpawn")
				.define("subspeciesSpawn", true);
		randomSizes = builder.comment("Set to false to prevent mobs from having a random size variation when spawning, this will not affect mobs that have already spawned.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.randomSizes")
				.define("randomSizes", true);
		randomSizeMin = builder.comment("The minimum size scale mobs can randomly spawn at.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.randomSizeMin")
				.define("randomSizeMin", 0.85D);
		randomSizeMax = builder.comment("The maximum size scale mobs can randomly spawn at.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures.randomSizeMax")
				.define("randomSizeMax", 1.15D);

		int difficultyIndex = 0;
		for(String difficultyName : CreatureManager.DIFFICULTY_NAMES) {
			Map<String, ForgeConfigSpec.ConfigValue<Double>> statMultipliers = new HashMap<>();
			for (String statName : CreatureStats.STAT_NAMES) {
				double defaultValue = CreatureManager.DIFFICULTY_DEFAULTS[difficultyIndex];
				if("easy".equalsIgnoreCase(difficultyName) && "speed".equalsIgnoreCase(statName))
					defaultValue = 1.0D;
				if("hard".equalsIgnoreCase(difficultyName) && ("attackSpeed".equalsIgnoreCase(statName) || "rangedSpeed".equalsIgnoreCase(statName)))
					defaultValue = 1.5D;
				if("armor".equalsIgnoreCase(statName))
					defaultValue = 1.0D;
				if("sight".equalsIgnoreCase(statName))
					defaultValue = 1.0D;

				statMultipliers.put(statName, builder
						.comment("Stat multiplier for " + statName + " on " + difficultyName + " difficulty.")
						.translation(CoreConfig.CONFIG_PREFIX + "difficulty.multipliers." + difficultyName + "." + statName)
						.define("difficulty.multipliers." + difficultyName + "." + statName, defaultValue));
			}
			this.difficultyMultipliers.put(difficultyName, statMultipliers);
			difficultyIndex++;
		}

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
			this.levelMultipliers.put(statName, builder
					.comment("Level multiplier for " + statName + ".")
					.translation(CoreConfig.CONFIG_PREFIX + "level.multipliers." + statName)
					.define("level.multipliers." + statName, levelValue));
		}

		builder.pop();
	}
}
