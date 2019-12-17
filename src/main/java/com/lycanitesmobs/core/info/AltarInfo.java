package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.altar.*;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.mobevent.trigger.AltarMobEventTrigger;
import com.lycanitesmobs.core.worldgen.mobevents.AmalgalichStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.AsmodeusStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.RahovartStructureBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class AltarInfo {
    // ========== Global Settings ==========
    /** If false, altars wont work. **/
    public static boolean altarsEnabled = true;

    /** If set to true, Altars will only activate in dimensions that the monster spawned or event started is allowed in. **/
    public static boolean checkDimensions = false;

    /** A static map containing all the global multipliers for each stat applied to rare subspecies spawned via Altars. **/
    public static Map<String, Double> rareSubspeciesMutlipliers = new HashMap<String, Double>();

    /** A list of all Altars, typically cycled through when a Soulkey is used. **/
    public static Map<String, AltarInfo> altars = new HashMap<String, AltarInfo>();


    // ========== Properties ==========
    public String name = "Altar";

    public AltarMobEventTrigger mobEventTrigger;


    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
    public static void loadGlobalSettings() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
        config.setCategoryComment("Altars", "Altars are block arrangements that can be activated using Soulkeys to summon rare subspecies as mini bosses or trigger events including boss events.");
        altarsEnabled = config.getBool("Altars", "Altars Enabled", altarsEnabled, "Set to false to disable altars, Soulkeys can still be crafted but wont work on Altars.");
        checkDimensions = config.getBool("Altars", "Check Dimension", checkDimensions, "If set to true, Altars will only activate in dimensions that the monster spawned or event started is allowed in.");


        String[] statNames = new String[] {"Health", "Defense", "Speed", "Damage", "Haste", "Effect", "Pierce"};
        rareSubspeciesMutlipliers = new HashMap<String, Double>();
        for(String statName : statNames) {
            double defaultValue = 4.0D;
            if("Health".equalsIgnoreCase(statName))
                defaultValue = 10D;
            if("Speed".equalsIgnoreCase(statName))
                defaultValue = 1.5D;
            rareSubspeciesMutlipliers.put(statName.toUpperCase(), config.getDouble("Altars", statName + " Altar Stat Multiplier", defaultValue));
        }
    }


    // ==================================================
    //                    Enabled
    // ==================================================
    public static boolean checkAltarsEnabled() {
        if(altars.size() < 1)
            return false;
        return altarsEnabled;
    }


    // ==================================================
    //                    Altar List
    // ==================================================
    public static void addAltar(AltarInfo altarInfo) {
        altars.put(altarInfo.name.toLowerCase(), altarInfo);
    }

    public static AltarInfo getAltar(String name) {
        if(altars.containsKey(name.toLowerCase())) {
            return altars.get(name.toLowerCase());
        }
        return null;
    }


    // ==================================================
    //                   Create Altars
    // ==================================================
    public static void createAltars() {
        AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
        addAltar(ebonCacodemonAltar);

        AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
        addAltar(rahovartAltar);
        StructureBuilder.addStructureBuilder(new RahovartStructureBuilder());

        AltarInfo asmodeusAltar = new AltarInfoAsmodeus("AsmodeusAltar");
        addAltar(asmodeusAltar);
        StructureBuilder.addStructureBuilder(new AsmodeusStructureBuilder());

        AltarInfo amalgalichAltar = new AltarInfoAmalgalich("AmalgalichAltar");
        addAltar(amalgalichAltar);
        StructureBuilder.addStructureBuilder(new AmalgalichStructureBuilder());

        AltarInfo umberLobberAltar = new AltarInfoUmberLobber("UmberLobberAltar");
        addAltar(umberLobberAltar);

        AltarInfo celestialGeonachAltar = new AltarInfoCelestialGeonach("CelestialGeonachAltar");
        addAltar(celestialGeonachAltar);

        AltarInfo lunarGrueAltar = new AltarInfoLunarGrue("LunarGrueAltar");
        addAltar(lunarGrueAltar);
    }


    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfo(String name) {
        this.name = name;
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called before all other checks to see if the player has permission to build in the area of this Altar using the BlockPlaceEvent. **/
    public boolean checkBlockEvent(Entity entity, World world, BlockPos pos) {
        return true;
    }

    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    public boolean quickCheck(Entity entity, World world, BlockPos pos) {
        return false;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    public boolean fullCheck(Entity entity, World world, BlockPos pos) {
        return false;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. If false is returned then the activation did not work, this is the place to check for things like dimensions. **/
    public boolean activate(Entity entity, World world, BlockPos pos, int level) {
    	if(this.mobEventTrigger == null)
        	return false;
    	return this.mobEventTrigger.onActivate(entity, world, pos, level);
    }


    // ==================================================
    //                     Utility
    // ==================================================
    public BlockPos getFacingPosition(BlockPos pos, double distance, double angle) {
        double xAmount = -Math.sin(Math.toRadians(angle));
        double zAmount = Math.cos(Math.toRadians(angle));
        BlockPos facingPos = new BlockPos(
            Math.round((double)pos.getX() + (distance * xAmount)),
            pos.getY(),
            Math.round((double) pos.getZ() + (distance * zAmount))
        );
        return facingPos;
    }
}
