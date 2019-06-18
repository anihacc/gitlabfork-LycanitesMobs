package com.lycanitesmobs;

import com.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import com.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.config.ConfigDebug;
import com.lycanitesmobs.core.config.ConfigGeneral;
import com.lycanitesmobs.core.config.CoreConfig;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.helpers.LMReflectionHelper;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.consumable.ItemHalloweenTreat;
import com.lycanitesmobs.core.item.consumable.ItemWinterGift;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mods.DLDungeons;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("lycanitesmobs")
public class LycanitesMobs {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String versionNumber = "2.1.0.0";
	public static final String versionMC = "1.12.2";
	public static final String version = versionNumber + " - MC " + versionMC;
	public static final String website = "http://lycanitesmobs.com";
	public static final String websiteAPI = "http://api.lycanitesmobs.com";
	public static final String websitePatreon = "https://www.patreon.com/lycanite";
	public static final String acceptedMinecraftVersions = "[1.12,1.13)";

	public static final PacketHandler packetHandler = new PacketHandler();

	public static ModInfo modInfo;
	//public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	// Capabilities:
	@CapabilityInject(IExtendedEntity.class)
	public static final Capability<IExtendedEntity> EXTENDED_ENTITY = null;

	@CapabilityInject(IExtendedPlayer.class)
	public static final Capability<IExtendedPlayer> EXTENDED_PLAYER = null;

	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";

	// Potion Effects:
	public Effects effects;

	// Universal Bucket:
	static {
		FluidRegistry.enableUniversalBucket();
	}

	/**
	 * Constructor
	 */
	public LycanitesMobs() {
		modInfo = new ModInfo(this, name, 1000);

		// Config:
		CoreConfig.buildSpec();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoreConfig.SPEC);

		// Event Listeners:
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		ObjectManager.setCurrentModInfo(modInfo);
		this.loadConfigs();

		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new EventListener());
		MinecraftForge.EVENT_BUS.register(CreatureManager.getInstance());
		MinecraftForge.EVENT_BUS.register(ProjectileManager.getInstance());

		// Network:
		packetHandler.register();
		//ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::openGui);

		// Change Health Limit:
		LMReflectionHelper.setPrivateFinalValue(RangedAttribute.class, (RangedAttribute) SharedMonsterAttributes.MAX_HEALTH, 100000, "maximumValue");

		// Blocks and Items:
		ItemManager.getInstance().loadItems();
		EquipmentPartManager.getInstance().loadAllFromJSON(modInfo);
		ObjectLists.createCustomItems();
		ObjectLists.createLists();

		// Tile Entities:
		ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class);
		ObjectManager.addTileEntity("equipmentforge", TileEntityEquipmentForge.class);

		// Potion Effects:
		this.effects = new Effects();

		// Elements:
		ElementManager.getInstance().loadAllFromJSON(modInfo);

		// Creatures:
		CapabilityManager.INSTANCE.register(IExtendedPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer::new);
		CapabilityManager.INSTANCE.register(IExtendedEntity.class, new ExtendedEntityStorage(), ExtendedEntity::new);
		CreatureManager.getInstance().startup(modInfo);

		// Projectiles:
		ProjectileManager.getInstance().startup(modInfo);

		// Spawners:
		MinecraftForge.EVENT_BUS.register(SpawnerEventListener.getInstance());
		SpawnerManager.getInstance().loadAllFromJSON();

		// Altars:
		AltarInfo.createAltars();

		// Mob Events:
		MinecraftForge.EVENT_BUS.register(MobEventManager.getInstance());
		MinecraftForge.EVENT_BUS.register(MobEventListener.getInstance());
		MobEventManager.getInstance().loadAllFromJSON(modInfo);

		// Dungeons:
		DungeonManager.getInstance().loadAllFromJSON();

		// Treat Lists:
		ItemHalloweenTreat.createObjectLists();
		ItemWinterGift.createObjectLists();

		// Mod Support:
		DLDungeons.init();
	}

	@SubscribeEvent
	public void clientSetup(final FMLClientSetupEvent event) {
		ClientManager.getInstance().initLanguageManager();
		ClientManager.getInstance().registerEvents();
		ClientManager.getInstance().registerTextures();
		ClientManager.getInstance().initRenderRegister();
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		// TODO New Commands
	}

	public void loadConfigs() {
		ConfigGeneral.INSTANCE.clearOldConfigs("2.1.0.0", version);
		ItemManager.getInstance().loadConfig();
		CreatureManager.getInstance().loadConfig();
		MobEventManager.getInstance().loadConfig();
		AltarInfo.loadGlobalSettings();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// Example code to dispatch IMC to another mod
		//InterModComms.sendTo("modif", "methodname", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
	}

	private void processIMC(final InterModProcessEvent event) {
		// Example code to receive and process InterModComms from other mods
		//LOGGER.info("Got IMC {}", event.getIMCStream().map(m->m.getMessageSupplier().get()).collect(Collectors.toList()));
	}


	/**
	 * Prints an info message into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void printInfo(String key, String message) {
		if("".equals(key) || ConfigDebug.INSTANCE.isEnabled(key.toLowerCase())) {
			LOGGER.info("[LycanitesMobs] [Info] [" + key + "] " + message);
		}
	}


	/**
	 * Prints an info debug into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void printDebug(String key, String message) {
		if("".equals(key) || ConfigDebug.INSTANCE.isEnabled(key.toLowerCase())) {
			LOGGER.debug("[LycanitesMobs] [Debug] [" + key + "] " + message);
		}
	}


	/**
	 * Prints an info warning into the console.
	 * @param key The debug config key to use, if empty, the message is always printed.
	 * @param message The message to print.
	 */
	public static void printWarning(String key, String message) {
		if("".equals(key) || ConfigDebug.INSTANCE.isEnabled(key.toLowerCase())) {
			LOGGER.warn("[LycanitesMobs] [WARNING] [" + key + "] " + message);
		}
	}
}
