package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldGenManager {
	private static WorldGenManager INSTANCE;
	public static WorldGenManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new WorldGenManager();
		}
		return INSTANCE;
	}
	
	// Placements:
	public AlwaysPlacement alwaysPlacement = new AlwaysPlacement(NoPlacementConfig::deserialize);
	
	// Features:
	public ChunkSpawnFeature chunkSpawnFeature = new ChunkSpawnFeature(NoFeatureConfig::deserialize);
	public DungeonFeature dungeonFeature = new DungeonFeature(NoFeatureConfig::deserialize);
	public DungeonStructure dungeonStructure = new DungeonStructure(NoFeatureConfig::deserialize);

	public void addToBiomes() {
		GenerationStage.Decoration spawningStage = GenerationStage.Decoration.TOP_LAYER_MODIFICATION;
		GenerationStage.Decoration structureStage = GenerationStage.Decoration.UNDERGROUND_STRUCTURES;

		// TODO Enable lake generation when fluids are back.
		for(BiomeManager.BiomeEntry biomeEntry : BiomeManager.getBiomes(BiomeManager.BiomeType.COOL)) { //was createDecoratedFeature(), is now something like a chained set of createConfiguredFeature()
			biomeEntry.biome.addFeature(spawningStage, this.chunkSpawnFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			biomeEntry.biome.addFeature(structureStage, this.dungeonFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			//biomeEntry.biome.addStructure(this.dungeonStructure, IFeatureConfig.NO_FEATURE_CONFIG);
			//biomeEntry.biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(ObjectManager.getBlock("ooze").getDefaultState()), Placement.WATER_LAKE, new LakeChanceConfig(4)));
		}
		for(BiomeManager.BiomeEntry biomeEntry : BiomeManager.getBiomes(BiomeManager.BiomeType.ICY)) {
			biomeEntry.biome.addFeature(spawningStage, this.chunkSpawnFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			biomeEntry.biome.addFeature(structureStage, this.dungeonFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			//biomeEntry.biome.addStructure(this.dungeonStructure, IFeatureConfig.NO_FEATURE_CONFIG);
			//biomeEntry.biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(ObjectManager.getBlock("ooze").getDefaultState()), Placement.WATER_LAKE, new LakeChanceConfig(4)));
		}
		for(BiomeManager.BiomeEntry biomeEntry : BiomeManager.getBiomes(BiomeManager.BiomeType.WARM)) {
			biomeEntry.biome.addFeature(spawningStage, this.chunkSpawnFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			biomeEntry.biome.addFeature(structureStage, this.dungeonFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			//biomeEntry.biome.addStructure(this.dungeonStructure, IFeatureConfig.NO_FEATURE_CONFIG);
			//biomeEntry.biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(ObjectManager.getBlock("moglava").getDefaultState()), Placement.WATER_LAKE, new LakeChanceConfig(4)));
		}
		for(BiomeManager.BiomeEntry biomeEntry : BiomeManager.getBiomes(BiomeManager.BiomeType.DESERT)) {
			biomeEntry.biome.addFeature(spawningStage, this.chunkSpawnFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			biomeEntry.biome.addFeature(structureStage, this.dungeonFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(this.alwaysPlacement.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			//biomeEntry.biome.addStructure(this.dungeonStructure, IFeatureConfig.NO_FEATURE_CONFIG);
			//biomeEntry.biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(ObjectManager.getBlock("moglava").getDefaultState()), Placement.WATER_LAKE, new LakeChanceConfig(4)));
		}
	}

	@SubscribeEvent
	public void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
		event.getRegistry().register(this.alwaysPlacement.setRegistryName(LycanitesMobs.modInfo.modid, "always"));
	}

	@SubscribeEvent
	public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().register(this.chunkSpawnFeature.setRegistryName(LycanitesMobs.modInfo.modid, "chunkspawn"));
		event.getRegistry().register(this.dungeonFeature.setRegistryName(LycanitesMobs.modInfo.modid, "dungeon_temp"));
		event.getRegistry().register(this.dungeonStructure.setRegistryName(LycanitesMobs.modInfo.modid, "dungeon"));
	}
}
