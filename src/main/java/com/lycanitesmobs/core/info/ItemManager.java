package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.FileLoader;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.StreamLoader;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.block.BlockEquipmentForge;
import com.lycanitesmobs.core.block.BlockMaker;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.block.effect.*;
import com.lycanitesmobs.core.item.*;
import com.lycanitesmobs.core.item.consumable.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import com.lycanitesmobs.core.item.special.ItemSoulkey;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.item.summoningstaff.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager extends JSONLoader {
	public static ItemManager INSTANCE;

	public Map<String, ItemInfo> items = new HashMap<>();

	/** A list of mod groups that have loaded with this manager. **/
	public List<ModInfo> loadedGroups = new ArrayList<>();

	/** Handles all global item general config settings. **/
	public ItemConfig config;

	// Creative Tabs:
	public final ItemGroup itemsGroup = new LMItemsGroup(ItemGroup.GROUPS.length, LycanitesMobs.MODID + ".items");
	public final ItemGroup blocksGroup = new LMBlocksGroup(ItemGroup.GROUPS.length, LycanitesMobs.MODID + ".blocks");
	public final ItemGroup creaturesGroups = new LMCreaturesGroup(ItemGroup.GROUPS.length, LycanitesMobs.MODID + ".creatures");
	public final ItemGroup equipmentPartsGroup = new LMEquipmentPartsGroup(ItemGroup.GROUPS.length, LycanitesMobs.MODID + ".equipmentparts");


	/** Returns the main Item Manager instance or creates it and returns it. **/
	public static ItemManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ItemManager();
		}
		return INSTANCE;
	}

	/**
	 * Called during startup and initially loads everything in this manager.
	 * @param modInfo The mod loading this manager.
	 */
	public void startup(ModInfo modInfo) {
		this.loadItems();
		this.loadAllFromJson(modInfo);
	}


	/** Loads all JSON Items. **/
	public void loadAllFromJson(ModInfo modInfo) {
		if(!this.loadedGroups.contains(modInfo)) {
			this.loadedGroups.add(modInfo);
		}
		this.loadAllJson(modInfo, "Items", "items", "name", true, null, FileLoader.COMMON, StreamLoader.COMMON);
		LycanitesMobs.logDebug("Items", "Complete! " + this.items.size() + " JSON Equipment Parts Loaded In Total.");
	}

	@Override
	public void parseJson(ModInfo modInfo, String loadGroup, JsonObject json) {
		ItemInfo itemInfo = new ItemInfo(modInfo);
		itemInfo.loadFromJSON(json);
		this.items.put(itemInfo.name, itemInfo);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for(ItemInfo itemInfo : items.values()) {
			LycanitesMobs.logDebug("Item", "Registering item: " + itemInfo.getName());
			event.getRegistry().register(itemInfo.getItem());
		}
	}

	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ItemConfig.loadGlobalSettings();
	}

	/** Called during early start up, loads all non-json items. **/
	public void loadItems() {
		ModInfo modInfo = LycanitesMobs.modInfo;
		Item.Properties itemProperties = new Item.Properties().group(this.itemsGroup);

		ObjectManager.addItem("soulgazer", new ItemSoulgazer(new Item.Properties().maxStackSize(1).group(this.itemsGroup)));
		ObjectManager.addItem("mobtoken", new ItemMobToken(new Item.Properties(), modInfo));
		ObjectManager.addItem("soulstone", new ItemSoulstone(itemProperties, null));

		// Equipment Pieces:
		Item.Properties equipmentProperties = new Item.Properties().maxStackSize(1).setNoRepair().setTEISR(() -> com.lycanitesmobs.core.renderer.EquipmentRenderer::new);
		ObjectManager.addItem("equipment", new ItemEquipment(equipmentProperties));

		// Keys:
		ObjectManager.addItem("soulkey", new ItemSoulkey(itemProperties, "soulkey", 0));
		ObjectManager.addItem("soulkeydiamond", new ItemSoulkey(itemProperties, "soulkeydiamond", 1));
		ObjectManager.addItem("soulkeyemerald", new ItemSoulkey(itemProperties, "soulkeyemerald", 2));


		// Utilities:
		ObjectManager.addBlock("summoningpedestal", new BlockSummoningPedestal(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL), modInfo));
		ObjectManager.addBlock("equipmentforge_lesser", new BlockEquipmentForge(Block.Properties.create(Material.WOOD).hardnessAndResistance(5, 10).sound(SoundType.WOOD), modInfo, 1));
		ObjectManager.addBlock("equipmentforge_greater", new BlockEquipmentForge(Block.Properties.create(Material.ROCK).hardnessAndResistance(5, 20).sound(SoundType.STONE), modInfo, 2));
		ObjectManager.addBlock("equipmentforge_master", new BlockEquipmentForge(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 1000).sound(SoundType.METAL), modInfo, 3));


		// Buff Items:
		ObjectManager.addItem("immunizer", new ItemImmunizer(itemProperties));
		ObjectManager.addItem("cleansingcrystal", new ItemCleansingCrystal(itemProperties));


		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat(itemProperties));
		ObjectManager.addItem("wintergift", new ItemWinterGift(itemProperties));
		ObjectManager.addItem("wintergiftlarge", new ItemWinterGiftLarge(itemProperties));


		// Special:
		ObjectManager.addItem("frostyfur", new ItemBlockPlacer(itemProperties, "frostyfur", "frostcloud"));
		ObjectManager.addItem("poisongland", new ItemBlockPlacer(itemProperties, "poisongland", "poisoncloud"));
		ObjectManager.addItem("geistliver", new ItemBlockPlacer(itemProperties, "geistliver", "shadowfire"));


		// Summoning Staves:
		Item.Properties summoningStaffProperties = new Item.Properties().group(this.itemsGroup).maxStackSize(1).maxDamage(500);
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning(summoningStaffProperties, "summoningstaff", "summoningstaff"));
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable(summoningStaffProperties, "stablesummoningstaff", "staffstable"));
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood(summoningStaffProperties, "bloodsummoningstaff", "staffblood"));
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy(summoningStaffProperties, "sturdysummoningstaff", "staffsturdy"));
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage(summoningStaffProperties, "savagesummoningstaff", "staffsavage"));


		// Building Blocks:
		BlockMaker.addStoneBlocks(modInfo, "lush", Blocks.TALL_GRASS);
		BlockMaker.addStoneBlocks(modInfo, "desert", Blocks.SANDSTONE);
		BlockMaker.addStoneBlocks(modInfo, "shadow", Blocks.OBSIDIAN);
		BlockMaker.addStoneBlocks(modInfo, "demon", Items.NETHER_WART);
		ObjectManager.addBlock("soulcubedemonic", new BlockBase(Block.Properties.create(Material.ROCK).sound(SoundType.STONE), modInfo, "soulcubedemonic"));
		ObjectManager.addBlock("propolis", new BlockVeswax(Block.Properties.create(Material.CLAY).sound(SoundType.WET_GRASS).hardnessAndResistance(0.6F).tickRandomly(), "propolis"));
		ObjectManager.addBlock("veswax", new BlockVeswax(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(0.6F).tickRandomly(), "veswax"));


		// Effect Blocks:
		Block.Properties fireProperties = Block.Properties.create(Material.FIRE).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH);
		ObjectManager.addSound("frostfire", modInfo, "block.frostfire");
		ObjectManager.addBlock("frostfire", new BlockFrostfire(fireProperties));
		ObjectManager.addSound("icefire", modInfo, "block.icefire");
		ObjectManager.addBlock("icefire", new BlockIcefire(fireProperties));
		ObjectManager.addSound("hellfire", modInfo, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire(fireProperties));
		ObjectManager.addSound("doomfire", modInfo, "block.doomfire");
		ObjectManager.addBlock("doomfire", new BlockDoomfire(fireProperties));
		ObjectManager.addSound("scorchfire", modInfo, "block.scorchfire");
		ObjectManager.addBlock("scorchfire", new BlockScorchfire(fireProperties));
		ObjectManager.addSound("shadowfire", modInfo, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire(fireProperties));

		Block.Properties cloudProperties = Block.Properties.create(Material.MISCELLANEOUS).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH);
		ObjectManager.addSound("frostcloud", modInfo, "block.frostcloud");
		ObjectManager.addBlock("frostcloud", new BlockFrostCloud(cloudProperties));
		ObjectManager.addSound("poisoncloud", modInfo, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud(cloudProperties));
		ObjectManager.addSound("poopcloud", modInfo, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud(cloudProperties));

		Block.Properties webProperties = Block.Properties.create(Material.WEB).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH);
		ObjectManager.addBlock("quickweb", new BlockQuickWeb(webProperties));
		ObjectManager.addBlock("frostweb", new BlockFrostweb(webProperties));


		/*/ Fluids: TODO New fluids
		Fluid fluidOoze = ObjectManager.addFluid("ooze");
		fluidOoze.setLuminosity(10).setDensity(3000).setViscosity(5000).setTemperature(0);
		ObjectManager.addBlock("ooze", new BlockFluidOoze(fluidOoze));
		ObjectManager.addItem("bucketooze", new ItemBucketOoze(fluidOoze).setContainerItem(Items.BUCKET));
		AssetManager.addSound("ooze", group, "block.ooze");
		ObjectManager.addDamageSource("ooze", new DamageSource("ooze"));

		Fluid fluidPureLava = ObjectManager.addFluid("purelava");
		fluidPureLava.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluidPureLava));
		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava(fluidPureLava).setContainerItem(Items.BUCKET));*/
	}
}
