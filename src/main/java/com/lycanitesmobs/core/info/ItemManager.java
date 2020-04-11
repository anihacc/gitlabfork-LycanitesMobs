package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.FileLoader;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.StreamLoader;
import com.lycanitesmobs.core.block.*;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.block.effect.*;
import com.lycanitesmobs.core.block.fluid.BlockFluidAcid;
import com.lycanitesmobs.core.block.fluid.BlockFluidMoglava;
import com.lycanitesmobs.core.block.fluid.BlockFluidOoze;
import com.lycanitesmobs.core.block.fluid.BlockFluidPoison;
import com.lycanitesmobs.core.item.*;
import com.lycanitesmobs.core.item.consumable.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import com.lycanitesmobs.core.item.special.ItemSoulkey;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.item.summoningstaff.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ItemManager extends JSONLoader {
	public static ItemManager INSTANCE;
	public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, LycanitesMobs.MODID);

	public Map<String, ItemInfo> items = new HashMap<>();

	/** A list of blocks that need to use the cutout renderer. **/
	public List<Block> cutoutBlocks = new ArrayList<>();

	/** A list of mod groups that have loaded with this manager. **/
	public List<ModInfo> loadedGroups = new ArrayList<>();

	/** Handles all global item general config settings. **/
	public ItemConfig config;

	// Creative Tabs:
	public final ItemGroup itemsGroup = new LMItemsGroup(LycanitesMobs.MODID + ".items");
	public final ItemGroup blocksGroup = new LMBlocksGroup(LycanitesMobs.MODID + ".blocks");
	public final ItemGroup creaturesGroups = new LMCreaturesGroup(LycanitesMobs.MODID + ".creatures");
	public final ItemGroup chargesGroup = new LMChargesGroup(LycanitesMobs.MODID + ".charges");
	public final ItemGroup equipmentPartsGroup = new LMEquipmentPartsGroup(LycanitesMobs.MODID + ".equipmentparts");


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
		LycanitesMobs.logDebug("Items", "Complete! " + this.items.size() + " JSON Items Loaded In Total.");
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
		Item.Properties equipmentProperties = new Item.Properties().maxStackSize(1).setNoRepair().setISTER(() -> com.lycanitesmobs.client.renderer.EquipmentRenderer::new);
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
		ObjectManager.addBlock("equipment_infuser", new EquipmentInfuserBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 1000).sound(SoundType.METAL), modInfo));


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
		BlockMaker.addStoneBlocks(modInfo, "lush");
		BlockMaker.addStoneBlocks(modInfo, "desert");
		BlockMaker.addStoneBlocks(modInfo, "shadow");
		BlockMaker.addStoneBlocks(modInfo, "demon");
		BlockMaker.addStoneBlocks(modInfo, "aberrant");
		ObjectManager.addBlock("soulcubedemonic", new BlockBase(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2F, 1200.0F), modInfo, "soulcubedemonic"));
		ObjectManager.addBlock("soulcubeundead", new BlockBase(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2F, 1200.0F), modInfo, "soulcubeundead"));
		ObjectManager.addBlock("soulcubeaberrant", new BlockBase(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2F, 1200.0F), modInfo, "soulcubeaberrant"));
		ObjectManager.addBlock("propolis", new BlockVeswax(Block.Properties.create(Material.CLAY).sound(SoundType.WET_GRASS).hardnessAndResistance(0.6F).tickRandomly(), "propolis"));
		ObjectManager.addBlock("veswax", new BlockVeswax(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(0.6F).tickRandomly(), "veswax"));


		// Effect Blocks:
		Block.Properties fireProperties = Block.Properties.create(Material.FIRE).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH).notSolid();
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

		Block.Properties cloudProperties = Block.Properties.create(Material.MISCELLANEOUS).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH).notSolid();
		ObjectManager.addSound("frostcloud", modInfo, "block.frostcloud");
		ObjectManager.addBlock("frostcloud", new BlockFrostCloud(cloudProperties));
		ObjectManager.addSound("poisoncloud", modInfo, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud(cloudProperties));
		ObjectManager.addSound("poopcloud", modInfo, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud(cloudProperties));

		Block.Properties webProperties = Block.Properties.create(Material.WEB).tickRandomly().doesNotBlockMovement().variableOpacity().sound(SoundType.CLOTH).notSolid();
		ObjectManager.addBlock("quickweb", new BlockQuickWeb(webProperties));
		ObjectManager.addBlock("frostweb", new BlockFrostweb(webProperties));


		// Fluids:
		Block.Properties waterBlockProperties = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100).noDrops();
		Block.Properties lavaBlockProperties = Block.Properties.create(Material.LAVA).doesNotBlockMovement().hardnessAndResistance(100).noDrops();

		this.addFluid("ooze", 0x003A9F, 3000, 3000, 0, 5, false);
		ObjectManager.addBlock("ooze", new BlockFluidOoze(() -> ObjectManager.getFluid("ooze").get(), waterBlockProperties, "ooze"));
		ObjectManager.addDamageSource("ooze", new DamageSource("ooze"));

		this.addFluid("rabbitooze", 0x002AAF, 3000, 3000, 0, 5, true);
		ObjectManager.addBlock("rabbitooze", new BlockFluidOoze(() -> ObjectManager.getFluid("rabbitooze").get(), waterBlockProperties, "rabbitooze"));

		this.addFluid("moglava", 0xFF5722, 3000, 5000, 1100, 15, true);
		ObjectManager.addBlock("moglava", new BlockFluidMoglava(() -> ObjectManager.getFluid("moglava").get(), lavaBlockProperties, "moglava"));

		this.addFluid("acid", 0x8BC34A, 1000, 10, 40, 10, false);
		ObjectManager.addBlock("acid", new BlockFluidAcid(() -> ObjectManager.getFluid("acid").get(), waterBlockProperties, "acid"));
		ObjectManager.addDamageSource("acid", new DamageSource("acid"));

		this.addFluid("sharacid", 0x8BB35A, 1000, 10, 40, 10, true);
		ObjectManager.addBlock("sharacid", new BlockFluidAcid(() -> ObjectManager.getFluid("sharacid").get(), waterBlockProperties, "sharacid"));

		this.addFluid("poison", 0x9C27B0, 1000, 8, 20, 0, false);
		ObjectManager.addBlock("poison", new BlockFluidPoison(() -> ObjectManager.getFluid("poison").get(), waterBlockProperties, "poison"));

		this.addFluid("vesspoison", 0xAC27A0, 1000, 8, 20, 0, true);
		ObjectManager.addBlock("vesspoison", new BlockFluidPoison(() -> ObjectManager.getFluid("vesspoison").get(), waterBlockProperties, "vesspoison"));
	}

	public void addFluid(String fluidName, int fluidColor, int density, int viscosity, int temperature, int luminosity, boolean multiply) {
		FluidAttributes.Builder fluidBuilder = FluidAttributes.builder(new ResourceLocation(LycanitesMobs.MODID, "block/" + fluidName + "_still"), new ResourceLocation(LycanitesMobs.MODID, "block/" + fluidName + "_flowing"));
		fluidBuilder.color(fluidColor);
		fluidBuilder.density(density);
		fluidBuilder.viscosity(viscosity);
		fluidBuilder.temperature(temperature);
		fluidBuilder.luminosity(luminosity);

		Supplier<FlowingFluid> flowingFluidSupplier = () -> ObjectManager.getFluid(fluidName).get();
		ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(flowingFluidSupplier, () -> ObjectManager.getFluid(fluidName + "_flowing").get(), fluidBuilder);
		if(multiply)
			fluidProperties.canMultiply();
		fluidProperties.bucket(() -> ObjectManager.getItem("bucket" + fluidName));
		fluidProperties.block(() -> (FlowingFluidBlock)ObjectManager.getBlock(fluidName));

		ObjectManager.addFluid(fluidName, FLUIDS.register(fluidName, () -> new ForgeFlowingFluid.Source(fluidProperties)));
		ObjectManager.addFluid(fluidName + "_flowing", FLUIDS.register(fluidName + "_flowing", () -> new ForgeFlowingFluid.Flowing(fluidProperties)));

		ObjectManager.addSound(fluidName, LycanitesMobs.modInfo, "block." + fluidName);

		Item.Properties bucketProperties = new Item.Properties().group(this.itemsGroup).containerItem(Items.BUCKET).maxStackSize(1);
		ObjectManager.addItem("bucket" + fluidName, new BucketItem(flowingFluidSupplier, bucketProperties).setRegistryName(LycanitesMobs.MODID, "bucket" + fluidName));
	}
}
