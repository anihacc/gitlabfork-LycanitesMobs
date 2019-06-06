package com.lycanitesmobs;

import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import com.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.command.CommandMain;
import com.lycanitesmobs.core.compatibility.Thaumcraft;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.helpers.LMReflectionHelper;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.info.altar.*;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.CreativeTabBlocks;
import com.lycanitesmobs.core.item.CreativeTabCreatures;
import com.lycanitesmobs.core.item.CreativeTabEquipmentParts;
import com.lycanitesmobs.core.item.CreativeTabItems;
import com.lycanitesmobs.core.item.consumable.ItemHalloweenTreat;
import com.lycanitesmobs.core.item.consumable.ItemWinterGift;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.mods.DLDungeons;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.pets.DonationFamiliars;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import com.lycanitesmobs.core.worldgen.WorldGeneratorDungeon;
import com.lycanitesmobs.core.worldgen.WorldGeneratorFluids;
import com.lycanitesmobs.core.worldgen.mobevents.AsmodeusStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.RahovartStructureBuilder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Arrays;

@Mod(
		modid = LycanitesMobs.modid,
		name = LycanitesMobs.name,
		version = LycanitesMobs.version,
		useMetadata = false,
		acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions,
		dependencies="after:" + Thaumcraft.modid
)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String versionNumber = "2.0.0.0";
	public static final String versionMC = "1.12.2";
	public static final String version = versionNumber + " - MC " + versionMC;
	public static final String website = "http://lycanitesmobs.com";
	public static final String websiteAPI = "http://api.lycanitesmobs.com";
	public static final String websitePatreon = "https://www.patreon.com/lycanite";
	public static final String acceptedMinecraftVersions = "[1.12,1.13)";
	
	public static final PacketHandler packetHandler = new PacketHandler();

    public static ModInfo modInfo;
    public static ConfigBase config;
	
	// Instance:
	@Mod.Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.ClientProxy", serverSide="com.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;

    // Capabilities:
    @CapabilityInject(IExtendedEntity.class)
    public static final Capability<IExtendedEntity> EXTENDED_ENTITY = null;
    @CapabilityInject(IExtendedPlayer.class)
    public static final Capability<IExtendedPlayer> EXTENDED_PLAYER = null;
	
	// Creative Tab:
    public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), modid + ".items");
    public static final CreativeTabs blocksTab = new CreativeTabBlocks(CreativeTabs.getNextID(), modid + ".blocks");
	public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), modid + ".creatures");
	public static final CreativeTabs equipmentPartsTab = new CreativeTabEquipmentParts(CreativeTabs.getNextID(), modid + ".equipmentparts");
	
	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";

	// Potion Effects:
	public PotionEffects potionEffects;

	// Dungeon System:
	public static WorldGeneratorDungeon dungeonGenerator;

	// Universal Bucket:
	static {
		FluidRegistry.enableUniversalBucket();
	}


	/**
	 * The first initialization, loads most configs and jsons and sets up most of the managers, event listeners, etc.
	 * @param event
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modInfo = new ModInfo(this, name, 1000); // TODO Remove groups.
		ObjectManager.setCurrentGroup(modInfo);

		// Localisation:
		proxy.initLanguageManager();

		// Config:
        ConfigBase.versionCheck("2.0.0.0", version);
		config = ConfigBase.getConfig(modInfo, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		config.setCategoryComment("Extras", "Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");

		// Familiars:
		DonationFamiliars.instance.familiarBlacklist = new ArrayList<>();
		String[] familiarBlacklist = config.getStringList("Extras", "Familiar Username Blacklist", new String[] {"Jbams"}, "Donation Familiars help support the development of this mod but can be turned of for individual players be adding their username to this list.");
		DonationFamiliars.instance.familiarBlacklist.addAll(Arrays.asList(familiarBlacklist));

		// Version Checker:
		VersionChecker.enabled = config.getBool("Extras", "Version Checker", VersionChecker.enabled, "Set to false to disable the version checker.");

		// Initialize Packet Handler:
		packetHandler.init();

		// Change Health Limit:
		LMReflectionHelper.setPrivateFinalValue(RangedAttribute.class, (RangedAttribute)SharedMonsterAttributes.MAX_HEALTH, 100000, "maximumValue", "field_111118_b");

		// Admin Entity Removal Tool:
        config.setCategoryComment("Admin", "Special tools for server admins.");
        ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = config.getStringList("Admin", "Force Remove Entity Names", new String[0], "Here you can add a list of entity IDs for entity that you want to be forcefully removed.");
        if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
            printInfo("", "Lycanites Mobs will forcefully remove the following entities based on their registered IDs:");
            for (String removeEntityID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS)
                printInfo("", removeEntityID);
        }
        ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = config.getInt("Admin", "Force Remove Entity Ticks", 40, "How many ticks it takes for an entity to be forcefully removed (1 second = 20 ticks). This only applies to EntityLiving, other entities are instantly removed.");

        // Potion Effects:
		this.potionEffects = new PotionEffects();
		this.potionEffects.init(config);

		// Elements:
		ElementManager.getInstance().loadConfig();
		ElementManager.getInstance().loadAllFromJSON(modInfo);

		// Creatures:
		CreatureManager.getInstance().loadConfig();
		CreatureManager.getInstance().loadCreatureTypesFromJSON(modInfo);
		CreatureManager.getInstance().loadCreaturesFromJSON(modInfo);

		// Projectiles:
		ProjectileManager.getInstance().loadAllFromJSON(modInfo);

		// Spawners:
		FMLCommonHandler.instance().bus().register(SpawnerEventListener.getInstance());

		// Mob Events:
		MobEventManager.getInstance().loadConfig();
		FMLCommonHandler.instance().bus().register(MobEventManager.getInstance());
		FMLCommonHandler.instance().bus().register(MobEventListener.getInstance());

        // Altars:
        AltarInfo.loadGlobalSettings();

        // Entity Capabilities:
        CapabilityManager.INSTANCE.register(IExtendedPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer.class);
        CapabilityManager.INSTANCE.register(IExtendedEntity.class, new ExtendedEntityStorage(), ExtendedEntity.class);

		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new EventListener());
        proxy.registerEvents();

		// Blocks and Items:
		CreatureManager.getInstance().createSpawnEggItems();
		ProjectileManager.getInstance().createChargeItems();
		ItemManager.getInstance().loadConfig();
		ItemManager.getInstance().loadItems();
		EquipmentPartManager.getInstance().loadAllFromJSON(modInfo);

		// Old Projectiles:
		ProjectileManager.getInstance().loadOldProjectiles();

		// Object Lists:
		ObjectLists.createCustomItems();
		ObjectLists.createLists();

        // Tile Entities:
        ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class);
		ObjectManager.addTileEntity("equipmentforge", TileEntityEquipmentForge.class);

		// Mod Support:
		DLDungeons.init();

		// Renderers:
		proxy.registerRenders(modInfo);
	}


	/**
	 * The second initialization phase, loads creatures and initializes special entities.
	 * @param event The forge init event.
	 */
	@Mod.EventHandler
    public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		// Item Ore Dictionary:
		ItemManager.getInstance().registerOres();

		// Register Creatures:
		CreatureManager.getInstance().registerAll(modInfo);

		// Special Entities:
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(new ResourceLocation(modInfo.filename, "summoningportal"), EntityPortal.class, "summoningportal", specialEntityID++, instance, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(modInfo.filename, "hitarea"), EntityHitArea.class, "hitarea", specialEntityID++, instance, 64, 1, true);
		AssetManager.addSound("effect_fear", modInfo, "effect.fear");

		// Altars:
		AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
		AltarInfo.addAltar(ebonCacodemonAltar);

		AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
		AltarInfo.addAltar(rahovartAltar);
		StructureBuilder.addStructureBuilder(new RahovartStructureBuilder());

		AltarInfo asmodeusAltar = new AltarInfoAsmodeus("AsmodeusAltar");
		AltarInfo.addAltar(asmodeusAltar);
		StructureBuilder.addStructureBuilder(new AsmodeusStructureBuilder());

		AltarInfo umberLobberAltar = new AltarInfoUmberLobber("UmberLobberAltar");
		AltarInfo.addAltar(umberLobberAltar);

		AltarInfo celestialGeonachAltar = new AltarInfoCelestialGeonach("CelestialGeonachAltar");
		AltarInfo.addAltar(celestialGeonachAltar);

		AltarInfo lunarGrueAltar = new AltarInfoLunarGrue("LunarGrueAltar");
		AltarInfo.addAltar(lunarGrueAltar);
	}


	/**
	 * Third initialization phase, registers tile entities and initializes creatures and then everything dependant on creatures being ready.
	 * @param event The forge init event.
	 */
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		// Register Tile Entities:
        proxy.registerTileEntities();

        // Init Creatures:
		CreatureManager.getInstance().initAll();

		// Load Spawners:
		SpawnerManager.getInstance().loadAllFromJSON();

		// Load Mob Events:
        MobEventManager.getInstance().loadAllFromJSON(modInfo);

        // Load Dungeons:
		DungeonManager.getInstance().loadAllFromJSON();
		dungeonGenerator = new WorldGeneratorDungeon();
		GameRegistry.registerWorldGenerator(dungeonGenerator, 1000);

		// World Generators:
		GameRegistry.registerWorldGenerator(new WorldGeneratorFluids(), 0);

        // Seasonal Item Lists:
        ItemHalloweenTreat.createObjectLists();
        ItemWinterGift.createObjectLists();

		// Register Assets:
		proxy.registerModels(modInfo);
		proxy.registerTextures();

        // Development:
		//LanguageManager.getInstance().generateLangs();
    }


	/**
	 * Server startup, adds commands and sets up other server only aspects.
	 * @param event Server starting event.
	 */
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		// Commands:
		event.registerServerCommand(new CommandMain());
	}


	/**
	 * Prints an info message into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
    public static void printInfo(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Info] [" + key + "] " + message);
        }
    }


	/**
	 * Prints an info debug into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void printDebug(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Debug] [" + key + "] " + message);
        }
    }


	/**
	 * Prints an info warning into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void printWarning(String key, String message) {
		if("".equals(key) || config.getBool("Debug", key, false)) {
			System.err.println("[LycanitesMobs] [WARNING] [" + key + "] " + message);
		}
	}
}
