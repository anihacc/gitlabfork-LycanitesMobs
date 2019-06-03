package com.lycanitesmobs.demonmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.BlockMaker;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.block.BlockSoulcube;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.entity.projectile.*;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.consumable.ItemCustomFood;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.core.item.egg.ItemDemonEgg;
import com.lycanitesmobs.core.item.soulstone.ItemSoulstoneDemonic;
import com.lycanitesmobs.core.item.temp.*;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.block.effect.BlockDoomfire;
import com.lycanitesmobs.core.block.effect.BlockHellfire;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorDemonicLightning;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorDevilstar;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorDoomfireball;
import com.lycanitesmobs.core.dispenser.projectile.DispenserBehaviorHellfireball;
import com.lycanitesmobs.core.info.altar.AltarInfoAsmodeus;
import com.lycanitesmobs.core.info.altar.AltarInfoEbonCacodemon;
import com.lycanitesmobs.core.info.altar.AltarInfoRahovart;
import com.lycanitesmobs.core.worldgen.mobevents.AsmodeusStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.RahovartStructureBuilder;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class DemonMobs extends Submod {
	
	public static final String modid = "demonmobs";
	public static final String name = "Lycanites Demon Mobs";
	
	// Instance:
	@Instance(modid)
	public static DemonMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.demonmobs.ClientSubProxy", serverSide="com.lycanitesmobs.demonmobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);

		AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
		AltarInfo.addAltar(ebonCacodemonAltar);

		AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
		AltarInfo.addAltar(rahovartAltar);
		StructureBuilder.addStructureBuilder(new RahovartStructureBuilder());

		AltarInfo asmodeusAltar = new AltarInfoAsmodeus("AsmodeusAltar");
		AltarInfo.addAltar(asmodeusAltar);
		StructureBuilder.addStructureBuilder(new AsmodeusStructureBuilder());
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
		group = new GroupInfo(this, "Demon Mobs", 11)
				.setDimensionBlacklist("-1").setDimensionWhitelist(true).setBiomes("NETHER").setDungeonThemes("NETHER, NECRO")
				.setEggName("demonspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("demonspawn", new ItemDemonEgg());
		ObjectManager.addItem("soulstonedemonic", new ItemSoulstoneDemonic(group));

		ObjectManager.addItem("doomfirecharge", new ItemDoomfireCharge());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireCharge());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstarCharge());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightningCharge());

		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WITHER, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));

		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.STRENGTH, 60, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));

		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.STRENGTH, 600, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("pinkytreat", new ItemTreat("pinkytreat", group));
		ObjectManager.addItem("cacodemontreat", new ItemTreat("cacodemontreat", group));

		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire(), 2, 1, 1);
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire(), 2, 1, 1);
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar(), 2, 1, 1);
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning(), 2, 1, 1);
	}

	@Override
	public void createBlocks() {
		ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
		AssetManager.addSound("hellfire", group, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
		AssetManager.addSound("doomfire", group, "block.doomfire");
		ObjectManager.addBlock("doomfire", new BlockDoomfire());

		BlockMaker.addStoneBlocks(group, "demon", Items.NETHER_WART);
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("demonspawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class, false);
		ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
		ObjectManager.addProjectile("hellfirewall", EntityHellfireWall.class, false);
		ObjectManager.addProjectile("hellfireorb", EntityHellfireOrb.class, false);
		ObjectManager.addProjectile("hellfirewave", EntityHellfireWave.class, false);
		ObjectManager.addProjectile("hellfirewavepart", EntityHellfireWavePart.class, false);
		ObjectManager.addProjectile("hellfirebarrier", EntityHellfireBarrier.class, false);
		ObjectManager.addProjectile("hellfirebarrierpart", EntityHellfireBarrierPart.class, false);
		ObjectManager.addProjectile("devilgatling", EntityDevilGatling.class, false);
		ObjectManager.addProjectile("hellshield", EntityHellShield.class, false);
		ObjectManager.addProjectile("helllaser", EntityHellLaser.class, false);
		ObjectManager.addProjectile("helllaserend", EntityHellLaserEnd.class, false);
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("pinkymeatraw"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("pinkymeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		Biome[] biomes = {Biomes.HELL };
		EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.MONSTER, biomes);
	}
}
