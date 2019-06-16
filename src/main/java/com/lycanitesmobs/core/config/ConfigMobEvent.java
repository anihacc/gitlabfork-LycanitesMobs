package com.lycanitesmobs.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigMobEvent {
	public static ConfigMobEvent INSTANCE = new ConfigMobEvent(CoreConfig.BUILDER);

	public final ForgeConfigSpec.ConfigValue<Boolean> mobEventsEnabled;
	public final ForgeConfigSpec.ConfigValue<Boolean> mobEventsRandom;
	public final ForgeConfigSpec.ConfigValue<Integer> defaultMobDuration;
	public final ForgeConfigSpec.ConfigValue<Integer> minEventsRandomDay;
	public final ForgeConfigSpec.ConfigValue<Integer> minTicksUntilEvent;
	public final ForgeConfigSpec.ConfigValue<Integer> maxTicksUntilEvent;

	public ConfigMobEvent(ForgeConfigSpec.Builder builder) {
		builder.push("Mob Events");
		builder.comment("These are various settings that apply to all mob events.");

		this.mobEventsEnabled = builder
				.comment("Set to false to completely disable the entire event.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", true);

		this.mobEventsRandom = builder
				.comment("Set to false to disable random mob events for every world.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", false);

		this.defaultMobDuration = builder
				.comment("The default temporary time applied to mobs spawned from events, where it will forcefully despawn after the specified time (in ticks). MobSpawns can override this.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", 12000);

		this.minEventsRandomDay = builder
				.comment("If random events are enabled, they wont occur until this day is reached. Set to 0 to have random events enabled from the start of a world.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", 0);

		this.minTicksUntilEvent = builder
				.comment("Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", 60 * 60 * 20);

		this.maxTicksUntilEvent = builder
				.comment("Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.")
				.translation(CoreConfig.CONFIG_PREFIX + "mobevents.enabled")
				.define("mobevents.enabled", 120 * 60 * 20);
	}
}