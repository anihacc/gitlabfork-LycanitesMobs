package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.LycanitesMobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemConfig {
    public static double seasonalItemDropChance = 0.1F;
    public static boolean removeOnNoFireTick = true;

    public static int lowEquipmentRepairAmount = 50;
    public static int mediumEquipmentRepairAmount = 100;
    public static int highEquipmentRepairAmount = 500;

    public static List<? extends String> lowEquipmentSharpnessItems = new ArrayList<>();
    public static List<? extends String> mediumEquipmentSharpnessItems = new ArrayList<>();
    public static List<? extends String> highEquipmentSharpnessItems = new ArrayList<>();
    public static List<? extends String> maxEquipmentSharpnessItems = new ArrayList<>();

    public static List<? extends String> lowEquipmentManaItems = new ArrayList<>();
    public static List<? extends String> mediumEquipmentManaItems = new ArrayList<>();
    public static List<? extends String> highEquipmentManaItems = new ArrayList<>();
    public static List<? extends String> maxEquipmentManaItems = new ArrayList<>();

    public static void loadGlobalSettings() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
        seasonalItemDropChance = config.getDouble("Seasonal Item Drop Chance", "Seasonal", seasonalItemDropChance, "The chance of seasonal items dropping such as Winter Gifts. Can be 0-1, 0.25 would be 25%. Set to 0 to disable these drops all together.");

        config.setCategoryComment("Fire", "Special settings for fire blocks, etc.");
        removeOnNoFireTick = config.getBool("Fire", "Remove On No Fire Tick", removeOnNoFireTick, "If set to false, when the doFireTick gamerule is set to false, instead of removing all custom fire such as Hellfire, the fire simply stops spreading instead, this is useful for decorative fire on adventure maps and servers.");

        config.setCategoryComment("Equipment", "Settings for Crafted Equipment");
        lowEquipmentRepairAmount = config.getInt("Equipment", "Equipment Repair Amount Low", lowEquipmentRepairAmount, "The amount of Sharpness or Mana Low tier Equipment repair items give.");
        mediumEquipmentRepairAmount = config.getInt("Equipment", "Equipment Repair Amount Medium", mediumEquipmentRepairAmount, "The amount of Sharpness or Mana Medium tier Equipment repair items give.");
        highEquipmentRepairAmount = config.getInt("Equipment", "Equipment Repair Amount High", highEquipmentRepairAmount, "The amount of Sharpness or Mana High tier Equipment repair items give.");

        // Sharpness:
        lowEquipmentSharpnessItems = Arrays.asList(config.getStringList("Equipment", "Equipment Sharpness Items Low", new String[] {
                "minecraft:flint",
                "minecraft:bone"
        }, "A list of ids for items that grant a low amount of Equipment Sharpness."));
        mediumEquipmentSharpnessItems = Arrays.asList(config.getStringList("Equipment", "Equipment Sharpness Items Medium", new String[] {
                "minecraft:iron_ingot",
                "minecraft:quartz"
        }, "A list of ids for items that grant a medium amount of Equipment Sharpness."));
        highEquipmentSharpnessItems = Arrays.asList(config.getStringList("Equipment", "Equipment Sharpness Items High", new String[] {
                "minecraft:diamond",
                "minecraft:emerald",
                "minecraft:prismarine_shard"
        }, "A list of ids for items that grant a high amount of Equipment Sharpness."));
        maxEquipmentSharpnessItems = Arrays.asList(config.getStringList("Equipment", "Equipment Sharpness Items Max", new String[] {
                "minecraft:prismarine_crystal"
        }, "A list of ids for items that grant a max amount of Equipment Sharpness."));

        // Mana:
        lowEquipmentManaItems = Arrays.asList(config.getStringList("Equipment", "Equipment Mana Items Low", new String[] {
                "minecraft:redstone",
                "minecraft:slime_ball"
        }, "A list of ids for items that grant a low amount of Equipment Mana."));
        mediumEquipmentManaItems = Arrays.asList(config.getStringList("Equipment", "Equipment Mana Items Medium", new String[] {
                "minecraft:lapis_lazuli",
                "minecraft:blaze_powder",
                "minecraft:gunpowder",
                "lycanitesmobs:frostyfur",
                "lycanitesmobs:poisongland",
                "lycanitesmobs:geistliver"
        }, "A list of ids for items that grant a medium amount of Equipment Mana."));
        highEquipmentManaItems = Arrays.asList(config.getStringList("Equipment", "Equipment Mana Items High", new String[] {
                "minecraft:experience_bottle",
                "minecraft:magma_cream"
        }, "A list of ids for items that grant a high amount of Equipment Mana."));
        maxEquipmentManaItems = Arrays.asList(config.getStringList("Equipment", "Equipment Mana Items Max", new String[] {
                "minecraft:nether_star"
        }, "A list of ids for items that grant a max amount of Equipment Mana."));
}
}