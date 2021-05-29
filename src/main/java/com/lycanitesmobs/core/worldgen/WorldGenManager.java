package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldGenManager {
	private static WorldGenManager INSTANCE;
	public static WorldGenManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new WorldGenManager();
		}
		return INSTANCE;
	}
	
	// Placements:
	public AlwaysPlacement alwaysPlacement = new AlwaysPlacement(NoPlacementConfig.CODEC);
	
	// Features:
	public ChunkSpawnFeature chunkSpawnFeature = new ChunkSpawnFeature(NoFeatureConfig.CODEC);
	public DungeonFeature dungeonFeature = new DungeonFeature(NoFeatureConfig.CODEC);

	@SubscribeEvent
	public void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
		event.getRegistry().register(this.alwaysPlacement.setRegistryName(LycanitesMobs.modInfo.modid, "always"));
	}

	@SubscribeEvent
	public void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().register(this.chunkSpawnFeature.setRegistryName(LycanitesMobs.modInfo.modid, "chunkspawn"));
		event.getRegistry().register(this.dungeonFeature.setRegistryName(LycanitesMobs.modInfo.modid, "dungeon"));
	}
}
