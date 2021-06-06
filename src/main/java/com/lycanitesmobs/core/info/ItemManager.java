package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.BlockMaker;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.block.*;
import com.lycanitesmobs.core.block.building.BlockPropolis;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.block.effect.*;
import com.lycanitesmobs.core.block.fluid.*;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.GenericBucketItem;
import com.lycanitesmobs.core.item.ItemMobToken;
import com.lycanitesmobs.core.item.consumable.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.special.*;
import com.lycanitesmobs.core.item.temp.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
		this.loadConfig();
		this.loadAllFromJson(modInfo);

		// Food Cooking Smelting Recipes:
		for(ItemInfo itemInfo : this.items.values()) {
			if(itemInfo.name.contains("raw_")) {
				String cookedName = itemInfo.name.replace("raw_", "cooked_");
				if(this.items.containsKey(cookedName)) {
					GameRegistry.addSmelting(itemInfo.item, new ItemStack(this.items.get(cookedName).item, 1), 0.5f);
				}
			}
		}

		this.loadItems();
	}

	/** Loads all JSON Items. **/
	public void loadAllFromJson(ModInfo modInfo) {
		if(!this.loadedGroups.contains(modInfo)) {
			this.loadedGroups.add(modInfo);
		}
		this.loadAllJson(modInfo, "Items", "items", "name", true);
		LycanitesMobs.logDebug("Items", "Complete! " + this.items.size() + " JSON Items Loaded In Total.");
	}

	@Override
	public void parseJson(ModInfo modInfo, String loadGroup, JsonObject json) {
		ItemInfo itemInfo = new ItemInfo(modInfo);
		itemInfo.loadFromJSON(json);
		this.items.put(itemInfo.name, itemInfo);
		ObjectManager.addItem(itemInfo.name, itemInfo.getItem());
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ItemConfig.loadGlobalSettings();
	}


	/** Called during early start up, loads all items. **/
	public void loadItems() {
		ModInfo group = LycanitesMobs.modInfo;
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("soulstone", new ItemSoulstone(group));
		ObjectManager.addItem("equipment", new ItemEquipment());
		ObjectManager.addItem("mobtoken", new ItemMobToken(group));

		ObjectManager.addItem("soulkey", new ItemSoulkey("soulkey", 0));
		ObjectManager.addItem("soulkeydiamond", new ItemSoulkey("soulkeydiamond", 1));
		ObjectManager.addItem("soulkeyemerald", new ItemSoulkey("soulkeyemerald", 2));


		// Utilities:
		ObjectManager.addBlock("summoningpedestal", new BlockSummoningPedestal(group));
		ObjectManager.addBlock("equipmentforge_lesser", new BlockEquipmentForge(group, 1));
		ObjectManager.addBlock("equipmentforge_greater", new BlockEquipmentForge(group, 2));
		ObjectManager.addBlock("equipmentforge_master", new BlockEquipmentForge(group, 3));
		ObjectManager.addBlock("equipment_infuser", new EquipmentInfuserBlock(group));
		ObjectManager.addBlock("equipment_station", new EquipmentStationBlock(group));


		// Buff Items:
		ObjectManager.addItem("immunizer", new ItemImmunizer());
		ObjectManager.addItem("cleansingcrystal", new ItemCleansingCrystal());


		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat());
		ObjectManager.addItem("wintergift", new ItemWinterGift());
		ObjectManager.addItem("wintergiftlarge", new ItemWinterGiftLarge());


		// Special:
		ObjectManager.addItem("frostyfur", new ItemFrostyFur());
		ObjectManager.addItem("poisongland", new ItemPoisonGland());
		ObjectManager.addItem("geistliver", new ItemGeistLiver());
		ObjectLists.addItem("diet_detritivore", ObjectManager.getItem("geistliver"));
		ObjectManager.addItem("wraithsigil", new ItemWraithSigil());


		// Fluids:
		Fluid fluid = ObjectManager.addFluid("ooze");
		this.addFluid("ooze", fluid, new BlockFluidOoze(fluid, "ooze"), 10, 3000, 5000, 0, true, true);

		fluid = ObjectManager.addFluid("rabbitooze");
		this.addFluid("rabbitooze", fluid, new BlockFluidRabbitooze(fluid, "rabbitooze"), 10, 3000, 5000, 0, true, true);

		fluid = ObjectManager.addFluid("moglava");
		this.addFluid("moglava", fluid, new BlockFluidMoglava(fluid), 15, 3000, 5000, 1100, false, false);

		fluid = ObjectManager.addFluid("acid");
		this.addFluid("acid", fluid, new BlockFluidAcid(fluid, "acid"), 10, 1000, 10, 40, false, true);

		fluid = ObjectManager.addFluid("sharacid");
		this.addFluid("sharacid", fluid, new BlockFluidSharacid(fluid, "sharacid"), 10, 1000, 10, 40, false, true);

		fluid = ObjectManager.addFluid("poison");
		this.addFluid("poison", fluid, new BlockFluidPoison(fluid, "poison"), 0, 1000, 8, 20, false, true);

		fluid = ObjectManager.addFluid("vesspoison");
		this.addFluid("vesspoison", fluid, new BlockFluidVesspoison(fluid, "vesspoison"), 0, 1000, 8, 20, false, true);



		// Summoning Staves:
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning("summoningstaff", "summoningstaff"));
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable("stablesummoningstaff", "staffstable"));
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood("bloodsummoningstaff", "staffblood"));
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy("sturdysummoningstaff", "staffsturdy"));
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage("savagesummoningstaff", "staffsavage"));
		ObjectManager.addBlock("frostweb", new BlockFrostweb());


		// Building Blocks:
		BlockMaker.addStoneBlocks(group, "lush");
		BlockMaker.addStoneBlocks(group, "desert");
		BlockMaker.addStoneBlocks(group, "shadow");
		BlockMaker.addStoneBlocks(group, "demon");
		BlockMaker.addStoneBlocks(group, "aberrant");
		BlockMaker.addStoneBlocks(group, "ashen");
		ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
		ObjectManager.addBlock("soulcubeundead", new BlockSoulcube(group, "soulcubeundead"));
		ObjectManager.addBlock("soulcubeaberrant", new BlockSoulcube(group, "soulcubeaberrant"));
		ObjectManager.addBlock("propolis", new BlockPropolis());
		GameRegistry.addSmelting(ObjectManager.getBlock("propolis"), new ItemStack(Blocks.HARDENED_CLAY, 1), 0.5f);
		ObjectManager.addBlock("veswax", new BlockVeswax());
		GameRegistry.addSmelting(ObjectManager.getBlock("veswax"), new ItemStack(Items.SUGAR, 6), 0.5f);


		// Effect Blocks:
		AssetManager.addSound("frostcloud", group, "block.frostcloud");
		ObjectManager.addBlock("frostcloud", new BlockFrostCloud());
		AssetManager.addSound("frostfire", group, "block.frostfire");
		ObjectManager.addBlock("frostfire", new BlockFrostfire());
		AssetManager.addSound("icefire", group, "block.icefire");
		ObjectManager.addBlock("icefire", new BlockIcefire());
		AssetManager.addSound("hellfire", group, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
		AssetManager.addSound("doomfire", group, "block.doomfire");
		ObjectManager.addBlock("doomfire", new BlockDoomfire());
		AssetManager.addSound("scorchfire", group, "block.scorchfire");
		ObjectManager.addBlock("scorchfire", new BlockScorchfire());
		AssetManager.addSound("primefire", group, "block.primefire");
		ObjectManager.addBlock("primefire", new BlockPrimefire());
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
		AssetManager.addSound("poopcloud", group, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud());
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
	}


	/**
	 * Adds a fluid with associated, blocks, items, sounds, et.
	 */
	public void addFluid(String name, Fluid fluid, BlockFluidBase block, int luminosity, int density, int viscosity, int temperature, boolean addSound, boolean addDamageSource) {
		fluid.setLuminosity(luminosity).setDensity(density).setViscosity(viscosity).setTemperature(temperature);
		ObjectManager.addBlock(name, block);
		ObjectManager.addItem("bucket" + name, new GenericBucketItem(fluid, block, name).setContainerItem(Items.BUCKET));
		if(addSound)
			AssetManager.addSound(name, LycanitesMobs.modInfo, "block." + name);
		if(addDamageSource)
			ObjectManager.addDamageSource(name, new DamageSource(name));
	}


	/**
	 * Adds items to the ore dictionary.
	 */
	public void registerItemOres() {
		OreDictionary.registerOre("listAllporkraw", Items.PORKCHOP);
		OreDictionary.registerOre("listAllporkcooked", Items.COOKED_PORKCHOP);
		OreDictionary.registerOre("listAllbeefraw", Items.BEEF);
		OreDictionary.registerOre("listAllbeefcooked", Items.COOKED_BEEF);
		OreDictionary.registerOre("listAllchickenraw", Items.CHICKEN);
		OreDictionary.registerOre("listAllchickencooked", Items.COOKED_CHICKEN);
		OreDictionary.registerOre("listAllmuttonraw", Items.MUTTON);
		OreDictionary.registerOre("listAllmuttoncooked", Items.COOKED_MUTTON);
		OreDictionary.registerOre("listAllrabbitraw", Items.RABBIT);
		OreDictionary.registerOre("listAllrabbitcooked", Items.COOKED_RABBIT);
		OreDictionary.registerOre("listAllfishraw", Items.FISH);
		OreDictionary.registerOre("listAllfishcooked", Items.COOKED_FISH);
		OreDictionary.registerOre("listAllVegetables", Items.WHEAT);
		OreDictionary.registerOre("listAllVegetables", Items.BREAD);
		OreDictionary.registerOre("listAllVegetables", Items.CARROT);
		OreDictionary.registerOre("listAllVegetables", Items.POTATO);
		OreDictionary.registerOre("listAllVegetables", Items.BAKED_POTATO);
		OreDictionary.registerOre("listAllVegetables", Items.BEETROOT);
		OreDictionary.registerOre("listAllFungi", Items.MUSHROOM_STEW);
		OreDictionary.registerOre("listAllFruit", Items.APPLE);
		OreDictionary.registerOre("listAllFruit", Items.MELON);
		OreDictionary.registerOre("listAllSweet", Items.CAKE);
		OreDictionary.registerOre("listAllSweet", Items.PUMPKIN_PIE);
		OreDictionary.registerOre("listAllSweet", Items.COOKIE);
		OreDictionary.registerOre("listAllSweet", Items.SUGAR);
		OreDictionary.registerOre("listAllEntomo", Items.SPIDER_EYE);
		OreDictionary.registerOre("listAllEntomo", Items.FERMENTED_SPIDER_EYE);
		OreDictionary.registerOre("listAllRotten", Items.ROTTEN_FLESH);

		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("raw_yeti_meat"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("cooked_yeti_meat"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("raw_pinky_meat"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("cooked_pinky_meat"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("raw_joust_meat"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("cooked_joust_meat"));
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("raw_silex_meat"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cooked_silex_meat"));
		OreDictionary.registerOre("listAllrabbitraw", ObjectManager.getItem("raw_krake_meat"));
		OreDictionary.registerOre("listAllrabbitcooked", ObjectManager.getItem("cooked_krake_meat"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cooked_cephignis_meat"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("raw_concapede_meat"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("cooked_concapede_meat"));
		OreDictionary.registerOre("listAllEntomoraw", ObjectManager.getItem("raw_concapede_meat"));
		OreDictionary.registerOre("listAllEntomocooked", ObjectManager.getItem("cooked_concapede_meat"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("raw_yale_meat"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("cooked_yale_meat"));
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("raw_maka_meat"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("cooked_maka_meat"));
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("raw_ika_meat"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cooked_ika_meat"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("raw_chupacabra_meat"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("cooked_chupacabra_meat"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("raw_aspid_meat"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("cooked_aspid_meat"));
		OreDictionary.registerOre("listAllRotten", ObjectManager.getItem("geistliver"));
	}


	/**
	 * Adds items to the ore dictionary.
	 */
	public void registerBlockOres() {
		OreDictionary.registerOre("listAllFungi", Blocks.RED_MUSHROOM);
		OreDictionary.registerOre("listAllFungi", Blocks.RED_MUSHROOM_BLOCK);
		OreDictionary.registerOre("listAllFungi", Blocks.BROWN_MUSHROOM);
		OreDictionary.registerOre("listAllFungi", Blocks.BROWN_MUSHROOM_BLOCK);
		OreDictionary.registerOre("listAllVegetables", Blocks.PUMPKIN);
	}

	/**
	 * Determines the Equipment Sharpness repair amount of the provided itemstack. Stack size is not taken into account.
	 * @param itemStack The itemstack to check the item and nbt data of.
	 * @return The amount of Sharpness the provided itemstack restores.
	 */
	public int getEquipmentSharpnessRepair(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		}

		Item item = itemStack.getItem();
		ResourceLocation checkId = item.getRegistryName();
		if (item == Items.DYE) {
			if (item.getMetadata(itemStack) == 4) {
				checkId = new ResourceLocation("minecraft:lapis_lazuli");
			}
		}

		for (String itemId : ItemConfig.lowEquipmentSharpnessItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.lowEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.mediumEquipmentSharpnessItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.mediumEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.highEquipmentSharpnessItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.highEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.maxEquipmentSharpnessItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemEquipment.SHARPNESS_MAX;
			}
		}
		return 0;
	}

	/**
	 * Determines the Equipment Mana repair amount of the provided itemstack. Stack size is not taken into account.
	 * @param itemStack The itemstack to check the item and nbt data of.
	 * @return The amount of Mana the provided itemstack restores.
	 */
	public int getEquipmentManaRepair(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		}

		Item item = itemStack.getItem();
		ResourceLocation checkId = item.getRegistryName();
		if (item == Items.DYE) {
			if (item.getMetadata(itemStack) == 4) {
				checkId = new ResourceLocation("minecraft:lapis_lazuli");
			}
		}

		if (item instanceof ChargeItem) {
			return ItemConfig.highEquipmentRepairAmount;
		}
		for (String itemId : ItemConfig.lowEquipmentManaItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.lowEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.mediumEquipmentManaItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.mediumEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.highEquipmentManaItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemConfig.highEquipmentRepairAmount;
			}
		}
		for (String itemId : ItemConfig.maxEquipmentManaItems) {
			if (new ResourceLocation(itemId).equals(checkId)) {
				return ItemEquipment.MANA_MAX;
			}
		}
		return 0;
	}
}
