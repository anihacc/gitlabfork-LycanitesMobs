package com.lycanitesmobs.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigExtra {
	public static ConfigExtra INSTANCE = new ConfigExtra(CoreConfig.BUILDER);

	public final ForgeConfigSpec.ConfigValue<Boolean> versionCheckerEnabled;
	public final ForgeConfigSpec.ConfigValue<Boolean> disableNausea;
	public final ForgeConfigSpec.ConfigValue<String[]> familiarBlacklist;

	public ConfigExtra(ForgeConfigSpec.Builder builder) {
		builder.push("Extra");
		builder.comment("Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");

		this.versionCheckerEnabled = builder
				.comment("Set to false to disable the version checker.")
				.translation(CoreConfig.CONFIG_PREFIX + "version.checker")
				.define("version.checker", true);

		this.disableNausea = builder
				.comment("Set to true to disable the nausea debuff on players.")
				.translation(CoreConfig.CONFIG_PREFIX + "disableNausea")
				.define("disableNausea", false);

		this.familiarBlacklist = builder
				.comment("Donation Familiars help support the development of this mod but can be turned of for individual players be adding their username to this list.")
				.translation(CoreConfig.CONFIG_PREFIX + "familiars.blacklist")
				.define("familiars.blacklist", new String[] {"Jbams"});
	}
}
