package com.lycanitesmobs.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigAdmin {
	public static ConfigAdmin INSTANCE = new ConfigAdmin(CoreConfig.BUILDER);

	public final ForgeConfigSpec.ConfigValue<String[]> forceRemoveEntityIds;

	public ConfigAdmin(ForgeConfigSpec.Builder builder) {
		builder.push("Admin");
		builder.comment("Special tools for server admins.");

		this.forceRemoveEntityIds = builder
				.comment("Here you can add a list of entity IDs for entity that you want to be forcefully removed.")
				.translation(CoreConfig.CONFIG_PREFIX + "admin.removeEntityIds")
				.define("admin.removeEntityIds", new String[] {});
	}
}
