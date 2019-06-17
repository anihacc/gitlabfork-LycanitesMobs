package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.block.BlockEquipmentForge;
import com.lycanitesmobs.core.block.BlockMaker;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.block.effect.*;
import com.lycanitesmobs.core.entity.projectile.EntityFrostweb;
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

public class ItemManager {
	public static ItemManager INSTANCE;

	/** Handles all global item general config settings. **/
	public ItemConfig config;

	// Creative Tabs:
	public final ItemGroup items = new LMItemsGroup(0, LycanitesMobs.modid + ".items");
	public final ItemGroup blocks = new LMBlocksGroup(1, LycanitesMobs.modid + ".blocks");
	public final ItemGroup creatures = new LMCreaturesGroup(2, LycanitesMobs.modid + ".creatures");
	public final ItemGroup equipmentParts = new LMEquipmentPartsGroup(3, LycanitesMobs.modid + ".equipmentparts");


	/** Returns the main Item Manager instance or creates it and returns it. **/
	public static ItemManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ItemManager();
		}
		return INSTANCE;
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ItemConfig.loadGlobalSettings();
	}


	/** Called during early start up, loads all items. **/
	public void loadItems() {
		ModInfo modInfo = LycanitesMobs.modInfo;
		Item.Properties itemProperties = new Item.Properties().group(this.items);

		ObjectManager.addItem("soulgazer", new ItemSoulgazer(new Item.Properties().maxStackSize(1).group(this.items)));
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


		// Old Projectile Charges and Scepters:
		ObjectManager.addItem("frostwebcharge", new ItemCharge(itemProperties, "frostwebcharge", EntityFrostweb.class));
		ObjectManager.addItem("tundracharge", new ItemCharge(itemProperties, "tundracharge", EntityFrostweb.class));
		ObjectManager.addItem("icefirecharge", new ItemCharge(itemProperties, "icefirecharge", EntityFrostweb.class));
		ObjectManager.addItem("blizzardcharge", new ItemCharge(itemProperties, "blizzardcharge", EntityFrostweb.class));
		ObjectManager.addItem("doomfirecharge", new ItemCharge(itemProperties, "doomfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("hellfirecharge", new ItemCharge(itemProperties, "hellfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("devilstarcharge", new ItemCharge(itemProperties, "devilstarcharge", EntityFrostweb.class));
		ObjectManager.addItem("demoniclightningcharge", new ItemCharge(itemProperties, "demoniclightningcharge", EntityFrostweb.class));
		ObjectManager.addItem("throwingscythe", new ItemCharge(itemProperties, "throwingscythe", EntityFrostweb.class));
		ObjectManager.addItem("mudshotcharge", new ItemCharge(itemProperties, "mudshotcharge", EntityFrostweb.class));
		ObjectManager.addItem("aquapulsecharge", new ItemCharge(itemProperties, "aquapulsecharge", EntityFrostweb.class));
		ObjectManager.addItem("whirlwindcharge", new ItemCharge(itemProperties, "whirlwindcharge", EntityFrostweb.class));
		ObjectManager.addItem("chaosorbcharge", new ItemCharge(itemProperties, "chaosorbcharge", EntityFrostweb.class));
		ObjectManager.addItem("acidsplashcharge", new ItemCharge(itemProperties, "acidsplashcharge", EntityFrostweb.class));
		ObjectManager.addItem("lightball", new ItemCharge(itemProperties, "lightball", EntityFrostweb.class));
		ObjectManager.addItem("lifedraincharge", new ItemCharge(itemProperties, "lifedraincharge", EntityFrostweb.class));
		ObjectManager.addItem("crystalshard", new ItemCharge(itemProperties, "crystalshard", EntityFrostweb.class));
		ObjectManager.addItem("frostboltcharge", new ItemCharge(itemProperties, "frostboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("faeboltcharge", new ItemCharge(itemProperties, "faeboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("aetherwavecharge", new ItemCharge(itemProperties, "aetherwavecharge", EntityFrostweb.class));
		ObjectManager.addItem("waterjetcharge", new ItemCharge(itemProperties, "waterjetcharge", EntityFrostweb.class));
		ObjectManager.addItem("magmacharge", new ItemCharge(itemProperties, "magmacharge", EntityFrostweb.class));
		ObjectManager.addItem("scorchfirecharge", new ItemCharge(itemProperties, "scorchfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("poopcharge", new ItemCharge(itemProperties, "poopcharge", EntityFrostweb.class));
		ObjectManager.addItem("boulderblastcharge", new ItemCharge(itemProperties, "boulderblastcharge", EntityFrostweb.class));
		ObjectManager.addItem("arcanelaserstormcharge", new ItemCharge(itemProperties, "arcanelaserstormcharge", EntityFrostweb.class));
		ObjectManager.addItem("quill", new ItemCharge(itemProperties, "quill", EntityFrostweb.class));
		ObjectManager.addItem("spectralboltcharge", new ItemCharge(itemProperties, "spectralboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("bloodleechcharge", new ItemCharge(itemProperties, "bloodleechcharge", EntityFrostweb.class));


		// Summoning Staves:
		Item.Properties summoningStaffProperties = new Item.Properties().group(this.items).maxStackSize(1).maxDamage(500);
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


		/*/ Foods: TODO Json Foods
		ObjectManager.addItem("battleburrito", new ItemFoodBattleBurrito("battleburrito", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));
		ObjectManager.addItem("explorersrisotto", new ItemFoodExplorersRisotto("explorersrisotto", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));

		ObjectManager.addItem("yetimeatraw", new ItemCustomFood("yetimeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potions.SLOWNESS, 45, 1, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yetimeatraw"));

		ObjectManager.addItem("yetimeatcooked", new ItemCustomFood("yetimeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(new Potion(new EffectInstance(Effects.RESISTANCE)), 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yetimeatcooked"));

		ObjectManager.addItem("palesoup", new ItemCustomFood("palesoup", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(new Potion(new EffectInstance(Effects.RESISTANCE)), 600, 1, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("palesoup"));

		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(new Potion(new EffectInstance(Effects.WITHER)), 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));

		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.STRENGTH, 60, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));

		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.STRENGTH, 600, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("joustmeatraw", new ItemCustomFood("joustmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potions.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("joustmeatraw"));

		ObjectManager.addItem("joustmeatcooked", new ItemCustomFood("joustmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.SWIFTNESS, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("joustmeatcooked"));

		ObjectManager.addItem("ambercake", new ItemCustomFood("ambercake", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.SWIFTNESS, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("ambercake"));

		ItemCustomFood rawMeat =  new ItemCustomFood("arisaurmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(new Potion(new EffectInstance(Effects.SATURATION)), 45, 2, 0.8F);
		if(ObjectManager.getEffect("paralysis") != null) {
			rawMeat.setPotionEffect(ObjectManager.getEffect("paralysis"), 10, 2, 0.8F);
		}
		ObjectManager.addItem("arisaurmeatraw", rawMeat);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatraw"));

		ItemCustomFood arisaurCooked = new ItemCustomFood("arisaurmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
		if(ObjectManager.getEffect("rejuvenation") != null) {
			arisaurCooked.setPotionEffect(ObjectManager.getEffect("rejuvenation"), 30, 1, 1.0F);
		}
		ObjectManager.addItem("arisaurmeatcooked", arisaurCooked);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatcooked"));

		ItemCustomFood paleosalad = new ItemCustomFood("paleosalad", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
		if(ObjectManager.getEffect("rejuvenation") != null) {
			paleosalad.setPotionEffect(ObjectManager.getEffect("rejuvenation"), 600, 1, 1.0F);
		}
		ObjectManager.addItem("paleosalad", paleosalad.setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("paleosalad"));

		Potion rawFoodEffectID = Potions.WEAKNESS;
		if(ObjectManager.getEffect("penetration") != null) {
			rawFoodEffectID = ObjectManager.getEffect("penetration");
		}
		ObjectManager.addItem("silexmeatraw", new ItemCustomFood("silexmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
		ObjectLists.addItem("rawfish", ObjectManager.getItem("silexmeatraw"));

		Potion cookedFoodEffectID = Potions.SPEED;
		if(ObjectManager.getEffect("swiftswimming") != null) {
			cookedFoodEffectID = ObjectManager.getEffect("swiftswimming");
		}
		ObjectManager.addItem("silexmeatcooked", new ItemCustomFood("silexmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(cookedFoodEffectID, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("silexmeatcooked"));

		ObjectManager.addItem("lapisfishandchips", new ItemCustomFood("lapisfishandchips", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(cookedFoodEffectID, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("lapisfishandchips"));

		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.FIRE_RESISTANCE, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));

		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.FIRE_RESISTANCE, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));

		ObjectManager.addItem("concapedemeatraw", new ItemCustomFood("concapedemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potions.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("concapedemeatraw"));

		ObjectManager.addItem("concapedemeatcooked", new ItemCustomFood("concapedemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.LEAPING, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("concapedemeatcooked"));

		ObjectManager.addItem("tropicalcurry", new ItemCustomFood("tropicalcurry", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.LEAPING, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("tropicalcurry"));

		ObjectManager.addItem("yalemeatraw", new ItemCustomFood("yalemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(new Potion(new EffectInstance(Effects.MINING_FATIGUE)), 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yalemeatraw"));

		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(new Potion(new EffectInstance(Effects.HASTE)), 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));

		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(new Potion(new EffectInstance(Effects.HASTE)), 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("peakskebab"));

		ObjectManager.addItem("makameatraw", new ItemCustomFood("makameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potions.WEAKNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("makameatraw"));

		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(new Potion(new EffectInstance(Effects.ABSORPTION)), 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));

		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(new Potion(new EffectInstance(Effects.ABSORPTION)), 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bulwarkburger"));

		Potion ikaRawFoodEffect = Potions.BLINDNESS;
		if(ObjectManager.getEffect("weight") != null) {
			ikaRawFoodEffect = ObjectManager.getEffect("weight");
		}
		ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(ikaRawFoodEffect, 45, 2, 0.8F));
		ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));

		ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.WATER_BREATHING, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));

		ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.WATER_BREATHING, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));

		ItemCustomFood rawChupacabraMeat = new ItemCustomFood("chupacabrameatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(new Potion(new EffectInstance(Effects.HUNGER)), 45, 2, 0.8F);
		if(ObjectManager.getEffect("fear") != null) {
			rawChupacabraMeat.setPotionEffect(ObjectManager.getEffect("fear"), 10, 2, 0.8F);
		}
		ObjectManager.addItem("chupacabrameatraw", rawChupacabraMeat);
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("chupacabrameatraw"));

		ItemCustomFood cookedMeat = new ItemCustomFood("chupacabrameatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
		if(ObjectManager.getEffect("leech") != null) {
			cookedMeat.setPotionEffect(ObjectManager.getEffect("leech"), 30, 1, 1.0F);
		}
		ObjectManager.addItem("chupacabrameatcooked", cookedMeat);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("chupacabrameatcooked"));

		ItemCustomFood meal = new ItemCustomFood("bloodchili", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
		meal.setMaxStackSize(16);
		if(ObjectManager.getEffect("leech") != null) {
			meal.setPotionEffect(ObjectManager.getEffect("leech"), 600, 1, 1.0F);
		}
		ObjectManager.addItem("bloodchili", meal, 3, 1, 6);

		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potions.POISON, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));

		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potions.REGENERATION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));

		ObjectManager.addItem("mosspie", new ItemCustomFood("mosspie", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potions.REGENERATION, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));*/
	}
}
