package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModInfo {
	/** A map containing all groups by their name. **/
	public static Map<String, ModInfo> modInfos = new HashMap<>();

    // ========== Group General ==========
	/** The mod this info belongs to. **/
	public Object mod;

    /** A unique Order ID for this mod, used when all groups need to be displayed in an order. Orders above 99 will be ignored. **/
    public int order;
	
    /** The name of this mod, normally displayed in the config. **/
    public String name;

    /** The filename of this mod, used for assets, config, etc. This should usually match the mod ID. **/
    public String filename;
    
    // ========== Mobs ==========
    /** The next available ID for registering a mob. **/
    protected int nextMobID = 0;

    /** The next available ID for registering a projectile. **/
	protected int nextProjectileID = 1000;

    // ========== Projectiles ==========
    /** A list of all Projectile Classes assigned to this mod. **/
    public List<Class> projectileClasses = new ArrayList<>();

    // ========== Special Entities ==========
    /** A list of all Special Entity Classes assigned to this mod (to be rendered invisible). **/
    public List<Class> specialClasses = new ArrayList<>();
	
	// ========== Spawn Dimensions ========== TODO Remove
    /** A comma separated list of dimensions that mobs in this mod spawn in. As read from the config **/
    public String dimensionEntries = "";
	
	/** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
	public boolean dimensionWhitelist = false;

    // ========== Spawn Biomes ========== TODO Remove
    /** The list of biomes that mobs in this mod spawn. As read from the config. Stores biome tags and special tags. **/
    public String biomeEntries = "";
	
	/** The list of biomes that mobs in this mod spawn. This stores the actual biomes not biome tags. **/
	public Biome[] biomes = new Biome[0];

    /** The list of biome types that mobs in this mod can spawn in. **/
    public BiomeDictionary.Type[] biomeTypesAllowed;

    /** The list of biome types that mobs in this mod cannot spawn in. **/
    public BiomeDictionary.Type[] biomeTypesDenied;


    // ==================================================
    //                     Constructor
    // ==================================================
    public ModInfo(Object mod, String name, int order) {
    	this.mod = mod;
        this.name = name;
        this.filename = name.toLowerCase().replace(" ", "");
        this.order = order;

        modInfos.put(this.name, this);
    }


	/**
	 * Returns the display title of this mod.
	 * @return The text to display.
	 */
	public String getTitle() {
    	return LanguageManager.translate(this.filename + ".name");
	}


    // ==================================================
    //                    Entity IDs
    // ==================================================
    public int getNextMobID() {
        int id = this.nextMobID;
        this.nextMobID++;
        return id;
    }

    public int getNextProjectileID() {
        int id = this.nextProjectileID;
        this.nextProjectileID++;
        return id;
    }
}
