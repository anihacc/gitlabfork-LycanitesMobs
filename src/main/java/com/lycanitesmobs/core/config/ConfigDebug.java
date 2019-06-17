package com.lycanitesmobs.core.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigDebug {
	public static ConfigDebug INSTANCE;

	public final ForgeConfigSpec.ConfigValue<Boolean> elements;
	public final ForgeConfigSpec.ConfigValue<Boolean> creatures;
	public final ForgeConfigSpec.ConfigValue<Boolean> spawning;
	public final ForgeConfigSpec.ConfigValue<Boolean> items;
	public final ForgeConfigSpec.ConfigValue<Boolean> projectiles;
	public final ForgeConfigSpec.ConfigValue<Boolean> equipment;
	public final ForgeConfigSpec.ConfigValue<Boolean> dungeons;
	public final ForgeConfigSpec.ConfigValue<Boolean> mobevents;

	public final ForgeConfigSpec.ConfigValue<List<? extends String>> enabled;

	public ConfigDebug(ForgeConfigSpec.Builder builder) {
		builder.push("Debug");
		builder.comment("Set debug options to true to show extra debugging information in the console.");

		this.elements = builder
				.comment("Shows debugging info for Elements.")
				.translation(CoreConfig.CONFIG_PREFIX + "elements")
				.define("elements", false);

		this.creatures = builder
				.comment("Shows debugging info for Creatures.")
				.translation(CoreConfig.CONFIG_PREFIX + "creatures")
				.define("creatures", false);

		this.spawning = builder
				.comment("Shows debugging info for Spawning.")
				.translation(CoreConfig.CONFIG_PREFIX + "spawning")
				.define("spawning", false);

		this.items = builder
				.comment("Shows debugging info for Items.")
				.translation(CoreConfig.CONFIG_PREFIX + "items")
				.define("items", false);

		this.projectiles = builder
				.comment("Shows debugging info for Projectiles.")
				.translation(CoreConfig.CONFIG_PREFIX + "projectiles")
				.define("projectiles", false);

		this.equipment = builder
				.comment("Shows debugging info for Equipment.")
				.translation(CoreConfig.CONFIG_PREFIX + "equipment")
				.define("equipment", false);

		this.dungeons = builder
				.comment("Shows debugging info for Dungeons.")
				.translation(CoreConfig.CONFIG_PREFIX + "dungeons")
				.define("dungeons", false);

		this.mobevents = builder
				.comment("Shows debugging info for Mob Events.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents")
				.define("mobevents", false);

		this.enabled = builder
				.comment("Shows debugging info for Mob Events.")
				.translation(CoreConfig.CONFIG_PREFIX + "debug.enabled")
				.define("enabled", Lists.newArrayList(), o -> o instanceof String);

		builder.pop();
	}

	public boolean isEnabled(String debugKey) {
		if(this.enabled.get().size() == 0)
			return false;

		for(String enabledDebugKey : this.enabled.get()) {
			if(debugKey.equals(enabledDebugKey))
				return true;
		}

		return false;
	}
}
