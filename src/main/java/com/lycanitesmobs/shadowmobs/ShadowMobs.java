package com.lycanitesmobs.shadowmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.BlockMaker;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.consumable.ItemCustomFood;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.core.item.egg.ItemShadowEgg;
import com.lycanitesmobs.core.item.soulstone.ItemSoulstoneShadow;
import com.lycanitesmobs.core.item.special.ItemGeistLiver;
import com.lycanitesmobs.core.item.temp.ItemBloodleechCharge;
import com.lycanitesmobs.core.item.temp.ItemScepterBloodleech;
import com.lycanitesmobs.core.item.temp.ItemScepterSpectralbolt;
import com.lycanitesmobs.core.item.temp.ItemSpectralboltCharge;
import com.lycanitesmobs.core.block.effect.BlockShadowfire;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorBloodleech;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorSpectralbolt;
import com.lycanitesmobs.core.entity.projectile.EntityBloodleech;
import com.lycanitesmobs.core.entity.projectile.EntitySpectralbolt;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = ShadowMobs.modid, name = ShadowMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class ShadowMobs extends Submod {
	
	public static final String modid = "shadowmobs";
	public static final String name = "Lycanites Shadow Mobs";
	
	// Instance:
	@Instance(modid)
	public static ShadowMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.shadowmobs.ClientSubProxy", serverSide="com.lycanitesmobs.shadowmobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Shadow Mobs", 10)
				.setDimensionBlacklist("1,-100").setDimensionWhitelist(true).setBiomes("END").setDungeonThemes("SHADOW, NECRO")
				.setEggName("shadowspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("shadowspawn", new ItemShadowEgg());
		ObjectManager.addItem("soulstoneshadow", new ItemSoulstoneShadow(group));

		ObjectManager.addItem("spectralboltcharge", new ItemSpectralboltCharge());
		ObjectManager.addItem("spectralboltscepter", new ItemScepterSpectralbolt(), 2, 1, 1);

		ObjectManager.addItem("bloodleechcharge", new ItemBloodleechCharge());
		ObjectManager.addItem("bloodleechscepter", new ItemScepterBloodleech(), 2, 1, 1);

		ItemCustomFood rawMeat = new ItemCustomFood("chupacabrameatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.HUNGER, 45, 2, 0.8F);
		if(ObjectManager.getPotionEffect("fear") != null)
			rawMeat.setPotionEffect(ObjectManager.getPotionEffect("fear"), 10, 2, 0.8F);
		ObjectManager.addItem("chupacabrameatraw", rawMeat);
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("chupacabrameatraw"));

		ItemCustomFood cookedMeat = new ItemCustomFood("chupacabrameatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
		if(ObjectManager.getPotionEffect("leech") != null)
			cookedMeat.setPotionEffect(ObjectManager.getPotionEffect("leech"), 30, 1, 1.0F);
		ObjectManager.addItem("chupacabrameatcooked", cookedMeat);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("chupacabrameatcooked"));

		ItemCustomFood meal = new ItemCustomFood("bloodchili", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
		meal.setMaxStackSize(16);
		if(ObjectManager.getPotionEffect("leech") != null)
			meal.setPotionEffect(ObjectManager.getPotionEffect("leech"), 600, 1, 1.0F);
		ObjectManager.addItem("bloodchili", meal, 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bloodchili"));

		ObjectManager.addItem("geistliver", new ItemGeistLiver());

		ObjectManager.addItem("chupacabratreat", new ItemTreat("chupacabratreat", group));
		ObjectManager.addItem("shadetreat", new ItemTreat("shadetreat", group));
	}

	@Override
	public void createBlocks() {
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());

		BlockMaker.addStoneBlocks(group, "shadow", Blocks.OBSIDIAN);
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("shadowspawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("spectralbolt", EntitySpectralbolt.class, ObjectManager.getItem("spectralboltcharge"), new DispenserBehaviorSpectralbolt());
		ObjectManager.addProjectile("bloodleech", EntityBloodleech.class, ObjectManager.getItem("bloodleechcharge"), new DispenserBehaviorBloodleech());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("chupacabrameatraw"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("chupacabrameatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("chupacabrameatraw"), new ItemStack(ObjectManager.getItem("chupacabrameatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {

	}
}
