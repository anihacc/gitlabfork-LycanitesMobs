package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigItem;

public class ItemConfig {
    // ========== Global Settings ==========
    public static double seasonalItemDropChance = 0.1D;
    public static boolean removeOnNoFireTick = true;

    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
    public static void loadGlobalSettings() {
        seasonalItemDropChance = ConfigItem.INSTANCE.seasonalDropChance.get();
        removeOnNoFireTick = ConfigItem.INSTANCE.removeOnNoFireTick.get();
    }
}