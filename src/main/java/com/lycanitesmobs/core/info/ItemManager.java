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
import com.lycanitesmobs.core.block.fluid.BlockFluidOoze;
import com.lycanitesmobs.core.block.fluid.BlockFluidPureLava;
import com.lycanitesmobs.core.item.ItemMobToken;
import com.lycanitesmobs.core.item.consumable.*;
import com.lycanitesmobs.core.item.egg.*;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.soulstone.*;
import com.lycanitesmobs.core.item.special.*;
import com.lycanitesmobs.core.item.temp.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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


		// Eggs:
		ObjectManager.addItem("arcticspawn", new ItemArcticEgg());
		ObjectManager.addItem("demonspawn", new ItemDemonEgg());
		ObjectManager.addItem("desertspawn", new ItemDesertEgg());
		ObjectManager.addItem("elementalspawn", new ItemElementalEgg());
		ObjectManager.addItem("forestspawn", new ItemForestEgg());
		ObjectManager.addItem("freshwaterspawn", new ItemFreshwaterEgg());
		ObjectManager.addItem("infernospawn", new ItemInfernoEgg());
		ObjectManager.addItem("junglespawn", new ItemJungleEgg());
		ObjectManager.addItem("mountainspawn", new ItemMountainEgg());
		ObjectManager.addItem("plainsspawn", new ItemPlainsEgg());
		ObjectManager.addItem("saltwaterspawn", new ItemSaltwaterEgg());
		ObjectManager.addItem("shadowspawn", new ItemShadowEgg());
		ObjectManager.addItem("swampspawn", new ItemSwampEgg());


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

		ObjectManager.addItem("yetimeatraw", new ItemCustomFood("yetimeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 1, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yetimeatraw"));
		GameRegistry.addSmelting(ObjectManager.getItem("yetimeatraw"), new ItemStack(ObjectManager.getItem("yetimeatcooked"), 1), 0.5f);

		ObjectManager.addItem("yetimeatcooked", new ItemCustomFood("yetimeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.RESISTANCE, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yetimeatcooked"));

		ObjectManager.addItem("palesoup", new ItemCustomFood("palesoup", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.RESISTANCE, 600, 1, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("palesoup"));

		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WITHER, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);

		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.STRENGTH, 60, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));

		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.STRENGTH, 600, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("joustmeatraw", new ItemCustomFood("joustmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("joustmeatraw"));

		ObjectManager.addItem("joustmeatcooked", new ItemCustomFood("joustmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.SPEED, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("joustmeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("joustmeatraw"), new ItemStack(ObjectManager.getItem("joustmeatcooked"), 1), 0.5f);

		ObjectManager.addItem("ambercake", new ItemCustomFood("ambercake", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.SPEED, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("ambercake"));

		ItemCustomFood rawMeat =  new ItemCustomFood("arisaurmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SATURATION, 45, 2, 0.8F);
		if(ObjectManager.getPotionEffect("paralysis") != null) {
			rawMeat.setPotionEffect(ObjectManager.getPotionEffect("paralysis"), 10, 2, 0.8F);
		}
		ObjectManager.addItem("arisaurmeatraw", rawMeat);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatraw"));

		ItemCustomFood arisaurCooked = new ItemCustomFood("arisaurmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
		if(ObjectManager.getPotionEffect("rejuvenation") != null) {
			arisaurCooked.setPotionEffect(ObjectManager.getPotionEffect("rejuvenation"), 30, 1, 1.0F);
		}
		ObjectManager.addItem("arisaurmeatcooked", arisaurCooked);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("arisaurmeatraw"), new ItemStack(ObjectManager.getItem("arisaurmeatcooked"), 1), 0.5f);

		ItemCustomFood paleosalad = new ItemCustomFood("paleosalad", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
		if(ObjectManager.getPotionEffect("rejuvenation") != null) {
			paleosalad.setPotionEffect(ObjectManager.getPotionEffect("rejuvenation"), 600, 1, 1.0F);
		}
		ObjectManager.addItem("paleosalad", paleosalad.setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("paleosalad"));

		Potion rawFoodEffectID = MobEffects.WEAKNESS;
		if(ObjectManager.getPotionEffect("penetration") != null) {
			rawFoodEffectID = ObjectManager.getPotionEffect("penetration");
		}
		ObjectManager.addItem("silexmeatraw", new ItemCustomFood("silexmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
		ObjectLists.addItem("rawfish", ObjectManager.getItem("silexmeatraw"));

		Potion cookedFoodEffectID = MobEffects.SPEED;
		if(ObjectManager.getPotionEffect("swiftswimming") != null) {
			cookedFoodEffectID = ObjectManager.getPotionEffect("swiftswimming");
		}
		ObjectManager.addItem("silexmeatcooked", new ItemCustomFood("silexmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(cookedFoodEffectID, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("silexmeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("silexmeatraw"), new ItemStack(ObjectManager.getItem("silexmeatcooked"), 1), 0.5f);

		ObjectManager.addItem("lapisfishandchips", new ItemCustomFood("lapisfishandchips", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(cookedFoodEffectID, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("lapisfishandchips"));

		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.FIRE_RESISTANCE, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));

		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.FIRE_RESISTANCE, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));

		ObjectManager.addItem("concapedemeatraw", new ItemCustomFood("concapedemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("concapedemeatraw"));

		ObjectManager.addItem("concapedemeatcooked", new ItemCustomFood("concapedemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.JUMP_BOOST, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("concapedemeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("concapedemeatraw"), new ItemStack(ObjectManager.getItem("concapedemeatcooked"), 1), 0.5f);

		ObjectManager.addItem("tropicalcurry", new ItemCustomFood("tropicalcurry", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.JUMP_BOOST, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("tropicalcurry"));

		ObjectManager.addItem("yalemeatraw", new ItemCustomFood("yalemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.MINING_FATIGUE, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yalemeatraw"));

		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.HASTE, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("yalemeatraw"), new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1), 0.5f);

		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.HASTE, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("peakskebab"));

		ObjectManager.addItem("makameatraw", new ItemCustomFood("makameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WEAKNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("makameatraw"));

		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.ABSORPTION, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("makameatraw"), new ItemStack(ObjectManager.getItem("makameatcooked"), 1), 0.5f);

		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.ABSORPTION, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bulwarkburger"));

		Potion ikaRawFoodEffect = MobEffects.BLINDNESS;
		if(ObjectManager.getPotionEffect("weight") != null) {
			ikaRawFoodEffect = ObjectManager.getPotionEffect("weight");
		}
		ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(ikaRawFoodEffect, 45, 2, 0.8F));
		ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));

		ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.WATER_BREATHING, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("ikameatraw"), new ItemStack(ObjectManager.getItem("ikameatcooked"), 1), 0.5f);

		ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.WATER_BREATHING, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));

		ItemCustomFood rawChupacabraMeat = new ItemCustomFood("chupacabrameatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.HUNGER, 45, 2, 0.8F);
		if(ObjectManager.getPotionEffect("fear") != null) {
			rawChupacabraMeat.setPotionEffect(ObjectManager.getPotionEffect("fear"), 10, 2, 0.8F);
		}
		ObjectManager.addItem("chupacabrameatraw", rawChupacabraMeat);
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("chupacabrameatraw"));

		ItemCustomFood cookedMeat = new ItemCustomFood("chupacabrameatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
		if(ObjectManager.getPotionEffect("leech") != null) {
			cookedMeat.setPotionEffect(ObjectManager.getPotionEffect("leech"), 30, 1, 1.0F);
		}
		ObjectManager.addItem("chupacabrameatcooked", cookedMeat);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("chupacabrameatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("chupacabrameatraw"), new ItemStack(ObjectManager.getItem("chupacabrameatcooked"), 1), 0.5f);

		ItemCustomFood meal = new ItemCustomFood("bloodchili", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
		meal.setMaxStackSize(16);
		if(ObjectManager.getPotionEffect("leech") != null) {
			meal.setPotionEffect(ObjectManager.getPotionEffect("leech"), 600, 1, 1.0F);
		}
		ObjectManager.addItem("bloodchili", meal, 3, 1, 6);

		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.POISON, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));

		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.REGENERATION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));
		GameRegistry.addSmelting(ObjectManager.getItem("aspidmeatraw"), new ItemStack(ObjectManager.getItem("aspidmeatcooked"), 1), 0.5f);

		ObjectManager.addItem("mosspie", new ItemCustomFood("mosspie", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.REGENERATION, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));


		// Special:
		ObjectManager.addItem("frostyfur", new ItemFrostyFur());
		ObjectManager.addItem("poisongland", new ItemPoisonGland());
		ObjectManager.addItem("geistliver", new ItemGeistLiver());
		ObjectManager.addItem("wraithsigil", new ItemWraithSigil());


		// Fluids:
		Fluid fluidOoze = ObjectManager.addFluid("ooze");
		fluidOoze.setLuminosity(10).setDensity(3000).setViscosity(5000).setTemperature(0);
		ObjectManager.addBlock("ooze", new BlockFluidOoze(fluidOoze));
		ObjectManager.addItem("bucketooze", new ItemBucketOoze(fluidOoze).setContainerItem(Items.BUCKET));
		AssetManager.addSound("ooze", group, "block.ooze");
		ObjectManager.addDamageSource("ooze", new DamageSource("ooze"));

		Fluid fluidPureLava = ObjectManager.addFluid("purelava");
		fluidPureLava.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluidPureLava));
		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava(fluidPureLava).setContainerItem(Items.BUCKET));


		// Charges and Scepters:
		ObjectManager.addItem("frostwebcharge", new ItemFrostwebCharge());
		ObjectManager.addItem("frostwebscepter", new ItemScepterFrostweb(), 2, 1, 1);
		ObjectManager.addItem("tundracharge", new ItemTundraCharge());
		ObjectManager.addItem("tundrascepter", new ItemScepterTundra(), 2, 1, 1);
		ObjectManager.addItem("icefirecharge", new ItemIcefireCharge());
		ObjectManager.addItem("icefirescepter", new ItemScepterIcefire(), 2, 1, 1);
		ObjectManager.addItem("blizzardcharge", new ItemBlizzardCharge());
		ObjectManager.addItem("blizzardscepter", new ItemScepterBlizzard(), 2, 1, 1);
		ObjectManager.addItem("doomfirecharge", new ItemDoomfireCharge());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireCharge());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstarCharge());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightningCharge());
		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire(), 2, 1, 1);
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire(), 2, 1, 1);
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar(), 2, 1, 1);
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning(), 2, 1, 1);
		ObjectManager.addItem("throwingscythe", new ItemThrowingScythe());
		ObjectManager.addItem("mudshotcharge", new ItemMudshotCharge());
		ObjectManager.addItem("scythescepter", new ItemScepterScythe(), 2, 1, 1);
		ObjectManager.addItem("mudshotscepter", new ItemScepterMudshot(), 2, 1, 1);
		ObjectManager.addItem("embercharge", new ItemEmberCharge());
		ObjectManager.addItem("emberscepter", new ItemScepterEmber(), 2, 1, 1);
		ObjectManager.addItem("aquapulsecharge", new ItemAquaPulseCharge());
		ObjectManager.addItem("aquapulsescepter", new ItemScepterAquaPulse(), 2, 1, 1);
		ObjectManager.addItem("whirlwindcharge", new ItemWhirlwindCharge());
		ObjectManager.addItem("chaosorbcharge", new ItemChaosOrbCharge());
		ObjectManager.addItem("acidsplashcharge", new ItemAcidSplashCharge());
		ObjectManager.addItem("lightball", new ItemLightBall());
		ObjectManager.addItem("lifedraincharge", new ItemLifeDrainCharge());
		ObjectManager.addItem("lifedrainscepter", new ItemScepterLifeDrain(), 2, 1, 1);
		ObjectManager.addItem("crystalshard", new ItemCrystalShard());
		ObjectManager.addItem("frostboltcharge", new ItemFrostboltCharge());
		ObjectManager.addItem("frostboltscepter", new ItemScepterFrostbolt(), 2, 1, 1);
		ObjectManager.addItem("faeboltcharge", new ItemFaeboltCharge());
		ObjectManager.addItem("aetherwavecharge", new ItemAetherwaveCharge());
		ObjectManager.addItem("waterjetcharge", new ItemWaterJetCharge());
		ObjectManager.addItem("waterjetscepter", new ItemScepterWaterJet(), 2, 1, 1);
		ObjectManager.addItem("magmacharge", new ItemMagmaCharge());
		ObjectManager.addItem("magmascepter", new ItemScepterMagma(), 2, 1, 1);
		ObjectManager.addItem("scorchfirecharge", new ItemScorchfireCharge());
		ObjectManager.addItem("scorchfirescepter", new ItemScepterScorchfire(), 2, 1, 1);
		ObjectManager.addItem("poopcharge", new ItemPoopCharge());
		ObjectManager.addItem("poopscepter", new ItemScepterPoop(), 2, 1, 1);
		ObjectManager.addItem("boulderblastcharge", new ItemBoulderBlastCharge());
		ObjectManager.addItem("boulderblastscepter", new ItemScepterBoulderBlast(), 2, 1, 1);
		ObjectManager.addItem("arcanelaserstormcharge", new ItemArcaneLaserStormCharge());
		ObjectManager.addItem("arcanelaserstormscepter", new ItemScepterArcaneLaserStorm(), 2, 1, 1);
		ObjectManager.addItem("quill", new ItemQuill());
		ObjectManager.addItem("quillscepter", new ItemScepterQuill(), 2, 1, 1);
		ObjectManager.addItem("spectralboltcharge", new ItemSpectralboltCharge());
		ObjectManager.addItem("spectralboltscepter", new ItemScepterSpectralbolt(), 2, 1, 1);
		ObjectManager.addItem("bloodleechcharge", new ItemBloodleechCharge());
		ObjectManager.addItem("bloodleechscepter", new ItemScepterBloodleech(), 2, 1, 1);
		ObjectManager.addItem("poisonrayscepter", new ItemScepterPoisonRay(), 2, 1, 1);
		ObjectManager.addItem("venomshotscepter", new ItemScepterVenomShot(), 2, 1, 1);


		// Tools and Weapons:
		ObjectManager.addItem("cinderfallsword", new ItemSwordCinderfall("cinderfallsword", "swordcinderfall"), 2, 1, 1);
		ObjectManager.addItem("azurecinderfallsword", new ItemSwordCinderfallAzure("azurecinderfallsword", "swordcinderfallazure"));
		ObjectManager.addItem("verdantcinderfallsword", new ItemSwordCinderfallVerdant("verdantcinderfallsword", "swordcinderfallverdant"));
		ObjectManager.addItem("venomaxeblade", new ItemSwordVenomAxeblade("venomaxeblade", "swordvenomaxeblade"), 2, 1, 1);
		ObjectManager.addItem("goldenvenomaxeblade", new ItemSwordVenomAxebladeGolden("goldenvenomaxeblade", "swordvenomaxebladegolden"));
		ObjectManager.addItem("verdantvenomaxeblade", new ItemSwordVenomAxebladeVerdant("verdantvenomaxeblade", "swordvenomaxebladeverdant"));


		// Treats:
		ObjectManager.addItem("arixtreat", new ItemTreat("arixtreat", group));
		ObjectManager.addItem("serpixtreat", new ItemTreat("serpixtreat", group));
		ObjectManager.addItem("maugtreat", new ItemTreat("maugtreat", group));
		ObjectManager.addItem("pinkytreat", new ItemTreat("pinkytreat", group));
		ObjectManager.addItem("cacodemontreat", new ItemTreat("cacodemontreat", group));
		ObjectManager.addItem("crusktreat", new ItemTreat("crusktreat", group));
		ObjectManager.addItem("erepedetreat", new ItemTreat("erepedetreat", group));
		ObjectManager.addItem("shamblertreat", new ItemTreat("shamblertreat", group));
		ObjectManager.addItem("wargtreat", new ItemTreat("wargtreat", group));
		ObjectManager.addItem("stridertreat", new ItemTreat("stridertreat", group));
		ObjectManager.addItem("threshertreat", new ItemTreat("threshertreat", group));
		ObjectManager.addItem("ioraytreat", new ItemTreat("ioraytreat", group));
		ObjectManager.addItem("afrittreat", new ItemTreat("afrittreat", group));
		ObjectManager.addItem("salamandertreat", new ItemTreat("salamandertreat", group));
		ObjectManager.addItem("gorgertreat", new ItemTreat("gorgertreat", group));
		ObjectManager.addItem("ignibustreat", new ItemTreat("ignibustreat", group));
		ObjectManager.addItem("uvaraptortreat", new ItemTreat("uvaraptortreat", group));
		ObjectManager.addItem("dawontreat", new ItemTreat("dawontreat", group));
		ObjectManager.addItem("cockatricetreat", new ItemTreat("cockatricetreat", group));
		ObjectManager.addItem("barghesttreat", new ItemTreat("barghesttreat", group));
		ObjectManager.addItem("beholdertreat", new ItemTreat("beholdertreat", group));
		ObjectManager.addItem("wildkintreat", new ItemTreat("wildkintreat", group));
		ObjectManager.addItem("ventoraptortreat", new ItemTreat("ventoraptortreat", group));
		ObjectManager.addItem("roctreat", new ItemTreat("roctreat", group));
		ObjectManager.addItem("feradontreat", new ItemTreat("feradontreat", group));
		ObjectManager.addItem("quillbeasttreat", new ItemTreat("quillbeasttreat", group));
		ObjectManager.addItem("morocktreat", new ItemTreat("morocktreat", group));
		ObjectManager.addItem("raikotreat", new ItemTreat("raikotreat", group));
		ObjectManager.addItem("roatreat", new ItemTreat("roatreat", group));
		ObjectManager.addItem("hermatreat", new ItemTreat("hermatreat", group));
		ObjectManager.addItem("quetzodracltreat", new ItemTreat("quetzodracltreat", group));
		ObjectManager.addItem("chupacabratreat", new ItemTreat("chupacabratreat", group));
		ObjectManager.addItem("shadetreat", new ItemTreat("shadetreat", group));
		ObjectManager.addItem("lurkertreat", new ItemTreat("lurkertreat", group));
		ObjectManager.addItem("eyewigtreat", new ItemTreat("eyewigtreat", group));


		// Summoning Staves:
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning("summoningstaff", "summoningstaff"));
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable("stablesummoningstaff", "staffstable"));
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood("bloodsummoningstaff", "staffblood"));
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy("sturdysummoningstaff", "staffsturdy"));
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage("savagesummoningstaff", "staffsavage"));
		ObjectManager.addBlock("frostweb", new BlockFrostweb());


		// Building Blocks:
		BlockMaker.addStoneBlocks(group, "shadow", Blocks.OBSIDIAN);
		BlockMaker.addStoneBlocks(group, "demon", Items.NETHER_WART);
		ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
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
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
		AssetManager.addSound("poopcloud", group, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud());
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
	}


	/**
	 * Adds items to the ore dictionary.
	 */
	public void registerOres() {
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("yetimeatraw"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("yetimeatcooked"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("pinkymeatraw"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("pinkymeatcooked"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("joustmeatraw"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("joustmeatcooked"));
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("silexmeatraw"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("silexmeatcooked"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cephignismeatcooked"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("concapedemeatraw"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("concapedemeatcooked"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("yalemeatraw"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("yalemeatcooked"));
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("makameatraw"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("makameatcooked"));
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("ikameatraw"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("ikameatcooked"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("chupacabrameatraw"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("chupacabrameatcooked"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("aspidmeatraw"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("aspidmeatcooked"));
	}
}
