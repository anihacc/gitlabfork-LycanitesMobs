package com.lycanitesmobs;

import com.lycanitesmobs.core.Effects;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.network.PacketHandler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LycanitesMobs implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "lycanitesmobs";
	public static final String MODID = MOD_ID; // TODO Remove
	public static final String name = "Lycanites Mobs";
	public static final String versionNumber = "2.2.3.2";
	public static final String versionMC = "1.15.2";
	public static final String version = versionNumber + " - MC " + versionMC;
	public static final String website = "https://lycanitesmobs.com";
	public static final String websiteAPI = "https://api.lycanitesmobs.com";
	public static final String twitter = "https://twitter.com/Lycanite05";
	public static final String patreon = "https://www.patreon.com/lycanite";
	public static final String discord = "https://discord.gg/bFpV3z4";

	public static final PacketHandler packetHandler = new PacketHandler();

	public static ModInfo modInfo;

	public static Effects EFFECTS;

	@Override
	public void onInitialize() {
		System.out.println("Lycanites Mobs is coming top Fabric, maybe? OwO?");
	}

	/**
	 * Prints an info message into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void logInfo(String key, String message) {
		if("".equals(key)) {
			LOGGER.info("[LycanitesMobs] [Info] [" + key + "] " + message);
		}
	}

	/**
	 * Prints an info debug into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void logDebug(String key, String message) {
		if("".equals(key)) {
			LOGGER.debug("[LycanitesMobs] [Debug] [" + key + "] " + message);
		}
	}

	/**
	 * Prints an info warning into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void logWarning(String key, String message) {
		if("".equals(key)) {
			LOGGER.warn("[LycanitesMobs] [WARNING] [" + key + "] " + message);
		}
	}

	/**
	 * Prints an error message into the console.
	 * @param message The error message to print.
	 */
	public static void logError(String message) {
		LOGGER.error("[LycanitesMobs] " + message);
	}
}
