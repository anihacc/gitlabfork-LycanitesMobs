package com.lycanitesmobs.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigItem {
	public static ConfigItem INSTANCE;

	public final ForgeConfigSpec.ConfigValue<Double> seasonalDropChance;
	public final ForgeConfigSpec.ConfigValue<Boolean> removeOnNoFireTick;

	public ConfigItem(ForgeConfigSpec.Builder builder) {
		builder.push("Items");
		builder.comment("Global settings for blocks and items.");

		this.seasonalDropChance = builder
				.comment("The chance of seasonal items dropping such as Winter Gifts. Can be 0-1, 0.25 would be 25%. Set to 0 to disable these drops all together.")
				.translation(CoreConfig.CONFIG_PREFIX + "item.seasonalDropChance")
				.define("seasonalDropChance", 0.1D);

		this.removeOnNoFireTick = builder
				.comment("If set to false, when the doFireTick gamerule is set to false, instead of removing all custom fire such as Hellfire, the fire simply stops spreading instead, this is useful for decorative fire on adventure maps and servers.")
				.translation(CoreConfig.CONFIG_PREFIX + "item.removeOnNoFireTick")
				.define("removeOnNoFireTick", true);

		builder.pop();
	}
}
