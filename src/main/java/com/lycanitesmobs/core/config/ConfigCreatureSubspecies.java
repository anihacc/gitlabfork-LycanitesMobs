package com.lycanitesmobs.core.config;

import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ConfigCreatureSubspecies {
	public static ConfigCreatureSubspecies INSTANCE = new ConfigCreatureSubspecies(CoreConfig.BUILDER);

	public final ForgeConfigSpec.ConfigValue<Integer> baseWeight;
	public Map<String,ForgeConfigSpec.ConfigValue<Integer>> commonWeights = new HashMap<>();
	public Map<String, Map<String, ForgeConfigSpec.ConfigValue<Double>>> subspeciesMultipliers = new HashMap<>();

	public final ForgeConfigSpec.ConfigValue<Integer> uncommonDropScale;
	public final ForgeConfigSpec.ConfigValue<Integer> rareDropScale;

	public final ForgeConfigSpec.ConfigValue<Double> uncommonExperienceScale;
	public final ForgeConfigSpec.ConfigValue<Double> rareExperienceScale;

	public final ForgeConfigSpec.ConfigValue<Integer> uncommonSpawnDayMin;
	public final ForgeConfigSpec.ConfigValue<Integer> rareSpawnDayMin;

	public final ForgeConfigSpec.ConfigValue<Boolean> rareHealthBars;

	public ConfigCreatureSubspecies(ForgeConfigSpec.Builder builder) {
		builder.push("Creatures");
		builder.comment("Global creature settings.");

		baseWeight = builder.comment("The minimum base starting level of every mob. Cannot be less than 1.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.baseWeight")
				.define("creature.subspecies.baseWeight", 1);

		for(String subspeciesName : Subspecies.SUBSPECIES_NAMES) {
			commonWeights.put(subspeciesName, builder
					.comment("Subspecies weight for " + subspeciesName + ".")
					.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.weights." + subspeciesName)
					.define("creature.subspecies.weights." + subspeciesName, Subspecies.COMMON_WEIGHTS.get(subspeciesName)));
		}

		for(String subspeciesName : Subspecies.SUBSPECIES_NAMES) {
			Map<String, ForgeConfigSpec.ConfigValue<Double>> statMultipliers = new HashMap<>();
			for (String statName : CreatureStats.STAT_NAMES) {
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

				statMultipliers.put(statName, builder
						.comment("Stat multiplier for " + statName + " for " + subspeciesName + " subspecies.")
						.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.multipliers." + subspeciesName + "." + statName)
						.define("creature.subspecies.multipliers." + subspeciesName + "." + statName, defaultValue));
			}
			this.subspeciesMultipliers.put(subspeciesName, statMultipliers);
		}

		uncommonDropScale = builder.comment("When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its item drops amount is multiplied by this value.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.uncommonDropScale")
				.define("creature.subspecies.uncommonDropScale", 2);
		rareDropScale = builder.comment("When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its item drops amount is multiplied by this value.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.rareDropScale")
				.define("creature.subspecies.rareDropScale", 5);

		uncommonExperienceScale = builder.comment("When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its experience amount is multiplied by this value.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.uncommonExperienceScale")
				.define("creature.subspecies.uncommonExperienceScale", 2.0D);
		rareExperienceScale = builder.comment("When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its experience amount is multiplied by this value.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.rareExperienceScale")
				.define("creature.subspecies.rareExperienceScale", 10.0D);

		uncommonSpawnDayMin = builder.comment("The minimum amount of days before uncommon species start to spawn.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.uncommonSpawnDayMin")
				.define("creature.subspecies.uncommonSpawnDayMin", 0);
		rareSpawnDayMin = builder.comment("The minimum amount of days before rare species start to spawn.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.rareSpawnDayMin")
				.define("creature.subspecies.rareSpawnDayMin", 0);

		rareHealthBars = builder.comment("If set to true, rare subspecies such as the Lunar Grue or Celestial Geonach will display boss health bars.")
				.translation(CoreConfig.CONFIG_PREFIX + "creature.subspecies.rareHealthBars")
				.define("creature.subspecies.rareHealthBars", false);
	}
}
