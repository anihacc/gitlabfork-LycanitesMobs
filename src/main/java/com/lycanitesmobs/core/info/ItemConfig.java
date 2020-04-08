package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.LycanitesMobs;

public class ItemConfig {
    // ========== Global Settings ==========
    public static double seasonalItemDropChance = 0.1F;
    public static boolean removeOnNoFireTick = true;

    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
    public static void loadGlobalSettings() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
        seasonalItemDropChance = config.getDouble("Seasonal Item Drop Chance", "Seasonal", seasonalItemDropChance, "The chance of seasonal items dropping such as Winter Gifts. Can be 0-1, 0.25 would be 25%. Set to 0 to disable these drops all together.");

        config.setCategoryComment("Fire", "Special settings for fire blocks, etc.");
        removeOnNoFireTick = config.getBool("Fire", "Remove On No Fire Tick", removeOnNoFireTick, "If set to false, when the doFireTick gamerule is set to false, instead of removing all custom fire such as Hellfire, the fire simply stops spreading instead, this is useful for decorative fire on adventure maps and servers.");
}
}