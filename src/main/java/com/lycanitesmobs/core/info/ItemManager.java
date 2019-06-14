package com.lycanitesmobs.core.info;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.BlockMaker;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockEquipmentForge;
import com.lycanitesmobs.core.block.BlockSoulcube;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.block.building.BlockPropolis;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.block.effect.*;
import com.lycanitesmobs.core.entity.projectile.EntityFrostweb;
import com.lycanitesmobs.core.item.ItemBlockPlacer;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.item.ItemMobToken;
import com.lycanitesmobs.core.item.consumable.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.soulstone.*;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import com.lycanitesmobs.core.item.special.ItemSoulkey;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.item.summoningstaff.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

public class ItemManager {
	public static ItemManager INSTANCE;

	/** Handles all global item general config settings. **/
	public ItemConfig config;


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
		ModInfo group = LycanitesMobs.modInfo;
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
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


		// Soulstones:
		ObjectManager.addItem("soulstone", new ItemSoulstone(group, ""));
		ObjectManager.addItem("soulstonedemonic", new ItemSoulstoneDemonic(group));
		ObjectManager.addItem("soulstonefreshwater", new ItemSoulstoneFreshwater(group));
		ObjectManager.addItem("soulstoneinferno", new ItemSoulstoneInferno(group));
		ObjectManager.addItem("soulstonemountain", new ItemSoulstoneMountain(group));
		ObjectManager.addItem("soulstoneshadow", new ItemSoulstoneShadow(group));


		// Buff Items:
		ObjectManager.addItem("immunizer", new ItemImmunizer());
		ObjectManager.addItem("cleansingcrystal", new ItemCleansingCrystal());


		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat());
		ObjectManager.addItem("wintergift", new ItemWinterGift());
		ObjectManager.addItem("wintergiftlarge", new ItemWinterGiftLarge());


		// Foods:
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
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));


		// Special:
		ObjectManager.addItem("frostyfur", new ItemBlockPlacer("frostyfur", "frostcloud"));
		ObjectManager.addItem("poisongland", new ItemBlockPlacer("poisongland", "poisoncloud"));
		ObjectManager.addItem("geistliver", new ItemBlockPlacer("geistliver", "shadowfire"));


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


		// Old Projectile Charges and Scepters:
		ObjectManager.addItem("frostwebcharge", new ItemCharge("frostwebcharge", EntityFrostweb.class));
		ObjectManager.addItem("tundracharge", new ItemCharge("tundracharge", EntityFrostweb.class));
		ObjectManager.addItem("icefirecharge", new ItemCharge("icefirecharge", EntityFrostweb.class));
		ObjectManager.addItem("blizzardcharge", new ItemCharge("blizzardcharge", EntityFrostweb.class));
		ObjectManager.addItem("doomfirecharge", new ItemCharge("doomfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("hellfirecharge", new ItemCharge("hellfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("devilstarcharge", new ItemCharge("devilstarcharge", EntityFrostweb.class));
		ObjectManager.addItem("demoniclightningcharge", new ItemCharge("demoniclightningcharge", EntityFrostweb.class));
		ObjectManager.addItem("throwingscythe", new ItemCharge("throwingscythe", EntityFrostweb.class));
		ObjectManager.addItem("mudshotcharge", new ItemCharge("mudshotcharge", EntityFrostweb.class));
		ObjectManager.addItem("aquapulsecharge", new ItemCharge("aquapulsecharge", EntityFrostweb.class));
		ObjectManager.addItem("whirlwindcharge", new ItemCharge("whirlwindcharge", EntityFrostweb.class));
		ObjectManager.addItem("chaosorbcharge", new ItemCharge("chaosorbcharge", EntityFrostweb.class));
		ObjectManager.addItem("acidsplashcharge", new ItemCharge("acidsplashcharge", EntityFrostweb.class));
		ObjectManager.addItem("lightball", new ItemCharge("lightball", EntityFrostweb.class));
		ObjectManager.addItem("lifedraincharge", new ItemCharge("lifedraincharge", EntityFrostweb.class));
		ObjectManager.addItem("crystalshard", new ItemCharge("crystalshard", EntityFrostweb.class));
		ObjectManager.addItem("frostboltcharge", new ItemCharge("frostboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("faeboltcharge", new ItemCharge("faeboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("aetherwavecharge", new ItemCharge("aetherwavecharge", EntityFrostweb.class));
		ObjectManager.addItem("waterjetcharge", new ItemCharge("waterjetcharge", EntityFrostweb.class));
		ObjectManager.addItem("magmacharge", new ItemCharge("magmacharge", EntityFrostweb.class));
		ObjectManager.addItem("scorchfirecharge", new ItemCharge("scorchfirecharge", EntityFrostweb.class));
		ObjectManager.addItem("poopcharge", new ItemCharge("poopcharge", EntityFrostweb.class));
		ObjectManager.addItem("boulderblastcharge", new ItemCharge("boulderblastcharge", EntityFrostweb.class));
		ObjectManager.addItem("arcanelaserstormcharge", new ItemCharge("arcanelaserstormcharge", EntityFrostweb.class));
		ObjectManager.addItem("quill", new ItemCharge("quill", EntityFrostweb.class));
		ObjectManager.addItem("spectralboltcharge", new ItemCharge("spectralboltcharge", EntityFrostweb.class));
		ObjectManager.addItem("bloodleechcharge", new ItemCharge("bloodleechcharge", EntityFrostweb.class));


		// Summoning Staves:
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning("summoningstaff", "summoningstaff"));
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable("stablesummoningstaff", "staffstable"));
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood("bloodsummoningstaff", "staffblood"));
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy("sturdysummoningstaff", "staffsturdy"));
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage("savagesummoningstaff", "staffsavage"));


		// Building Blocks:
		BlockMaker.addStoneBlocks(group, "lush", Blocks.TALL_GRASS);
		BlockMaker.addStoneBlocks(group, "desert", Blocks.SANDSTONE);
		BlockMaker.addStoneBlocks(group, "shadow", Blocks.OBSIDIAN);
		BlockMaker.addStoneBlocks(group, "demon", Items.NETHER_WART);
		ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
		ObjectManager.addBlock("propolis", new BlockPropolis());
		ObjectManager.addBlock("veswax", new BlockVeswax());
		

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
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
		AssetManager.addSound("poopcloud", group, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud());
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
		ObjectManager.addBlock("frostweb", new BlockFrostweb());
	}
}
