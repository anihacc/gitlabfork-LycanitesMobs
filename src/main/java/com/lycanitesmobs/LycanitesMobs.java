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
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.helpers.LMReflectionHelper;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.*;
import com.lycanitesmobs.core.item.consumable.ItemHalloweenTreat;
import com.lycanitesmobs.core.item.consumable.ItemWinterGift;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mods.DLDungeons;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.pets.DonationFamiliars;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import com.lycanitesmobs.core.worldgen.WorldGeneratorDungeon;
import com.lycanitesmobs.core.worldgen.WorldGeneratorFluids;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
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
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Arrays;

@Mod(
		modid = LycanitesMobs.modid,
		name = LycanitesMobs.name,
		version = LycanitesMobs.version,
		useMetadata = false,
		acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions,
		dependencies = "after:" + Thaumcraft.modid
)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String versionNumber = "2.0.6.7";
	public static final String versionMC = "1.12.2";
	public static final String version = versionNumber + " - MC " + versionMC;
	public static final String website = "https://lycanitesmobs.com";
	public static final String websiteAPI = "https://api.lycanitesmobs.com";
	public static final String twitter = "https://twitter.com/Lycanite05";
	public static final String patreon = "https://www.patreon.com/lycanite";
	public static final String discord = "https://discord.gg/bFpV3z4";
	public static final String acceptedMinecraftVersions = "[1.12,1.13)";
	
	public static final PacketHandler packetHandler = new PacketHandler();

    public static ModInfo modInfo;
    public static ConfigBase config;
	
	// Instance:
	@Mod.Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.client.ClientProxy", serverSide="com.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;

    // Capabilities:
    @CapabilityInject(IExtendedEntity.class)
    public static final Capability<IExtendedEntity> EXTENDED_ENTITY = null;
    @CapabilityInject(IExtendedPlayer.class)
    public static final Capability<IExtendedPlayer> EXTENDED_PLAYER = null;
	
	// Creative Tabs:
    public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), modid + ".items");
    public static final CreativeTabs blocksTab = new CreativeTabBlocks(CreativeTabs.getNextID(), modid + ".blocks");
	public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), modid + ".creatures");
	public static final CreativeTabs chargesTab = new CreativeTabCharges(CreativeTabs.getNextID(), modid + ".charges");
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
	 * The first initialization, load everything for this mod, the order that things are loaded within this method is important.
	 * @param event
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modInfo = new ModInfo(this, name, 1000);
		ObjectManager.setCurrentModInfo(modInfo);

		// Localisation:
		proxy.initLanguageManager();

		// Config:
        ConfigBase.versionCheck("2.0.0.0", version);
		config = ConfigBase.getConfig(modInfo, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		config.setCategoryComment("Extras", "Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");
		config.setCategoryComment("Player", "Settings for player related stats and features.");

		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new GameEventListener());
		MinecraftForge.EVENT_BUS.register(CreatureManager.getInstance());
		MinecraftForge.EVENT_BUS.register(ProjectileManager.getInstance());

		// Familiars:
		DonationFamiliars.instance.familiarBlacklist = new ArrayList<>();
		String[] familiarBlacklist = config.getStringList("Extras", "Familiar Username Blacklist", new String[] {"Jbams"}, "Donation Familiars help support the development of this mod but can be turned of for individual players be adding their username to this list.");
		DonationFamiliars.instance.familiarBlacklist.addAll(Arrays.asList(familiarBlacklist));

		// Version Checker:
		VersionChecker.enabled = config.getBool("Extras", "Version Checker", VersionChecker.enabled, "Set to false to disable the version checker.");

		// Network:
		packetHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		// Change Health Limit:
		LMReflectionHelper.setPrivateFinalValue(RangedAttribute.class, (RangedAttribute)SharedMonsterAttributes.MAX_HEALTH, 100000, "maximumValue", "field_111118_b");

		// Admin Entity Removal Tool:
        config.setCategoryComment("Admin", "Special tools for server admins.");
        ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = config.getStringList("Admin", "Force Remove Entity Names", new String[0], "Here you can add a list of entity IDs for entity that you want to be forcefully removed.");
        if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
            logInfo("", "Lycanites Mobs will forcefully remove the following entities based on their registered IDs:");
            for (String removeEntityID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS)
                logInfo("", removeEntityID);
        }
        ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = config.getInt("Admin", "Force Remove Entity Ticks", 40, "How many ticks it takes for an entity to be forcefully removed (1 second = 20 ticks). This only applies to EntityLiving, other entities are instantly removed.");

		// Elements:
		ElementManager.getInstance().loadConfig();
		ElementManager.getInstance().loadAllFromJSON(modInfo);

		// Potion Effects:
		this.potionEffects = new PotionEffects();
		this.potionEffects.init(config);

		// Blocks and Items:
		ObjectLists.createVanillaLists();
		ObjectLists.createCustomItems();
		ItemManager.getInstance().startup(modInfo);
		EquipmentPartManager.getInstance().loadAllFromJSON(modInfo);

		// Tile Entities:
		ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class);
		ObjectManager.addTileEntity("equipmentforge", TileEntityEquipmentForge.class);
		ObjectManager.addTileEntity("equipment_infuser", EquipmentInfuserTileEntity.class);

		// Special Entities:
		ObjectManager.addSpecialEntity("hitarea", EntityHitArea.class);

		// Creatures:
		CapabilityManager.INSTANCE.register(IExtendedPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer.class);
		CapabilityManager.INSTANCE.register(IExtendedEntity.class, new ExtendedEntityStorage(), ExtendedEntity.class);
		CreatureManager.getInstance().startup(modInfo);

		// Projectiles:
		ProjectileManager.getInstance().startup(modInfo);

		// Spawners:
		FMLCommonHandler.instance().bus().register(SpawnerEventListener.getInstance());
		SpawnerManager.getInstance().loadAllFromJSON();

		// Altars:
		AltarInfo.loadGlobalSettings();
		AltarInfo.createAltars();

		// Mob Events:
		FMLCommonHandler.instance().bus().register(MobEventManager.getInstance());
		FMLCommonHandler.instance().bus().register(MobEventListener.getInstance());
		MobEventManager.getInstance().loadConfig();
		MobEventManager.getInstance().loadAllFromJSON(modInfo);

		// World Generators:
		GameRegistry.registerWorldGenerator(new WorldGeneratorFluids(), 0);

		// Dungeons:
		DungeonManager.getInstance().loadAllFromJSON();
		dungeonGenerator = new WorldGeneratorDungeon();
		GameRegistry.registerWorldGenerator(dungeonGenerator, 1000);

		// Treat Lists:
		ItemHalloweenTreat.createObjectLists();
		ItemWinterGift.createObjectLists();

		// Client:
		proxy.registerEvents();
		proxy.registerRenders(modInfo);
		proxy.registerTextures(); // Includes Beastiary Tab

		// Mod Support:
		DLDungeons.init();
	}


	/**
	 * The second initialization phase, only used to register some things.
	 * @param event The forge init event.
	 */
	@Mod.EventHandler
    public void init(FMLInitializationEvent event) {
		// Client:
		proxy.registerModels(modInfo); // Here for Item Color
	}


	/**
	 * Third initialization phase, no longer needed.
	 * @param event The forge init event.
	 */
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		// Creatures:
		CreatureManager.getInstance().lateStartup(modInfo);

		/*/ Test All Drops:
		for (ItemDrop itemDrop: ItemDrop.allDrops) {
			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(itemDrop.itemId)); // TESTING
			if (item == null) {
				LycanitesMobs.logWarning("", "[JSON] Bad item id: " + itemDrop.itemId);
			}
		}*/
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
    public static void logInfo(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Info] [" + key + "] " + message);
        }
    }


	/**
	 * Prints an info debug into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void logDebug(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Debug] [" + key + "] " + message);
        }
    }


	/**
	 * Prints an info warning into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void logWarning(String key, String message) {
		if("".equals(key) || config.getBool("Debug", key, false)) {
			System.err.println("[LycanitesMobs] [WARNING] [" + key + "] " + message);
		}
	}
}
