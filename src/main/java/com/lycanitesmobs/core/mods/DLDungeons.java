package com.lycanitesmobs.core.mods;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

/** This is a wrapper class for the DLDungeons API, uses reflection for now until I find a better way. **/
public class DLDungeons {
	public static String API_CLASS_NAME = "jaredbgreat.dldungeons.api.DLDungeonsAPI";
	
	// ========== Initialize ==========
	/** Called if the DLDungeons API is found, this then asks the API if DLDungeons is ready to go, if so, it updates MobInfo so that mobs will register their themes. **/
	public static void init() {
		if(!Loader.isModLoaded("DLDungeonsJBG") && !Loader.isModLoaded("dldungeonsjbg"))
			return;
		
		LycanitesMobs.logInfo("", "Doomlike Dungeons Mod Detected...");
		
		Class dlDungeonsAPI = null;
		Method isLoaded = null;
		try {
			dlDungeonsAPI = Class.forName(API_CLASS_NAME);
			isLoaded = dlDungeonsAPI.getMethod("isLoaded");
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "Unable to find DLDungeons API Class/Method via reflection:");
			e.printStackTrace();
		}
		
		if(dlDungeonsAPI == null || isLoaded == null)
			return;
		
		try {
			if(((Boolean)isLoaded.invoke(null)).booleanValue())
				CreatureManager.getInstance().dlDungeonsLoaded = true;
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "Unable to invoke DLDungeons API method isLoaded():");
			e.printStackTrace();
		}
	}
	
	
	// ========== Add Mob ==========
	/**
	 * Adds a mob entry to the DLDungeon Themes.
	 * Themes are: FOREST, PLAINS, MOUNTAIN, SWAMP, WATER, DESERT, WASTELAND, JUNGLE, FROZEN, NETHER, END, MUSHROOM, MAGICAL, DUNGEON, NECRO, URBAN, FIERY, SHADOW, PARADISE
	 * @param creatureInfo The creature info of the mob, this will be used to get the themes amongst other things.
	 */
	public static void addMob(CreatureInfo creatureInfo) {
		String mobName = creatureInfo.getEntityId();

		// Get Themes:
		String themes = "";
		boolean first = true;
		for(String biomeTag : creatureInfo.creatureSpawn.biomeTags) {
			if(!first) {
				themes += ",";
			}
			first = false;

			if (biomeTag.charAt(0) == '-' || biomeTag.charAt(0) == '+') {
				biomeTag = biomeTag.substring(1);
			}

			if("DRY".equalsIgnoreCase(biomeTag)) {
				biomeTag = "DESERT";
			}
			else if("COLD".equalsIgnoreCase(biomeTag)) {
				biomeTag = "FROZEN";
			}
			else if("VOID".equalsIgnoreCase(biomeTag)) {
				biomeTag = "SHADOW";
			}
			else if("SPOOKY".equalsIgnoreCase(biomeTag)) {
				biomeTag = "NECRO";
			}

			themes += biomeTag;
		}

		if(!"".equalsIgnoreCase(themes) && creatureInfo.dungeonLevel > 0) {
			try {
				Class dlDungeonsAPI = Class.forName(API_CLASS_NAME);
				Method addMob = dlDungeonsAPI.getMethod("addMob", String.class, int.class, String[].class);
				addMob.invoke(null, mobName, creatureInfo.dungeonLevel, themes.split(","));
				LycanitesMobs.logDebug("MobSetup", "[DLDungeons] Added " + mobName + " with the level: " + creatureInfo.dungeonLevel + " and themes: " + themes);
			} catch(Exception e) {
				LycanitesMobs.logWarning("", "Unable to add " + mobName + " to DLDungeons API:");
			}
		}
	}
}
